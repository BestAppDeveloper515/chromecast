package app.rayscast.air.activity;

/**
 * Created by Anand Vardhan on 9/30/2016.
 */
public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               CustomModel {

    public interface OnCustomStateListener {
        void urlSelected(String urlSelected);
    }

    private static CustomModel mInstance;
    private OnCustomStateListener mListener;
    private boolean mState;


    private CustomModel() {}

    public static CustomModel getInstance() {
        if(mInstance == null) {
            mInstance = new CustomModel();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

//    public void changeState(boolean state) {
//        if(mListener != null) {
//            mState = state;
//            notifyStateChange();
//        }
//    }


    public void urlSelectedFinished(String value){
        String UrlSelectedIs=value;
        mListener.urlSelected(UrlSelectedIs);
    }


//    public boolean getState() {
//        return mState;
//    }

//    private void notifyStateChange() {
//        mListener.stateChanged();
//    }


}