package com.changlu.springbootmybatis.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleJobExpandMapper {

    void insertNextRunForJobs(@Param("jobIds") List<String> jobIds);

    void insertNextRunForJob(@Param("jobId") String jobId);

}