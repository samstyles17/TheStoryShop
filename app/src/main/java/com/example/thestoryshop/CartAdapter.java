package com.example.thestoryshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>
{
    private ArrayList<CartClass> cartDataList;
    private Context context;
    private DatabaseReference cartReference;

    public CartAdapter(ArrayList<CartClass> cartDataList, Context context, DatabaseReference cartReference) {
        this.cartDataList = cartDataList;
        this.context = context;
        this.cartReference = cartReference;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartbooksitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position)
    {
        int cartPosition = holder.getAdapterPosition();
        CartClass cartItem =cartDataList.get(position);
        holder.cartTitle.setText(cartItem.getTitle());
        holder.cartPrice.setText("Price: Rs." + cartItem.getPrice());
        holder.cartQuantity.setText("Quantity: " + cartItem.getQuantity());
        Glide.with(context).load(cartItem.getImageUrl()).into(holder.cartImage);

        holder.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDataList.remove(position);
                cartReference.child(cartItem.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(context,"Item deletion failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    private int calculateTotalPrice() {
        int totalPrice = 0;
        for (CartClass cartItem : cartDataList) {
            totalPrice += cartItem.getPrice() * cartItem.getQuantity();
        }
        return totalPrice;
    }

    // Display the total price in an AlertDialog
    private void showTotalPriceAlertDialog(int totalPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Total Cart Price");
        builder.setMessage("Total Price: Rs. " + totalPrice);
        builder.setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return cartDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView cartImage;
        TextView cartTitle,cartPrice,cartQuantity;
        FloatingActionButton fabDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImage = itemView.findViewById(R.id.cartImage);
            cartTitle = itemView.findViewById(R.id.cartbookTitle);
            cartPrice = itemView.findViewById(R.id.cartbookPrice);
            cartQuantity = itemView.findViewById(R.id.cartbookQuantity);
            fabDelete = itemView.findViewById(R.id.fabDelete);
        }

    }
}
