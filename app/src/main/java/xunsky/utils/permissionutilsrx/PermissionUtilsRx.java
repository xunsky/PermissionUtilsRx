package xunsky.utils.permissionutilsrx;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class PermissionUtilsRx {
    public static void requestPermissions(Activity act, final CallBack callBack, String... permissions){
        new RxPermissions(act)
                .request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            callBack.success();
                        } else {
                            callBack.fail("user reject");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callBack.fail(throwable.getMessage());
                    }
                });

    }
    public interface CallBack{
        void success();
        void fail(String message);
    }
}
