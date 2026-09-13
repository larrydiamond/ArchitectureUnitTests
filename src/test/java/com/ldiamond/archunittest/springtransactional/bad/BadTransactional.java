package com.ldiamond.archunittest.springtransactional.bad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BadTransactional {

    @Transactional
    public String saveAuthor(Long id) {
        return new String("Jane Doe");
    }

    public String saveAnotherAuthor(Long id) {
        // This internal call will bypass the transactional proxy
        return saveAuthor(id);
    }
}
