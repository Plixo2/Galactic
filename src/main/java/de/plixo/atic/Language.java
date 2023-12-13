package de.plixo.atic;

import com.google.common.io.Resources;
import de.plixo.atic.boundary.JVMLoader;
import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.files.FileTree;
import de.plixo.atic.lexer.Tokenizer;
import de.plixo.atic.parsing.Grammar;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.TreeBuilding;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.parsing.TIRClassParsing;
import de.plixo.atic.tir.parsing.TIRUnitParsing;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.stages.Check;
import de.plixo.atic.tir.stages.CheckFlow;
import de.plixo.atic.tir.stages.Infer;
import de.plixo.atic.tir.stages.Symbols;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The Language class is the entry point for the compiler.
 * It contains the stages of the compiler.
 */
@RequiredArgsConstructor
@Getter
public class Language {
    private final String filePattern = "atic";
    private final String configFile = "/cfg.txt";
    private final String entryRule = "unit";

    private final LoadedBytecode loadedBytecode = new LoadedBytecode();

    private final Symbols symbolsStage = new Symbols();
    private final Infer inferStage = new Infer();
    private final Check checkStage = new Check();
    private final CheckFlow checkFlowStage = new CheckFlow();

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
        var rule = Objects.requireNonNull(grammar.get(entryRule), "missing unit2 rule");
        var tokens = new AticTokens().tokens();
        var tokenizer = new Tokenizer(tokens);

        var rootEntry = FileTree.generateFileTree(file, filePattern);
        if (rootEntry == null) {
            throw new NullPointerException("cant find a valid root entry");
        }
        rootEntry.readAndLex(tokenizer);
        rootEntry.parse(rule);

        var root = TreeBuilding.convertRoot(rootEntry);
        compile(root);


        var endTime = System.currentTimeMillis();
        System.out.println("Took " + (endTime - startTime) + " ms");
    }

    private void compile(CompileRoot root) {
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

        var classes = new ArrayList<AticClass>();
        for (var unit : units) {
            classes.addAll(unit.classes());
        }
        var blocks = new ArrayList<AticBlock>();
        for (var unit : units) {
            blocks.addAll(unit.blocks());
        }

        for (var aClass : classes) {
            var context = new Context(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillSuperclasses(aClass, context);
        }

        //types are known here
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
        classes.forEach(aClass -> {
            var context = new TypeContext(aClass.unit(), root, loadedBytecode);
            TIRClassParsing.fillMethodExpressions(aClass, context, this);
        });
        blocks.forEach(block -> {
            TIRUnitParsing.fillBlockExpressions(block.unit(), root, block, this);
        });

        for (var aClass : classes) {
            TIRClassParsing.assertMethodsImplemented(aClass);
        }
    }

}
