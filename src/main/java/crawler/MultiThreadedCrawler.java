package crawler;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import crawler.entities.Link;
import crawler.entities.Page;
import crawler.entities.PageAtDepth;
import crawler.entities.TermCount;
import crawler.runners.FetcherRunner;
import crawler.runners.LinkCheckerRunner;
import crawler.runners.ParserRunner;
import crawler.runners.StoppableRunnerInterface;
import crawler.runners.TermCountExtractorRunner;
import crawler.runners.TermCounterRunner;

// TODO: implement stopping after no more pages in queue while max seconds not yet elapsed
// TODO: implement scaling
public class MultiThreadedCrawler implements CrawlerInterface {
    public void crawl(int maxDepth, int maxSeconds, String seedUrl) {
        BlockingQueue<Link> linksToProcess = new ArrayBlockingQueue<Link>(1000, false);
        BlockingQueue<PageAtDepth> pagesToParse = new ArrayBlockingQueue<PageAtDepth>(1000, false);
        BlockingQueue<Page> pagesToExctractTermsFrom = new ArrayBlockingQueue<Page>(1000, false);
        BlockingQueue<Link> linksToCheck = new ArrayBlockingQueue<Link>(1000, false);
        BlockingQueue<TermCount> termCountsToProcess = new ArrayBlockingQueue<TermCount>(1000, false);

        ArrayList<StoppableRunnerInterface> runners = new ArrayList<StoppableRunnerInterface>();

        FetcherRunner fetcherRunner = new FetcherRunner(linksToProcess, pagesToParse, pagesToExctractTermsFrom, maxDepth);
        ParserRunner parserRunner = new ParserRunner(pagesToParse, linksToCheck);
        TermCountExtractorRunner termCountExtractorRunner = new TermCountExtractorRunner(pagesToExctractTermsFrom, termCountsToProcess);
        LinkCheckerRunner linkCheckerRunner = new LinkCheckerRunner(linksToCheck, linksToProcess);
        TermCounterRunner termCounterRunner = new TermCounterRunner(termCountsToProcess);

        runners.add(fetcherRunner);
        runners.add(parserRunner);
        runners.add(termCountExtractorRunner);
        runners.add(linkCheckerRunner);
        runners.add(termCounterRunner);

        ArrayList<Thread> threads = new ArrayList<Thread>();
        threads.add(Thread.ofVirtual().name("Fetcher").start(fetcherRunner));
        threads.add(Thread.ofVirtual().name("Parser").start(parserRunner));
        threads.add(Thread.ofVirtual().name("TermCountExtractor").start(termCountExtractorRunner));
        threads.add(Thread.ofVirtual().name("LinkChecker").start(linkCheckerRunner));
        threads.add(Thread.ofVirtual().name("TermCounter").start(termCounterRunner));

        linksToProcess.add(new Link(seedUrl, 0));

        try {
            Thread.sleep(maxSeconds * 1000);

            for (StoppableRunnerInterface runner : runners) {
                runner.stop();
            }

            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TermCount topTermCount = termCounterRunner.topTermCount();
        System.out.println("Top term is '" + topTermCount.term() + "' with count " + topTermCount.count());
    }
}
