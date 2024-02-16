package de.plixo.galactic.macros;

import de.plixo.galactic.Universe;
import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.parsing.Node;
import de.plixo.galactic.parsing.Parser;
import de.plixo.galactic.parsing.TokenStream;

import java.util.ArrayList;
import java.util.List;

public class ForEachMacro extends Macro {

    Grammar.Rule rule;

    public ForEachMacro(Grammar.RuleSet standardGrammar) {
        super("forEach");
        var grammar = Universe.generateGrammar("/macros.txt", standardGrammar);
        rule = grammar.get("forEachMacro");
        assert rule != null : "Rule for 'forEach' not found";
    }


    @Override
    public MacroResult execute(TokenRecord macroRecord, TokenStream<TokenRecord> stream,
                               Tokenizer tokenizer) {
        var parser = new Parser(stream);
        var build = parser.buildPartial(rule);
        if (!(build instanceof Parser.SyntaxMatch match)) {
            if (!stream.hasEntriesLeft()) {
                return new MacroResult.FailedMacro(macroRecord.position(), build.message());
            } else {
                return new MacroResult.FailedMacro(stream.current().position(), build.message());
            }
        }

        var node = match.node();
        return new MacroResult.RecordStream(convert(macroRecord, node, tokenizer));
    }

    private List<TokenRecord> convert(TokenRecord creator, Node node, Tokenizer tokenizer) {
        node.assertType("forEachMacro");
        var varName = node.getID();
        var forEachIter = node.get("forEachIter").get("expression");
        var expression = node.get("expression");
        var varIterName = STR."iter_\{varName}";

        var tokens = new ArrayList<TokenRecord>();
        tokens.addAll(tokenizer.dummy(creator, "{"));
        tokens.addAll(tokenizer.dummy(creator, STR."var \{varIterName} = "));
        tokens.addAll(forEachIter.tokenize());
        tokens.addAll(tokenizer.dummy(creator, ".iterator()"));

        tokens.addAll(tokenizer.dummy(creator, "while"));
        tokens.addAll(tokenizer.dummy(creator, varIterName));
        tokens.addAll(tokenizer.dummy(creator, ".hasNext()"));
        tokens.addAll(tokenizer.dummy(creator, "{"));
        tokens.addAll(tokenizer.dummy(creator, STR."var \{varName} = \{varIterName}.next()"));
        tokens.addAll(expression.tokenize());
        tokens.addAll(tokenizer.dummy(creator, "}"));

        tokens.addAll(tokenizer.dummy(creator, "}"));


        return tokens;
    }


}
