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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarnamehListAdapter extends RecyclerView.Adapter<BarnamehListAdapter.BarnamehViewHolder> {

    private Context mContext;
    List<BarnamehListItem> barnamehList;
    private CheckBox SelAllChBox;
    public boolean onBind = false;

    public BarnamehListAdapter(Context context, List<BarnamehListItem> list, CheckBox selAll) {
        mContext = context;
        if(list != null){
            barnamehList = new ArrayList<>(list);
        }else{
            barnamehList = new ArrayList<>();
        }
        SelAllChBox = selAll;
    }

    @Override
    public BarnamehViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barnameh, parent, false);

        return new BarnamehViewHolder(itemView, SelAllChBox);
    }

    @Override
    public void onBindViewHolder(final BarnamehViewHolder holder, int position) {

        BarnamehListItem obj = barnamehList.get(position);
        holder.data = obj;
        if(obj != null) {
            holder.SMSChBox.setChecked(obj.isSelectedForSMS());
            holder.SMSChBox.setOnCheckedChangeListener(null);
            holder.SMSChBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    barnamehList.get(holder.getAdapterPosition()).setSelectedForSMS(b);
                    onBind = true;
                    if(!b){
                        holder.selAllChBox.setChecked(false);
                    }else{
                        for(int i = 0; i < barnamehList.size(); i++){
                            if(!barnamehList.get(i).isSelectedForSMS()){
                                b = false;
                                break;
                            }
                        }
                        holder.selAllChBox.setChecked(b);
                    }
                    onBind = false;
                }
            });
            if(!obj.isSelectedForSMS()){
                onBind = true;
                holder.selAllChBox.setChecked(false);
                onBind = false;
            }
            holder.LadingNoTxt.setText(Integer.toString(obj.getLadingNo()));
            holder.CarNoTxt.setText(obj.getCarNo());
            holder.DateTxt.setText(obj.getTarikh());
            DecimalFormat df = new DecimalFormat("#.##");
            holder.WeightTxt.setText(df.format(obj.getInWeight()));
            holder.SafiTxt.setText(String.format(new Locale("fa", "IR"),"%,d", obj.getSafi()));

            if(position % 2 == 0){
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
            }else{
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
            }
        }
    }

    @Override
    public int getItemCount() {
        return barnamehList.size();
    }

    class BarnamehViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
        private CheckBox SMSChBox;
        private TextView LadingNoTxt;
        private TextView CarNoTxt;
        private TextView DateTxt;
        private TextView WeightTxt;
        private TextView SafiTxt;
        public BarnamehListItem data;

        private CheckBox selAllChBox;

        public BarnamehViewHolder(View itemView, CheckBox selAll) {
            super(itemView);

            SMSChBox = itemView.findViewById(R.id.item_barnameh_smschbox);
            LadingNoTxt = itemView.findViewById(R.id.item_barnameh_ladingnotxt);
            CarNoTxt = itemView.findViewById(R.id.item_barnameh_carnotxt);
            DateTxt = itemView.findViewById(R.id.item_barnameh_datetxt);
            WeightTxt = itemView.findViewById(R.id.item_barnameh_weighttxt);
            SafiTxt = itemView.findViewById(R.id.item_barnameh_safitxt);
            backView = itemView.findViewById(R.id.item_barnameh_back);

            selAllChBox = selAll;
        }
    }

}
