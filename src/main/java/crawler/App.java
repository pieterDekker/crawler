package crawler;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java -jar crawler.jar <seed-url> <max-depth> <max-seconds> <crawler-type>");
            System.exit(1);
        }

        String seedUrl = args[0];
        int maxDepth = 4;
        int maxSeconds = 60;

        try {
            maxDepth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("max-depth must be an integer");
            System.exit(1);
        }
        try {
            maxSeconds = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("max-seconds must be an integer");
            System.exit(1);
        }
        ArrayList<String> crawlerTypes = new ArrayList<String>();
        crawlerTypes.add("single-threaded");
        crawlerTypes.add("multi-threaded");
        if (!crawlerTypes.contains(args[3])) {
            System.out.println("crawler-type must be one of: single-threaded, multi-threaded");
            System.exit(1);
        }

        switch (args[3]) {
            case "single-threaded":
                new SingleThreadedCrawler().crawl(maxDepth, maxSeconds, seedUrl);
                break;
            case "multi-threaded":
                new MultiThreadedCrawler().crawl(maxDepth, maxSeconds, seedUrl);
        }
    }
}