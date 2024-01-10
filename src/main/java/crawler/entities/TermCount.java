package crawler.entities;

import java.util.Objects;

public record TermCount(String term, int count) {
    public TermCount {
        Objects.requireNonNull(term);
        if (count < 0) {
            throw new IllegalArgumentException("count must be non-negative");
        }
    }
}
