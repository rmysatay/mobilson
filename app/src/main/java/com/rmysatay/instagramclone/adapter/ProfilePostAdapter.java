package com.rmysatay.instagramclone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmysatay.instagramclone.databinding.GridItemBinding;
import com.rmysatay.instagramclone.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public ProfilePostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridItemBinding binding = GridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = postArrayList.get(position);
        Picasso.get().load(post.downloadUrl).into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        GridItemBinding binding;

        public PostHolder(GridItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
} 