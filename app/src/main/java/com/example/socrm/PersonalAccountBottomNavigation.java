package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socrm.data.Order;
import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PersonalAccountBottomNavigation extends AppCompatActivity {

    public static Context contextOfApplication;
    private DatabaseReference mDatabase;
    private String shopName, email, password, linkForm;
    private Uri uriAvatar;
    private ProgressBar progressBar;
    private ArrayList<Order> orders;
    private boolean avatarDownloadComplete, ordersOnDataChangeComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account_bottom_navigation);

        progressBar = findViewById(R.id.progressBar);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        contextOfApplication = getApplicationContext();

        orders = new ArrayList<>();
        // Получаем instance авторизированного пользователя
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // указание ссылок для storage firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();

        // Получаем ссылку на БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            // получаем инфу о пользователе чтобы потом вывести в фрагменте для профиля
            String uid = user.getUid();
            email = user.getEmail();
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
                            avatarDownloadComplete = true;
                            if (ordersOnDataChangeComplete && avatarDownloadComplete){
                                navigation.setVisibility(View.VISIBLE);
                                loadFragment(OrdersFragment.newInstance(orders));
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            // строка для ссылки на форму у магазина
            linkForm = "https://socrm-online.ru/insert.php?id=" + uid;

//            mDatabase.child("orders").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Toast.makeText(PersonalAccountBottomNavigation.this, "bad", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Order order = task.getResult().getValue(Order.class);
//                        orders.add(order);
//                    }
//                }
//            });
            mDatabase.child("orders").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orders.clear();
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()){
                        Order order = orderSnapshot.getValue(Order.class);
                        order.id = orderSnapshot.getKey();
                        orders.add(order);
                    }
                    ordersOnDataChangeComplete = true;
                    if (ordersOnDataChangeComplete && avatarDownloadComplete){
                        navigation.setVisibility(View.VISIBLE);
                        loadFragment(OrdersFragment.newInstance(orders));
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            Log.d("photoUser", "user is null");
        }
    }
    // слушатель для нажатий по BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_orders:
                    loadFragment(OrdersFragment.newInstance(orders));
                    return true;
                case R.id.navigation_products:
                    loadFragment(ProductsFragment.newInstance());
                    return true;
                case R.id.navigation_profile:
                    loadFragment(ProfileFragment.newInstance(shopName, uriAvatar, email, linkForm));
                    return true;
            }
            return false;
        }
    };
    // подгрузка фрагмента в FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }
    // метод для получения инстанс контсекта в фрагменте для профиля
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}