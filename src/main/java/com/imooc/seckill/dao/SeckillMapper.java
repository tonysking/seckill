package com.imooc.seckill.dao;

import com.imooc.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface SeckillMapper {
    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime 执行秒杀的时间
     * @return 表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     * 根据ID查询秒杀对象
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);
    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offet
     * @param limit 偏移量之后取多少条记录
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offet, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramsMap
     */
    void killByProcedure(Map<String,Object> paramsMap);
}
