package com.paytm.insider.dao;

import com.paytm.insider.dto.Story;
import org.joda.time.DateTime;

import java.util.Optional;
import java.util.Set;

public interface StoryRepository {

    void save(Story story);

    Optional<Story> findById(Long storyId);

    Set<Story> findByTime(DateTime startTime, DateTime endTime);

    Set<Story> findAll();
}
