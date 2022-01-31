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

public class GardeshListAdapter extends RecyclerView.Adapter<GardeshListAdapter.GardeshViewHolder>{

    private Context mContext;
    public List<GardeshListItem> GardeshList;
    private CheckBox SelAllChBox;
    public boolean onBind = false;

    public GardeshListAdapter(Context context, List<GardeshListItem> list, CheckBox selAll) {
        mContext = context;
        if(list != null){
            GardeshList = new ArrayList<>(list);
        }else{
            GardeshList = new ArrayList<>();
        }

        SelAllChBox = selAll;
    }

    @Override
    public GardeshViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gardesh, parent, false);
        return new GardeshViewHolder(itemView, SelAllChBox);
    }

    @Override
    public void onBindViewHolder(final GardeshViewHolder holder, int position) {
        GardeshListItem item = GardeshList.get(position);

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
                    GardeshList.get(holder.getAdapterPosition()).setSelectedForSMS(b);
                    onBind = true;
                    if (!b) {
                        holder.selAllChBox.setChecked(false);
                    } else {
                        for (int i = 0; i < GardeshList.size(); i++) {
                            if (!GardeshList.get(i).isSelectedForSMS()) {
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
        holder.PhoneTxt.setText(item.getPhoneNumber());

        if(position % 2 == 0){
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
        }else{
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
        }
    }

    @Override
    public int getItemCount() {
        return GardeshList.size();
    }

    public class GardeshViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
        private CheckBox SMSChBox;
        private TextView AccNumberTxt;
        private TextView AccNameTxt;
        private TextView PhoneTxt;
        private TextView PriceTxt;
        private TextView StatusTxt;

        private CheckBox selAllChBox;

        public GardeshViewHolder(View itemView, CheckBox selAll) {
            super(itemView);

            SMSChBox = itemView.findViewById(R.id.item_gardesh_smschbox);
            AccNumberTxt = itemView.findViewById(R.id.item_gardesh_accnumbertxt);
            AccNameTxt = itemView.findViewById(R.id.item_gardesh_accnametxt);
            PhoneTxt = itemView.findViewById(R.id.item_gardesh_phonetxt);
            PriceTxt = itemView.findViewById(R.id.item_gardesh_pricetxt);
            StatusTxt = itemView.findViewById(R.id.item_gardesh_statustxt);
            

            backView = itemView.findViewById(R.id.item_gardesh_back);

            selAllChBox = selAll;
        }
    }
}
