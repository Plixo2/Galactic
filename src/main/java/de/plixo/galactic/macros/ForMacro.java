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

public class ForMacro extends Macro {

    Grammar.Rule rule;

    public ForMacro(Grammar.RuleSet standardGrammar) {
        super("for");
        var grammar = Universe.generateGrammar("/macros.txt", standardGrammar);
        rule = grammar.get("forMacro");
        assert rule != null : "Rule for 'for' not found";
    }


    @Override
    public MacroResult execute(TokenRecord macroRecord, TokenStream<TokenRecord> stream,
                               Tokenizer tokenizer) {
//        assert stream.current().token() instanceof MacroToken;
//        stream.consume();
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
        node.assertType("forMacro");
        var forInit = node.get("forInit").get("expression");
        var forCondition = node.get("forCondition").get("expression");
        var forUpdate = node.get("forUpdate").get("expression");
        var expression = node.get("expression");

        var tokens = new ArrayList<TokenRecord>();
        //create outer block, so the for loop variables can be encapsulated
        tokens.addAll(tokenizer.dummy(creator, "{"));
        tokens.addAll(forInit.tokenize());
        tokens.addAll(tokenizer.dummy(creator, "while"));
        tokens.addAll(forCondition.tokenize());
        tokens.addAll(tokenizer.dummy(creator, "{"));

        //encapsulate the expression in a block, so it cannot be used for updating the for loop
        tokens.addAll(tokenizer.dummy(creator, "{"));
        tokens.addAll(expression.tokenize());
        tokens.addAll(tokenizer.dummy(creator, "}"));

        //update loop
        tokens.addAll(forUpdate.tokenize());
        tokens.addAll(tokenizer.dummy(creator, "}"));

        tokens.addAll(tokenizer.dummy(creator, "}"));

//        for (TokenRecord token : tokens) {
//            System.out.println(token.literal());
//        }

        return tokens;
    }


}
