package crawler.entities;

import java.util.Objects;

public record Link(String location, int depth) {
    public Link {
        Objects.requireNonNull(location);
        if (depth < 0) {
            throw new IllegalArgumentException("Depth must be non-negative");
        }
    }
}
