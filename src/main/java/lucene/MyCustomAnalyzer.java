package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.ClassicFilter;


public class MyCustomAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        CharArraySet stopWords = new CharArraySet(Lucene.getStopWords(), true);
        StandardTokenizer src = new StandardTokenizer();
        TokenStream result = new ClassicFilter(src);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopWords);
        return new TokenStreamComponents(src, result);
    }
}