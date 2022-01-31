package hesabyar.report_generator;


import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hesabyar.report_generator.persiandatepicker.PersianDatePicker;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static java.lang.Math.abs;


public class GardeshFragment extends Fragment {

    private ViewGroup mContainer;

    ArrayAdapter accAdapter;
    Spinner accSpinner;

    GardeshListAdapter gardeshAdapter;
    GardeshListItem gardeshItem;
    List<GardeshListItem> gardeshList;

    SMSItem smsItem;
    List<SMSItem> smsList;
    SMSListAdapter smsListAdapter;

    CreateSMSTask SMSTask;

    String Name, Address, Phone;

    String AccNo1, AccNo2;
    int AccFrom = 0, AccTo = 0;
    int AccType = 1; // 1:All, 2:Bed, 3:Bes
    int SortMode; // 1:AccName, 2:AccNo, 3:Mandeh
    boolean OnlyMandeh = false;
    String date;

    CreateReportTask ReportTask;

    RealmConfiguration RealmConfig;
    Realm realm;

    LinearLayout CL;

    CheckBox SMSChBox;

    public GardeshFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        return inflater.inflate(R.layout.fragment_gardesh, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

        ((CheckBox)view.findViewById(R.id.gardeshMandehChBox)).setChecked(true);
        ((RadioButton)view.findViewById(R.id.gardeshSortAccName)).setChecked(true);
        ((EditText)view.findViewById(R.id.gardeshCodeFromEdit)).setText("300001");
        ((EditText)view.findViewById(R.id.gardeshCodeToEdit)).setText("309999");

        String[] accList = getResources().getStringArray(R.array.account_type_list);
        accSpinner = (Spinner)mContainer.findViewById(R.id.gardeshAccountTypeSpinner);
        accAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem, accList);
        accAdapter.setDropDownViewResource(R.layout.spinnerlist);
        accSpinner.setAdapter(accAdapter);
        accSpinner.setSelection(0);

        Button reportBtn = (Button)mContainer.findViewById(R.id.gardeshFormReportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                EditText accFromEdit = (EditText)mContainer.findViewById(R.id.gardeshCodeFromEdit);
                EditText accToEdit = (EditText)mContainer.findViewById(R.id.gardeshCodeToEdit);
                if(accFromEdit.getText().toString().equals("") || accToEdit.getText().toString().equals("")){
                    Snackbar snackBar = Snackbar.make(CL, "بازه شماره حساب را وارد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else {
                    date = ((TextView) mContainer.findViewById(R.id.gardeshDateText)).getText().toString();
                    if (date.equals("") || (date == null)) {
                        Snackbar snackBar = Snackbar.make(CL, "تاریخ را مشخص نمایید", Snackbar.LENGTH_LONG);
                        ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        sText.setTextColor(getResources().getColor(android.R.color.white));
                        snackBar.show();
                    } else {
                        AccFrom = Integer.valueOf(accFromEdit.getText().toString());
                        AccTo = Integer.valueOf(accToEdit.getText().toString());

                        AccNo1 = Integer.toString(AccFrom);
                        AccNo2 = Integer.toString(AccTo);

                        AccType = accSpinner.getSelectedItemPosition() + 1;

                        OnlyMandeh = ((CheckBox) getView().findViewById(R.id.gardeshMandehChBox)).isChecked();

                        RadioGroup rg = getView().findViewById(R.id.gardeshSortRadio);
                        switch (rg.getCheckedRadioButtonId()) {
                            case R.id.gardeshSortAccName: {
                                SortMode = 1;
                            }
                            break;
                            case R.id.gardeshSortAccNo: {
                                SortMode = 2;
                            }
                            break;
                            case R.id.gardeshSortMandeh: {
                                SortMode = 3;
                            }
                            break;
                            default: {
                                SortMode = 2;
                            }
                        }

                        ReportTask = new CreateReportTask();
                        ReportTask.execute("");
                    }
                }
            }
        });

        final Button formBtn = (Button)mContainer.findViewById(R.id.gardeshFormBtn);
        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FrameLayout fl = (FrameLayout)mContainer.findViewById(R.id.gardeshFormFragmentFrame);
//                final RelativeLayout rl = (RelativeLayout)mContainer.findViewById(R.id.gardeshTopBtnsLayout);
                if(fl.getVisibility() == View.VISIBLE){
                    ObjectAnimator formAnimation = ObjectAnimator.ofFloat(fl, "translationY", -fl.getHeight());
                    formAnimation.setDuration(200);
                    formAnimation.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            fl.setVisibility(View.GONE);
                            formBtn.setBackground(ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_open_form, null));
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });

                    formAnimation.start();
