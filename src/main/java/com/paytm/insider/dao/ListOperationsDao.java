package com.paytm.insider.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Repository
@SuppressWarnings("rawtypes, unchecked")
public class ListOperationsDao {

    private static final String KEY = "ignored";

    private ListOperations<String, Long> listOperations;

    private final RedisTemplate redisTemplate;

    @Autowired
    public ListOperationsDao(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        listOperations = redisTemplate.opsForList();
    }

    public List<Long> findAll() {
        return listOperations.range(KEY, 0, -1);
    }

    public void insert(Long id) {
        listOperations.leftPush(KEY, id);
    }

    public void removeLast(){
        listOperations.rightPop(KEY);
    }

    public int size() {
        return Objects.requireNonNull(listOperations.size(KEY)).intValue();
    }
}
