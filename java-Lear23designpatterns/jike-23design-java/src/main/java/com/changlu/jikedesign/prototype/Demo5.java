package com.changlu.jikedesign.prototype;
import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * 需求2
 * 处理方式四：原型模式应用【浅拷贝、深拷贝搭配使用】
 */
public class Demo5 {

    // 内存数据
    private HashMap<String, SearchWord> currentKeywords = new HashMap<>();
    private long lastUpdateTime = -1;

    public void refresh() {
        // 浅拷贝（保留老数据）
        HashMap<String, SearchWord> newKeyWords = (HashMap<String, SearchWord>) currentKeywords.clone();

        // 从数据库中取出更新时间>lastUpdateTime的数据，放入到newKeywords中
        List<SearchWord> searchWords = getSearchWords(lastUpdateTime);
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord searchWord : searchWords) {
            if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                maxNewUpdatedTime = searchWord.getLastUpdateTime();
            }
            // 如果有存在老数据，此时直接删除该key
            if (newKeyWords.containsKey(searchWord.getKeyword())) {
                newKeyWords.remove(searchWord.getKeyword());
            }
            // 深拷贝（将当前新数据存储）
            newKeyWords.put(searchWord.getKeyword(), searchWord);
        }
        this.lastUpdateTime = maxNewUpdatedTime;
        this.currentKeywords = newKeyWords;
    }


    // 获取更新时间>lastUpdatedTime的记录
    private List<SearchWord> getSearchWords(long lastUpdateTime) {
        //todo db获取到最新更新的数据
        return null;
    }

}
