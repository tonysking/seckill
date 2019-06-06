//存放主要交互逻辑JS代码
//javaScripts 模块化

var seckill = {
    //封装秒杀相关的ajax的url地址
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        killUrl: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    //验证手机号
    validatePhone: function (phone) {
        // isNaN判断是否非数字
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    // 获取秒杀地址，控制显示逻辑，执行秒杀
    handleSeckillkill: function (seckillId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');//按钮
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中，执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                //如果exposed为true，就显示开始秒杀按钮，并绑定点击事件
                if (exposer['exposed']) {
                    //获取秒杀MD5
                    var md5 = exposer['md5'];
                    //组装秒杀地址
                    var killUrl = seckill.URL.killUrl(seckillId, md5);
                    console.log('killUrl:' + killUrl);
                    //绑定一次点击事件，回调函数内容为执行秒杀
                    $('#killBtn').one('click', function () {
                        //1.禁用按钮
                        $(this).addClass('disable');
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                // alert(result['data'])
                                var stateInfo = killResult['stateInfo'];
                                //3.显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['startTime'];
                    var end = exposer['endTime'];
                    //重新计算计时逻辑
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result:' + result);
            }
        })
    },
    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        console.log(nowTime);
        //时间判断
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            // 秒杀未开始 ---使用计时事件绑定 （+1s 防止用户端计时过程中时间偏移）
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //控制时间格式
                var format = event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件
            }).on('finish.countdown', function () {
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handleSeckillkill(seckillId, seckillBox);
            });
        } else {
            // 秒杀开始
            seckill.handleSeckillkill(seckillId, seckillBox);
        }

    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证个登录, 计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            //验证手机号killPhone是否为空，为空则cookie中不存在，弹出登录框
            if (!killPhone) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true, // 显示弹出层
                    backdrop: 'static', // 禁止位置关闭（禁止点击其他位置关闭登陆框）
                    keyboard: false // 关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    //验证输入的手机号是否正确
                    if (seckill.validatePhone(inputPhone)) {
                        //手机号写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面---重新走一便detail.init
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }

            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result){
                console.log('now:' + seckill.URL.now());
                if (result && result['success']) {
                    var nowTime = result['data'];
                    console.log("nowTime:"+nowTime);
                    //时间判断
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    //输出到浏览器的console中，方便调试
                    console.log('result:' + result);
                }
            });
        }
    }
};