(function () {

    /**操作cookie工具类*/
    var CookieUtil = {
        /**
         * 读cookie
         * @param name key
         */
        get: function (name) {

            var cookieName = encodeURIComponent(name) + "=",
                cookieStart = document.cookie.indexOf(cookieName),
                cookieValue = null;

            if (cookieStart > -1) {
                var cookieEnd = document.cookie.indexOf(";", cookieStart);
                if (cookieEnd == -1) {
                    cookieEnd = document.cookie.length;
                }
                cookieValue = decodeURIComponent(document.cookie.substring(cookieStart, cookieEnd));
            }

            return cookieValue;
        },
        /**
         * 写Cookie
         * @param name key
         * @param value
         * @param expires 过期时间
         * @param path 路径
         * @param domain 域
         * @param secure
         */
        set: function (name, value, expires, path, domain, secure) {

            var cookieText = encodeURIComponent(name) + "=" + encodeURIComponent(value);

            if (expires) {
                var expiresTime = new Date();
                expiresTime.setTime(expires);
                cookieText += ";expires=" + expiresTime.toGMTString();
            }

            if (path) {
                cookieText += ";path=" + path;
            }

            if (domain) {
                cookieText += ";domain=" + domain;
            }

            if (secure) {
                cookieText += ";secure";
            }

            document.cookie = cookieText;
        },
        setExt: function (name, value) {
            this.set(name, value, new Date().getTime() + tracker.cookieExpiresTime, "/");
        }
    }

    /**
     * 主体，其实就是tracker js
     * @type {{}}
     */
    var tracker = {

        /**
         * 常量
         */
        clientConfig: {
            /**服务器地址*/
            serverUrl: "http://data01/BfImg.gif",
            /**session过期时间*/
            sessionTimeout: 360,//360s -> 6min
            /**当前页面最大等待时间*/
            maxWaitTime: 3600,// 3600s -> 60 min -> 1h
            ver: "1"
        },

        /**
         * cookie 过期时间 -> 10 year
         */
        cookieExpiresTime: 315360000000,

        /**
         * 发送到服务器的字段
         */
        columns: {
            // 发送到服务器的列名称
            eventName: "en",
            version: "ver",
            platform: "pl",
            sdk: "sdk",
            uuid: "u_ud",
            mid: "u_mid",
            sessionId: "u_sd",
            clientTime: "c_time",
            language: "l",
            userAgent: "b_iev",
            resolution: "b_rst",
            currentUrl: "p_url",
            referrerUrl: "p_ref",
            title: "tt",
            orderId: "oid",
            orderName: "on",
            currencyAmount: "cua",
            currencyType: "cut",
            paymentType: "pt",
            category: "ca",
            action: "ac",
            kv: "kv_",
            duration: "du"
        },

        /**
         * 写cookie的字段
         */
        keys: {
            pageView: "e_pv",
            chargeRequestEvent: "e_crt",
            launch: "e_l",
            eventDurationEvent: "e_e",
            sid: "tracker_sid",
            uuid: "tracker_uuid",
            mid: "tracker_mid",
            /*前一次访问时间*/
            preVisitTime: "tracker_pre_visit_time"
        },

        /**
         * 返回全部event的名称数组
         */
        getEventKeys: function () {

            return [
                this.keys.pageView,
                this.keys.chargeRequestEvent,
                this.keys.launch,
                this.keys.eventDurationEvent,
                this.keys.sid
            ];
        },

        /**
         * 获取会员id
         */
        getMid: function () {
            return CookieUtil.get(this.keys.mid);
        },

        /**
         * 获取会员id
         */
        setMid: function (mid) {
            return CookieUtil.set(this.keys.mid, mid);
        },

        /**
         * 获取会话id
         */
        getSid: function () {
            return CookieUtil.get(this.keys.sid);
        },

        /**
         * 设置sid
         */
        setSid: function (sid) {
            if (sid) {
                CookieUtil.setExt(this.keys.sid, sid);
            }
        },

        /**
         * 获取uuid
         */
        getUuid: function () {
            return CookieUtil.get(this.keys.uuid);
        },

        /**
         * 设置uuid
         */
        setUuid: function (uuid) {
            if (uuid) {
                CookieUtil.setExt(this.keys.uuid, uuid);
            }
        },

        /**
         * 产生uuid
         */
        generateId: function() {
            var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
            var tmpid = [];
            var r;

            tmpid[8] = tmpid[13] = tmpid[18] = tmpid[23] = '-';

            tmpid[14] = '4';

            for (i = 0; i < 36; i++) {

                if (!tmpid[i]) {

                    r = 0 | Math.random() * 16;
                    tmpid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
            return tmpid.join('');
        },

        /**
         * 参数编码返回字符串
         */
        parseParam: function(data) {

            var params = "";

            for (var e in data) {
                if (e && data[e]) {
                    params += encodeURIComponent(e) + "=" + encodeURIComponent(data[e]) + "&";
                }
            }

            if (params) {

                return params.substring(0, params.length - 1);
            } else {

                return params;
            }
        },

        startSession: function () {
            // 加载js就触发的方法
            if (this.getSid()) {
                // 会话id存在，表示uuid也存在
                if (this.isSessionTimeout()) {
                    // 会话过期，产生新的会话
                    this.createNewSession();
                } else {
                    // 会话没有过期，更新最近的访问时间
                    this.updatePreVisitTime(new Date().getTime());
                }
            } else {
                // 会话id不存在，不是uuid也不存在
                this.createNewSession();
            }

            this.onPageView();
        },

        /**
         * 触发page view 事件
         */
        onPageView: function () {

            if (this.preCallApi()) {

                var pageviewEvent = {};

                pageviewEvent[this.columns.eventName] = this.keys.pageView;
                // 设置当前url
                pageviewEvent[this.columns.currentUrl] = window.location.href;
                // 设置前一个页面的url
                pageviewEvent[this.columns.referrerUrl] = document.referrer;
                // 设置title
                pageviewEvent[this.columns.title] = document.title;
                this.setCommonColumns(pageviewEvent);

                this.sendDataToServer(this.parseParam(pageviewEvent));

                this.updatePreVisitTime(new Date().getTime());
            }

        },

        /**
         * 触发订单产生事件
         */
        onChargeRequest: function(orderId, name, currencyAmount, currencyType, paymentType) {
            if (this.preCallApi()) {
                if (!orderId || !currencyType || !paymentType) {
                    this.log("订单id、货币类型以及支付方式不能为空");
                    return ;
                }

                if (typeof(currencyAmount) != "number") {
                    this.log("订单金额必须是数字");
                    return ;
                }

                var chargeRequestEvent = {};

                chargeRequestEvent[this.columns.eventName] = this.keys.chargeRequestEvent;
                chargeRequestEvent[this.columns.orderId] = orderId;
                chargeRequestEvent[this.columns.orderName] = name;
                chargeRequestEvent[this.columns.currencyAmount] = currencyAmount;
                chargeRequestEvent[this.columns.currencyType] = currencyType;
                chargeRequestEvent[this.columns.paymentType] = paymentType;
                // 设置公用columns
                this.setCommonColumns(chargeRequestEvent);

                this.sendDataToServer(this.parseParam(chargeRequestEvent)); // 最终发送编码后的数据ss
                this.updatePreVisitTime(new Date().getTime());
            }
        },

        /**
         * 触发event事件
         */
        onEventDuration: function(category, action, map, duration) {
            if (this.preCallApi()) {
                if (!(category && action)) {
                    this.log("category和action不能为空");
                    return;
                }

                var event = {};

                event[this.columns.eventName] = this.keys.eventDurationEvent;
                event[this.columns.category] = category;
                event[this.columns.action] = action;
                if (map) {
                    for (var k in map) {
                        if (k && map[k]) {
                            event[this.columns.kv + k] = map[k];
                        }
                    }
                }
                if (duration) {
                    event[this.columns.duration] = duration;
                }

                // 设置公用columns
                this.setCommonColumns(event);
                this.sendDataToServer(this.parseParam(event));
                this.updatePreVisitTime(new Date().getTime());
            }
        },

        /**
         * 触发onlaunch事件
         */
        onLaunch: function () {
            var launch = {};

            // 设置事件名称
            launch[this.columns.eventName] = this.keys.launch;
            // 设置公共columns
            this.setCommonColumns(launch);
            // 最终发送编码后的数据
            this.sendDataToServer(this.parseParam(launch));
        },

        /**
         * 发送数据到服务器
         * @param data
         */
        sendDataToServer: function (data) {

            // data 是一个字符串 name=value&name=value...
            var that = this;
            var i2 = new Image(1, 1);
            i2.onerror = function (ev) {
                // 重试？
            }

            i2.src = that.clientConfig.serverUrl + "?" + data;
        },

        /**
         * 执行对外方法前必须执行的方法
         */
        preCallApi: function () {

            if (this.isSessionTimeout()) {
                // 如果session过期需要新建一个session
                this.startSession();
            } else {
                // 更新最近访问时间
                this.updatePreVisitTime(new Date().getTime());
            }
            return true;
        },

        /**
         * 设置公共的字段
         */
        setCommonColumns: function(data) {
            data[this.columns.version] = this.clientConfig.ver;
            data[this.columns.platform] = "website";
            data[this.columns.sdk] = "js";
            // 设置用户id
            data[this.columns.uuid] = this.getUuid();
            // 设置会员id
            data[this.columns.mid] = this.getMid();
            // 设置sid
            data[this.columns.sessionId] = this.getSid();
            // 设置客户端时间
            data[this.columns.clientTime] = new Date().getTime();
            // 设置浏览器语言
            data[this.columns.language] = window.navigator.language;
            // 设置浏览器类型
            data[this.columns.userAgent] = window.navigator.userAgent;
            // 设置浏览器分辨率
            data[this.columns.resolution] = screen.width + "*" + screen.height;
        },

        /**
         * session是否过期
         */
        isSessionTimeout: function () {
            var time = new Date().getTime();
            // 上次访问时间
            var preTime = CookieUtil.get(this.keys.preVisitTime);
            if (preTime) {
                // 最近访问时间存在，就与上次访问时间相减 再 和session过期时间比较
                return time - preTime > this.clientConfig.sessionTimeout * 1000;
            }
            // 不存在上次访问时间，直接过期
            return true;
        },

        /**
         * 更新上一次访问时间
         */
        updatePreVisitTime: function (time) {
            CookieUtil.setExt(this.keys.preVisitTime, time);
        },

        /**
         * 创建新的session
         */
        createNewSession: function () {

            var time = new Date().getTime();

            // 进行会话更新操作
            var sid = this.generateId();// 产生一个sessionid
            this.setSid(sid);
            this.updatePreVisitTime(time);

            // 进行uuid查看操作
            if (!this.getUuid()) {
                // 不存在uuid，先创建uuid，然后保存到cookie，最后触发launch事件
                var uuid = this.generateId();// 生成uuid
                this.setUuid(uuid);
                this.onLaunch();
            }
        },

        /**
         * 日志打印
         * @param msg
         */
        log:function (msg) {
            console.log("异常：" + msg);
        }

    };

    window.__AE__ = {
        startSession:function () {
            tracker.startSession();
        }
    }

    window.__AE__ = {
        startSession: function() {
            tracker.startSession();
        },
        onPageView: function() {
            tracker.onPageView();
        },
        onChargeRequest: function(orderId, name, currencyAmount, currencyType, paymentType) {
            tracker.onChargeRequest(orderId, name, currencyAmount, currencyType, paymentType);
        },
        onEventDuration: function(category, action, map, duration) {
            tracker.onEventDuration(category, action, map, duration);
        },
        setMid: function(mid) {
            tracker.setMid(mid);
        }
    };


    // 自动加载方法
    var autoLoad = function() {
        // 进行参数设置
        var _aelog_ = _aelog_ || window._aelog_ || [];
        var mid = null;

        for (i=0;i<_aelog_.length;i++) {
            _aelog_[i][0] === "mid" && (mid = _aelog_[i][1]);
        }

        // 根据是给定mid，设置mid的值
        mid && __AE__.setMid(mid);

        // 启动session
        __AE__.startSession();
    };

    autoLoad();
})()