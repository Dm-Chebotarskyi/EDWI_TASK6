import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Created by dima on 11/7/17.
 */
public class MainApp {

    private static String indexDir = "/home/dima/index.lucene";

    public static void main(String[] args) throws IOException, ParseException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory index = null;
        try {
            index = FSDirectory.open(Paths.get(indexDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            String querystr = askForQuery();
            if (querystr.equals("-1")) {
                System.out.println("Bye!");
                exit(0);
            }
            Query q = null;
            q = new QueryParser("title", analyzer).parse(querystr);
            processQuery(index, q);
        }
    }

    private static String askForQuery() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\nPlease enter query to search: ");
        String title = scanner.nextLine();
        return title;
    }

    private static void processQuery(Directory index, Query q) throws IOException {
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title") + "\n" + d.get("path"));
        }
    }

}
