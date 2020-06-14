package com.paytm.insider.service;

import com.paytm.insider.constants.Constants;
import com.paytm.insider.dao.StoryDao;
import com.paytm.insider.dto.Story;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@SuppressWarnings(value = "unchecked,rawtypes")
public class CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(fixedRate = 60000)
    private void updateCache(){
        logger.info("Updating cache at: {}", DateTime.now());
        StoryDao storyDao = new StoryDao(redisTemplate);
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();
        Set<Long> ignoredStories = new HashSet<>(Objects.requireNonNull(listOperations.range(Constants.IGNORED, 0, -1)));
        ResponseEntity<List<Long>> response = restTemplate.exchange(Constants.HN_TOP_STORIES_URL, HttpMethod.GET,  null, new ParameterizedTypeReference<List<Long>>() {}, Collections.emptyMap() );
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            List<Long> ids = new ArrayList<>(response.getBody());
            ids.removeAll(ignoredStories);
            logger.info("Removed {} stories", ignoredStories.size());
            List<Story> stories = new ArrayList<>();
            getStories(ids, stories, storyDao);
            if (stories.isEmpty()) {
                logger.info("No new stories");
                return;
            }
            insertStories(stories, storyDao, listOperations);
        }
    }

    private void getStories(List<Long> ids, List<Story> stories, StoryDao storyDao) {
        for (Long storyId : ids) {
            Optional<Story> story = storyDao.findById(storyId);
            if (story.isPresent()) {
                logger.info("Story:{} already present updating time", storyId);
                stories.add(story.get());
                continue;
            }
            logger.info("Request for story:{}", storyId);
            ResponseEntity<Story> storyResponse = restTemplate.exchange(Constants.HN_STORY_URL + storyId + ".json?print=pretty", HttpMethod.GET,  null, Story.class);
            if (storyResponse.getStatusCode().equals(HttpStatus.OK) && storyResponse.getBody() != null) {
                stories.add(storyResponse.getBody());
            }
        }
    }

    private void insertStories(List<Story> stories, StoryDao storyDao, ListOperations<String, Long> listOperations) {
        stories.sort(Comparator.comparing(Story::getScore).reversed());
        int i = 0;
        for (Story story : stories) {
            if (i < 10) {
                storyDao.save(story);
            } else {
                listOperations.leftPush(Constants.IGNORED, story.getId());
                if (Objects.requireNonNull(listOperations.size(Constants.IGNORED)).intValue() > 500) {
                    //Storing only 500 elements in the list
                    listOperations.rightPop(Constants.IGNORED);
                }
            }
            i++;
        }
    }

}
