package com.paytm.insider.dao;

import com.paytm.insider.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@SuppressWarnings("rawtypes, unchecked")
public class UserRepositoryImpl implements UserRepository{

    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private static final String KEY = "User";

    private HashOperations<String, String, User> hashOperations;

    private final RedisTemplate redisTemplate;

    @Autowired
    public UserRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(User user) {
        hashOperations.put(KEY, user.getId(), user);
        logger.info(String.format("User with ID %s saved", user.getId()));
    }

    @Override
    public User findById(String userId) {
        return hashOperations.get(KEY, userId);
    }
}
