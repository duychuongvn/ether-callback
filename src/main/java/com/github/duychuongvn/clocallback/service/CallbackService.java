package com.github.duychuongvn.clocallback.service;

import com.github.duychuongvn.clocallback.dao.entity.ScheduleInfo;

public interface CallbackService {

    void callbackContract(ScheduleInfo scheduleInfo);
}
