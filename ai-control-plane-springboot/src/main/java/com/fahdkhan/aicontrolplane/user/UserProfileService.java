package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.security.User;
import com.fahdkhan.aicontrolplane.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    @Autowired
    UserRepository userRepository;

    public List<User> listSeededUsers() {

        return userRepository.findAll();
    }
}
