package com.paytm.insider.util;

import com.paytm.insider.dao.CommentRepository;
import com.paytm.insider.dao.StoryRepository;
import com.paytm.insider.dao.UserRepository;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.Story;
import com.paytm.insider.dto.User;
import com.paytm.insider.service.HNApiService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
class NewsUtilTest {

    @Mock
    StoryRepository storyRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    NewsUtil newsUtil;

    @Mock
    HNApiService hnApiService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Story story = new Story();
        story.setId(1L);
        Optional<Story> storyOptional = Optional.of(story);
        Story story2 = new Story();
        story2.setId(2L);
        Mockito.when(storyRepository.findById(1L)).thenReturn(storyOptional).thenReturn(Optional.empty());
        Mockito.when(hnApiService.getStory(2L)).thenReturn(story2).thenReturn(null);
    }

    @Test
    void getStory() {
        Story story = newsUtil.getStory(1L);
        Assert.assertEquals(1L, (long) story.getId());
        Story story2 = newsUtil.getStory(2L);
        Assert.assertEquals(2L, (long) story2.getId());
        Story story3 = newsUtil.getStory(3L);
        Assert.assertNull(story3);
    }

    @Test
    void getComment() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        Mockito.when(commentRepository.findById(1L)).thenReturn(comment1).thenReturn(null);
        Mockito.when(hnApiService.getComment(2L)).thenReturn(comment2).thenReturn(null);
        Comment comment = newsUtil.getComment(1L);
        Assert.assertEquals(1L, comment.getId().longValue());
        comment = newsUtil.getComment(2L);
        Assert.assertEquals(2L, comment.getId().longValue());
        comment = newsUtil.getComment(3L);
        Assert.assertNull(comment);
    }

    @Test
    void getUser() {
        User user1 = new User();
        user1.setId("1");
        user1.setCreated("2019-01-01 12:12");
        User user2 = new User();
        user2.setId("2");
        user2.setCreated("2010-01-01 12:12");
        Mockito.when(userRepository.findById("1")).thenReturn(user1).thenReturn(null);
        Mockito.when(hnApiService.getUser("2")).thenReturn(user2).thenReturn(null);
        User user = newsUtil.getUser("1");
        Assert.assertEquals(1, user.getAge());
        user = newsUtil.getUser("2");
        Assert.assertEquals(10, user.getAge());
        user = newsUtil.getUser("3");
        Assert.assertNull(user);
    }
}