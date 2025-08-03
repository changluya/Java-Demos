package com.changlu.ai.rag.example.milvus.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class RagController {

	private final VectorStore vectorStore;

	private final ChatClient chatClient;

	public RagController(VectorStore vectorStore, ChatClient chatClient) {
		this.vectorStore = vectorStore;
		this.chatClient = chatClient;
	}

	// 历史消息列表
	private static List<Message> historyMessage = new ArrayList<>();

	// 历史消息列表的最大长度
	private final static int maxLen = 10;

	@GetMapping(value = "/chat")
	public Flux<String> generation(@RequestParam("prompt") String userInput, HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");

		// 发起聊天请求并处理响应
		Flux<String> resp = chatClient.prompt()
			.messages(historyMessage)
			.user(userInput)
			.advisors(
					QuestionAnswerAdvisor
					.builder(vectorStore) // 指定向量数据库，来进行search
					.searchRequest(SearchRequest.builder().build())
					.build()
			)
			.stream()
			.content();

		// 用户输入的文本是 UserMessage
		historyMessage.add(new UserMessage(userInput));

		// 发给 AI 前对历史消息对列的长度进行检查
		if (historyMessage.size() > maxLen) {
			historyMessage = historyMessage.subList(historyMessage.size() - maxLen - 1, historyMessage.size());
		}

		return resp;
	}

	/**
	 * 向量数据查询测试
	 */
	@GetMapping("/select")
	public List<Document> search() {
		return vectorStore.similaritySearch(
				SearchRequest.builder().query("SpringAIAlibaba").topK("SpringAIAlibaba".length()).build());
	}

}
