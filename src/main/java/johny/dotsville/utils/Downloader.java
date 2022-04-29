package johny.dotsville.utils;

import java.io.*;
import java.net.URLConnection;
import java.util.List;
import java.net.URL;
import java.util.stream.Collectors;

public class Downloader {

    public enum ContentType {
        ANY(null), HTML("text/html");

        private final String tag;

        private ContentType(String tag) {
            this.tag = tag;
        }
    }

    public static Either<Exception, Object> download(URL url, ContentType contentType) {
        Object content = null;
        Exception error = null;
        try {
            URLConnection conn = url.openConnection();
            content = getContentOfType(conn, contentType);
        } catch (Exception ex) {
            error = ex;
        }
        return new Either<Exception, Object>(error, content);
    }

    public static List<Either<Exception, Object>> download(List<URL> urls, ContentType contentType) {
        return urls.stream()
                .map(url -> download(url, contentType))
                .collect(Collectors.toList());
    }

    private static Object getContentOfType(URLConnection conn, ContentType type) throws IOException {
        if (!conn.getContentType().startsWith(type.tag)) {
            throw new IllegalArgumentException("Фактический тип контента не совпадает с ожидаемым.");
        }
        return conn.getContent();
    }
}