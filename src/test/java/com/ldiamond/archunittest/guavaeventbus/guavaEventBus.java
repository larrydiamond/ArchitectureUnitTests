package com.ldiamond.archunittest.guavaeventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class guavaEventBus {
    public void createEventBus() {
        // Guava EventBus does not have a direct equivalent to the CacheLoader in Guava Cache.
        // Instead, we can create an EventBus instance and register a listener.
        EventBus eventBus = new EventBus("myEventBus");

        // Example listener
        class MyListener {
            @Subscribe
            public void handleEvent(String event) {
            }
        }

        // Register the listener
        eventBus.register(new MyListener());

        // Post an event
        eventBus.post("Hello, EventBus!");
    }
}
