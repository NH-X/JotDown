package com.example.jotdown.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jotdown.R;

import java.util.Calendar;

public class ChooseDateDialog implements View.OnClickListener {
    private Dialog dialog;                  //声明一个对话框对象
    private View view;                      //声明一个视图对象
    private DatePicker dp_date;             //声明一个日期选择器对象
    private TimePicker tp_time;             //声明一个时间选择器对象
    private TextView tv_display_date;       //声明一个显示选择的日期
    private TextView tv_display_time;       //声明一个显示选择的时间

    private boolean flag=false;

    public ChooseDateDialog(Context context){
        //根据布局文件dialog_date.xml生成视图对象
        view= LayoutInflater.from(context).inflate(R.layout.dialog_date,null);
        //创建一个指定风格的对话框对象
        dialog=new Dialog(context,R.style.ChooseDateDialog);
        dp_date=view.findViewById(R.id.dp_date);
        tp_time=view.findViewById(R.id.tp_time);

        tv_display_date=view.findViewById(R.id.tv_display_date);
        tv_display_time=view.findViewById(R.id.tv_display_time);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);            //设置取消按键的点击监听器
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);           //设置确定按键的点击监听器
        tv_display_time.setOnClickListener(this);
        tv_display_date.setOnClickListener(this);

        dp_date.setVisibility(View.VISIBLE);
        tp_time.setVisibility(View.GONE);
    }

    //设置日期对话框内部的年、月、日，以及日期变更监听器
    public void setDate(int year,int month,int day,OnDateSetListener listener){
        dp_date.init(year,month,day,null);
        mDateSetListener=listener;
    }

    //设置时间对话框的时间变更监听器
    public void setTime(OnTimeSetListener listener){
        mTimeSetListener=listener;
    }

    //显示对话框
    public void show(){
        //设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        //设置对话框窗口的布局参数
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();              //显示对话框
    }

    //关闭对话框
    public void dismiss(){
        //如果对话框显示出来了，就关闭它
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    //判断对话框是否显示
    public boolean isShowing(){
        if(dialog!=null){
            return dialog.isShowing();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        int selectedYear = 0;
        int selectedMonth =0;
        int selectedDay = 0;
        int selectedHour = 0;
        int selectedMinute = 0;

        if(id==R.id.tv_display_date){               //点击了日期选择器
            dp_date.setVisibility(View.VISIBLE);
            tp_time.setVisibility(View.GONE);
            if(flag) {
                selectedHour= tp_time.getHour();
                selectedMinute = tp_time.getMinute();
                tv_display_time.setText(String.format("%s:%s",
                        selectedHour>=10?selectedHour:"0"+selectedHour,
                        selectedMinute>=10?selectedMinute:"0"+selectedMinute));
            }
            flag=false;
        }
        else if(id==R.id.tv_display_time){          //点击了时间选择器
            tp_time.setVisibility(View.VISIBLE);
            dp_date.setVisibility(View.GONE);
            if(!flag){
                selectedYear = dp_date.getYear();
                selectedMonth = dp_date.getMonth() + 1;
                selectedDay = dp_date.getDayOfMonth();
                tv_display_date.setText(String.format("%s-%s-%s",selectedYear,
                        selectedMonth>=10?selectedMonth:"0"+selectedMonth,
                        selectedDay>=10?selectedDay:"0"+selectedDay));
            }
            flag=true;
        }
        else if(id==R.id.btn_cancel){               //点击了取消按钮
            dismiss();
        }
        else if(id==R.id.btn_confirm) {              //点击了确定按钮
            Calendar currentDateTime = Calendar.getInstance();
            int currentYear = currentDateTime.get(Calendar.YEAR);
            int currentMonth = currentDateTime.get(Calendar.MONTH) + 1;
            int currentDay = currentDateTime.get(Calendar.DAY_OF_MONTH);
            int currentHour = currentDateTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentDateTime.get(Calendar.MINUTE);

            selectedYear = dp_date.getYear();
            selectedMonth = dp_date.getMonth() + 1;
            selectedDay = dp_date.getDayOfMonth();
            selectedHour = tp_time.getHour();
            selectedMinute = tp_time.getMinute();

            if(selectedYear>currentYear || (selectedYear==currentYear && (selectedMonth>currentMonth || (
                    selectedMonth==currentMonth && (selectedDay>currentDay || (selectedDay==currentDay &&
                            selectedHour>currentHour || (selectedHour==currentHour &&
                            (selectedMinute>currentMinute)))))))){
                mDateSetListener.onDateSet(dp_date.getYear(), dp_date.getMonth()+1, dp_date.getDayOfMonth());
                mTimeSetListener.onTimeSet(tp_time.getHour(), tp_time.getMinute());
                dismiss();
            }
            else {
                Toast.makeText(dialog.getContext(), "请选择一个合法的日期时间", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    //声明一个时间变更的监听器对象
    private OnDateSetListener mDateSetListener;

    //定义一个日期变更的监听器接口
    public interface OnDateSetListener{
        void onDateSet(int year,int month,int dayOfMonth);
    }

    //声明一个时间变更的监听器对象
    private OnTimeSetListener mTimeSetListener;

    //定义一个时间变更的监听器接口
    public interface OnTimeSetListener{
        void onTimeSet(int hour,int minute);
    }
}
