package com.changlu.test;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    /**
     * 获取当前可支持并发的sidecar（最大并发度）：返回当前sidecar_list_info列表中concurrency_free最大的sideCar的Id、localIp
     * @param data
     * @return
     * @throws IOException
     */
    private static Pair<String, String> getCanCurrencySidecarId(ResourceStatusResponse.DataResponse data)
            throws IOException {
        List<ResourceStatusResponse.DataResponse.SidecarInfo> sidecarListInfo = data.getSidecar_list_info();
        if (CollectionUtil.isNotEmpty(sidecarListInfo)) {
            // 过滤concurrency_free<=0的节点，同时根据concurrency_free进行降序排序
            List<ResourceStatusResponse.DataResponse.SidecarInfo> canUseSideCars = sidecarListInfo.stream()
                    .filter(sidecarInfo -> sidecarInfo.getConcurrency_free() > 0)
                    .sorted(Comparator.comparingInt(ResourceStatusResponse.DataResponse.SidecarInfo::getConcurrency_free).reversed())
                    .collect(Collectors.toList());
//            LOG.info("cur currency sidecars: {}", canUseSideCars);
            // 获取当前最大剩余容量并发度的节点
            if (CollectionUtil.isNotEmpty(canUseSideCars)) {
                ResourceStatusResponse.DataResponse.SidecarInfo sidecar = canUseSideCars.get(0);
                canUseSideCars.get(0).setConcurrency_free(sidecar.getConcurrency_free() - 1);
                return Pair.of(sidecar.getId(), sidecar.getLocal_ip());
            }
        }
        throw new RuntimeException("Not found can currency sidecar in em!");
    }

    public static void main(String[] args) throws Exception{
//        demo1();
        demo2();
    }

    /**
     * 案例1：SidecarInfo为空情况
     */
    public static void demo1() throws Exception{
        ResourceStatusResponse resourceStatusResponse = new ResourceStatusResponse();
        ResourceStatusResponse.DataResponse dataResponse = new ResourceStatusResponse.DataResponse();
        resourceStatusResponse.setData(dataResponse);

        for (int i = 0; i < 41; i ++) {
            Pair<String, String> canCurrencySidecarId = getCanCurrencySidecarId(dataResponse);
            System.out.println(canCurrencySidecarId);
        }
    }

    /**
     * 案例2：SidecarInfo中满足并发程度为40个
     */
    public static void demo2() throws Exception{
        ResourceStatusResponse resourceStatusResponse = new ResourceStatusResponse();
        ResourceStatusResponse.DataResponse dataResponse = new ResourceStatusResponse.DataResponse();
        // 三组 总共并发为40
        ResourceStatusResponse.DataResponse.SidecarInfo sidecarInfo = new ResourceStatusResponse.DataResponse.SidecarInfo();
        sidecarInfo.setId("1");
        sidecarInfo.setConcurrency_free(20);
        sidecarInfo.setLocal_ip("127.0.0.1");
        ResourceStatusResponse.DataResponse.SidecarInfo sidecarInfo2 = new ResourceStatusResponse.DataResponse.SidecarInfo();
        sidecarInfo2.setId("2");
        sidecarInfo2.setConcurrency_free(10);
        sidecarInfo2.setLocal_ip("127.0.0.2");
        ResourceStatusResponse.DataResponse.SidecarInfo sidecarInfo3 = new ResourceStatusResponse.DataResponse.SidecarInfo();
        sidecarInfo3.setId("3");
        sidecarInfo3.setConcurrency_free(10);
        sidecarInfo3.setLocal_ip("127.0.0.3");
        dataResponse.setSidecar_list_info(Arrays.asList(sidecarInfo, sidecarInfo2, sidecarInfo3));
        resourceStatusResponse.setData(dataResponse);

        // 额外多出一个情况：此时最后会抛出异常，其他均可以进行消费
        for (int i = 0; i < 41; i ++) {
            Pair<String, String> canCurrencySidecarId = getCanCurrencySidecarId(dataResponse);
            System.out.println(canCurrencySidecarId);
        }
    }


}
