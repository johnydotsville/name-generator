package johny.dotsville.utils;

import java.net.MalformedURLException;
import java.net.URL;

public final class UrlHelper {
    public static Either<Exception, URL> urlFromString(String str) {
        URL url = null;
        Exception error = null;
        try {
            url = new URL(str);
        } catch (MalformedURLException ex) {
            error = ex;
        }
        return new Either<>(error, url);
    }
}
