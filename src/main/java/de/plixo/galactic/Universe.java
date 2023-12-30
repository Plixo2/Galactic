package de.plixo.galactic;

import com.google.common.io.Resources;
import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.codegen.Codegen;
import de.plixo.galactic.codegen.GeneratedCode;
import de.plixo.galactic.common.ObjectPath;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.SyntaxFlairHandler;
import de.plixo.galactic.exception.TokenFlairHandler;
import de.plixo.galactic.files.FileTree;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.TreeBuilding;
import de.plixo.galactic.typed.parsing.TIRClassParsing;
import de.plixo.galactic.typed.parsing.TIRUnitParsing;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.lowering.Check;
import de.plixo.galactic.typed.lowering.Infer;
import de.plixo.galactic.typed.lowering.Symbols;
import de.plixo.galactic.typed.stellaclass.StellaBlock;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The Language class is the entry point for the compiler.
 * It contains the stages of the compiler and some settings.
 */
@Getter
public class Universe {
    private final String filePattern = "stella";
    private final String configFile = "/cfg.txt";
    private final String entryRule = "unit";
    private final String manifestVersion = "1.0";
    private final ObjectPath defaultSuperClass = new ObjectPath("java", "lang", "Object");
    //java 8
    private final int codeGenVersion = 52;

    private final LoadedBytecode loadedBytecode = new LoadedBytecode();

    private final Symbols symbolsStage = new Symbols();
    private final Infer inferStage = new Infer();
    private final Check checkStage = new Check();


    public CompileResult parse(File file) throws FlairException {
        var startTime = System.currentTimeMillis();

        var grammar = generateGrammar();
        var rule = Objects.requireNonNull(grammar.get(entryRule), "missing entry rule");
        var tokens = new GalacticTokens().tokens();
        var tokenizer = new Tokenizer(tokens);

        var rootEntry = FileTree.generateFileTree(file, filePattern);
        if (rootEntry == null) {
            throw new NullPointerException("Cant open project file");
        }

        var tokenFlairHandler = new TokenFlairHandler();
        rootEntry.readAndLex(tokenizer, tokenFlairHandler);
        rootEntry.parse(rule);
        tokenFlairHandler.handle();

        var syntaxFlairHandler = new SyntaxFlairHandler();
        var root = TreeBuilding.convertRoot(rootEntry, syntaxFlairHandler);
        syntaxFlairHandler.handle();
        try {
            read(root);
        } catch (FlairCheckException e) {
            return new Error(e);
        } finally {
            var endTime = System.currentTimeMillis();
            System.out.println(STR."Reading Took \{endTime - startTime} ms");
        }


        return new Success(root);
    }

    public void write(FileOutputStream stream, CompileRoot root, @Nullable String mainClass)
            throws IOException {
        var startTime = System.currentTimeMillis();
        var units = root.getUnits();
        var compiler = new Codegen(codeGenVersion);
        for (var unit : units) {
            var context = new Context(this,unit, root, loadedBytecode);
            compiler.addUnit(unit, context);
            for (var aClass : unit.classes()) {
                compiler.addClass(aClass, context);
            }
        }
        var output = compiler.getOutput();
        var manifest = new GeneratedCode.Manifest(mainClass, manifestVersion);
        output.write(stream, manifest, loadedBytecode);
        output.dump(new File("resources/out"));

        var endTime = System.currentTimeMillis();
        System.out.println(STR."Writing Took \{endTime - startTime} ms");
    }

    private void read(CompileRoot root) {
        var units = root.getUnits();
        var defaultSuperClass = JVMLoader.asJVMClass(this.defaultSuperClass, loadedBytecode);
        if (defaultSuperClass == null) {
            throw new FlairException(STR."Cant find default super class\{this.defaultSuperClass}");
        }
        for (var unit : units) {
            TIRUnitParsing.parse(unit, defaultSuperClass);
        }
        for (var unit : units) {
            TIRUnitParsing.parseImports(unit, root, loadedBytecode, false);
        }

        var classes = new ArrayList<StellaClass>();
        for (var unit : units) {
            classes.addAll(unit.classes());
        }
        var blocks = new ArrayList<StellaBlock>();
        for (var unit : units) {
            blocks.addAll(unit.blocks());
        }

        for (var aClass : classes) {
            var context = new Context(this,aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillSuperclasses(aClass, context, defaultSuperClass);
        }

        //types are known here
        units.forEach(unit -> {
            var context = new Context(this,unit, root, loadedBytecode);
            TIRUnitParsing.fillMethodShells(unit, context);
        });
        //rerun import for static method imports
        for (var unit : units) {
            TIRUnitParsing.parseImports(unit, root, loadedBytecode, true);
        }
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillFields(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodShells(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), root, loadedBytecode);
            aClass.addAllFieldsConstructor(context);
        });

        //expressions can be evaluated here
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodExpressions(aClass, context);
        });
        blocks.forEach(block -> {
            var context = new Context(this, block.unit(), root, loadedBytecode);
            TIRUnitParsing.fillBlockExpressions(block.unit(), block, context);
        });

        units.forEach(unit -> {
            var context = new Context(this, unit, root, loadedBytecode);
            TIRUnitParsing.fillMethodExpressions(unit, context, this);
        });

        for (var aClass : classes) {
            TIRClassParsing.assertMethodsImplemented(aClass);
        }
    }

    /**
     * Generates the grammar from the grammar file in the classpath
     *
     * @return the grammar rule set
     */
    private Grammar.RuleSet generateGrammar() {
        var resource = Objects.requireNonNull(Main.class.getResource(configFile));
        String grammarStr;
        try {
            grammarStr = Resources.toString(resource, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FlairException("Cant load grammar resource", e);
        }
        var grammar = new Grammar();
        return grammar.generate(grammarStr.lines().iterator());
    }

    public sealed interface CompileResult {

    }

    public record Success(CompileRoot root) implements CompileResult {

    }

    public record Error(FlairCheckException exception) implements CompileResult {

    }
}
