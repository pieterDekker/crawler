package crawler;
import java.net.URI;

public class LinkSanitizer {
    public String sanitize(String link, URI origin) {
        link = ensureProtocol(link, origin);
        link = ensureAbsoluteLink(link, getBaseUriString(origin));
        return link;
    }

    private String getBaseUriString(URI u) {
        String scheme = u.getScheme();
        String host = u.getHost();
        int port = u.getPort();
        if (port == -1) {
            return scheme + "://" + host;
        }
        return scheme + "://" + host + ":" + port;
    }

    private String ensureAbsoluteLink(String link, String currentBaseUrl) {
        if (isRelativeLink(link)) {
            return currentBaseUrl + link;
        }
        return link;
    }

    private String ensureProtocol(String link, URI currentUri) {
        if (!hasProtocol(link) && link.startsWith("//")) {
            return currentUri.getScheme() + ":" + link;
        }
        return link;
    }

    private boolean isRelativeLink(String link) {
        if (link.startsWith("//")) { // absolute link without scheme, use scheme from current location
            return false;
        }
        if (link.startsWith("/")) {
            return true;
        }
        return false;
    }

    private boolean hasProtocol(String link) {
        return link.startsWith("http");
    }
}
