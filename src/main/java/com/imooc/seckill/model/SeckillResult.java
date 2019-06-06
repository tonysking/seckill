package com.imooc.seckill.model;

import lombok.Data;

/**
 * 所有Ajax请求返回类型，封装json
 * @param <T>
 */
@Data
public class SeckillResult<T> {

    private boolean success;
    private T data;
    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
