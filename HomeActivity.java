package com.example.linkedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;



import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class HomeActivity extends AppCompatActivity {

    // Initialize Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView skillsRecyclerView;
    private SkillsAdapter skillsAdapter;
    private List<String> skillsList;

    TextView userName,Gender,phoneNumber,Email,Bio,skillsLabel;
    ImageView profilePic;
    Button sendEmailBtn,makeCallBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        db = FirebaseFirestore.getInstance();

        ImageView profilePic = findViewById(R.id.profilePic);
        TextView userName = findViewById(R.id.userName);
        TextView Gender = findViewById(R.id.Gender);
        TextView phoneNumber = findViewById(R.id.phoneNumber);
        TextView Email = findViewById(R.id.Email);
        TextView Bio = findViewById(R.id.Bio);
        TextView skillsLabel = findViewById(R.id.skillsLabel);
        Button sendEmailBtn = findViewById(R.id.sendEmailBtn);
        Button makeCallBtn = findViewById(R.id.makeCallBtn);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the user document from Firestore
        DocumentReference userRef = db.collection("Users").document("987654321");
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get user data
                    String fetchedUserName = documentSnapshot.getString("userName");
                    String fetchedEmail = documentSnapshot.getString("email");
                    String fetchedBio = documentSnapshot.getString("bio");
                    String fetchedPhoneNumber = documentSnapshot.getString("phoneNumber");
                    String fetchedGender = documentSnapshot.getString("gender");
                    String fetchedSkills = documentSnapshot.getString("skills");
                    String fetchedProfileImageUrl = documentSnapshot.getString("profileImageUrl");


                    // Set the retrieved data to the respective TextViews
                    userName.setText(fetchedUserName);
                    Email.setText(fetchedEmail);
                    Bio.setText(fetchedBio);
                    phoneNumber.setText(fetchedPhoneNumber);
                    Gender.setText(fetchedGender);
                    skillsLabel.setText(fetchedSkills);

                    // Use Picasso or another library to load the image into ImageView
                    Picasso.get().load(fetchedProfileImageUrl).into(profilePic);

                } else {
                    Log.d("TAG", "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "There was an error!");
            }
        });


        skillsRecyclerView = findViewById(R.id.skillsRecyclerView);
        skillsList = new ArrayList<>();
        skillsAdapter = new SkillsAdapter(skillsList);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        skillsRecyclerView.setLayoutManager(layoutManager);
        skillsRecyclerView.setAdapter(skillsAdapter);






    }
}