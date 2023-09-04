package com.example.thestoryshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private TextView name;
    private TextView phone;
    FirebaseFirestore db;
    private String overallExperience;
    private float navigationRating;
    private int featureSatisfaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_feedback, container, false);

        name = rootView.findViewById(R.id.feedbackName);
        phone = rootView.findViewById(R.id.feedbackPhone);
        RadioGroup overallExperienceRadioGroup = rootView.findViewById(R.id.overallexperienceRadioGroup);
        RatingBar navigationRatingBar = rootView.findViewById(R.id.ratingBar);
        SeekBar featureSatisfactionSeekBar = rootView.findViewById(R.id.seekBar);
        Button submitButton = rootView.findViewById(R.id.submitButton);
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
                        String Fphone = userSnapshot.getString("Phno");

                        name.setText(Fname);
                        phone.setText(Fphone);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = name.getText().toString().trim();
                String userphone = phone.getText().toString().trim();
                int selectedExperienceId = overallExperienceRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedExperienceButton = rootView.findViewById(selectedExperienceId);
                overallExperience = selectedExperienceButton.getText().toString();

                navigationRating = navigationRatingBar.getRating();
                featureSatisfaction = featureSatisfactionSeekBar.getProgress();

                db.collection("feedback").whereEqualTo("Phone",userPhone).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty())
                        {
                            DocumentSnapshot userProfileDoc = queryDocumentSnapshots.getDocuments().get(0);
                            String docId = userProfileDoc.getId();
                            updateFeedbackDocument(docId,username,userphone,overallExperience,navigationRating,featureSatisfaction);
                        }
                        else
                        {
                            saveFeedbackDocument(username,userphone,overallExperience,navigationRating,featureSatisfaction);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        return rootView;


    }

    private void updateFeedbackDocument(String docId,String username, String userphone,String overallExperience,float navigationRating,int featureSatisfaction)
    {
        Map<String, Object> userFeedbackUpdates = new HashMap<>();
        userFeedbackUpdates.put("Name",username);
        userFeedbackUpdates.put("Phone",userphone);
        userFeedbackUpdates.put("Overall Experience",overallExperience);
        userFeedbackUpdates.put("Navigation Rating",navigationRating);
        userFeedbackUpdates.put("Feature Satisfaction", featureSatisfaction);

        db.collection("feedback").document(docId).update(userFeedbackUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(requireContext(), "Feedback Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Feedback Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFeedbackDocument(String username, String userphone,String overallExperience,float navigationRating,int featureSatisfaction)
    {
        Map<String, Object> userFeedback = new HashMap<>();
        userFeedback.put("Name",username);
        userFeedback.put("Phone",userphone);
        userFeedback.put("Overall Experience",overallExperience);
        userFeedback.put("Navigation Rating",navigationRating);
        userFeedback.put("Feature Satisfaction", featureSatisfaction);

        db.collection("feedback").add(userFeedback).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireContext(), "Feedback added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Feedback adding failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}