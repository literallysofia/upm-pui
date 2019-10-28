package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.ModelManager;
import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {

    ModelManager modelManager;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Article> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Lookup the recycler view in activity layout
        recyclerView = findViewById(R.id.recycler_View);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set layout manager to position the items
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Start the AsyncTask to fetch the data
        NewsAsyncTask newsAsyncTask = new NewsAsyncTask();
        newsAsyncTask.execute();

        // Create adapter passing in the sample user data
        adapter = new NewsAdapter(data, this);

        // Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(adapter);
    }

    private final class NewsAsyncTask extends AsyncTask<Void, Void, ModelManager> {

        @Override
        protected ModelManager doInBackground(Void... params) {

            Properties prop = new Properties();
            prop.setProperty(ModelManager.ATTR_LOGIN_USER, "us_3_2");
            prop.setProperty(ModelManager.ATTR_LOGIN_PASS, "48392");
            prop.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
            prop.setProperty(ModelManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

            // Log in
            ModelManager mm = null;
            try {
                mm = new ModelManager(prop);
            } catch (AuthenticationError e) {
                e.printStackTrace();
                Log.e("AsyncLogin", e.getLocalizedMessage());
            }

            // get list of articles for logged user
            try {
                data.clear();
                data.addAll(mm.getArticles());
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
            for (Article article : data) {
                System.out.println(article);
            }

            return mm;
        }

        @Override
        protected void onPostExecute(ModelManager modelManager) {
            super.onPostExecute(modelManager);
            MainActivity.this.modelManager = modelManager;
            adapter.notifyDataSetChanged();
        }
    }

}
