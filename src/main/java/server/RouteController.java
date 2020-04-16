package server;

import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Response;
import com.google.gson.Gson;
import lucene.Lucene;
import utils.News;
import utils.UserPass;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


@Path
public class RouteController {
    @PostRoute("/getNews")
    @JSON
    public void getNews(@BodyParam NewsOption no, @Param Integer page, Response response) {
        Lucene lucene = new Lucene();
        ArrayList<News> a;
        if (no.getSearchQuery().trim().equals("")) {
            a = lucene.getAll();
        } else {
            a = lucene.queryDocuments(no.getSearchQuery());
        }

        ArrayList<News> out = new ArrayList<>();

        int i = 0;
        int j = 0;
        boolean add;
        while (j < 50 * (page + 1) && i <= a.size() - 1) {
            add = (no.getCategories().size() == 0);
            for (String cat : no.getCategories()) {
                if (a.get(i).getCategory().equals(cat)) {
                    add = true;
                    break;
                }
            }
            if (add) {
                j++;
                if (j > 50 * page) {
                    out.add(a.get(i));
                }
            }
            i++;
        }
        Comparator<News> title = new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return o1.getHeadline().compareTo(o2.getHeadline());
            }
        };
        Comparator<News> date = new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return -1 * o1.getDate().compareTo(o2.getDate());
            }
        };
        Comparator<News> likes = new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return (o1.getLikes() < (o2.getLikes())) ? 1 : -1;
            }
        };
        Comparator<News> views = new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return (o1.getViews() < (o2.getViews())) ? 1 : -1;
            }
        };

        switch (no.getSortBy()) {
            case "Title":
                out.sort(title);
                break;
            case "Date":
                out.sort(date);
                break;
            case "Likes":
                out.sort(likes);
                break;
            case "Views":
                out.sort(views);
                break;
        }

        Gson gson = new Gson();
        String json = gson.toJson(out);
        System.out.println(a.size());
        System.out.println(out.size());
        response.header("Access-Control-Allow-Origin", "*");
        response.json(json);
    }

    @PostRoute("/addNews")
    @JSON
    public void addNews(@BodyParam News news, @Param String token, Response response) {
        response.header("Access-Control-Allow-Origin", "*");
        if (token.equals("a66abb5684c45962d887564f08346e8d")) {
            Lucene lucene = new Lucene();
            boolean result = lucene.addDocument(news);
            response.json("{\"result\": \"" + result + "\"}");
        } else {
            response.json("{\"result\": \"authentication failed\"}");
        }
    }

    @PostRoute("/deleteNews")
    @JSON
    public void deleteNews(@Param int docId, @Param String token, Response response) {
        response.header("Access-Control-Allow-Origin", "*");
        if (token.equals("a66abb5684c45962d887564f08346e8d")) {
            Lucene lucene = new Lucene();
            boolean result = lucene.deleteDocument(String.valueOf(docId));
            response.json("{\"result\": \"" + result + "\"}");
        } else {
            response.json("{\"result\": \"authentication failed\"}");
        }
    }

    @PostRoute("/updateNews")
    @JSON
    public void updateNews(@BodyParam News news, @Param int docId, @Param String token, Response response) {
        response.header("Access-Control-Allow-Origin", "*");
        if (token.equals("a66abb5684c45962d887564f08346e8d")) {
            Lucene lucene = new Lucene();
            boolean result = lucene.updateDocument(String.valueOf(docId), news);
            response.json("{\"result\": \"" + result + "\"}");
        } else {
            response.json("{\"result\": \"authentication failed\"}");
        }
    }

    @PostRoute("/authenticate")
    @JSON
    public void authenticate(@BodyParam UserPass userPass, Response response) {
        response.header("Access-Control-Allow-Origin", "*");
        if (userPass.getUsername().equals("admin") && userPass.getPassword().equals("123456")) {
            response.json("{\"token\": \"a66abb5684c45962d887564f08346e8d\", \"result\": \"true\"}");
        } else {
            response.json("{\"result\": \"authentication failed\"}");
        }
    }
}
