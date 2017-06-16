package com.jmgzs.carnews.network;

import android.app.Activity;

import com.jmgzs.carnews.BuildConfig;
import com.jmgzs.carnews.network.annotation.JsonElement;
import com.jmgzs.carnews.ui.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * 网络请求不同服务器数据单元测试用例
 * Created by Wxl on 2017/6/10.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class RequestUtilDiffResponseTest {

    private static List<Object[]> params;
    private String url;
    private IJudgeResult judgeResult;

    public static List<Object[]> getParameters() {
        List<Object[]> p = new ArrayList<>();
        p.add(new Object[]{TestUrls.TEST_JSON_URL_TEST_GET_1, new IJudgeResult() {
            @Override
            public void judgeSuccess(String url, Object obj) {
                assertEquals(url, TestUrls.TEST_JSON_URL_TEST_GET_1);
                assertTrue(obj instanceof TestBean);
                TestBean result = (TestBean) obj;
                assertTrue("kghsjk334".equals(result.text));
                assertTrue(12 == result.code);
            }

            @Override
            public void judgeFailure(String url, int errorCode, String msg) {
                fail("url:" + url + "\terrorCode:" + errorCode + "\tmsg:" + msg);
            }
        }});

        p.add(new Object[]{TestUrls.TEST_JSON_URL_TEST_GET_2, new IJudgeResult() {
            @Override
            public void judgeSuccess(String url, Object obj) {
                assertEquals(url, TestUrls.TEST_JSON_URL_TEST_GET_2);
                assertTrue(obj instanceof TestBean);
                TestBean result = (TestBean) obj;
                assertTrue("kghsjk334".equals(result.text));
                assertTrue(10 == result.code);
            }

            @Override
            public void judgeFailure(String url, int errorCode, String msg) {
                fail("url:" + url + "\terrorCode:" + errorCode + "\tmsg:" + msg);
            }
        }});

        p.add(new Object[]{TestUrls.TEST_JSON_URL_TEST_POST_1, new IJudgeResult() {
            @Override
            public void judgeSuccess(String url, Object obj) {
                assertEquals(url, TestUrls.TEST_JSON_URL_TEST_POST_1);
                assertTrue(obj instanceof TestBean);
                TestBean result = (TestBean) obj;
                assertTrue("kghsjk334".equals(result.text));
                assertTrue(12 == result.code);
            }

            @Override
            public void judgeFailure(String url, int errorCode, String msg) {
                fail("url:" + url + "\terrorCode:" + errorCode + "\tmsg:" + msg);
            }
        }});
        return p;
    }

    static {
        params = getParameters();
    }

    public RequestUtilDiffResponseTest() {
    }

    private void setParams(int position){
        Object[] ps = params.get(position);
        url = (String) ps[0];
        judgeResult = (IJudgeResult) ps[1];
    }

    @Before
    public void setUp() throws URISyntaxException {
        //输出日志
        ShadowLog.stream = System.out;
    }

    @Test
    public void testGet1() {
        setParams(0);
        testGet();
    }
    @Test
    public void testGet2() {
        setParams(1);
        testGet();
    }

    @Test
    public void testPost1() {
        setParams(2);
        testPost();
    }

    private void testGet(){
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create().start();
        final Activity activity = activityController.get();
        activityController.resume();
        RequestUtil.requestByGetSync(activity, url, false, null, TestBean.class, new IRequestCallBack<TestBean>() {

            @Override
            public void onSuccess(final String url, final TestBean data) {
                judgeResult.judgeSuccess(url, data);
                System.out.println("返回正常");
            }

            @Override
            public void onFailure(final String url, final int errorCode, final String msg) {
                judgeResult.judgeFailure(url, errorCode, msg);
                System.out.println("返回失败");
            }

            @Override
            public void onCancel(String url) {
                System.out.println("返回取消");
            }
        });

        activityController.destroy();
    }

    private void testPost(){
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create().start();
        final Activity activity = activityController.get();
        activityController.resume();
        RequestUtil.requestByPostSync(activity, url, false, "test body", TestBean.class, new IRequestCallBack<TestBean>() {

            @Override
            public void onSuccess(final String url, final TestBean data) {
                judgeResult.judgeSuccess(url, data);
                System.out.println("返回正常");
            }

            @Override
            public void onFailure(final String url, final int errorCode, final String msg) {
                judgeResult.judgeFailure(url, errorCode, msg);
                System.out.println("返回失败");
            }

            @Override
            public void onCancel(String url) {
                System.out.println("返回取消");
            }
        });

        activityController.destroy();
    }
//    private static void mockRxAndroid() {
//        Function<Scheduler, Scheduler> schedulerFunc = new Function<Scheduler, Scheduler>() {
//            @Override
//            public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
//                return ImmediateThinScheduler.INSTANCE;
//            }
//        };
//        Function<Callable<Scheduler>, Scheduler> schedulerFunc2 = new Function<Callable<Scheduler>, Scheduler>() {
//            @Override
//            public Scheduler apply(@NonNull Callable<Scheduler> scheduler) throws Exception {
//                return ImmediateThinScheduler.INSTANCE;
//            }
//        };
//
//        RxAndroidPlugins.reset();
//
//        RxAndroidPlugins.
//        RxAndroidPlugins.setMainThreadSchedulerHandler(schedulerFunc);
//        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerFunc2);
//    }

//    private void mockTextUtils(){
//        try {
//            PowerMockito.mockStatic(TextUtils.class);
//            if (TextUtils.class.getEnclosingMethod() == null){
//                throw new RuntimeException("TextUtils has no methods");
//            }
//            if (TextUtils.class.getMethods().length < 1){
//                throw new RuntimeException("TextUtils methods less than 1");
//            }
//            for(Method method : TextUtils.class.getDeclaredMethods()){
//                if (method.getName().equals("isEmpty")){
////                    Method method = TextUtils.class.getMethod("isEmpty", CharSequence.class);
//                    PowerMockito.when(TextUtils.class, method).withArguments(Mockito.anyString()).thenCallRealMethod();
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}

interface IJudgeResult {
    void judgeSuccess(String url, Object obj);

    void judgeFailure(String url, int errorCode, String msg);
}

class TestBean {
    @JsonElement(valueType = String.class, isNotNull = true)
    public String text;
    @JsonElement(valueType = int.class, intDefaultValue = 10)
    public int code;
}
