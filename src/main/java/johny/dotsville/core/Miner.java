package johny.dotsville.core;

import johny.dotsville.core.MinerSettings;
import johny.dotsville.utils.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

public class Miner {
    private MinerSettings settings;

    public Miner() { }

    public Miner(MinerSettings settings) {
        this.settings = settings;
    }

    public void setSettings(MinerSettings settings) {
        this.settings = settings;
    }

    public void mine() {
        List<String> contents = downloadContentAsStrings();
        Collection<String> data = Parser.parse(contents, settings.getParsePatterns())
                .stream()
                .sorted((x, y) -> x.compareTo(y))
                .collect(Collectors.toList());

        // TODO сначала проверить корректность пути и только потом скачивать
        try {
            FileWriter.write(data, settings.getOutputFile());
        } catch (IOException ex) {

        }
    }

    private List<String> downloadContentAsStrings() {
        List<String> result = new LinkedList<>();
        List<Either<Exception, Object>> contents = Downloader.download(settings.getUrls(), Downloader.ContentType.HTML);
        for (Either<Exception, Object> item : contents) {
            if (item.isRight()) {
                result.addAll(Converter.bytesToStrings(item.getRight()));
            } else {
                // TODO приделать логгер
                System.out.println(item.getLeft().getMessage());
            }
        }
        return result;
    }

//    public void mine() {
////        List<URL> urls = settings.getUrls().stream()
////                .map(l -> UrlHelper.urlFromString(l))
////                .filter(e -> e.isRight())
////                .map(e -> e.getRight())
////                .collect(Collectors.toList());
////
////        List<Object> contents = Downloader.download(urls, Downloader.ContentType.HTML).stream()
////                .filter(e -> e.isRight())
////                .map(e -> e.getRight())
////                .collect(Collectors.toList());
////
////        List<String> contentsAsStrings = contents.stream()
////                .map(c -> Converter.bytesToStrings(c))
////                .flatMap(c -> c.stream())
////                .collect(Collectors.toList());
////
////        String regex1 = "\\s*span class=\"sex__names__name\">([А-Яа-я]{2,})</span>";
////        String regex2 = "\\s+([А-Яа-я]+)\\s*</a>";
////        Pattern pattern1 = Pattern.compile(regex1);
////        Pattern pattern2 = Pattern.compile(regex2);
////
////        List<String> names = Parser.parse(contentsAsStrings, { pattern1, pattern2 })
////                .stream()
////                .sorted((x, y) -> x.compareTo(y))
////                .collect(Collectors.toList());
////
////        // TODO сначала проверить корректность пути и только потом скачивать
////        try {
////            FileWriter.write(names, outputFile);
////        } catch (IOException ex) {
////
////        }
//    }
}
