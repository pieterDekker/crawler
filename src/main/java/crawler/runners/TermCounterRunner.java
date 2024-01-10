package crawler.runners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.entities.TermCount;

public class TermCounterRunner implements Runnable, StoppableRunnerInterface {
    private boolean shouldKeepRunning = true;

    private BlockingQueue<TermCount> termCountsToProcess;

    private HashMap<String, Integer> termCounts = new HashMap<String, Integer>();

    public TermCounterRunner(
        BlockingQueue<TermCount> termCountsToProcess
    ) {
        this.termCountsToProcess = termCountsToProcess;
    }

    @Override
    public void run() {
        while (shouldKeepRunning) {
            try {
                TermCount termCount = termCountsToProcess.poll(500, TimeUnit.MILLISECONDS);
                if (termCount == null) {
                    continue;
                }
                termCounts.put(termCount.term() , termCounts.getOrDefault(termCount.term(),  0) + termCount.count());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }   
        }
    }

    public TermCount topTermCount() {
        String topTerm = "";
        int topCount = 0;
        for (Map.Entry<String, Integer> e : termCounts.entrySet()) {
            if (e.getValue() > topCount) {
                topTerm = e.getKey();
                topCount = e.getValue();
            }
        }
        return new TermCount(topTerm, topCount);
    }

    public void stop() {
        this.shouldKeepRunning = false;
    }
}
