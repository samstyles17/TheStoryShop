package com.example.thestoryshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            // Load the HomeFragment as the default fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new NewBooks())
                    .commit();
        }

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);

                if(id == R.id.nav_trending)
                    repalaceFragmant(new NewBooks());
                else if (id == R.id.nav_cart)
                {
                   //Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_SHORT).show();
                    repalaceFragmant(new CartFragment());
                }
                else if (id == R.id.nav_wishlist)
                {
                    //Toast.makeText(getApplicationContext(),"Wishlist",Toast.LENGTH_SHORT).show();
                    repalaceFragmant(new WishlistFragment());
                }
                else if (id == R.id.nav_settings)
                {
                    repalaceFragmant(new ProfileFragment());
                    //Toast.makeText(getApplicationContext(),"Profile",Toast.LENGTH_SHORT).show();
                }

                else if (id == R.id.nav_feedback)
                {
                    repalaceFragmant(new FeedbackFragment());
                    //Toast.makeText(getApplicationContext(),"Profile",Toast.LENGTH_SHORT).show();
                }

                else if(id == R.id.nav_slogout)
                {

                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();
                    Intent i = new Intent(HomeActivity.this, AuthenticationActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    return true;
                }
                return true;
            }
        });
    }

    private void repalaceFragmant(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_option1) {
            // Handle the search action
            // For example, you can open a search activity or fragment.
            // Replace the following line with your desired action:
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}