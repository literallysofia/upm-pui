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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {

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
        DownloadArticlesTask downloadArticlesTask = new DownloadArticlesTask();
        downloadArticlesTask.execute();

        // Create adapter passing in the sample user data
        adapter = new NewsAdapter(articles, this, new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article item) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("ArticleID", item.getId());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        // Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final class DownloadArticlesTask extends AsyncTask<Void, Void, List<Article>> {

        ProgressBar progressBar = findViewById(R.id.progressBar);

        @Override
        protected List<Article> doInBackground(Void... params) {

            List<Article> articles = null;

            try {
                articles = DataManager.getInstance().getModelManager().getArticles();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }

            return articles;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);
            progressBar.setVisibility(View.GONE);

            MainActivity.this.articles.clear();
            MainActivity.this.articles.addAll(articles);
            adapter.notifyDataSetChanged();
        }
    }
}
