package com.paytm.insider.service;

import com.paytm.insider.constants.Constants;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.Story;
import com.paytm.insider.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HNApiService {

    private final Logger logger = LoggerFactory.getLogger(HNApiService.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * Returns Top 500 story ids from Hacker news
     * @return List of ids
     */
    public List<Long> getTopStories(){
        logger.info("Requesting top 500 stories from hacker news");
        ResponseEntity<List<Long>> response = restTemplate.exchange(Constants.HN_TOP_STORIES_URL, HttpMethod.GET,  null, new ParameterizedTypeReference<List<Long>>() {}, Collections.emptyMap());
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        }
        logger.info("No top stories found");
        return null;
    }

    /**
     * Get story from Hacker news
     * @param storyId story id
     * @return Story
     */
    public Story getStory(Long storyId) {
        logger.info("Request for story:{}", storyId);
        ResponseEntity<Story> response = restTemplate.exchange(Constants.HN_STORY_URL + storyId + ".json?print=pretty", HttpMethod.GET,  null, Story.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        }
        logger.info("Story not found with id:{}", storyId);
        return null;
    }

    /**
     * Get comment from Hacker news
     * @param commentId comment id
     * @return Comment
     */
    public Comment getComment(Long commentId) {
        logger.info("Request for comment:{}", commentId);
        ResponseEntity<Comment> response = restTemplate.exchange(Constants.HN_STORY_URL + commentId + ".json?print=pretty", HttpMethod.GET,  null, Comment.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        }
        logger.info("Comment not found with id:{}", commentId);
        return null;
    }

    public User getUser(String userId) {
        logger.info("Request for user:{}", userId);
        ResponseEntity<User> response = restTemplate.exchange(Constants.HN_USER_URL + userId + ".json?print=pretty", HttpMethod.GET,  null, User.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        }
        logger.info("User not found with id:{}", userId);
        return null;
    }
}
