!function() {
    var $ = function(str) {
        return document.querySelectorAll(str);
    };
    var AJAX = function(opts) {
        var params = formatParams(opts.data, []);
        // 创建 script 标签并加入到页面中
        var callbackName = ('jsonp_' + Math.random()).replace('.', '');
        opts.data[opts.success] = callbackName;
        var oS = document.createElement('script');
        head.appendChild(oS);
        // 创建jsonp回调函数
        global[callbackName] = function (json) {
            head.removeChild(oS);
            clearTimeout(oS.timer);
            global[callbackName] = null;
            opts.success && opts.success(json);
        };
        // 发送请求
        oS.src = opts.url + callbackName + '&' + params;
    };
    var AXD = {
        get: function(o, node, len) {
            var me = this;
            AJAX({
                url: API,
                data: o,
                success: function(data) {
                    me._init++;
                    if (data.code === 100 && data.imp_ads.length) {
                        me.analysis(data, node);
                    }
                    if (me._init === len) {
                        me.end();
                    }
                }
            });
        }// get
    }
}();
