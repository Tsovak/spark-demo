package com.tsovak.sparkdemo.repositories;


import com.tsovak.sparkdemo.model.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StatisticsRepository extends MongoRepository<Statistics, UUID> {

    @Query(fields = "{ 'uuid' : 0, 'userUuid' : 0}")
    List<Statistics> findAllByUserUuidAndDateBetweenOrderByDateDesc(UUID userUuid, LocalDateTime from, LocalDateTime to);
}
