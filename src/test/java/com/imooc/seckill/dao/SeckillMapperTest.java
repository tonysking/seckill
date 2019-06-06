package com.imooc.seckill.dao;

import com.imooc.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SeckillMapperTest {

    @Autowired
    SeckillMapper seckillMapper;

    @Test
    public void reduceNumber() {
        /*
            update
              seckill
            set
              number = number - 1
            where seckill_id = #{seckillId}
            and start_time <![CDATA[ <= ]]> #{killTime}
            and end_time >= #{killTime}
            and number > 0;

            start_time
            '2019-11-01 00:00:00'
            end_time
            '2019-11-02 00:00:00'
         */
        int updateCount = seckillMapper.reduceNumber(1000L, new Date());
        System.out.println("更新的记录：（0/1）"+updateCount);
    }

    @Test
    public void queryById() {
        Seckill seckill = seckillMapper.queryById(1000L);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> list = seckillMapper.queryAll(2,2);
        for(Seckill seckill:list){
            System.out.println(seckill);
        }
    }
}