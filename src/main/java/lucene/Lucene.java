package lucene;

import com.google.gson.Gson;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utils.News;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Lucene {
    private static String indexPath = "indexDir";

    public static ArrayList<String> getStopWords() {
        return stopWords;
    }

    private static ArrayList<String> stopWords = new ArrayList<>();

    public static boolean createStopWords() {
        HashMap<String, Integer> wordsMap = new HashMap<>();
        try {
            int docNumber = 0;
            final File folder = new File("D:\\news");
            Scanner scanner;
            List<String> words;
            for (final File fileEntry : folder.listFiles()) {
                scanner = new Scanner(fileEntry);
                docNumber++;
                words = analyze(scanner.nextLine(), new SimpleAnalyzer());
                for (String w : words) {
                    if (wordsMap.containsKey(w)) {
                        wordsMap.put(w, wordsMap.get(w) + 1);
                    } else {
                        wordsMap.put(w, 1);
                    }
                }
            }
            FileWriter myWriter = new FileWriter("stopWords.txt");

            for (String w : wordsMap.keySet()) {
                if (wordsMap.get(w) / (double) docNumber > 0.95) {
                    stopWords.add(w);
                    myWriter.write(w + "\n");
                }
            }
            myWriter.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream("news", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    public static boolean addDocuments() {
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new MyCustomAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir, iwc);
            //IndexReader reader = DirectoryReader.open(dir);

            final File folder = new File("D:\\news");
            Scanner scanner;
            int count = 0;
            for (final File fileEntry : folder.listFiles()) {
                scanner = new Scanner(fileEntry);
                Document doc = new Document();
                String json = scanner.nextLine();
                Gson gson = new Gson();
                News news;
                try {
                    news = gson.fromJson(json, News.class);
                } catch (Exception e) {
                    System.out.println("please fix the file:" + fileEntry.getName());
                    scanner = new Scanner(System.in);
                    scanner.nextLine();
                    System.out.println("continuing");
                    scanner = new Scanner(fileEntry);
                    json = scanner.nextLine();
                    news = gson.fromJson(json, News.class);
                }
                doc.add(new StringField("id", String.valueOf(++count), Field.Store.YES));
                doc.add(new StoredField("likes", news.getLikes()));
                doc.add(new StoredField("views", news.getViews()));
                doc.add(new TextField("headline", news.getHeadline(), Field.Store.YES));
                doc.add(new TextField("category", news.getCategory(), Field.Store.YES));
                doc.add(new TextField("link", news.getLink(), Field.Store.YES));
                doc.add(new TextField("date", news.getDate(), Field.Store.YES));
                doc.add(new TextField("short_description", news.getShort_description(), Field.Store.YES));
                doc.add(new TextField("authors", news.getAuthors(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
            return false;
        }

    }

    public ArrayList<News> getAll() {
        ArrayList<News> findDocs = new ArrayList<>();
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = null;
            try {
                doc = reader.document(i);
            } catch (IOException e) {
                continue;
            }
            findDocs.add(new News(doc.get("id"), doc.get("headline"), doc.get("category"), doc.get("authors"), doc.get("link"), doc.get("short_description"), doc.get("date"), Integer.valueOf(doc.get("views")), Integer.valueOf(doc.get("likes"))));
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return findDocs;
    }

    public ArrayList<News> queryDocuments(String queryLine) {
        ArrayList<News> findDocs = new ArrayList<>();
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

            System.out.println("Number of all docs: " + reader.numDocs());
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new MyCustomAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"headline", "short_description"}, analyzer);
            Query query = parser.parse(queryLine);

            TopDocs results = searcher.search(query, 2000000);
            ScoreDoc[] hits = results.scoreDocs;
            long numTotalHits = results.totalHits.value;
            System.out.println("Number of Hits: " + numTotalHits);
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                News n = new News(doc.get("id"), doc.get("headline"), doc.get("category"), doc.get("authors"), doc.get("link"), doc.get("short_description"), doc.get("date"), Integer.valueOf(doc.get("views")), Integer.valueOf(doc.get("likes")));
                findDocs.add(n);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        } catch (org.apache.lucene.queryparser.classic.ParseException ex) {
            Logger.getLogger(Lucene.class.getName()).log(Level.SEVERE, null, ex);
        }
        return findDocs;
    }

    public boolean updateDocument(String docID, News news) {
        try {
            this.deleteDocument(docID);
            this.addDocument(news);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteDocument(String docID) {
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new MyCustomAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir, iwc);
            Term term = new Term("id", docID);
            writer.deleteDocuments(term);
            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean addDocument(News news) {
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexReader reader = DirectoryReader.open(dir);
            Document doc = new Document();
            Analyzer analyzer = new MyCustomAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir, iwc);
            doc.add(new StringField("id", String.valueOf(reader.maxDoc()), Field.Store.YES));
            doc.add(new StoredField("likes", news.getLikes()));
            doc.add(new StoredField("views", news.getViews()));
            doc.add(new TextField("headline", news.getHeadline(), Field.Store.YES));
            doc.add(new TextField("category", news.getCategory(), Field.Store.YES));
            doc.add(new TextField("link", news.getLink(), Field.Store.YES));
            doc.add(new TextField("date", news.getDate(), Field.Store.YES));
            doc.add(new TextField("short_description", news.getShort_description(), Field.Store.YES));
            doc.add(new TextField("authors", news.getAuthors(), Field.Store.YES));
            writer.addDocument(doc);

            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
