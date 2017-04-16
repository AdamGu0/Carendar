package com.group15.apps.carendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Neo on 3/18/17.
 */

public class AccountFragment extends Fragment implements View.OnClickListener {

    private EditText etEmail, etPassword, etConfirm, etUsername, etDescription;
    private RadioButton radioMale, radioFemale, radioNone;
    private Button btnUpdate;
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
        etPassword = (EditText) v.findViewById(R.id.et_password);
        etConfirm = (EditText) v.findViewById(R.id.et_confirm);
        etUsername = (EditText) v.findViewById(R.id.et_username);
        etDescription = (EditText) v.findViewById(R.id.et_description);
        radioMale = (RadioButton) v.findViewById(R.id.radio_male);
        radioFemale = (RadioButton) v.findViewById(R.id.radio_female);
        radioNone = (RadioButton) v.findViewById(R.id.radio_none);
        btnUpdate = (Button) v.findViewById(R.id.btn_updateProfile);

        radioMale.setOnClickListener(this);
        radioFemale.setOnClickListener(this);
        radioNone.setOnClickListener(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClick(v);
            }
        });

        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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

    @Override
    public void onClick(View v) {
        onGenderClick(v);
    }

    private void onGenderClick(View v) {
        int g = Integer.parseInt((String)v.getTag());
        setGender(g);
    }

    private void onUpdateClick(View v) {

    }
}
