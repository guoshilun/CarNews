!function(global, document, window) {
	var protocol = location.protocol;
	var readyRE = /complete|loaded|interactive/;
	var urls = [
			[50, protocol+'//mj-public.oss-cn-shanghai.aliyuncs.com/jssdk/v1/mj_adv_prd.js'],
			[100, protocol+'//mj-public.oss-cn-shanghai.aliyuncs.com/jssdk/v1/mj_adv_prd_170323.js']
		];
	var random = Math.floor(Math.random()*100);
	var url;
	for (var i = 0,len = urls.length; i < len; i++) {
		var n = urls[i];
		var max = n[0];
		var min = i? urls[i-1][0]: 0;
		if (random >= min && random < max) {
			url = n[1];
			console.log(random, i);
		}
	}
	var ready = function(fn) {
		if (readyRE.test(document.readyState) && document.body) fn()
		else document.addEventListener('DOMContentLoaded', function() {fn()});
	}
	ready(function() {
		var script = document.createElement('script');
		script.src = url;
		document.head.appendChild(script);
	});
}(this, document, window);
