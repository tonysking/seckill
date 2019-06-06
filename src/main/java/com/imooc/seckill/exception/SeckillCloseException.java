package com.imooc.seckill.exception;

/**
 * 秒杀关闭异常
 * 原因：秒杀关闭，库存不足...
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
