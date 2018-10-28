package com.github.duychuongvn.clocallback.config;

import com.github.duychuongvn.clocallback.CallbackJob;
import com.github.duychuongvn.clocallback.contract.ScheduleContract;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;

@Configuration
@EnableMongoAuditing
@ComponentScan(value = "com.github.duychuongvn")
public class Config {

    @Value("${network.rpc}")
    private String rpc;
    @Value("${network.privateKey}")
    private String privateKey;
    @Value("${network.gasPrice}")
    private BigInteger gasPrice;
    @Value("${network.gasLimit}")
    private BigInteger gasLimit;
    @Value("${network.address}")
    private String address;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpc));
    }

    @Bean(name = "web3TransactionManager")
    public TransactionManager transactionManager() {
        Credentials credentials = Credentials.create(privateKey);
        return new RawTransactionManager(web3j(), credentials);
    }

    @Bean
    public ScheduleContract scheduleContract() {
        return new ScheduleContract(web3j(), transactionManager(), address, gasPrice, gasLimit);
    }

    @Bean
    public SchedulerFactory schedulerFactory() {
        return new org.quartz.impl.StdSchedulerFactory();
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        Scheduler sched = schedulerFactory().getScheduler();
        sched.start();
        return sched;
    }
}
