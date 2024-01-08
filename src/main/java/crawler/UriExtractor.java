package crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import crawler.entities.Page;

public class UriExtractor {
    public ArrayList<String> processPage(Page page) {
        ArrayList<String> links = new ArrayList<String>();

        Pattern pattern = Pattern.compile("<a\\s+href\\s*=\\s*\"([^\"#]*)\"");
        Matcher matcher = pattern.matcher(page.html());
        while (matcher.find()) {
            String link = matcher.group(1);
            link = new LinkSanitizer().sanitize(link, page.location());
            links.add(link);
        }
        return links;
    }
}
