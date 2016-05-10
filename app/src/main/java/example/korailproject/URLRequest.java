package example.korailproject;

import android.content.ContentValues;
import android.os.Message;
import android.os.StrictMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Kim W on 2016-04-20.
 */
public class URLRequest extends Thread {

    StationInfo mStart;
    StationInfo mEnd;
    Calendar mCal;
    String date = "";
    ArrayList<ContentValues> mResult;
    String mXmlList[] = {"adultcharge", "arrplacename", "arrplandtime", "depplacename",
    "depplandtime", "traingradename"};
    MainActivity mHandler;

    String makeString(int val)
    {
        String temp = String.valueOf(val);
        if(val >= 1 && val < 10)
            temp = "0"+val;

        return temp;
    }


    public URLRequest(StationInfo mStart, StationInfo mEnd,MainActivity mHandler) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        mCal = Calendar.getInstance();
        String year = String.valueOf(mCal.get(Calendar.YEAR));
        String month = makeString(mCal.get(Calendar.MONTH) + 1);
        String day = makeString(mCal.get(Calendar.DAY_OF_MONTH));
        date =  year + month + day;
        mResult = new ArrayList<>();
        this.mHandler = mHandler;
    }
    boolean isPossibleTagName(String tag)
    {
        if(tag == null) return false;


        for(int i = 0; i < mXmlList.length; i++)
            if(tag.equals(mXmlList[i])) return true;

        return false;

    }
    boolean CheckEndItem(int event,String name)
    {
        if(event == XmlPullParser.END_TAG)
        {
            if(name != null) {
                name.equals("items");
                return true;
            }
        }
        return false;
    }
    //getVhcleKndList
    int RequestURL(int pageNo)
    {
        if(mStart == null || mEnd == null ) return 0;
        String st_key = "API KEY 입력";
        int idx = 0;
        try {

                URL url = new URL("http://openapi.tago.go.kr/openapi/service/TrainInfoService/getStrtpntAlocFndTrainInfo?ServiceKey=" + st_key +
                        "&numOfRows=100" +
                        "&pageNo=" + pageNo +
                        "&depPlaceId=" + mStart.st_stationCode +
                        "&arrPlaceId=" + mEnd.st_stationCode +
                        "&depPlandTime=" + date
                );

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨

                XmlPullParser parser = factory.newPullParser();
                // XML Resource를 파싱할 parser를 factory로 생성

                parser.setInput(url.openStream(), null);
                // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행

                int parserEvent = parser.getEventType();

                String tagName;
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    // XML문이 끝날 때 까지 정보를 읽는다
                    if (parserEvent == XmlPullParser.START_TAG) {
//                    TrainInfo train = new TrainInfo();
                        //시작태그의 이름을 알아냄
                        ContentValues mContent = new ContentValues();

                       for( ; ; ) { // 하나의 item이 끝날 때, 들어감.
                            tagName = parser.getName();

                            if (!isPossibleTagName(tagName)) {
                                if(CheckEndItem(parserEvent,tagName)) break;

                                parserEvent = parser.next();
                                continue;
                            }

                            for (int index = 0; index < mXmlList.length; index++) {
                                if (parserEvent == parser.START_TAG && tagName.equals(mXmlList[index]) && mXmlList.length - 1 == index) {
//                                    mContent.put(mXmlList[index], parser.getAttributeValue(null, mXmlList[index]));
                                    mContent.put(mXmlList[index], parser.nextText());
                                    mResult.add(mContent);
                                    idx++;
                                    break;
                                } else if (parserEvent == parser.START_TAG && tagName.equals(mXmlList[index])) {
//                                    mContent.put(mXmlList[index], parser.getAttributeValue(null, mXmlList[index]));
                                    mContent.put(mXmlList[index], parser.nextText());
                                    break;
                                }
                            }
                            parserEvent = parser.next();
                        }
                    }
                    parserEvent = parser.next();

                }
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(XmlPullParserException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return idx;
    }

    @Override
    public void run() {
        super.run();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        for(int i = 1; ;i++)
            if(RequestURL(i) == 0)  break;

        Message msg= new Message();
        msg.what = HandlerMessage.THREAD_HANDLER_SUCCESS_INFO;
        msg.obj = mResult;
        mHandler.handler.sendMessage(msg);
        //Thread 작업 종료, UI 작업을 위해 MainHandler에 Message보냄    }
    }
}
