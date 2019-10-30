package com.example.newsmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import es.upm.hcid.pui.assignment.ModelManager;
import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;

public class DataManager {

    static private DataManager instance = null;
    private ModelManager modelManager;

    /**
     * Gets singleton instance
     *
     * @return singleton instance
     */
    static public DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    /**
     * Data Manager constructor
     */
    private DataManager() {

        // Start the AsyncTask to fetch the data
        DownloadModelManagerTask downloadModelManagerTask = new DownloadModelManagerTask();
        downloadModelManagerTask.execute();
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    private final class DownloadModelManagerTask extends AsyncTask<Void, Void, ModelManager> {

        @Override
        protected ModelManager doInBackground(Void... params) {

            Properties prop = new Properties();
            prop.setProperty(ModelManager.ATTR_LOGIN_USER, "us_3_2");
            prop.setProperty(ModelManager.ATTR_LOGIN_PASS, "48392");
            prop.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
            prop.setProperty(ModelManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

            // Log in
            ModelManager modelManager = null;
            try {
                modelManager = new ModelManager(prop);
            } catch (AuthenticationError e) {
                e.printStackTrace();
                Log.e("AsyncLogin", e.getLocalizedMessage());
            }

            return modelManager;
        }

        @Override
        protected void onPostExecute(ModelManager modelManager) {
            super.onPostExecute(modelManager);
            DataManager.this.modelManager = modelManager;
        }
    }
}
