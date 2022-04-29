package johny.dotsville.utils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    public static Set<String> parse(List<String> data, Pattern... patterns) {
        Set<String> result = new TreeSet<>();
        for (String string : data) {
            result.addAll(getInfo(string, patterns));
        }
        return result;
    }

    private static Set<String> getInfo(String string, Pattern[] patterns) {
        Set<String> names = new TreeSet<>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                names.add(matcher.group(1));
            }
        }
        return names;
    }
}
