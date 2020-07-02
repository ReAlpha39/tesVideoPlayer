package id.nyaa.tesvideoplayerii;

import android.app.Application;

/**
 * Global Application
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    /**
     * Get Application
     *
     * @return BaseApplication
     */
    public synchronized static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    /**
     * Get Context
     *
     * @return getInstance()
     */
    public static MyApplication getContext() {
        return getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


}
