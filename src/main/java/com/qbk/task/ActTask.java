package com.qbk.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
public class ActTask {

    @Scheduled(cron = "0 0/3 * * * ?")
    @Scheduled(cron = "30 1/3 * * * ?")
    public void checkAct(){
        System.out.println(LocalDateTime.now());
    }

}
