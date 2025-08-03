import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;

public class StreamServer {

    public static void main(String[] args) throws Exception {
        // 1. 初始化Stream客户端
        OpenDingTalkStreamClientBuilder.custom()
            .credential(new AuthClientCredential("dingvaxy13w4mfqzi4mm", "dmkLhKG1OV3-lpa5bKrfk4LolreEFkA-pRoviOe-G4liJJWqZB1Ffgb0_LePA1Kj"))
            
            // 2. 注册全局事件监听器
            .registerAllEventListener(new GenericEventListener() {
                @Override
                public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
                    try {
                        // 3. 解析事件数据
                        String eventType = event.getEventType();
                        String eventBody = event.getData().toString();
                        
                        // 4. 业务处理（示例：考勤打卡事件）
                        if ("attendance_check_record".equals(eventType)) {
                            handleAttendanceEvent(eventBody);
                        }
                        
                        return EventAckStatus.SUCCESS; // 必须返回ACK确认
                    } catch (Exception e) {
                        return EventAckStatus.LATER; // 失败时要求重试
                    }
                }
            })
            .build().start(); // 5. 启动服务
    }

    // 考勤打卡事件处理
    private static void handleAttendanceEvent(String eventBody) {
        // 解析JSON示例（使用Fastjson2）
        JSONObject data = JSON.parseObject(eventBody);
        String userId = data.getString("userId");
        long checkTime = data.getLong("checkTime");
        
        System.out.printf("收到打卡事件: 用户=%s, 时间=%tF %tT%n", 
                          userId, checkTime, checkTime);
        
        // TODO: 调用考勤API拉取详情（需额外实现）
//        fetchAttendanceDetails(userId, checkTime);
    }
}