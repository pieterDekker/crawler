package crawler.runners;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.PageFetcher;
import crawler.entities.Link;
import crawler.entities.Page;
import crawler.entities.PageAtDepth;

public class FetcherRunner implements Runnable, StoppableRunnerInterface {
    private boolean shouldKeepRunning = true;
    private int maxDepth;
    private int pagesFetched = 0;
    
    private BlockingQueue<Link> linksToProcess;
    private BlockingQueue<PageAtDepth> pagesToParse;
    private BlockingQueue<Page> PagesToExctractTermsFrom;
    
    private PageFetcher pageFetcher = new PageFetcher();

    public FetcherRunner(
        BlockingQueue<Link> linksToProcess,
        BlockingQueue<PageAtDepth> pagesToParse,
        BlockingQueue<Page> pagesToExctractTermsFrom,
        int maxDepth
    ) {
        this.linksToProcess = linksToProcess;
        this.pagesToParse = pagesToParse;
        this.PagesToExctractTermsFrom = pagesToExctractTermsFrom;
        this.maxDepth = maxDepth;
    }

    @Override
    public void run() {
        while (shouldKeepRunning) {
            try {
                Link link = linksToProcess.poll(500, TimeUnit.MILLISECONDS);
                if (link == null) {
                    continue;
                }
                Page page = this.pageFetcher.getPage(link.location());
                PagesToExctractTermsFrom.put(page);
                if (link.depth() < maxDepth) {
                    PageAtDepth pageAtDepth = new PageAtDepth(page, link.depth());
                    boolean added = false;
                    while (shouldKeepRunning && !added) {
                        added = pagesToParse.offer(pageAtDepth, 500, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pagesFetched++;
        }
    }

    public int pagesFetched() {
        return pagesFetched;
    }
    
    public void stop() {
        this.shouldKeepRunning = false;
    }
}
