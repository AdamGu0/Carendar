package com.group15.apps.carendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Neo on 3/18/17.
 */

public class AccountFragment extends Fragment implements View.OnClickListener, ValueEventListener {

    private EditText etEmail, etUsername, etDescription;
    private RadioButton radioMale, radioFemale, radioNone;
    private Button btnUpdate, btnPassword;

    private DatabaseReference mRef;

    private String mEmail;
    private int mGender; //male:1 female:2 not provided:0


    public static AccountFragment newInstance() {
        return new AccountFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        etEmail = (EditText) v.findViewById(R.id.et_email);
        etEmail.setEnabled(false);
        etUsername = (EditText) v.findViewById(R.id.et_username);
        etDescription = (EditText) v.findViewById(R.id.et_description);
        radioMale = (RadioButton) v.findViewById(R.id.radio_male);
        radioFemale = (RadioButton) v.findViewById(R.id.radio_female);
        radioNone = (RadioButton) v.findViewById(R.id.radio_none);
        btnUpdate = (Button) v.findViewById(R.id.btn_updateProfile);
        btnPassword = (Button) v.findViewById(R.id.btn_password);

        radioMale.setOnClickListener(this);
        radioFemale.setOnClickListener(this);
        radioNone.setOnClickListener(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClick();
            }
        });
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPasswordClick();
            }
        });

        mEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etEmail.setText(mEmail);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference("users/"+id+"/info");
        mRef.addValueEventListener(this);
        setGender(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setGender(int gender) {
        if (mGender == gender) return;
        mGender = gender;
        switch (mGender) {
            case 0:
                radioMale.setChecked(false);
                radioFemale.setChecked(false);
                radioNone.setChecked(true);
                break;
            case 1:
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
                radioNone.setChecked(false);
                break;
            case 2:
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
                radioNone.setChecked(false);
                break;
        }
    }

    private void resetEmail() {
        String email = etEmail.getText().toString();
        if (email.equals(mEmail)) //email changed
            return;
        if ( !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            Toast.makeText(getContext(), "Invalid e-mail address, please check your input.", Toast.LENGTH_LONG);
            etEmail.setText(mEmail);
            return;


        }

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Map info = (Map) dataSnapshot.getValue();
        if (info == null) return;

        String n = (String) info.get("username");
        if(n != null) etUsername.setText(n);

        Long g = (Long) info.get("gender");
        if (g != null) setGender(g.intValue());

        String d = (String) info.get("description");
        if(d != null) etDescription.setText(d);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {
        onGenderClick(v);
    }

    private void onGenderClick(View v) {
        int g = Integer.parseInt((String)v.getTag());
        setGender(g);
    }

    private void onUpdateClick() {
        //resetEmail();

        mRef.child("username").setValue(etUsername.getText().toString());
        mRef.child("gender").setValue(mGender);
        mRef.child("description").setValue(etDescription.getText().toString());
        Toast.makeText(getContext(), "Information updated.", Toast.LENGTH_SHORT).show();
    }

    private void onPasswordClick() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "A link to reset your password has been sent to your e-mail address.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to send password reset email, please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
