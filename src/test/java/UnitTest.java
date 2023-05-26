import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.JSONCollectors;
import com.kinnarastudio.commons.jsonstream.JSONMapper;
import com.kinnarastudio.commons.jsonstream.JSONObjectEntry;
import com.kinnarastudio.commons.jsonstream.JSONStream;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JettisonArrayAdapter;
import com.kinnarastudio.commons.jsonstream.adapter.impl.JettisonObjectAdapter;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitTest {
    @Test
    public void jsonArray() {
        JSONArray jsonArrayString = Stream.of("a", "b", "c")
                .map(String::toUpperCase)
                .collect(JSONCollectors.toJSONArray(JSONArray::new));

        Assert.assertEquals(jsonArrayString.length(), 3);

        JSONArray jsonArrayInteger = Stream.of(1, 2, 3)
                .collect(JSONCollectors.toJSONArray(JSONArray::new));

        Assert.assertEquals(jsonArrayInteger.length(), 3);

        JSONArray combined = JSONMapper.concat(jsonArrayString, jsonArrayInteger);
        Assert.assertEquals(6, combined.length());

        JSONStream.of(combined, JSONArray::get).forEach(System.out::println);

        Stream<String> stream1 = JSONStream.of(jsonArrayString, JSONArray::getString);
        Stream<String> stream2 = JSONStream.of(jsonArrayInteger, JSONArray::getInt)
                .map(String::valueOf);

//        long count = Stream.concat(stream1, stream2).count();
//        Assert.assertEquals(6, count);

        Collection<String> collection = Stream.concat(stream1, stream2).collect(Collectors.toSet());
        Assert.assertEquals(6, collection.size());
    }

    @Test
    public void jsonObject() {
        final String[] input = new String[] {"a", "a", "a", "c"};

        JSONObject jsonObject = Arrays.stream(input)
                .collect(JSONCollectors.toJSONObject(String::toUpperCase, String::toLowerCase));

        Assert.assertTrue(jsonObject.has("A"));
        Assert.assertFalse(jsonObject.has("a"));
        Assert.assertTrue(jsonObject.has("C"));
        Assert.assertFalse(jsonObject.has("c"));
        Assert.assertEquals("a", jsonObject.getString("A"));
        Assert.assertEquals("c", jsonObject.getString("C"));

        Collection<String> collection = JSONStream.of(jsonObject, JSONObject::getString)
                .map(JSONObjectEntry::getKey)
                .collect(Collectors.toSet());

        Assert.assertEquals(collection.size(), 2);
    }

    @Test
    public void jettison() {
        final String[] input = new String[] {"a", "a", "a", "c"};
        JettisonObjectAdapter<String> objectAdapter = new JettisonObjectAdapter<>();

        JettisonArrayAdapter<String> arrayAdapter = new JettisonArrayAdapter<>();

        org.codehaus.jettison.json.JSONObject jsonObject = Arrays.stream(input).collect(JSONCollectors.toJson(objectAdapter, String::toLowerCase, String::toUpperCase));
        JSONStream.of(jsonObject, Try.onBiFunction(org.codehaus.jettison.json.JSONObject::optString)).forEach(System.out::println);

        org.codehaus.jettison.json.JSONArray json = Arrays.stream(input).collect(JSONCollectors.toJson(new JettisonArrayAdapter<>()));

        Assert.assertEquals(4, json.length());

        JSONStream.of(json, Try.onBiFunction(org.codehaus.jettison.json.JSONArray::getString)).forEach(System.out::println);
    }
}
