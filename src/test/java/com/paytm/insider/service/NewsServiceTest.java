package com.paytm.insider.service;

import com.paytm.insider.dao.StoryRepository;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.CommentsResponseDTO;
import com.paytm.insider.dto.StoriesResponseDTO;
import com.paytm.insider.dto.Story;
import com.paytm.insider.util.NewsUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class NewsServiceTest {

    @Mock
    StoryRepository storyRepository;

    @InjectMocks
    NewsService newsService;

    @Mock
    NewsUtil newsUtil;

    @BeforeEach
    void setUp() {
        Set<Story> stories = new HashSet<Story>(){{
            add(new Story(1L, "2020-06-15 10:00", 10));
            add(new Story(2L, "2020-06-15 10:01", 20));
            add(new Story(3L, "2020-06-15 10:02", 30));
            add(new Story(4L, "2020-06-15 10:02", 40));
            add(new Story(5L, "2020-06-15 10:02", 50));
            add(new Story(6L, "2020-06-15 10:02", 60));
            add(new Story(7L, "2020-06-15 10:02", 70));
            add(new Story(8L, "2020-06-15 10:02", 80));
            add(new Story(9L, "2020-06-15 10:02", 90));
            add(new Story(10L, "2020-06-15 10:02", 100));
            add(new Story(11L, "2020-06-15 10:02", 110));
            add(new Story(12L, "2020-06-15 10:02", 120));
        }};
        Mockito.when(storyRepository.findByTime(Mockito.any(), Mockito.any())).thenReturn(stories).thenReturn(new HashSet<>());
        Mockito.when(storyRepository.findAll()).thenReturn(stories).thenReturn(new HashSet<>());
    }

    @Test
    void getTopStories() {
        StoriesResponseDTO storiesResponseDTO = newsService.getTopStories();
        Assert.assertEquals(10, storiesResponseDTO.getData().size());
        Assert.assertEquals(120L, storiesResponseDTO.getData().get(0).getScore().longValue());
        storiesResponseDTO = newsService.getTopStories();
        Assert.assertNull(storiesResponseDTO.getData());
        Assert.assertEquals("No Stories found", storiesResponseDTO.getMessage());
    }

    @Test
    void getPastStories() {
        StoriesResponseDTO storiesResponseDTO = newsService.getPastStories();
        Assert.assertEquals(12, storiesResponseDTO.getData().size());
        Assert.assertEquals(120L, storiesResponseDTO.getData().get(0).getScore().longValue());
        storiesResponseDTO = newsService.getPastStories();
        Assert.assertNull(storiesResponseDTO.getData());
        Assert.assertEquals("No Stories found", storiesResponseDTO.getMessage());
    }

    @Test
    void getComments() {
        Story story = new Story(1L, "2020-06-15 10:00", 10);
        story.setComments(new ArrayList<Long>(){{add(1L);add(2L);}});
        Story story2 = new Story(2L, "2020-06-15 10:00", 10);
        Mockito.when(newsUtil.getStory(1L)).thenReturn(story).thenReturn(null);
        Mockito.when(newsUtil.getStory(2L)).thenReturn(story2).thenReturn(null);
        CommentsResponseDTO commentsResponseDTO = newsService.getComments(3L);
        Assert.assertEquals("Invalid story id", commentsResponseDTO.getMessage());
        Assert.assertNull(commentsResponseDTO.getData());
        Assert.assertFalse(commentsResponseDTO.isSuccess());
        commentsResponseDTO = newsService.getComments(2L);
        Assert.assertEquals("No comments found", commentsResponseDTO.getMessage());
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setBy("test1");
        comment1.setComments(new ArrayList<Long>(){{add(1L);}});
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setBy("test2");
        comment2.setComments(new ArrayList<Long>(){{add(1L);add(2L);}});
        Mockito.when(newsUtil.getComment(1L)).thenReturn(comment1).thenReturn(null);
        Mockito.when(newsUtil.getComment(2L)).thenReturn(comment2).thenReturn(null);
        commentsResponseDTO = newsService.getComments(1L);
        Assert.assertEquals(2, commentsResponseDTO.getData().size());
        Assert.assertEquals("test2", commentsResponseDTO.getData().get(0).getUser());
    }
}