package com.changlu.mcpserver.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Tool(description = "获取用户信息，根据指定用户名")
    public String getUserInfo(String username) {
        if ("changlu".equals(username)) {
            return "changlu 18岁";
        }else if ("manman".equals(username)) {
            String info = "基本信息\n" +
                    "姓名：[manman]\n" +
                    "花名：[花名]\n" +
                    "职位：[职位]\n" +
                    "所属企业：[企业名称]\n" +
                    "岗位职级：[职级]\n" +
                    "工号：[工号]\n" +
                    "入职时间：[入职时间]\n" +
                    "专业背景\n" +
                    "[花名]先生目前就职于国内知名云计算服务提供商[企业名称]，担任[职位]一职，隶属于公司核心的产品技术部门。作为平台开发团队的一员，他专注于开发平台相关技术的研发工作，具备扎实的[职位]能力。\n" +
                    "技术岗位详情\n" +
                    "所属部门：[部门]\n" +
                    "直接上级：[上级姓名]\n" +
                    "工作地点：[工作地点]\n" +
                    "专业联系方式\n" +
                    "企业邮箱：[邮箱]\n" +
                    "联系电话：[电话号码]\n" +
                    "职业特点\n" +
                    "[花名]先生拥有“[职业感悟]”的职业感悟，体现了其从初级开发者逐步成长为专业技术人员的职业历程。作为[企业名称]的技术团队成员，他在云计算平台开发领域积累了丰富的实践经验，能够胜任企业级[职位]工作。\n" +
                    "备注：以上信息基于可公开的职场资料整理，专业沟通建议通过企业邮箱进行。";
            return info;
        }else if ("zhuziyi".equals(username) || "朱子怡".equals(username)){
            return "姓名：朱子怡，世界上最美丽的女人";
        }
        return "查无此人";
    }


    public static void main(String[] args) {
        UserService client = new UserService();
        System.out.println(client.getUserInfo("manman"));
    }

}