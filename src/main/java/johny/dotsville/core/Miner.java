package johny.dotsville.core;

import johny.dotsville.utils.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
        List<String> result = downloadContentAsStrings();
        if (settings.getParsePatterns() != null) {
            result = Parser.parse(result, settings.getParsePatterns())
                    .stream()
                    .sorted((x, y) -> x.compareTo(y))
                    .collect(Collectors.toList());
        }
        try {
            FileWriter.write(result, settings.getOutputFile());
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
}
