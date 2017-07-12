/**
  *广告页面所需的js，当广告页面直接放入WebView中显示时使用
  *
  */
//广告界面加载完成回调
function loadFinish(){
    <!--window.carnews.toast('浏览器宽高：'+document.getElementsByClassName('axd_container')[0].clientWidth+'\t'+document.getElementsByClassName('axd_container')[0].clientHeight);-->
    window.carnews.loadAdvFinish();
};
//获取当前广告页面的宽高
function getWidthHeight(){
    var w = document.getElementsByClassName('axd_container')[0].clientWidth;
    var h = document.getElementsByClassName('axd_container')[0].clientHeight;
    window.carnews.getAdvWidthHeight(w, h);
};
//显示当前广告页面
function show(){
    document.getElementById('adv_body').style.display="block";
};
//默认先隐藏广告页面，等加载完成后再显示
window.addEventListener('DomContentLoaded', function(){document.getElementById('adv_body').style.display="none";}, false);