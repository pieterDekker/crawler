package crawler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class WordCounter {
    private StopAnalyzer analyzer;

    public WordCounter() {
        CharArraySet stopWordsSet;
        final List<String> stopWords = Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with", "en", "wikipedia", "org", "wiki", "https", "http", "com", "html"
        );
        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        stopWordsSet = CharArraySet.unmodifiableSet(stopSet);
        this.analyzer = new StopAnalyzer(stopWordsSet);
    }

    /**
     * Count words in a document. Normalize words by using {@link StopAnalyzer} with a custom stop word list. Any words
     * shorter than 6 characters are ignored.
     * 
     * @param docText
     * @return
     */
    public HashMap<String, Integer> countWords(String docText) {
        TokenStream ts = this.analyzer.tokenStream("text", docText);
        CharTermAttribute charTermAtt = ts.addAttribute(CharTermAttribute.class);

        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();

        try {
            ts.reset();
            while (ts.incrementToken()) {
                if (charTermAtt.length() < 6) {
                    continue;
                }
                String word = charTermAtt.toString();
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
            ts.end();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ts.close();
            } catch (Exception e) {
                e.printStackTrace();
                return new HashMap<String, Integer>();
            }
        }

        return wordCounts;
    }

    public void close() {
        this.analyzer.close();
    }
}
