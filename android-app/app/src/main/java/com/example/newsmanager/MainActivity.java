package com.example.newsmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

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
        DataManager.getInstance().setAdapter(new NewsAdapter(DataManager.getInstance().getArticles(), this, new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article item) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("ArticleID", item.getId());
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        }));

        // Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(DataManager.getInstance().getAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.main_menu_login);

        if (DataManager.getInstance().isLoggedIn()) {
            item.setIcon(R.drawable.ic_lock_open);
        } else {
            item.setIcon(R.drawable.ic_lock);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_login:
                if (DataManager.getInstance().isLoggedIn()) {
                    showLogoutDialog(item);
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLogoutDialog(final MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out of " + DataManager.getInstance().getCurrentUser() + "?")
                .setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        item.setIcon(R.drawable.ic_lock);
                        DataManager.getInstance().setCurrentUser(null);
                        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
            DataManager.getInstance().updateAdapter(articles);
        }
    }
}
