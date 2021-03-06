package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import net.cachapa.expandablelayout.ExpandableLayout;

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

        // ???????????????? instance ?????????????????????????????????? ????????????????????????
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // ???????????????? ???????? ?? ???????????????????????? ?????????? ?????????? ?????????????? ?? ?????????????????? ?????? ??????????????
            uid = user.getUid();
        }else{
            Log.d("photoUser", "user is null");
        }
        // ???????????????? ???????????? ???? ????
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadFragment(OrdersFragment.newInstance(mDatabase));
    }

    // ?????????????????? ?????? ?????????????? ???? BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_orders:
                    loadFragment(OrdersFragment.newInstance(mDatabase));
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


    // ?????????????????? ?????????????????? ?? FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }
    // ?????????? ?????? ?????????????????? ?????????????? ?????????????????? ?? ?????????????????? ?????? ??????????????
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    protected void onRestart() {
        //getOrders();
        super.onRestart();
    }
}