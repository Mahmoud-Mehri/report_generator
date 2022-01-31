package hesabyar.report_generator;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahmood_M on 07/01/2018.
 */

public class MoeinListAdapter extends RecyclerView.Adapter<MoeinListAdapter.MoeinViewHolder>{

    private Context mContext;
    public List<MoeinListItem> MoeinList;
    private CheckBox SelAllChBox;
    public boolean onBind = false;

    public MoeinListAdapter(Context context, List<MoeinListItem> list, CheckBox selAll) {
        mContext = context;
        if(list != null){
            MoeinList = new ArrayList<>(list);
        }else{
            MoeinList = new ArrayList<>();
        }

        SelAllChBox = selAll;
    }

    @Override
    public MoeinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_moein, parent, false);
        return new MoeinViewHolder(itemView, SelAllChBox);
    }

    @Override
    public void onBindViewHolder(final MoeinViewHolder holder, int position) {
        MoeinListItem item = MoeinList.get(position);

        if (item.getPhoneNumber().equals("")){
            holder.SMSChBox.setChecked(false);
            holder.SMSChBox.setEnabled(false);
        }else {
            holder.SMSChBox.setEnabled(true);
            holder.SMSChBox.setChecked(item.isSelectedForSMS());
            holder.SMSChBox.setOnCheckedChangeListener(null);
            holder.SMSChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    MoeinList.get(holder.getAdapterPosition()).setSelectedForSMS(b);
                    onBind = true;
                    if (!b) {
                        holder.selAllChBox.setChecked(false);
                    } else {
                        for (int i = 0; i < MoeinList.size(); i++) {
                            if (!MoeinList.get(i).isSelectedForSMS()) {
                                b = false;
                                break;
                            }
                        }
                        holder.selAllChBox.setChecked(b);
                    }
                    onBind = false;
                }
            });
            if (!item.isSelectedForSMS()) {
                onBind = true;
                holder.selAllChBox.setChecked(false);
                onBind = false;
            }
        }

        holder.AccNumberTxt.setText(Integer.toString(item.getAccNumber()));
        holder.AccNameTxt.setText(item.getAccName());
        holder.PriceTxt.setText(String.format("%,d", item.getPrice()));
        switch (item.getStatus()){
            case 2 : {
                holder.StatusTxt.setText("بد");
            } break;
            case 3 : {
                holder.StatusTxt.setText("بس");
            } break;
            default : holder.StatusTxt.setText("-");
        }
        holder.DateTxt.setText(item.getHDate());

        if(position % 2 == 0){
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
        }else{
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
        }
    }

    @Override
    public int getItemCount() {
        return MoeinList.size();
    }

    public class MoeinViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
        private CheckBox SMSChBox;
        private TextView AccNumberTxt;
        private TextView AccNameTxt;
        private TextView PriceTxt;
        private TextView StatusTxt;
        private TextView DateTxt;

        private CheckBox selAllChBox;

        public MoeinViewHolder(View itemView, CheckBox selAll) {
            super(itemView);

            SMSChBox = itemView.findViewById(R.id.item_moein_smschbox);
            AccNumberTxt = itemView.findViewById(R.id.item_moein_accnumbertxt);
            AccNameTxt = itemView.findViewById(R.id.item_moein_accnametxt);
            PriceTxt = itemView.findViewById(R.id.item_moein_pricetxt);
            StatusTxt = itemView.findViewById(R.id.item_moein_statustxt);
            DateTxt = itemView.findViewById(R.id.item_moein_datetxt);

            backView = itemView.findViewById(R.id.item_moein_back);

            selAllChBox = selAll;
        }
    }
}
