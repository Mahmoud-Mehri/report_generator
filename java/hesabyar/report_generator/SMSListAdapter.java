package hesabyar.report_generator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class SMSListAdapter extends RecyclerView.Adapter<SMSListAdapter.SMSViewHolder> {

    Context mContext;
    List<SMSItem> SMSList;

    public SMSListAdapter(Context context, List<SMSItem> list){
        mContext = context;
        if(list != null){
            SMSList = new ArrayList<>(list);
        }else{
            SMSList = new ArrayList<>();
        }
    }

    @Override
    public SMSListAdapter.SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        return new SMSViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SMSListAdapter.SMSViewHolder holder, int position) {
        SMSItem item = SMSList.get(position);
        holder.AccNameTxt.setText(item.getAccName());
        holder.PhoneTxt.setText(item.getPhoneNumber());
        switch(item.getStatus()) {
            case 1: {
                holder.StatusTxt.setText("در انتظار ارسال");
                holder.StatusTxt.setTextColor(BLACK);
            }break;
            case 2: {
                holder.StatusTxt.setText("ارسال شد");
                holder.StatusTxt.setTextColor(BLACK);
            }break;
            case 3: {
                holder.StatusTxt.setText("خطا در ارسال");
                holder.StatusTxt.setTextColor(RED);
            }break;
            case 4: {
                holder.StatusTxt.setText("شماره نادرست");
                holder.StatusTxt.setTextColor(RED);
            }break;
            default: {
                holder.StatusTxt.setText("نامشخص !");
                holder.StatusTxt.setTextColor(RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return SMSList.size();
    }

    class SMSViewHolder extends RecyclerView.ViewHolder{

        TextView AccNameTxt;
        TextView PhoneTxt;
        TextView StatusTxt;

        public SMSViewHolder(View itemView){
            super(itemView);

            AccNameTxt = itemView.findViewById(R.id.item_sms_accname);
            PhoneTxt = itemView.findViewById(R.id.item_sms_phone);
            StatusTxt = itemView.findViewById(R.id.item_sms_status);
        }

    }


}
