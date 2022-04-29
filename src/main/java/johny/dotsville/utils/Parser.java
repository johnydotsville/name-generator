package johny.dotsville.utils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Parser {
    public static Set<String> parse(List<String> data, Pattern... patterns) {
        return data.stream()
                .map(str -> extractData(str, patterns))
                .flatMap(e -> e.stream())
                .collect(Collectors.toSet());
    }

    // TODO: поскольку здесь выбирается фиксированная группа (1), метод не универсален
    private static Set<String> extractData(String string, Pattern[] patterns) {
        Set<String> data = new TreeSet<>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                data.add(matcher.group(1));
            }
        }
        return data;
    }
}
