package johny.dotsville;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;

import johny.dotsville.utils.*;

@Command(name = "SomeRandomName")
public class App implements Callable<Integer>
{
    @Option(names = { "-p", "--parse"}, description = "Файл с шаблонами регулярного выражения")
    private String[] parse;

    @ArgGroup(exclusive = true)
    private UrlList urlList;

    static class UrlList {
        // arity - чтобы в опцию можно было передавать разом много значений, -u a1 b2 c3 d4
        @Option(names = { "-u", "--url" }, arity = "1..*", description = "Url источник информации")
        private String[] urlManual;
        @Option(names = { "-ul", "--urllist" }, description = "File with urls download content from")
        private String urlFromSource;
    }

    @Option(names = { "-o", "--output" }, description = "Файл для сохранения результата")
    private String outputFile;

    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try {
            process(getRawUrls());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }

    private void process(Stream<String> rawUrls) {

        List<URL> urls = rawUrls
                .map(l -> UrlHelper.urlFromString(l))
                .filter(e -> e.isRight())
                .map(e -> e.getRight())
                .collect(Collectors.toList());

        List<Object> contents = Downloader.download(urls, Downloader.ContentType.HTML).stream()
                .filter(e -> e.isRight())
                .map(e -> e.getRight())
                .collect(Collectors.toList());

        List<String> contentsAsStrings = contents.stream()
                .map(c -> Converter.bytesToStrings(c))
                .flatMap(c -> c.stream())
                .collect(Collectors.toList());

        String regex1 = "\\s*span class=\"sex__names__name\">([А-Яа-я]{2,})</span>";
        String regex2 = "\\s+([А-Яа-я]+)\\s*</a>";
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);

        List<String> names = Parser.parse(contentsAsStrings, pattern1, pattern2)
                .stream()
                .sorted((x, y) -> x.compareTo(y))
                .collect(Collectors.toList());

        // TODO сначала проверить корректность пути и только потом скачивать
        try {
            FileWriter.write(names, outputFile);
        } catch (IOException ex) {

        }
    }

    private Stream<String> getRawUrls() {
        if (urlList.urlManual != null ) {
            return Arrays.stream(urlList.urlManual);
        }
        if (urlList.urlFromSource != null) {
            return getRawUrlsFromFile(urlList.urlFromSource);
        }
        throw new RuntimeException("Не удалось получить список url");
    }

    private Stream<String> getRawUrlsFromFile(String file) {
        Path path = Paths.get(file);
        try {
            return Files.lines(path);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Файл " + file + " не существует или его невозможно прочитать.");
        }
    }
}
