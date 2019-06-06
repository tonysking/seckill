package com.imooc.seckill.dao;

import com.imooc.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SuccessKilledMapperTest {

    @Autowired
    SuccessKilledMapper successKilledMapper;

    @Test
    public void insertSuccessKilled() {
        /*
            第一次：insertCount：1
            第二次：insertCount：0（不允许重复插入）

            <!-- 主键冲突时，报错，忽略错误，返回0 -->
            insert ignore into success_killed(seckill_id,user_phone)
            values (#{seckillId},#{userPhone})

            PRIMARY KEY (seckill_id, user_phone),/*联合主键 防止同一用户重复秒杀
         */
        int insertCount = successKilledMapper.insertSuccessKilled(1002L,13027062000L);
        System.out.println("insertCount:(1/0):"+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(1002L,13027062000L);
        System.out.println(successKilled);
    }
}