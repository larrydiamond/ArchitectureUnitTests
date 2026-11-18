package com.ldiamond.archunittest.springproxiedselfcall.bad;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BadAsyncSelfCall {

    @Async
    public void sendEmail(Long id) {
    }

    public void sendAllEmails(Long id) {
        // This internal call will bypass the async proxy and run synchronously
        sendEmail(id);
    }
}
