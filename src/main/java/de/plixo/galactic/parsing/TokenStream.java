package de.plixo.galactic.parsing;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple stream structure with resetting
 *
 * @param <T>
 */
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

    /**
     * Creates a list of leftover tokens
     *
     * @return list of tokens left in the stream
     */
    public List<T> left() {
        var left = new ArrayList<T>();
        while (this.hasEntriesLeft()) {
            left.add(this.current());
            this.consume();
        }
        return left;
    }
}
