/**
  *广告页面所需的js，被父页面以IFrame形式嵌入时使用
  *
  */

var parentWindow;//父页面的window对象
var isLoaded = 0;//是否当前页面已加载完成
//设置父窗口window对象
function setParentWindow(pw){
    parentWindow = pw;
    pw.toast();
    if (isLoaded != 0){
        parentWindow.loadAdvFinish();
    }
};
//广告界面加载完成回调
function loadFinish(){
    <!--window.carnews.toast('浏览器宽高：'+document.getElementsByClassName('axd_container')[0].clientWidth+'\t'+document.getElementsByClassName('axd_container')[0].clientHeight);-->
    isLoaded = 1;
    if (parentWindow){
        parentWindow.loadAdvFinish();
    }
};
//获取当前广告页面的宽高
function getWidthHeight(){
    var container = document.getElementsByClassName('axd_container')[0];
    var w = container.clientWidth;
    var h = container.clientHeight;
    parentWindow.callbackAdvWidthHeight(w, h);
};
//显示当前广告页面
function show(){
    document.getElementById('adv_body').style.display="block";
};
//默认先隐藏广告页面，等加载完成后再显示
window.addEventListener('DomContentLoaded', function(){document.getElementById('adv_body').style.display="none";}, false);