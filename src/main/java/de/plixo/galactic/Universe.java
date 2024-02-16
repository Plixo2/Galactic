package de.plixo.galactic;

import com.google.common.io.Resources;
import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.check.CheckProject;
import de.plixo.galactic.codegen.Codegen;
import de.plixo.galactic.codegen.GeneratedCode;
import de.plixo.galactic.codegen.JarOutput;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.SyntaxFlairHandler;
import de.plixo.galactic.exception.TokenFlairHandler;
import de.plixo.galactic.files.FileTree;
import de.plixo.galactic.files.FileTreeEntry;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.macros.ForEachMacro;
import de.plixo.galactic.macros.ForMacro;
import de.plixo.galactic.macros.Macro;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.StandardLibs;
import de.plixo.galactic.typed.TreeBuilding;
import de.plixo.galactic.typed.lowering.Infer;
import de.plixo.galactic.typed.lowering.Symbols;
import de.plixo.galactic.typed.parsing.TIRClassParsing;
import de.plixo.galactic.typed.parsing.TIRUnitParsing;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Mac;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
    private final String debugOutput = "resources/build/";
    private final ObjectPath defaultSuperClass = new ObjectPath("java", "lang", "Object");
    //java 8
    private final int codeGenVersion = 52;


    /**
     * Bytecode memoization
     */
    private final LoadedBytecode loadedBytecode = new LoadedBytecode();

    /**
     * The stages of the compiler
     */
    private final Symbols symbolsStage = new Symbols();
    private final Infer inferStage = new Infer();

    /**
     * Entry point for the compiler. Reads the Grammar and generates a Tokenizer.
     *
     * @param file the project Path (file or directory)
     * @return result of the compilation
     */
    public CompileResult parse(File file, StandardLibs standardLibs) throws FlairException {
        var grammar = generateGrammar(configFile, new Grammar.RuleSet());
        var rule = Objects.requireNonNull(grammar.get(entryRule), "missing entry rule");
        var tokens = new GalacticTokens().tokens();
        var tokenizer = new Tokenizer(tokens);

        var macros = List.of(new ForMacro(grammar), new ForEachMacro(grammar));

        var rootEntry = FileTree.generateFileTree(file, filePattern);
        if (rootEntry == null) {
            throw new NullPointerException("Cant open project file");
        }
        rootEntry = addLibs(rootEntry, standardLibs);

        var tokenFlairHandler = new TokenFlairHandler();
        rootEntry.readAndLex(tokenizer, tokenFlairHandler);
        tokenFlairHandler.handle();


        try {
            rootEntry.applyMacros(macros, tokenizer);
            rootEntry.parse(rule);

            var syntaxFlairHandler = new SyntaxFlairHandler();
            var root = TreeBuilding.convertRoot(rootEntry, syntaxFlairHandler);
            syntaxFlairHandler.handle();

            read(root, standardLibs);
            var checkProject = new CheckProject();
            checkProject.check(root, this);
            return new Success(root);
        } catch (FlairCheckException e) {
            return new Error(e);
        }
    }

    /**
     * Writes the compiled code into the output stream
     *
     * @param stream    the output stream
     * @param root      the root of the compile tree
     * @param mainClass the main class, e.g. "de/plixo/Main". If it's null, and the root is a unit,
     *                  the main class will be the unit path
     */
    public void write(FileOutputStream stream, CompileRoot root, @Nullable String mainClass,
                      boolean debug) throws IOException {

        var mainClassPath = mainClass;
        if (root instanceof Unit unit && mainClassPath == null) {
            mainClassPath = unit.getJVMDestination();
        }

        var units = root.getUnits();
        var compiler = new Codegen(codeGenVersion);
        for (var unit : units) {
            var context = new Context(this, unit, null, root, loadedBytecode);
            compiler.addUnit(unit, context);
            for (var aClass : unit.classes()) {
                context = new Context(this, unit, aClass, root, loadedBytecode);
                compiler.addClass(aClass, context);
            }
        }
        var output = compiler.getOutput();
        var manifest = new GeneratedCode.Manifest(mainClassPath, manifestVersion);
        output.write(stream, manifest, loadedBytecode);
        if (debug) {
            output.dump(new File(debugOutput));
        }
    }

    public List<JarOutput> compileUnit(Unit unit, CompileRoot root) {
        var compiler = new Codegen(codeGenVersion);
        var context = new Context(this, unit, null, null, loadedBytecode);
        compiler.addUnit(unit, context);
        for (var aClass : unit.classes()) {
            context = new Context(this, unit, aClass, root, loadedBytecode);
            compiler.addClass(aClass, context);
        }
        return compiler.getOutput().output();
    }

    /**
     * Main compiler step. read, lex, parse and check
     *
     * @param root the root of the compile tree
     */
    private void read(CompileRoot root, StandardLibs standardLibs) {
        var units = root.getUnits();
        var defaultSuperClass = JVMLoader.asJVMClass(this.defaultSuperClass, loadedBytecode);
        if (defaultSuperClass == null) {
            throw new FlairException(STR."Cant find default super class\{this.defaultSuperClass}");
        }
        for (var unit : units) {
            TIRUnitParsing.parse(unit, defaultSuperClass);
        }
        for (var unit : units) {
            TIRUnitParsing.parseImports(standardLibs, unit, root, loadedBytecode, false);
        }

        var classes = new ArrayList<StellaClass>();
        for (var unit : units) {
            classes.addAll(unit.classes());
        }

        for (var aClass : classes) {
            var context = new Context(this, aClass.unit(), aClass, root, loadedBytecode);
            TIRClassParsing.fillSuperclasses(aClass, context, defaultSuperClass);
        }

        //types are known here
        units.forEach(unit -> {
            var context = new Context(this, unit, null, root, loadedBytecode);
            TIRUnitParsing.fillMethodShells(unit, context);
        });
        //rerun import for static method imports
        for (var unit : units) {
            TIRUnitParsing.parseImports(standardLibs, unit, root, loadedBytecode, true);
        }
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), aClass, root, loadedBytecode);
            TIRClassParsing.fillFields(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), aClass, root, loadedBytecode);
            TIRClassParsing.fillMethodShells(aClass, context);
        });
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), aClass, root, loadedBytecode);
            aClass.addAllFieldsConstructor(context);
        });

        //expressions can be evaluated here
        classes.forEach(aClass -> {
            var context = new Context(this, aClass.unit(), aClass, root, loadedBytecode);
            TIRClassParsing.fillMethodExpressions(aClass, context);
        });

        units.forEach(unit -> {
            var context = new Context(this, unit, null, root, loadedBytecode);
            TIRUnitParsing.fillMethodExpressions(unit, context, this);
        });

    }

    /**
     * Generates the grammar from the grammar file in the classpath
     *
     * @return the grammar rule set
     */
    public static Grammar.RuleSet generateGrammar(String configFile, Grammar.RuleSet base) {
        var resource = Objects.requireNonNull(Main.class.getResource(configFile));
        String grammarStr;
        try {
            grammarStr = Resources.toString(resource, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FlairException("Cant load grammar resource", e);
        }
        var grammar = new Grammar();
        return grammar.generate(grammarStr.lines().iterator(), base);
    }

    private FileTreeEntry addLibs(FileTreeEntry rootEntry, StandardLibs standardClasses) {
        var root = switch (rootEntry) {
            case FileTreeEntry.FileTreePackage fileTreePackage -> fileTreePackage;
            case FileTreeEntry.FileTreeUnit fileTreeUnit -> {
                var name = fileTreeUnit.localName();
                var children = new ArrayList<FileTreeEntry>();
                children.add(fileTreeUnit);
                yield new FileTreeEntry.FileTreePackage(name, name, children);
            }
        };
        var fileTreeEntries = new ArrayList<FileTreeEntry>();
        var packageName = standardClasses.packageName();
        for (var standardClass : standardClasses.imports()) {
            var stdSource = new File(standardClass.file());
            if (!stdSource.exists()) {
                throw new FlairException(STR."Cant find lib \{standardClass.file()}");
            }
            var localName = standardClass.name();
            var entry = new FileTreeEntry.FileTreeUnit(localName, STR."\{packageName}.\{localName}",
                    stdSource);
            fileTreeEntries.add(entry);
        }
        root.children()
                .add(new FileTreeEntry.FileTreePackage(packageName, packageName, fileTreeEntries));
        return root;
    }

    /**
     * The result of the compilation, can be either an Error or a Success
     */
    public sealed interface CompileResult {

    }

    public record Success(CompileRoot root) implements CompileResult {

    }

    public record Error(FlairCheckException exception) implements CompileResult {

    }

}
