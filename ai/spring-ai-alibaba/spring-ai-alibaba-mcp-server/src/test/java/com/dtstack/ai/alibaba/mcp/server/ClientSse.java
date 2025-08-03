package com.dtstack.ai.alibaba.mcp.server;

import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author brianxiadong
 */
public class ClientSse {

	public static void main(String[] args) {
		var transport = new WebFluxSseClientTransport(WebClient.builder().baseUrl("http://localhost:8080"));
		new SampleClient(transport).run();
	}

}
