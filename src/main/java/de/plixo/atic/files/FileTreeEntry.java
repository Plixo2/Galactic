package de.plixo.atic.files;

import de.plixo.atic.lexer.Position;
import de.plixo.atic.lexer.TokenRecord;
import de.plixo.atic.lexer.Tokenizer;
import de.plixo.atic.lexer.tokens.EOFToken;
import de.plixo.atic.lexer.tokens.UnknownToken;
import de.plixo.atic.lexer.tokens.WhiteSpaceToken;
import de.plixo.atic.parsing.Grammar;
import de.plixo.atic.parsing.Parser;
import de.plixo.atic.parsing.TokenStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public sealed abstract class FileTreeEntry {
    private final String localName;
    private final String name;

    /**
     * Reads the File, turns it into tokens, then stores the tokens in the unit
     * @param tokenizer the tokenizer to use
     */
    public void readAndLex(Tokenizer tokenizer) {
        switch (this) {
            case FileTreeUnit unit -> {
                String src;
                try {
                    src = FileUtils.readFileToString(unit.file, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                var recordList = tokenizer.fromFile(unit.file, src);
                var filteredTokens = recordList.stream().filter(ref -> switch (ref.token()) {
                    case UnknownToken ignored -> {
                        throw ref.createException();
                        //TODO Error reporting
                    }
                    case WhiteSpaceToken ignored -> false;
                    default -> true;
                }).toList();
                var tokens = new ArrayList<>(filteredTokens);
                int line = 0;
                if (!tokens.isEmpty()) {
                    line = tokens.get(tokens.size() - 1).position().line();
                }
                tokens.add(new TokenRecord(new EOFToken(), "", new Position(unit.file, line)));
                unit.tokens = tokens;
            }
            case FileTreePackage treePackage -> {
                treePackage.children().parallelStream().forEach(ref -> {
                    ref.readAndLex(tokenizer);
                });
            }
        }
    }


    /**
     * Creates a CFG from the tokens
     * @param rule the grammar rule to apply
     */
    public void parse(Grammar.Rule rule) {
        switch (this) {
            case FileTreeUnit unit -> {
                unit.syntaxResult = new Parser(new TokenStream<>(unit.tokens)).build(rule);
                unit.tokens = new ArrayList<>();
            }
            case FileTreePackage treePackage -> {
                treePackage.children().parallelStream().forEach(ref -> {
                    ref.parse(rule);
                });
            }
        }
    }


    /**
     * Represents a file
     */
    @Getter
    public static final class FileTreeUnit extends FileTreeEntry {
        private final File file;
        private @Nullable Parser.SyntaxResult syntaxResult = null;
        private List<TokenRecord> tokens = new ArrayList<>();

        public FileTreeUnit(String localName, String name, File file) {
            super(localName, name);
            this.file = file;
        }

    }

    /**
     * Represents a folder
     */
    @Getter
    public static final class FileTreePackage extends FileTreeEntry {
        private final List<FileTreeEntry> children;

        public FileTreePackage(String localName, String name, List<FileTreeEntry> children) {
            super(localName, name);
            this.children = children;
        }
    }
}
