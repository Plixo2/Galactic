package de.plixo.atic;

import de.plixo.atic.lexer.Lexer;

public record ParseConfig(String filePattern, Lexer lexer, boolean threaded) {

}
