package hesabyar.report_generator;

import io.realm.RealmObject;

public class ForooshRecordObject extends RealmObject {
    private int LadingNo;
    private int ItemNo;
    private String ItemName;
    private double Quantity;
    private double Weight;
    private long Fi;
    private long SumFi;

    public int getLadingNo() {
        return LadingNo;
    }

    public void setLadingNo(int ladingNo) {
        LadingNo = ladingNo;
    }

    public int getItemNo() {
        return ItemNo;
    }

    public void setItemNo(int itemNo) {
        ItemNo = itemNo;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public long getFi() {
        return Fi;
    }

    public void setFi(long fi) {
        Fi = fi;
    }

    public long getSumFi() {
        return SumFi;
    }

    public void setSumFi(long sumFi) {
        SumFi = sumFi;
    }
}
