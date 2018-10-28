package com.github.duychuongvn.clocallback;

import com.github.duychuongvn.clocallback.constant.Constants;
import com.github.duychuongvn.clocallback.dao.entity.ScheduleInfo;
import com.github.duychuongvn.clocallback.dao.repository.ScheduleInfoRepository;
import com.github.duychuongvn.clocallback.service.CallbackService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CallbackJob extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        CallbackService callbackService = (CallbackService) context.getMergedJobDataMap().get(Constants.JOB_KEY_REPOSITORY_SCHEDULE_INFO);
        ScheduleInfo scheduleInfo = (ScheduleInfo) context.getMergedJobDataMap().get(Constants.JOB_KEY_SCHEDULE_INFO);
        callbackService.callbackContract(scheduleInfo);
    }

}
