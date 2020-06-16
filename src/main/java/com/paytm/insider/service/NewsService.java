package com.paytm.insider.service;

import com.paytm.insider.dao.StoryRepository;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.CommentsResponseDTO;
import com.paytm.insider.dto.StoriesResponseDTO;
import com.paytm.insider.dto.Story;
import com.paytm.insider.util.NewsUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    NewsUtil newsUtil;

    /**
     * Returns Top 10 Hacker News Stories in last 10 mins
     * @return List of top 10 stories
     */
    public StoriesResponseDTO getTopStories() {
        List<StoriesResponseDTO.Stories> storiesList = new ArrayList<>();
        DateTime endTime = DateTime.now();
        DateTime startTime = endTime.minusMinutes(10);
        for (Story story : storyRepository.findByTime(startTime, endTime).stream().sorted(Comparator.comparing(Story::getScore).reversed()).limit(10L).collect(Collectors.toList())) {
            storiesList.add(new StoriesResponseDTO.Stories(story.getId(), story.getTitle(), story.getUrl(), story.getScore(), story.getTime(), story.getUser()));
        }
        if (storiesList.isEmpty()) {
            logger.info("No Stories found in last 10 mins");
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
        for (Story story : storyRepository.findAll()) {
            storiesList.add(new StoriesResponseDTO.Stories(story.getId(), story.getTitle(), story.getUrl(), story.getScore(), story.getTime(), story.getUser()));
        }
        if (storiesList.isEmpty()) {
            logger.info("No Stories found in cache");
            return new StoriesResponseDTO(true, "No Stories found");
        }
        storiesList.sort(Comparator.comparing(StoriesResponseDTO.Stories::getScore).reversed());
        return new StoriesResponseDTO(storiesList);
    }

    /**
     * Return top 10 parent comments for a story
     * @param storyId  story id
     * @return list of comments
     */
    public CommentsResponseDTO getComments(Long storyId) {
        Story story = newsUtil.getStory(storyId);
        if (story == null) {
            logger.info("Story:{} not found", storyId);
            return new CommentsResponseDTO(false, "Invalid story id");
        }
        List<Comment> comments = new ArrayList<>();
        if (story.getComments() != null && !story.getComments().isEmpty()) {
            for (Long commentId : story.getComments()) {
                if (comments.size() <= 10) {
                    Comment comment = newsUtil.getComment(commentId);
                    if (comment != null) {
                        comments.add(comment);
                    }
                } else {
                    break;
                }
            }
        }
        if (comments.isEmpty()) {
            logger.info("Comments not found for story:{}", storyId);
            return new CommentsResponseDTO(true, "No comments found");
        }
        comments.sort(commentComparator);
        List<CommentsResponseDTO.Comment> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            int age = comment.getUser() != null ? comment.getUser().getAge() : 0;
            commentList.add(new CommentsResponseDTO.Comment(comment.getText(), comment.getBy(), age));
        }
        return new CommentsResponseDTO(commentList);
    }

    //Sort comments by total number of child comments
    Comparator<Comment> commentComparator = (comment1, comment2) -> {
        if (comment1.getComments() == null) return 1;
        if (comment2.getComments() == null) return -1;
        return comment2.getComments().size() - comment1.getComments().size();
    };

}
