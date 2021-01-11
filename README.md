# Declutter

### Description ###
Decluttering JAVA 8 lamda programing and stream API


### Example ###
```
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
        myStringCollection.forEach(tryConsumer(s -> {
             // some code that throws exceptions
        });
    }
}
```

### Functional Interfaces ###

#### Throwable BiConsumer ####

#### Throwable BiFunction ####

#### Throwable Consumer ####

#### Throwable Function ####

#### Throwable Predicate ####

#### Throwable Supplier ####
