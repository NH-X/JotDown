package com.example.jotdown;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jotdown.adapter.LinearDynamicAdapter;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.DateUtil;
import com.example.jotdown.widget.RecyclerExtras.OnItemClickListener;
import com.example.jotdown.widget.RecyclerExtras.OnItemLongClickListener;
import com.example.jotdown.widget.SpacesItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener ,OnItemLongClickListener{
    private static final String TAG="MainActivity";
    private CoordinatorLayout cl_main;
    private RecyclerView rv_dynamic;
    private Toolbar tl_head;
    private TextView tv_fuzzy_query;

    private SpacesItemDecoration decoration;                //item之间的间距

    private LinearDynamicAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl_main=findViewById(R.id.cl_main);
        findViewById(R.id.fab_btn).setOnClickListener(this);
        tl_head=findViewById(R.id.tl_head);
        tv_fuzzy_query = findViewById(R.id.tv_fuzzy_query);

        setSupportActionBar(tl_head);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_btn){
            Intent intent=new Intent(this,RenewNodeActivity.class);
            startActivity(intent);
        }
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
        adapter=new LinearDynamicAdapter(this,nodesArray);
        //设置线性列表的点击监听器
        adapter.setOnItemClickListener(this);
        //设置线性列表的长按监听器
        adapter.setOnItemLongClickListener(this);
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
                fuzzyQueryStr=query;
                refresh.post(fuzzyQuery);
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
            Snackbar.make(cl_main, "刷新成功，时间：" +
                    DateUtil.getNowDateTime("yyyy/MM/dd HH:mm:ss"), Snackbar.LENGTH_LONG).show();
            tv_fuzzy_query.setVisibility(View.GONE);
            refresh.post(queryAll);
            Toast.makeText(this, "数据库数据条数："+nodesArray.size(), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id==R.id.menu_about){           //点击了关于菜单项
            Toast.makeText(this, "这是个工具栏的演示demo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private NodesDBHelper helper;               //数据库帮助器
    private List<NodeInfo> nodesArray;

    //重新加载页面
    @Override
    protected void onResume(){
        super.onResume();
        helper=MainApplication.getNodesDBHelper();
        refresh.post(queryAll);                 //分支线程查询数据
    }

    //暂停页面
    @Override
    protected void onStop() {
        super.onStop();
    }

    //销毁页面
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(helper!=null){
            helper.close();                         //关闭数据库
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: id="+nodesArray.get(position)._id);
        Bundle bundle=new Bundle();                 //创建一个包裹
        bundle.putInt("_id",nodesArray.get(position)._id);
        Intent intent=new Intent(this,RenewNodeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);                      //跳转到修改页面
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
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

                if(!helper.writeIsOpen()){                      //如果写数据库没有打开，进行打开写数据库
                    helper.getWriteDB();
                }
                helper.deleteById(info._id);                        // 删除数据库中对应的记录
                nodesArray.remove(position);                    // 删除列表中对应的元素
                adapter.notifyItemRemoved(position); // 通知适配器列表在第几项删除数据
                adapter.notifyItemRangeChanged(position, nodesArray.size() - position); // 更新列表

                if(!info.remind.equals(getString(R.string.notRemind))) {            //如果该备忘录有设置提醒时间
                    CancellationNotifyUtil.deleteReminder(
                            MainActivity.this,AlarmReceiver.class,info.requestCode);       //取消该备忘录的提醒
                }

                Snackbar.make(cl_main, "成功删除：" + info.title, Snackbar.LENGTH_LONG).show(); // 页面提醒用户
                Log.d(TAG, "onClick: position="+position);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {      //取消删除
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        dialog.show();
    }

    private Handler refresh=new Handler();                      //创建一个处理器对象

    private String fuzzyQueryStr="";                            //模糊查询参数

    private Runnable fuzzyQuery=new Runnable() {                //模糊查询
        @Override
        public void run() {
            if(!helper.readIsOpen()){
                Log.d(TAG, "run: readDB is close");
                helper.getReadDB();
            }
            if(fuzzyQueryStr.equals("")){                       //如果查询参数为空，则获取全部数据。
                nodesArray=helper.queryInfoAll();
            }
            else{
                nodesArray=helper.queryLikeInfo(fuzzyQueryStr);
            }
            Log.d(TAG, "fuzzyQuery: nodesArray is null? "+(nodesArray==null));
            Snackbar.make(cl_main,"查询条件为："+fuzzyQueryStr+"符合条数为："+nodesArray.size(),Snackbar.LENGTH_LONG).show();

            if(nodesArray.size()<=0) {
                tv_fuzzy_query.setVisibility(View.VISIBLE);
                tv_fuzzy_query.setText(String.format("没有%s的搜索结果。\n请尝试检查您的拼写或使用关键词进行搜索",fuzzyQueryStr));
            }

            initRecyclerDynamic();
        }
    };

    private Runnable queryAll=new Runnable() {                  //查询所有
        @Override
        public void run() {
            if(!helper.readIsOpen()){
                Log.d(TAG, "run: readDB is close");
                helper.getReadDB();
            }
            nodesArray=helper.queryInfoAll();
            Log.d(TAG, "run: nodesArray is null? "+(nodesArray==null));
            Log.d(TAG, "run: nodeArraySize:"+nodesArray.size());
            initRecyclerDynamic();
        }
    };
}