package redis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;


public class Test {
	
	public static void main(String[] args) {
		ApplicationContext ac =  new ClassPathXmlApplicationContext("spring-context.xml");
		RedisUtil redisUtil = (RedisUtil)ac.getBean("redisUtil");
		
		for(int i = 0; i <= 10; i++){
			redisUtil.makeId("cz");
			
			System.out.println(redisUtil.getString("cz"));
		}
		
		
//		final RedisTemplate jedisTemplate = (RedisTemplate)ac.getBean("jedisTemplate");
//		ValueOperations<String,String> valueOper = jedisTemplate.opsForValue();
//		
//		valueOper.set("aa","44");
//		
//		System.out.println(valueOper.get("aa"));
//		
//		System.out.println(redisUtil.getString("cz"));
//		
//		
//		
//		for(int i = 0; i < 1; i++){
//			valueOper.increment("cz2", 1);
//		}
//		//System.out.println(valueOper.get("cz2"));
//		//System.out.println(redisUtil.getString("aa"));
//		
//		System.out.println("--------------");
//		jedisTemplate.execute(new RedisCallback<Long>() {
//
//	            public Long  doInRedis(RedisConnection connection) throws DataAccessException {
//	            	RedisSerializer<String> serializer = jedisTemplate.getStringSerializer();
//	                return connection.incr(serializer.serialize("cz2"));
//	            }
//	        });
//		
//		
//		System.out.println(redisUtil.getString("cz2"));
	}
	
}
