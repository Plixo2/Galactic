package de.plixo.galactic.macros;

import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.parsing.Parser;
import de.plixo.galactic.parsing.TokenStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;

@Getter
@RequiredArgsConstructor
public abstract class Macro {
    private final String name;


    @Contract(mutates = "param0")
    public abstract MacroResult execute(TokenStream<TokenRecord> stream);

    public record MacroResult(Parser.SyntaxResult result) {

    }
}
