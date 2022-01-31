package hesabyar.report_generator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Mahmood_M on 19/01/2018.
 */

public class AccountListAdapter extends RealmRecyclerViewAdapter<AccountRecordObject, AccountListAdapter.AccountViewHolder> {

    private Context mContext;

    public AccountListAdapter(Context context, OrderedRealmCollection<AccountRecordObject> data) {
        super(data, true);

        mContext = context;
    }

    @Override
    public AccountListAdapter.AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);

        return new AccountViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AccountListAdapter.AccountViewHolder holder, int position) {
        AccountRecordObject obj = getItem(position);
        holder.data = obj;
        if(obj != null) {
            holder.AccNoTxt.setText(Integer.toString(obj.getAccNo()));
            holder.AccNameTxt.setText(obj.getAccName());

            if(position % 2 == 0){
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
            }else{
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
            }
        }
    }

    class AccountViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
        private TextView AccNoTxt;
        private TextView AccNameTxt;
        public AccountRecordObject data;

        public AccountViewHolder(View itemView) {
            super(itemView);

            AccNoTxt = itemView.findViewById(R.id.item_account_accnumbertxt);
            AccNameTxt = itemView.findViewById(R.id.item_account_accnametxt);
            backView = itemView.findViewById(R.id.item_account_back);
        }
    }
}
