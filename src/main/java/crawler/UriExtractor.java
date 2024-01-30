package crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import crawler.entities.Link;
import crawler.entities.Page;

public class UriExtractor {
    // TODO: use a python-generator-like approach to avoid loading the whole list of links into memory
    public ArrayList<Link> processPage(Page page) {
        ArrayList<Link> links = new ArrayList<Link>();

        Pattern pattern = Pattern.compile("<a\\s+href\\s*=\\s*\"([^\"#]*)\"");
        Matcher matcher = pattern.matcher(page.html());
        while (matcher.find()) {
            String location = matcher.group(1);
            location = new LinkSanitizer().sanitize(location, page.location());
            Link link = new Link(location, page.depth() + 1);
            links.add(link);
        }
        return links;
    }
}
