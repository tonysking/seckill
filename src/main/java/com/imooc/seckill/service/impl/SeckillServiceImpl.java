package com.imooc.seckill.service.impl;

import com.imooc.seckill.dao.SeckillMapper;
import com.imooc.seckill.dao.SuccessKilledMapper;
import com.imooc.seckill.dao.cache.RedisDao;
import com.imooc.seckill.entity.Seckill;
import com.imooc.seckill.entity.SuccessKilled;
import com.imooc.seckill.enums.SeckillStateEnum;
import com.imooc.seckill.exception.RepeatKillException;
import com.imooc.seckill.exception.SeckillCloseException;
import com.imooc.seckill.exception.SeckillException;
import com.imooc.seckill.model.Exposer;
import com.imooc.seckill.model.SeckillExecution;
import com.imooc.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Autowired
    private RedisDao redisDao;

    //md5盐字符串， 用于混淆md5
    private final String salt = "f**kYouEvery!!!";

    @Override
    public List<Seckill> getAll() {
        return seckillMapper.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    // 暴露接口---控制秒杀开始时间
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 缓存优化: 超时的基础上维护一致性
        // 1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 2.访问数据库
            seckill = seckillMapper.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                // 3.放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //秒杀未开始或已结束
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //加密
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckllId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        // 用户md5加密信息不匹配
        if (md5 == null || !md5.equals(getMD5(seckllId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存 + 记录购买行为
        // ------简单优化： 先记录购买行为， 再减库存
        Date nowTime = new Date();
        try {
            //记录购买行为
            int insertCount = successKilledMapper.insertSuccessKilled(seckllId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else {
                //减库存---热点商品竞争
                int updateCount  = seckillMapper.reduceNumber(seckllId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录, 秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckllId, userPhone);
                    return new SeckillExecution(seckllId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //所有编译器异常转换为  运行期异常(spring声明式事务会帮助rollback)
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        System.out.println("秒杀时间："+killTime);
        // 执行存储过程，result被赋值
        try {
            seckillMapper.killByProcedure(map);
            // 获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
