package com.imooc.seckill.dao;

import com.imooc.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessKilledMapper {
    /**
     * 插入购买明细，可过滤重复
     * seckillId和userPhone组成联合主键
     *
     * @param seckillId
     * @param userPhone
     * @return 插入影响的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀对象实体
     *
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
}
