package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socrm.data.Order;
import com.example.socrm.data.Product;
import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
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
    private ArrayList<Product> products;
    private boolean avatarDownloadComplete, ordersOnDataChangeComplete, productsOnDataChangeComplete = false;
    private BottomNavigationView navigation;
    private String uid;
    private LinearLayout linearLayout;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference rootRef;
    private int fragmentSavedInstanceState;
    //private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account_bottom_navigation);

        progressBar = findViewById(R.id.progressBar);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        contextOfApplication = getApplicationContext();
        linearLayout = findViewById(R.id.linearLayoutPersonal);
        //swipeRefreshLayout = findViewById(R.id.swipe);

        orders = new ArrayList<>();
        products = new ArrayList<>();
        fragmentSavedInstanceState = 0;
        // Получаем instance авторизированного пользователя
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // получаем инфу о пользователе чтобы потом вывести в фрагменте для профиля
            uid = user.getUid();
            email = user.getEmail();
        }else{
            Log.d("photoUser", "user is null");
        }
        // указание ссылок для storage firebase
        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();

        // Получаем ссылку на БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getOrders();
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
                    loadFragment(ProductsFragment.newInstance(mDatabase, uid));
                    fragmentSavedInstanceState = 2;
                    return true;
                case R.id.navigation_profile:
                    loadFragment(ProfileFragment.newInstance(mDatabase, uid));
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragment", fragmentSavedInstanceState);
        Toast.makeText(getApplicationContext(), String.valueOf(fragmentSavedInstanceState), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        fragmentSavedInstanceState = savedInstanceState.getInt("fragment");
        switch (fragmentSavedInstanceState){
            case 2:
                loadFragment(ProductsFragment.newInstance(mDatabase, uid));
                break;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    // подгрузка фрагмента в FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }
    // метод для получения инстанс контсекта в фрагменте для профиля
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    public void getOrders(){
        mDatabase.child("orders").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    orders.clear();
                    for (DataSnapshot orderSnapshot : task.getResult().getChildren()){
                        Order order = orderSnapshot.getValue(Order.class);
                        order.id = orderSnapshot.getKey();
                        orders.add(order);
                    }
                    ordersOnDataChangeComplete = true;
                    navigation.setVisibility(View.VISIBLE);
                    loadFragment(OrdersFragment.newInstance(orders));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    @Override
    protected void onRestart() {
        //getOrders();
        super.onRestart();
    }
}