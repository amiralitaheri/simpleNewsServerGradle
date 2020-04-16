package server;

import com.blade.Blade;
import lucene.Lucene;


public class Server {
    public static void main(String[] args) {
//        Lucene.createStopWords();
        Blade.of().start(Server.class, args);

    }
}
