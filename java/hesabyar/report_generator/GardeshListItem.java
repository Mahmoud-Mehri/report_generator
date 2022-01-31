package hesabyar.report_generator;

import java.util.Date;

/**
 * Created by Mahmood_M on 07/01/2018.
 */

public class GardeshListItem {

    private int AccNumber;
    private String AccName;
    private String PhoneNumber;
    private long Price;
    private int Status;
    private boolean SelectedForSMS;

    public int getAccNumber() {
        return AccNumber;
    }

    public void setAccNumber(int accNumber) {
        AccNumber = accNumber;
    }

    public String getAccName() {
        return AccName;
    }

    public void setAccName(String accName) {
        AccName = accName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public long getPrice() {
        return Price;
    }

    public void setPrice(long price) {
        Price = price;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isSelectedForSMS() {
        return SelectedForSMS;
    }

    public void setSelectedForSMS(boolean selectedForSMS) {
        SelectedForSMS = selectedForSMS;
    }
}
