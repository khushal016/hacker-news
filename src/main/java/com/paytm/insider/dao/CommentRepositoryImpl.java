package com.paytm.insider.dao;

import com.paytm.insider.dto.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@SuppressWarnings("rawtypes, unchecked")
public class CommentRepositoryImpl implements CommentRepository{

    private final Logger logger = LoggerFactory.getLogger(CommentRepositoryImpl.class);

    private static final String KEY = "Comment";

    private HashOperations<String, Long, Comment> hashOperations;

    private final RedisTemplate redisTemplate;

    @Autowired
    public CommentRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(Comment comment) {
        hashOperations.put(KEY, comment.getId(), comment);
        logger.info(String.format("Comment with ID %s saved", comment.getId()));
    }

    @Override
    public Comment findById(Long commentId) {
        return hashOperations.get(KEY, commentId);
    }
}
