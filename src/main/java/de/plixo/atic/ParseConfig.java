package de.plixo.atic;

import de.plixo.atic.lexer.Lexer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParseConfig {
    private String filePattern;
    private Lexer lexer;
}
