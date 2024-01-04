package de.plixo.galactic.check;

import de.plixo.galactic.Universe;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.lexer.GalacticTokens;
import de.plixo.galactic.lexer.tokens.WordToken;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.Package;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;

import static de.plixo.galactic.exception.FlairKind.TYPE_MISMATCH;

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

    public static void checkMethodBody(Context context, CheckProject checkProject,
                                       StellaMethod method) {
        for (Parameter parameter : method.parameters()) {
            if (parameter.type().isVoid()) {
                throw new FlairCheckException(method.region(), FlairKind.TYPE_MISMATCH,
                        STR."Parameter \{parameter.name()} cannot be void");
            }
        }
        assert method.body() != null;
        checkProject.checkExpressions().parse(method.body(), context, 0);

        var expected = method.returnType();
        assert method.body != null;

        var found = method.body.getType(context);
        var isVoid = Type.isSame(expected, new VoidType());
        var typeMatch = Type.isAssignableFrom(expected, found, context);
        if (!typeMatch && !isVoid) {
            throw new FlairCheckException(method.region(), TYPE_MISMATCH,
                    STR."method return type doesnt match, expected \{expected}, but found \{found}");
        }
    }
}

