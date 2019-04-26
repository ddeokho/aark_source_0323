package automacticphone.android.com.casebook.activity.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PromotionData
{
    private int seq;
    private int seq2;
    private int memberSeq;
    private int type;
    private String name;
    private String title;
    private String email;
    private String url;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
    private String logImg;
    private int feq;            //조회수
    private String timestamp;
    private int showState;
    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getShowState() {
        return showState;
    }

    public void setShowState(int showState) {
        this.showState = showState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq2() {
        return seq2;
    }

    public void setSeq2(int seq2) {
        this.seq2 = seq2;
    }

    public int getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(int memberSeq) {
        this.memberSeq = memberSeq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    //url 추가
    public String getUrl(){return url;}

    public void setUrl(String url) {
        this.url = url;
    }
    //

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogImg() {
        return logImg;
    }

    public void setLogImg(String logImg) {
        this.logImg = logImg;
    }

    public int getFeq() {
        return feq;
    }

    public void setFeq(int feq) {
        this.feq = feq;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
        try {
            this.dateTime = dt.parse(this.timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
