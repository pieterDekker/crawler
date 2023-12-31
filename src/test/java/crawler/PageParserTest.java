package crawler;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

// @RunWith(DataProviderRunner.class)
// See https://stackoverflow.com/questions/19203730/writing-java-tests-with-data-providers
public class PageParserTest {
    @Test
    public void shouldAnswerWithTrue()
    {
        LinkExtractor parser = new LinkExtractor();
        String html = "<a href=\"/wiki/Java_(programming_language)\" title=\"Java (programming language)\">Java</a>";
        assertTrue(parser.parseHtml(html).get(0).equals("/wiki/Java_(programming_language)"));
    }

    // @DataProvider
    // public static Object[][] provideStringAndExpectedLength() {
    //     return new Object[][] {
    //         { "Hello World", 11 },
    //         { "Foo", 3 }
    //     };
    // }
}
