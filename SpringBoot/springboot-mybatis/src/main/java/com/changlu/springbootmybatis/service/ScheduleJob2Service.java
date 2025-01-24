package com.changlu.springbootmybatis.service;

import com.changlu.springbootmybatis.mapper.ScheduleJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleJob2Service {

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

}
