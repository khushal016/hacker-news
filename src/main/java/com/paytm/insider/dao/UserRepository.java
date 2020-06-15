package com.paytm.insider.dao;

import com.paytm.insider.dto.User;

public interface UserRepository {

    void save(User user);

    User findById(String id);
}
