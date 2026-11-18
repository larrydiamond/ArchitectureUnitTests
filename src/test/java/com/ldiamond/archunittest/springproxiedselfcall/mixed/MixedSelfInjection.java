package com.ldiamond.archunittest.springproxiedselfcall.mixed;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MixedSelfInjection {

    private final MixedSelfInjection self;

    public MixedSelfInjection(@Lazy MixedSelfInjection self) {
        this.self = self;
    }

    @Async
    public void sendEmail(Long id) {
    }

    public void viaProxy(Long id) {
        self.sendEmail(id);
    }

    public void viaThis(Long id) {
        // Self-injection exists, but this call still bypasses the proxy
        this.sendEmail(id);
    }
}
