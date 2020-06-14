package com.paytm.insider.dao;

import com.paytm.insider.constants.Constants;
import com.paytm.insider.dto.Story;
import com.paytm.insider.service.NewsService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@SuppressWarnings(value = "unchecked,rawtypes")
public class StoryDao {

    private final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final ZSetOperations<String, Story> zSetOperations;

    @Autowired
    private RedisTemplate redisTemplate;


    public StoryDao(RedisTemplate redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void save(Story story) {
        zSetOperations.add(Constants.STORY, story, DateTime.now().getMillis());
        logger.info(String.format("Story with ID %s saved", story.getId()));
    }

    public Optional<Story> findById(Long storyId) {
        return Objects.requireNonNull(zSetOperations.range(Constants.STORY, 0, -1)).parallelStream().filter(s -> s.getId().equals(storyId)).findFirst();
    }

    public Set<Story> findAll(DateTime startTime, DateTime endTime) {
        return zSetOperations.rangeByScore(Constants.STORY, startTime.getMillis(), endTime.getMillis(), 0, -1);
    }
}
