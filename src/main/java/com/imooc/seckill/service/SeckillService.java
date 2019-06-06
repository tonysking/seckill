package com.imooc.seckill.service;

import com.imooc.seckill.entity.Seckill;
import com.imooc.seckill.exception.RepeatKillException;
import com.imooc.seckill.exception.SeckillCloseException;
import com.imooc.seckill.exception.SeckillException;
import com.imooc.seckill.model.Exposer;
import com.imooc.seckill.model.SeckillExecution;

import java.util.List;

/**
 * 站在 使用者 角度设计
 */
public interface SeckillService {

    //查询所有秒杀
    List<Seckill> getAll();

    //查询单个秒杀
    Seckill getById(long seckillId);

    //秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);

    //执行秒杀
    SeckillExecution executeSeckill(long seckllId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;
    //---优化：通过存储过程执行秒杀
    SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
