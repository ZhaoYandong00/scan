package com.zyd.qrcode;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.zyd.http.MyHttpConnectionThread;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private TextView mTvResult;
    /**
     * 线程通信
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage (Message msg) {
            Bundle bundle = msg.getData();
            String result = bundle.getString("HTTP");
            if (result != null && !result.isEmpty()) {
                mTvResult.setText(result);
                handlerResult(result);
            }

            return false;
        }
    });

    private void handlerResult (String result) {
        String url = "http://192.168.1.6:8080/record/add";
        String mode = "POST";
        String param = "";
        String regex = "";
        if (result.matches(regex))
            param = result;
        new MyHttpConnectionThread(url, mode, param, mHandler).start();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvResult = findViewById(R.id.tv_result);
        MainActivityPermissionsDispatcher.getStorageAndCameraWithPermissionCheck(this);
    }

    /**
     * 二维码
     */
    public void QR (View view) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("result");
                mTvResult.setText(result);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 得到权限
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getStorageAndCamera () {

    }

    /**
     * 要求权限
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 结果数组
     */
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 请求显示
     *
     * @param request 请求
     */
    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getStorageAndCameraOnShow (final PermissionRequest request) {
        showRationaleDialog(request);
    }

    /**
     * 请求拒绝
     */
    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getStorageAndCameraDenied () {
        Toast.makeText(this, "你拒绝了该权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 不再提醒
     */
    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getStorageAndCameraNever () {
        AskForPermission();
    }


    /**
     * 再用户拒绝过一次之后,告知用户具体需要权限的原因
     *
     * @param request 请求
     */
    private void showRationaleDialog (final PermissionRequest request) {
        new AlertDialog.Builder(this).setPositiveButton("确定", (dialog, which) -> {
            request.proceed();//请求权限
        }).setTitle("请求权限").setCancelable(false).setMessage("我,存储，摄像头，开启授权").show();
    }

    /**
     * 被拒绝并且不再提醒,提示用户去设置界面重新打开权限
     */
    private void AskForPermission () {
        new AlertDialog.Builder(this).setTitle("缺少基础存储权限")
                .setMessage("当前应用缺少存储权限,请去设置界面授权.\n授权之后按两次返回键可回到该应用哦").setNegativeButton("取消",
                                                                                         (dialog, which) -> Toast
                                                                                                 .makeText(
                                                                                                         getApplicationContext(),
                                                                                                         "你拒绝了该权限",
                                                                                                         Toast.LENGTH_SHORT)
                                                                                                 .show())
                .setNeutralButton("不在提醒", (dialogInterface, i) -> Toast
                        .makeText(getApplicationContext(), "不再提供权限", Toast.LENGTH_SHORT).show())
                .setPositiveButton("设置", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                    startActivity(intent);
                }).create().show();
    }


}
