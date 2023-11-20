package de.plixo.atic.macro;

import de.plixo.atic.Token;
import de.plixo.atic.lexer.AutoLexer;
import de.plixo.atic.lexer.GrammarReader;
import de.plixo.atic.lexer.Record;
import de.plixo.atic.lexer.TokenStream;
import org.jetbrains.annotations.Nullable;

public class Processor {
    public static @Nullable AutoLexer.SyntaxResult process(TokenStream<Record> stream) {
        var current = stream.current();
        var data = current.data();
        if (current.token() != Token.KEYWORD) {
            return null;
        }


        throw new NullPointerException("macro");
    }
}
