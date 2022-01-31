package hesabyar.report_generator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;

import org.json.JSONException;

import java.io.File;
import java.nio.charset.Charset;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class FileFragment extends Fragment implements FileDialog.OnFileSelectedListener{

    RecyclerView.Adapter accAdapter, sanadAdapter;
    OrderedRealmCollection<AccountRecordObject> accList;
    OrderedRealmCollection<SanadRecordObject> sanadList;

    String fileAddr = "";

    RealmConfiguration RealmConfig;
    Realm realm;

    ImportFileTask importFileTask;

    ProgressBar pBar;

    private Handler progressHandler;

//    File F;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        String S = Environment.getExternalStorageDirectory().getParent() + "/";
//        F = new File(Environment.getExternalStorageDirectory().getParent());

        progressHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int Max = msg.getData().getInt("Max", 0);
                pBar.setMax(Max);

                int progress = msg.getData().getInt("Progress", 0);
                if(progress > 0){
                    pBar.incrementProgressBy(1);
                }else{
                    pBar.setProgress(0);
                }
            }
        };

        final View parentView = view;

        pBar = parentView.findViewById(R.id.fileProgressBar);
        pBar.setVisibility(View.GONE);

        Button importBtn = view.findViewById(R.id.fileImportBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean imported = false;
                if(Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.v("Permission","Permission is granted");
                        File rootDir = Environment.getExternalStorageDirectory();
                        if(rootDir != null){
                            File importFile = new File(rootDir.getAbsolutePath() + "/HesabyarAND.hinfo");
                            if(importFile.exists()){
                                fileAddr = importFile.getAbsolutePath();
                                importFileTask = new ImportFileTask();
                                importFileTask.execute(fileAddr);

                                imported = true;
                            }
                        }

                        if(!imported) {
                            FileDialog openDlg = new OpenFileDialog();
                            openDlg.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
                            Bundle args = new Bundle();
                            args.putString(FileDialog.EXTENSION, "hinfo");
//                args.putSerializable(FileDialog.ROOT_DIRECTORY, F);
//                args.putSerializable(FileDialog.START_DIRECTORY, F);
                            openDlg.setArguments(args);
                            openDlg.show(getChildFragmentManager(), OpenFileDialog.class.getName());
                        }

                    } else {
                        Log.v("Permission","Permission is revoked");
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }else {
                    File rootDir = Environment.getExternalStorageDirectory();
                    if(rootDir != null){
                        File importFile = new File(rootDir.getAbsolutePath() + "/HesabyarAND.hinfo");
                        if(importFile.exists()){
                            fileAddr = importFile.getAbsolutePath();
                            importFileTask = new ImportFileTask();
                            importFileTask.execute(fileAddr);

                            imported = true;
                        }
                    }

                    if(!imported) {
                        FileDialog openDlg = new OpenFileDialog();
                        openDlg.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
                        Bundle args = new Bundle();
                        args.putString(FileDialog.EXTENSION, "hinfo");
//                args.putSerializable(FileDialog.ROOT_DIRECTORY, F);
//                args.putSerializable(FileDialog.START_DIRECTORY, F);
                        openDlg.setArguments(args);
                        openDlg.show(getChildFragmentManager(), OpenFileDialog.class.getName());
                    }

                }
            }
        });

        Button hesabBtn = view.findViewById(R.id.fileHesabBtn);
        hesabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RealmConfig == null){
                    RealmConfig = new RealmConfiguration.Builder()
                            .name("info.realm")
                            .deleteRealmIfMigrationNeeded()
                            .build();
                }

                realm = Realm.getInstance(RealmConfig);
                accList = realm.where(AccountRecordObject.class).findAll().sort("AccNo");
                accAdapter = new AccountListAdapter(getContext(), accList);
                RecyclerView accRecView = parentView.findViewById(R.id.fileList);
                accRecView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//                accRecView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                accRecView.setAdapter(accAdapter);
                CardView CV = parentView.findViewById(R.id.fileHesabHeader);
                CV.setVisibility(View.VISIBLE);
                CV = parentView.findViewById(R.id.fileSanadHeader);
                CV.setVisibility(View.GONE);
                accRecView.setVisibility(View.VISIBLE);
            }
        });

        Button sanadBtn = view.findViewById(R.id.fileSanadBtn);
        sanadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RealmConfig == null){
                    RealmConfig = new RealmConfiguration.Builder()
                            .name("info.realm")
                            .deleteRealmIfMigrationNeeded()
                            .build();
                }

                realm = Realm.getInstance(RealmConfig);
                sanadList = realm.where(SanadRecordObject.class).findAll().sort("Tarikh", Sort.DESCENDING);
                sanadAdapter = new SanadListAdapter(getContext(), sanadList);
                RecyclerView sanadRecView = parentView.findViewById(R.id.fileList);
                sanadRecView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//                sanadRecView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                sanadRecView.setAdapter(sanadAdapter);
                CardView CV = parentView.findViewById(R.id.fileHesabHeader);
                CV.setVisibility(View.GONE);
                CV = parentView.findViewById(R.id.fileSanadHeader);
                CV.setVisibility(View.VISIBLE);
                sanadRecView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        fileAddr = file.getAbsolutePath();

        if(fileAddr.equals("")){
            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "فایلی برای ورود اطلاعات انتخاب نشده است", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
        }else {
            if(Build.VERSION.SDK_INT >= 23){
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permission","Permission is granted");
                    importFileTask = new ImportFileTask();
                    importFileTask.execute(fileAddr);
                } else {
                    Log.v("Permission","Permission is revoked");
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }
            }else {
                importFileTask = new ImportFileTask();
                importFileTask.execute(fileAddr);
            }
        }
    }

    @Override
    public void onDestroy() {
        if(realm != null){
            realm.close();
        }

        super.onDestroy();
    }

    public class ImportFileTask extends AsyncTask<String, String, String> {

        boolean ImportSuccessfull;

        @Override
        protected String doInBackground(String... strings) {
            Importer importer = new Importer(getContext());
            ImportSuccessfull = importer.ImportFromFile(strings[0], true, progressHandler);

            return "";
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            ((Button)getView().findViewById(R.id.fileImportBtn)).setEnabled(false);
            ((Button)getView().findViewById(R.id.fileHesabBtn)).setEnabled(false);
            ((Button)getView().findViewById(R.id.fileSanadBtn)).setEnabled(false);

//            ImageView progImg = (ImageView)getView().findViewById(R.id.fileProgressImg);
//            progImg.setVisibility(View.VISIBLE);
//            ((AnimationDrawable)progImg.getBackground()).start();

            pBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pBar.setVisibility(View.GONE);

//            ImageView progImg = (ImageView)getView().findViewById(R.id.fileProgressImg);
//            progImg.setVisibility(View.INVISIBLE);
//            ((AnimationDrawable)progImg.getBackground()).stop();

            ((Button)getView().findViewById(R.id.fileImportBtn)).setEnabled(true);
            ((Button)getView().findViewById(R.id.fileHesabBtn)).setEnabled(true);
            ((Button)getView().findViewById(R.id.fileSanadBtn)).setEnabled(true);

            if (ImportSuccessfull) {
                Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "اطلاعات فایل با موفقیت ثبت شد", Snackbar.LENGTH_LONG);
                ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                sText.setTextColor(getResources().getColor(android.R.color.white));
                snackBar.show();
            } else {
                Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "خطا در خواندن اطلاعات فایل !", Snackbar.LENGTH_LONG);
                ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                TextView sText = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                sText.setTextColor(getResources().getColor(android.R.color.white));
                snackBar.show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean imported = false;
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == 1){
                File rootDir = Environment.getExternalStorageDirectory();
                if(rootDir != null){
                    File importFile = new File(rootDir.getAbsolutePath() + "/HesabyarAND.hinfo");
                    if(importFile.exists()){
                        fileAddr = importFile.getAbsolutePath();
                        importFileTask = new ImportFileTask();
                        importFileTask.execute(fileAddr);

                        imported = true;
                    }
                }

                if(!imported) {
                    FileDialog openDlg = new OpenFileDialog();
                    openDlg.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
                    Bundle args = new Bundle();
                    args.putString(FileDialog.EXTENSION, "hinfo");
//                args.putSerializable(FileDialog.ROOT_DIRECTORY, F);
//                args.putSerializable(FileDialog.START_DIRECTORY, F);
                    openDlg.setArguments(args);
                    openDlg.show(getChildFragmentManager(), OpenFileDialog.class.getName());
                }
            }else{
                Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                importFileTask = new ImportFileTask();
                importFileTask.execute(fileAddr);
            }
        }else{
            Snackbar snackBar = Snackbar.make(getActivity().findViewById(R.id.mainCoordinatorLayout), "اجازه دسترسی به حافظه داده نشد", Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            TextView sText = (TextView)snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
            sText.setTextColor(getResources().getColor(android.R.color.white));
            snackBar.show();
        }
    }
}
