package com.rmysatay.instagramclone.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmysatay.instagramclone.databinding.ActivityProfileBinding;
import com.rmysatay.instagramclone.adapter.ProfilePostAdapter;
import com.rmysatay.instagramclone.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private ArrayList<Post> userPosts;
    private ProfilePostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar ayarları
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // RecyclerView ayarları
        userPosts = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        postAdapter = new ProfilePostAdapter(userPosts);
        binding.recyclerView.setAdapter(postAdapter);

        if (user != null) {
            String username = user.getEmail().split("@")[0];
            binding.usernameText.setText(username);
            binding.bioText.setText("Hey there! I am using Instagram Clone");
            
            // Kullanıcının postlarını getir
            getUserPosts(user.getEmail());
        }
    }

    private void getUserPosts(String userEmail) {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        FirebaseFirestore.getInstance().collection("Posts")
            .whereEqualTo("useremail", userEmail)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                binding.progressBar.setVisibility(View.GONE);
                
                if (error != null) {
                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (value != null) {
                    userPosts.clear();
                    int postCount = 0;
                    
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data != null) {
                            String downloadUrl = (String) data.get("downloadurl");
                            if (downloadUrl != null) {
                                String comment = (String) data.get("comment");
                                String postId = snapshot.getId();
                                Post post = new Post(postId,userEmail, comment, downloadUrl);
                                userPosts.add(post);
                                postCount++;
                            }
                        }
                    }
                    
                    binding.postsCountText.setText("Posts: " + postCount);
                    postAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 