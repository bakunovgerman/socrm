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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;

public class PersonalAccountBottomNavigation extends AppCompatActivity {

    public static Context contextOfApplication;
    private DatabaseReference mDatabase;
    private BottomNavigationView navigation;
    private String uid;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account_bottom_navigation);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        contextOfApplication = getApplicationContext();

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen){
                            navigation.setVisibility(View.GONE);
                        }else{
                            navigation.setVisibility(View.VISIBLE);
                        }
                    }
                });

        // Получаем instance авторизированного пользователя
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // получаем инфу о пользователе чтобы потом вывести в фрагменте для профиля
            uid = user.getUid();
        }else{
            Log.d("photoUser", "user is null");
        }
        // Получаем ссылку на БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadFragment(OrdersFragment.newInstance(mDatabase, uid));
    }

    // слушатель для нажатий по BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_orders:
                    loadFragment(OrdersFragment.newInstance(mDatabase, uid));
                    return true;
                case R.id.navigation_products:
                    loadFragment(ProductsFragment.newInstance(mDatabase, uid));
                    return true;
                case R.id.navigation_profile:
                    loadFragment(ProfileFragment.newInstance(mDatabase, uid));
                    return true;
            }
            return false;
        }
    };


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





    @Override
    protected void onRestart() {
        //getOrders();
        super.onRestart();
    }
}