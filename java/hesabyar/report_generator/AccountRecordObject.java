package hesabyar.report_generator;

import java.nio.charset.Charset;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Mahmood_M on 17/01/2018.
 */

public class AccountRecordObject extends RealmObject {

    private int AccNo;
    private String AccName;
    private String PhoneNumber;

    public int getAccNo() {
        return AccNo;
    }

    public void setAccNo(int accNo) {
        AccNo = accNo;
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
}
