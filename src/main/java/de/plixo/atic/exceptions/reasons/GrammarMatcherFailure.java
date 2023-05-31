package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.GrammarReader;
import org.jetbrains.annotations.Nullable;

public final class GrammarMatcherFailure extends Failure {

    private final MatchFailType failType;
    private final @Nullable GrammarReader.Rule rule;

    private final @Nullable String name;

    public GrammarMatcherFailure(GrammarReader.Rule rule, String name, boolean isLiteral) {
        setMessage("Failed to capture keyword " + name + " in " + rule.name());
        this.name = name;
        this.rule = rule;
        this.failType = isLiteral ? MatchFailType.RULE : MatchFailType.KEYWORD;
    }

    public GrammarMatcherFailure(String entry) {
        this.name = entry;
        this.rule = null;
        this.failType = MatchFailType.UNKNOWN_ENTRY;
        setMessage("Unknown Rule " + entry);
    }

    public enum MatchFailType {
        KEYWORD,
        RULE,
        UNKNOWN_ENTRY,
    }
}
