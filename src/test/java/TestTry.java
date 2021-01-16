import com.kinnarastudio.commons.Try;
import org.json.JSONObject;
import org.junit.Test;

import java.util.stream.Stream;

public class TestTry {
    @Test
    public void test1() {
        Try.onConsumer(x -> new Integer(1) );
        Stream.of("{}").map(Try.onFunction(JSONObject::new));
    }
}
