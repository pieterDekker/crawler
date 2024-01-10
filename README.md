# Crawler

A simple crawler, built in Java, that finds the most import word across the pages that were scanned.

## Usage

Open a terminal and run the following command.
```shell
mvn compile -q exec:java -Dexec.mainClass="crawler.App" -Dexec.args="https://en.wikipedia.org/wiki/Open-source_intelligence 5 60"
```

The first argument is the seed URL, the second the maximum depth and the third the maximum runtime.