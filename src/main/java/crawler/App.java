package crawler;

public class App {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar crawler.jar <seed-url> <max-depth> <max-seconds>");
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

        new Crawler().crawl(maxDepth, maxSeconds, seedUrl);
    }
}