package com.jmgzs.carnews.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.bean.PushBean;
import com.jmgzs.carnews.ui.NewsInfoActivity;
import com.jmgzs.lib_network.utils.L;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Wxl on 2017/6/19.
 */

public class YMPushService extends UmengMessageService implements IPush {

    private IPushReceiver listener;

    @Override
    public void appInit(final Context context) {
        final PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setPushIntentServiceClass(YMPushService.class);
//        mPushAgent.setDebugMode(true);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                L.e("推送注册成功:"+deviceToken);
                onMessage(context, null);
            }

            @Override
            public void onFailure(String s, String s1) {
                L.e("推送注册失败");
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
    public void onMessage(final Context context, Intent intent) {
        L.e("收到推送");
        if (!App.isRecptPush){
            return;
        }
        try {
            //可以通过MESSAGE_BODY取得消息体
            if (intent == null){
                L.e("推送为空");
                return;
            }
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            L.e("message=" + message);    //消息体
            L.e("custom=" + msg.custom);    //自定义消息的内容
            L.e("title=" + msg.title);    //通知标题
            L.e("text=" + msg.text);    //通知内容
            L.e("taskId="+msg.task_id);
            // code  to handle message here
            if (listener != null){
                listener.onReceive(context, message, msg.custom, msg.title, msg.text);
            }
            final PushBean pushBean = new Gson().fromJson(msg.custom, PushBean.class);
//            final PushBean pushBean = new Gson().fromJson("{\"aid\":45296,\"abstr\":\"我叫林雨，今年34岁，是一家私企老板。我生长在单亲家庭，小时候家境贫困。我知道白手起家的辛酸和无奈。所以，从高中时代开始，我就十分渴望能拥有一份纯洁的爱情，和一个温暖的家。为了这个卑微的梦想，我竭尽全力奋斗，抛开一切繁华\",\"img\":\"http://mjcrawl-1252328573.file.myqcloud.com/fa9d23a764871c03e4921f6e116aa4a95bcd53c6.jpg\",\"title\":\"突然发现老婆的秘密我想离婚\"}", PushBean.class);
            L.e(msg.custom);
            L.e(pushBean.toString());
            L.e("推送大图："+pushBean.getImg());
//            App.getInstance().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    GlideApp.with(context).asBitmap().fitCenter().load(pushBean.getImg()).into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                            L.e("推送大图下载成功");
//
//                        }
//                    });
//                }
//            });
            showNotification(context, pushBean);
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

    private void showNotification(Context context, PushBean pushBean){
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, NewsInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(NewsInfoActivity.INTENT_AID, pushBean.getAid());
        intent.putExtra("fromNotify", true);
        ArrayList<String> imgList = new ArrayList<>();
        imgList.add(pushBean.getImg());
        intent.putStringArrayListExtra(NewsInfoActivity.INTENT_IMAGES, imgList);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                //设置小图标
                .setSmallIcon(R.mipmap.logo)
                .setAutoCancel(true)
                .setTicker("您收到一条新闻")
                //设置通知标题
                .setContentTitle(pushBean.getTitle())
                //设置通知内容
                .setContentText(pushBean.getAbstr())
                .setLargeIcon(((BitmapDrawable)context.getResources().getDrawable(R.mipmap.logo)).getBitmap())
                .setContentIntent(contentIntent)
                ;
//        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.);
//        builder.setCustomBigContentView(expandedView);
        notifyManager.notify(""+pushBean.getAid(), 1, builder.build());
    }
}
