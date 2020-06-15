package com.paytm.insider.service;

import com.paytm.insider.dao.ListOperationsRepository;
import com.paytm.insider.dao.StoryRepository;
import com.paytm.insider.dto.Story;
import com.paytm.insider.util.NewsUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CacheService {

    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    ListOperationsRepository listOperationsRepository;

    @Autowired
    HNApiService hnApiService;

    @Autowired
    NewsUtil newsUtil;

    @Scheduled(fixedRate = 60000)
    private void updateCache(){
        logger.info("Updating cache at: {}", DateTime.now());
        List<Story> stories = getStories();
        if (stories == null || stories.isEmpty()) {
            logger.info("No new stories");
            return;
        }
        insertStories(stories);
    }



    /**
     * Returns Top stories not present in cache
     * @return List of Stories
     */
    private List<Story> getStories() {
        List<Long> ids = hnApiService.getTopStories();
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        if (listOperationsRepository.size() > 0) {
            ids.removeAll(listOperationsRepository.findAll());
            logger.info("Removed {} stories", listOperationsRepository.size());
        }
        List<Story> stories = new ArrayList<>();
        for (Long storyId : ids) {
            Story story = newsUtil.getStory(storyId);
            if (story != null) {
                stories.add(story);
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
                storyRepository.save(story);
            } else {
                listOperationsRepository.insert(story.getId());
                if (listOperationsRepository.size() > 500) {
                    //Storing only 500 elements in the list
                    listOperationsRepository.removeLast();
                }
            }
            i++;
        }
    }

}
