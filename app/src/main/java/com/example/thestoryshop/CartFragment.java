package com.example.thestoryshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private RecyclerView recyclerCart;
    private ArrayList<CartClass> cartDataList;
    private CartAdapter cartAdapter;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private TextView textTotalPrice;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_cart, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("cart");
        recyclerCart = rootView.findViewById(R.id.cartbooksrecyclerview);
        recyclerCart.setHasFixedSize(true);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));

        cartDataList = new ArrayList<CartClass>();
        cartAdapter = new CartAdapter(cartDataList,getContext(),databaseReference);
        recyclerCart.setAdapter(cartAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartClass cartItem = snapshot.getValue(CartClass.class);
                    cartItem.setKey(snapshot.getKey()); // Set the key of the cart item
                    cartDataList.add(cartItem);


                }
                // Calculate and display the total price
                int totalPrice = calculateTotalPrice();

                // Show the AlertDialog with the total price
                showTotalPriceAlertDialog(totalPrice);

// Notify the adapter of data changes
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }
    private int calculateTotalPrice() {
        int totalPrice = 0;
        for (CartClass cartItem : cartDataList) {
            totalPrice += cartItem.getPrice() * cartItem.getQuantity();
        }
        Log.d("CartActivity", "Total Price: " + totalPrice);
        return totalPrice;
    }

    private void showTotalPriceAlertDialog(int totalPrice) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Total Cart Price");
            builder.setMessage("Total Price: Rs. " + totalPrice);
            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


}