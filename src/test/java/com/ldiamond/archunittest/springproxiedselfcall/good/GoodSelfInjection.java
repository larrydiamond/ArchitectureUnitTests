package com.ldiamond.archunittest.springproxiedselfcall.good;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodSelfInjection {

    private final GoodSelfInjection self;

    public GoodSelfInjection(@Lazy GoodSelfInjection self) {
        this.self = self;
    }

    @Async
    public void sendEmail(Long id) {
        // Intentionally empty: the rule only checks who calls this method
    }

    @Transactional
    public void saveAuthor(Long id) {
        // Intentionally empty: the rule only checks who calls this method
    }

    public void sendAllEmails(Long id) {
        // Calling through the self-injected reference goes through the proxy
        self.sendEmail(id);
        self.saveAuthor(id);
    }
}
