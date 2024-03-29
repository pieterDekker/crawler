package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import crawler.entities.Link;
import crawler.entities.Page;

public class SingleThreadedCrawler implements CrawlerInterface {
    private PageFetcher fetcher;
    private Set<String> visited;
    private LinkedBlockingQueue<Link> queue;
    private HashMap<String, Integer> wordCounts;
    private WordCounter wc;

    public SingleThreadedCrawler() {
        this.fetcher = new PageFetcher();
        this.visited = new HashSet<String>();
        this.queue = new LinkedBlockingQueue<Link>();
        this.wordCounts = new HashMap<>();
        this.wc = new WordCounter();
    }

    public void crawl(int maxDepth, int maxSeconds, String seedUrl) {
        StopWatch sw = new StopWatch();
        sw.start();

        queue.add(new Link(seedUrl, 0));
        visited.add(seedUrl);

        Link link;
        while ((link = queue.poll()) != null) {
            if (sw.getTime(TimeUnit.SECONDS) > maxSeconds) {
                break;
            }
            Page page = fetcher.getPage(link);
            countWords(page);
            if (link.depth() < maxDepth) {
                insertNewUrls(page, queue, visited, link.depth());
            }
        }
        wc.close();
        printTopWord();
    }

    private void countWords(Page page) {
        for (Map.Entry<String, Integer> e: wc.countWords(page).entrySet()) {
            wordCounts.put(e.getKey(), wordCounts.getOrDefault(e.getKey(),  0) + e.getValue());
        }
    }

    private void insertNewUrls(Page page, LinkedBlockingQueue<Link> queue, Set<String> visited, int currentDepth) {
        ArrayList<Link> newLinks = new UriExtractor().processPage(page);
        for (Link newLink : newLinks) {
            if (visited.contains(newLink.location())) {
                continue;
            }
            queue.add(newLink);
            visited.add(newLink.location());
        }
    }

    private void printTopWord() {
        String topWord = "";
        int topCount = 0;
        for (Map.Entry<String, Integer> e: wordCounts.entrySet()) {
            if (e.getValue() > topCount) {
                topCount = e.getValue();
                topWord = e.getKey();
            }
        }
        System.out.println("Top term: '" + topWord + "' with count " + topCount);
    }
}
