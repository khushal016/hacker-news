package com.paytm.insider.service;

import com.paytm.insider.constants.Constants;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.Story;
import com.paytm.insider.dto.User;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class HNApiServiceTest {

    @InjectMocks
    HNApiService hnApiService;

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        //define the entity you want the exchange to return
        ResponseEntity<List<Long>> myEntity = new ResponseEntity<>(ids, HttpStatus.OK);
        ResponseEntity<List<Long>> myEntity2 = new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        Mockito.when(restTemplate.exchange(Constants.HN_TOP_STORIES_URL, HttpMethod.GET,  null, new ParameterizedTypeReference<List<Long>>() {}, Collections.emptyMap())
        ).thenReturn(myEntity).thenReturn(myEntity2);
        Story story = new Story(1L, "2020-02-02 00:00", 10);
        ResponseEntity<Story> responseEntity = new ResponseEntity<>(story, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(Constants.HN_STORY_URL + "1.json?print=pretty", HttpMethod.GET,  null, Story.class)).thenReturn(responseEntity).thenReturn(new ResponseEntity<>(HttpStatus.BAD_GATEWAY));
        Comment comment = new Comment();
        comment.setId(2L);
        ResponseEntity<Comment> responseEntity2 = new ResponseEntity<>(comment, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(Constants.HN_STORY_URL + "2.json?print=pretty", HttpMethod.GET,  null, Comment.class)).thenReturn(responseEntity2).thenReturn(new ResponseEntity<>(HttpStatus.BAD_GATEWAY));
        User user = new User();
        user.setId("1");
        ResponseEntity<User> userResponseEntity = new ResponseEntity<>(user, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(Constants.HN_USER_URL + "1.json?print=pretty", HttpMethod.GET,  null, User.class)).thenReturn(userResponseEntity).thenReturn(new ResponseEntity<>(HttpStatus.BAD_GATEWAY));
    }

    @Test
    void getTopStories() {
        List<Long> ids = hnApiService.getTopStories();
        Assert.assertEquals(3, ids.size());
        List<Long> ids2 = hnApiService.getTopStories();
        Assert.assertNull(ids2);
    }

    @Test
    void getStory() {
        Story story = hnApiService.getStory(1L);
        Assert.assertEquals(1L, story.getId().longValue());
        story = hnApiService.getStory(1L);
        Assert.assertNull(story);
    }

    @Test
    void getComment() {
        Comment comment = hnApiService.getComment(2L);
        Assert.assertEquals(2L, comment.getId().longValue());
        comment = hnApiService.getComment(2L);
        Assert.assertNull(comment);
    }

    @Test
    void getUser() {
        User user = hnApiService.getUser("1");
        Assert.assertEquals("1", user.getId());
        user = hnApiService.getUser("1");
        Assert.assertNull(user);
    }
}