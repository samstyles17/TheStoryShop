package com.example.thestoryshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;

public class WishlistFragment extends Fragment {

    private RecyclerView recylerWishList;
    private ArrayList<WishlistClass> wishDataList;
    private WishlistAdapter wishlistAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wishlist, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("wishlist");
        recylerWishList = rootView.findViewById(R.id.wishlistrecyclerview);
        recylerWishList.setHasFixedSize(true);
        recylerWishList.setLayoutManager(new LinearLayoutManager(getContext()));
        wishDataList = new ArrayList<WishlistClass>();
        wishlistAdapter = new WishlistAdapter(wishDataList,getContext());
        recylerWishList.setAdapter(wishlistAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishDataList.clear(); // Clear the existing data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WishlistClass wish = snapshot.getValue(WishlistClass.class);
                    wishDataList.add(wish);
                }

                wishlistAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
        return rootView;
    }
}