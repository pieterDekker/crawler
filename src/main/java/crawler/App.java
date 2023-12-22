package crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import crawler.entities.Link;

class App {
    public static int MAX_DEPTH = 4;
    public static boolean limitRecursion = true;
    public static void main(String[] args) {
        PageFetcher fetcher = new PageFetcher();
        PageParser parser = new PageParser();

        Set<String> visited = new HashSet<String>();

        LinkedBlockingQueue<Link> queue = new LinkedBlockingQueue<Link>();
        queue.add(new Link("https://en.wikipedia.org/wiki/Open-source_intelligence", 0));
        visited.add("https://en.wikipedia.org/wiki/Open-source_intelligence");
        Link link;
        while ((link = queue.poll()) != null) {
            System.out.println("visiting " + link.url() + " at depth " + link.depth());
            String html = fetcher.getPage(link.url());
            ArrayList<String> newUrls = parser.parseHtml(html);
            int added = 0;
            for (String newUrl : newUrls) {
                if (newUrl.contains(":")) {
                    continue;
                }
                Link newLink = new Link("https://en.wikipedia.org" + newUrl, link.depth() + 1);
                if (visited.contains(newLink.url())) {
                    continue;
                }
                if (link.depth() == MAX_DEPTH) {
                    continue;
                }
                queue.add(new Link("https://en.wikipedia.org" + newUrl, link.depth() + 1));
                visited.add(newLink.url());
                added++;
                if (limitRecursion && added > 1) {
                    break;
                }
            }
        }
    }
}