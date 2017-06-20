package com.jmgzs.carnews.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jmgzs.lib_network.utils.L;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Created by Wxl on 2017/6/19.
 */

public class YMPush extends UmengMessageService implements IPush {

    private IPushReceiver listener;

    @Override
    public void appInit(Context context) {
        PushAgent mPushAgent = PushAgent.getInstance(context);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    @Override
    public void activityInit(Activity activity) {
        PushAgent.getInstance(activity).onAppStart();
    }

    @Override
    public void setPushReceiver(IPushReceiver receiver) {
        this.listener = receiver;
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        try {
            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            L.d("message=" + message);    //消息体
            L.d("custom=" + msg.custom);    //自定义消息的内容
            L.d("title=" + msg.title);    //通知标题
            L.d("text=" + msg.text);    //通知内容
            // code  to handle message here
            if (listener != null){
                listener.onReceive(context, message, msg.custom, msg.title, msg.text);
            }

            // 对完全自定义消息的处理方式，点击或者忽略
            boolean isClickOrDismissed = true;
            if (isClickOrDismissed) {
                //完全自定义消息的点击统计
                UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
            } else {
                //完全自定义消息的忽略统计
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
            }

//            // 使用完全自定义消息来开启应用服务进程的示例代码
//            // 首先需要设置完全自定义消息处理方式
//            // mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
//            // code to handle to start/stop service for app process
//            JSONObject json = new JSONObject(msg.custom);
//            String topic = json.getString("topic");
//            L.d("topic=" + topic);
//            if (topic != null && topic.equals("appName:startService")) {
//                // 在友盟portal上新建自定义消息，自定义消息文本如下
//                //{"topic":"appName:startService"}
//                if (Helper.isServiceRunning(context, NotificationService.class.getName()))
//                    return;
//                Intent intent1 = new Intent();
//                intent1.setClass(context, NotificationService.class);
//                context.startService(intent1);
//            } else if (topic != null && topic.equals("appName:stopService")) {
//                // 在友盟portal上新建自定义消息，自定义消息文本如下
//                //{"topic":"appName:stopService"}
//                if (!Helper.isServiceRunning(context, NotificationService.class.getName()))
//                    return;
//                Intent intent1 = new Intent();
//                intent1.setClass(context, NotificationService.class);
//                context.stopService(intent1);
//            }
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }
}
