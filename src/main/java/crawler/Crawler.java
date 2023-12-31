package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.entities.Link;

public class Crawler {
    private PageFetcher fetcher;
    private Set<String> visited;
    private LinkedBlockingQueue<Link> queue;
    private HashMap<String, Integer> wordCounts;
    private WordCounter wc;

    public Crawler() {
        this.fetcher = new PageFetcher();
        this.visited = new HashSet<String>();
        this.queue = new LinkedBlockingQueue<Link>();
        this.wordCounts = new HashMap<>();
        this.wc = new WordCounter();
    }

    public void crawl(int MAX_DEPTH, int MAX_SECONDS, String seedUrl) {
        long t = System.currentTimeMillis();

        queue.add(new Link(seedUrl, 0));
        visited.add(seedUrl);
        Link link;
        while ((link = queue.poll()) != null) {
            if ((System.currentTimeMillis() - t )> TimeUnit.SECONDS.toMillis(MAX_SECONDS)) {
                System.out.println("1 minute elapsed");
                break;
            }
            System.out.println("visiting " + link.url() + " at depth " + link.depth());
            String html = fetcher.getPage(link.url());

            for (Map.Entry<String, Integer> e: wc.countWords(html).entrySet()) {
                wordCounts.put(e.getKey(), wordCounts.getOrDefault(e.getKey(),  0) + e.getValue());
            }
            
            if (link.depth() == MAX_DEPTH) { // No need to look for more links
                    continue;
            }
            insertNewUrls(html, queue, visited, link.depth());
        }
        wc.close();
        printTopWord();
    }

    private void insertNewUrls(String html, LinkedBlockingQueue<Link> queue, Set<String> visited, int currentDepth) {
        ArrayList<String> newUrls = new LinkExtractor().parseHtml(html);
        for (String newUrl : newUrls) {
            Link newLink = new Link("https://en.wikipedia.org" + newUrl, currentDepth + 1);
            if (visited.contains(newLink.url())) {
                continue;
            }
            queue.add(new Link("https://en.wikipedia.org" + newUrl, currentDepth + 1));
            visited.add(newLink.url());
        }
    }

    private void printTopWord() {
        String topWord = "";
        int topCount = 0;
        for (Map.Entry<String, Integer> e: wordCounts.entrySet()) {
            if (e.getValue() > 100) {
                System.out.println(e.getKey() + " " + e.getValue());
            }
            if (e.getValue() > topCount) {
                topCount = e.getValue();
                topWord = e.getKey();
            }
        }
        System.out.println("top word: " + topWord + " with " + topCount + " occurrences");
    }
}
