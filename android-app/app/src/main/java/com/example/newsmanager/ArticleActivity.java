package com.example.newsmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.Image;
import es.upm.hcid.pui.assignment.Utils;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ArticleActivity extends AppCompatActivity {

    private int articleId;
    private Article article;
    private TextView titleText;

    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static int RESULT_SELECT_IMAGE = 1;

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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_SELECT_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.article_menu_edit:
                if (mayRequestExternalStorage())
                    openGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean mayRequestExternalStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                UploadImageTask uploadImageTask = new UploadImageTask();
                uploadImageTask.newImage = bitmap;
                uploadImageTask.execute();
                System.out.println("IMAGE SAVED");

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private final class UploadImageTask extends AsyncTask<Void, Void, Article> {

        public Bitmap newImage;

        @Override
        protected Article doInBackground(Void... params) {

            Article article = null;

            try {
                article = DataManager.getInstance().getModelManager().getArticle(articleId);
                Image image = article.addImage(Utils.encodeImage(this.newImage), "thumbnail");
                image.save();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }

            return article;
        }

        @Override
        protected void onPostExecute(Article article) {
            super.onPostExecute(article);
            ArticleActivity.this.article = article;
        }
    }
}
