package com.example.codealpha_tasks.FitnessTrackingApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codealpha_tasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class AccountFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser currentUser;
    String email, name, url;
    Double height, weight;
    Button logout;
    ImageView profileimage;

    private TextView weigt,heit,mail_txt,waterconter,nametxt,steps,distance,sleeptimer,colories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileimage=view.findViewById(R.id.profilePicture_img);
        nametxt=view.findViewById(R.id.nameTextView);
        weigt=view.findViewById(R.id.weightTextView);
        heit=view.findViewById(R.id.heightTextView);
        mail_txt=view.findViewById(R.id.emailTextView);


        if (currentUser != null) {
            email = currentUser.getEmail();
            fetchData();
        } else {
            Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
        }
//        binding.edit.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), editScreen.class);
//            intent.putExtra("name", name);
//            intent.putExtra("email", email);
//            intent.putExtra("url", url);
//            intent.putExtra("height", height);
//            intent.putExtra("weight", weight);
//            startActivity(intent);
//        });

        view.findViewById(R.id.logout).setOnClickListener(v -> {
            signOut();
        });
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), Login.class);

        Toast.makeText(getContext(), "Logged Out!", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        getActivity().finish();
    }

    private void fetchData(){
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                name = document.getString("name");
                                url = document.getString("ImageUri");
                                height = document.getDouble("height");
                                weight = document.getDouble("weight");

                                nametxt.setText(name);
                                mail_txt.setText(email);
                                heit.setText(height + " feets");
                                weigt.setText(weight + " kg");

                                loadImage(url);
                            }
                        }
                    }
                });
    }

    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile) // Placeholder image while loading
                .error(R.drawable.profile) // Image to show if loading fails
                .into(profileimage);
    }

    }