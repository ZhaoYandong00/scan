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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.zyd.http.MyHttpConnectionThread;
import com.zyd.model.Production;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private static final String FIND_BY_PRO_NUM = "findByProNum";
    private static final String UPDATE_OR_INSERT = "updateOrInsert";
    private TextView mTvResult;
    private EditText name, specification, productNumber, productionDate, producer, inspector;
    /**
     * 线程通信
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage (Message msg) {
            Bundle bundle = msg.getData();
            String result = bundle.getString(FIND_BY_PRO_NUM);
            if (result != null && !result.isEmpty()) {
                handlerHttpResult(result);
            }
            result = bundle.getString(UPDATE_OR_INSERT);
            if (result != null && !result.isEmpty()) {
                mTvResult.setVisibility(View.VISIBLE);
                mTvResult.setText(result);
            }
            return false;
        }
    });


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged (Editable s) {
            mTvResult.setText(null);
            mTvResult.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvResult = findViewById(R.id.tv_result);
        this.name = findViewById(R.id.name);
        this.specification = findViewById(R.id.specification);
        this.productNumber = findViewById(R.id.productNO);
        this.productionDate = findViewById(R.id.productionDate);
        this.producer = findViewById(R.id.producer);
        this.inspector = findViewById(R.id.inspector);
        name.addTextChangedListener(textWatcher);
        specification.addTextChangedListener(textWatcher);
        productionDate.addTextChangedListener(textWatcher);
        productNumber.addTextChangedListener(textWatcher);
        producer.addTextChangedListener(textWatcher);
        inspector.addTextChangedListener(textWatcher);
        MainActivityPermissionsDispatcher.getStorageAndCameraWithPermissionCheck(this);
    }

    /**
     * 二维码
     */
    public void QR (View view) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0);
    }

    public void commit (View view) {
        addProduction();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("QR");
                mTvResult.setVisibility(View.VISIBLE);
                mTvResult.setText(result);
                handlerQRResult(result);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理扫描结果
     *
     * @param result 结果
     */
    private void handlerQRResult (String result) {
        String url = "http://192.168.1.6:8080/record/add";
        String mode = "POST";
        String param = "";
        String regex = "";
        if (result.matches(regex))
            param = result;
        new MyHttpConnectionThread(url, mode, param, mHandler, FIND_BY_PRO_NUM).start();
    }

    /**
     * 处理服务器返回参数
     *
     * @param result 处理器返回结果
     */
    private void handlerHttpResult (String result) {
        String regex = "1";
        if (result.matches(regex))
            name.setText(result);
    }

    /**
     * 向服务器添加数据
     */
    private void addProduction () {
        String regex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
        String regex1 = "\\s*";
        if (name.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("产品名称不能为空");
            return;
        }
        if (specification.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("规格型号不能为空");
            return;
        }
        if (productNumber.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("产品编号不能为空");
            return;
        }
        if (productionDate.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("生产日期不能为空");
            return;
        } else if (!productionDate.getText().toString().matches(regex)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("生产日期格式不对");
            return;
        }
        if (producer.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("生产人员不能为空");
            return;
        }
        if (inspector.getText().toString().matches(regex1)) {
            mTvResult.setVisibility(View.VISIBLE);
            mTvResult.setText("生产人员不能为空");
            return;
        }
        String url = "http://192.168.1.6:8080/record/add";
        String mode = "POST";
        Production production = new Production();
        production.setName(name.getText().toString());
        production.setSpecification(specification.getText().toString());
        production.setProductNumber(productNumber.getText().toString());
        production.setProductionDate(productionDate.getText().toString());
        production.setEndProductionDate(productionDate.getText().toString());
        production.setProducer(producer.getText().toString());
        production.setInspector(inspector.getText().toString());
        String param = production.toString();
        Log.i("Production", param);
        new MyHttpConnectionThread(url, mode, param, mHandler, UPDATE_OR_INSERT).start();
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
