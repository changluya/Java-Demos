/**
 * @description TODO
 * @author changlu
 * @date 2024/07/21 12:44
 * @version 1.0
 */
package com.changlu;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @description  jedis连接哨兵实战
 * @author changlu
 * @date 2024-07-21 12:44
 * @version 1.0
 */
public class JedisSentinelTest {
    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);

        //设置master节点的名称，这个与sentinel中的配置映射
        String masterName = "mymaster";
        Set<String> sentinels = new HashSet<String>();
        //设置三个哨兵的真实ip地址寄端口号
        sentinels.add(new HostAndPort("192.168.10.130",26379).toString());
        sentinels.add(new HostAndPort("192.168.10.130",26380).toString());
        sentinels.add(new HostAndPort("192.168.10.130",26381).toString());
        //JedisSentinelPool其实本质跟JedisPool类似，都是与redis主节点建立的连接池
        //JedisSentinelPool并不是说与sentinel建立的连接池，而是通过sentinel发现redis主节点并与其建立连接
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, config, 3000, null);
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
            System.out.println(jedis.set("sentinel", "changlu"));
            System.out.println(jedis.get("sentinel"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
            if (jedis != null)
                jedis.close();
        }
    }
}
