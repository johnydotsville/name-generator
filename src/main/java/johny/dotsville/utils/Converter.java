package johny.dotsville.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    public static List<String> bytesToStrings(Object content) {
        InputStreamReader stream = new InputStreamReader((InputStream) content);
        BufferedReader buffer = new BufferedReader(stream);

        return buffer.lines().collect(Collectors.toList());
    }
}
