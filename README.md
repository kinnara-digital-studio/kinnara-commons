# Declutter

### Description ###
Decluttering JAVA 8 lamda programing and stream API


### Example ###
```
class Yourclass implements com.kinnarastudio.declutter.Declutter {
    public yourMethod() {
        Collection<String> myStringCollection = ...
        
        myStringCollection.forEach(throwableConsumer(s -> {
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
