package johny.dotsville.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Parser {
    private static final Logger logger = LogManager.getLogger();

    public static Set<String> parse(List<String> data, List<Pattern> patterns) {
        return data.stream()
                .map(str -> extractData(str, patterns))
                .flatMap(e -> e.stream())
                .collect(Collectors.toSet());
    }

    // TODO: поскольку здесь выбирается фиксированная группа (1), метод не универсален
    private static Set<String> extractData(String string, List<Pattern> patterns) {
        logger.trace("Извлекаем данные из строки: {}", string);
        Set<String> data = new TreeSet<>();
        for (Pattern pattern : patterns) {
            logger.trace("Применяем шаблон {}", pattern);
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                String match = matcher.group(1);
                logger.debug("Обнаружено совпадение: {}", match);
                data.add(match);
            }
        }
        return data;
    }
}
