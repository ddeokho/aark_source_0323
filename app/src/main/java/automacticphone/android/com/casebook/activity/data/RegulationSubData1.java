package automacticphone.android.com.casebook.activity.data;

public class RegulationSubData1 {
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getRegul() {
        return regul;
    }

    public void setRegul(String regul) {
        this.regul = regul;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCateMainSeq() {
        return cateMainSeq;
    }

    public void setCateMainSeq(int cate_main_seq) {
        this.cateMainSeq = cate_main_seq;
    }

    private int seq;
    private int cateMainSeq;
    private String regul;
    private String content;
}
