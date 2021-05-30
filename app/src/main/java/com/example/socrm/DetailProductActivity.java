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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socrm.data.Order;
import com.example.socrm.data.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetailProductActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private SwitchMaterial switchMaterial;
    private TextInputLayout nameTextInputLayout, priceTextInputLayout,saleTextInputLayout;
    private ImageView productImageView;
    private Product product;
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
        setContentView(R.layout.activity_detail_product);

        // создаем ссылку на БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // получаем залогиненного пользователя
        user = FirebaseAuth.getInstance().getCurrentUser();
        // инициализируем компоненты activity
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
        // красим в белый цвет иконку в floatingActionBar
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
        // устанавливаем тектовые значение EditText с информацией о товаре
        // получаем переданный intent из родительской activity
        Bundle arguments = getIntent().getExtras();
        // получаем объект order из intent
        product = arguments.getParcelable(Product.class.getSimpleName());

        nameTextInputLayout.getEditText().setText(product.getName());
        priceTextInputLayout.getEditText().setText(String.valueOf(product.getPrice()));
        if (product.getSale() != 0){
            switchMaterial.setChecked(true);
            saleTextInputLayout.setEnabled(true);
            saleTextInputLayout.getEditText().setText(String.valueOf((int)product.getSale()));
        }
        Picasso.get()
                .load(product.getUrl())
                .placeholder(R.drawable.default_image_product)
                .error(R.drawable.default_image_product)
                .fit()
                .centerCrop()
                .into(productImageView);

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
    // метод создания товара
    public void updateProductOnClick(View view) {
        // проверка валидации введенных данных о товаре
        if (!nameTextInputLayout.getEditText().getText().toString().isEmpty()){
            nameTextInputLayout.setError(null);
            if (!priceTextInputLayout.getEditText().getText().toString().isEmpty()){
                priceTextInputLayout.setError(null);
                // выключение переключателя скидки если он включен, а скидка не задана в числовом значении
                if (switchMaterial.isChecked() && saleTextInputLayout.getEditText().getText().toString().isEmpty()
                        || saleTextInputLayout.getEditText().getText().toString().equals("0")){
                    switchMaterial.setChecked(false);
                }
                // Получаем id пользователя
                uid = user.getUid();
                // Получаем key id для того что уникализировать каждый товар под свой id
                String key = mDatabase.child("products").push().getKey();
                if (user != null) {
                    if (uri != null){
                        /* включение видимости progressbar для ожидания процесса успешного добавления
                           товара в бд */
                        progressbar.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        Uri file = Uri.fromFile(new File(uri.getPath()));
                        // получаем расширение изображения
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
                                        Product productUpdate = createProductObject(uri.toString());
                                        Map<String, Object> productValues = productUpdate.toMap();
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("products/" + user.getUid() + "/" + product.getId(), productValues);
                                        mDatabase.updateChildren(childUpdates);
                                        finish();
                                    }
                                });
                            }
                        });
                    } else {
                        // создания товара с дефолтным изображением
                        Product productUpdate = createProductObject(product.getUrl());
                        Map<String, Object> productValues = productUpdate.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("products/" + user.getUid() + "/" + product.getId(), productValues);
                        mDatabase.updateChildren(childUpdates);
                        finish();
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
    // метод создания объекта класса Product в зависимости от наличия скидки на товар
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

    public void deleteProductOnClick(View view) {
        mDatabase.child("products").child(user.getUid()).child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}