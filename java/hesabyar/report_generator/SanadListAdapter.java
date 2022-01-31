package hesabyar.report_generator;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Mahmood_M on 20/01/2018.
 */

public class SanadListAdapter extends RealmRecyclerViewAdapter<SanadRecordObject, SanadListAdapter.SanadViewHolder> {

    private Context mContext;

    public SanadListAdapter(Context context, OrderedRealmCollection<SanadRecordObject> data) {
        super(data, true);

        mContext = context;
    }

    @Override
    public SanadListAdapter.SanadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sanad, parent, false);
        return new SanadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SanadListAdapter.SanadViewHolder holder, int position) {
        SanadRecordObject obj = getItem(position);
        holder.data = obj;
        if (obj != null) {
            holder.TarikhTxt.setText(obj.getTarikh());
            holder.SharhTxt.setText(obj.getSharh());
            holder.BedTxt.setText(String.format(new Locale("fa", "IR"),"%,d", obj.getBed()));
            holder.BesTxt.setText(String.format(new Locale("fa", "IR"),"%,d", obj.getBes()));
            holder.MandehTxt.setText(String.format(new Locale("fa", "IR"),"%,d", obj.getMandeh()));
            holder.StatusTxt.setText(obj.getStatus());

            if(position % 2 == 0){
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_even));
            }else{
                holder.backView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_odd));
            }
        }
    }

    class SanadViewHolder extends RecyclerView.ViewHolder{

        private CardView backView;
//        private TextView SIDTxt;
//        private TextView AccNoTxt;
//        private TextView IndTxt;
//        private TextView ResidNoTxt;
        private TextView TarikhTxt;
        private TextView SharhTxt;
//        private TextView QuantityTxt;
//        private TextView WeightTxt;
//        private TextView FiTxt;
        private TextView BedTxt;
        private TextView BesTxt;
        private TextView MandehTxt;
        private TextView StatusTxt;

        public SanadRecordObject data;

        public SanadViewHolder(View itemView) {
            super(itemView);

            TarikhTxt = itemView.findViewById(R.id.item_sanad_tarikhtxt);
            SharhTxt = itemView.findViewById(R.id.item_sanad_sharhtxt);
            BedTxt = itemView.findViewById(R.id.item_sanad_bedtxt);
            BesTxt = itemView.findViewById(R.id.item_sanad_bestxt);
            MandehTxt = itemView.findViewById(R.id.item_sanad_mandehtxt);
            StatusTxt = itemView.findViewById(R.id.item_sanad_statustxt);

            backView = itemView.findViewById(R.id.item_sanad_back);
        }
    }
}
