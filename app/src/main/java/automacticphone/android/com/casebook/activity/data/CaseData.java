package automacticphone.android.com.casebook.activity.data;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import automacticphone.android.com.casebook.activity.common.DataManager;

public class CaseData
{
    private int seq;
    private int member_seq;
    private int year;
    private String title;
    private String content;
    private int cate_reg;
    private int cate_1;
    private int cate_2;
    private int cate_3;
    private int feq;
    private int grade;
    private String img_1;
    private String img_2;
    private String img_3;
    private String timestamp;
    private Date dateTime;
    private int comment_count;

    public Date getDateTime() {
        return dateTime;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getMember_seq() {
        return member_seq;
    }

    public void setMember_seq(int member_seq) {
        this.member_seq = member_seq;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCate_reg() {
        return cate_reg;
    }

    public void setCate_reg(int cate_reg) {
        this.cate_reg = cate_reg;
    }

    public int getCate_1() {
        return cate_1;
    }

    public void setCate_1(int cate_1) {
        this.cate_1 = cate_1;
    }

    public int getCate_2() {
        return cate_2;
    }

    public void setCate_2(int cate_2) {
        this.cate_2 = cate_2;
    }

    public int getCate_3() {
        return cate_3;
    }

    public void setCate_3(int cate_3) {
        this.cate_3 = cate_3;
    }

    public int getFeq() {
        return feq;
    }

    public void setFeq(int feq) {
        this.feq = feq;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getImg_1() {
        return img_1;
    }

    public void setImg_1(String img_1) {
        this.img_1 = img_1;
    }

    public String getImg_2() {
        return img_2;
    }

    public void setImg_2(String img_2) {
        this.img_2 = img_2;
    }

    public String getImg_3() {
        return img_3;
    }

    public void setImg_3(String img_3) {
        this.img_3 = img_3;
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

    public boolean IsExistImg()
    {
        if( getImg_1().length() > 0 || getImg_2().length() > 0 || getImg_3().length() > 0 )
            return true;

        return false;
    }

    //
    public String getCategoryText()
    {
        String categoryText = "";
        RegulationMainData regulMainData = DataManager.inst().getRegulationMainData( cate_reg );
        if( regulMainData != null )
            categoryText = categoryText.concat( regulMainData.getRegul().substring(0, regulMainData.getRegul().length()-5) );

        RegulationSubData2 subData2 = DataManager.inst().getRegulSubData2( cate_2);
        RegulationSubData3 subData3 = DataManager.inst().getRegulSubData3( cate_3 );
        if( subData2 != null )
        {
            if( subData3 != null )
            {
                if( subData3.getRegul().equals(".") )
                    categoryText = categoryText.concat( ":"+subData2.getRegul() );
                else
                    categoryText = categoryText.concat( ":"+subData3.getRegul() );
            }
            else
                categoryText = categoryText.concat( ":"+subData2.getRegul() );
        }

        return categoryText;
    }

    //댓글
    public int getComment_count(){return comment_count;}
    public void setComment_count(int comment_count){this.comment_count=comment_count;}

    //
}
