package app.rayscast.air.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.models.ItemWebURL;

/**
 * Created by Qing on 8/23/2016.
 */
public class WebContentAdapter extends BaseAdapter {

    private List<ItemWebURL> webContents;
    private Context mContext;
    private LayoutInflater mInflater;
    private String token;

    public WebContentAdapter(Context context, List<ItemWebURL> webURLs, String token) {
        this.webContents = webURLs;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.token = token;
    }

    @Override
    public int getCount() {
        return webContents.size();
    }

    @Override
    public Object getItem(int position) {
        return webContents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.item_webcontent, null);

        ItemWebURL itemWebURL = webContents.get(position);

        ImageView imgHeader = (ImageView) convertView.findViewById(R.id.img_videoorimage);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.txt_title);

        TextView txtFiletype = (TextView) convertView.findViewById(R.id.txt_filetype);

        String fileName = itemWebURL.getContentName();

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (this.token.equals("FAV")) {
            imgHeader.setVisibility(View.GONE);
        } else {
            imgHeader.setVisibility(View.VISIBLE);
        }

        if (fileType.equals("mp3")) {
            imgHeader.setImageResource(R.mipmap.ic_music);
            txtFiletype.setText("mp3");
        } else {
            imgHeader.setImageResource(R.mipmap.ic_video);
            txtFiletype.setText("mp4");
        }
        txtTitle.setText(fileName.trim());

        return convertView;
    }

    public void deleteAllHistoryItem() {
        webContents = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void deleteItem(ItemWebURL itemWebURL) {
        webContents.remove(itemWebURL);
        notifyDataSetChanged();
    }
}
