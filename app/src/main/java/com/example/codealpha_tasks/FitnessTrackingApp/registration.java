package com.example.codealpha_tasks.FitnessTrackingApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codealpha_tasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    Uri imageUri;
    private TextInputEditText nme,mail,paswrd,hegth,wei;
    ImageView profileRegister,addimage;
    Button register;
    TextView login;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nme=findViewById(R.id.nameField_register);
        mail=findViewById(R.id.emailField_register);
        paswrd=findViewById(R.id.passwordField_register);
        hegth=findViewById(R.id.heightField_register);
        wei=findViewById(R.id.weightField_register);
        profileRegister=findViewById(R.id.profile_register);
        profileRegister.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addimage=findViewById(R.id.add_image);
        register=findViewById(R.id.registerbtn);
        login=findViewById(R.id.loginText);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registration.this, Login.class));
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputFields()){
                    signupWithEmail();
                    //resetInputFields();
                   // Toast.makeText(registration.this, "Registration successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private boolean validateInputFields() {
        String email = mail.getText().toString();
        String password = paswrd.getText().toString();
        String name = nme.getText().toString();
        String height =hegth.getText().toString();
        String weight = wei.getText().toString();
        Log.d("UserData", "Name: " + name + ", Email: " + email);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(height)) {
            Toast.makeText(this, "Enter height!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(weight)) {
            Toast.makeText(this, "Enter weight!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Select a profile picture!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    public void signupWithEmail(){
        String email = mail.getText().toString();
        String password = paswrd.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadImage();

                        } else {
                            Toast.makeText(registration.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void moveToHomeScreen(){
        Intent intent = new Intent(registration.this, MainActivityFitness.class);
        startActivity(intent);
        finish();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }
    private void upload_data_to_firebase(String imageURL){
        String email = mail.getText().toString();
        String password = paswrd.getText().toString();
        String name = nme.getText().toString();
        String height = hegth.getText().toString();
        String weight = wei.getText().toString();

        Map<String,Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);
        user.put("height", Double.parseDouble(height));
        user.put("weight", Double.parseDouble(weight));
        user.put("ImageUri", imageURL);
        user.put("water", 0);
        user.put("calories", "0");
        user.put("distance", "0");
        user.put("steps", 0);
        user.put("totalHours", "0");

        db.collection("users")
                .add(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileRegister.setImageURI(imageUri);
            Log.d("UIR","ImageUri "+imageUri);
        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }



    private void uploadImage() {
        String fileName = nme.getText().toString().trim();

        storageReference = FirebaseStorage.getInstance().getReference("profiles/" + fileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                upload_data_to_firebase(imageURL);
                                moveToHomeScreen();

                            }
                        });
                        Toast.makeText(registration.this, "Account created!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(registration.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void resetInputFields() {
//        mail.setText("");
//        paswrd.setText("");
//        nme.setText("");
//        hegth.setText("");
//        wei.setText("");
//
//        profileRegister.setImageResource(R.drawable.profile); // Set to a default image
//        //imageUri = null; // Reset the image URI if necessary
//    }


}