package net.bigtangle.wallet.activity.market;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

import net.bigtangle.wallet.R;

public class MarketActivity extends Activity implements OnClickListener{

    private TextInputEditText startShowDialog;

    private TextInputEditText endShowDialog;

    private Calendar cal;

    private int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_market_order);

        //获取当前日期
        getDate();
        // 开始时间
        startShowDialog=(TextInputEditText) findViewById(R.id.startShowDialog);
        startShowDialog.setOnClickListener(this);
        // 结束时间
        endShowDialog=(TextInputEditText) findViewById(R.id.endShowDialog);
        endShowDialog.setOnClickListener(this);


    }

    //获取当前日期
    private void getDate() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startShowDialog:
                OnDateSetListener startListener=new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                        startShowDialog.setText(year+"-"+(++month)+"-"+day);
                    }
                };
                //主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                DatePickerDialog startDialog=new DatePickerDialog(MarketActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,startListener,year,month,day);
                startDialog.show();
                break;
            case R.id.endShowDialog:
                OnDateSetListener endListener=new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                        startShowDialog.setText(year+"-"+(++month)+"-"+day);
                    }
                };
                //主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                DatePickerDialog endDialog=new DatePickerDialog(MarketActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,endListener,year,month,day);
                endDialog.show();
                break;

            default:
                break;
        }
    }

}
