package johny.dotsville.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileWriter {
    public static void write(Collection<String> data, String filename) throws IOException {
        Path path = Paths.get(filename);
        Files.write(path, data, Charset.defaultCharset());
    }
}
