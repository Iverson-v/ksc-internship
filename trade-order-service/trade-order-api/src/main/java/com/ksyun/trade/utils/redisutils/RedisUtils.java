package com.ksyun.trade.utils.redisutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**expire(String key, long timeout)：设置键的有效时间。可以通过指定的超时时间（以秒为单位）将键设置为过期。返回值表示设置是否成功。
 expire(String key, long timeout, TimeUnit unit)：设置键的有效时间。可以通过指定的超时时间和时间单位将键设置为过期。返回值表示设置是否成功。
 getExpire(String key, TimeUnit unit)：根据键获取过期时间。返回键的过期时间，单位为指定的时间单位。
 hasKey(String key)：判断键是否存在。检查指定的键是否存在于Redis中。返回值为true表示键存在，false表示键不存在。
 del(String... key)：删除多个键的缓存。可以同时删除多个键对应的缓存。返回值表示删除是否成功。
 delKey(String key)：删除单个键的缓存。删除指定键对应的缓存。返回值表示删除是否成功。
 delKeys(Collection<String> keys)：删除多个键的缓存。可以通过键的集合来删除多个键对应的缓存。返回值表示删除的键的数量。**/
@Component
public class RedisUtils {

    public RedisUtils() {
    }

    //获取IOC容器中的redisTemplate对象。
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;





