# redis
底层驱动有两个可选
- jedis
- lettuce

应用层有三个
- spring-data-redis
- spring-cache
- redisson

底层驱动通常选择lettuce,根据不同场景选择不同应用层组件, 这里选择spring-cache(缓存) 和 redisson(分布式锁)

## redisson

