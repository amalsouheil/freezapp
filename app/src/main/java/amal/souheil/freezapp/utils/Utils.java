package amal.souheil.freezapp.utils;

import android.util.Log;

/**
 * Created by Souheil Amal on 2019-02-15
 */
public class Utils {
    public static Long executeLongActionDuring7seconds(){

        Log.e("TAG", "Long action is starting...");
        Long endTime = System.currentTimeMillis() + 7000;
        while (System.currentTimeMillis() <  endTime) {
            //Loop during 7 secs hehehe...
        }
        Log.e("TAG", "Long action is finished !");

        return endTime;
    }

}
