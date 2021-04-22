package com.example.socrm;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private TextView shopNameTextView, linkTextView;
    private String shopName, email, linkForm;
    private Uri uriAvatar;
    private ImageButton logoutBtn;
    private FrameLayout frameLayout;
    private TextInputLayout emailTextInputLayout;
    private MaterialButton editBtn, saveBtn, cancelBtn, copyBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String shopName, Uri uriAvatar, String email, String linkForm) {
        return new ProfileFragment(shopName, uriAvatar, email, linkForm);
    }

    public ProfileFragment(String shopName, Uri uriAvatar, String email, String linkForm) {
        this.shopName = shopName;
        this.uriAvatar = uriAvatar;
        this.email = email;
        this.linkForm = linkForm;
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
        copyBtn = v.findViewById(R.id.copyLink);
        logoutBtn = v.findViewById(R.id.logoutBtn);
        shopNameTextView = v.findViewById(R.id.shopNameTextView);
        frameLayout = v.findViewById(R.id.progressbar_layout);
        emailTextInputLayout = v.findViewById(R.id.editTextEmailAddress);
        saveBtn = v.findViewById(R.id.saveBtn);
        editBtn = v.findViewById(R.id.editBtn);
        cancelBtn = v.findViewById(R.id.cancelBtn);
        linkTextView = v.findViewById(R.id.linkTextView);

        linkTextView.setText(linkForm);
        shopNameTextView.setText(shopName);
        emailTextInputLayout.getEditText().setText(email);
        Glide.with(getContext()).load(uriAvatar).into(circleImageView);
        frameLayout.setVisibility(View.GONE);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", linkForm);
                clipboard.setPrimaryClip(clip);
                Snackbar snackbar = Snackbar.make(
                        v,
                        "Ссылка скопирована",
                        Snackbar.LENGTH_LONG
                );
                snackbar.show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.GONE);
                emailTextInputLayout.setEnabled(true);
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChange();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential("german.bakunov@gmail.com", "Gera2002");

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("checkChange", "User re-authenticated.");
                            }
                        });
                if (user != null){
                    if (!emailTextInputLayout.getEditText().getText().toString().equals(email)){
                        if(RegActivity.isEmailValid(emailTextInputLayout.getEditText().getText().toString())){
                            emailTextInputLayout.setError(null);
                            frameLayout.setVisibility(View.VISIBLE);
                            // обновление email
                            user.updateEmail(emailTextInputLayout.getEditText().getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("checkChange", "User email address updated.");
                                                cancelChangeSave();
                                            } else{
                                                Log.d("checkChange", "User email address updated FAIL");
                                            }
                                        }
                                    });
                        }
                        else{
                            emailTextInputLayout.setError("E-mail некорректный");
                        }
                    }
                } else{
                    Log.d("checkChange", "User pis null");
                }

            }
        });

        return v;
    }
    public void cancelChange(){
        editBtn.setVisibility(View.VISIBLE);
        emailTextInputLayout.setEnabled(false);
        cancelBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
    }
    public void cancelChangeSave(){
        startActivity(new Intent(getContext(), MainActivity.class));
    }
}