//                    ObjectAnimator.ofFloat(rl, "translationY", -rl.getHeight()).setDuration(400).start();
                }else{
                    ObjectAnimator formAnimation = ObjectAnimator.ofFloat(fl, "translationY", 0);
                    formAnimation.setDuration(200);
                    formAnimation.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            fl.setVisibility(View.VISIBLE);
                            formBtn.setBackground(ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_close_form, null));
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    formAnimation.start();
                }
            }
        });

        final Button SMSBtn = view.findViewById(R.id.gardeshSMSBtn);
        SMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gardeshAdapter == null){
                    Snackbar snackBar = Snackbar.make(CL, "ابتدا گزارش را ایجاد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{
                    if(gardeshAdapter.GardeshList.size() > 0) {
                        SMSTask = new CreateSMSTask();
                        SMSTask.execute("");
                    }else{
                        Snackbar snackBar = Snackbar.make(CL, "حسابی در لیست موجود نیست", Snackbar.LENGTH_LONG);
                        ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        sText.setTextColor(getResources().getColor(android.R.color.white));
                        snackBar.show();
                    }
                }
            }
        });

        SMSChBox = mContainer.findViewById(R.id.gardesh_smschbox_all);
        SMSChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(gardeshAdapter != null) {
                    if (!gardeshAdapter.onBind) {
                        for (int i = 0; i < gardeshAdapter.GardeshList.size(); i++) {
                            gardeshAdapter.GardeshList.get(i).setSelectedForSMS(b);
                        }

                        gardeshAdapter.notifyDataSetChanged();
                    }
                }else{
                    compoundButton.setChecked(false);
                }
            }
        });
        
        Button dateBtn = (Button)mContainer.findViewById(R.id.gardeshDateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PersianDatePicker datepicker = new PersianDatePicker(getContext());
                datepicker.setDisplayDate(new Date());
                AlertDialog dateDlg;
                AlertDialog.Builder DlgBuilder = new AlertDialog.Builder(getContext());
                DlgBuilder.setView(datepicker);
                DlgBuilder.setTitle(R.string.dialog_date_title);
                DlgBuilder.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView dateTxt = (TextView)mContainer.findViewById(R.id.gardeshDateText);
                        dateTxt.setText(datepicker.getDisplayPersianDate().getPersianShortDate());
                        dialogInterface.dismiss();
                    }
                });
                DlgBuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dateDlg = DlgBuilder.create();
                dateDlg.show();
            }
        });
        
        
    }

    public class CreateReportTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (RealmConfig == null) {
                RealmConfig = new RealmConfiguration.Builder()
                        .name("info.realm")
                        .deleteRealmIfMigrationNeeded()
                        .build();
            }

            realm = Realm.getInstance(RealmConfig);
            try {
                OrderedRealmCollection<AccountRecordObject> AccountList = realm.where(AccountRecordObject.class).between("AccNo", AccFrom, AccTo).findAll();
                gardeshList = new ArrayList<>();
                for (int i = 0; i < AccountList.size(); i++) {
                    int AccNo = AccountList.get(i).getAccNo();
                    String AccName = AccountList.get(i).getAccName();
                    String Phone = AccountList.get(i).getPhoneNumber();

                    long sanadCount = realm.where(SanadRecordObject.class).equalTo("AccNo", AccNo).equalTo("Tarikh", date).count();
                    if(sanadCount > 0) {
                        long BedPrice = realm.where(SanadRecordObject.class)
                                .equalTo("AccNo", AccNo)
                                .greaterThan("Bed", 0)
                                .sum("Bed").longValue();
                        long BesPrice = realm.where(SanadRecordObject.class)
                                .equalTo("AccNo", AccNo)
                                .greaterThan("Bes", 0)
                                .sum("Bes").longValue();

                        long Mandeh = abs(BedPrice - BesPrice);
                        int Status = 1;
                        boolean addItem = true;

                        if (BedPrice > BesPrice) {
                            Status = 2; // Bedehkar
                        } else if (BesPrice > BedPrice) {
                            Status = 3; // Bestankar
                        }

                        if(OnlyMandeh){
                            addItem = (Status > 1);
                        }

//                        switch (AccType) {
//                            case 2: {
//                                if (Status == 2) {
//                                    addItem = true;
//                                }
//                            }
//                            break;
//                            case 3: {
//                                if (Status == 3) {
//                                    addItem = true;
//                                }
//                            }
//                            break;
//                            default: {
//                                if (OnlyMandeh) {
//                                    addItem = (Status > 1);
//                                } else {
//                                    addItem = true;
//                                }
//                            }
//                        }

                        if (addItem) {
                            gardeshItem = new GardeshListItem();
                            gardeshItem.setAccNumber(AccNo);
                            gardeshItem.setAccName(AccName);
                            gardeshItem.setPhoneNumber(Phone);
                            gardeshItem.setPrice(Mandeh);
                            gardeshItem.setStatus(Status);

                            gardeshList.add(gardeshItem);
                        }
                    }
                }

                Collections.sort(gardeshList, new Comparator<GardeshListItem>() {
                    @Override
                    public int compare(GardeshListItem t1, GardeshListItem t2) {
                        if(t1.getPhoneNumber().equals("-")) return Integer.MAX_VALUE;
                        else if (t2.getPhoneNumber().equals("-")) return Integer.MIN_VALUE;
                        else return t1.getPhoneNumber().compareTo(t2.getPhoneNumber());

//                        switch (SortMode) {
//                            case 1: { // AccName
//                                return Collator.getInstance(new Locale("fa", "IR")).compare(t1.getAccName(), t2.getAccName());
//                            }
//                            case 3: { // Mandeh
//                                if (t1.getPrice() > t2.getPrice()) {
//                                    return 1;
//                                } else if (t1.getPrice() < t2.getPrice()) {
//                                    return -1;
//                                } else {
//                                    return 0;
//                                }
//                            }
//                            default: { // AccNumber
//                                return t1.getAccNumber() - t2.getAccNumber();
//                            }
//                        }
                    }
                });

                ConfigRecordObject obj = realm.where(ConfigRecordObject.class).findAll().get(0);
                if(obj != null) {
                    Name = obj.getName();
                    Address = obj.getAddress();
                    Phone = obj.getPhone();
                }else{
                    Name = "-";
                    Address = "-";
                    Phone = "-";
                }

                gardeshAdapter = new GardeshListAdapter(getContext(), gardeshList, SMSChBox);
            } finally {
                realm.close();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().findViewById(R.id.gardeshFormReportBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().findViewById(R.id.gardeshReportProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            RecyclerView gardeshRV = getView().findViewById(R.id.gardeshList);
            gardeshRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                        gardeshRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            gardeshRV.setAdapter(gardeshAdapter);

            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "تعداد آیتم های یافت شده : " + Integer.toString(gardeshList.size()), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();

            ImageView progImg = (ImageView)getView().findViewById(R.id.gardeshReportProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().findViewById(R.id.gardeshFormReportBtn)).setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }

    public class CreateSMSTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (RealmConfig == null) {
                RealmConfig = new RealmConfiguration.Builder()
                        .name("info.realm")
                        .deleteRealmIfMigrationNeeded()
                        .build();
            }

            realm = Realm.getInstance(RealmConfig);
            try {
                smsList = new ArrayList<>();
                String SMSMessage = "-";
                String RTLBD = Character.toString((char)(0x202B));
                String RTLPDF = Character.toString((char)(0x202C));
                DecimalFormat df = new DecimalFormat("#.##");
                Locale persian = new Locale("fa", "IR");
                for (int i = 0; i < gardeshAdapter.GardeshList.size(); i++) {
                    GardeshListItem item = gardeshAdapter.GardeshList.get(i);
                    if(item.isSelectedForSMS()) {
                        smsItem = new SMSItem();
                        smsItem.setAccName(item.getAccName());
                        smsItem.setPhoneNumber(item.getPhoneNumber());
//                                smsItem.setPhoneNumber("09118303368");
                        SMSMessage = Name + "\n" + Phone + "\n\n";
                        SMSMessage = SMSMessage + "ش ح : " + item.getAccNumber();
                        SMSMessage = SMSMessage + "\n" + "تاریخ : " + date;

                        OrderedRealmCollection<SanadRecordObject> sanadList = realm.where(SanadRecordObject.class)
                                .equalTo("AccNo", item.getAccNumber())
                                .findAll().sort("Tarikh", Sort.ASCENDING, "ResidNo", Sort.ASCENDING);
                        long Mandeh_Prev = -1;
                        int Mandeh_Prev_Status = 1;
                        for(int j = 0; j < sanadList.size(); j++) {
                            String Tarikh = sanadList.get(j).getTarikh();
                            if(Tarikh.equals(date)){
                                if (Mandeh_Prev == -1) {
                                    if (j > 0) {
                                        SanadRecordObject sanad = sanadList.get(j - 1);
                                        Mandeh_Prev = sanad.getMandeh();
                                        Mandeh_Prev_Status = 1;
                                        if (sanad.getStatus().equals("بد")) {
                                            Mandeh_Prev_Status = 2; // Bedehkar
                                        } else if (sanad.getStatus().equals("بس")) {
                                            Mandeh_Prev_Status = 3; // Bestankar
                                        }
                                    } else {
                                        Mandeh_Prev = 0;
                                        Mandeh_Prev_Status = 1;
                                    }
                                }
                            }
                        }

                        switch (Mandeh_Prev_Status) {
                            case 1 : SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده از قبل : " + String.format(Locale.ENGLISH, "%,d", Mandeh_Prev) + RTLPDF;
                            break;
                            case 2 : SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده از قبل : " + String.format(Locale.ENGLISH, "%,d", Mandeh_Prev) + " " + "بدهکار" + RTLPDF;
                            break;
                            case 3 : SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده از قبل : " + String.format(Locale.ENGLISH, "%,d", Mandeh_Prev) + " " + "بستانکار" + RTLPDF;
                        }

                        sanadList = realm.where(SanadRecordObject.class)
                                .equalTo("AccNo", item.getAccNumber())
                                .equalTo("Tarikh", date).findAll().sort("ResidNo");

                        long BedPrice = 0;
                        long BesPrice = 0;

                        for (int j = 0; j < sanadList.size(); j++){
                            SanadRecordObject sanad = sanadList.get(j);

                            if(sanad.getFi() > 0) {
                                SMSMessage = SMSMessage + "\n" + RTLBD + sanad.getSharh()
                                        + " - " + String.format(Locale.ENGLISH, "%,d", sanad.getFi())
                                        + " - " + df.format(sanad.getWeight())
                                        + " - " + df.format(sanad.getQuantity()) + RTLPDF;
                            }

                            BedPrice = BedPrice + sanad.getBed();
                            BesPrice = BesPrice + sanad.getBes();
                        }

                        SMSMessage = SMSMessage + "\n\n" + RTLBD + "بدهی روز : " + String.format(Locale.ENGLISH, "%,d", BedPrice) + RTLPDF;
                        SMSMessage = SMSMessage + "\n" + RTLBD + "بستانکاری روز : " + String.format(Locale.ENGLISH, "%,d", BesPrice) + RTLPDF;

                        long Mandeh = realm.where(SanadRecordObject.class).equalTo("AccNo", item.getAccNumber())
                                .sort("Tarikh", Sort.ASCENDING, "ResidNo", Sort.ASCENDING)
                                .findAll().last().getMandeh();

                        if(Mandeh > 0) {
                            SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده : " + String.format(Locale.ENGLISH, "%,d", abs(Mandeh)) + " " + "بدهکار" + RTLPDF;
                        }else if(Mandeh < 0){
                            SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده : " + String.format(Locale.ENGLISH, "%,d", abs(Mandeh)) + " " + "بستانکار" + RTLPDF;
                        }else{
                            SMSMessage = SMSMessage + "\n\n" + RTLBD + "مانده : " + String.format(Locale.ENGLISH, "%,d", abs(Mandeh)) + RTLPDF;
                        }
                        SMSMessage = SMSMessage + "\n" + "*" + "\n" + "نرم افزار حسابیار";
                        smsItem.setSMSMessage(SMSMessage);
                        smsItem.setStatus(1);

                        smsList.add(smsItem);
                    }
                }
            } finally {
                realm.close();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().findViewById(R.id.gardeshSMSBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().findViewById(R.id.gardeshSMSProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (smsList.size() > 0) {
                SMSDialog smsDlg = new SMSDialog();
                smsDlg.createAdapter(smsList, Name, Phone);
                smsDlg.setCancelable(false);
                smsDlg.show(getFragmentManager(), "SMSDialog");
            } else {
                Snackbar snackBar = Snackbar.make(CL, "حسابی برای ارسال پیامک انتخاب نشده است", Snackbar.LENGTH_LONG);
                ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                sText.setTextColor(getResources().getColor(android.R.color.white));
                snackBar.show();
            }

            ImageView progImg = (ImageView)getView().findViewById(R.id.gardeshSMSProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().findViewById(R.id.gardeshSMSBtn)).setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }

}
