package crawler.entities;

import java.util.Objects;

public record PageAtDepth(Page page, int depth) {
    public PageAtDepth {
        Objects.requireNonNull(page);
    }
}
