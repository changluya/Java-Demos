package com.changlu.springbootmybatis.mapper;

import com.changlu.springbootmybatis.pojo.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleJobMapper {

    int insertScheduleJob(ScheduleJob scheduleJob);

    ScheduleJob selectScheduleJobById(Integer id);

    int updateScheduleJob(ScheduleJob scheduleJob);

    int deleteScheduleJobById(Integer id);

    List<ScheduleJob> selectAllScheduleJobs();

    ScheduleJob selectScheduleJobByJobIdAndIsDeleted(String jobId, Boolean isDeleted);

    // 新增批量插入方法
    int batchInsertScheduleJobs(List<ScheduleJob> scheduleJobs);

    List<String> selectJobIds();

    // 新增复杂查询方法
    int countByConditions(
            @Param("dtuicTenantId") Integer dtuicTenantId,
            @Param("projectId") Integer projectId,
            @Param("flowJobId") String flowJobId,
            @Param("startCycTime") String startCycTime,
            @Param("endCycTime") String endCycTime,
            @Param("jobIds") List<String> jobIds,
            @Param("appType") Integer appType
    );

    // 新增复杂查询方法
    int countByConditions2(
            @Param("dtuicTenantId") Integer dtuicTenantId,
            @Param("projectId") Integer projectId,
            @Param("flowJobId") String flowJobId,
            @Param("startCycTime") String startCycTime,
            @Param("endCycTime") String endCycTime,
            @Param("jobIds") List<String> jobIds,
            @Param("appType") Integer appType
    );
}