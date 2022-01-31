package hesabyar.report_generator;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Mahmood_M on 07/01/2018.
 */

public class HesabListAdapter extends RecyclerView.Adapter<HesabListAdapter.HesabViewHolder>{

    private Context mContext;
    public List<HesabListItem> HesabList;

    public HesabListAdapter(Context context, List<HesabListItem> list) {
        mContext = context;
        if(list != null){
            HesabList = new ArrayList<>(list);
        }else{
            HesabList = new ArrayList<>();
        }
    }

    @Override
    public HesabListAdapter.HesabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_hesab, parent, false);
        return new HesabListAdapter.HesabViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HesabListAdapter.HesabViewHolder holder, int position) {
        HesabListItem item = HesabList.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        holder.QuantityTxt.setText(df.format(item.getQuantity()));
        holder.WeightTxt.setText(df.format(item.getWeight()));
        holder.FiTxt.setText(String.format(new Locale("fa", "IR"),"%,d", item.getFi()));
        holder.BedTxt.setText(String.format(new Locale("fa", "IR"),"%,d", item.getBed()));
        holder.BesTxt.setText(String.format(new Locale("fa", "IR"),"%,d", item.getBes()));
        holder.MandehTxt.setText(String.format(new Locale("fa", "IR"),"%,d", item.getMandeh()));

        holder.TarikhTxt.setText(item.getTarikh());
        holder.SharhTxt.setText(item.getSharh());

        switch (item.getStatus()){
            case 2 : {
                holder.StatusTxt.setText("بد");
            } break;
            case 3 : {
                holder.StatusTxt.setText("بس");
            } break;
            default : holder.StatusTxt.setText("-");
        }

        if(position % 2 == 0){
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
        }else{
            holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
        }
    }

    @Override
    public int getItemCount() {
        return HesabList.size();
    }

    public class HesabViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
//        private TextView IndTxt;
//        private TextView ResidNoTxt;
        private TextView TarikhTxt;
        private TextView SharhTxt;
        private TextView QuantityTxt;
        private TextView WeightTxt;
        private TextView FiTxt;
        private TextView BedTxt;
        private TextView BesTxt;
        private TextView MandehTxt;
        private TextView StatusTxt;

        public HesabViewHolder(View itemView) {
            super(itemView);

            TarikhTxt = itemView.findViewById(R.id.item_hesab_tarikhtxt);
            SharhTxt = itemView.findViewById(R.id.item_hesab_sharhtxt);
            QuantityTxt = itemView.findViewById(R.id.item_hesab_quantitytxt);
            WeightTxt = itemView.findViewById(R.id.item_hesab_weighttxt);
            FiTxt = itemView.findViewById(R.id.item_hesab_fitxt);
            BedTxt = itemView.findViewById(R.id.item_hesab_bedtxt);
            BesTxt = itemView.findViewById(R.id.item_hesab_bestxt);
            MandehTxt = itemView.findViewById(R.id.item_hesab_mandehtxt);
            StatusTxt = itemView.findViewById(R.id.item_hesab_statustxt);

            backView = itemView.findViewById(R.id.item_hesab_back);
        }
    }

}