    //----------------------------------------------基于键的用法-----------------------------------------------------------
    /**
     * 设置有效时间
     * @param key
     * @param timeout
     * @return
     */
    public  boolean expire(final String key, final long timeout) {
        try {
            if (timeout > 0) {
                //给key设置过期时间，单位是秒
                Boolean ret = redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
                return ret != null && ret;
            } else {
                throw new RuntimeException("过期时间需要大于0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置有效时间，需要传递单位
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public  boolean expire(final String key, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                Boolean ret = redisTemplate.expire(key, timeout, unit);
                return ret != null && ret;
            } else {
                throw new RuntimeException("过期时间需要大于0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key  键 不能为null
     * @param unit 时间单位
     * @return 时间 返回0代表为永久有效
     */
    public  long getExpire(final String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 判断key是否存在
     *
     * @param key key 键
     * @return true 存在 false不存在
     */
    public  boolean hasKey(String key) {
        try {
            Boolean ret = redisTemplate.hasKey(key);
            return ret != null && ret;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除多个缓存
     *
     * @param key
     */
    public  boolean del(final String... key) {
        boolean del = false;
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                Boolean ret = redisTemplate.delete(key[0]);
                del = ret != null && ret;
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
                del = true;
            }
        }
        return del;
    }
    /**
     * 删除单个缓存
     *
     * @param key
     * @return
     */
    public  boolean delKey(final String key) {
        Boolean ret = redisTemplate.delete(key);
        return ret != null && ret;
    }


    /**
     * 删除多个缓存
     *
     * @param keys
     * @return
     */
    public  long delKeys(final Collection<String> keys) {
        Long ret = redisTemplate.delete(keys);
        return ret == null ? 0 : ret;
    }



    //----------------------------------------------String用法-----------------------------------------------------------

    /**
     * 存入有时限的普通对象
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    public  boolean strSetWithTimeLimit(final String key, final Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                strSet(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存入永久的普通对象
     *
     * @param key
     * @param value
     */
    public  boolean strSet(final String key, final Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取普通对象
     *
     * @param key
     * @return
     */
    public  Object strGet(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增
     *
     * @param key
     * @param by
     * @return
     */
    public  long strIncrement(final String key, long by) {
        return redisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 递减
     *
     * @param key
     * @param by
     * @return
     */
    public  long strDecrement(final String key, long by) {
        return redisTemplate.opsForValue().increment(key, -by);
    }

    /**
     * 设置键的字符串值并返回其旧值
     * @param key
     * @param newValue
     * @return
     */
    public  Object strGetAndSet(final String key,Object newValue){
        return redisTemplate.opsForValue().getAndSet(key,newValue);
    }

    /**
     * key存在：追加；key不存在：同set
     * @param key
     * @param value
     * @return
     */
    public  int strAppend(final String key,String value){
        Integer appendLength = redisTemplate.opsForValue().append(key, value);
        return appendLength == null ? 0 : appendLength;
    }


//----------------------------------------------Hash用法-----------------------------------------------------------


    /**
     * 确定哈希hashKey是否存在
     *
     * @param key
     * @param hkey
     * @return
     */
    public  boolean hashHasKey(final String key, final String hkey) {
        Boolean ret = redisTemplate.opsForHash().hasKey(key, hkey);
        return ret != null && ret;
    }

    /**
     * 往Hash中存入数据
     *
     * @param key
     * @param item
     * @param value
     */
    public  boolean hashPut(final String key, final String item, final Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 往hash里面添加新值的时候同时修改存活时间
     *
     * @param key
     * @param item
     * @param value
     * @param timeout
     * @return
     */
    public  boolean hashPut(final String key, final String item, final Object value, long timeout) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 往Hash中存入多个数据
     *
     * @param key
     * @param values
     */
    public  boolean hashPutAll(final String key, final Map<String, Object> values) {
        try {
            redisTemplate.opsForHash().putAll(key, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 往Hash中存入多个数据 并添加过期时间
     *
     * @param key
     * @param map
     * @param time
     * @return
     */
    public  boolean hashPutAll(final String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取Hash中单个键的数据
     *
     * @param key  不能为null
     * @param item 不能为null
     * @return
     */
    public  Object hashGet(final String key, final String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hash所有k-v
     *
     * @param key
     * @return
     */
    public  Map<Object, Object> hashMGet(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hash所有value
     * @param key
     * @return
     */
    public  List<Object> hashValues(final String key){
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取hash所有的key
     * @param key
     * @return
     */
    public  Set<Object> hashKeys(final String key){
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取hash的长度
     *
     * @param key
     * @return
     */
    public  long hashSize(final String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key
     * @param items
     * @return
     */
    public  List<Object> hashMultiGet(final String key, final Collection<Object> items) {
        return redisTemplate.opsForHash().multiGet(key, items);
    }

    /**
     * 删除hash单个k-v元素
     * @param key
     * @param item
     * @return
     */
    public  long hashDelete(final String key,final String item){
        return redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 删除Hash中的多个k-v数据
     *
     * @param key
     * @param items
     * @return
     */
    public  long hashDelete(final String key, final Collection<Object> items) {
        return redisTemplate.opsForHash().delete(key, items);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hashIncrement(final String key, final String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hashDecrement(final String key, final String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


//----------------------------------------------集合Set用法-----------------------------------------------------------

    /**
     * 往Set中存入数据
     *
     * @param key
     * @param values
     * @return 成功个数
     */
    public  long setAdd(final String key, final Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 往Set中存入数据并设置过期时间
     *
     * @param key
     * @param timeount
     * @param values
     * @return 成功个数
     */
    public  long setAddWithTimeLimit(final String key, long timeount, final Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (timeount > 0) expire(key, timeount);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key
     * @return
     */
    public  long setSize(final String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 删除Set中的数据
     *
     * @param key
     * @param values
     * @return
     */
    public  long setRemove(final String key, final Object... values) {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key
     * @return
     */
    public  Set<Object> setGetAll(final String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询set中是否存在value元素
     *
     * @param key
     * @param value
     * @return
     */
    public  boolean setHasKey(final String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从set中随机弹出一个元素并移除
     * @param key
     * @return
     */
    public  Object setPop(final String key){
        return redisTemplate.opsForSet().pop(key);
    }


    //----------------------------------------------有序集合用法-----------------------------------------------------------


    /**
     * zset添加一个元素
     * @param key
     * @param value
     * @param score
     * @return
     */
    public  boolean zsetAdd(final String key,Object value,double score){
        Boolean addSuccess = redisTemplate.opsForZSet().add(key, value, score);
        return addSuccess != null && addSuccess;
    }
    /**
     * zset删除一个元素
     * @param key
     * @param value
     * @return
     */
    public  long zsetRemove(final String key,Object value){
        Long remove = redisTemplate.opsForZSet().remove(key, value);
        return remove == null ? 0 : remove;
    }
    /**
     * 往ZSet中存入多个元素
     *
     * @param key
     * @param values
     * @return
     */
    public  long zsetAdd(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().add(key, values);
        return count == null ? 0 : count;
    }
    /**
     * 删除ZSet中的多个元素
     *
     * @param key
     * @param values
     * @return
     */
    public  long zsetRemove(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }
    /**
     * 返回有序集中指定成员的排名
     * @param key
     * @param value
     * @return
     */
    public  long zsetRank(final String key,Object value){
        Long rank = redisTemplate.opsForZSet().rank(key, value);
        return rank;
    }
    /**
     * 返回有序集合成指定区间内的成员
     * @param key
     * @param start
     * @param end
     * @return
     */
    public  Set<Object> zsetRange(final String key,long start,long end){
        return redisTemplate.opsForZSet().range(key,start,end);
    }
    /**
     * 有序集合的长度
     * @param key
     * @return
     */
    public  long zsetSize(final String key){
        Long size = redisTemplate.opsForZSet().size(key);
        return size == null ? 0 : size;
    }
    /**
     * 修改有序集合中指定元素的分值
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public  double zsetIncrementScore(final String key,Object value,double delta){
        BoundZSetOperations boundZSetOperations = redisTemplate.boundZSetOps(key);
        Double aDouble = boundZSetOperations.incrementScore(value, delta);
        return aDouble;
    }



    //----------------------------------------------list用法-----------------------------------------------------------

    /**
     * 往列表的右边存入一个对象数据
     *
     * @param key
     * @param value
     * @return
     */
    public  long listRightPush(final String key, final Object value) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 往列表的右边存入多个对象数据
     *
     * @param key
     * @param values
     * @return
     */
    public  long listRightPushAll(final String key, final Collection<Object> values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 往列表的右边存入多个对象数据
     *
     * @param key
     * @param values
     * @return
     */
    public  long listRightPushAll(final String key, final Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 往列表的右边存入多个对象数据
     *
     * @param key
     * @param value
     * @return
     */
    public  long listRightPushAll(final String key, List<Object> value) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 往列表的右边存入多个对象数据,并设置过期时间
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    public  long listRightPushAll(final String key, List<Object> value, long time) {
        try {
            Long pushLength = redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return pushLength;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从List中获取begin到end之间的元素
     *
     * @param key
     * @param start
     * @param end   （start=0，end=-1表示获取全部元素）
     *              0      1      2     3     4
     *              item1 item2 item3 item4 item5
     *              -5     -4    -3    -2    -1
     * @return
     */
    public  List<Object> listGet(final String key, final int start, final int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list的长度
     *
     * @param key
     * @return
     */
    public  long listGetSize(final String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过index 获取list中的值
     *
     * @param key
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public  Object listGetByIndex(final String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key
     * @param index
     * @param value
     * @return
     */
    public  boolean listUpdateByIndex(final String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 在list中移除count个值为value的元素
     * @param key
     * @param count
     * count> 0：删除等于从头到尾移动的值的元素。
     * count <0：删除等于从尾到头移动的值的元素。
     * count = 0：删除等于value的所有元素。
     * @param value
     * @return
     */
    public  long listRemove(final String key,long count,Object value) {
        try {
            Long removeLength = redisTemplate.opsForList().remove(key, count, value);
            return removeLength;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * list左边弹出一个元素，并将弹出的值返回
     * @param key
     * @return
     */
    public  Object listLeftPop(final String key){
        return redisTemplate.opsForList().leftPop(key);
    }



    //----------------------------------------------其他用法-----------------------------------------------------------
    //scan用法
    public Set<String> keys(String keyPrefix) {
        String realKey = "*" + keyPrefix + "*";
        try {
            return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(realKey).count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
