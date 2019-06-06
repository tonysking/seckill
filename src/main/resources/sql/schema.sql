-- 创建数据库seckill
CREATE DATABASE seckill;

-- 使用数据库
USE seckill;

-- 创建秒杀库存表
CREATE TABLE seckill (
  `seckill_id` bigint       NOT NULL AUTO_INCREMENT  COMMENT '商品库存ID',
  `name`       VARCHAR(120) NOT NULL  COMMENT '商品名称',
  `number`     INT          NOT NULL  COMMENT '库存数量',
  `start_time` TIMESTAMP    NOT NULL  COMMENT '秒杀开始时间',
  `end_time`   TIMESTAMP    NOT NULL  COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
  -- AUTO_INCREMENT=1000，自增ID从1000开始
)  ENGINE = InnoDB
  AUTO_INCREMENT = 1000  DEFAULT CHARSET = utf8  COMMENT ='秒杀库存表';

-- 初始化数据
INSERT INTO
  seckill (name, number, start_time, end_time)
VALUES
  ('6000元秒杀iPhoneX', 100, '2019-11-01 00:00:00', '2019-11-02 00:00:00'),
  ('8000元秒杀iPad', 200, '2019-11-01 00:00:00', '2019-11-02 00:00:00'),
  ('10000元秒杀MacBook Pro', 300, '2019-11-01 00:00:00', '2019-11-02 00:00:00'),
  ('120000元秒杀Tesla Model S', 400, '2019-11-01 00:00:00', '2019-11-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关的信息
CREATE TABLE success_killed (
  `seckill_id` bigint    NOT NULL  COMMENT '秒杀商品id',
  `user_phone` bigint    NOT NULL  COMMENT '用户手机号',
  `state`      tinyint   NOT NULL DEFAULT -1  COMMENT '状态标识：-1 无效 0 成功 1 已付款 2 已发货',
  `create_time` TIMESTAMP NOT NULL  COMMENT '创建时间',
  PRIMARY KEY (seckill_id, user_phone),/*联合主键 防止同一用户重复秒杀*/
  KEY idx_create_time(create_time)
)  ENGINE = InnoDB  DEFAULT CHARSET = utf8  COMMENT ='秒杀成功明细表';