package crawler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;

import crawler.entities.Page;

public class UriExtractorTest {
    @Test
    public void shouldReturnRelativeUrl()
    {
        UriExtractor parser = new UriExtractor();
        String html = "<a href=\"/wiki/Java_(programming_language)\" title=\"Java (programming language)\">Java</a>";
        URI location = URI.create("https://en.wikipedia.org/wiki/Java");
        Page page = new Page(location, html);
        assertTrue(parser.processPage(page).get(0).equals("https://en.wikipedia.org/wiki/Java_(programming_language)"));
    }

    @Test
    public void shouldNotReturnPlaceholder()
    {
        UriExtractor parser = new UriExtractor();
        String html = "<a href=\"#local-element\">Local Element</a>";
        URI location = URI.create("https://en.wikipedia.org/wiki/Java");
        Page page = new Page(location, html);
        assertTrue(parser.processPage(page).isEmpty());
    }
}
