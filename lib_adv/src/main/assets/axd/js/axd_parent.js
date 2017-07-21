/**
  *需要广告页面以IFrame形式内嵌在自己页面中所需的js
  *
  */
//把当前主页面的window传给IFrame广告子页面
function setIFrameParentWindow(){
    window.adv_js.toast("准备发送iframe一个window");
    var iframe = document.getElementById('adv_page');
    if (iframe){
        window.adv_js.toast("开始发送iframe一个window");
        iframe.contentWindow.setParentWindow(window);
        window.adv_js.toast("已发送iframe一个window");
    }else{
        window.adv_js.toast("iframe不存在");
    }
};
//通过广告页面的js显示广告
function showAdvPage(){
    document.getElementById('adv_page').contentWindow.show();
};
//加载广告完成，回调给原生
function loadAdvFinish(){
    window.adv_js.loadAdvFinish();
};
////调用广告页面的js获取广告页面宽高，然后会回调给callbackAdvWidthHeight(w,h)方法
//function getAdvRealWidthHeight(){
//    <!--window.carnews.toast("开始获取到宽高");-->
//    document.getElementById('adv_page').contentWindow.getWidthHeight();
//};
////把获取到的广告位的页面宽高回调给原生
//function callbackAdvWidthHeight(w, h){
//    <!--window.carnews.toast("获取到宽高"+w+"\t"+h);-->
//    window.carnews.getAdvWidthHeight(w, h);
//};
//设置广告所在iframe的宽高
function setAdvWidthHeight(width, height){
    <!--window.adv_js.toast("设置宽高"+width+"\t"+height);-->
    var adv = document.getElementById('adv_page');
    adv.width = width;
    adv.height = height;
};

//function toast(){
//    window.adv_js.toast("收到iframe回调");
//};