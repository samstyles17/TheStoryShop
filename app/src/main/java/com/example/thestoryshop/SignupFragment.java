package com.example.thestoryshop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText name;
    private EditText email;
    private EditText phno;
    FirebaseFirestore db;
    private Button signup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);
    }

    public void initElements(View view)
    {
        db = FirebaseFirestore.getInstance();
        name = view.findViewById(R.id.txtName);
        email = view.findViewById(R.id.txtEmail);
        phno = view.findViewById(R.id.txtPhone);

        signup = view.findViewById(R.id.btnSignup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Phno = phno.getText().toString().trim();

                // Inside signup.setOnClickListener method
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userPhone", Phno);
                editor.putBoolean("isLoggedIn",true);
                editor.apply();


                if(isValidInput(Name,Email,Phno))
                {



                    db.collection("user").whereEqualTo("Phno",Phno).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty())
                            {
                                // Phone number already exists, show AlertDialog
                                showPhoneNumberExistsDialog();
                            }
                            else
                            {
                                Map<String, Object> user = new HashMap<>();
                                user.put("Name", Name);
                                user.put("Email", Email);
                                user.put("Phno", Phno);

                                db.collection("user").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(requireContext(), "Signup Successful", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(requireContext(), HomeActivity.class);
                                        startActivity(i);
                                        requireActivity().finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireContext(), "Signup Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }


        });
    }

    private boolean isValidInput(String Name, String Email, String Phno) {
        if (Name.isEmpty() || !Name.matches("^[a-zA-Z ]+$")) {
            name.setError("Name is required");
            name.requestFocus();
            return false;
        } else if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Valid email is required");
            email.requestFocus();
            return false;
        } else if (Phno.length() != 10) {
            phno.setError("Valid 10-digit phone number is required");
            phno.requestFocus();
            return false;
        }
        return true;
    }

    private void showPhoneNumberExistsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Phone Number Exists")
                .setMessage("The phone number is already used for another account.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}