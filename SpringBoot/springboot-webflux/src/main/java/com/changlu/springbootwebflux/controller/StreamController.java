package com.changlu.springbootwebflux.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/api/stream")
public class StreamController {


    @GetMapping(value = "/numbers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> streamNumbers() {
        return Flux.interval(Duration.ofMillis(200))
                .map(i -> i.intValue() + 1)
                .take(100); // 只发送10个数字
    }


    @GetMapping(value = "/numbers2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamNumbers2() {
        return Flux.<String>create(emitter -> {
            for (int i = 0; i < 100; i++) {
                emitter.next( i + "\n" +
                        "2025-08-13T01:07:04.429+08:00  INFO 73505 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("loop");
            }
            System.out.println("end");
        })
        .doOnCancel(() -> {
            System.out.println("doOnCancel ...");
        })
        .doOnTerminate(() -> {
            System.out.println("123");
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/numbers3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamNumbers3(HttpServletResponse response) {
        return Flux.<String>create(emitter -> {
                    for (int i = 0; i < 100; i++) {
                        int finalI = i;
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                emitter.next("data: " + finalI + "\n\n"); // SSE 格式要求
//                                System.out.println("data: " + finalI);
//                            }
//                        }.start();
                        emitter.next("data: " + finalI + "\n\n"); // SSE 格式要求
                        try {
                            Thread.sleep(100);
                            response.getOutputStream().flush();
                        }
                        catch (InterruptedException e) {
                            emitter.error(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    emitter.complete();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/numbers4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamNumbers4() {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        // 模拟在不同位置发送事件
        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                try {
                    Thread.sleep(1000); // 模拟业务处理时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String event = i + "\n\n";
                sink.tryEmitNext(event);
            }
            sink.tryEmitComplete();
        }).start();

        return sink.asFlux();
    }


    @GetMapping(value = "/numbers5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamNumbers5() {
        return Flux.<String>create(sink -> {
//            for (int i = 1; i <= 100; i++) {
//                try {
//                    Thread.sleep(100);
//                    System.out.println(i);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                String event = "data: " + i + "\n\n";
//                sink.next(event);
//            }
//            sink.complete();
            new Thread(() -> {
                for (int i = 1; i <= 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String event = "data: " + i + "\n\n";
                    sink.next(event);
                }
                sink.complete();
            }).start();
        });
    }



}