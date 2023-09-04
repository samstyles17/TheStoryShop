package com.example.thestoryshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private String[] locations = {"SG Palya","Electronic City","Kormangala","White Field","Baiyappanhalli","Kalyan Nagar"};
    private Spinner locSpinner;
    private EditText name,email,ad1,ad2;
    private TextView phno;
    private Button updateBtn;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locSpinner = rootView.findViewById(R.id.spinner);
        locSpinner.setAdapter(adapter);

        name = rootView.findViewById(R.id.profileName);
        email = rootView.findViewById(R.id.profileEmail);
        phno = rootView.findViewById(R.id.profilePhone);
        ad1 = rootView.findViewById(R.id.profileAddressLine1);
        ad2 = rootView.findViewById(R.id.profileAddressLine2);
        updateBtn = rootView.findViewById(R.id.updateButton);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("userPhone", "");

        if(!userPhone.isEmpty())
        {
            db = FirebaseFirestore.getInstance();
            db.collection("user").whereEqualTo("Phno",userPhone).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty())
                    {
                        DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String Fname = userSnapshot.getString("Name");
                        String Femail = userSnapshot.getString("Email");
                        String Fphno = userSnapshot.getString("Phno");

                        name.setText(Fname);
                        email.setText(Femail);
                        phno.setText(Fphno);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPhone = phno.getText().toString();
                String addressLine1 = ad1.getText().toString();
                String addressLine2 = ad2.getText().toString();
                String selectedLocation = locSpinner.getSelectedItem().toString();

                db.collection("userprofile")
                        .whereEqualTo("Phno", userPhone)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Update the existing document
                                    DocumentSnapshot userProfileDoc = queryDocumentSnapshots.getDocuments().get(0);
                                    String docId = userProfileDoc.getId();
                                    updateProfileDocument(docId, userName, userEmail, addressLine1, addressLine2, selectedLocation);
                                }
                                else
                                {
                                    // Create a new document
                                    saveUserProfile(userPhone, userName, userEmail, addressLine1, addressLine2, selectedLocation);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                            }
                        });


            }
        });

        return rootView;

    }

    private void updateProfileDocument(String docId, String userName, String userEmail, String addressLine1, String addressLine2, String location) {
        Map<String, Object> userProfileUpdates = new HashMap<>();
        userProfileUpdates.put("Name", userName);
        userProfileUpdates.put("Email", userEmail);
        userProfileUpdates.put("AddressLine1", addressLine1);
        userProfileUpdates.put("AddressLine2", addressLine2);
        userProfileUpdates.put("Location", location);

        db.collection("userprofile").document(docId)
                .update(userProfileUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserProfile(String userPhone, String userName, String userEmail, String addressLine1, String addressLine2, String location)
    {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("Name", userName);
        userProfile.put("Email", userEmail);
        userProfile.put("Phno", userPhone);
        userProfile.put("AddressLine1", addressLine1);
        userProfile.put("AddressLine2", addressLine2);
        userProfile.put("Location", location);

        db.collection("userprofile").add(userProfile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireContext(), "Profile added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Profile adding failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}