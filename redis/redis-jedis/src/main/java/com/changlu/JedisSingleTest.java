/**
 * @description TODO
 * @author changlu
 * @date 2024/07/21 00:08
 * @version 1.0
 */
package com.changlu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description  jedis
 * @author changlu
 * @date 2024-07-21 0:08
 * @version 1.0
 */
public class JedisSingleTest {
    public static void main(String[] args) {
        //创建jedis池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(20);
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(5);

        // timeout，这里既是连接超时又是读写超时，从Jedis 2.8开始有区分connectionTimeout和soTimeout的构造函数
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.10.130", 6379, 3000, null);

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //******* jedis普通操作示例 ********
            System.out.println(jedis.set("single", "changlu"));
            System.out.println(jedis.get("single"));

            //******* 管道示例 *******
//            //创建管道
//            Pipeline pl = jedis.pipelined();
//            //在管道到创建多条命令
//            for (int i = 0; i < 10; i++) {
//                pl.incr("pipelineKey");
//                pl.set("zhuge" + i, "zhuge");
//                //模拟管道报错
//                pl.setbit("zhuge", -1, true);
//            }
//            //全部发送到管道中
//            List<Object> results = pl.syncAndReturnAll();
//            System.out.println(results);

            //******* lua脚本示例 ********
//            //模拟一个商品减库存的原子操作
//            //lua脚本命令执行方式：redis-cli --eval /tmp/test.lua , 10
//            jedis.set("product_stock_10016", "15");  //初始化商品10016的库存
//            //扣减库存代码，若是库存够则扣减库存返回1，若是不够则返回0
//            String script = " local count = redis.call('get', KEYS[1]) " +
//                    " local a = tonumber(count) " +
//                    " local b = tonumber(ARGV[1]) " +
//                    " if a >= b then " +
//                    "   redis.call('set', KEYS[1], a-b) " +
//                    "   return 1 " +
//                    " end " +
//                    " return 0 ";
//            Object obj = jedis.eval(script, Arrays.asList("product_stock_10016"), Arrays.asList("10"));
//            System.out.println(obj);

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还到资源池
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
