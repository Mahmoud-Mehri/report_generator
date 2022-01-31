package hesabyar.report_generator;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;


public class SMSDialog extends DialogFragment {

    SMSListAdapter smsAdapter;
    RecyclerView RV;
    View RootView;

    Button sendBtn, closeBtn;
    ImageView progressImg;

    String StoreName, StorePhone;
    Integer SendCount, RecieveCount;

    SmsManager smsManager;

    SendSMSTask sendTask;

    String validNumber = "^[+]?[0-9]{8,15}$";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            List<SMSItem> smsList = (List<SMSItem>)getArguments().getSerializable("SMSLIST");
            smsAdapter = new SMSListAdapter(getContext(), smsList);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View rootView = inflater.inflate(R.layout.fragment_sms, container);
        RootView = rootView;

        if(smsAdapter != null) {
            RV = rootView.findViewById(R.id.smsRV);
            RV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            RV.setAdapter(smsAdapter);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressImg = view.findViewById(R.id.smsProgressImg);

        sendBtn = view.findViewById(R.id.smsSendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                        &&
                        (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)){
//                    smsManager = SmsManager.getDefault();

//                    for(int i = 0; i < smsAdapter.SMSList.size(); i++){
//                        SMSItem sms = smsAdapter.SMSList.get(i);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(Integer.toString(i)), 0);
//                        getContext().registerReceiver(new BroadcastReceiver() {
//                            @Override
//                            public void onReceive(Context context, Intent intent) {
//                                switch(getResultCode()){
//                                    case RESULT_OK : {
//                                        smsAdapter.SMSList.get(Integer.parseInt(intent.getAction())).setStatus(2);
//                                    }break;
//                                    default : {
//                                        smsAdapter.SMSList.get(Integer.parseInt(intent.getAction())).setStatus(3);
//                                    }
//                                }
//
//                                smsAdapter.notifyDataSetChanged();
//                            }
//                        }, new IntentFilter(Integer.toString(i)));
//
//                        smsManager.sendTextMessage(sms.getPhoneNumber(), "", sms.getSMSMessage(), pendingIntent, null);
//                    }

                    sendTask = new SendSMSTask();
                    sendTask.execute("");
                }else{
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, 0);
                }
            }
        });

        closeBtn = view.findViewById(R.id.smsCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
    }

    public void createAdapter(List<SMSItem> list, String name, String phone){
        smsAdapter = new SMSListAdapter(getContext(), list);
        StoreName = name;
        StorePhone = phone;
    }

    public class SendSMSTask extends AsyncTask<String, String, String> {
        boolean Canceled = false;

        @Override
        protected String doInBackground(String... strings) {
            SendCount = smsAdapter.SMSList.size();
            RecieveCount = 0;
            smsManager = SmsManager.getDefault();
            for(int i = 0; i < smsAdapter.SMSList.size(); i++){
                SMSItem sms = smsAdapter.SMSList.get(i);
                if(sms.getPhoneNumber().matches(validNumber)) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(Integer.toString(i)), 0);
                    getContext().registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            int N = 0;
                            try {
                                N = Integer.parseInt(intent.getAction());
                            }catch (Exception E){
                                N = -1;
                            }
                            if(N > -1) {
                                switch (getResultCode()) {
                                    case RESULT_OK: {
                                        smsAdapter.SMSList.get(N).setStatus(2);
                                    }
                                    break;
                                    default: {
                                        smsAdapter.SMSList.get(N).setStatus(3);
                                    }
                                }

//                            runOnUIThread()
                                smsAdapter.notifyDataSetChanged();

                                RecieveCount = RecieveCount + 1;

                                if (RecieveCount == SendCount) {
                                    sendBtn.setEnabled(true);
                                    closeBtn.setEnabled(true);

                                    progressImg.setVisibility(View.INVISIBLE);
                                    ((AnimationDrawable) progressImg.getBackground()).stop();
                                }
                            }

//                            getContext().unregisterReceiver(this);
                        }
                    }, new IntentFilter(Integer.toString(i)));

                    ArrayList<String> MsgParts = smsManager.divideMessage(sms.getSMSMessage());
                    ArrayList<PendingIntent> intentList = new ArrayList<>();
                    intentList.add(pendingIntent);
//                    for(int j = 0; j < MsgParts.size(); j++){
//                        PendingIntent
//                        intentList.add(pendingIntent);
//                    }
                    smsManager.sendMultipartTextMessage(sms.getPhoneNumber(), null, MsgParts, intentList, null);

                    if(Canceled) {
                        break;
                    }else{
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException E) {

                        }
                    }
                }else{
                    sms.setStatus(4);
                    SendCount = SendCount - 1;

                    publishProgress("");
                }
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            Canceled = true;
        }

        public void doCancel(){
            Canceled = true;
        }

        @Override
        protected void onPreExecute() {

            sendBtn.setEnabled(false);
            closeBtn.setEnabled(false);

            progressImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progressImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if((SendCount == 0) || Canceled) {
                sendBtn.setEnabled(true);
                closeBtn.setEnabled(true);

                progressImg.setVisibility(View.INVISIBLE);
                ((AnimationDrawable) progressImg.getBackground()).stop();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            smsAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//            smsManager = SmsManager.getDefault();
            sendTask = new SendSMSTask();
            sendTask.execute("");
        }else{
            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "اجازه ارسال پیامک داده نشد !", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
        }
    }

}
