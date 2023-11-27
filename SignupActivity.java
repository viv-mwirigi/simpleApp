package com.example.linkedin;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.CollectionReference;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText userName, EmailAddress, Password, confirmPwd, Bio, phoneNumber;
    Spinner Gender;
    MultiAutoCompleteTextView Skills;
    Button signupBtn;
    private Spinner genderSpinner;
    private String selectedGender;
    private MultiAutoCompleteTextView skillsMultiAutoCompleteTextView;
    private String[] skills;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Uri imageUri;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText userName = findViewById(R.id.userName);
        EditText EmailAddress = findViewById(R.id.EmailAddress);
        EditText Password = findViewById(R.id.Password);
        EditText confirmPwd = findViewById(R.id.confirmPwd);
        EditText Bio = findViewById(R.id.Bio);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        Button signupBtn = findViewById(R.id.signupBtn);
        Spinner genderSpinner = findViewById(R.id.Gender);
        MultiAutoCompleteTextView skillsMultiAutoCompleteTextView = findViewById(R.id.Skills);
        ImageView profileImageView = findViewById(R.id.profilePic);


        if(imageUri !=null) {
            uploadImageToFirebaseStorage(imageUri);
        }
        else {
            Toast.makeText(SignupActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
        }

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        // Populate the gender spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options, // create an array in strings.xml with gender options
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // Set listener to capture selected gender
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedGender = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        skills = getResources().getStringArray(R.array.skills_array);
        ArrayAdapter<String> skillsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                skills
        );
        skillsMultiAutoCompleteTextView.setAdapter(skillsAdapter);
        skillsMultiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate inputs and save to Firebase
                String name = userName.getText().toString();
                String email = EmailAddress.getText().toString();
                String pwd = Password.getText().toString();
                String confirmPass = confirmPwd.getText().toString();
                String bio =Bio.getText().toString();
                String num = phoneNumber.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String skills = skillsMultiAutoCompleteTextView.getText().toString();
                Uri imageUri = getIntent().getParcelableExtra("IMAGE_URI");
                Picasso.get()
                        .load(imageUri)
                        .resize(300, 300)
                        .centerCrop() // Crop the image to fit the specified dimensions
                        .into(profileImageView);
                if(name.isEmpty()){
                    userName.setError("Enter username");
                    return;
                }
                if(email.isEmpty()){
                    EmailAddress.setError("Email required");
                    return;
                }
                if(pwd.isEmpty()){
                    Password.setError("Password required");
                    return;
                }
                if(confirmPass.isEmpty()){
                    confirmPwd.setError("Confirm password");
                    return;
                }
                if (!pwd.equals(confirmPass)) {
                    confirmPwd.setError("Password do not match");
                    return;
                }
                //data is validated



                //register the user using firebase


                Toast.makeText(SignupActivity.this, "Data Validated", Toast.LENGTH_SHORT).show();
                fAuth.createUserWithEmailAndPassword(email,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // For simplicity, assume inputs are valid and navigate directly
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("images");

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Firebase Storage reference

        String email = new String();
        StorageReference imageRef = storageRef.child("profile_images/" + email + "_profile.jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageURL = uri.toString();

                                // Once you have the imageURL, save other user data to Firestore
                                saveUserDataToFirestore(imageURL);
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to get image URL
                                Log.d("TAG", "URL not received");

                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to upload image
                    Log.d("TAG","Error uploading");
                });
    }

    private void saveUserDataToFirestore(String imageURL) {
        // Get other user data
        String name = userName.getText().toString();
        String email = EmailAddress.getText().toString();
        String bio = Bio.getText().toString();
        String num = phoneNumber.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String userSkills = skillsMultiAutoCompleteTextView.getText().toString();

        // Firebase firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference userRef = db.collection("Users").document(userId);
        DocumentReference docRef = db.collection("Users").document("987654321");




        // Create a User object with the collected data
        Map<String, Object> user = new HashMap<>();
        user.put("userName", name);
        user.put("email", email);

        user.put("bio",bio);
        user.put("phoneNumber",num);
        user.put("gender",gender);
        user.put("skills",userSkills);
        user.put("profileimageURL",imageURL);




        DocumentReference userDocumentRef = db.collection("Users").document("987654321");

        // Set the data for the specific document
        userDocumentRef.set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DocumentSnapshot added with ID: 987654321");
                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error adding document", e);
                    // Handle failure
                });



       // db.collection("Users").document(userId)
             //   .update("profileImageUrl", imageURL)
              //  .addOnSuccessListener(aVoid -> {
                    // Image URL updated in Firestore successfully
                    //Log.d("TAG", "Image URL updated successfully");
             //   })
               // .addOnFailureListener(e -> {
                    // Handle failure to update Firestore
                  //  Log.w("TAG", "Error updating image URL in Firestore", e);});



                    // Save the user data to the Firestore
       // db.collection("Users").add(user)
               // .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                //    @Override
                    //public void onSuccess(DocumentReference documentReference) {
                  //      Log.d("TAG","DocumentSnapshot added with ID:" + documentReference.getId());
                    //    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                       // startActivity(intent);
                    //    finish();
                 //   }



             //   })
             //   .addOnFailureListener(new OnFailureListener() {
              //      @Override
                  //  public void onFailure(@NonNull Exception e) {
                     //   Log.w("TAG", "There was error");

                    //}

               // });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imageUri = data.getData(); // Update the global imageUri variable
            uploadImageToFirebaseStorage(imageUri); // Trigger the image upload to Firebase
        }
    }



}
