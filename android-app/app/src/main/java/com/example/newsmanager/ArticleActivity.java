package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class ArticleActivity extends AppCompatActivity {

    private int articleId;
    private Article article;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        articleId = getIntent().getIntExtra("ArticleID", 0);
        this.titleText = findViewById(R.id.article_title);

        // Start the AsyncTask to fetch the data
        DownloadArticleTask downloadArticleTask = new DownloadArticleTask();
        downloadArticleTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final class DownloadArticleTask extends AsyncTask<Void, Void, Article> {

        ProgressBar progressBar = findViewById(R.id.progressBar);

        @Override
        protected Article doInBackground(Void... params) {

            Article article = null;

            try {
                article = DataManager.getInstance().getModelManager().getArticle(articleId);
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }

            return article;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Article article) {
            super.onPostExecute(article);
            progressBar.setVisibility(View.GONE);
            ArticleActivity.this.article = article;
            ArticleActivity.this.titleText.setText(article.getTitleText());
        }
    }
}
