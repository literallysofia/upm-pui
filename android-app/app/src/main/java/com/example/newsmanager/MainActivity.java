package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;


import java.util.List;
import java.util.Properties;
import java.io.IOException;



import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.Image;
import es.upm.hcid.pui.assignment.ModelManager;
import es.upm.hcid.pui.assignment.Utils;
import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {


    ModelManager mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LongOperation runningTask;
        runningTask = new LongOperation();
        runningTask.execute();

    }


    private final class LongOperation extends AsyncTask<Void, Void, ModelManager> {

        @Override
        protected ModelManager doInBackground(Void... params) {


            Properties prop = new Properties();
            prop.setProperty(ModelManager.ATTR_LOGIN_USER, "us_3_2");
            prop.setProperty(ModelManager.ATTR_LOGIN_PASS, "48392");
            prop.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
            prop.setProperty(ModelManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

            // Log in
            ModelManager mm = null;
            try{

                mm = new ModelManager(prop);
            }catch (AuthenticationError e) {
                e.printStackTrace();
                Log.e("AsyncLogin",e.getLocalizedMessage());
            }

            // get list of articñes for logged user
            List<Article> res = null;
            try {
                res = mm.getArticles();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
            for (Article article : res) {
                System.out.println(article);
            }

            return  mm;
        }

        @Override
        protected void onPostExecute(ModelManager modelManager) {
            super.onPostExecute(modelManager);

            MainActivity.this.mm=modelManager;
        }
    }

}
