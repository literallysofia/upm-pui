package com.example.newsmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.Image;
import es.upm.hcid.pui.assignment.Utils;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class ArticleActivity extends AppCompatActivity {

    private int articleId;
    private LinearLayout articleContent;
    private TextView titleText;
    private ImageView imageView;
    private ProgressBar progressBar;

    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_CAMERA = 1;
    private static int RESULT_SELECT_IMAGE = 2;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.articleId = getIntent().getIntExtra("ArticleID", 0);
        this.titleText = findViewById(R.id.article_title);
        this.imageView = findViewById(R.id.article_image);

        this.progressBar = findViewById(R.id.progressBar);
        this.articleContent = findViewById(R.id.article_content);

        // Start the AsyncTask to fetch the data
        DownloadArticleTask downloadArticleTask = new DownloadArticleTask();
        downloadArticleTask.execute();
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
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Choose an action");
        String[] pictureDialogItems = {
                "Select from Gallery",
                "Open Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (mayRequestExternalStorage())
                                    openGallery();
                                break;
                            case 1:
                                if (mayRequestCamera())
                                    openCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_SELECT_IMAGE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.newsmanager",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //Start the camera application
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    private boolean mayRequestExternalStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean mayRequestCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates the image file in the external directory
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
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

        if (requestCode == REQUEST_CAMERA) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            UploadImageTask uploadImageTask = new UploadImageTask();
            uploadImageTask.newImage = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            uploadImageTask.execute();
            System.out.println("IMAGE SAVED");
        }
    }

    private final class DownloadArticleTask extends AsyncTask<Void, Void, Article> {

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
            ArticleActivity.this.titleText.setText(article.getTitleText());
            try {
                ArticleActivity.this.imageView.setImageBitmap(Utils.base64StringToImg(article.getImage().getImage()));
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }
    }

    private final class UploadImageTask extends AsyncTask<Void, Void, List<Article>> {

        public Bitmap newImage;
        private Article article = null;

        @Override
        protected List<Article> doInBackground(Void... params) {

            List<Article> articles = null;

            try {
                article = DataManager.getInstance().getModelManager().getArticle(articleId);
                Image image = article.addImage(Utils.encodeImage(this.newImage), "thumbnail");
                image.save();
                articles = DataManager.getInstance().getModelManager().getArticles();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }

            return articles;
        }

        @Override
        protected void onPreExecute() {
            articleContent.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(List<Article> articles) {
            super.onPostExecute(articles);
            progressBar.setVisibility(View.GONE);
            articleContent.setVisibility(View.VISIBLE);
            try {
                ArticleActivity.this.imageView.setImageBitmap(Utils.base64StringToImg(article.getImage().getImage()));
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
            DataManager.getInstance().updateAdapter(articles);
            Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
