package com.github.duychuongvn.clocallback.dao.repository;

import com.github.duychuongvn.clocallback.dao.entity.ScheduleInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleInfoRepository extends MongoRepository<ScheduleInfo, String> {

    List<ScheduleInfo> findByFinishedIsFalse();
}
