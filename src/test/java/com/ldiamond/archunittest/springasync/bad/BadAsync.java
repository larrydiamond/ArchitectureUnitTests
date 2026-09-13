package com.ldiamond.archunittest.springasync.bad;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BadAsync {

    @Async
    protected void doSomethingAsync() {
    }
}
