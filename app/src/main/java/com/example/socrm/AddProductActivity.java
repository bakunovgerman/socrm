package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socrm.data.Product;
import com.example.socrm.db.RealTimeDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private SwitchMaterial switchMaterial;
    private TextInputLayout nameTextInputLayout, priceTextInputLayout,saleTextInputLayout;
    private ImageView productImageView;
    // Объект подключения БД
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseUser user;
    // REQUEST CODE
    private final int PHOTO_REQUEST = 1;
    private Uri uri;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FrameLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        nameTextInputLayout = findViewById(R.id.editTextNameProduct);
        priceTextInputLayout = findViewById(R.id.editTextPriceProduct);
        saleTextInputLayout = findViewById(R.id.editTextSaleProduct);
        productImageView = findViewById(R.id.toolbarImage);
        floatingActionButton = findViewById(R.id.fab_add_product);
        progressbar = findViewById(R.id.fl_content);

        uri = null;
        // установка toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_product);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
        // кнопка назад метод
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // подключения БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // подключение аутентификации
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // подключаемся к облачному хранилищу
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        floatingActionButton.setColorFilter(Color.argb(255, 255, 255, 255));
        switchMaterial = findViewById(R.id.switchMaterial);

        // слушатель на switchMaterial
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    saleTextInputLayout.setEnabled(true);
                } else {

                    saleTextInputLayout.setEnabled(false);
                }
            }
        });

    }
    // метод выбора изображения
    public void chooserImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
        //setResult(RESULT_OK);
    }
    // получаем результат выбора изображения
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createProductOnClick(View view) {
        if (!nameTextInputLayout.getEditText().getText().toString().isEmpty()){
            nameTextInputLayout.setError(null);
            if (!priceTextInputLayout.getEditText().getText().toString().isEmpty()){
                priceTextInputLayout.setError(null);
                if (switchMaterial.isChecked() && saleTextInputLayout.getEditText().getText().toString().isEmpty()
                        || saleTextInputLayout.getEditText().getText().toString().equals("0")){
                    switchMaterial.setChecked(false);
                }

                // Получаем id пользователя
                uid = user.getUid();
                String key = mDatabase.child("products").push().getKey();
                if (user != null) {
                    if (uri != null){
                        progressbar.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        Uri file = Uri.fromFile(new File(uri.getPath()));
                        String[] extensionFile = file.getLastPathSegment().split("\\.");
                        StorageReference productImgRef = storageRef.child("images/"+uid+"/product_img_"+key+"." + extensionFile[1]);
                        UploadTask uploadTask = productImgRef.putFile(uri);
                        // отслеживание загрузки
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.d("storage", "fail");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("storage", "success");
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Product product = createProductObject(uri.toString());
                                        mDatabase.child("products").child(uid).child(key).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        Product product = createProductObject("default");
                        mDatabase.child("products").child(uid).child(key).setValue(product);
                    }
                }
                else {
                    Toast.makeText(this, "Пользователь не идентифицирован",Toast.LENGTH_LONG).show();
                }
            }
            else{
                priceTextInputLayout.setError("Введите цену товара");
            }
        }
        else {
            nameTextInputLayout.setError("Введите наименование товара");
            if (priceTextInputLayout.getEditText().getText().toString().isEmpty()){
                priceTextInputLayout.setError("Введите цену товара");
            }
        }

    }

    Product createProductObject(String uriImg){
        if (switchMaterial.isChecked()){
            return new Product(nameTextInputLayout.getEditText().getText().toString().trim(),
                    Integer.parseInt(priceTextInputLayout.getEditText().getText().toString().trim()),
                    Float.parseFloat(saleTextInputLayout.getEditText().getText().toString().trim()), uriImg);
        }
        else{
            return new Product(nameTextInputLayout.getEditText().getText().toString().trim(),
                    Integer.parseInt(priceTextInputLayout.getEditText().getText().toString().trim()),0, uriImg);
        }
    }
}