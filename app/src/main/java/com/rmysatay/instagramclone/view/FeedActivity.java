package com.rmysatay.instagramclone.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmysatay.instagramclone.R;
import com.rmysatay.instagramclone.adapter.PostAdapter;
import com.rmysatay.instagramclone.databinding.ActivityFeedBinding;
import com.rmysatay.instagramclone.model.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar ayarları
        setSupportActionBar(binding.toolbar);

        // Firebase init
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        
        // RecyclerView ayarları
        postArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

        getData();
    }

    private void getData() {
        // Kullanıcı oturum kontrolü
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show();
            startActivity(new Intent(FeedActivity.this, MainActivity.class));
            finish();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {  // get() yerine addSnapshotListener kullanıyoruz
                binding.progressBar.setVisibility(View.GONE);
                
                if (error != null) {
                    Toast.makeText(FeedActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FeedActivity", "Error loading posts", error);
                    return;
                }

                if (value != null) {
                    postArrayList.clear();
                    
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            String userEmail = (String) data.get("useremail");
                            String comment = (String) data.get("comment");
                            String downloadUrl = (String) data.get("downloadurl");
                            String postId = document.getId();

                            if (downloadUrl != null && userEmail != null) {
                                Post post = new Post(postId, userEmail, comment, downloadUrl);
                                postArrayList.add(post);
                                Log.d("FeedActivity", "Post added: " + postId);
                            }
                        }
                    }
                    
                    postAdapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post) {
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.sign_out) {
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        } else if (item.getItemId() == R.id.profile) {
            Intent intentToProfile = new Intent(FeedActivity.this, ProfileActivity.class);
            startActivity(intentToProfile);
        }
        return super.onOptionsItemSelected(item);
    }

}