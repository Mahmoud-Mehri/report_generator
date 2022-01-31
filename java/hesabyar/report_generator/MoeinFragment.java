package hesabyar.report_generator;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static java.lang.Math.abs;


public class MoeinFragment extends Fragment {

    private ViewGroup mContainer;

    ArrayAdapter accAdapter;
    Spinner accSpinner;

    MoeinListAdapter moeinAdapter;
    MoeinListItem moeinItem;
    List<MoeinListItem> moeinList;

    SMSItem smsItem;
    List<SMSItem> smsList;
    SMSListAdapter smsListAdapter;

    String Name, Address, Phone;

    String AccNo1, AccNo2;
    int AccFrom = 0, AccTo = 0;
    int AccType = 1; // 1:All, 2:Bed, 3:Bes
    int SortMode; // 1:AccName, 2:AccNo, 3:Mandeh, 4:Tarikh
    boolean OnlyMandeh = false;

    CreateReportTask ReportTask;

    String PdfFileAddr;
    CreatePDfTask PDfTask;

    RealmConfiguration RealmConfig;
    Realm realm;

    LinearLayout CL;

    CheckBox SMSChBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        return inflater.inflate(R.layout.fragment_moein, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
//        FragmentTransaction fragmentTrans = getChildFragmentManager().beginTransaction();
//        fragmentTrans.add(R.id.moeinFormFragmentFrame, new MoeinFormFragment()).commit();
        CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

        ((CheckBox)view.findViewById(R.id.moeinMandehChBox)).setChecked(true);
        ((RadioButton)view.findViewById(R.id.moeinSortAccName)).setChecked(true);
        ((EditText)view.findViewById(R.id.moeinCodeFromEdit)).setText("300001");
        ((EditText)view.findViewById(R.id.moeinCodeToEdit)).setText("309999");

        String[] accList = getResources().getStringArray(R.array.account_type_list);
        accSpinner = (Spinner)mContainer.findViewById(R.id.moeinAccountTypeSpinner);
        accAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinneritem, accList);
        accAdapter.setDropDownViewResource(R.layout.spinnerlist);
        accSpinner.setAdapter(accAdapter);
        accSpinner.setSelection(0);

        Button reportBtn = (Button)mContainer.findViewById(R.id.moeinFormReportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                EditText accFromEdit = (EditText)mContainer.findViewById(R.id.moeinCodeFromEdit);
                EditText accToEdit = (EditText)mContainer.findViewById(R.id.moeinCodeToEdit);
                if(accFromEdit.getText().toString().equals("") || accToEdit.getText().toString().equals("")){
                    Snackbar snackBar = Snackbar.make(CL, "بازه شماره حساب را وارد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{

                    AccFrom = Integer.valueOf(accFromEdit.getText().toString());
                    AccTo = Integer.valueOf(accToEdit.getText().toString());

                    AccNo1 = Integer.toString(AccFrom);
                    AccNo2 = Integer.toString(AccTo);

                    AccType = accSpinner.getSelectedItemPosition() + 1;

                    OnlyMandeh = ((CheckBox)getView().findViewById(R.id.moeinMandehChBox)).isChecked();

                    RadioGroup rg = getView().findViewById(R.id.moeinSortRadio);
                    switch(rg.getCheckedRadioButtonId()){
                        case R.id.moeinSortAccName : {
                            SortMode = 1;
                        } break;
                        case R.id.moeinSortAccNo : {
                            SortMode = 2;
                        } break;
                        case R.id.moeinSortMandeh : {
                            SortMode = 3;
                        } break;
                        case R.id.moeinSortDate : {
                            SortMode = 4;
                        } break;
                        default : {
                            SortMode = 2;
                        }
                    }

                    ReportTask = new CreateReportTask();
                    ReportTask.execute("");
                }
            }
        });

        Button saveBtn = view.findViewById(R.id.moeinSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                if(moeinAdapter != null) {
                    if(Build.VERSION.SDK_INT >= 23){
                        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Log.v("Permission","Permission is granted");
                            PDfTask = new MoeinFragment.CreatePDfTask();
                            PDfTask.execute(AccNo1, AccNo2, Name, Address, Phone);
                        } else {
                            Log.v("Permission","Permission is revoked");
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }else {
                        PDfTask = new MoeinFragment.CreatePDfTask();
                        PDfTask.execute(AccNo1, AccNo2, Name, Address, Phone);
                    }
                }else{
                    Snackbar snackBar = Snackbar.make(CL, "ابتدا گزارش را ایجاد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }
            }
        });

        Button shareBtn = view.findViewById(R.id.moeinShareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                if((PdfFileAddr == null) || (PdfFileAddr.equals(""))){
                    Snackbar snackBar = Snackbar.make(CL, "ابتدا گزارش را در فایل PDF ذخیره نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else {
                    File file = new File(PdfFileAddr);
                    Uri uri = FileProvider.getUriForFile(getContext(), "hesabyar.report_generator.provider", file);

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("application/pdf");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "گزارش معین");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(sharingIntent, "اشتراک گذاری با :"));
                    }catch(ActivityNotFoundException E){
                        Snackbar snackBar = Snackbar.make(CL, "نرم افزاری برای اشتراک گذاری فایلهای PDF مشخص نشده است", Snackbar.LENGTH_LONG);
                        ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        sText.setTextColor(getResources().getColor(android.R.color.white));
                        snackBar.show();
                    }
                }
            }
        });

        final Button formBtn = (Button)mContainer.findViewById(R.id.moeinFormBtn);
        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FrameLayout fl = (FrameLayout)mContainer.findViewById(R.id.moeinFormFragmentFrame);
