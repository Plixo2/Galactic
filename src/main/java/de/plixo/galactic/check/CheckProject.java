package de.plixo.galactic.check;

import de.plixo.galactic.Universe;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.tokens.WordToken;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.Package;
import de.plixo.galactic.typed.path.Unit;
import lombok.Getter;

@Getter
public class CheckProject {
    private static final WordToken wordToken = new WordToken();
    private static final GalacticTokens tokens;

    static {
        tokens = new GalacticTokens();
    }

    private final CheckUnit checkUnit = new CheckUnit();
    private final CheckClass checkClass = new CheckClass();
    private final CheckExpressions checkExpressions = new CheckExpressions();
    private final CheckPackage checkPackage = new CheckPackage();

    public void check(CompileRoot root, Universe language) {
        switch (root) {
            case Package aPackage -> {
                checkPackage.check(aPackage, root, language, this);
            }
            case Unit unit -> {
                checkUnit.check(unit, root, language, this);
            }
        }
    }


    public static boolean isAllowedTopLevelName(String name) {
        var matches = name.matches("[a-zA-Z]\\w*");

        return matches && !tokens.isKeyword(name) && !tokens.isJavaKeyword(name);
    }
}

