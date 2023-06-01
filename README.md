# kinnara-commons

Decluttering JAVA 8 lamda programing and stream API

## Functional Interfaces ###
#### Example
```java
class Yourclass implements com.kinnarastudio.commons.Declutter {
    public yourMethod() {
        Collection<String> myStringCollection = ...
        
        // turns this
        myStringCollection.forEach(s -> {
            try {
                // some code that throws exceptions
            } catch (Exception e) {
            
            }
        });
        
        // to this
        myStringCollection.forEach(Try.onConsumer(s -> {
             // some code that throws exceptions
        });
    }
}
```

### Throwable BiConsumer ####

`Try.onBiConsumer(<BiConsumer Callback>)`

### Throwable BiFunction ####

`Try.onBiFunction(<BiFunction Callback>)`

### Throwable Consumer ####

`Try.onConsumer(<Consumer Callback>)`

### Throwable Function ####

`Try.onFunction(<Function Callback>)`

### Throwable Predicate ####

`Try.onPredicate(<Predicate Callback>)`

### Throwable Supplier ####

`Try.onSupplier(<Supplier Callback>)`

## Stream APIs ###

### JSONStream ####

Create a `java.util.Stream` instance of json :

- `JSONStream.of(<object>, <valueExtractor>)`

- `JSONStream.of(<array>, <valueExtractor>)`

#### Example
```java
    Stream<JSONObjectEntry<String>> jsonObjectStream = JSONStream.of(jsonObject, JSONObject::getString);
    Stream<String> jsonArrayStream = JSONStream.of(jsonArray, JSONArray::getString);
```

## JSON Collectors ####

Collect `java.util.Stream` into json object or json array

```java
    // result = {"a":"A", "b":"B", "c":"C"}
    JSONObject result = Stream.of("a", "b", "c")
        .collect(JSONCollectors.toJSONObject(String::toLowerCase, String::toUpperCase));

    // result = ["a", "b", "c"]
    JSONArray result = Stream.of("a", "b", "c")
        .collect(JSONCollectors.toJSONArray());
```

## JSONMapper ####

### JSONMapper.combine

Combine json object `object1` with `object2` into json object `result`.

`<result> = JSONMapper.combine(<object1>, <object2>)`

### JSONMapper.concat

Concatenate json array `array1` with `array2` into json array `result`.

`<result> = JSONMapper.concat(<array1>, <array2>)`

## Custom Adapters

By using custom adapters, you can implement your own support for other json objects. To implement custom adapter

1. Create adapter class `MyCustomAdapter` which implements either interface `com.kinnarastudio.commons.jsonstream.adapter.ObjectAdapter` or `com.kinnarastudio.commons.jsonstream.adapter.ArrayAdapter`
2. Implements required methods. 
3. Use the adapter on your code
   - `JSONStream.of(myObjectAdapter, jsonObject);`
   - `JSONStream.of(myArrayAdapter, jsonArray);`

## Change Log ##

#### 4.1.0
- Change architecture to use adapter
- Add support for `org.codehaus.jettison.json.*`

#### 3.1.0

- Add implementation of `Try.toNegate` 

#### 3.0.0

- Remove implementation of `org.codehaus.jettison`

#### 2.2.0

- Support for package `org.codehaus.jettison`

#### 2.0.0 ####

- Add `flatten` methods in `JSONStream`