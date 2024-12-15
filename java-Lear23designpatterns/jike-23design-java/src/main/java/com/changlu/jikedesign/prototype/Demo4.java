package com.changlu.jikedesign.prototype;
import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * 需求2
 * 处理方式三：原型模式应用【深拷贝-序列化方式】
 */
public class Demo4 {

    // 内存数据
    private HashMap<String, SearchWord> currentKeywords = new HashMap<>();
    private long lastUpdateTime = -1;

    public void refresh() {
        // 序列化实现深拷贝
        HashMap<String, SearchWord> newKeyWords = (HashMap<String, SearchWord>) deepCopy(currentKeywords);

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

    public Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(object);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream ous = new ObjectInputStream(bi);
            return ous.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    // 获取更新时间>lastUpdatedTime的记录
    private List<SearchWord> getSearchWords(long lastUpdateTime) {
        //todo db获取到最新更新的数据
        return null;
    }

}
