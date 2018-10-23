package com.tsovak.sparkdemo.repositories;

import com.tsovak.sparkdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {

    @Query("{uuid: { $in: ?0 } })")
    List<User> findAllByUuid(List<UUID> uuids);

    List<User> findAllByRegistrationDateBetween(LocalDateTime from, LocalDateTime to);
}
