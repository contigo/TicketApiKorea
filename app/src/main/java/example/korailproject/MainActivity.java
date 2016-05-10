package example.korailproject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    Button btn_startStation;
    Button btn_endStation;
    Button btn_search;
    ArrayList<TrainTicketInfo> mResult;
    ArrayList<TrainType> mTrainCategory;
    MainActivity mThis;
    URLRequest mRequest;

    //NAT010000
    //NAT011668
    StationInfo mStartStation;
    StationInfo mEndStation;
    String st_startName;
    String st_endName;
    TextView tv_result;
    TextView tv_startStationName;
    TextView tv_endStationName;
    Context mContext;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThis = this;
        mContext = this.getApplicationContext();
        Initialize();
        TrainCategorySet();

    }
    void TrainCategorySet()
    {
        mTrainCategory.add(new TrainType("00","KTX"));
        mTrainCategory.add(new TrainType("01","새마을호"));
        mTrainCategory.add(new TrainType("02","무궁화호"));
        mTrainCategory.add(new TrainType("03","통근열차"));
        mTrainCategory.add(new TrainType("04","누리로"));
        mTrainCategory.add(new TrainType("06", "공항직통"));
        mTrainCategory.add(new TrainType("07", "KTX-산천"));
        mTrainCategory.add(new TrainType("08","ITX-새마을"));
        mTrainCategory.add(new TrainType("09", "ITX-청춘"));
        mTrainCategory.add(new TrainType("10", "KTX-산천"));

    }
    void Initialize()
    {
        mTrainCategory = new ArrayList<>();
        mResult = new ArrayList<>();
        tv_result = (TextView)findViewById(R.id.tv_result);
        btn_endStation = (Button)findViewById(R.id.btn_endStation);
        btn_startStation = (Button)findViewById(R.id.btn_startStation);
        btn_search = (Button)findViewById(R.id.btn_SearchTicket);
        btn_endStation.setOnClickListener(this);
        btn_startStation.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        st_startName = null;
        st_endName = null;
        tv_startStationName = (TextView)findViewById(R.id.tv_startStation);
        tv_endStationName= (TextView)findViewById(R.id.tv_endStation);

        mStartStation = new StationInfo("서울역","NAT010000");
        mEndStation = new StationInfo("부산역","NAT014445");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId())
        {
            case R.id.btn_endStation:
                intent = new Intent(this,StationListActivity.class);
                startActivityForResult(intent,HandlerMessage.END_SET);
                break;
            case R.id.btn_startStation:
                intent = new Intent(this,StationListActivity.class);
                startActivityForResult(intent,HandlerMessage.START_SET);
                break;
            case R.id.btn_SearchTicket:
                mRequest = new URLRequest(mStartStation,mEndStation,mThis);
                mRequest.run();
                dialog = ProgressDialog.show(this, "",
                        "Loading", true);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case HandlerMessage.START_SET: //
                if(data != null)
                    mStartStation = (StationInfo)data.getSerializableExtra("stationInfo");

                tv_startStationName.setText(mStartStation.st_station);
                break;
            case HandlerMessage.END_SET:
                if(data !=null)
                    mEndStation = (StationInfo)data.getSerializableExtra("stationInfo");

                tv_endStationName.setText(mEndStation.st_station);
                break;
        }
    }

    public String makeDate(String rawString)
    {
        String result;
        String year = rawString.substring(0,4);
        String month = rawString.substring(4,6);
        String day = rawString.substring(6,8);
        String hour = rawString.substring(8,10);
        String min = rawString.substring(10,12);
        result = year + "년 " + month + "월 " + day + "일 "
                + hour + "시 " + min + "분";
        return result;
    }

    public String makeCity(String startStation,String endStation)
    {
        String result = startStation + " -> " + endStation;
        return result;
    }

    public String TrainCategory(String train)
    {
        for(int i = 0; i < mTrainCategory.size(); i++)
        {
            if(mTrainCategory.get(i).id.equals(train))
                return mTrainCategory.get(i).val;
        }
        return "화물열차";
    }
    public ArrayList<String> makeResult()
    {
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < mResult.size(); i++)
        {
            String endDate = mResult.get(i).arrPlandTime;
            String startDate = mResult.get(i).depPlandTime;
            startDate = makeDate(startDate);
            endDate = makeDate(endDate);
            String city = makeCity(mResult.get(i).depPlaceName,mResult.get(i).arrPlaceName);
            String pay = mResult.get(i).adultCharge;
            String trainCategory =TrainCategory(mResult.get(i).trainGradeName);

            result.add(startDate + " -> " + "\r\n"  + endDate + "\r\n" + city + "\r\n" + trainCategory + "  " + pay + "\r\n" + "\r\n");
        }
        if(result.size()!=0)
            return result;

        return null;
    }
    void ContentValueToArrayList(ArrayList<ContentValues> mData)
    {

        for(int j = 0; j < mData.size(); j++)
        {
            String trainGradeName = String.valueOf(mData.get(j).get("traingradename")); // 차량 종류
            String depPlandTime = String.valueOf(mData.get(j).get("depplandtime"));   // 출발 시간
            String arrPlandTime = String.valueOf(mData.get(j).get("arrplandtime"));   // 도착 시간
            String depPlaceName = String.valueOf(mData.get(j).get("depplacename"));  // 출발지
            String arrPlaceName = String.valueOf(mData.get(j).get("arrplacename")); // 도착지
            String adultCharge = String.valueOf(mData.get(j).get("adultcharge")); // 비용

            if(trainGradeName != null && depPlaceName != null && depPlandTime != null && arrPlandTime != null
                    && arrPlaceName != null && adultCharge != null) {
                TrainTicketInfo mTemp = new TrainTicketInfo(trainGradeName, depPlandTime, arrPlandTime, depPlaceName, arrPlaceName, adultCharge);
                mResult.add(mTemp);
            }
        }

    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerMessage.THREAD_HANDLER_SUCCESS_INFO:
                    ArrayList<ContentValues> mTrainTicekt = (ArrayList<ContentValues>)msg.obj;
                    ContentValueToArrayList(mTrainTicekt);
                    ArrayList<String> result_Text = makeResult();
                    if(result_Text == null) {
                        Toast.makeText(mContext,"역 운행정보가 없습니다",Toast.LENGTH_SHORT);
                        break;
                    }
                    String tv_SetResult = "";
                    for(int i = 0; i < result_Text.size(); i++)
                    {
                        String temp = result_Text.get(i);
                        tv_SetResult += temp;
                    }
                    tv_result.setText(tv_SetResult);

                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
