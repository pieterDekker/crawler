package crawler;

public interface CrawlerInterface {
    public void crawl(int maxDepth, int maxSeconds, String seedUrl);
}
