package automacticphone.android.com.casebook.activity.data;

public class CommentData
{
    private int seq;
    private int memberSeq;
    private int exampleSeq;
    private String imgName;
    private String content;
    private String timestamp;
    private String memberID = "";

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(int memberSeq) {
        this.memberSeq = memberSeq;
    }

    public int getExampleSeq() {
        return exampleSeq;
    }

    public void setExampleSeq(int exampleSeq) {
        this.exampleSeq = exampleSeq;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
