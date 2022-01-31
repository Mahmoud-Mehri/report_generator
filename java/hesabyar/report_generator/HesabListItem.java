package hesabyar.report_generator;

import java.util.Date;

/**
 * Created by Mahmood_M on 07/01/2018.
 */

public class HesabListItem {
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
    private int Status;

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

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
