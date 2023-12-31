package crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    public ArrayList<String> parseHtml(String html) {
        ArrayList<String> links = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<a\\s+href\\s*=\\s*\"(/wiki[^\"]*)\"");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            if (matcher.group(1).contains(":")) {
                continue;
            }
            links.add(matcher.group(1));
        }
        return links;
    }
}
