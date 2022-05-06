package johny.dotsville.core;

import johny.dotsville.utils.Either;
import johny.dotsville.utils.UrlHelper;

import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MinerSettings {
    private List<URL> urls = new LinkedList<>();
    private List<Pattern> parsePatterns = new LinkedList<>();
    private String outputFile;

    // TODO написать тесты для этого
    public MinerSettings(List<String> urls, List<String> parsePatterns, String outputFile) {
        List<String> rawUrls = Objects.requireNonNull(urls);
        List<String> rawPatterns = parsePatterns;
        this.outputFile = Objects.requireNonNull(outputFile);

        skipIncorrectUrls(rawUrls);
        skipIncorrectPatterns(rawPatterns);
    }

    public List<URL> getUrls() {
        return List.copyOf(urls);
    }

    public List<Pattern> getParsePatterns() {
        return List.copyOf(parsePatterns);
    }

    public String getOutputFile() {
        return outputFile;
    }

    private void skipIncorrectUrls(List<String> urls) {
        for (String url : urls) {
            Either<Exception, URL> result = UrlHelper.urlFromString(url);
            if (result.isRight()) {
                this.urls.add(result.getRight());
            } else {
                // TODO приделать логгер
                System.out.println(result.getLeft().getMessage());
            }
        }
    }

    private void skipIncorrectPatterns(List<String> rawPatterns) {
        for (String rawPattern : rawPatterns) {
            try {
                parsePatterns.add(Pattern.compile(rawPattern));
            } catch (PatternSyntaxException ex) {
                // TODO приделать логгер
                System.out.println("Кривой паттерн: " + rawPattern);
            }
        }
    }
}
