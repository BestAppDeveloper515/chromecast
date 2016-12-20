package app.rayscast.air.utils;

import java.util.ArrayList;

import app.rayscast.air.R;
import app.rayscast.air.models.HomeMenuItem;

/**
 * Created by darshanz on 8/29/16.
 */
public class HomeMenuHelper {

    public static ArrayList<HomeMenuItem> getHomeMenu() {
        ArrayList<HomeMenuItem> list = new ArrayList<>();
        list.add(new HomeMenuItem(R.drawable.audio_hdpi, "Audio", HomeMenuItem.AUDIO));
        list.add(new HomeMenuItem(R.drawable.video_hdpi, "Video", HomeMenuItem.VIDEO));
        list.add(new HomeMenuItem(R.drawable.gallery_hdpi, "Gallery", HomeMenuItem.GALLERY));
        list.add(new HomeMenuItem(R.drawable.web_hdpi, "Cast from Web", HomeMenuItem.WEB));
        list.add(new HomeMenuItem(R.drawable.dropbox_hdpi, "Dropbox", HomeMenuItem.DROPBOX));
        list.add(new HomeMenuItem(R.drawable.add_link_hdpi, "Add Link", HomeMenuItem.ADD_LINK));
        return list;
    }
}
