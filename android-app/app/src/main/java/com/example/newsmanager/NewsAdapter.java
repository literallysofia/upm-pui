package com.example.newsmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.hcid.pui.assignment.Article;

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

        public MyViewHolder(View itemView) {
            super(itemView);
            this.thumbnailImage = itemView.findViewById(R.id.card_thumbnail);
            this.titleText = itemView.findViewById(R.id.card_title);
            this.abstractText = itemView.findViewById(R.id.card_abstract);
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

        Article currentArticle = articles.get(position);

        textViewTitle.setText(currentArticle.getTitleText());
        textViewAbstract.setText(currentArticle.getAbstractText());

        //imageViewThumbnail.setImageResource(currentArticle.getImage());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

}
