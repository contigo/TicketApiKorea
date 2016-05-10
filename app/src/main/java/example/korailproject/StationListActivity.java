package example.korailproject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Kim W on 2016-04-20.
 */
public class StationListActivity extends AppCompatActivity {

    EditText et_searchStation;
    ListView lv_stationList;
    ArrayList<StationInfo> mInfo;
    AssetManager mag;
    String name_buffer;
    String code_buffer;
    StationListAdapter mAdapter;
    Intent mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        onInit();
        ReadStation();
        mAdapter = new StationListAdapter(this,android.R.layout.simple_list_item_1, mInfo);
        lv_stationList.setAdapter(mAdapter);

    }
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerMessage.THREAD_HANDLER_SUCCESS_INFO:
                    mResult = getIntent();
                    StationInfo tResult = (StationInfo)msg.obj;
                    mResult.putExtra("stationInfo", tResult);
                    setResult(RESULT_OK,mResult);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };




    void onInit()
    {
        mag = this.getResources().getAssets();
        mInfo = new ArrayList<>();
        et_searchStation = (EditText)findViewById(R.id.et_stationSearch);
        et_searchStation.addTextChangedListener((new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = et_searchStation.getText().toString();
                mAdapter.Filter(temp);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));
        lv_stationList = (ListView)findViewById(R.id.lv_stationList);
    }

    void ReadStation()
    {
        try {
            InputStream name = mag.open("InfoTrainStation.txt");
            InputStream code = mag.open("InfoTrainStationCode.txt");
            int nameSize = name.available();
            int codeSize = code.available();

            byte[] buffer = new byte[nameSize];
            byte[] buffer2 = new byte[codeSize];

            name.read(buffer);
            code.read(buffer2);

            name.close();
            code.close();

            name_buffer = new String(buffer);
            code_buffer = new String(buffer2);

            StringToToken(name_buffer,code_buffer);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }


    }
    void StringToToken(String name,String code)
    {
        StringTokenizer mToken = new StringTokenizer(name, "\r\n");
        StringTokenizer mToken2 = new StringTokenizer(code, "\r\n");

        while(mToken.hasMoreTokens() )
        {
            String mName = mToken.nextToken();
            String mCode = mToken2.nextToken();

            StationInfo temp = new StationInfo(mName+"역",mCode);
            mInfo.add(temp);
        }
    }
}
