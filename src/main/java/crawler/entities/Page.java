package crawler.entities;

import java.net.URI;
import java.util.Objects;

public record Page(URI location, String html) {
    public Page {
        Objects.requireNonNull(html);
    }
}
