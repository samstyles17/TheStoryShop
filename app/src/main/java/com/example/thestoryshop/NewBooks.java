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

import java.util.ArrayList;


public class NewBooks extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<NewBookClass> newBookDataList;
    private NewBookAdapter newBookAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_new_books, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("books");
        recyclerView = rootView.findViewById(R.id.newbooksrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newBookDataList = new ArrayList<NewBookClass>();
        newBookAdapter = new NewBookAdapter(newBookDataList,getContext());

        recyclerView.setAdapter(newBookAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newBookDataList.clear(); // Clear the existing data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewBookClass book = snapshot.getValue(NewBookClass.class);
                    newBookDataList.add(book);
                }

                newBookAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });

        return rootView;
    }
}