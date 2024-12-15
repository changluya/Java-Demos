package com.changlu.jikedesign.prototype;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 需求2
 * 处理方式一：全量直接每次更新最新的数据
 */
public class Demo2 {

    // 内存数据
    private HashMap<String, SearchWord> currentKeywords = new HashMap<>();

    // 当前方式为每次全量同步更新（暴力）
    public void refresh() {
        // 准备一个新容器存储最新获取到的全部数据
        HashMap<String, SearchWord> newKeywords = new LinkedHashMap<>();
        // 获取到全部数据
        List<SearchWord> searchWords = getSearchWords();
        for (SearchWord searchWord : searchWords) {
            newKeywords.put(searchWord.getKeyword(), searchWord);
        }
        this.currentKeywords = newKeywords;
    }


    // 获取所有的数据
    private List<SearchWord> getSearchWords() {
        //todo db获取到最新更新的数据
        return null;
    }

}
