package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
    List<Article> articles = new ArrayList<>();

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
        adapter = new NewsAdapter(articles, this, new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article item) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                //intent.putExtra("Article", item);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        // Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(adapter);
    }

    private final class NewsAsyncTask extends AsyncTask<Void, Void, ModelManager> {

        ProgressBar progressBar = findViewById(R.id.progressBar);

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
                articles.clear();
                articles.addAll(mm.getArticles());
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
            for (Article article : articles) {
                System.out.println(article);
            }

            return mm;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ModelManager modelManager) {
            super.onPostExecute(modelManager);
            progressBar.setVisibility(View.GONE);
            MainActivity.this.modelManager = modelManager;
            adapter.notifyDataSetChanged();
        }
    }
}
