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
import com.squareup.picasso.Picasso;

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
    private DatabaseReference mDatabase;
    private String uid;
    private FirebaseStorage storage;
    private StorageReference rootRef;

    public ProfileFragment() {
        // Required empty public constructor
    }
    public ProfileFragment(DatabaseReference mDatabase, String uid) {
        this.mDatabase = mDatabase;
        this.uid = uid;
    }
    public static ProfileFragment newInstance(DatabaseReference mDatabase, String uid) {
        return new ProfileFragment(mDatabase, uid);
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
        // получаем данные о профиле пользователя
        getProfile();
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
        // клик-метод выхода из аккаунта
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        // клик-метод включения редактирования профиля пользователя
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.GONE);
                emailTextInputLayout.setEnabled(true);
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
            }
        });
        // клик-метод отмены редактирования профиля пользователя
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChange();
            }
        });
        // клик-метод сохранения редактирования профиля пользователя
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
    // Метод получения данные о профиле пользователя
    public void getProfile(){
        // указание ссылок для storage firebase
        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();
        // подключение к объекту для магазинов
        mDatabase.child("shops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // получаем объект класса магазина
                Shop shop = snapshot.child(uid).getValue(Shop.class);
                shopName = shop.getShop_name();
                // забираем ссылку на аватарку из облака чтобы потом вывести аватарку в фрагменте профиля
                rootRef.child("images/" + uid + "/avatar_shop." + shop.getExtensionAvatar()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uriAvatar = uri;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        // строка для ссылки на форму у магазина
                        linkForm = "https://socrm-online.ru/insert.php?id=" + uid;

                        linkTextView.setText(linkForm);
                        shopNameTextView.setText(shopName);
                        emailTextInputLayout.getEditText().setText(email);
                        Picasso.get()
                                .load(uriAvatar)
                                .placeholder(R.drawable.default_image_product)
                                .error(R.drawable.default_image_product)
                                .fit()
                                .centerCrop()
                                .into(circleImageView);
                        frameLayout.setVisibility(View.GONE);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // отслеживание обновления данных о заказах в БД
//            mDatabase.child("orders").child(uid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    orders.clear();
//                    for (DataSnapshot orderSnapshot : snapshot.getChildren()){
//                        Order order = orderSnapshot.getValue(Order.class);
//                        order.id = orderSnapshot.getKey();
//                        orders.add(order);
//                    }
//                    ordersOnDataChangeComplete = true;
//                    if (ordersOnDataChangeComplete && avatarDownloadComplete){
//                        navigation.setVisibility(View.VISIBLE);
//                        loadFragment(OrdersFragment.newInstance(orders));
//                        progressBar.setVisibility(View.INVISIBLE);
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    getOrders();
//                }
//            });


    }
}