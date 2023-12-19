package de.plixo.galactic.tir;

import de.plixo.galactic.files.FileTreeEntry;
import de.plixo.galactic.hir.HIRUnitParsing;
import de.plixo.galactic.parsing.Parser;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.path.Package;
import de.plixo.galactic.tir.path.Unit;
import org.jetbrains.annotations.Nullable;

/**
 * Converts the FileTree into a Compiler Tree (Unit and Package)
 */
public class TreeBuilding {


    /**
     * crates a Root for the Compiler (Unit or Package)
     *
     * @param root file tree root
     * @return root of the compiler
     */
    public static CompileRoot convertRoot(FileTreeEntry root) {
        return switch (root) {
            case FileTreeEntry.FileTreeUnit unit -> createUnit(null, unit);
            case FileTreeEntry.FileTreePackage treePackage -> createPackage(null, treePackage);
        };
    }

    private static Package createPackage(@Nullable Package parent,
                                         FileTreeEntry.FileTreePackage treePackage) {
        var thePackage = new Package(treePackage.localName(), parent);

        treePackage.children().forEach(ref -> {
            switch (ref) {
                case FileTreeEntry.FileTreeUnit unit -> {
                    thePackage.addUnit(createUnit(thePackage, unit));
                }
                case FileTreeEntry.FileTreePackage subPackage -> {
                    thePackage.addPackage(createPackage(thePackage, subPackage));
                }
            }
        });
        return thePackage;
    }

    private static Unit createUnit(@Nullable Package parent, FileTreeEntry.FileTreeUnit unit) {
        var createdUnit = new Unit(parent, unit.localName(), unit);
        switch (unit.syntaxResult()) {
            case Parser.FailedRule failedRule -> {
                failedRule.records().forEach(System.out::println);
                throw new NullPointerException("Failed rule " + failedRule.failedRule() + " in " +
                        failedRule.parentRule());
                //TODO error reporting
            }
            case Parser.FailedLiteral failedLiteral -> {
                failedLiteral.records().forEach(System.out::println);
                throw failedLiteral.consumedLiteral()
                        .createException("expected " + failedLiteral.expectedLiteral());
                //TODO error reporting
            }
            case Parser.SyntaxMatch syntaxMatch -> {
                HIRUnitParsing.parse(createdUnit, syntaxMatch.node());
            }
            case null -> {
                throw new NullPointerException("syntaxResult should be known");
            }
        }
        return createdUnit;
    }
}
