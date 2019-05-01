package automacticphone.android.com.casebook.activity.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import automacticphone.android.com.casebook.activity.common.DataManager;


public class AnnounceData {
    private int seq;
    private String content;
    private String grade;
    private String timeStamp;
    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public int getSeq() {
        return seq;
    }

    public String getContent() {
        return content;
    }

    public String getGrade() {
        return grade;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm");
        try {
            this.dateTime = dt.parse(this.timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
