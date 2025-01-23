package com.rmysatay.instagramclone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmysatay.instagramclone.databinding.RecyclerRowBinding;
import com.rmysatay.instagramclone.model.Post;
import com.rmysatay.instagramclone.model.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = postArrayList.get(position);
        String username = post.email.split("@")[0];
        holder.binding.userEmailText.setText(username);
        holder.binding.commentText.setText(post.comment);
        Picasso.get().load(post.downloadUrl).into(holder.binding.imageView);

        // Yorum gönderme butonu için click listener
        holder.binding.sendCommentButton.setOnClickListener(v -> {
            String comment = holder.binding.commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                addComment(post, comment, holder);
            }
        });

        // Yorumları yükle
        loadComments(post, holder);
    }

    private void addComment(Post post, String commentText, PostHolder holder) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        
        HashMap<String, Object> commentData = new HashMap<>();
        commentData.put("userEmail", currentUserEmail);
        commentData.put("comment", commentText);
        commentData.put("date", FieldValue.serverTimestamp());

        firestore.collection("Posts")
            .document(post.postId)
            .collection("comments")
            .add(commentData)
            .addOnSuccessListener(documentReference -> {
                holder.binding.commentEditText.setText("");
                Toast.makeText(holder.itemView.getContext(), "Comment added", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(holder.itemView.getContext(), "Error adding comment", Toast.LENGTH_SHORT).show();
            });
    }

    private void loadComments(Post post, PostHolder holder) {
        // RecyclerView ayarları
        ArrayList<Comment> comments = new ArrayList<>();
        CommentAdapter commentAdapter = new CommentAdapter(comments);
        holder.binding.commentsRecyclerView.setAdapter(commentAdapter);
        holder.binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        firestore.collection("Posts")
            .document(post.postId)
            .collection("comments")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    return;
                }

                if (value != null) {
                    comments.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String userEmail = doc.getString("userEmail");
                        String comment = doc.getString("comment");
                        if (userEmail != null && comment != null) {
                            comments.add(new Comment(userEmail, comment));
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding binding;

        public PostHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
