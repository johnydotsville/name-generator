package johny.dotsville.utils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.URLConnection;
import java.util.List;
import java.net.URL;
import java.util.stream.Collectors;

public class Downloader {
    private static final Logger logger = LogManager.getLogger();

    public enum ContentType {
        ANY(null), HTML("text/html");

        private final String tag;

        private ContentType(String tag) {
            this.tag = tag;
        }
    }

    public static List<Either<Exception, Object>> download(List<URL> urls, ContentType contentType) {
        return urls.stream()
            .map(url -> download(url, contentType))
            .collect(Collectors.toList());
    }

    private static List<String> getContentOfType(URLConnection urlConnection, ContentType type) throws IOException {
        validateContentType(urlConnection, type);
        BufferedReader buff = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        return buff.lines().collect(Collectors.toList());
    }

    private static void validateContentType(URLConnection urlConnection, ContentType type) {
        if (!urlConnection.getContentType().startsWith(type.tag)) {
            throw new IllegalArgumentException("Фактический тип контента не совпадает с ожидаемым.");
        }
    }

    public static Either<Exception, Object> download(URL url, ContentType contentType) {
        Object content = null;
        Exception error = null;
        try {
            logger.debug("Пытаемся открыть соединение: \"{}\"", url);
            URLConnection conn = url.openConnection();
            logger.debug("Соединение открыто, пытаемся получить содержимое");
            content = getContentOfType(conn, contentType);
            logger.debug("Содержимое получено");
        } catch (Exception ex) {
            logger.warn("Не удалось скачать содержимое url \"{}\". Причина: {}", url, ex.getMessage());
            error = ex;
        }
        return new Either<Exception, Object>(error, content);
    }
}