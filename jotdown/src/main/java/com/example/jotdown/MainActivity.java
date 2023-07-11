package com.example.jotdown;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jotdown.adapter.LinearDynamicAdapter;
import com.example.jotdown.adapter.LinearDynamicAdapter1;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.DateUtil;
import com.example.jotdown.utils.PermissionUtil;
import com.example.jotdown.viewmodel.MainViewModel;
import com.example.jotdown.widget.RecyclerExtras.OnItemClickListener;
import com.example.jotdown.widget.RecyclerExtras.OnItemLongClickListener;
import com.example.jotdown.widget.RecyclerExtras.OnItemPlayListener;
import com.example.jotdown.widget.SpacesItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnClickListener, OnItemClickListener ,OnItemLongClickListener ,OnItemPlayListener {
    private static final String TAG="MainActivity";
    private CoordinatorLayout cl_main;
    private RecyclerView rv_dynamic;
    private Toolbar tl_head;
    private TextView tv_fuzzy_query;
    private MediaPlayer mediaPlayer;

    private SpacesItemDecoration decoration;                //item之间的间距

    //private LinearDynamicAdapter adapter;
    private LinearDynamicAdapter1 adapter;

    private MainViewModel mMainViewModel;
    private String fuzzyQueryStr="";                            //模糊查询参数

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hideStatusBarNavigationBar();
        setContentView(R.layout.activity_main);

        checkPermissions();                 //检查权限
        initFindView();
        mMainViewModel=new ViewModelProvider(this).get(MainViewModel.class);
        mMainViewModel.init();
        if(!mMainViewModel.getQueryAllSchedule().hasObservers()){
            mMainViewModel.getQueryAllSchedule().observe(MainActivity.this, new Observer<Resource<List<NodeInfo>>>() {
                @Override
                public void onChanged(Resource<List<NodeInfo>> listResource) {
                    if(listResource.getType() == QueryProcessType.query_successful){
                        nodesArray=listResource.getData();
                        adapter.dataSet(listResource.getData());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,"目标总数："+listResource.getData().size(),Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onChanged: getQueryAllSchedule().observer() is run");
                    }
                }
            });
        }
    }

