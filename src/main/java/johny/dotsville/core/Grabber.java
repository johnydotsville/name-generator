package johny.dotsville.core;

import johny.dotsville.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Grabber {
    private static final Logger logger = LogManager.getLogger();

    private GrabberSettings settings;

    public Grabber() { }

    public Grabber(GrabberSettings settings) {
        this.settings = settings;
    }

    public void setSettings(GrabberSettings settings) {
        this.settings = settings;
    }

    public void grab() throws Exception {
        if (settings.getParsePatterns() != null) {
//            List<String> result = Timer.<List<String>>runWithTimeTrack(() -> downloadContentAsStrings(), "Скачка контента");
            List<String> result = downloadContentAsStrings();
            result = Parser.parse(result, settings.getParsePatterns())
                    .stream()
                    .sorted((x, y) -> x.compareTo(y))
                    .collect(Collectors.toList());
            try {
                logger.info("Запись результата в файл...");
                FileWriter.write(result, settings.getOutputFile());
                logger.info("Результат успешно записан в файл");
            } catch (IOException ex) {
                logger.warn("Проблемы записи в файл: {}", ex.getMessage());
            }
        }
    }

    private List<String> downloadContentAsStrings() {
        logger.info("Начинаем скачивать содержимое ссылок...");
        List<String> result = new LinkedList<>();
        List<Either<Exception, Object>> contents = Downloader.download(settings.getUrls(), Downloader.ContentType.HTML);
        for (Either<Exception, Object> item : contents) {
            if (item.isRight()) {
                result.addAll((List<String>)item.getRight());
            }
        }
        logger.info("Скачивание содержимого ссылок закончено.");
        return result;
    }
}
