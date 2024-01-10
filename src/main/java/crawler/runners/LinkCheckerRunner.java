package crawler.runners;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crawler.entities.Link;

public class LinkCheckerRunner implements Runnable, StoppableRunnerInterface {
    private boolean shouldKeepRunning = true;

    private BlockingQueue<Link> linksToCheck;
    private BlockingQueue<Link> linksToProcess;

    private HashSet<String> visited = new HashSet<String>();

    public LinkCheckerRunner(
        BlockingQueue<Link> linksToCheck,
        BlockingQueue<Link> linksToProcess
    ) {
        this.linksToCheck = linksToCheck;
        this.linksToProcess = linksToProcess;
    }

    @Override
    public void run() {
        while (shouldKeepRunning) {
            try {
                Link link = linksToCheck.poll(500, TimeUnit.MILLISECONDS);
                if (link == null) {
                    continue;
                }
                if (!visited.contains(link.location())) {
                    visited.add(link.location());
                    boolean added = false;
                    while (shouldKeepRunning && !added) {
                        added = linksToProcess.offer(link, 500, TimeUnit.MILLISECONDS);
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
