package com.karrardelivery.common.httpclient.service;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class IdleConnectionMonitorConfig implements SchedulingConfigurer {

    @Autowired
    private PoolingHttpClientConnectionManager syberPoolingConnectionManager;

    @Value("${http.idle.monitor.fixeddelay:5000}")
    private int idleMonitorFixedDelay;

    @Bean
    @Qualifier("threadPoolTaskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // Adjust based on your needs
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
        taskRegistrar.addFixedDelayTask(new Runnable() {
            @Override
            public void run() {
                syberPoolingConnectionManager.closeExpired();
                syberPoolingConnectionManager.closeIdle(TimeValue.ofSeconds(idleMonitorFixedDelay));
            }
        }, idleMonitorFixedDelay);
    }
}

