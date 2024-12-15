package com.changlu.jikedesign.prototype;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 需求2
 * 处理方式二：原型模式应用【浅拷贝】
 */
public class Demo3 {

    // 内存数据
    private HashMap<String, SearchWord> currentKeywords = new HashMap<>();
    private long lastUpdateTime = -1;

    public void refresh() {
        // 原型模式应用：拷贝已有对象的数据，更新少量差值【此处相当于直接拷贝一份，我们在进行refresh的时候不应该干扰原始业务数据】
        // 缺点：当前为浅拷贝【会造成使用具体对象时依旧操作的是currentKeywords里的】
        HashMap<String, SearchWord> newKeyWords = (HashMap<String, SearchWord>) currentKeywords.clone();
        // 从数据库中取出更新时间>lastUpdateTime的数据，放入到newKeywords中
        List<SearchWord> searchWords = getSearchWords(lastUpdateTime);
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord searchWord : searchWords) {
            if (searchWord.getLastUpdateTime() > maxNewUpdatedTime) {
                maxNewUpdatedTime = searchWord.getLastUpdateTime();
            }
            // 当前内存中有该keyword字段，此时进行更新
            if (newKeyWords.containsKey(searchWord.getKeyword())) {
                SearchWord oldSearchWord = newKeyWords.get(searchWord.getKeyword());
                oldSearchWord.setCount(searchWord.getCount());// 更新数量
                oldSearchWord.setLastUpdateTime(searchWord.getLastUpdateTime());// 更新时间
            }else {
                newKeyWords.put(searchWord.getKeyword(), searchWord);
            }
        }
        // 更新内存中的最近更新时间
        this.lastUpdateTime = maxNewUpdatedTime;
        // 更新引用
        this.currentKeywords = newKeyWords;
    }


    // 获取更新时间>lastUpdatedTime的记录
    private List<SearchWord> getSearchWords(long lastUpdateTime) {
        //todo db获取到最新更新的数据
        return null;
    }

}
