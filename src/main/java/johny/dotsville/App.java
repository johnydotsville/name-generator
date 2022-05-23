package johny.dotsville;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import johny.dotsville.core.Grabber;
import johny.dotsville.core.GrabberSettings;

@Command(name = "SomeRandomName")
public class App implements Callable<Integer>
{
    private static final Logger logger = LogManager.getLogger();

    @ArgGroup(exclusive = true)
    private ParsingPattern parsingPattern;
    static class ParsingPattern {
        // Почему эти поля видно через parsingPattern, они же приватные?
        @Option(names = { "-p", "--pattern" }, arity = "1..*", description = "Регулярное выражение для разбора строк")
        private String[] patternManual;
        @Option(names = { "-ps", "--patternSource"}, description = "Файл с регулярными выражениями для разбора строк")
        private String patternSource;
    }

    // exclusive - означает, что нельзя использовать одновременно >1 параметра из этой группы
    @ArgGroup(exclusive = true)
    private UrlList urlList;
    static class UrlList {
        // arity - чтобы в опцию можно было передавать разом много значений, -u a1 b2 c3 d4
        @Option(names = { "-u", "--url" }, arity = "1..*", description = "Url источник информации")
        private String[] urlManual;
        @Option(names = { "-ul", "--urlSoource" }, description = "File with urls download content from")
        private String urlFromSource;
    }

    @Option(names = { "-o", "--output" }, description = "Файл для сохранения результата")
    private String outputFile;

    public static void main(String[] args)
    {
        logger.info("Запуск программы");
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    // Тут собственно код-работяга
    @Override
    public Integer call() throws Exception {
        try {
            logger.info("Получение настроек...");
            GrabberSettings settings = new GrabberSettings(getRawUrls(), getRawPatterns(), outputFile);
            logger.info("Получение информации...");
            new Grabber(settings).grab();
        } catch (Exception ex) {
            logger.error("Ошибка выполнения: {}", ex.getMessage());
        }
        return 0;
    }

    public List<String> getRawPatterns() {
        if (parsingPattern.patternManual != null) {
            return Arrays.asList(parsingPattern.patternManual);
        }
        if (parsingPattern.patternSource == null) {
            throw new RuntimeException("Не удалось получить регулярные выражения для разбора строк");
        }
        Path path = Paths.get(parsingPattern.patternSource);
        try {
            List<String> patterns = Files.lines(path).collect(Collectors.toList());
            return patterns;
        } catch (IOException ex)  {
            logger.error("Ошибка получения регулярных выражений из файла. Причина: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private List<String> getRawUrls() {
        if (urlList.urlManual != null ) {
            return Arrays.asList(urlList.urlManual);
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

//    TODO:
//    - Добавить логирование
//    - Добавить тесты
//    - Добавить вывод в консоль сообщений и ходе всех операций
//    - Добавить замер и показ времени каждой операции

}