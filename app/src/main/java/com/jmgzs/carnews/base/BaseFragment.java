package com.jmgzs.carnews.base;

import android.support.v4.app.Fragment;
import android.view.View;


import com.jmgzs.carnews.util.L;


public class BaseFragment extends Fragment {


    @Override
    public void onStart() {
        super.onStart();
        L.setTag(this.getClass().getSimpleName());
    }

    protected <E extends View> E getView(View rootView, int resViewId) {
        try {
            if (rootView != null) {
                return (E) rootView.findViewById(resViewId);
            } else {
                throw new NullPointerException("root view is null, can not find view");
            }
        } catch (Throwable e) {
            throw e;
        }
    }


//    protected void showLoadingDialog(int msgId) {
//        showLoadingDialog(getString(msgId));
//    }
//
//    protected void showLoadingDialog(String res) {
//        if (loadingDialog == null) {
//            loadingDialog = new DialogProgress(getActivity());
//        }
//        loadingDialog.show();
//        loadingDialog.setMsg(res);
//    }
//
//    protected void showLoadingDialog() {
//        showLoadingDialog(R.string.get_data);
//    }
//
//    protected void hideLoadingDialog() {
//        if (loadingDialog != null && loadingDialog.isShowing()) {
//            loadingDialog.hide();
//        }
//    }
//
//    protected void dismissLoadingDialog() {
//        try {
//            if (loadingDialog != null && loadingDialog.isShowing()) {
//                loadingDialog.dismiss();
//            }
//        } catch (Exception e) {
//        }
//    }

}
