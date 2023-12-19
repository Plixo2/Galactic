package de.plixo.galactic;

import com.google.common.io.Resources;
import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.compiler.Compiler;
import de.plixo.galactic.files.FileTree;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.TreeBuilding;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.stellaclass.StellaBlock;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.parsing.TIRClassParsing;
import de.plixo.galactic.tir.parsing.TIRUnitParsing;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.stages.Check;
import de.plixo.galactic.tir.stages.CheckFlow;
import de.plixo.galactic.tir.stages.Infer;
import de.plixo.galactic.tir.stages.Symbols;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The Language class is the entry point for the compiler.
 * It contains the stages of the compiler and some settings.
 */
@Getter
public class Language {
    private final String filePattern = "stella";
    private final String configFile = "/cfg.txt";
    private final String entryRule = "unit";
    private final @Nullable String mainClass;

    private final LoadedBytecode loadedBytecode = new LoadedBytecode();

    private final Symbols symbolsStage = new Symbols();
    private final Infer inferStage = new Infer();
    private final Check checkStage = new Check();
    private final CheckFlow checkFlowStage = new CheckFlow();

    public Language(@Nullable String mainClass) {
        this.mainClass = mainClass;
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
            throw new RuntimeException("cant load grammar resource", e);
        }
        var grammar = new Grammar();
        return grammar.generate(grammarStr.lines().iterator());
    }

    public void parse(File file) {
        var startTime = System.currentTimeMillis();

        var grammar = generateGrammar();
        var rule = Objects.requireNonNull(grammar.get(entryRule), "missing entry rule");
        var tokens = new GalacticTokens().tokens();
        var tokenizer = new Tokenizer(tokens);

        var rootEntry = FileTree.generateFileTree(file, filePattern);
        if (rootEntry == null) {
            throw new NullPointerException("cant find a valid root entry");
        }
        rootEntry.readAndLex(tokenizer);
        rootEntry.parse(rule);

        var root = TreeBuilding.convertRoot(rootEntry);
        read(root);
        write(root);


        var endTime = System.currentTimeMillis();
        System.out.println("Took " + (endTime - startTime) + " ms");
    }

    private void write(CompileRoot root) {
        var units = root.getUnits();
        var compiler = new Compiler();
        try {
            for (var unit : units) {
                var context = new Context(unit, root, loadedBytecode);
                compiler.compile(unit, context);
            }
            compiler.makeJarFile(mainClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void read(CompileRoot root) {
        var units = root.getUnits();
        var objectPath = new ObjectPath("java", "lang", "Object");
        var defaultSuperClass = JVMLoader.asJVMClass(objectPath, loadedBytecode);
        if (defaultSuperClass == null) {
            throw new NullPointerException("cant find java.lang.Object");
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
            TIRClassParsing.fillSuperclasses(aClass, context);
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
        classes.parallelStream().forEach(aClass -> {
            var context = new TypeContext(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodExpressions(aClass, context, this);
        });
        blocks.parallelStream().forEach(block -> {
            TIRUnitParsing.fillBlockExpressions(block.unit(), root, block, this);
        });

        units.parallelStream().forEach(unit -> {
            var context = new TypeContext(unit, root, loadedBytecode);
            TIRUnitParsing.fillMethodExpressions(unit, context, this);
        });

        for (var aClass : classes) {
            TIRClassParsing.assertMethodsImplemented(aClass);
        }
    }

}
