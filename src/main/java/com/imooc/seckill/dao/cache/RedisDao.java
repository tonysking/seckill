package com.imooc.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.imooc.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 通过Jedis接口使用Redis数据库
 */
@Component
public class RedisDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JedisPool jedisPool;
	// protostuff序列化工具用到的架构
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


	public RedisDao(@Value("127.0.0.1") String ip,@Value("6379") int port) {
		jedisPool = new JedisPool(ip, port);
	}

	public Seckill getSeckill(long seckillId) {
		//redis操作逻辑
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				//并没有实现内部序列化操作
				//get-> byte[] -> 反序列化 -> Object(Seckill)
				//采用自定义序列化
				//protostuff:pojo
				byte[] bytes = jedis.get(key.getBytes());
				//缓存重新获取到
				if (bytes != null) {
					//空对象
					Seckill seckill = schema.newMessage();
					//seckill 被反序列化（把数据传到空对象中） 空间压缩原生序列化的1/5~1/10 压缩速度是两个数量级快
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public String putSeckill(Seckill seckill) {
		//set Object(Seckill)-> 序列化 ->byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				//对象序列化
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//缓存超时时间
				int timeout = 60 * 60; //60*60s 即 1小时
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
