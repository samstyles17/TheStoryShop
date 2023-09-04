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

public class NewBookAdapter extends RecyclerView.Adapter<NewBookAdapter.MyViewHolder>
{
    private ArrayList<NewBookClass> newBookDataList;
    private Context context;

    public NewBookAdapter(ArrayList<NewBookClass> newBookDataList, Context context) {
        this.newBookDataList = newBookDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewBookAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.newbooksitem,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewBookAdapter.MyViewHolder holder, int position) {

        NewBookClass newBook = newBookDataList.get(position);
        Glide.with(context).load(newBookDataList.get(position).getImageURL()).into(holder.newbookrecyclerImage);
        holder.title.setText(newBook.getTitle());
        holder.price.setText("Price: Rs."+newBook.getPrice());

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show an AlertDialog with a number picker
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Quantity");

                NumberPicker numberPicker = new NumberPicker(context);
                numberPicker.setMinValue(1); // Minimum quantity
                numberPicker.setMaxValue(10); // Maximum quantity

                builder.setView(numberPicker);

                builder.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedQuantity = numberPicker.getValue();

                        // Set the selected quantity to the book item
                        newBook.setQuantity(selectedQuantity);

                        // Get the reference to the "cart" collection in the database
                        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("cart");
                        String cartItemId = cartReference.push().getKey();
                        // Create a new HashMap to hold the cart item data
                        HashMap<String, Object> cartItemData = new HashMap<>();
                        cartItemData.put("title", newBook.getTitle());
                        cartItemData.put("imageUrl", newBook.getImageURL());
                        cartItemData.put("price", newBook.getPrice());
                        cartItemData.put("quantity", newBook.getQuantity());

                        cartReference.child(cartItemId).setValue(cartItemData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Added to cart: " + selectedQuantity + " x " + newBook.getTitle(), Toast.LENGTH_SHORT).show();
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


        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference wishlistReference = FirebaseDatabase.getInstance().getReference().child("wishlist");
                String wishlistItemId = wishlistReference.push().getKey();
                HashMap<String, Object> wishlistItemData = new HashMap<>();
                wishlistItemData.put("title",newBook.getTitle());
                wishlistItemData.put("imageURL",newBook.getImageURL());
                wishlistItemData.put("price",newBook.getPrice());

                wishlistReference.child(wishlistItemId).setValue(wishlistItemData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context,"Added to Wishlist",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context,"Failed to add to wishlist",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return newBookDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView newbookrecyclerImage;
        TextView title,price;
        FloatingActionButton cart,wishlist;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newbookrecyclerImage = itemView.findViewById(R.id.recyclerImage);
            title = itemView.findViewById(R.id.titleBook);
            price = itemView.findViewById(R.id.priceBook);
            cart = itemView.findViewById(R.id.fabCart);
            wishlist = itemView.findViewById(R.id.fabWishlist);
        }
    }
}
