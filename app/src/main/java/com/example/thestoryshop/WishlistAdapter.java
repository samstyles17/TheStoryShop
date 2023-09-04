package com.example.thestoryshop;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder>
{
    private ArrayList<WishlistClass> wishDataList;
    private Context context;

    public WishlistAdapter(ArrayList<WishlistClass> wishDataList, Context context) {
        this.wishDataList = wishDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public WishlistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlistbooksitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.MyViewHolder holder, int position) {
        WishlistClass wish = wishDataList.get(position);
        Glide.with(context).load(wishDataList.get(position).getImageURL()).into(holder.wishImage);
        holder.title.setText(wish.getTitle());
        holder.price.setText("Price: Rs." + wish.getPrice());

        holder.cartToWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Quantity");

                NumberPicker numberPicker = new NumberPicker(context);
                numberPicker.setMinValue(1); // Minimum quantity
                numberPicker.setMaxValue(10); // Maximum quantity

                builder.setView(numberPicker);
                builder.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedQuantity = numberPicker.getValue();

                        // Set the selected quantity to the book item
                        wish.setQuantity(selectedQuantity);

                        // Get the reference to the "cart" collection in the database
                        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("cart");
                        String cartItemId = cartReference.push().getKey();
                        // Create a new HashMap to hold the cart item data
                        HashMap<String, Object> cartItemData = new HashMap<>();
                        cartItemData.put("title", wish.getTitle());
                        cartItemData.put("imageUrl", wish.getImageURL());
                        cartItemData.put("price", wish.getPrice());
                        cartItemData.put("quantity", wish.getQuantity());

                        cartReference.child(cartItemId).setValue(cartItemData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Added to cart: " + selectedQuantity + " x " + wish.getTitle(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
    }

    @Override
    public int getItemCount() {
        return wishDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView wishImage;
        TextView title, price;
        FloatingActionButton cartToWish;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            wishImage = itemView.findViewById(R.id.wishImage);
            title = itemView.findViewById(R.id.wishbookTitle);
            price = itemView.findViewById(R.id.wishbookPrice);
            cartToWish = itemView.findViewById(R.id.fabWishToCart);
        }
    }
}
