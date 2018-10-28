package com.github.duychuongvn.clocallback;

import com.github.duychuongvn.clocallback.constant.Constants;
import com.github.duychuongvn.clocallback.contract.ScheduleContract;
import com.github.duychuongvn.clocallback.dao.entity.ScheduleInfo;
import com.github.duychuongvn.clocallback.dao.repository.ScheduleInfoRepository;
import com.github.duychuongvn.clocallback.service.CallbackService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.DefaultBlockParameterName;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleApplication {

    private Logger logger = LoggerFactory.getLogger(ScheduleApplication.class);
    @Autowired
    private ScheduleContract scheduleContract;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduleInfoRepository scheduleInfoRepository;
    @Autowired
    private CallbackService callbackService;

    @PostConstruct
    public void handleEvent() {
        registerPendingEvent();
        scheduleContract.approvalScheduleEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(event -> {

                    String queryIdBase64 = Base64.getEncoder().encodeToString(event.queryId.getValue());

                    if (!scheduleInfoRepository.existsById(queryIdBase64)) {
                        ScheduleInfo scheduleInfo = new ScheduleInfo();
                        scheduleInfo.setId(queryIdBase64);
                        scheduleInfo.setFinished(false);
                        scheduleInfo.setGasLimit(event.gasLimit.getValue());
                        scheduleInfo.setContractAddress(event.target.getValue());
                        scheduleInfo.setTimestamp(event.timestamp.getValue().intValue());
                        scheduleInfoRepository.save(scheduleInfo);
                        addSchedule(scheduleInfo);
                    }


                });
    }

    private void addSchedule(ScheduleInfo scheduleInfo) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putIfAbsent(Constants.JOB_KEY_SCHEDULE_INFO, scheduleInfo);
        jobDataMap.putIfAbsent(Constants.JOB_KEY_REPOSITORY_SCHEDULE_INFO, callbackService);
        JobDetail jobDetail = JobBuilder.newJob(CallbackJob.class)
                .withIdentity("callbackJob_" + scheduleInfo.getId())
                .storeDurably()
                .usingJobData(jobDataMap)
                .build();


        logger.info("====== register event====");
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, scheduleInfo.getTimestamp());
        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("scheduleTrigger_" + scheduleInfo.getId())
                .startAt(calendar.getTime())
                .withSchedule(scheduleBuilder).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void registerPendingEvent() {
        List<ScheduleInfo> scheduleInfos = scheduleInfoRepository.findByFinishedIsFalse();
        scheduleInfos.forEach(this::addSchedule);
    }
}
