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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hesabyar.report_generator.persiandatepicker.PersianDatePicker;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

import static java.lang.Math.abs;

public class HesabFragment extends Fragment {

    private ViewGroup mContainer;

    HesabListAdapter hesabAdapter;
    HesabListItem hesabItem;
    List<HesabListItem> hesabList;

    AccountSearchAdapter AccNameAdapter, AccNoAdapter;

    boolean filterDate;
    int AccNo;
    String AccName;
    CreateReportTask ReportTask;

    String Rep_AccName, Rep_AccNo, DateFrom, DateTo, Name, Address, Phone;
    String PdfFileAddr;

    CreatePDfTask PDfTask;

    AutoCompleteTextView codeEdit;
    AutoCompleteTextView nameEdit;

    RealmConfiguration RealmConfig;
    Realm realm;

    LinearLayout CL;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainer = container;
        return inflater.inflate(R.layout.fragment_hesab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
//        FragmentTransaction fragmentTrans = getChildFragmentManager().beginTransaction();
//        fragmentTrans.add(R.id.hesabFormFragmentFrame, new HesabFormFragment()).commit();

        codeEdit = (AutoCompleteTextView)mContainer.findViewById(R.id.hesabCodeEdit);
        nameEdit = (AutoCompleteTextView)mContainer.findViewById(R.id.hesabNameEdit);

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


        ((Button)mContainer.findViewById(R.id.hesabDateFromBtn)).setEnabled(false);
        ((Button)mContainer.findViewById(R.id.hesabDateToBtn)).setEnabled(false);

        CheckBox dateChBox = (CheckBox)mContainer.findViewById(R.id.hesabDateChBox);
        dateChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Button dateBtn = (Button)mContainer.findViewById(R.id.hesabDateFromBtn);
                dateBtn.setEnabled(b);

                dateBtn = (Button)mContainer.findViewById(R.id.hesabDateToBtn);
                dateBtn.setEnabled(b);
            }
        });

        Button datefromBtn = (Button)mContainer.findViewById(R.id.hesabDateFromBtn);
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
                        TextView datefromTxt = (TextView)mContainer.findViewById(R.id.hesabDateFromText);
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

        Button datetoBtn = (Button)mContainer.findViewById(R.id.hesabDateToBtn);
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
                        TextView datetoTxt = (TextView)mContainer.findViewById(R.id.hesabDateToText);
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

        Button reportBtn = (Button)mContainer.findViewById(R.id.hesabFormReportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                AutoCompleteTextView codeEdit = (AutoCompleteTextView) mContainer.findViewById(R.id.hesabCodeEdit);
                AutoCompleteTextView nameEdit = (AutoCompleteTextView) mContainer.findViewById(R.id.hesabNameEdit);
                if(codeEdit.getText().toString().equals("") && nameEdit.getText().toString().equals("")){
                    Snackbar snackBar = Snackbar.make(CL, "کد یا نام حساب را وارد نمایید", Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    sText.setTextColor(getResources().getColor(android.R.color.white));
                    snackBar.show();
                }else{
                    filterDate = ((CheckBox)getView().findViewById(R.id.hesabDateChBox)).isChecked();
                    DateFrom = "-";
                    DateTo = "-";
                    if(filterDate){
                        DateFrom = ((TextView)getView().findViewById(R.id.hesabDateFromText)).getText().toString();
                        DateTo = ((TextView)getView().findViewById(R.id.hesabDateToText)).getText().toString();
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

        Button saveBtn = view.getRootView().findViewById(R.id.hesabSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CL = getActivity().findViewById(R.id.mainCoordinatorLayout);

                if(hesabAdapter != null) {
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

        Button shareBtn = view.getRootView().findViewById(R.id.hesabShareBtn);
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


        final Button formBtn = (Button)mContainer.findViewById(R.id.hesabFormBtn);
        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FrameLayout fl = (FrameLayout)mContainer.findViewById(R.id.hesabFormFragmentFrame);
//                final RelativeLayout rl = (RelativeLayout)mContainer.findViewById(R.id.hesabTopBtnsLayout);
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
            PdfFileAddr = pdfCreator.createHesabPDF(strings[0], strings[1], strings[2], strings[3], hesabAdapter.HesabList, strings[4], strings[5], strings[6]);
            return PdfFileAddr;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().getRootView().findViewById(R.id.hesabSaveBtn)).setEnabled(false);
            ((Button)getView().getRootView().findViewById(R.id.hesabShareBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.hesabProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.hesabProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().getRootView().findViewById(R.id.hesabSaveBtn)).setEnabled(true);
            ((Button)getView().getRootView().findViewById(R.id.hesabShareBtn)).setEnabled(true);

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
                hesabList = new ArrayList<>();
                Rep_AccName = "-";
                Rep_AccNo = "-";
                if(AccountList.size() > 0) {
                    AccountRecordObject AccObject = AccountList.get(0);
                    Rep_AccName = AccObject.getAccName();
                    Rep_AccNo = Integer.toString(AccObject.getAccNo());
                    OrderedRealmCollection<SanadRecordObject> SanadList = realm.where(SanadRecordObject.class).equalTo("AccNo", AccObject.getAccNo()).findAll().sort("Tarikh", Sort.ASCENDING, "ResidNo", Sort.ASCENDING);

                    long Mandeh_Prev = -1;
                    int Mandeh_Prev_Status = 1;
                    boolean Mandeh_Prev_Added = false;

                    for (int i = 0; i < SanadList.size(); i++) {
                        SanadRecordObject SanadObj = SanadList.get(i);
                        String Ind = SanadObj.getInd();
                        int ResidNo = SanadObj.getResidNo();
                        String Tarikh = SanadObj.getTarikh();
                        String Sharh = SanadObj.getSharh();
                        double Quantity = SanadObj.getQuantity();
                        double Weight = SanadObj.getWeight();
                        long Fi = SanadObj.getFi();
                        long BedPrice = SanadObj.getBed();
                        long BesPrice = SanadObj.getBes();
                        long Mandeh = SanadObj.getMandeh();
                        int Status = 1;
                        if (SanadObj.getStatus().equals("بد")) {
                            Status = 2; // Bedehkar
                        } else if (SanadObj.getStatus().equals("بس")) {
                            Status = 3; // Bestankar
                        }

                        boolean addItem = true;

                        if(filterDate){
                            if(!((Tarikh.compareTo(DateFrom) >= 0) && (Tarikh.compareTo(DateTo) <= 0))){
                                addItem = false;
                            }else{
                                if(Mandeh_Prev == -1){
                                    if(i > 0){
                                        SanadRecordObject sanad = SanadList.get(i-1);
                                        Mandeh_Prev = sanad.getMandeh();
                                        Mandeh_Prev_Status = 1;
                                        if (sanad.getStatus().equals("بد")) {
                                            Mandeh_Prev_Status = 2; // Bedehkar
                                        } else if (sanad.getStatus().equals("بس")) {
                                            Mandeh_Prev_Status = 3; // Bestankar
                                        }
                                    }else{
                                        Mandeh_Prev = 0;
                                        Mandeh_Prev_Status = 1;
                                    }
                                }
                            }
                        }else{
                            Mandeh_Prev = 0;
                            Mandeh_Prev_Status = 1;
                        }

                        if((!Mandeh_Prev_Added) && (Mandeh_Prev != -1)){
                            hesabItem = new HesabListItem();
                            hesabItem.setInd("-");
                            hesabItem.setResidNo(0);
                            hesabItem.setTarikh("-");
                            hesabItem.setSharh("مانده از قبل");
                            hesabItem.setQuantity(0);
                            hesabItem.setWeight(0);
                            hesabItem.setFi(0);
                            hesabItem.setBed(0);
                            hesabItem.setBes(0);
                            hesabItem.setMandeh(Mandeh_Prev);
                            hesabItem.setStatus(Mandeh_Prev_Status);

                            hesabList.add(hesabItem);

                            Mandeh_Prev_Added = true;
                        }

                        if (addItem) {
                            hesabItem = new HesabListItem();
                            hesabItem.setInd(Ind);
                            hesabItem.setResidNo(ResidNo);
                            hesabItem.setTarikh(Tarikh);
                            hesabItem.setSharh(Sharh);
                            hesabItem.setQuantity(Quantity);
                            hesabItem.setWeight(Weight);
                            hesabItem.setFi(Fi);
                            hesabItem.setBed(BedPrice);
                            hesabItem.setBes(BesPrice);
                            hesabItem.setMandeh(Mandeh);
                            hesabItem.setStatus(Status);

                            hesabList.add(hesabItem);
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

                hesabAdapter = new HesabListAdapter(getContext(), hesabList);
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
            ((Button)getView().getRootView().findViewById(R.id.hesabFormReportBtn)).setEnabled(false);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.hesabReportProgressImg);
            progImg.setVisibility(View.VISIBLE);
            ((AnimationDrawable)progImg.getBackground()).start();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ImageView progImg = (ImageView)getView().getRootView().findViewById(R.id.hesabReportProgressImg);
            progImg.setVisibility(View.INVISIBLE);
            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().getRootView().findViewById(R.id.hesabFormReportBtn)).setEnabled(true);

            RecyclerView hesabRV = getView().getRootView().findViewById(R.id.hesabList);
            hesabRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                        hesabRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            hesabRV.setAdapter(hesabAdapter);

            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "تعداد آیتم های یافت شده : " + Integer.toString(hesabList.size()), Snackbar.LENGTH_LONG);
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
            PDfTask = new CreatePDfTask();
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
