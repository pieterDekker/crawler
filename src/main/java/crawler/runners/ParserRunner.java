package crawler.runners;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.UriExtractor;
import crawler.entities.Link;
import crawler.entities.PageAtDepth;

public class ParserRunner implements Runnable, StoppableRunnerInterface {
    private boolean shouldKeepRunning = true;

    private BlockingQueue<PageAtDepth> pagesToParse;
    private BlockingQueue<Link> linksToCheck;

    private UriExtractor uriExtractor = new UriExtractor();

    public ParserRunner(
        BlockingQueue<PageAtDepth> pagesToParse,
        BlockingQueue<Link> linksToCheck
    ) {
        this.pagesToParse = pagesToParse;
        this.linksToCheck = linksToCheck;
    }

    @Override
    public void run() {
        while (shouldKeepRunning) {
            try {
                PageAtDepth pageAtDepth = pagesToParse.poll(500, TimeUnit.MILLISECONDS);
                if (pageAtDepth == null) {
                    continue;
                }
                for (String uri : uriExtractor.processPage(pageAtDepth.page())) {
                    boolean added = false;
                    while (shouldKeepRunning && !added) {
                        added = linksToCheck.offer(new Link(uri, pageAtDepth.depth() + 1), 500, TimeUnit.MILLISECONDS);
                    }
                    if (!shouldKeepRunning) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }   
        }
    }

    public void stop() {
        this.shouldKeepRunning = false;
    }
}
