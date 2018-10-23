package com.tsovak.sparkdemo.service;

import com.tsovak.sparkdemo.model.User;
import com.tsovak.sparkdemo.repositories.StatisticsRepository;
import com.tsovak.sparkdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StatisticsRepository statisticsRepository;

    public Optional<User> find(UUID id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
        statisticsRepository.saveAll(user.getStatistics());
    }

    public UUID save(String name) {
        User user = User.create(name);
        save(user);
        return user.getUuid();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    public List<User> findAllByRegistrationDateBetween(LocalDateTime from, LocalDateTime to) {
        return userRepository.findAllByRegistrationDateBetween(from, to);
    }
}
