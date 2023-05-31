package de.plixo.atic.exceptions.reasons;

public final class GrammarRuleFailure extends Failure{

    public GrammarRuleFailure(String msg) {
        setMessage(msg);
    }
}
