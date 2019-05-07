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
    public static void requestPermissions(final AppCompatActivity act, final CallBack callBack, final String... permissions) {
        new RxPermissions(act)
                .request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            callBack.success();
                        } else {
                            callBack.fail("user reject",haveSomePermissionPermanentlyDenied(act,permissions));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callBack.fail(throwable.getMessage(),false);
                    }
                });
    }

    //在Fragment中申请权限时不要使用fragment.getActivity()
    //有可能会导致java.lang.IllegalStateException: FragmentManager is already executing transactions.
    public static void requestPermissions(final Fragment fragment, final CallBack callBack, final String... permissions) {
        new RxPermissions(fragment)
                .request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            callBack.success();
                        } else {
                            callBack.fail("user reject",haveSomePermissionPermanentlyDenied(fragment.getActivity(),permissions));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callBack.fail(throwable.getMessage(),false);
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

    //该方法只能在发起权限申请后使用
    public static boolean haveSomePermissionPermanentlyDenied(Activity act, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return false;

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            //在该权限未被授权的前提下,系统表示不提示,则表示被永久拒绝
            if (act.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                boolean isPermanentlyReject = !ActivityCompat.shouldShowRequestPermissionRationale(act, permission);
                if (isPermanentlyReject)
                    return true;
            }
        }
        return false;
    }

    public interface CallBack {
        void success();

        void fail(String message,boolean somePermissionPermanentlyReject);
    }
}
