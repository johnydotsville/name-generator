package johny.dotsville;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import johny.dotsville.utils.*;

public class App
{
    public static void main(String[] args)
    {
        String baseUrl = "https://peoplenames.ru/male/";
        List<String> letters = Arrays.asList(
                "a", "b", "v", "g", "d",
                "e", "zh", "z", "i",
                "k", "l", "m", "n", "o",
                "p", "r", "s", "t", "u",
                "f", "h", "c", "je", "ya");

        List<URL> urls = letters.stream()
                .map(l -> UrlHelper.urlFromString(baseUrl + l))
                .filter(e -> e.isRight())
                .map(e -> e.getRight())
                .collect(Collectors.toList());

        List<Object> contents = Downloader.download(urls, Downloader.ContentType.HTML).stream()
                .filter(e -> e.isRight())
                .map(e -> e.getRight())
                .collect(Collectors.toList());

        String regex1 = "\\s*span class=\"sex__names__name\">([А-Яа-я]{2,})</span>";
        String regex2 = "\\s+([А-Яа-я]+)\\s*</a>";
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);

        List<String> contentsAsStrings = contents.stream()
                .map(c -> Converter.bytesToStrings(c))
                .flatMap(c -> c.stream())
                .collect(Collectors.toList());

        Set<String> names = Parser.parse(contentsAsStrings, pattern1, pattern2);

        names.forEach(System.out::println);
    }
}
