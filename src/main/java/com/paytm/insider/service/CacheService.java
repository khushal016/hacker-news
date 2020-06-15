package com.paytm.insider.service;

import com.paytm.insider.constants.Constants;
import com.paytm.insider.dao.ListOperationsDao;
import com.paytm.insider.dao.StoryDao;
import com.paytm.insider.dto.Story;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StoryDao storyDao;

    @Autowired
    private ListOperationsDao listOperationsDao;

    @Scheduled(fixedRate = 60000)
    private void updateCache(){
        logger.info("Updating cache at: {}", DateTime.now());
        List<Story> stories = getStories();
        if (stories.isEmpty()) {
            logger.info("No new stories");
            return;
        }
        insertStories(stories);
    }

    /**
     * Returns Top 500 story ids from Hacker news
     * @return List of ids
     */
    private List<Long> getTopStories(){
        logger.info("Requesting top 500 stories from hacker news");
        ResponseEntity<List<Long>> response = restTemplate.exchange(Constants.HN_TOP_STORIES_URL, HttpMethod.GET,  null, new ParameterizedTypeReference<List<Long>>() {}, Collections.emptyMap());
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        }
        return new ArrayList<>();
    }

    /**
     * Returns Top stories not present in cache
     * @return List of Stories
     */
    private List<Story> getStories() {
        List<Long> ids = getTopStories();
        if (listOperationsDao.size() > 0) {
            ids.removeAll(listOperationsDao.findAll());
            logger.info("Removed {} stories", listOperationsDao.size());
        }
        List<Story> stories = new ArrayList<>();
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
        return stories;
    }

    /**
     * Insert/update top 10 stories in cache
     * @param stories List of top stories
     */
    private void insertStories(List<Story> stories) {
        stories.sort(Comparator.comparing(Story::getScore).reversed());
        int i = 0;
        for (Story story : stories) {
            if (i < 10) {
                storyDao.save(story);
            } else {
                listOperationsDao.insert(story.getId());
                if (listOperationsDao.size() > 500) {
                    //Storing only 500 elements in the list
                    listOperationsDao.removeLast();
                }
            }
            i++;
        }
    }

}
