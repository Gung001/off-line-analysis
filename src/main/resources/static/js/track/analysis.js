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
        }
    }

    /**
     * 主体，其实就是tracker js
     * @type {{}}
     */
    var tracker = { 

        startSession: function () {

            // 加载js就触发的方法
        },

        onPageView: function () {

            // 触发page view 事件
        },

        onChargeRequest: function () {

            // 触发订单产生事件
        },

        onEventDuration: function () {
            // chu触发event事件
        },

        sendDataToServer: function (data) {
            // 发送数据到服务器
        }
    }
})()