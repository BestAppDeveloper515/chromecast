package app.rayscast.air.utils;

import java.util.List;

import app.rayscast.air.models.ItemWebURL;

/**
 * Created by Anand Vardhan on 9/27/2016.
 */
public interface AsyncResponse {
    void processFinish(List<ItemWebURL> output);
}
