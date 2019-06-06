package com.imooc.seckill.service;

import com.imooc.seckill.dao.SeckillMapper;
import com.imooc.seckill.entity.Seckill;
import com.imooc.seckill.exception.RepeatKillException;
import com.imooc.seckill.exception.SeckillCloseException;
import com.imooc.seckill.model.Exposer;
import com.imooc.seckill.model.SeckillExecution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class SeckillServiceTest {

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getAll() {
        List<Seckill> list = seckillService.getAll();
        log.info("list={}",list);
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000L);
        log.info("seckill={}",seckill);
    }


    /*
        单独测试秒杀逻辑的两部分
     */

    @Test
    public void exportSeckillUrl() {
        Exposer exposer = seckillService.exportSeckillUrl(1000L);
        log.info("exposer={}",exposer);
        /*
            exposer=Exposer(
            exposed=true,
            md5=60d03a5a06e6c112d03f627c42f7ef7c,
            seckillId=1000,
            nowTime=0, startTime=0, endTime=0)S
         */
    }

    @Test
    public void executeSeckill() {
        String md5 = "60d03a5a06e6c112d03f627c42f7ef7c";
        //重复秒杀会抛异常：com.imooc.seckill.exception.RepeatKillException: seckill repeated
        //主动try catch
        try {

            SeckillExecution seckillExecution = seckillService.executeSeckill(1000L, 13027062000L, md5);
            log.info("seckillExecution={}",seckillExecution);
        } catch (RepeatKillException e){
            log.error(e.getMessage());
        } catch (SeckillCloseException e){
            log.error(e.getMessage());
        }
    }


    /*
        测试秒杀完整逻辑  注意可重复性
     */
    @Test
    public void testsSeckill() {
        long seckillId = 1002L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            //秒杀开启
            log.info("exposer={}",exposer);
            String md5 = exposer.getMd5();
            try {

                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, 13027062003L, md5);
                log.info("seckillExecution={}",seckillExecution);
            } catch (RepeatKillException e){
                log.error(e.getMessage());
            } catch (SeckillCloseException e){
                log.error(e.getMessage());
            }
        } else {
            //秒杀未开启  警告
            log.warn("exposer={}",exposer);
        }

    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1002L;
        long phone = 13027062002L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            log.info(execution.getStateInfo() + execution.getState());
        }
    }

    @Autowired
    private SeckillMapper seckillMapper;
    @Test
    public  void fuck(){
//        Date killTime = new Date();
        Timestamp killTime = new Timestamp(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", 1002L);
        map.put("phone", 13027062004L);
        map.put("killTime", killTime);
        map.put("result", null);
        System.out.println("秒杀时间："+killTime);
        // 执行存储过程，result被赋值
        seckillMapper.killByProcedure(map);
        // 获取result
        int result = MapUtils.getInteger(map, "result", -2);
        System.out.println(result);
    }
}