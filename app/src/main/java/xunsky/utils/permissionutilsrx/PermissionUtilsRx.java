package xunsky.utils.permissionutilsrx;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class PermissionUtilsRx {
    public static void requestPermissions(AppCompatActivity act, final CallBack callBack, String... permissions) {
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

    //在Fragment中申请权限时不要使用fragment.getActivity()
    //有可能会导致java.lang.IllegalStateException: FragmentManager is already executing transactions.
    public static void requestPermissions(Fragment fragment, final CallBack callBack, String... permissions) {
        new RxPermissions(fragment)
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

    public static boolean checkPermission(AppCompatActivity act, String... permissions) {
//        Observable.just(1)
//                .compose(
//                        new RxPermissions(act)
//                        .ensure(permissions))
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                    }
//                });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int hasPermission = act.checkSelfPermission(permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean haveSomePermissionPermanentlyDenied(Activity act, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean isPermanentlyReject = !ActivityCompat.shouldShowRequestPermissionRationale(act, permission);
            if (isPermanentlyReject)
                return true;
        }
        return false;
    }

    public interface CallBack {
        void success();

        void fail(String message);
    }
}
