import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TestTry {
    final static Pattern DATA_PATTERN = Pattern.compile("data:(?<mime>[\\w/\\-\\.]+);(?<properties>(\\w+=\\w+;)*)base64,(?<data>.*)");

    @Test
    public void test1() {
        Matcher m = DATA_PATTERN.matcher("data:image/png;filename=12312;asdasd=asdasqw;base64, iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
        if(m.find()) {
            String contentType = m.group("mime");
            String fileName = Optional.of(m.group("properties"))
                    .map(s -> s.split(";"))
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .filter(s -> !s.isEmpty())
                    .map(s -> {
                        System.out.println(s);
                        String[] split = s.split("=", 2);
                        return Pair.of(split[0], split[1]);
                    })
                    .filter(pair -> "filename".equalsIgnoreCase(pair.getLeft()))
                    .map(Pair::getRight)
                    .findFirst()
                    .orElse(UUID.randomUUID().toString() + "." + contentType.split("/", 2)[1]);
            String base64 = m.group("data");
            System.out.println(String.join(" | ", contentType, fileName, base64));
        } else {
            System.out.println("error");
        }
    }
}
