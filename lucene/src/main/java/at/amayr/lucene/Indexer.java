package at.amayr.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class Indexer {
    private IndexWriter writer;

    public Indexer(Path indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(indexDirectoryPath);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // recreate index each time
        writer = new IndexWriter(indexDirectory, iwc);
    }

    public void close() throws IOException {
        writer.close();
    }

    public int createIndex(Path dataDirPath, FileFilter filter) throws IOException {
        File[] files = dataDirPath.toFile().listFiles();

        for (File file : Objects.requireNonNull(files)) {
            if (shouldHandleFile(file, filter)) {
                indexFile(file);
            }
        }
        return writer.numRamDocs();
    }

    private boolean shouldHandleFile(File file, FileFilter filter) {
        return !file.isDirectory()
                && !file.isHidden()
                && file.exists()
                && file.canRead()
                && filter.accept(file);
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        TextField contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
        TextField fileNameField = new TextField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);
        TextField filePathField = new TextField(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }
}
