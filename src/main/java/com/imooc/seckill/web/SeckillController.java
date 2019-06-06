package com.imooc.seckill.web;

import com.imooc.seckill.entity.Seckill;
import com.imooc.seckill.enums.SeckillStateEnum;
import com.imooc.seckill.exception.RepeatKillException;
import com.imooc.seckill.exception.SeckillCloseException;
import com.imooc.seckill.model.Exposer;
import com.imooc.seckill.model.SeckillExecution;
import com.imooc.seckill.model.SeckillResult;
import com.imooc.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    // 秒杀列表
    @GetMapping("list")
    public String list(Model model) {
        List<Seckill> list = seckillService.getAll();
        model.addAttribute("list", list);
        return "list";
    }

    // 秒杀详情
    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // 秒杀接口暴露
    @PostMapping(value = "/{seckillId}/exposer", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            // 暴露成功
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            // 暴露失败
            log.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    // 执行秒杀
    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        // ---若用户信息较复杂，可使用springMVC valid验证
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;

        // ---优化 使用存储过程执行秒杀
        SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
        return new SeckillResult<SeckillExecution>(true, execution);


       /* try {
            // 执行成功
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            // 重复秒杀
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        }catch (SeckillCloseException e) {
            // 秒杀结束
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        }catch (Exception e) {
            // 内部错误
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }*/
    }

    // 获取当前日期
    @GetMapping("/time/now")
    @ResponseBody // ---返回JSON数据 否则无法显示倒计时
    public SeckillResult<Long> now() {
        Date date = new Date();
        return new SeckillResult<Long>(true, date.getTime());
    }
}
