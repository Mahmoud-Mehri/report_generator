package hesabyar.report_generator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Mahmood_M on 17/01/2018.
 */

public class Importer {

    RealmConfiguration RealmConfig;

    private Context mContext;

//    private String[] JSONList;
//    private JSONArray accounts;
//    private JSONArray sanads;
//    private JSONObject config;

    private JSONObject JObject;
    int ObjectType = 0;

    BufferedReader br;
    boolean Next = true;
    boolean DeleteCurrentData = true;

    String FileAddr;

    Handler progressHandler;
    Bundle progressBundle;
    Message progressMsg;

    String FieldDelimiter = "Ä";

    public Importer(Context context){
        mContext = context;
    }

    public boolean ImportFromFile(String FileName, boolean deleteCurrentData, Handler h){
        FileAddr = FileName;
        DeleteCurrentData = deleteCurrentData;
        progressHandler = h;

        progressBundle = new Bundle();
        progressMsg = new Message();

        if (RealmConfig == null) {
            RealmConfig = new RealmConfiguration.Builder()
                    .name("info.realm")
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }

        Realm realm = Realm.getInstance(RealmConfig);

        realm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                Next = true;

                if(DeleteCurrentData){
                    try {
                        realm.delete(AccountRecordObject.class);
                        realm.delete(SanadRecordObject.class);
                        realm.delete(ConfigRecordObject.class);
                        realm.delete(BarnamehRecordObject.class);
                        realm.delete(ForooshRecordObject.class);
                    }catch (Exception E){
                        Next = false;
                        E.printStackTrace();
                    }
                }

                if(Next) {
                    try {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(FileAddr), "UTF-8"));
                        String line;
                        line = br.readLine();
                        int max = Integer.parseInt(line) + 6;
                        progressBundle.putInt("Max", max);
                        progressBundle.putInt("Progress", 0);
                        progressMsg.setData(progressBundle);
                        progressHandler.sendMessage(Message.obtain(progressMsg));

                        while ((line = br.readLine()) != null) {
                            if (line.equals("[Accounts]")) {
                                ObjectType = 1;
                            } else if (line.equals("[Sanads]")) {
                                ObjectType = 2;
                            } else if (line.equals("[Barnameh]")) {
                                ObjectType = 3;
                            } else if (line.equals("[Foroosh]")) {
                                ObjectType = 4;
                            } else if (line.equals("[Config]")) {
                                ObjectType = 5;
                            } else {
//                                char q = '"';
                                char s = '\\';
//                                char d = ':';
//                                char c = ',';
//                                char b = '}';
                                line = line.replace(""+s, "");
//                                line = line.replace(""+q+c, ""+s+q+c);
//                                line = line.replace(""+q+b, ""+s+q+b);

                                String Fields[] = line.split(FieldDelimiter);

//                                JObject = new JSONObject(line);
                                switch (ObjectType) {
                                    case 1: {
                                        AccountRecordObject obj = realm.createObject(AccountRecordObject.class);
                                        obj.setAccNo(Integer.parseInt(Fields[0]));
                                        obj.setAccName(Fields[1]);
                                        obj.setPhoneNumber(Fields[2]);
                                    }break;
                                    case 2: {
                                        SanadRecordObject obj = realm.createObject(SanadRecordObject.class);
                                        obj.setSID(Integer.parseInt(Fields[0]));
                                        obj.setAccNo(Integer.parseInt(Fields[1]));
                                        obj.setInd(Fields[2]);
                                        obj.setResidNo(Integer.parseInt(Fields[3]));
                                        obj.setTarikh(Fields[4]);
                                        obj.setSharh(Fields[5]);
                                        obj.setQuantity(Double.parseDouble(Fields[6]));
                                        obj.setWeight(Double.parseDouble(Fields[7]));
                                        obj.setFi(Long.parseLong(Fields[8]));
                                        obj.setBed(Long.parseLong(Fields[9]));
                                        obj.setBes(Long.parseLong(Fields[10]));
                                        obj.setMandeh(Long.parseLong(Fields[11]));
                                        obj.setStatus(Fields[12]);
                                    }break;
                                    case 3: {
                                        BarnamehRecordObject obj = realm.createObject(BarnamehRecordObject.class);
                                        obj.setLadingNo(Integer.parseInt(Fields[0]));
                                        obj.setAccNo(Integer.parseInt(Fields[1]));
                                        AccountRecordObject acc = realm.where(AccountRecordObject.class).equalTo("AccNo", Integer.parseInt(Fields[1])).findFirst();
                                        String AccName = "-";
                                        String PhoneNumber = "-";
                                        if(acc != null) {
                                            AccName = acc.getAccName();
                                            PhoneNumber = acc.getPhoneNumber();
                                        }
                                        obj.setAccName(AccName);
                                        obj.setPhoneNumber(PhoneNumber);
                                        obj.setTarikh(Fields[2]);
                                        obj.setResidNo(Integer.parseInt(Fields[3]));
                                        obj.setCarNo(Fields[4]);
                                        obj.setInCount(Double.parseDouble(Fields[5]));
                                        obj.setInWeight(Double.parseDouble(Fields[6]));
                                        obj.setBaskul1(Double.parseDouble(Fields[7]));
                                        obj.setBaskul2(Double.parseDouble(Fields[8]));
                                        obj.setSood(Long.parseLong(Fields[9]));
                                        obj.setSafi(Long.parseLong(Fields[10]));
                                        obj.setKeraye(Long.parseLong(Fields[11]));
                                        obj.setTakhlie(Long.parseLong(Fields[12]));
                                        obj.setPeybari(Long.parseLong(Fields[13]));
                                        obj.setDasti(Long.parseLong(Fields[14]));
                                        obj.setMotafarregheh(Long.parseLong(Fields[15]));
                                        obj.setNote(Fields[16]);
                                    }break;
                                    case 4: {
                                        ForooshRecordObject obj = realm.createObject(ForooshRecordObject.class);
                                        obj.setLadingNo(Integer.parseInt(Fields[0]));
                                        obj.setItemNo(Integer.parseInt(Fields[1]));
                                        obj.setItemName(Fields[2]);
                                        obj.setQuantity(Double.parseDouble(Fields[3]));
                                        obj.setWeight(Double.parseDouble(Fields[4]));
                                        obj.setFi(Long.parseLong(Fields[5]));
                                        obj.setSumFi(Long.parseLong(Fields[6]));
                                    }break;
                                    case 5: {
                                        ConfigRecordObject obj = realm.createObject(ConfigRecordObject.class);
                                        obj.setName(Fields[0]);
                                        obj.setAddress(Fields[1]);
                                        obj.setPhone(Fields[2]);
                                    }
                                }
                            }

                            progressBundle.putInt("Progress", 1);
                            progressMsg.setData(progressBundle);
                            progressHandler.sendMessage(Message.obtain(progressMsg));
                        }
                        br.close();
                    } catch (FileNotFoundException E) {
                        Next = false;
                        E.printStackTrace();
                    } catch (IOException E) {
                        Next = false;
                        E.printStackTrace();
                    } catch (OutOfMemoryError E) {
                        Next = false;
                        E.printStackTrace();
//                    } catch (JSONException E) {
//                        Next = false;
//                        E.printStackTrace();
                    } catch (Exception E){
                        Next = false;
                        E.printStackTrace();
                    }
                }
            }
        });
        
        return Next;
    }
}
