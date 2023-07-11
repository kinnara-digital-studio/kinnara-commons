import com.kinnarastudio.commons.jsonstream.JSONCollectors;
import com.kinnarastudio.commons.jsonstream.JSONStream;
import com.kinnarastudio.commons.jsonstream.model.JSONObjectEntry;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UnitTest {
    @Test
    public void jsonArray() {
        org.json.JSONArray array = new org.json.JSONArray();
        array.put("EEEE");
        array.put("AAAA");
        array.put("DDDD");
        array.put("BBBB");
        array.put("CCCC");

        List<String> list = JSONStream.of(array, org.json.JSONArray::getString)
                .sorted(Comparator.comparing(s -> s))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        org.json.JSONObject json = JSONStream.of(array, org.json.JSONArray::getString)
                .sorted()
                .collect(JSONCollectors.toJSONObject(String::toLowerCase, String::toUpperCase));

        JSONStream.of(json, org.json.JSONObject::getString)
                .filter(e -> e.getKey().startsWith("a"))
                .map(JSONObjectEntry::getValue)
                .collect(JSONCollectors.toJSONArray());
    }

    @Test
    public void jsonObject() {
        final String[] input = new String[] {"a", "a", "a", "c"};

        org.json.JSONObject jsonObject = Arrays.stream(input)
                .collect(JSONCollectors.toJSONObject(String::toUpperCase, String::toLowerCase));

        Assert.assertTrue(jsonObject.has("A"));
        Assert.assertFalse(jsonObject.has("a"));
        Assert.assertTrue(jsonObject.has("C"));
        Assert.assertFalse(jsonObject.has("c"));
        Assert.assertEquals("a", jsonObject.getString("A"));
        Assert.assertEquals("c", jsonObject.getString("C"));

        Collection<String> collection = JSONStream.of(jsonObject, org.json.JSONObject::getString)
                .map(JSONObjectEntry::getKey)
                .collect(Collectors.toSet());

        Assert.assertEquals(collection.size(), 2);
    }
}
