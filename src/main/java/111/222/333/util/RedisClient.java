package com.zheman.lock.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component("redisClient")
public class RedisClient {
	@Autowired
	private JedisPool jedisPool;

	public void set(String key, String value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	public String get(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	/**
	 * 批量删除对应的value
	 * 
	 * @param keys
	 */
	public void remove(final String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			for (String key : keys)
				jedis.del(key);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (exists(key)) {
				jedis.del(key);
			}
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean set(final String key, Object value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value.toString());
		} finally {
			// 返还到连接池
			jedis.close();
		}
		return false;
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, String value, int expireTime) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setex(key, expireTime, value);
		} finally {
			// 返还到连接池
			jedis.close();
		}
		return false;
	}

	/**
	 * 写入缓存Map
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean mSet(final String key, Map<String, String> map) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hmset(key, map);
		} finally {
			// 返还到连接池
			jedis.close();
		}
		return false;
	}

	/**
	 * 写入缓存Map 设置过期时间
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean mSet(final String key, Map<String, String> map, int validtime) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hmset(key, map);
			jedis.expire(key, validtime);
		} finally {
			// 返还到连接池
			jedis.close();
		}
		return false;
	}

	/**
	 * 查询缓存Map
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public List<String> mGet(final String key, String field) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(key, field);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

}