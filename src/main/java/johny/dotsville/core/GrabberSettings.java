package johny.dotsville.core;

import johny.dotsville.utils.Either;
import johny.dotsville.utils.UrlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GrabberSettings {
    private static final Logger logger = LogManager.getLogger();

    private List<URL> urls = new LinkedList<>();
    private List<Pattern> parsePatterns;
    private String outputFile;

    // TODO написать тесты для этого
    public GrabberSettings(List<String> urls, List<String> parsePatterns, String outputFile) {
        List<String> rawUrls = Objects.requireNonNull(urls);
        List<String> rawPatterns = parsePatterns;
        this.outputFile = Objects.requireNonNull(outputFile);

        skipIncorrectUrls(rawUrls);
        if (parsePatterns != null) {
            skipIncorrectPatterns(rawPatterns);
        }
    }

    public List<URL> getUrls() {
//        return List.copyOf(urls);
        return urls;
    }

    public List<Pattern> getParsePatterns() {
//        return parsePatterns == null ? null : List.copyOf(parsePatterns);
        return parsePatterns == null ? null : parsePatterns;
    }

    public String getOutputFile() {
        return outputFile;
    }

    private void skipIncorrectUrls(List<String> urls) {
        logger.info("Обработка списка url...");
        for (String url : urls) {
            Either<Exception, URL> result = UrlHelper.urlFromString(url);
            if (result.isRight()) {
                logger.trace("URL сформирован: {}", result.getRight());
                this.urls.add(result.getRight());
            } else {
                logger.warn("Не удалось создать url из \"{}\". Причина: {}", url, result.getLeft().getMessage());
            }
        }
    }

    private void skipIncorrectPatterns(List<String> rawPatterns) {
        logger.info("Обработка списка регулярных выражений...");
        for (String rawPattern : rawPatterns) {
            try {
                if (parsePatterns == null) {
                    parsePatterns = new LinkedList<>();
                }
                logger.debug("Попытка сформировать паттерн из: {}", rawPattern);
                parsePatterns.add(Pattern.compile(rawPattern));
            } catch (PatternSyntaxException ex) {
                logger.warn("Не удалось создать паттерн из \"{}\". Причина: {}", rawPattern, ex.getMessage());
            }
        }
    }
}
