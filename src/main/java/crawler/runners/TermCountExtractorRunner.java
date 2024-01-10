package crawler.runners;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.WordCounter;
import crawler.entities.Page;
import crawler.entities.TermCount;

public class TermCountExtractorRunner implements Runnable, StoppableRunnerInterface {
    private boolean shouldKeepRunning = true;
    private int itemsProcessed = 0;

    private BlockingQueue<Page> pagesToExctractTermsFrom;
    private BlockingQueue<TermCount> termCountsToProcess;

    WordCounter wordCounter = new WordCounter();

    public TermCountExtractorRunner(
        BlockingQueue<Page> PagesToExctractTermsFrom,
        BlockingQueue<TermCount> TermCountsToProcess
    ) {
        this.pagesToExctractTermsFrom = PagesToExctractTermsFrom;
        this.termCountsToProcess = TermCountsToProcess;
    }

    @Override
    public void run() {
        while (shouldKeepRunning) {
            try {
                Page page = pagesToExctractTermsFrom.poll(500, TimeUnit.MILLISECONDS);
                if (page == null) {
                    continue;
                }
                WordCounter wordCounter = new WordCounter();
                for (Map.Entry<String, Integer> e: wordCounter.countWords(page.html()).entrySet()) {
                    boolean added = false;
                    while (shouldKeepRunning && !added) {
                        added = termCountsToProcess.offer(new TermCount(e.getKey(), e.getValue()), 500, TimeUnit.MILLISECONDS);
                    }
                    if (!shouldKeepRunning) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                wordCounter.close();
                e.printStackTrace();
            }
            itemsProcessed++;
        }
        wordCounter.close();
    }

    public int itemsProcessed() {
        return itemsProcessed;
    }

    public void stop() {
        this.shouldKeepRunning = false;
    }
}
