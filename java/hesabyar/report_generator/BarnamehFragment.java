package hesabyar.report_generator;


import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hesabyar.report_generator.persiandatepicker.PersianDatePicker;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 */
public class BarnamehFragment extends Fragment {

    private ViewGroup mContainer;

    BarnamehListAdapter barnamehAdapter;
    List<BarnamehListItem> barnamehList;
    BarnamehListItem barnamehItem;

    SMSItem smsItem;
    List<SMSItem> smsList;
    SMSListAdapter smsListAdapter;

    AccountSearchAdapter AccNameAdapter, AccNoAdapter;

    boolean filterDate;
    int AccNo;
    String AccName;
    CreateReportTask ReportTask;

    String Rep_AccName, Rep_AccNo, Rep_Phone, DateFrom, DateTo, Name, Address, Phone;
    String PdfFileAddr;

    CreatePDfTask PDfTask;

    AutoCompleteTextView codeEdit;
    AutoCompleteTextView nameEdit;

    RealmConfiguration RealmConfig;
    Realm realm;

    LinearLayout CL;

    CheckBox SMSChBox;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainer = container;
        return inflater.inflate(R.layout.fragment_barnameh, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
//        FragmentTransaction fragmentTrans = getChildFragmentManager().beginTransaction();
//        fragmentTrans.add(R.id.barnamehFormFragmentFrame, new BarnamehFormFragment()).commit();

        codeEdit = (AutoCompleteTextView)mContainer.findViewById(R.id.barnamehCodeEdit);
        nameEdit = (AutoCompleteTextView)mContainer.findViewById(R.id.barnamehNameEdit);

        AccNoAdapter = new AccountSearchAdapter(getContext(), 1);
        codeEdit.setThreshold(1);
        codeEdit.setAdapter(AccNoAdapter);
        codeEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                codeEdit.setText(AccNoAdapter.getItem(i).getAccNo());
                nameEdit.setText(AccNoAdapter.getItem(i).getAccName());
            }
        });

        CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

        AccNameAdapter = new AccountSearchAdapter(getContext(), 2);
        nameEdit.setThreshold(1);
        nameEdit.setAdapter(AccNameAdapter);
        nameEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                codeEdit.setText(AccNameAdapter.getItem(i).getAccNo());
                nameEdit.setText(AccNameAdapter.getItem(i).getAccName());
            }
        });


        ((Button)mContainer.findViewById(R.id.barnamehDateFromBtn)).setEnabled(false);
        ((Button)mContainer.findViewById(R.id.barnamehDateToBtn)).setEnabled(false);

        CheckBox dateChBox = (CheckBox)mContainer.findViewById(R.id.barnamehDateChBox);
        dateChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Button dateBtn = (Button)mContainer.findViewById(R.id.barnamehDateFromBtn);
                dateBtn.setEnabled(b);

                dateBtn = (Button)mContainer.findViewById(R.id.barnamehDateToBtn);
                dateBtn.setEnabled(b);
            }
        });

        Button datefromBtn = (Button)mContainer.findViewById(R.id.barnamehDateFromBtn);
        datefromBtn.setOnClickListener(new View.OnClickListener() {
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
                        TextView datefromTxt = (TextView)mContainer.findViewById(R.id.barnamehDateFromText);
                        datefromTxt.setText(datepicker.getDisplayPersianDate().getPersianShortDate());
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

        Button datetoBtn = (Button)mContainer.findViewById(R.id.barnamehDateToBtn);
        datetoBtn.setOnClickListener(new View.OnClickListener() {
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
                        TextView datetoTxt = (TextView)mContainer.findViewById(R.id.barnamehDateToText);
                        datetoTxt.setText(datepicker.getDisplayPersianDate().getPersianShortDate());
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

        Button reportBtn = (Button)mContainer.findViewById(R.id.barnamehFormReportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                AutoCompleteTextView codeEdit = (AutoCompleteTextView) mContainer.findViewById(R.id.barnamehCodeEdit);
                AutoCompleteTextView nameEdit = (AutoCompleteTextView) mContainer.findViewById(R.id.barnamehNameEdit);
                if(codeEdit.getText().toString().equals("") && nameEdit.getText().toString().equals("")){
                    Snackbar snackBar = Snackbar.make(CL, "کد یا نام حساب را وارد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{
                    filterDate = ((CheckBox)getView().findViewById(R.id.barnamehDateChBox)).isChecked();
                    DateFrom = "-";
                    DateTo = "-";
                    if(filterDate){
                        DateFrom = ((TextView)getView().findViewById(R.id.barnamehDateFromText)).getText().toString();
                        DateTo = ((TextView)getView().findViewById(R.id.barnamehDateToText)).getText().toString();
                        if(DateFrom.equals("") || DateTo.equals("")){
                            Snackbar snackBar = Snackbar.make(CL, "بازه تاریخ را مشخص نمایید", Snackbar.LENGTH_LONG);
                            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            sText.setTextColor(getResources().getColor(android.R.color.white));
                            snackBar.show();

                            return;
                        }
                    }

                    AccNo = -1;
                    if(!codeEdit.getText().toString().equals("")){
                        AccNo = Integer.valueOf(codeEdit.getText().toString());
                    }

                    AccName = nameEdit.getText().toString();

                    ReportTask = new CreateReportTask();
                    ReportTask.execute("");
                }
            }
        });

        Button saveBtn = view.getRootView().findViewById(R.id.barnamehSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                if(barnamehAdapter != null) {
                    if(Build.VERSION.SDK_INT >= 23){
                        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Log.v("Permission","Permission is granted");
                            PDfTask = new CreatePDfTask();
                            PDfTask.execute(Rep_AccName, Rep_AccNo, DateFrom, DateTo, Name, Address, Phone);
                        } else {
                            Log.v("Permission","Permission is revoked");
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }else {
                        PDfTask = new CreatePDfTask();
                        PDfTask.execute(Rep_AccName, Rep_AccNo, DateFrom, DateTo, Name, Address, Phone);
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

        Button shareBtn = view.getRootView().findViewById(R.id.barnamehShareBtn);
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
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "گزارش صورتحساب");
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

        final Button SMSBtn = view.findViewById(R.id.barnamehSMSBtn);
        SMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(barnamehAdapter == null){
                    Snackbar snackBar = Snackbar.make(CL, "ابتدا گزارش را ایجاد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{
                    if(barnamehAdapter.barnamehList.size() > 0) {
                        smsList = new ArrayList<>();
                        String SMSMessage = "-";
                        String RTLBD = Character.toString((char)(0x202B));
                        String RTLPDF = Character.toString((char)(0x202C));
                        DecimalFormat df = new DecimalFormat("#.##");
                        for (int i = 0; i < barnamehAdapter.barnamehList.size(); i++) {
                            BarnamehListItem item = barnamehAdapter.barnamehList.get(i);
                            if(item.isSelectedForSMS()) {
                                smsItem = new SMSItem();
                                smsItem.setAccName(item.getAccName());
                                smsItem.setPhoneNumber(item.getPhoneNumber());
//                                smsItem.setPhoneNumber("09118303368");
                                SMSMessage = Name + "\n" + Phone + "\n\n";
                                SMSMessage = SMSMessage + item.getTarikh();
                                SMSMessage = SMSMessage + "\n" + "ش بارنامه : " + Integer.toString(item.getLadingNo());
                                SMSMessage = SMSMessage + "\n" + "ش ماشین : " + item.getCarNo();

                                realm = Realm.getInstance(RealmConfig);
                                RealmResults<ForooshRecordObject> fList = realm.where(ForooshRecordObject.class).equalTo("LadingNo", item.getLadingNo()).findAll();

                                double Count = fList.sum("Quantity").doubleValue();
                                SMSMessage = SMSMessage + "\n" + RTLBD  + "تعداد : " + df.format(Count) + RTLPDF;

                                double Weight = fList.sum("Weight").doubleValue();
                                SMSMessage = SMSMessage + "\n" + RTLBD + "وزن : " + df.format(Weight) + RTLPDF;

                                SMSMessage = SMSMessage + "\n" + RTLBD + "صافی : " + String.format(Locale.ENGLISH, "%,d", item.getSafi()) + RTLPDF;
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
                            Snackbar snackBar = Snackbar.make(CL, "بارنامه ای برای ارسال پیامک اتخاب نشده است", Snackbar.LENGTH_LONG);
                            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                            TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            sText.setTextColor(getResources().getColor(android.R.color.white));
                            snackBar.show();
                        }
                    }else{
                        Snackbar snackBar = Snackbar.make(CL, "بارنامه ای در لیست موجود نیست", Snackbar.LENGTH_LONG);
                        ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        sText.setTextColor(getResources().getColor(android.R.color.white));
                        snackBar.show();
                    }
                }
            }
        });

        SMSChBox = mContainer.findViewById(R.id.barnameh_smschbox_all);
        SMSChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(barnamehAdapter != null) {
                    if (!barnamehAdapter.onBind) {
                        for (int i = 0; i < barnamehAdapter.barnamehList.size(); i++) {
                            barnamehAdapter.barnamehList.get(i).setSelectedForSMS(b);
                        }
                        barnamehAdapter.notifyDataSetChanged();
                    }
                }else{
                    compoundButton.setChecked(false);
                }
            }
        });

        final Button formBtn = (Button)mContainer.findViewById(R.id.barnamehFormBtn);
        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FrameLayout fl = (FrameLayout)mContainer.findViewById(R.id.barnamehFormFragmentFrame);
//                final RelativeLayout rl = (RelativeLayout)mContainer.findViewById(R.id.barnamehTopBtnsLayout);
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
    }

    public void openPdfFile(Context context, File url){
        File file = url;
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(context, "hesabyar.report_generator.provider", file);

        long i = file.getTotalSpace();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(intent, "نمایش فایل :"));
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
            PdfFileAddr = pdfCreator.createBarnamehPDF(strings[0], strings[1], strings[2], strings[3], barnamehList, strings[4], strings[5], strings[6]);
            return PdfFileAddr;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().getRootView().findViewById(R.id.barnamehSaveBtn)).setEnabled(false);
            ((Button)getView().getRootView().findViewById(R.id.barnamehShareBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.barnamehProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.barnamehProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().getRootView().findViewById(R.id.barnamehSaveBtn)).setEnabled(true);
            ((Button)getView().getRootView().findViewById(R.id.barnamehShareBtn)).setEnabled(true);

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
            if(RealmConfig == null){
                RealmConfig = new RealmConfiguration.Builder()
                        .name("info.realm")
                        .deleteRealmIfMigrationNeeded()
                        .build();
            }

            realm = Realm.getInstance(RealmConfig);
            try {
                OrderedRealmCollection<AccountRecordObject> AccountList;
                if((AccNo > -1) && (!AccName.equals(""))) {
                    AccountList = realm.where(AccountRecordObject.class)
                            .equalTo("AccNo", AccNo).findAll();
                }else{
                    if(AccNo > -1){
                        AccountList = realm.where(AccountRecordObject.class)
                                .equalTo("AccNo", AccNo).findAll();
                    }else{
                        AccountList = realm.where(AccountRecordObject.class)
                                .like("AccName", "*"+AccName+"*").findAll();
                    }
                }

                barnamehList = new ArrayList<>();
                Rep_AccName = "-";
                Rep_AccNo = "-";
                Rep_Phone = "-";
                if(AccountList.size() > 0) {
                    AccountRecordObject AccObject = AccountList.get(0);
                    Rep_AccName = AccObject.getAccName();
                    Rep_AccNo = Integer.toString(AccObject.getAccNo());
                    Rep_Phone = AccObject.getPhoneNumber();
                    OrderedRealmCollection<BarnamehRecordObject> BarnamehList = realm.where(BarnamehRecordObject.class).equalTo("AccNo", AccObject.getAccNo()).findAll().sort("Tarikh", Sort.ASCENDING, "ResidNo", Sort.ASCENDING);

                    for (int i = 0; i < BarnamehList.size(); i++) {
                        BarnamehRecordObject BarnamehObj = BarnamehList.get(i);
                        int LadingNo = BarnamehObj.getLadingNo();
                        String Tarikh = BarnamehObj.getTarikh();
                        int ResidNo = BarnamehObj.getResidNo();
                        String CarNo = BarnamehObj.getCarNo();
                        double InCount = BarnamehObj.getInCount();
                        double InWeight = BarnamehObj.getInWeight();
                        double Baskul1 = BarnamehObj.getBaskul1();
                        double Baskul2 = BarnamehObj.getBaskul2();
                        long Sood = BarnamehObj.getSood();
                        long Safi = BarnamehObj.getSafi();
                        long Keraye = BarnamehObj.getKeraye();
                        long Takhlie = BarnamehObj.getTakhlie();
                        long Peybari = BarnamehObj.getPeybari();
                        long Dasti = BarnamehObj.getDasti();
                        long Motafarregheh = BarnamehObj.getMotafarregheh();
                        String Note = BarnamehObj.getNote();

                        boolean addItem = true;

                        if(filterDate){
                            if(!((Tarikh.compareTo(DateFrom) >= 0) && (Tarikh.compareTo(DateTo) <= 0))){
                                addItem = false;
                            }
                        }

                        if (addItem) {
                            RealmResults<ForooshRecordObject> fList = realm.where(ForooshRecordObject.class).equalTo("LadingNo", LadingNo).findAll();
                            InCount = fList.sum("Quantity").doubleValue();
                            InWeight = fList.sum("Weight").doubleValue();

                            barnamehItem = new BarnamehListItem();
                            barnamehItem.setLadingNo(LadingNo);
                            barnamehItem.setAccNo(Integer.parseInt(Rep_AccNo));
                            barnamehItem.setAccName(Rep_AccName);
                            barnamehItem.setPhoneNumber(Rep_Phone);
                            barnamehItem.setTarikh(Tarikh);
                            barnamehItem.setResidNo(ResidNo);
                            barnamehItem.setCarNo(CarNo);
                            barnamehItem.setInCount(InCount);
                            barnamehItem.setInWeight(InWeight);
                            barnamehItem.setBaskul1(Baskul1);
                            barnamehItem.setBaskul2(Baskul2);
                            barnamehItem.setSood(Sood);
                            barnamehItem.setSafi(Safi);
                            barnamehItem.setKeraye(Keraye);
                            barnamehItem.setTakhlie(Takhlie);
                            barnamehItem.setPeybari(Peybari);
                            barnamehItem.setDasti(Dasti);
                            barnamehItem.setMotafarregheh(Motafarregheh);
                            barnamehItem.setNote(Note);

                            barnamehList.add(barnamehItem);
                        }
                    }
                }

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

                barnamehAdapter = new BarnamehListAdapter(getContext(), barnamehList, SMSChBox);
            }finally {
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
            ((Button)getView().getRootView().findViewById(R.id.barnamehFormReportBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.barnamehReportProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.barnamehReportProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().getRootView().findViewById(R.id.barnamehFormReportBtn)).setEnabled(true);

            RecyclerView barnamehRV = getView().getRootView().findViewById(R.id.barnamehList);
            barnamehRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                        barnamehRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            barnamehRV.setAdapter(barnamehAdapter);

            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "تعداد آیتم های یافت شده : " + Integer.toString(barnamehList.size()), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
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
            PDfTask = new BarnamehFragment.CreatePDfTask();
            PDfTask.execute(Rep_AccName, Rep_AccNo, DateFrom, DateTo, Name, Address, Phone);
        }else{
            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "اجازه ذخیره فایل داده نشد !", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
        }
    }


}
