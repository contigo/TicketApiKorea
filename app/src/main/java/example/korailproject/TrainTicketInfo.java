package example.korailproject;

/**
 * Created by Kim W on 2016-03-15.
 */
public class TrainTicketInfo {
    String trainGradeName; // 차량 종류
    String depPlandTime;   // 출발 시간
    String arrPlandTime;   // 도착 시간
    String depPlaceName;  // 출발지
    String arrPlaceName; // 도착지
    String adultCharge; // 비용

    public TrainTicketInfo(String trainGradeName, String depPlandTime, String arrPlandTime, String depPlaceName, String arrPlaceName, String adultCharge) {
        this.trainGradeName = trainGradeName;
        this.depPlandTime = depPlandTime;
        this.arrPlandTime = arrPlandTime;
        this.depPlaceName = depPlaceName;
        this.arrPlaceName = arrPlaceName;
        this.adultCharge = adultCharge;
    }
}
