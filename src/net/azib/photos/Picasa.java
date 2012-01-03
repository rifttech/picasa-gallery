package net.azib.photos;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.UserFeed;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Picasa {
    // TODO change username
    String user;
    String analytics;
    PicasawebService service;

    public Picasa() {
        Properties config = loadConfig();
        user = config.getProperty("google.user");
        analytics = config.getProperty("google.analytics");
        service = new PicasawebService(user);
    }

    public String getUser() {
        return user;
    }

    public String getAnalytics() {
        return analytics;
    }

    public UserFeed getGallery() {
        return feed("?kind=album&thumbsize=212c", UserFeed.class);
    }

    public AlbumFeed getAlbum(String name) {
        try {
            return feed("/album/" + name + "?imgmax=1024&thumbsize=144c", AlbumFeed.class);
        }
        catch (RuntimeException e) {
            AlbumFeed results = search(name);
            results.setTitle(new PlainTextConstruct("Photos matching '" + name + "'"));
            return results;
        }
    }

    public AlbumFeed search(String query) {
        return feed("?kind=photo&q=" + query + "&imgmax=1024&thumbsize=144c", AlbumFeed.class);
    }

    private <T extends IFeed> T feed(String url, Class<T> type) {
        try {
            return service.getFeed(new URL("http://picasaweb.google.com/data/feed/api/user/" + user + url), type);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Properties loadConfig() {
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/config.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException("Can't load config.properties");
        }
        return config;
    }
}
