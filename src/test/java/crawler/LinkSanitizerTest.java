package crawler;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LinkSanitizerTest {
    @Test
    public void shouldSanitizeRelativeLink() {
        String link = "/wiki/Java_(programming_language)";
        URI origin = URI.create("https://en.wikipedia.org/wiki/Java");
        String expected = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String actual = new LinkSanitizer().sanitize(link, origin);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldSanitizeProtocolRelativeLinkHttp() {
        String link = "//en.wikipedia.org/wiki/Wikipedia:Contact_us";
        URI origin = URI.create("http://en.wikipedia.org/wiki/Java");
        String expected = "http://en.wikipedia.org/wiki/Wikipedia:Contact_us";
        String actual = new LinkSanitizer().sanitize(link, origin);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldSanitizeProtocolRelativeLinkHttps() {
        String link = "//en.wikipedia.org/wiki/Wikipedia:Contact_us";
        URI origin = URI.create("https://en.wikipedia.org/wiki/Java");
        String expected = "https://en.wikipedia.org/wiki/Wikipedia:Contact_us";
        String actual = new LinkSanitizer().sanitize(link, origin);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotChangeAbsoluteLink() {
        String link = "http://www.google.com";
        URI origin = URI.create("https://en.wikipedia.org/wiki/Java");
        String expected = "http://www.google.com";
        String actual = new LinkSanitizer().sanitize(link, origin);
        assertEquals(expected, actual);
    }
}
