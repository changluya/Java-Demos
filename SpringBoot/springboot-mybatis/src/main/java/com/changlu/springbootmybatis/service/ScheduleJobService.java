package com.changlu.springbootmybatis.service;

import com.changlu.springbootmybatis.config.CustomizedTransactionTemplate;
import com.changlu.springbootmybatis.mapper.ScheduleJobExpandMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class ScheduleJobService {

    @Autowired
    private ScheduleJobExpandMapper scheduleJobExpandMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomizedTransactionTemplate customizedTransactionTemplate;

    // 编程式事务 spring框架自带事务管理器 有效 ✅
    public void batchRestartScheduleJob2 (List<String> jobIds) {
        // 事务回滚
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    doBatchRestartScheduleJob(jobIds);
                } catch (Exception e) {
                    // 回滚事务
                    status.setRollbackOnly();
                    System.out.println("Transaction rolled back due to: " + e.getMessage());
                }
            }
        });
    }

    // 本身方法无Transactional，无传播机制，内部doBatchRestartScheduleJob无效
    public void batchRestartScheduleJob(List<String> jobIds) {
        try {
            doBatchRestartScheduleJob(jobIds);
        }catch (Exception ex) {
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void doBatchRestartScheduleJob (List<String> jobIds) {
        scheduleJobExpandMapper.insertNextRunForJobs(jobIds);
//        throw new RuntimeException("123");
    }

    // **************自定义事务工具类*************
    // 自定义编程事务 有效 ✅
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void batchRestartScheduleJob3 (List<String> jobIds) {
        // 拆分批量
        List<List<String>> partition = Lists.partition(jobIds, 200);
        // 分区
        for (List<String> jobs : partition) {
            // 编程式事务
//            customizedTransactionTemplate.execute(() -> {
//                try{
//                    doBatchRestartScheduleJob2(jobs);
//                }catch(Exception ex){
//                    ex.printStackTrace();
//                    System.out.println("jobs=>" + jobs.toString());
//                    throw ex;
//                }
//            });
            // 容器中获取代理对象调用
//            ScheduleJobService scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
//            scheduleJobService.doBatchRestartScheduleJob2(jobs);
//            synchronized (ScheduleJobService.class) {
                long startTime = System.currentTimeMillis(); // 获取开始时间
                ScheduleJobService scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
                scheduleJobService.doBatchRestartScheduleJob2(jobs);
                long endTime = System.currentTimeMillis(); // 获取结束时间
                long duration = endTime - startTime; // 计算持续时间
                System.out.println("batchRestartScheduleJob3 ends. Total time: " + duration + " ms.");
//            }
        }
    }

//    @Transactional(rollbackFor = Exception.class)
    public void doBatchRestartScheduleJob2 (List<String> jobIds) {
        // 多条插入
        scheduleJobExpandMapper.insertNextRunForJobs(jobIds);
        // 单条插入
//        for (int i = 0; i < jobIds.size(); i++) {
//            scheduleJobExpandMapper.insertNextRunForJob(jobIds.get(i));
//        }
//        throw new RuntimeException("123");
    }

}
