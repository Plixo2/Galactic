package de.plixo.galactic.macros;

import de.plixo.galactic.Universe;
import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.lexer.tokens.MacroToken;
import de.plixo.galactic.parsing.Grammar;
import de.plixo.galactic.parsing.Node;
import de.plixo.galactic.parsing.Parser;
import de.plixo.galactic.parsing.TokenStream;

import java.util.ArrayList;

public class ForMacro extends Macro {

    Grammar.Rule rule;

    public ForMacro(Grammar.RuleSet standardGrammar) {
        super("for");
        var grammar = Universe.generateGrammar("/macros.txt", standardGrammar);
        rule = grammar.get("for");
        assert rule != null : "Rule for 'for' not found";
    }


    @Override
    public MacroResult execute(TokenStream<TokenRecord> stream) {
        assert stream.current().token() instanceof MacroToken;
        stream.consume();
        var parser = new Parser(stream, new ArrayList<>());
        var build = parser.buildPartial(rule);
        if (!(build instanceof Parser.SyntaxMatch match)) {
            return new MacroResult(build);
        }
        var ok = new Parser.SyntaxMatch(convert(match.node()));
//        var index = stream.index();
//        stream.setIndex(0);
//        stream.left().forEach(System.out::println);
//        stream.setIndex(index);
        return new MacroResult(ok);
//        return new MacroResult(new Parser.SyntaxMatch(new Node(match.node().record(), "hello", new ArrayList<>(), match.node().region())));
//        return new MacroResult(
//                new Parser.FailedMacro(stream.left(), this, "ForMacro not implemented yet"));
    }

    private Node convert(Node node) {
        var record = node.record();
        var region = node.region();
        node.assertType("for");
        var forInit = node.get("forInit").get("expression");

        var forCondition = node.get("forCondition").get("expression");
        var forUpdate = node.get("forUpdate").get("expression");

        var expression = node.get("expression");
        return expression;
        //return new Node(record, "blockExpr", new ArrayList<>(), region);
    }

}
