package com.example.codealpha_tasks.FitnessTrackingApp;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.codealpha_tasks.R;

public class MainActivityFitness extends AppCompatActivity {

    LinearLayout home,profile,steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_fitness);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        home=findViewById(R.id.home);
        profile=findViewById(R.id.profile);
        steps=findViewById(R.id.steps);
        replaceFragments(new HomeFragment());

        home.setOnClickListener(v -> {
            home.setBackgroundResource(R.drawable.nav);
            steps.setBackgroundResource(0);
            profile.setBackgroundResource(0);
//            findViewById(R.id.homeImage).setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
//            stepsImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
//            profileImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

            replaceFragments(new HomeFragment());
        });

        steps.setOnClickListener(v -> {
            home.setBackgroundResource(0);
            profile.setBackgroundResource(0);
            steps.setBackgroundResource(R.drawable.nav);

//            homeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
//            stepsImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
//            profileImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));


            replaceFragments(new StepsFragment());
        });

        profile.setOnClickListener(v -> {
            home.setBackgroundResource(0);
            steps.setBackgroundResource(0);
            profile.setBackgroundResource(R.drawable.nav);

//            binding.homeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
//            binding.stepsImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
//            binding.profileImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));


            replaceFragments(new AccountFragment());
        });
    }

    public void replaceFragments(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}