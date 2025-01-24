package com.changlu.springbootmybatis;

import com.changlu.springbootmybatis.mapper.ScheduleJobMapper;
import com.changlu.springbootmybatis.pojo.ScheduleJob;
import com.changlu.springbootmybatis.service.ScheduleJobService;
import com.changlu.springbootmybatis.utils.DtJobIdWorker;
import com.changlu.springbootmybatis.utils.RandomDateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ScheduleJobTests {

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    // 总插入条数
    private static final int TOTAL_RECORDS = 200 * 10000;

    // 每次批量插入的条数
    private static final int BATCH_SIZE = 2500;

    // 100 0000  批次 5000 一批 200-300ms 耗时30s
    // 100 0000  批次 2500 一批 70-100ms 耗时29s
    // 100 0000  批次 1000 一批 20-40ms 耗时37s
    // 100 0000  批次 500 一批 15-20 耗时36s
    @Test
    public void testBatchInsertScheduleJobs() {
        List<ScheduleJob> batchList = new ArrayList<>(BATCH_SIZE);
        int insertedRecords = 0;
        // 记录总开始时间
        long totalStartTime = System.currentTimeMillis();
        while (insertedRecords < TOTAL_RECORDS) {
            // 记录当前批次的开始时间
            long batchStartTime = System.currentTimeMillis();
            // 清空当前批次
            batchList.clear();
            // 生成当前批次的 1000 条数据
            for (int i = 0; i < BATCH_SIZE && insertedRecords < TOTAL_RECORDS; i++) {
                ScheduleJob job = new ScheduleJob();
                job.setProjectId(0);
                job.setDtuicTenantId(-1);
                job.setAppType(1);
                job.setJobId(DtJobIdWorker.generateUniqueSign());
//                job.setCycTime(RandomDateUtil.generateRandomDateTime("20250114000000")); // 随机生成日期时间
                job.setCycTime("20250115000000"); // 随机生成日期时间
                job.setIsDeleted(false);
                job.setFlowJobId("0");
                batchList.add(job);
                insertedRecords++;
            }
            // 批量插入当前批次
            scheduleJobMapper.batchInsertScheduleJobs(batchList);
            // 计算当前批次的执行时间
            long batchEndTime = System.currentTimeMillis();
            long batchTime = batchEndTime - batchStartTime;
            // 打印当前批次的执行时间和插入进度
            System.out.printf("已插入 %d 条数据，当前批次耗时：%d ms，总进度：%.2f%%\n",
                    insertedRecords, batchTime, (insertedRecords * 100.0 / TOTAL_RECORDS));
        }
        // 计算总执行时间
        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        // 打印总执行时间
        System.out.println("批量插入完成，总插入条数：" + insertedRecords);
        System.out.println("总执行时间：" + totalTime + " ms");
    }

    @Test
    public void testCountByConditions() {
        Integer dtuicTenantId = -1;
        Integer projectId = 0;
        String flowJobId = "0";
        String startCycTime = "20250115000000";
        String endCycTime = "20250115235959";
        // 构造1万条jobId
//        List<String> jobIds = Arrays.asList("4q8fss7locrr");
        List<String> jobIds = scheduleJobMapper.selectJobIds();
        Integer appType = 1;

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        // 执行查询
        int count = scheduleJobMapper.countByConditions(dtuicTenantId, projectId, flowJobId, startCycTime, endCycTime, jobIds, appType);
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        // 计算耗时
        long elapsedTime = endTime - startTime;
        // 打印结果和耗时
        System.out.println("符合条件的记录数：" + count);
        System.out.println("查询耗时：" + elapsedTime + " ms");
    }
}
