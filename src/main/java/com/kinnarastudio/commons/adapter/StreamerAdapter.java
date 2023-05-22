package com.kinnarastudio.commons.adapter;

import java.util.Iterator;

public interface StreamerAdapter<JSON, KEY> {
    Iterator<KEY> getKeyIterator(JSON json);

}
