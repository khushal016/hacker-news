package com.paytm.insider.util;

import com.paytm.insider.dao.CommentRepository;
import com.paytm.insider.dao.StoryRepository;
import com.paytm.insider.dao.UserRepository;
import com.paytm.insider.dto.Comment;
import com.paytm.insider.dto.Story;
import com.paytm.insider.dto.User;
import com.paytm.insider.service.HNApiService;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NewsUtil {

    private final Logger logger = LoggerFactory.getLogger(NewsUtil.class);

    @Autowired
    HNApiService hnApiService;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    public Story getStory(Long storyId) {
        Optional<Story> cachedStory = storyRepository.findById(storyId);
        if (cachedStory.isPresent()) {
            logger.info("Story:{} found in cache", storyId);
            return cachedStory.get();
        }
        return hnApiService.getStory(storyId);
    }

    public Comment getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment != null) {
            logger.info("Comment:{} found in cache", commentId);
            comment.setUser(getUser(comment.getBy()));
            return comment;
        }
        comment = hnApiService.getComment(commentId);
        if (comment != null) {
            comment.setUser(getUser(comment.getBy()));
            commentRepository.save(comment);
        }
        return comment;
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            logger.info("User:{} found in cache", userId);
            user.setAge(Years.yearsBetween(formatter.parseDateTime(user.getCreated()), DateTime.now()).getYears());
            return user;
        }
        user = hnApiService.getUser(userId);
        if (user != null) {
            user.setAge(Years.yearsBetween(formatter.parseDateTime(user.getCreated()), DateTime.now()).getYears());
            userRepository.save(user);
        }
        return user;
    }
}
