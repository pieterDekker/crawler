package crawler;

public class App {
    public static void main(String[] args) {
        new Crawler().crawl(4, 60, "https://en.wikipedia.org/wiki/Open-source_intelligence");
    }
}