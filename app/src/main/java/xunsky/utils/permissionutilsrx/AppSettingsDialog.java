package xunsky.utils.permissionutilsrx;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class AppSettingsDialog{
    public static final int DEFAULT_SETTINGS_REQ_CODE = 16061;


    static final String EXTRA_APP_SETTINGS = "extra_app_settings";

    private final int mThemeResId;
    private final String mRationale;
    private final String mTitle;
    private final String mPositiveButtonText;
    private final String mNegativeButtonText;
    private final int mRequestCode;
    private final int mIntentFlags;

    private Object mActivityOrFragment;
    private Context mContext;

    private AppSettingsDialog(final Object activityOrFragment,
                               int themeResId,
                               String rationale,
                               String title,
                               String positiveButtonText,
                               String negativeButtonText,
                              int requestCode,
                              int intentFlags) {
        setActivityOrFragment(activityOrFragment);
        mThemeResId = themeResId;
        mRationale = rationale;
        mTitle = title;
        mPositiveButtonText = positiveButtonText;
        mNegativeButtonText = negativeButtonText;
        mRequestCode = requestCode;
        mIntentFlags = intentFlags;
    }

    private void setActivityOrFragment(Object activityOrFragment) {
        mActivityOrFragment = activityOrFragment;

        if (activityOrFragment instanceof Activity) {
            mContext = (Activity) activityOrFragment;
        } else if (activityOrFragment instanceof Fragment) {
            mContext = ((Fragment) activityOrFragment).getContext();
        } else {
            throw new IllegalStateException("Unknown object: " + activityOrFragment);
        }
    }

    private void startForResult(Intent intent) {
        if (mActivityOrFragment instanceof Activity) {
            ((Activity) mActivityOrFragment).startActivityForResult(intent, mRequestCode);
        } else if (mActivityOrFragment instanceof Fragment) {
            ((Fragment) mActivityOrFragment).startActivityForResult(intent, mRequestCode);
        }
    }

    public void show() {
        showDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", mContext.getPackageName(), null));
                intent.addFlags(mIntentFlags);
                mContext.startActivity(intent);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    AlertDialog showDialog(DialogInterface.OnClickListener positiveListener,
                           DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder;
        if (mThemeResId > 0) {
            builder = new AlertDialog.Builder(mContext, mThemeResId);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        return builder
                .setCancelable(false)
                .setTitle(mTitle)
                .setMessage(mRationale)
                .setPositiveButton(mPositiveButtonText, positiveListener)
                .setNegativeButton(mNegativeButtonText, negativeListener)
                .show();
    }



    public static class Builder {

        private final Object mActivityOrFragment;
        private final Context mContext;
        private int mThemeResId = -1;
        private String mRationale;
        private String mTitle;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private int mRequestCode = -1;
        private boolean mOpenInNewTask = false;

        public Builder(Activity activity) {
            mActivityOrFragment = activity;
            mContext = activity;
        }

        public Builder(Fragment fragment) {
            mActivityOrFragment = fragment;
            mContext = fragment.getContext();
        }

        public Builder setThemeResId(int themeResId) {
            mThemeResId = themeResId;
            return this;
        }

        
        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        
        public Builder setTitle(int title) {
            mTitle = mContext.getString(title);
            return this;
        }

        
        public Builder setRationale(String rationale) {
            mRationale = rationale;
            return this;
        }

        
        public Builder setRationale(int rationale) {
            mRationale = mContext.getString(rationale);
            return this;
        }

        
        public Builder setPositiveButton(String text) {
            mPositiveButtonText = text;
            return this;
        }

        
        public Builder setPositiveButton(int textId) {
            mPositiveButtonText = mContext.getString(textId);
            return this;
        }

        
        public Builder setNegativeButton(String text) {
            mNegativeButtonText = text;
            return this;
        }

        
        public Builder setNegativeButton(int textId) {
            mNegativeButtonText = mContext.getString(textId);
            return this;
        }

        
        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        
        public Builder setOpenInNewTask(boolean openInNewTask) {
            mOpenInNewTask = openInNewTask;
            return this;
        }

        
        public AppSettingsDialog build() {
            mRationale = TextUtils.isEmpty(mRationale) ?
                    "权限获取失败,请到设置页面中打开": mRationale;
            mTitle = TextUtils.isEmpty(mTitle) ?
                    "权限获取" : mTitle;
            mPositiveButtonText = TextUtils.isEmpty(mPositiveButtonText) ?
                    mContext.getString(android.R.string.ok) : mPositiveButtonText;
            mNegativeButtonText = TextUtils.isEmpty(mNegativeButtonText) ?
                    mContext.getString(android.R.string.cancel) : mNegativeButtonText;
            mRequestCode = mRequestCode > 0 ? mRequestCode : DEFAULT_SETTINGS_REQ_CODE;

            int intentFlags = 0;
            if (mOpenInNewTask) {
                intentFlags |= Intent.FLAG_ACTIVITY_NEW_TASK;
            }

            return new AppSettingsDialog(
                    mActivityOrFragment,
                    mThemeResId,
                    mRationale,
                    mTitle,
                    mPositiveButtonText,
                    mNegativeButtonText,
                    mRequestCode,
                    intentFlags);
        }
    }
}
