package johny.dotsville;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;

import johny.dotsville.core.Grabber;
import johny.dotsville.core.GrabberSettings;

@Command(name = "SomeRandomName")
public class App implements Callable<Integer>
{
    @Option(names = { "-p", "--parse"}, arity = "0..1", description = "Файл с шаблонами регулярного выражения")
    private String parsePatternSource;

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
            GrabberSettings settings = new GrabberSettings(getRawUrls(), getRawPatterns(), outputFile);
            new Grabber(settings).grab();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }

    public List<String> getRawPatterns() {
        if (parsePatternSource == null) {
            return null;
        }
        Path path = Paths.get(parsePatternSource);
        try {
            List<String> patterns = Files.lines(path).collect(Collectors.toList());
            return patterns;
        } catch (IOException ex)  {
            // TODO приделать логгер
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private List<String> getRawUrls() {
        if (urlList.urlManual != null ) {
            return List.of(urlList.urlManual);
        }
        if (urlList.urlFromSource != null) {
            return getRawUrlsFromFile(urlList.urlFromSource);
        }
        throw new RuntimeException("Не удалось получить список url");
    }

    private List<String> getRawUrlsFromFile(String file) {
        Path path = Paths.get(file);
        try {
            return Files.lines(path)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Файл " + file + " не существует или его невозможно прочитать.");
        }
    }
}
