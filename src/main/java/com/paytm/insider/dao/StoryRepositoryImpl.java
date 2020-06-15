package com.paytm.insider.dao;

import com.paytm.insider.dto.Story;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@SuppressWarnings("rawtypes, unchecked")
public class StoryRepositoryImpl implements StoryRepository{

    private final Logger logger = LoggerFactory.getLogger(StoryRepositoryImpl.class);

    private static final String KEY = "Story";

    private ZSetOperations<String, Story> zSetOperations;

    private final RedisTemplate redisTemplate;

    @Autowired
    public StoryRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    public void save(Story story) {
        zSetOperations.add(KEY, story, DateTime.now().getMillis());
        logger.info(String.format("Story with ID %s saved", story.getId()));
    }

    public Optional<Story> findById(Long storyId) {
        return Objects.requireNonNull(zSetOperations.range(KEY, 0, -1)).parallelStream().filter(s -> s.getId().equals(storyId)).findFirst();
    }

    public Set<Story> findByTime(DateTime startTime, DateTime endTime) {
        return zSetOperations.rangeByScore(KEY, startTime.getMillis(), endTime.getMillis(), 0, -1);
    }

    public Set<Story> findAll() {
        return zSetOperations.range(KEY, 0, -1);
    }
}
