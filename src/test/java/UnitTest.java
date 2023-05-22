import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.stream.Stream;

public class UnitTest {

    final JSONArrayCollector<JSONArray> collector = new JSONArrayCollector<>(JSONArray::new, JSONArray::put);

    final JSONObjectCollector<JSONObject> objectCollector = new JSONObjectCollector<>(JSONObject::new, Try.onTriConsumer(JSONObject::put));

    @Test
    public void jsonCollector() {
        JSONArray jsonArray = Stream.of("a", "b", "c")
                .map(String::toUpperCase)
                .collect(collector.toJSON(s -> s, JSONMapper::concat, json -> json));

        JSONObject jsonObject = Stream.of("a", "a", "a", "c")
                .map(String::toUpperCase)
                .collect(objectCollector.toJSON(s -> s, s -> s, JSONMapper::combine, json -> json));

        System.out.println(jsonArray.toString());
        System.out.println(jsonObject.toString());

        JSONStream.of(jsonArray, Try.onBiFunction(JSONArray::get))
                .forEach(System.out::println);

        JSONStream.of(jsonObject, Try.onBiFunction((json, key) -> json.get(key)))
                .map(JSONObjectEntry::getValue)
                .forEach(System.out::println);
    }
}