//                final RelativeLayout rl = (RelativeLayout)mContainer.findViewById(R.id.moeinTopBtnsLayout);
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

        final Button SMSBtn = view.findViewById(R.id.moeinSMSBtn);
        SMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(moeinAdapter == null){
                    Snackbar snackBar = Snackbar.make(CL, "ابتدا گزارش را ایجاد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{
                    if(moeinAdapter.MoeinList.size() > 0) {
                        smsList = new ArrayList<>();
                        String SMSMessage = "-";
                        String RTLBD = Character.toString((char)(0x202B));
                        String RTLPDF = Character.toString((char)(0x202C));
                        for (int i = 0; i <moeinAdapter.MoeinList.size(); i++) {
                            MoeinListItem item = moeinAdapter.MoeinList.get(i);
                            if(item.isSelectedForSMS()) {
                                smsItem = new SMSItem();
                                smsItem.setAccName(item.getAccName());
                                smsItem.setPhoneNumber(item.getPhoneNumber());
//                                smsItem.setPhoneNumber("09118303368");
                                SMSMessage = Name + "\n" + Phone + "\n\n";
                                SMSMessage = SMSMessage + "ش ح : " + item.getAccNumber();
                                SMSMessage = SMSMessage + "\n" + "تاریخ : " + item.getHDate();
                                ;
                                if(item.getStatus() == 2) {
                                    SMSMessage = SMSMessage + "\n" + RTLBD + "مانده : " + String.format(Locale.ENGLISH, "%,d", item.getPrice()) + " " + "بدهکار" + RTLPDF;
                                }else{
                                    SMSMessage = SMSMessage + "\n" + RTLBD +  "مانده : " + String.format(Locale.ENGLISH, "%,d", item.getPrice()) + " " + "بستانکار" + RTLPDF;
                                }
                                SMSMessage = SMSMessage + "\n" + "*" + "\n" + "نرم افزار حسابیار";
                                smsItem.setSMSMessage(SMSMessage);
                                smsItem.setStatus(1);

                                smsList.add(smsItem);
                            }
                        }

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

        SMSChBox = mContainer.findViewById(R.id.moein_smschbox_all);
        SMSChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(moeinAdapter != null) {
                    if (!moeinAdapter.onBind) {
                        for (int i = 0; i < moeinAdapter.MoeinList.size(); i++) {
                            moeinAdapter.MoeinList.get(i).setSelectedForSMS(b);
                        }

                        moeinAdapter.notifyDataSetChanged();
                    }
                }else{
                    compoundButton.setChecked(false);
                }
            }
        });
    }

    public void openPdfFile(Context context, File url){
        File file = url;
        Uri uri = FileProvider.getUriForFile(context, "hesabyar.report_generator.provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(intent, "نمایش فایل"));
        }catch(ActivityNotFoundException E){
            Snackbar snackBar = Snackbar.make(CL, "نرم افزاری برای نمایش فایلهای PDF مشخص نشده است", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(context.getResources().getColor(android.R.color.white));
            snackBar.show();
        }
    }

    public class CreatePDfTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            PDFCreator pdfCreator = new PDFCreator(getContext());
            PdfFileAddr = pdfCreator.createMoeinPDF(strings[0], strings[1], moeinAdapter.MoeinList, strings[2], strings[3], strings[4]);
            return PdfFileAddr;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().findViewById(R.id.moeinSaveBtn)).setEnabled(false);
            ((Button)getView().findViewById(R.id.moeinShareBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().findViewById(R.id.moeinProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ImageView progImg = (ImageView)getView().findViewById(R.id.moeinProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().findViewById(R.id.moeinSaveBtn)).setEnabled(true);
            ((Button)getView().findViewById(R.id.moeinShareBtn)).setEnabled(true);

            if(!PdfFileAddr.equals("")){
                openPdfFile(getContext(), new File(PdfFileAddr));
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

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
                moeinList = new ArrayList<>();
                for (int i = 0; i < AccountList.size(); i++) {
                    int AccNo = AccountList.get(i).getAccNo();
                    String AccName = AccountList.get(i).getAccName();
                    String Phone = AccountList.get(i).getPhoneNumber();
                    long BedPrice = realm.where(SanadRecordObject.class).equalTo("AccNo", AccNo)
                            .greaterThan("Bed", 0).sum("Bed").longValue();
                    long BesPrice = realm.where(SanadRecordObject.class).equalTo("AccNo", AccNo)
                            .greaterThan("Bes", 0).sum("Bes").longValue();
                    long Mandeh = abs(BedPrice - BesPrice);
                    int Status = 1;
                    if (BedPrice > BesPrice) {
                        Status = 2; // Bedehkar
                    } else if (BesPrice > BedPrice) {
                        Status = 3; // Bestankar
                    }
                    String HDate = "-";
                    RealmResults<SanadRecordObject> sanadList = realm.where(SanadRecordObject.class).equalTo("AccNo", AccNo)
                            .findAll();
                    if (sanadList.size() > 0) {
                        HDate = sanadList.last().getTarikh();
                    }
                    boolean addItem = false;
                    switch (AccType) {
                        case 2: {
                            if (Status == 2) {
                                addItem = true;
                            }
                        }
                        break;
                        case 3: {
                            if (Status == 3) {
                                addItem = true;
                            }
                        }
                        break;
                        default: {
                            if (OnlyMandeh) {
                                addItem = (Status > 1);
                            } else {
                                addItem = true;
                            }
                        }
                    }

                    if (addItem) {
                        moeinItem = new MoeinListItem();
                        moeinItem.setAccNumber(AccNo);
                        moeinItem.setAccName(AccName);
                        moeinItem.setPhoneNumber(Phone);
                        moeinItem.setPrice(Mandeh);
                        moeinItem.setStatus(Status);
                        moeinItem.setHDate(HDate);

                        moeinList.add(moeinItem);
                    }
                }

                Collections.sort(moeinList, new Comparator<MoeinListItem>() {
                    @Override
                    public int compare(MoeinListItem t1, MoeinListItem t2) {
                        switch (SortMode) {
                            case 1: { // AccName
                                return Collator.getInstance(new Locale("fa", "IR")).compare(t1.getAccName(), t2.getAccName());
                            }
                            case 3: { // Mandeh
                                if (t1.getPrice() > t2.getPrice()) {
                                    return 1;
                                } else if (t1.getPrice() < t2.getPrice()) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                            case 4: { // HDate
                                return t1.getHDate().compareTo(t2.getHDate());
                            }
                            default: { // AccNumber
                                return t1.getAccNumber() - t2.getAccNumber();
                            }
                        }
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

                moeinAdapter = new MoeinListAdapter(getContext(), moeinList, SMSChBox);
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
            ((Button)getView().findViewById(R.id.moeinFormReportBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().findViewById(R.id.moeinReportProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            RecyclerView moeinRV = getView().findViewById(R.id.moeinList);
            moeinRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                        moeinRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            moeinRV.setAdapter(moeinAdapter);

            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "تعداد آیتم های یافت شده : " + Integer.toString(moeinList.size()), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();

            ImageView progImg = (ImageView)getView().findViewById(R.id.moeinReportProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().findViewById(R.id.moeinFormReportBtn)).setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            PDfTask = new MoeinFragment.CreatePDfTask();
            PDfTask.execute(AccNo1, AccNo2, Name, Address, Phone);
        }else{
            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "اجازه ذخیره فایل داده نشد !", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
        }
    }
}
