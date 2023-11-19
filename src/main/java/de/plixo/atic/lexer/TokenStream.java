package de.plixo.atic.lexer;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

public class TokenStream<T> {
    private final List<T> list;
    @Setter
    @Accessors(fluent = false)
    protected int index = 0;

    public TokenStream(List<T> list) {
        this.list = list;
    }

    public T current() {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public boolean hasEntriesLeft() {
        return index < list.size();
    }

    public void consume() {
        index += 1;
    }

    public int index() {
        return index;
    }

    public TokenStream<T> copy() {
        var stream = new TokenStream<>(list);
        stream.index = this.index;
        return stream;
    }
}
