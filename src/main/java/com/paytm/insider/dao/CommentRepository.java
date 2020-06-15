package com.paytm.insider.dao;

import com.paytm.insider.dto.Comment;

public interface CommentRepository {

    void save(Comment comment);

    Comment findById(Long commentId);
}
