package com.imooc.seckill.exception;

/**
 * 秒杀相关业务异常(总)
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
