package com.paytm.insider.service;

import com.paytm.insider.dao.StoryDao;
import com.paytm.insider.dto.StoriesResponseDTO;
import com.paytm.insider.dto.Story;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings(value = "rawtypes")
public class NewsService {

    private final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StoryDao storyDao;

    /**
     * Returns Top 10 Hacker News Stories in last 10 mins
     * @return List of top 10 stories
     */
    public StoriesResponseDTO getTopStories() {
        List<StoriesResponseDTO.Stories> storiesList = new ArrayList<>();
        DateTime endTime = DateTime.now();
        DateTime startTime = endTime.minusMinutes(10);
        for (Story story : storyDao.findByTime(startTime, endTime).stream().sorted(Comparator.comparing(Story::getScore).reversed()).limit(10L).collect(Collectors.toList())) {
            storiesList.add(new StoriesResponseDTO.Stories(story.getTitle(), story.getUrl(), story.getScore(), story.getTime(), story.getUser()));
        }
        if (storiesList.isEmpty()) {
            return new StoriesResponseDTO(true, "No Stories found");
        }
        return new StoriesResponseDTO(storiesList);
    }

    /**
     * Returns all the past top stories that were served previously
     * @return List of past top stories
     */
    public StoriesResponseDTO getPastStories() {
        List<StoriesResponseDTO.Stories> storiesList = new ArrayList<>();
        for (Story story : storyDao.findAll()) {
            storiesList.add(new StoriesResponseDTO.Stories(story.getTitle(), story.getUrl(), story.getScore(), story.getTime(), story.getUser()));
        }
        if (storiesList.isEmpty()) {
            return new StoriesResponseDTO(true, "No Stories found");
        }
        storiesList.sort(Comparator.comparing(StoriesResponseDTO.Stories::getScore).reversed());
        return new StoriesResponseDTO(storiesList);
    }
}
