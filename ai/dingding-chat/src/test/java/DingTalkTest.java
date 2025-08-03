

import com.changlu.dingtalk.DingTalkSpringbootApplication;
import com.changlu.dingtalk.service.DingTalkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = DingTalkSpringbootApplication.class) // 明确指定主配置类
public class DingTalkTest {

    @Autowired
    private DingTalkService dingTalkService;

    @Test
    public void testSendMessage() {
        // 测试发送文本消息
//        dingTalkService.sendTextMessage("测试测试这是一条测试消息", null);

        Map<String, Object> atMap = new HashMap<>();
        atMap.put("isAtAll", false);
        atMap.put("atUserIds", Arrays.asList("manager3555"));
//        // 测试发送文本消息
//        dingTalkService.sendTextMessage("测试测试这是一条测试消息", atMap);

        // 测试发送Markdown消息
        String markdown = "### 钉钉AI助手测试\n" +
                "> **功能列表**\n" +
                "> 1. 智能问答\n" +
                "> 2. 信息查询\n" +
                "> 3. 任务提醒\n" +
                "> \n" +
                "> [点击查看详情](https://www.example.com)";
        dingTalkService.sendMarkdownMessage("AI助手功能", markdown, atMap);
    }
}