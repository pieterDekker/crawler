package crawler.entities;

import java.util.Objects;

public record Link(String url, int depth) {
    public Link {
        Objects.requireNonNull(url);
        if (depth < 0) {
            throw new IllegalArgumentException("Depth must be non-negative");
        }
    }
}
