package com.dtstack.ai.alibaba.mcp.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

/**
 * @author brianxiadong
 */

public class SampleClient {

	private final McpClientTransport transport;

    public SampleClient(McpClientTransport transport) {
        this.transport = transport;
    }

    public void run() {

        var client = McpClient.sync(this.transport).build();

        client.initialize();

        client.ping();

        // 列出并展示可用的工具
        McpSchema.ListToolsResult toolsList = client.listTools();
        System.out.println("可用工具 = " + toolsList);

        // 获取北京的天气预报
        McpSchema.CallToolResult weatherForecastResult = client.callTool(new McpSchema.CallToolRequest("getWeatherForecastByLocation",
                Map.of("latitude", "39.9042", "longitude", "116.4074")));
        System.out.println("北京天气预报: " + weatherForecastResult);

        // 获取北京的空气质量信息
        McpSchema.CallToolResult airQualityResult = client.callTool(new McpSchema.CallToolRequest("getAirQuality",
                Map.of("latitude", "39.9042", "longitude", "116.4074")));
        System.out.println("北京空气质量: " + airQualityResult);

        client.closeGracefully();
    }
}