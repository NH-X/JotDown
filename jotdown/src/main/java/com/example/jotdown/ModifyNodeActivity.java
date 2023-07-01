package com.example.jotdown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.service.ReminderService;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.DateUtil;
import com.example.jotdown.utils.MenuUtil;
import com.example.jotdown.viewmodel.MainViewModel;
import com.example.jotdown.viewmodel.UpdateViewModel;
import com.example.jotdown.widget.AudioRecorder;
import com.example.jotdown.widget.ChooseDateDialog;
import com.example.jotdown.widget.ChooseDateDialog.OnDateSetListener;
import com.example.jotdown.widget.ChooseDateDialog.OnTimeSetListener;
import com.example.jotdown.widget.LabelAdapter;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

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
    private Toolbar tl_head;
    private EditText et_title;
    private EditText et_content;
    private Spinner sp_importance;
    private View view_title_color;
    private View view_background_color;
    private View view_content_color;
    private ImageView iv_recording;

    private int titleColor = 0;
    private int contentColor = 0;
    private int backgroundColor = 0;
    private int importancePosition=0;

    private MainApplication myApp;
    private UpdateViewModel mUpdateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_node);
        audioRecorder=new AudioRecorder();
        myApp=MainApplication.getInstance();
        Intent intent=getIntent();

        if (intent.hasExtra("_id")) {                 //如果上一个Activity传入了包裹，则拆包进行查询数据库
            //上一个Activity传入了包裹
            Bundle bundle = intent.getExtras();
            //进行更改数据操作
            rowId = bundle.getLong("_id");
            Log.d(TAG, "onCreate: rowID is "+rowId);
            nodeHandler.post(queryNode);
        }
        initFindView();
        mUpdateViewModel=new ViewModelProvider(this).get(UpdateViewModel.class);
        mUpdateViewModel.init();
        if(!mUpdateViewModel.getUpdateSchedule().hasObservers()){
            mUpdateViewModel.getUpdateSchedule().observe(ModifyNodeActivity.this, new Observer<Resource<Boolean>>() {
                @Override
                public void onChanged(Resource<Boolean> booleanResource) {

                }
            });
        }
    }

    private void initFindView(){
        tl_head=findViewById(R.id.tl_head);
        setSupportActionBar(tl_head);
        et_title=findViewById(R.id.et_title);
        et_content=findViewById(R.id.et_content);
        view_title_color=findViewById(R.id.view_title_color);
        view_background_color=findViewById(R.id.view_background_color);
        view_content_color=findViewById(R.id.view_content_color);
        view_title_color.setOnClickListener(this);
        view_content_color.setOnClickListener(this);
        view_background_color.setOnClickListener(this);
        iv_recording=findViewById(R.id.iv_recording);
        iv_recording.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.view_title_color || viewId == R.id.view_content_color || viewId == R.id.view_background_color) {
            initColorPickerDialog(viewId);
        }
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

    private void initPage() {
        et_title.setText(node.title);
        et_content.setText(node.content);
        titleColor = node.titleColor;
        contentColor = node.contentColor;
        backgroundColor = node.backgroundColor;
        view_title_color.setBackgroundColor(titleColor);
        view_content_color.setBackgroundColor(contentColor);
        view_background_color.setBackgroundColor(backgroundColor);

        for(int i=0;i<labelArray.size();i++){
            if(labelArray.get(i).importance.equals(node.importance)){
                importancePosition=i;
                break;
            }
        }
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
            node.titleColor = titleColor;
            String content=et_content.getText().toString();
            node.content = content.equals("")?null:content;
            Log.d(TAG, "onOptionsItemSelected: node content is "+content);
            node.contentColor = contentColor;
            node.backgroundColor = backgroundColor;
            node.remind = "{year}-{month}-{day} {hour}:{minute}".equals(newRemindTime) ?
                    getResources().getString(R.string.notRemind) : newRemindTime;
            node.requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            node.changeTime = DateUtil.getNowDateTime();
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
        String outputFile = audioRecorder.stopRecording();
        node.audioFilePath=outputFile;
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
    private Handler nodeHandler = new Handler();          //创建一个处理器对象

    private long rowId = 0;

    //从数据库中查询Node数据
    private Runnable queryNode = new Runnable() {
        @Override
        public void run() {
            if (!nodesDBHelper.readIsOpen()) {
                nodesDBHelper.getReadDB();
            }
            node = nodesDBHelper.queryInfoById(rowId).get(0);
            //Toast.makeText(RenewNodeActivity.this, node.toString(), Toast.LENGTH_SHORT).show();
            if (!node.remind.equals(getString(R.string.notRemind))) {
                oldRemindTime = node.remind;
            }
            labelArray=myApp.getLabelArray();
            initPage();                                 //初始化页面
            initSpinner();
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
