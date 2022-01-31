package hesabyar.report_generator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Mahmood_M on 29/01/2018.
 */

public class AccountSearchAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<AccountSearchItem> accountList;

//    private RealmConfiguration realmConfig;
//    private Realm realm;
    private int searchType;

    public AccountSearchAdapter(Context context, int SearchType) {
        mContext = context;
        searchType = SearchType; // 1:AccNo, 2:AccName

        accountList = new ArrayList<>();

//        realmConfig = new RealmConfiguration.Builder().name("info.realm").build();
//        realm = Realm.getInstance(realmConfig);
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public AccountSearchItem getItem(int index) {
        return accountList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_account_search, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.item_account_search_accnametxt)).setText(getItem(position).getAccName());
        ((TextView)convertView.findViewById(R.id.item_account_search_accnumbertxt)).setText(getItem(position).getAccNo());

        if(position % 2 == 0){
            ((CardView)convertView.findViewById(R.id.item_account_search_back)).setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_search_even));
        }else{
            ((CardView)convertView.findViewById(R.id.item_account_search_back)).setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null) {
                    String query = constraint.toString().toLowerCase();
                    List<AccountSearchItem> list = filterAccounts(query);

                    results.count = list.size();
                    results.values = list;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (constraint != null) {
//                    String query = constraint.toString().toLowerCase();
//                    accountList = filterAccounts(query);
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
                if(results != null && results.count > 0) {
                    accountList.clear();
                    accountList = new ArrayList<>((List<AccountSearchItem>)results.values);
                }else{
                    accountList.clear();
                }

                notifyDataSetChanged();
            }};
    }

    @NonNull
    private List<AccountSearchItem> filterAccounts(String query) {
        RealmConfiguration realmConfig;
        Realm realm;
        realmConfig = new RealmConfiguration.Builder().name("info.realm").deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfig);
        RealmResults<AccountRecordObject> accList;
        List<AccountSearchItem> list = new ArrayList<>();
        try {
            if (searchType == 1) {
                accList = realm.where(AccountRecordObject.class).findAll();
                for (int i = 0; i < accList.size(); i++) {
                    AccountRecordObject accObj = accList.get(i);
                    String AccNo = Integer.toString(accObj.getAccNo());
                    if (AccNo.startsWith(query)) {
                        AccountSearchItem accItem = new AccountSearchItem();
                        accItem.setAccName(accObj.getAccName());
                        accItem.setAccNo(Integer.toString(accObj.getAccNo()));
                        list.add(accItem);
                    }
                }
            } else {
                accList = realm.where(AccountRecordObject.class).like("AccName", "*" + query + "*").findAll();
                for (int i = 0; i < accList.size(); i++) {
                    AccountRecordObject accObj = accList.get(i);
                    AccountSearchItem accItem = new AccountSearchItem();
                    accItem.setAccName(accObj.getAccName());
                    accItem.setAccNo(Integer.toString(accObj.getAccNo()));
                    list.add(accItem);
                }
            }
        }finally{
            realm.close();
        }

        return list;
    }

}