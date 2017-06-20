!function(global, document) {
	var $ = function(str) {
		return document.querySelectorAll(str);
	};
	var page_request_id = {};
	var protocol = location.protocol;
	var head     = $('head')[0];
	var API      = protocol+'//r.mjmobi.com/jsv1?callback=';
	var formatParams = function(data, arr) {
		for (var name in data) {
			arr.push(encodeURIComponent(name) + '=' + encodeURIComponent(data[name]));
		}
		return arr.join('&');
	};
	// 初始化样式加载
	var CSS = function() {
		var css = document.createElement('link');
		css.type = 'text/css';
		css.rel = 'stylesheet';
		css.href = protocol + '//mj-public.oss-cn-shanghai.aliyuncs.com/jssdk/v1/mj_adv_prd.css';
		css.id = 'mjcss';
		return css;
	};
	var AJAX = function(opts) {
		var params = formatParams(opts.data, []);
		// 创建 script 标签并加入到页面中
		var callbackName = ('jsonp_' + Math.random()).replace('.', '');
		opts.data[opts.success] = callbackName;
		var oS = document.createElement('script');
		head.appendChild(oS);
		// 创建jsonp回调函数
		window[callbackName] = function (json) {
			head.removeChild(oS);
			clearTimeout(oS.timer);
			window[callbackName] = null;
			opts.success && opts.success(json);
		};
		// 发送请求
		oS.src = opts.url + callbackName + '&' + params;
	};
	// 模板引擎
	var FST = function(html, options) {
		var re = /<%([^%>]+)?%>/g,
			reExp = /(^( )?(if|for|else|switch|case|break|{|}))(.*)?/g,
			code = 'var r=[];\n',
			cursor = 0,
			match;
		var add = function(line, js) {
			js? (code += line.match(reExp) ? line + '\n' : 'r.push(' + line + ');\n') :
				(code += line != '' ? 'r.push("' + line.replace(/"/g, '\\"') + '");\n' : '');
			return add;
		}
		while(match = re.exec(html)) {
			add(html.slice(cursor, match.index))(match[1], true);
			cursor = match.index + match[0].length;
		}
		add(html.substr(cursor, html.length - cursor));
		code += 'return r.join("");';
		// console.log(code);
		return new Function(code.replace(/[\r\t\n]/g, '')).apply(options);
	};
	var TEMPOR = {
		as: '<a href="<%this.u%>" target="_blank">',	// a标签开始
		ae: '</a>',										// a标签结束
		t: '<b<%this.tc%>><%this.t%></b><span<%this.dc%>><%this.d%></span>',	// 文字(标题|描述)
		p: '<p><%this.c%></p>',							// 文字(详情)
		i: '<img src="<%this.i%>">'						// 图片
	};
	var TEMP = {
		st: '<div class="mg_c"<%this.bc%>>'+TEMPOR.as,
		ed: [TEMPOR.ae+'</div>',
			'<%if(this.kp){%>',
				'<a id="mg_count"></a>',
			'<%}else if(!this.co){%>',
				'<a class="mg_cl"></a>',
			'<%}%>',
			'<%if(!this.kp){%><em class="mg_ad">广告</em><%}%>',
			].join(''),
		// banner悬浮(微信|图文)
		baF: TEMPOR.i+TEMPOR.t,
		// 插屏(图文)
		inT: TEMPOR.i+TEMPOR.t+TEMPOR.ae+TEMPOR.as+TEMPOR.p,
		// banner嵌入(图文)
		baT: TEMPOR.i+TEMPOR.t,
		// banner插屏大图
		img: TEMPOR.i,
		// 文字链
		txt: TEMPOR.t,
		fu: '<i style="background-image:url(<%this.i%>)"></i>'
	};
	// 核心代码
	var MJGG = {
		// 初始化
		init: function() {
			var me = this;
			me._init = 0;
			var MJID = $('[name=mjgg_adv]');
			var LEN = MJID.length;
			if (!LEN) return false;
			var css = $('#mjcss')[0];
			if (!css) head.appendChild(CSS());
			for (var i = 0; i < LEN; i++) {
				var NODE = MJID[i];
				var code = NODE.getAttribute('data-code');
				var prid = page_request_id[code] || '';
				// var count = ~~(ID.attr('data-count')) || 1;
				// count = count > 1? 1: count;
				var o = {
					adloc_code: NODE.getAttribute('data-code'),
					ad_count: 1,
					domain: location.hostname,
					js_key: NODE.getAttribute('data-key'),
					wx: typeof global.WeixinJSBridge === 'object' && typeof global.WeixinJSBridge.invoke === 'function'? 1: 0,
					version: '1.1'
				}
				if (prid) o.page_request_id = prid;
				me.get(o, NODE, LEN);
			}
		},
		ready: function(fn) {
			var readyRE = /complete|loaded|interactive/;
			if (readyRE.test(document.readyState) && document.body) {
				fn();
			} else {
				document.addEventListener('DOMContentLoaded', function() {
					fn();
				}, false);
			}
		},
		get: function(o, node, len) {
			var me = this;
			AJAX({
				url: API,
				data: o,
				success: function(data) {
					// data = ddd[17];
					me._init++;
					if (data.code === 100 && data.imp_ads.length) {
						me.analysis(data, node);
					}
					if (me._init === len) {
						// console.log('end');
						me.end();
					}
				}
			});
		},
		end: function() {
			var close = $('.mg_cl,#mg_count');
			for (var i = 0, l = close.length; i < l; i++) {
				var e = close[i];
				e.onclick = function() {
					var ad = e.parentNode;
					var pa = ad.parentNode;
					pa.removeChild(ad);
				}
			}
		},
		// 分析重组数据
		analysis: function(o, node) {
			page_request_id[o.adloc_code] = o.page_request_id;
			var ads  = o.imp_ads[0];
			var img = document.createElement('img');
			var ba = ads.banner_ads;
			var el = ba.elements = JSON.parse(ba.elements);
			var id = ba.template_id;
			var obj  = {
				c: el.content || '',			// 内容
				d: el.desc || '',				// 描述
				i: el.icon || el.image || '',	// 图标图片
				m: id,							// 模板ID
				t: el.title || '',				// 标题
				u: ba.click_url					// 链接
			};
			img.src = o.show_url + '&seqs=' + ads.impression_seq_no;	// 曝光
			var filter = this.filter(id, obj, node);
			var type = filter[0];
			obj = filter[1];
			if (type in TEMP) {
				var temp = FST(TEMP.st+TEMP[type]+TEMP.ed, obj);
				node.innerHTML = temp;
			}
		},
		/*
		 * 模板过滤(筛选)
		 *
		 * banner悬浮(微信|图文): 1, 21		80x80
		 * banner悬浮(微信|大图): 3, 22		640x100
		 * 插屏(图文): 5					160x160
		 * 插屏(大图): 7					600x500
		 * banner嵌入(大图): 9, 25			640x200
		 * banner嵌入(图文): 11, 26			640x200
		 * 开屏: 17							全屏
		 * 文字链 悬浮: 23					640x100
		 * 文字链 嵌入: 24					640x100
		 *
		 */
		filter: function(id, obj, node) {
			var type, z, p = '';
			// banner悬浮(微信|图文)
			if (id == 1 || id == 21) {
				type = 'baF';
				z = 'mg_1';
			// banner插屏大图
			} else if (id == 3 || id == 7 || id == 9 || id == 22 || id == 25 || id == 401 || id == 402 || id == 403 || id == 404 || id == 405 || id == 406) {
				type = 'img';
				z = id == 7? 'mg_7': 'mg_3';
			// 插屏(图文)
			} else if (id == 5) {
				type = 'inT';
				z = 'mg_5';
			// banner嵌入(图文)
			} else if (id == 11 || id == 26) {
				type = 'baT';
				z = 'mg_11';
			// 文字链
			} else if (id == 23 || id == 24) {
				type = 'txt';
				z = 'mg_23';
				var bc = node.getAttribute('data-bgcolor');
				var tc = node.getAttribute('data-titlecolor');
				var dc = node.getAttribute('data-desccolor');
				if (bc) obj.bc = ' style="background:'+bc+'"';
				if (tc) obj.tc = ' style="color:'+tc+'"';
				if (dc) obj.dc = ' style="color:'+dc+'"';
			// 开屏
			} else if (id == 17) {
				type = 'fu';
				z = 'mg_17';
				obj.kp = 1;
				setTimeout(function() {
					var btn = $('#mg_count')[0];
					if (btn) btn.onclick();
				}, 3100);
			}
			// 悬浮
			if (id == 1 || id == 3 || id == 21 || id == 22 || id == 23 || id == 401 || id == 402 || id == 403) {
				var po = { top: 1, bottom: 1 };
				var ps = node.getAttribute('data-pos');
				p = ' mg_'+(ps in po? ps: 'top');
			}
			obj.co = node.getAttribute('data-close') == 'false'? 1: 0;
			node.className = 'mjgg_b ' + z + p;
			return [type, obj];
		}
	};
	MJGG.ready(function() {
		MJGG.init();
		var sss = setInterval(function() {
			MJGG.init();
		}, 30000);
		setTimeout(function() {
			clearInterval(sss);
		}, 1800000);
	})
}(this, document);