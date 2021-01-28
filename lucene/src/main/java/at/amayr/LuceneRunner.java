package at.amayr;

import at.amayr.file.ClassPathResolver;
import at.amayr.file.TextFileFilter;
import at.amayr.lucene.Indexer;
import at.amayr.lucene.LuceneConstants;
import at.amayr.lucene.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.net.URISyntaxException;

public class LuceneRunner {
    String indexDir = "idx";
    String dataDir = "data";
    Indexer indexer;
    Searcher searcher;

    public static void main(String[] args) {
        LuceneRunner runner;

        try {
            runner = new LuceneRunner();
            runner.createIndex();
            runner.search("Weiznglasl");
        } catch (IOException | ParseException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void search(String searchQuery) throws IOException, ParseException, URISyntaxException {
        searcher = new Searcher(ClassPathResolver.ofClassPath(indexDir));
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
                " documents found. Time :" + (endTime - startTime));
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
                    + doc.get(LuceneConstants.FILE_PATH));
        }
    }

    private void createIndex() throws IOException, URISyntaxException {
        indexer = new Indexer(ClassPathResolver.ofClassPath(indexDir));
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(ClassPathResolver.ofClassPath(dataDir), new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File indexed, time taken: "
                + (endTime - startTime) + " ms");
    }
}
