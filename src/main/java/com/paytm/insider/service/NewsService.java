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

    public StoriesResponseDTO getTopStories() {
        StoryDao storyDao = new StoryDao(redisTemplate);
        List<StoriesResponseDTO.Stories> storiesList = new ArrayList<>();
        DateTime endTime = DateTime.now();
        DateTime startTime = endTime.minusMinutes(10);
        for (Story story : storyDao.findAll(startTime, endTime).stream().sorted(Comparator.comparing(Story::getScore).reversed()).limit(10L).collect(Collectors.toList())) {
            storiesList.add(new StoriesResponseDTO.Stories(story.getTitle(), story.getUrl(), story.getScore(), story.getTime(), story.getUser()));
        }
        if (storiesList.isEmpty()) {
            return new StoriesResponseDTO(false, "No Stories found");
        }
        return new StoriesResponseDTO(storiesList);
    }
}
