!function(global, document) {
	global._$ = function(str) {
		return document.querySelector(str);
	}
	var btn = document.createElement('div');
	btn.className = 'btn';
	_$('body').appendChild(btn);
	_$('.btn').innerHTML = '<div class="btn-con"><a class="btn_show">显示</a><a class="btn_hide">隐藏</a><a class="btn_reload">刷新</a><a href="mobile.html">返回</a></div>';
	var ad = _$('[name=mjgg_adv]');
	_$('.btn_show').onclick = function() {
		ad.style.display = 'block';
	}
	_$('.btn_hide').onclick = function() {
		ad.style.display = 'none';
	}
	_$('.btn_reload').onclick = function() {
		location.reload();
	}
}(this, document);