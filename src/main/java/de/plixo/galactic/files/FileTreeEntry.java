package de.plixo.galactic.files;

import de.plixo.galactic.exception.TokenFlairHandler;
import de.plixo.galactic.lexer.Position;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.lexer.tokens.CommentToken;
import de.plixo.galactic.lexer.tokens.EOFToken;
import de.plixo.galactic.lexer.tokens.UnknownToken;
import de.plixo.galactic.lexer.tokens.WhiteSpaceToken;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.parsing.Parser;
import de.plixo.galactic.parsing.TokenStream;
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
     * Reads the file, turns it into tokens, then stores the tokens in the unit
     *
     * @param tokenizer the tokenizer to use
     */
    public void readAndLex(Tokenizer tokenizer, TokenFlairHandler errorHandler) {
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
                        errorHandler.add(ref);
                        yield false;
                    }
                    case WhiteSpaceToken ignored -> false;
                    case CommentToken ignored -> false;
                    default -> true;
                }).toList();

                var tokens = new ArrayList<>(filteredTokens);
                int line = 0;
                if (!tokens.isEmpty()) {
                    line = tokens.getLast().position().right().line();
                }
                var eofRegion = new Position(unit.file, line + 1, 0).toRegion();
                tokens.add(
                        new TokenRecord(new EOFToken(), "", eofRegion));
                unit.tokens = tokens;
            }
            case FileTreePackage treePackage -> {
                treePackage.children().forEach(ref -> {
                    ref.readAndLex(tokenizer, errorHandler);
                });
            }
        }
    }


    /**
     * Creates a CFG from the tokens
     *
     * @param rule the grammar rule to apply
     */
    public void parse(Grammar.Rule rule) {
        switch (this) {
            case FileTreeUnit unit -> {
                unit.syntaxResult = new Parser(new TokenStream<>(unit.tokens)).build(rule);
                unit.tokens = new ArrayList<>();
            }
            case FileTreePackage treePackage -> {
                treePackage.children().forEach(ref -> {
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
