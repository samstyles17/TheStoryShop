package com.example.thestoryshop;

import static android.content.Context.DOMAIN_VERIFICATION_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {

    private String verificationId;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private EditText phoneNo;
    private  EditText otp;
    private Button login;
    private TextView getotp;
    private  TextView otpsent;
    private TextView resendOTP;
    private boolean getotpclicked = false;
    private  TextView countdowntimer;
    Dialog dialog;
    FirebaseUser user;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initElements(view);

    }

    public void initElements(View view)
    {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        phoneNo = view.findViewById(R.id.txtPhone);
        otp = view.findViewById(R.id.txtotp);
        getotp = view.findViewById(R.id.get_otp);
        login = view.findViewById(R.id.btnLogin);
        resendOTP = view.findViewById(R.id.resend_otp);
        otpsent = view.findViewById(R.id.otp_sent);
        countdowntimer = view.findViewById(R.id.countdown_timer);

        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getotpOnclick();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnClick();
            }
        });
        login.setTextColor(Color.parseColor("#C0BEBE"));
    }

    public void loginOnClick()
    {
        String number = phoneNo.getText().toString().trim();
        String otp1 = otp.getText().toString().trim();

        if(number.length() == 10 && otp1.length() > 4)
        {
            if (number.length() == 10 && otp1.length() > 4)
            {
                db.collection("user").whereEqualTo("Phno",number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty())
                        {
                            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                            dialog = new Dialog(requireContext());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_wait);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            verifyCode(otp1);

                            // Inside loginOnClick method
                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userPhone", number);
                            editor.putBoolean("isLoggedIn",true);
                            editor.apply();


                        }
                        else
                        {
                            showPhoneNumberNotRegisteredDialog();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {


                    }
                });
            }
            else
            {
                if (number.isEmpty() || number.length() < 10) {
                    phoneNo.setError("Valid number is required");
                    phoneNo.requestFocus();
                } else if (otp1.isEmpty() || otp1.length() < 5) {
                    otp.setError("Valid OTP is required");
                    otp.requestFocus();
                }

            }
        }
    }

    private void showPhoneNumberNotRegisteredDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Phone Number Not Registered")
                .setMessage("The phone number is not registered. Please sign up to create an account.")
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

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                Intent i = new Intent(requireContext(), HomeActivity.class);
                                startActivity(i);
                                requireActivity().finish();
                            } else {
                                if (dialog != null) {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    public void getotpOnclick() {
        if (!getotpclicked) {
            String num = phoneNo.getText().toString().trim();

            if (num.length() != 10) {
                phoneNo.setError("Valid number is required");
                phoneNo.requestFocus();
            } else {
                getotpclicked = true;
                String phoneNumber = "+91" + num;
                sendVerificationCode(phoneNumber);
                getotp.setTextColor(Color.parseColor("#C0BEBE"));
                dialog = new Dialog(requireContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_wait);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(mCallBack)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            dialog.dismiss();
            login.setTextColor(Color.parseColor("#000000"));
            otpsent.setText("OTP has been sent");
            otpsent.setVisibility(View.VISIBLE);
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;

            countdowntimer.setVisibility(View.VISIBLE);

            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countdowntimer.setText(String.valueOf(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    resendOTP.setVisibility(View.VISIBLE);
                    countdowntimer.setVisibility(View.INVISIBLE);
                }
            }.start();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            getotpclicked = false;
            getotp.setTextColor(Color.parseColor("#0000FF"));
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };



}