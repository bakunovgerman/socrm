package com.example.socrm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView circleImageView;
    private TextView shopNameTextView;
    private String shopName, email;
    private Uri uriAvatar;
    private FrameLayout frameLayout;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout, passwordRepeatTextInputLayout;
    private MaterialButton editBtn, saveBtn, cancelBtn;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String shopName, Uri uriAvatar, String email) {
        return new ProfileFragment(shopName, uriAvatar, email);
    }

    public ProfileFragment(String shopName, Uri uriAvatar, String email) {
        this.shopName = shopName;
        this.uriAvatar = uriAvatar;
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageView = v.findViewById(R.id.userPhoto);
        shopNameTextView = v.findViewById(R.id.shopNameTextView);
        frameLayout = v.findViewById(R.id.progressbar_layout);
        emailTextInputLayout = v.findViewById(R.id.editTextEmailAddress);
        passwordTextInputLayout = v.findViewById(R.id.editTextTextPassword);
        passwordRepeatTextInputLayout = v.findViewById(R.id.editTextTextPasswordRepeat);
        saveBtn = v.findViewById(R.id.saveBtn);
        editBtn = v.findViewById(R.id.editBtn);
        cancelBtn = v.findViewById(R.id.cancelBtn);

        shopNameTextView.setText(shopName);
        emailTextInputLayout.getEditText().setText(email);
        Glide.with(getContext()).load(uriAvatar).into(circleImageView);
        frameLayout.setVisibility(View.GONE);



        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.GONE);
                emailTextInputLayout.setEnabled(true);
                passwordTextInputLayout.setEnabled(true);
                passwordTextInputLayout.getEditText().setText("");
                passwordTextInputLayout.setHint("Новый пароль");
                passwordRepeatTextInputLayout.setEnabled(true);
                passwordRepeatTextInputLayout.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.VISIBLE);
                emailTextInputLayout.setEnabled(false);
                passwordTextInputLayout.setEnabled(false);
                passwordRepeatTextInputLayout.setEnabled(false);
                passwordRepeatTextInputLayout.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                saveBtn.setVisibility(View.GONE);
                passwordTextInputLayout.setHint("Пароль");
                passwordTextInputLayout.getEditText().setText("ggggggg");
            }
        });

        return v;
    }

}