package de.plixo.galactic.macros;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.lexer.tokens.MacroToken;
import de.plixo.galactic.parsing.TokenStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Macro {
    private final String name;


    /**
     * Execute the macro on a stream of tokens
     * Modifies the stream and returns a new stream with new tokens
     *
     * @param stream token stream input (modifiable)
     * @return new token stream for the result
     */
    public abstract MacroResult execute(TokenRecord macroToken, TokenStream<TokenRecord> stream,
                                        Tokenizer tokenizer);


    public sealed interface MacroResult {
        record RecordStream(List<TokenRecord> records) implements MacroResult {

        }

        record FailedMacro(Region region, String message) implements MacroResult {

        }
    }

    public static MacroResult convert(List<Macro> macros, List<TokenRecord> records,
                                      Tokenizer tokenizer) {

        var currentRecords = records;

        var converted = true;
        while (converted) {
            converted = false;
            for (int i = 0; i < currentRecords.size(); i++) {
                var currentRecord = currentRecords.get(i);
                if (currentRecord.token() instanceof MacroToken) {
                    var literal = currentRecord.literal();
                    assert !literal.isEmpty();
                    var macroName = literal.substring(1);
                    var macro = getMacro(macroName, macros);
                    if (macro == null) {
                        return new MacroResult.FailedMacro(currentRecord.position(),
                                STR."Macro not found: \{macroName}");
                    }
                    var left = currentRecords.subList(0, i);
                    var right = currentRecords.subList(i + 1, currentRecords.size());
                    var macroInput = new TokenStream<>(right);
                    var result = macro.execute(currentRecord, macroInput, tokenizer);

                    switch (result) {
                        case MacroResult.FailedMacro failedMacro -> {
                            return failedMacro;
                        }
                        case MacroResult.RecordStream recordStream -> {
                            var resultTokenComplete = new ArrayList<TokenRecord>();
                            resultTokenComplete.addAll(left);
                            resultTokenComplete.addAll(recordStream.records);
                            resultTokenComplete.addAll(macroInput.tokenLeft());
                            currentRecords = resultTokenComplete;
                        }
                    }
                    converted = true;
                    break;
                }
            }
        }
//        if (currentRecords.getFirst().position().left().file().getAbsolutePath()
//                .contains("Macro")) {
//            for (TokenRecord currentRecord : currentRecords) {
//                System.out.println(currentRecord.literal());
//            }
//        }


        return new MacroResult.RecordStream(currentRecords);
    }

    private static @Nullable Macro getMacro(String macroName, List<Macro> macros) {
        return macros.stream().filter(m -> m.name().equals(macroName)).findFirst().orElse(null);
    }


}
