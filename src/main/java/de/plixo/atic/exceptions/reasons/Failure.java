package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.exceptions.LangError;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract sealed class Failure
        permits DuplicateFailure, FeatureFailure, FileIOFailure, GeneralFailure,
        GrammarMatcherFailure, GrammarNodeFailure, GrammarRuleFailure, ImportFailure, ThreadFailure,
        TokenFailure, TypeError {

    @Getter
    private @Nullable Region region = null;

    @Getter
    private @Nullable File file = null;

    @Getter
    private @Nullable String message = null;
    @Getter
    private @Nullable Node node = null;

    @Getter
    private @Nullable Throwable internalError = null;

    public void setFile(File file) {
        this.file = file;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setInternalError(Throwable internalError) {
        this.internalError = internalError;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public LangError create() {
        return new LangError(this);
    }
}
