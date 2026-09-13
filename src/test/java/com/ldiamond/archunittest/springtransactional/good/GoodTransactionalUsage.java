package com.ldiamond.archunittest.springtransactional.good;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodTransactionalUsage {

    @Transactional
    public String saveAuthor(Long id) {
        return new String("Jane Doe");
    }
}
