package com.ldiamond.archunittest.springasync.good;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GoodAsyncUsage {

    @Async
    public void doSomethingAsync() {
    }
}
