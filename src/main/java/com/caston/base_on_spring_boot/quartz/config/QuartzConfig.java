package com.caston.base_on_spring_boot.quartz.config;

import com.caston.base_on_spring_boot.quartz.service.QuartzService;
import org.quartz.CronTrigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Objects;

@Configuration
public class QuartzConfig {
    /**
     * 该方法设置定时执行的任务
     * <p>
     * TargetObject：指定需要定时执行的那个对象
     * targetMethod: 指定需要定时执行的方法
     * concurrent：
     * false:多个job不会并发执行,设置此次定时完成再执行下一次
     * true:多个job并发执行
     *
     * @param quartzService
     * @return
     */
    @Bean(name = "detailFactoryBean1")
    public MethodInvokingJobDetailFactoryBean detailFactoryBean1(QuartzService quartzService) {
        MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();
        factoryBean.setTargetObject(quartzService);
        factoryBean.setTargetMethod("quartz1");
        factoryBean.setConcurrent(false);
        return factoryBean;
    }

    /**
     * 该方法设置触发器（定时任务的执行方式）
     *
     * @param detailFactoryBean
     * @return
     */
    @Bean("cronTriggerFactoryBean1")
    public CronTriggerFactoryBean cronTriggerFactoryBean1(@Qualifier("detailFactoryBean1") MethodInvokingJobDetailFactoryBean detailFactoryBean) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(Objects.requireNonNull(detailFactoryBean.getObject()));
        try {
            trigger.setCronExpression("0/5 * * * * ? ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trigger;
    }

    @Bean(name = "detailFactoryBean2")
    public MethodInvokingJobDetailFactoryBean detailFactoryBean2(QuartzService quartzService) {
        MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();
        factoryBean.setTargetObject(quartzService);
        factoryBean.setTargetMethod("quartz2");
        factoryBean.setConcurrent(false);
        return factoryBean;
    }

    @Bean("cronTriggerFactoryBean2")
    public CronTriggerFactoryBean cronTriggerFactoryBean2(@Qualifier("detailFactoryBean2") MethodInvokingJobDetailFactoryBean detailFactoryBean) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(Objects.requireNonNull(detailFactoryBean.getObject()));
        try {
            trigger.setCronExpression("0/5 * * * * ? ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trigger;
    }

    /**
     * 该方法是通过调度工厂执行定时任务
     *
     * @param cronTrigger1
     * @param cronTrigger2
     * @return
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean2(@Qualifier("cronTriggerFactoryBean1") CronTrigger cronTrigger1, @Qualifier("cronTriggerFactoryBean2") CronTrigger cronTrigger2) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(cronTrigger1, cronTrigger2);
        return schedulerFactoryBean;
    }
}
