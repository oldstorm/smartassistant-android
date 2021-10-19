package com.yctc.zhiting.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yctc.zhiting.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {
    public static String KEY_ERROR_LOG = "key_error_log";
    private TextView tvErrorLog;
    private Button btRestart;
    private Button btClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        initUI();
        initData();
        initAction();
    }

    private void initAction() {
        btRestart.setOnClickListener(this);
        btClose.setOnClickListener(this);
        tvErrorLog.setOnClickListener(this);
    }

    protected void initUI() {
        tvErrorLog = findViewById(R.id.tvErrorLog);
        btRestart = findViewById(R.id.btRestart);
        btClose = findViewById(R.id.btClose);
    }

    protected void initData() {
        Intent intent = getIntent();
        if (intent != null && tvErrorLog != null) {
            tvErrorLog.setText(intent.getStringExtra(KEY_ERROR_LOG));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btRestart) {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
            Process.killProcess(Process.myPid());
        } else if (id == R.id.btClose) {
            System.exit(0);
            finish();
        }
    }
}
