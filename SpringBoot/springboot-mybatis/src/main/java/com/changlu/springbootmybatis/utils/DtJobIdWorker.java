/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.changlu.springbootmybatis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo changlu 案例：
 *  1、获取当前时间戳：用 timeGen()，假设返回的时间戳为 1672502400（即 2023-01-01 00:00:00）。lastTimestamp 更新为 1672502400。
 *  2、检查时间回拨：当前时间戳 1672502400 不小于 lastTimestamp，无需处理时间回拨。
 *  3、增序列号：astTimestamp 与当前时间戳相等，sequence 自增为 1。
 *  4、拼接 Job ID： 时间戳：1672502400、网络标识：001、序列号：00001。 拼接结果：167250240000100001
 *  5、转换为32进制：调用 digits32(167250240000100001)，假设转换结果为 1a2b3c4d。
 *      167250240000100001 每5位低位二进制作为一个数字 【可见main实现过程】
 * @author yuebai
 * @date 2021-09-08
 */
public class DtJobIdWorker {

    private static volatile DtJobIdWorker singleton;

    //the last one net work
    // todo changlu 网络标识，用于区分不同的网络环境
    private Integer network;

    //auto increment sequence
    // todo changlu 自增序列号，用于在同一时间戳内生成多个ID
    private long sequence;

    //the last build jobId timeStamp
    // todo changlu 上一次生成ID的时间戳，用于避免时间回拨问题
    private long lastTimestamp = 0L;

    // todo changlu 初始化网络标识和序列号
    private DtJobIdWorker(Integer network, long sequence) {
        this.network = network;
        this.sequence = sequence;
    }

    public static DtJobIdWorker getInstance(Integer network, long sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence can not less than 0");
        }
        if (null == network) {
            throw new IllegalArgumentException("net work can not null");
        }
        if (singleton == null) {
            synchronized (DtJobIdWorker.class) {
                if (singleton == null) {
                    singleton = new DtJobIdWorker(network, sequence);
                }
            }
        }
        return singleton;
    }

    // todo changlu 32进制字符表
    final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v'
    };

    /**
     * change long to 32 digits
     * todo changlu 将长整型值转换为32进制字符串
     *
     * @param val
     * @return
     */
    static String digits32(long val) {
        // todo changlu 每次右移5位（因为2^5=32）
        int shift = 5;
        // todo changlu 计算值的二进制位数   Long.numberOfLeadingZeros(val)含义：从最高位（最左侧）开始连续为零的位数
        int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
        // todo changlu 计算需要的字符长度 每5位二进制作为一组
        int len = Math.max(((mag + (shift - 1)) / shift), 1);
        char[] buf = new char[len];
        // todo changlu 逐位转换 每次取出低位32的二进制  val(二进制) & 11111 用一个字符来表示&的结果
        do {
            buf[--len] = digits[((int) val) & 31]; // 取低5位
            val >>>= shift; // 右移5位
        } while (val != 0 && len > 0);
        // 返回32进制字符串
        return new String(buf);
    }


    //next JobId
    // todo changlu 生成下一个作业ID
    public synchronized String nextJobId() {
        // todo changlu 获取到秒级别时间戳
        long timestamp = timeGen();

        // todo changlu 处理时间回拨问题
        if (timestamp < lastTimestamp) {
            timestamp = lastTimestamp;
        }

        // todo changlu 自增序列号
        //increment sequence
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1;
            // todo changlu 如果序列号超过最大值，等待下一个时间戳
            if (sequence > 99999) {
                //out of sequence
                timestamp = tilNextMillis(lastTimestamp);
                sequence  = 0;
            }
        } else {
            sequence = 0;
        }
        // todo changlu 更新上一次时间戳
        //refresh
        lastTimestamp = timestamp;
        // todo changlu 拼接时间戳、网络标识和序列号
        String jobId = String.valueOf(lastTimestamp).concat(String.format("%03d", network)).concat(String.format("%05d", sequence));
        // todo changlu 将长整型ID转换为32进制字符串 173557304910400000 => 4q4pec1lnig0
        return digits32(Long.parseLong(jobId));
    }

    // todo changlu 秒级时间戳
    //get seconds
    private long timeGen() {
        return System.currentTimeMillis() / 1000;
    }

    //todo changlu 等待直到下一个时间戳
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static DtJobIdWorker jobIdWorker;

    public static String generateUniqueSign() {
        if (null == jobIdWorker) {
            String[] split = "0.0.0.0".split("\\.");
            jobIdWorker = DtJobIdWorker.getInstance(split.length >= 4 ? Integer.parseInt(split[3]) : 0, 0);
        }
        return jobIdWorker.nextJobId();
    }

    public static void main(String[] args) {
        System.out.println(jobIdWorker.generateUniqueSign());
    }

}
