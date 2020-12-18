package com.re.spring.boot.springbootdemo.util;

import com.re.spring.boot.springbootdemo.domain.ProjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    private static RedisTemplate<String, Object> redisTemplate;


    @Autowired
    void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    //============================= distributed lock ============================

    public static RedisLock newRedisLock(String redisKey, int timeOut, int expire) {
        return new RedisLock(redisKey, timeOut, expire);
    }

    public static class RedisLock {

        /**
         * 重试时间
         */
        private static final int DEFAULT_ACQUIRY_RETRY_MILLIS = 100;
        /**
         * 锁的前缀
         */
        private static final String LOCK_PREFIX = ProjectProperty.getProjectName() + "_";
        /**
         * 锁的后缀
         */
        private static final String LOCK_SUFFIX = "_redis_lock";
        /**
         * 锁的key
         */
        private final String lockKey;
        /**
         * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
         */
        private int expireMsecs = 60 * 1000;
        /**
         * 线程获取锁的等待时间
         */
        private int timeoutMsecs = 10 * 1000;
        /**
         * 是否锁定标志
         */
        private volatile boolean locked = false;

        /**
         * 构造器
         *
         * @param lockKey 锁的key
         */
        RedisLock(String lockKey) {
            this.lockKey = LOCK_PREFIX + lockKey + LOCK_SUFFIX;
        }

        /**
         * 构造器
         *
         * @param lockKey      锁的key
         * @param timeoutMsecs 获取锁的超时时间
         */
        RedisLock(String lockKey, int timeoutMsecs) {
            this(lockKey);
            this.timeoutMsecs = timeoutMsecs;
        }

        /**
         * 构造器
         *
         * @param lockKey      锁的key
         * @param timeoutMsecs 获取锁的超时时间
         * @param expireMsecs  锁的有效期
         */
        RedisLock(String lockKey, int timeoutMsecs, int expireMsecs) {
            this(lockKey, timeoutMsecs);
            this.expireMsecs = expireMsecs;
        }

        public String getLockKey() {
            return lockKey;
        }

        private String get(final String key) {
            Object obj = redisTemplate.opsForValue().get(key);
            return obj != null ? obj.toString() : null;
        }

        private boolean setNX(final String key, final String value) {
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, value);
            return aBoolean != null && aBoolean;
        }

        private String getSet(final String key, final String value) {
            Object obj = redisTemplate.opsForValue().getAndSet(key, value);
            return obj != null ? (String) obj : null;
        }

        public synchronized boolean lock() throws InterruptedException {
            int timeout = timeoutMsecs;
            while (timeout >= 0) {
                long expires = System.currentTimeMillis() + expireMsecs + 1;
                String expiresStr = String.valueOf(expires); //锁到期时间
                if (this.setNX(lockKey, expiresStr)) {
                    locked = true;
                    return true;
                }
                //redis里key的时间
                String currentValue = this.get(lockKey);
                //判断锁是否已经过期，过期则重新设置并获取
                if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
                    //设置锁并返回旧值
                    String oldValue = this.getSet(lockKey, expiresStr);
                    //比较锁的时间，如果不一致则可能是其他锁已经修改了值并获取
                    if (oldValue != null && oldValue.equals(currentValue)) {
                        locked = true;
                        return true;
                    }
                }
                timeout -= DEFAULT_ACQUIRY_RETRY_MILLIS;
                //延时
                Thread.sleep(DEFAULT_ACQUIRY_RETRY_MILLIS);
            }
            return false;
        }

        public boolean isLocked() {
            return locked;
        }

        /**
         * 释放获取到的锁
         */
        public synchronized void unlock() {
            if (locked) {
                redisTemplate.delete(lockKey);
                locked = false;
            }
        }

    }

    //============================= common ============================
    public static Long getExpire(String redisRegisterKey) {
        return redisTemplate.getExpire(redisRegisterKey);
    }

    public static boolean setIfAbsent(final String key, final String value, final long l, TimeUnit timeUnit) {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, value, l, timeUnit);
        return aBoolean == null ? false : aBoolean;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        Boolean aBoolean = redisTemplate.hasKey(key);
        return aBoolean != null && aBoolean;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */

    //============================ String =============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return false;
        }
    }

    //================================Map=================================

    /**
     * 查询hash
     *
     * @param key key
     * @return values
     */
    public static List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return List
     */
    public static List<Object> lGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public static Object hGet(String redisKey, String taskId) {
        return redisTemplate.opsForHash().get(redisKey, taskId);
    }

}
