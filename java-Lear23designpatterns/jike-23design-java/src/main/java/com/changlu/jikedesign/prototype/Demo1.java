package com.changlu.jikedesign.prototype;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需求1
 */
public class Demo1 {

    // 内存数据
    private ConcurrentHashMap<String, SearchWord> currentKeywords = new ConcurrentHashMap<>();
    private long lastUpdateTime = -1;

    // 更新数据
    public void refresh() {
        // 从数据库中获取到最新更新的一批数据
        List<SearchWord> toBeUpdatedSearchWords = getSearchWords(lastUpdateTime);
        // 更新时间戳
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord searchWord : toBeUpdatedSearchWords) {
            // 更新当前一批数据中最大的更新时间
            if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                maxNewUpdatedTime= searchWord.getLastUpdateTime();
            }
            // 区分新增数据还是更新数据
            if (currentKeywords.containsKey(searchWord.getKeyword())) {
                // 更新数据
                currentKeywords.replace(searchWord.getKeyword(), searchWord);
            }else {
                // 新增数据
                currentKeywords.put(searchWord.getKeyword(), searchWord);
            }
        }
        this.lastUpdateTime = maxNewUpdatedTime;
    }



    // 获取更新时间>lastUpdatedTime的记录
    private List<SearchWord> getSearchWords(long lastUpdateTime) {
        //todo db获取到最新更新的数据
        return null;
    }

}
