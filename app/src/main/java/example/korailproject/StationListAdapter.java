package example.korailproject;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kim W on 2016-04-28.
 */
public class StationListAdapter extends ArrayAdapter<StationInfo> implements View.OnClickListener {
    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
    //자음
    private static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

    ArrayList<StationInfo> mData;
    ArrayList<StationInfo> mTemp;
    Context mContext;
    StationListActivity mStationActivity;
    public StationListAdapter(Context context, int resource, ArrayList<StationInfo> objects) {
        super(context, resource, objects);
        mData = objects;
        mStationActivity = (StationListActivity)context;
        mContext = context;
        Sort_String();
        mTemp = new ArrayList<>();
        mTemp.addAll(mData);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        parent.invalidate();
        View v = convertView;

        final int set = position;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.trainitem1, null);
        }
        v.setTag(position);
        v.setOnClickListener(this);

        if(mData.size() <= position) return v;

        final StationInfo p = mData.get(position);
        if (p != null) {
            TextView tv = (TextView) v.findViewById(R.id.tv_stationList);

            if (tv != null) {
                tv.setText(p.st_station);
            }

        }
        return v;
    }


    @Override
    public void onClick(View v) {
        Message msg = new Message();
        int position = (int)v.getTag();
         msg.obj = mData.get(position);
         msg.what = HandlerMessage.THREAD_HANDLER_SUCCESS_INFO;
         mStationActivity.handler.sendMessage(msg);

    }

    public void Filter(String data)
    {
        if(data.length() == 0) {
            mData.clear();
            mData.addAll(mTemp);
            Sort_String();
            return ;
        }
        mData.clear();
        for(StationInfo temp : mTemp)
        {
            if(matchString(temp.st_station,data))
                mData.add(temp);
        }

        Sort_String();
        notifyDataSetChanged();
    }
    private  boolean isInitialSound(char searchar){
        for(char c:INITIAL_SOUND){
            if(c == searchar){
                return true;
            }
        }
        return false;
    }

    public  boolean matchString(String value, String search){
        int t = 0;
        int seof = value.length() - search.length();
        int slen = search.length();
        if(seof < 0)
            return false; //검색어가 더 길면 false를 리턴한다.
        for(int i = 0;i <= seof;i++){
            t = 0;
            while(t < slen){
                if(isInitialSound(search.charAt(t))==true && isHangul(value.charAt(i+t))){
                    //만약 현재 char이 초성이고 value가 한글이면
                    if(getInitialSound(value.charAt(i+t))==search.charAt(t))
                        //각각의 초성끼리 같은지 비교한다
                        t++;
                    else
                        break;
                } else {
                    //char이 초성이 아니라면
                    if(value.charAt(i+t)==search.charAt(t))
                        //그냥 같은지 비교한다.
                        t++;
                    else
                        break;
                }
            }
            if(t == slen)
                return true; //모두 일치한 결과를 찾으면 true를 리턴한다.
        }
        return false; //일치하는 것을 찾지 못했으면 false를 리턴한다.
    }

    private  boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    private  char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    void Sort_String()
    {
        final Comparator<StationInfo> myComparator= new Comparator<StationInfo>() {
            private final Collator collator = Collator.getInstance();
            @Override
            public int compare(StationInfo object1,StationInfo object2) {
                return collator.compare(object1.st_station, object2.st_station);
            }
        };
        // Collections.sort 로 comparator 를 주어서 데이터를 정렬 시킨다.
        Collections.sort(mData, myComparator);
    }
}
