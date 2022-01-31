package hesabyar.report_generator;

import io.realm.RealmObject;

/**
 * Created by Mahmood_M on 17/01/2018.
 */

public class SanadRecordObject extends RealmObject {

    private int SID;
    private int AccNo;
    private String Ind;
    private int ResidNo;
    private String Tarikh;
    private String Sharh;
    private double Quantity;
    private double Weight;
    private long Fi;
    private long Bed;
    private long Bes;
    private long Mandeh;
    private String Status;

    public int getSID() {
        return SID;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getAccNo() {
        return AccNo;
    }

    public void setAccNo(int accNo) {
        AccNo = accNo;
    }

    public String getInd() {
        return Ind;
    }

    public void setInd(String ind) {
        Ind = ind;
    }

    public int getResidNo() {
        return ResidNo;
    }

    public void setResidNo(int residNo) {
        ResidNo = residNo;
    }

    public String getTarikh() {
        return Tarikh;
    }

    public void setTarikh(String tarikh) {
        Tarikh = tarikh;
    }

    public String getSharh() {
        return Sharh;
    }

    public void setSharh(String sharh) {
        Sharh = sharh;
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

    public long getBed() {
        return Bed;
    }

    public void setBed(long bed) {
        Bed = bed;
    }

    public long getBes() {
        return Bes;
    }

    public void setBes(long bes) {
        Bes = bes;
    }

    public long getMandeh() {
        return Mandeh;
    }

    public void setMandeh(long mandeh) {
        Mandeh = mandeh;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


}
