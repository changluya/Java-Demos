package com.changlu.learn1;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.restore.KeyGroup;
import org.apache.flink.util.Collector;

/**
 * Flink批数据 word count
 */
public class BatchWordCount {
    public static void main(String[] args) throws Exception {
        //1、设置本地环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //2、读取文件
        DataSource<String> linesDS = env.readTextFile("./data/words.txt");

        //3、切分单词
        FlatMapOperator<String, String> wordDS = linesDS.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String line, Collector<String> collector) throws Exception {
                // 对每一行进行切割
                String[] arr = line.split(" ");
                // 将所有的单词进行收集
                for (String word : arr) {
                    collector.collect(word);
                }
            }
        });

        //4、对单词进行过滤
        MapOperator<String, Tuple2<String, Long>> kvWords = wordDS.map(new MapFunction<String, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(String word) throws Exception {
                // 对每一个word都返回一个Tuple2，key为word，value为数字
                return Tuple2.of(word, 1L);
            }
        });

        //5、进行数据分组、计算
        // 以key来进行分组，对value来进行求和
        kvWords.groupBy(0).sum(1).print();
    }
}
