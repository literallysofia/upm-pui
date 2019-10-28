package com.example.newsmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;

import java.util.List;

import es.upm.hcid.pui.assignment.Article;
import es.upm.hcid.pui.assignment.Utils;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<Article> articles;
    private Context context;

    public NewsAdapter(List<Article> data, Context context) {
        this.articles = data;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnailImage;
        TextView titleText;
        TextView abstractText;
        Chip categoryChip;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.thumbnailImage = itemView.findViewById(R.id.card_thumbnail);
            this.titleText = itemView.findViewById(R.id.card_title);
            this.abstractText = itemView.findViewById(R.id.card_abstract);
            this.categoryChip = itemView.findViewById(R.id.card_category);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.article_card, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        ImageView imageViewThumbnail = holder.thumbnailImage;
        TextView textViewTitle = holder.titleText;
        TextView textViewAbstract = holder.abstractText;
        Chip chipCategory = holder.categoryChip;
        String thumbnailString;

        Article currentArticle = articles.get(position);

        textViewTitle.setText(currentArticle.getTitleText());
        textViewAbstract.setText(currentArticle.getAbstractText());
        chipCategory.setText(currentArticle.getCategory());
        try {
            thumbnailString = currentArticle.getImage().getImage();
            imageViewThumbnail.setImageBitmap(Utils.base64StringToImg(thumbnailString));
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

}
