package net.azib.photos;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.photos.UserFeed;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestRouter implements Filter {
    Picasa picasa = new Picasa();

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getRequestURI();

        if (path == null || "/".equals(path)) {
            render("gallery", picasa.getGallery(), request, response);
        }
        else {
            String[] parts = path.split("/");
            request.setAttribute("photoId", parts.length > 2 ? parts[2] : null);
            render("album", picasa.getAlbum(parts[1]), request, response);
        }
    }

    void render(String template, BaseFeed feed, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("picasa", picasa);
        request.setAttribute(template, feed);

        response.setContentType("text/html; charset=utf8");
        response.addDateHeader("Last-Modified", feed.getUpdated().getValue());
        response.addHeader("ETag", feed.getEtag());

        request.getRequestDispatcher("/WEB-INF/jsp/" + template + ".jsp").include(request, response);
    }

    public void destroy() {
    }
}