package de.plixo.galactic.tir;

import de.plixo.galactic.exception.SyntaxFlairHandler;
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
    public static CompileRoot convertRoot(FileTreeEntry root, SyntaxFlairHandler errorHandler) {
        return switch (root) {
            case FileTreeEntry.FileTreeUnit unit -> createUnit(null, unit, errorHandler);
            case FileTreeEntry.FileTreePackage treePackage ->
                    createPackage(null, treePackage, errorHandler);
        };
    }

    private static Package createPackage(@Nullable Package parent,
                                         FileTreeEntry.FileTreePackage treePackage,
                                         SyntaxFlairHandler errorHandler) {
        var thePackage = new Package(treePackage.localName(), parent);

        treePackage.children().forEach(ref -> {
            switch (ref) {
                case FileTreeEntry.FileTreeUnit unit -> {
                    thePackage.addUnit(createUnit(thePackage, unit, errorHandler));
                }
                case FileTreeEntry.FileTreePackage subPackage -> {
                    thePackage.addPackage(createPackage(thePackage, subPackage, errorHandler));
                }
            }
        });
        return thePackage;
    }

    private static Unit createUnit(@Nullable Package parent, FileTreeEntry.FileTreeUnit unit,
                                   SyntaxFlairHandler errorHandler) {
        var createdUnit = new Unit(parent, unit.localName(), unit);
        switch (unit.syntaxResult()) {
            case Parser.FailedRule failedRule -> {
                errorHandler.add(new SyntaxFlairHandler.FailedRule(failedRule.records(),
                        failedRule.failedRule(), failedRule.parentRule()));
            }
            case Parser.FailedLiteral failedLiteral -> {
                errorHandler.add(new SyntaxFlairHandler.FailedLiteral(failedLiteral.records(),
                        failedLiteral.parentRule(), failedLiteral.expectedLiteral(),
                        failedLiteral.consumedLiteral()));
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