//    private void hideStatusBarNavigationBar(){
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//            Window window=getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//            return;
//        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_btn){
            Intent intent=new Intent(this, AddToNodeActivity.class);
            startActivity(intent);
        }
    }

    private void initFindView(){
        cl_main=findViewById(R.id.cl_main);
        findViewById(R.id.fab_btn).setOnClickListener(this);
        tl_head=findViewById(R.id.tl_head);
        tv_fuzzy_query = findViewById(R.id.tv_fuzzy_query);

        setSupportActionBar(tl_head);
    }

    //初始化动态线性布局的循环视图
    private void initRecyclerDynamic(){
        //从布局文件中获取名叫rv_dynamic的循环视图
        rv_dynamic =findViewById(R.id.rv_dynamic);
        //创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        //设置循环视图的布局管理器
        rv_dynamic.setLayoutManager(manager);
        //构建一个笔记列表的线性适配器
        adapter=new LinearDynamicAdapter1(this,nodesArray);
        //设置线性列表的点击监听器
        adapter.setOnItemClickListener(this);
        //设置线性列表的长按监听器
        adapter.setOnItemLongClickListener(this);
        adapter.setOnItemPlayListener(this);
        //给rv_dynamic设置笔记列表的线性适配器
        rv_dynamic.setAdapter(adapter);
        //设置rv_dynamic的默认动画效果
        rv_dynamic.setItemAnimator(new DefaultItemAnimator());
        //给rv_dynamic添加列表项之间的空白装饰
        if(decoration!=null){
            rv_dynamic.removeItemDecoration(decoration);
        }
        decoration=new SpacesItemDecoration(50);
        rv_dynamic.addItemDecoration(decoration);
    }

    //根据菜单项初始化搜索框
    private void initSearchView(Menu menu){
        MenuItem menuItem=menu.findItem(R.id.menu_search);
        //从菜单项中获取搜索框对象
        SearchView searchView= (SearchView) menuItem.getActionView();
        //设置搜索框默认自动缩小为图标
        searchView.setIconifiedByDefault(getIntent().getBooleanExtra("collapse",true));
        // 设置是否显示搜索按钮。搜索按钮只显示一个箭头图标，Android暂不支持显示文本。
        searchView.setSubmitButtonEnabled(true);
        //设置搜索框的监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tv_fuzzy_query.setVisibility(View.GONE);
                fuzzyQueryStr=query;
                mMainViewModel.queryNodes(fuzzyQueryStr);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //从menu_search.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_search,menu);
        //初始化搜索框
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.menu_refresh) {              //点击了刷新图标
            Toast.makeText(this, "刷新成功，时间：" +
                    DateUtil.getNowDateTime("yyyy/MM/dd HH:mm:ss"), Toast.LENGTH_LONG).show();
            tv_fuzzy_query.setVisibility(View.GONE);
            mMainViewModel.queryNodes(null);
            return true;
        }
        else if(id==R.id.menu_about){           //点击了关于菜单项
            Toast.makeText(this, "这是个工具栏的演示demo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<NodeInfo> nodesArray=new ArrayList<>();

    //重新加载页面
    @Override
    protected void onResume(){
        super.onResume();
        mMainViewModel.onResume();
        initRecyclerDynamic();
    }

    //暂停页面
    @Override
    protected void onStop() {
        super.onStop();
        mMainViewModel.onStop();
    }

    //销毁页面
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainViewModel.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: id="+nodesArray.get(position)._id);
        Bundle bundle=new Bundle();                 //创建一个包裹
        bundle.putLong("_id",nodesArray.get(position)._id);
        Log.d(TAG, "onItemClick: _id is "+nodesArray.get(position)._id);
        Intent intent=new Intent(this,ModifyNodeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);                      //跳转到修改页面
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog=new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Light_Dialog);           //创建一个浅色的提示对话框
        dialog.setTitle("注意！");                                         //设置对话框标题文字
        dialog.setIcon(R.mipmap.ic_launcher);                           //设置对话框标题图标
        dialog.setMessage(String.format("是否删除“%s”这条备忘录？",nodesArray.get(position).title));
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {      //确定删除
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: nodesArray.size()="+nodesArray.size());
                NodeInfo info = nodesArray.get(position);       // 获取要删除的元素

                mMainViewModel.deleteNode(info._id);            // 删除数据库中对应的记录
                Log.d(TAG, "onClick: position is "+position);
                nodesArray.remove(position);                    // 删除列表中对应的元素
                adapter.notifyItemRemoved(position); // 通知适配器列表在第几项删除数据
                adapter.notifyItemRangeChanged(position, nodesArray.size() - position); // 更新列表

                if(!info.audioFilePath.equals("")){
                    deleteAudioFile(info.audioFilePath);
                }
                if(!info.remind.equals(getString(R.string.notRemind))) {            //如果该备忘录有设置提醒时间
                    CancellationNotifyUtil.deleteReminder(
                            MainActivity.this,AlarmReceiver.class,info.requestCode);       //取消该备忘录的提醒
                }

                Snackbar.make(cl_main, "成功删除：" + info.title, Snackbar.LENGTH_LONG).show(); // 页面提醒用户
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {      //取消删除
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void deleteAudioFile(String audioFilePath) {
        File audioFile = new File(audioFilePath);
        if (audioFile.exists()) {
            boolean deleted = audioFile.delete();
            if (deleted) {
                //Toast.makeText(this, "录音文件已删除", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "无法删除录音文件", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(this, "录音文件不存在", Toast.LENGTH_SHORT).show();
        }
    }


    private static boolean playerStopFlag =true;

    @Override
    public void onItemPlayerClick(String audioFilePath) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // 正在播放，暂停播放
            pausePlaying();
        } else if (mediaPlayer != null && !playerStopFlag) {
            // 已播放完毕，重新播放
            mediaPlayer.seekTo(0);  // 将播放进度设置为0，重新开始播放
            mediaPlayer.start();
        } else {
            // 播放音频
            startPlaying(audioFilePath);
        }
        Log.d(TAG, "onItemPlayerClick: audioFilePath is "+audioFilePath);
    }

    // 开始播放音频
    private void startPlaying(String audioFilePath) {
        mediaPlayer = new MediaPlayer();
        //Toast.makeText(this, audioFilePath, Toast.LENGTH_SHORT).show();

        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停播放音频
    private void pausePlaying() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // 停止播放音频
    private void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    //检查应用所需要的权限
    private void checkPermissions(){
        String[] permissions={
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SET_ALARM,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.VIBRATE,
                android.Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if(PermissionUtil.checkMultiPermission(this,permissions,0)){

        }
    }

    // 权限请求回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // 已获取到所有权限，继续应用的正常逻辑
            } else {
                // 未完全启用权限，向用户解释为何应用需要这些权限，并提供引导用户手动打开权限的选项
                //finish();
            }
        }
    }
}