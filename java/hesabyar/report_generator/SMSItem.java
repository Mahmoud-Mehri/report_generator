package hesabyar.report_generator;

import java.io.Serializable;

public class SMSItem implements Serializable {
    private String AccName;
    private String PhoneNumber;
    private String SMSMessage;
    private int Status;

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

    public String getSMSMessage() {
        return SMSMessage;
    }

    public void setSMSMessage(String SMSMessage) {
        this.SMSMessage = SMSMessage;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
