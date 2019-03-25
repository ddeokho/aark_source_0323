package automacticphone.android.com.casebook.activity.data;

public class SubTreeCaseData
{
    private int selectYear;
    private int cate_reg;
    private int cate_1;
    private int cate_2;
    private int cate_3;

    public SubTreeCaseData( int selectYear, int cate_reg, int cate_1, int cate_2, int cate_3 )
    {
        this.selectYear = selectYear;
        this.cate_reg = cate_reg;
        this.cate_1 = cate_1;
        this.cate_2 = cate_2;
        this.cate_3 = cate_3;
    }

    public int getSelectYear() {
        return selectYear;
    }

    public int getCate_reg() {
        return cate_reg;
    }

    public int getCate_1() {
        return cate_1;
    }

    public int getCate_2() {
        return cate_2;
    }

    public int getCate_3() {
        return cate_3;
    }
}
