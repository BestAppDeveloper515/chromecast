package app.rayscast.air.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.rayscast.air.R;
import app.rayscast.air.models.HomeMenuItem;
import app.rayscast.air.ui.SquareImageView;

/**
 * Created by darshanz on 8/29/16.
 */
public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.HomeMenuViewHolder> {

    private ArrayList<HomeMenuItem> homeMenuItems;
    private OnHomeMenuSelectListener menuSelectListener;

    public HomeMenuAdapter(ArrayList<HomeMenuItem> homeMenuItems) {
        this.homeMenuItems = homeMenuItems;
    }

    @Override
    public HomeMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_menu_list, parent, false);
        return new HomeMenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeMenuViewHolder holder, int position) {
        final HomeMenuItem menuItem = homeMenuItems.get(position);
        holder.menuTitle.setText(menuItem.getLabel());
        holder.menuImage.setImageResource(menuItem.getImageResource());
        if(position==0 || position == 2 || position==4){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)holder.containeritemHomeMenuList.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            holder.containeritemHomeMenuList.setLayoutParams(params);
        }
        else{
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)holder.containeritemHomeMenuList.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            holder.containeritemHomeMenuList.setLayoutParams(params);
        }
        holder.homeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(menuSelectListener != null){
                    menuSelectListener.OnMenuSelected(menuItem.getType());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeMenuItems.size();
    }

    public class HomeMenuViewHolder extends RecyclerView.ViewHolder{
        private SquareImageView menuImage;
        private TextView menuTitle;
        private RelativeLayout homeItem;
        private LinearLayout containeritemHomeMenuList;

        public HomeMenuViewHolder(View itemView) {
            super(itemView);
            menuImage = (SquareImageView) itemView.findViewById(R.id.meenuImage);
            menuTitle = (TextView) itemView.findViewById(R.id.menuTitle);
            homeItem = (RelativeLayout) itemView.findViewById(R.id.homeItemMenu);
            containeritemHomeMenuList=(LinearLayout)itemView.findViewById(R.id.container_item_menu_list);

        }
    }

    public void setMenuSelectListener(OnHomeMenuSelectListener menuSelectListener) {
        this.menuSelectListener = menuSelectListener;
    }

    public interface OnHomeMenuSelectListener{
        void OnMenuSelected(int type);
    }
}
