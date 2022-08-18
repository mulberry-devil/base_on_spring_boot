package com.caston.base_on_spring_boot.quartz.service;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.Date;

@Service
public class QuartzService {

    public void quartz1() {
        DateFormat formatter = DateFormat.getDateTimeInstance();
        System.out.println(formatter.format(new Date()));
    }

    public void quartz2() {
        DateFormat formatter = DateFormat.getDateTimeInstance();
        System.out.println("当前时间：" + formatter.format(new Date()));
    }
}
