package johny.dotsville.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FileWriter {
    private static final Logger logger = LogManager.getLogger();

    public static void write(Collection<String> data, String filename) throws IOException {
        logger.debug("Записываем в файл результат. Всего {} элементов.", data.size());
        Path path = Paths.get(filename);
        Files.write(path, data, Charset.defaultCharset());
    }
}
