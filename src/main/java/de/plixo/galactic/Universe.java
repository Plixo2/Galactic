package de.plixo.galactic;

import com.google.common.io.Resources;
import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.codegen.Codegen;
import de.plixo.galactic.codegen.GeneratedCode;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.SyntaxFlairHandler;
import de.plixo.galactic.exception.TokenFlairHandler;
import de.plixo.galactic.files.FileTree;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.TreeBuilding;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.parsing.TIRClassParsing;
import de.plixo.galactic.tir.parsing.TIRUnitParsing;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.stages.Check;
import de.plixo.galactic.tir.stages.CheckFlow;
import de.plixo.galactic.tir.stages.Infer;
import de.plixo.galactic.tir.stages.Symbols;
import de.plixo.galactic.tir.stellaclass.StellaBlock;
import de.plixo.galactic.tir.stellaclass.StellaClass;
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

    private final LoadedBytecode loadedBytecode = new LoadedBytecode();

    private final Symbols symbolsStage = new Symbols();
    private final Infer inferStage = new Infer();
    private final Check checkStage = new Check();
    private final CheckFlow checkFlowStage = new CheckFlow();


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
            System.out.println("Reading Took " + (endTime - startTime) + " ms");
        }


        return new Success(root);
    }

    public void write(CompileRoot root, @Nullable String mainClass) throws FlairException {
        var startTime = System.currentTimeMillis();
        var units = root.getUnits();
        var compiler = new Codegen();
        for (var unit : units) {
            var context = new Context(unit, root, loadedBytecode);
            compiler.addUnit(unit, context);
        }
        var output = compiler.getOutput();
        var manifest = new GeneratedCode.Manifest(mainClass, manifestVersion);
        try {
            var out = new FileOutputStream("resources/out.jar");
            output.write(out, manifest);
        } catch (IOException e) {
            throw new FlairException("Trouble writing to file", e);
        }

        var endTime = System.currentTimeMillis();
        System.out.println("Writing Took " + (endTime - startTime) + " ms");
    }

    private void read(CompileRoot root) {
        var units = root.getUnits();
        var defaultSuperClass = JVMLoader.asJVMClass(this.defaultSuperClass, loadedBytecode);
        if (defaultSuperClass == null) {
            throw new FlairException("Cant find super class" + this.defaultSuperClass);
        }
        for (var unit : units) {
            TIRUnitParsing.parse(unit, defaultSuperClass);
        }
        for (var unit : units) {
            TIRUnitParsing.parseImports(unit, root, loadedBytecode);
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
            var context = new Context(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillSuperclasses(aClass, context, defaultSuperClass);
        }

        //types are known here
        units.forEach(unit -> {
            var context = new Context(unit, root, loadedBytecode);
            TIRUnitParsing.fillMethodShells(unit, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillFields(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodShells(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(aClass.unit(), root, loadedBytecode);
            aClass.addAllFieldsConstructor(context);
        });

        //expressions can be evaluated here
        classes.forEach(aClass -> {
            var context = new TypeContext(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodExpressions(aClass, context, this);
        });
        blocks.forEach(block -> {
            TIRUnitParsing.fillBlockExpressions(block.unit(), root, block, this);
        });

        units.forEach(unit -> {
            var context = new TypeContext(unit, root, loadedBytecode);
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
