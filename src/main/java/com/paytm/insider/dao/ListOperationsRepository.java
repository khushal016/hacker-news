package com.paytm.insider.dao;

import java.util.List;

public interface ListOperationsRepository {

    List<Long> findAll();

    void insert(Long id);

    void removeLast();

    int size();
}
