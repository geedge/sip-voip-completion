package cn.ac.iie.utils.redis;

import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Redis连接池
 *
 * @author my
 * @date 2018-07-04
 */
public class RedisUrlPollUtils {
    private static final Logger logger = Logger.getLogger(RedisUrlPollUtils.class);
    private static String redisConfigFile = "redis_url_config.properties";
    private static JedisCluster jedisCluster;
    private static Properties props = new Properties();

    /**
     * 不允许通过new创建该类的实例
     */
    private RedisUrlPollUtils() {
    }

    /**
     * 初始化Redis连接池
     */
    public static JedisCluster getJedisCluster() {
        try {
            props.load(RedisUrlPollUtils.class.getClassLoader().getResourceAsStream(redisConfigFile));
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(Integer.valueOf(props.getProperty("jedis.pool.maxActive")));
            poolConfig.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));
            poolConfig.setMaxWaitMillis(Long.valueOf(props.getProperty("jedis.pool.maxWait")));
            poolConfig.setTestOnBorrow(Boolean.valueOf(props.getProperty("jedis.pool.testOnBorrow")));
            poolConfig.setTestOnReturn(Boolean.valueOf(props.getProperty("jedis.pool.testOnReturn")));
            Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
            for (String port : props.getProperty("redis.port").split(",")) {
                nodes.add(new HostAndPort(props.getProperty("redis.ip1"), Integer.parseInt(port)));
                nodes.add(new HostAndPort(props.getProperty("redis.ip2"), Integer.parseInt(port)));
                nodes.add(new HostAndPort(props.getProperty("redis.ip3"), Integer.parseInt(port)));
                nodes.add(new HostAndPort(props.getProperty("redis.ip4"), Integer.parseInt(port)));
                nodes.add(new HostAndPort(props.getProperty("redis.ip5"), Integer.parseInt(port)));
            }
            if (jedisCluster == null) {
                jedisCluster = new JedisCluster(nodes, poolConfig);
            }
            return jedisCluster;
        } catch (Exception e) {
            logger.error(("JedisCluster Connection creation Failed!!!") + e);
            e.printStackTrace();
            return null;
        }
    }

}
