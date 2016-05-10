package example.korailproject;

import java.io.Serializable;

/**
 * Created by Kim W on 2016-03-15.
 */
public class StationInfo implements Serializable{
    String st_station;
    String st_stationCode;

    public StationInfo(String st_station, String st_stationCode) {
        this.st_station = st_station;
        this.st_stationCode = st_stationCode;
    }
}
