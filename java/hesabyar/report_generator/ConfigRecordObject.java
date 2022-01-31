package hesabyar.report_generator;

import io.realm.RealmObject;

/**
 * Created by Mahmood_M on 17/01/2018.
 */

public class ConfigRecordObject extends RealmObject {

    private String Name;
    private String Address;
    private String Phone;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
