package crawler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import crawler.entities.Link;
import crawler.entities.Page;
import crawler.entities.TermCount;
import crawler.runners.StepRunnable;

// TODO: implement stopping after no more pages in queue while max seconds not yet elapsed. Idea: use CountDownLatches (or something similar) to signal when a queue is empty and when a worker is waiting for a queue to have elements
// TODO: turn this into asynchronous code using futures, or a reactive approach
public class MultiThreadedCrawler implements CrawlerInterface {
    public void crawl(int maxDepth, int maxSeconds, String seedUrl) {
        BlockingQueue<Link> linksToProcess = new ArrayBlockingQueue<Link>(1000, false);
        BlockingQueue<Page> pagesToExtractUrisFrom = new ArrayBlockingQueue<Page>(1000, false);
        BlockingQueue<Page> pagesToExctractTermsFrom = new ArrayBlockingQueue<Page>(1000, false);
        BlockingQueue<Link> linksToCheck = new ArrayBlockingQueue<Link>(1000, false);
        BlockingQueue<TermCount> termCountsToProcess = new ArrayBlockingQueue<TermCount>(1000, false);

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        PageFetcher pageFetcher = new PageFetcher();
        WordCounter wordCounter = new WordCounter();
        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();

        Runnable fetcher = this.buildFetcher(linksToProcess, pagesToExtractUrisFrom, pagesToExctractTermsFrom, pageFetcher, maxDepth);
        Runnable uriExtractor = this.buildUriExtractor(pagesToExtractUrisFrom, linksToCheck);
        Runnable termCountExtractor = this.buildTermCountExtractor(pagesToExctractTermsFrom, termCountsToProcess, wordCounter);
        Runnable termCounter = this.buildTermCounter(termCountsToProcess, wordCounts);
        Runnable linkChecker = this.buildLinkChecker(linksToCheck, linksToProcess);

        executorService.submit(fetcher);
        executorService.submit(uriExtractor);
        executorService.submit(termCountExtractor);
        executorService.submit(termCounter);
        executorService.submit(linkChecker);

        linksToProcess.add(new Link(seedUrl, 0));

        Instant start = Instant.now();
        try {
            Thread.sleep(maxSeconds * 1000);

            executorService.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();
        System.out.println("Crawled " + wordCounts.size() + " words in " + (end.getEpochSecond() - start.getEpochSecond()) + " seconds");
        TermCount topTermCount = this.topTermCount(wordCounts);
        System.out.println("Top term is '" + topTermCount.term() + "' with count " + topTermCount.count());
    }

    private Runnable buildFetcher(BlockingQueue<Link> linksToProcess, BlockingQueue<Page> pagesToExtractUrisFrom, BlockingQueue<Page> pagesToExctractTermsFrom, PageFetcher pageFetcher, int maxDepth) {
        return new StepRunnable<Link, Page>(
            () -> {
                return linksToProcess.take();
            },
            (Link link) -> {
                return pageFetcher.getPage(link);
            },
            (Page page) -> {
                pagesToExctractTermsFrom.add(page);
                if (page.depth() < maxDepth) {
                    pagesToExtractUrisFrom.add(page);
                }
            }
        );
    }

    private Runnable buildUriExtractor(BlockingQueue<Page> pagesToParse, BlockingQueue<Link> linksToCheck) {
        return new StepRunnable<Page, ArrayList<Link>>(
            () -> {
                return pagesToParse.take();
            },
            (Page page) -> {
                return new UriExtractor().processPage(page);
            },
            (ArrayList<Link> links) -> {
                for (Link link : links) {
                    linksToCheck.add(link);
                }
            }
        );
    }

    private Runnable buildLinkChecker(BlockingQueue<Link> linksToCheck, BlockingQueue<Link> linksToProcess) {
        HashSet<String> visited = new HashSet<String>();
        return new StepRunnable<Link, Link>(
            () -> {
                return linksToCheck.take();
            },
            (Link link) -> {
                return link;
            },
            (Link link) -> {
                if (!visited.contains(link.location())) {
                    visited.add(link.location());
                    linksToProcess.add(link);
                }
            }
        );
    }

    private Runnable buildTermCountExtractor(BlockingQueue<Page> pagesToExtractTermsFrom, BlockingQueue<TermCount> termCountsToProcess, WordCounter wordCounter) {
        return new StepRunnable<Page, HashMap<String, Integer>>(
            () -> {
                return pagesToExtractTermsFrom.take();
            },
            (Page page) -> {
                return wordCounter.countWords(page);
            },
            (HashMap<String, Integer> termCounts) -> {
                for (HashMap.Entry<String, Integer> e : termCounts.entrySet()) {
                    termCountsToProcess.add(new TermCount(e.getKey(), e.getValue()));
                }
            }
        );
    }

    private Runnable buildTermCounter(BlockingQueue<TermCount> termCountsToProcess, HashMap<String, Integer> wordCounts) {
        return new StepRunnable<TermCount, Void>(
            () -> {
                return termCountsToProcess.take();
            },
            (TermCount termCount) -> {
                wordCounts.put(termCount.term(), wordCounts.getOrDefault(termCount.term(), 0) + termCount.count());
                return null;
            },
            (Void v) -> {}
        );
    }

    public TermCount topTermCount(HashMap<String, Integer> termCounts) {
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
}
