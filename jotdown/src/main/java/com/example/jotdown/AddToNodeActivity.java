package com.example.jotdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.service.ReminderService;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.DateUtil;
import com.example.jotdown.utils.MenuUtil;
import com.example.jotdown.widget.ChooseDateDialog;
import com.example.jotdown.widget.ChooseDateDialog.OnDateSetListener;
import com.example.jotdown.widget.ChooseDateDialog.OnTimeSetListener;
import com.example.jotdown.widget.LabelAdapter;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.Calendar;
import java.util.List;

public class AddToNodeActivity extends AppCompatActivity implements
        OnClickListener, OnDateSetListener, OnTimeSetListener {
    private final static String TAG = "RenewNodeActivity";

    private Toolbar tl_head;
    private EditText et_title;
    private EditText et_content;
    private Spinner sp_importance;
    private View view_title_color;
    private View view_background_color;
    private View view_content_color;

    private NodeInfo node;
    private List<LabelInfo> labelArray;

    private int titleColor = 0;
    private int contentColor = 0;
    private int backgroundColor = 0;
    private int importancePosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_node);
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        view_content_color = findViewById(R.id.view_content_color);
        view_title_color = findViewById(R.id.view_title_color);
        view_background_color = findViewById(R.id.view_background_color);
        view_title_color.setOnClickListener(this);
        view_content_color.setOnClickListener(this);
        view_background_color.setOnClickListener(this);
        tl_head = findViewById(R.id.tl_head);
        setSupportActionBar(tl_head);

        node = new NodeInfo(this);
        labelArray=MainApplication.getLabelArray();
        initSpinner();
    }

    private void initSpinner() {
        sp_importance = findViewById(R.id.sp_importance);
        LabelAdapter adapter = new LabelAdapter(this, labelArray);
        sp_importance.setAdapter(adapter);
        sp_importance.setSelection(importancePosition);
        sp_importance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                node.labelColor=labelArray.get(position).labelColor;
                node.importance=labelArray.get(position).importance;
                importancePosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //显示菜单项目左侧的图标
        MenuUtil.setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //从menu_search.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_remind, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {                  //点击了工具栏左边的返回箭头
            if (!node.remind.equals(getString(R.string.notRemind))) {
                if (DateUtil.timeToTimestamp("yyyy-MM-dd HH:mm", node.remind) < System.currentTimeMillis()) {
                    CancellationNotifyUtil.deleteReminder(this, AlarmReceiver.class, node.requestCode);
                }
            }
            finish();
        } else if (id == R.id.menu_remind) {              //点击了提醒图标
            Toast.makeText(this, "点击了提醒图标", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            //弹出自定义的提醒时间选择对话框
            ChooseDateDialog dialog = new ChooseDateDialog(this);
            dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
            dialog.setTime(this);
            dialog.show();

            return true;
        } else if (id == R.id.menu_send) {                //点击了完成图标
            Log.d(TAG, "onOptionsItemSelected: newRemindTime:" +
                    ("{year}-{month}-{day} {hour}:{minute}".equals(newRemindTime) ? oldRemindTime : newRemindTime));

            if (null == node) {
                node = new NodeInfo(this);
            }
            node.title = et_title.getText().toString();
            node.titleColor = titleColor;
            node.content = et_content.getText().toString();
            node.contentColor = contentColor;
            node.backgroundColor = backgroundColor;
            node.remind = "{year}-{month}-{day} {hour}:{minute}".equals(newRemindTime) ? oldRemindTime : newRemindTime;
            node.requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            node.changeTime = DateUtil.getNowDateTime();
            nodeHandler.post(updateOrCreate);

            if (!node.remind.equals(getString(R.string.notRemind))) {
                Intent intent = new Intent(this, ReminderService.class);
                intent.putExtra("title", getString(R.string.app_name));
                intent.putExtra("content", node.title); // 备忘录内容
                intent.putExtra("reminderTime", DateUtil.timeToTimestamp("yyyy-MM-dd HH:mm", node.remind)); // 提醒时间
                intent.putExtra("requestCode", node.requestCode);
                startService(intent);
            }
            Toast.makeText(this, node.labelColor+node.importance, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.view_title_color || viewId == R.id.view_content_color || viewId == R.id.view_background_color) {
            initColorPickerDialog(viewId);
        }
    }

    private void initColorPickerDialog(int viewId) {                                     //初始化颜色选择器
        AlertDialog dialog = new ColorPickerDialog.Builder(this)
                .setTitle("设置颜色")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(getString(R.string.confirm),
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                setLayoutColor(envelope, viewId);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true)          // the default value is true.
                .attachBrightnessSlideBar(true)     // the default value is true.
                .setBottomSpace(12)                        // set a bottom space between the last slidebar and buttons.
                .show();
    }

    private void setLayoutColor(ColorEnvelope envelope, int viewId) {                     //设置颜色
        if (viewId == R.id.view_title_color) {
            titleColor = envelope.getColor();
            view_title_color.setBackgroundColor(titleColor);
        } else if (viewId == R.id.view_content_color) {
            contentColor = envelope.getColor();
            view_content_color.setBackgroundColor(contentColor);
        } else if (viewId == R.id.view_background_color) {
            backgroundColor = envelope.getColor();
            view_background_color.setBackgroundColor(backgroundColor);
        }
    }

    private NodesDBHelper nodesDBHelper;

    //重新加载页面
    @Override
    protected void onResume() {
        super.onResume();
        nodesDBHelper = MainApplication.getNodesDBHelper();
    }

    //销毁页面
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭数据库
        if (nodesDBHelper != null) {
            nodesDBHelper.close();
        }
    }

    private Handler nodeHandler = new Handler();          //创建一个处理器对象

    //往数据库中添加数据或更新数据
    private Runnable updateOrCreate = new Runnable() {
        @Override
        public void run() {
            if (!nodesDBHelper.writeIsOpen()) {
                nodesDBHelper.getWriteDB();
            }
            node._id = (int) nodesDBHelper.add(node);
            if (!nodesDBHelper.readIsOpen()) {
                nodesDBHelper.getReadDB();
            }
            Toast.makeText(AddToNodeActivity.this, "数据库数据条数：" + nodesDBHelper.queryCount(), Toast.LENGTH_SHORT).show();
        }
    };

    String oldRemindTime = "不提醒";
    String newRemindTime = "{year}-{month}-{day} {hour}:{minute}";

    @Override
    public void onDateSet(int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: " + year + " " + month + " " + dayOfMonth);
        newRemindTime = newRemindTime.replace("{year}", year + "");
        newRemindTime = newRemindTime.replace("{month}", month >= 10 ? "" + month : "0" + month);
        newRemindTime = newRemindTime.replace("{day}", dayOfMonth >= 10 ? "" + dayOfMonth : "0" + dayOfMonth);
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        Log.d(TAG, "onTimeSet: " + hour + " " + minute);
        newRemindTime = newRemindTime.replace("{hour}", hour >= 10 ? "" + hour : "0" + hour);
        newRemindTime = newRemindTime.replace("{minute}", minute >= 10 ? "" + minute : "0" + minute);
    }
}