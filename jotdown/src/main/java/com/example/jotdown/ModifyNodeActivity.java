package com.example.jotdown;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.service.ReminderService;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.DateUtil;
import com.example.jotdown.utils.MenuUtil;
import com.example.jotdown.viewmodel.UpdateViewModel;
import com.example.jotdown.widget.AudioRecorder;
import com.example.jotdown.widget.ChooseDateDialog;
import com.example.jotdown.widget.ChooseDateDialog.OnDateSetListener;
import com.example.jotdown.widget.ChooseDateDialog.OnTimeSetListener;
import com.example.jotdown.widget.LabelAdapter;

import java.util.Calendar;
import java.util.List;

/**
* 更改备忘录
* @author XN-H
* */
public class ModifyNodeActivity extends AppCompatActivity
        implements OnClickListener,OnTouchListener ,OnDateSetListener, OnTimeSetListener {
    private static final String TAG="ModifyNodeActivity";

    private AudioRecorder audioRecorder;
    private EditText et_title;
    private EditText et_content;

    private int importancePosition=0;

    private MainApplication myApp;
    private UpdateViewModel mUpdateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_node);
        oldRemindTime = getString(R.string.notRemind);
        audioRecorder=new AudioRecorder();
        myApp=MainApplication.getInstance();
        Intent intent=getIntent();

        mUpdateViewModel=new ViewModelProvider(this).get(UpdateViewModel.class);
        mUpdateViewModel.init();
        if (intent.hasExtra("_id")) {                 //如果上一个Activity传入了包裹，则拆包进行查询数据库
            //上一个Activity传入了包裹
            Bundle bundle = intent.getExtras();
            //进行更改数据操作
            long rowId = bundle.getLong("_id");
            Log.d(TAG, "onCreate: rowID is "+ rowId);
            mUpdateViewModel.queryNode(rowId);
        }
        initFindView();
        if(!mUpdateViewModel.getQuerySchedule().hasObservers()){
            mUpdateViewModel.getQuerySchedule().observe(ModifyNodeActivity.this, nodeInfoResource -> {
                node=nodeInfoResource.getData();
                if (!node.remind.equals(getString(R.string.notRemind))) {
                    oldRemindTime = node.remind;
                }
                Log.d(TAG, "run: remind time is "+node.remind);
                labelArray=myApp.getLabelArray();
                initPage();                                 //初始化页面
                initSpinner();
            });
        }
        if(!mUpdateViewModel.getUpdateSchedule().hasObservers()){
            mUpdateViewModel.getUpdateSchedule().observe(ModifyNodeActivity.this, booleanResource -> {

            });
        }
    }

    private void initFindView(){
        Toolbar tl_head = findViewById(R.id.tl_head);
        setSupportActionBar(tl_head);
        et_title=findViewById(R.id.et_title);
        et_content=findViewById(R.id.et_content);
        ImageView iv_recording = findViewById(R.id.iv_recording);
        iv_recording.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int viewId=view.getId();

        if(viewId==R.id.iv_recording) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                startRecording();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                stopRecording();
            }
        }
        return true;
    }

    private void initPage() {
        et_title.setText(node.title);
        et_content.setText(node.content);

        for(int i=0;i<labelArray.size();i++){
            if(labelArray.get(i).importance.equals(node.importance)){
                importancePosition=i;
                break;
            }
        }
    }

    private void initSpinner() {
        Spinner sp_importance = findViewById(R.id.sp_importance);
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
            //Toast.makeText(this, "点击了提醒图标", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            //弹出自定义的提醒时间选择对话框
            ChooseDateDialog dialog = new ChooseDateDialog(this);
            dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
            dialog.setTime(this);
            dialog.show();

            return true;
        } else if (id == R.id.menu_save) {                //点击了完成图标
            Log.d(TAG, "onOptionsItemSelected: newRemindTime:" +
                    ("{year}-{month}-{day} {hour}:{minute}".equals(newRemindTime) ?
                            getResources().getString(R.string.notRemind) : newRemindTime));

            if (null == node) {
                node = new NodeInfo(this);
            }
            String title=et_title.getText().toString();
            node.title = title.equals("")?null:title;
            Log.d(TAG, "onOptionsItemSelected: title is "+title);
            String content=et_content.getText().toString();
            node.content = content.equals("")?null:content;
            Log.d(TAG, "onOptionsItemSelected: node content is "+content);
            node.remind = "{year}-{month}-{day} {hour}:{minute}".equals(newRemindTime) ?
                    oldRemindTime : newRemindTime;
            Log.d(TAG, "onOptionsItemSelected: remind time is "+node.remind);
            node.requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            mUpdateViewModel.updateNode(node);

            if (!node.remind.equals(getString(R.string.notRemind))) {
                Intent intent = new Intent(this, ReminderService.class);
                intent.putExtra("title", getString(R.string.app_name));
                intent.putExtra("content", node.title); // 备忘录内容
                intent.putExtra("reminderTime", DateUtil.timeToTimestamp("yyyy-MM-dd HH:mm", node.remind)); // 提醒时间
                intent.putExtra("requestCode", node.requestCode);
                startService(intent);
            }
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRecording() {
        audioRecorder.startRecording();
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        node.audioFilePath= audioRecorder.stopRecording();
        Toast.makeText(this, "Recording ended", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Recording stopped. File saved: " + outputFile, Toast.LENGTH_SHORT).show();
    }

    //重新加载页面
    @Override
    protected void onResume() {
        super.onResume();
        nodesDBHelper = myApp.getNodesDBHelper();
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

    private NodeInfo node;
    private List<LabelInfo> labelArray;
    private NodesDBHelper nodesDBHelper;

    String oldRemindTime = "";
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
