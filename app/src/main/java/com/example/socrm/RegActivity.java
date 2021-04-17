package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegActivity extends AppCompatActivity {

    private TextInputLayout editTextEmail, editTextPassword, editTextRepeatPassword, editTextShopName;
    // Объект авторизации
    private FirebaseAuth mAuth;
    // Объект подключения БД
    private DatabaseReference mDatabase;
    // Объект для облачного хранилища
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private boolean isErrorEmail, isErrorPassword, isErrorRepeatPassword = false;
    // REQUEST CODE
    private final int PHOTO_REQUEST = 1;
    private Uri uri, uriAvatar;
    private CircleImageView avatarShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        // подключение аутентификации
        mAuth = FirebaseAuth.getInstance();
        // подключения БД
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // подключаемся к облачному хранилищу
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextRepeatPassword = findViewById(R.id.editTextTextPasswordRepeat);
        editTextShopName = findViewById(R.id.editTextTitleShop);
        avatarShop = findViewById(R.id.imageButtonAvatarShop);

        // Прослушиватель изменения текста
        editTextEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isCorrectStyleInput(editTextEmail, isErrorEmail);
            }
        });

        // Прослушиватель изменения текста
        editTextPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isCorrectStyleInput(editTextPassword, isErrorEmail);
            }
        });

        editTextRepeatPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextPassword.getEditText().getText().toString().equals(editTextRepeatPassword.getEditText().getText().toString())){
                    if (isErrorRepeatPassword) {
                        editTextRepeatPassword.setError(null);
                    }
                    editTextRepeatPassword.setEndIconDrawable(R.drawable.ic_correct);
                    editTextRepeatPassword.getEditText().setBackgroundResource(R.drawable.custom_btn_correct);
                }
                else{
                    editTextRepeatPassword.setEndIconDrawable(null);
                    editTextRepeatPassword.getEditText().setBackgroundResource(R.drawable.custom_btn);
                }
            }
        });

    }
    // Метод стилизации корректного инпута
    public void isCorrectStyleInput(TextInputLayout textInputEditText, boolean isError){

        if (!TextUtils.isEmpty(textInputEditText.getEditText().getText().toString())
                && isValidType(textInputEditText)){
            if (isError){
                textInputEditText.setError(null);
            }
            textInputEditText.setEndIconDrawable(R.drawable.ic_correct);
            textInputEditText.getEditText().setBackgroundResource(R.drawable.custom_btn_correct);
        }
        else {
            textInputEditText.setEndIconDrawable(null);
            textInputEditText.getEditText().setBackgroundResource(R.drawable.custom_btn);
        }
    }
    // Метод регистрации
    public void regUser(View view) {
        // Проверка на пустоту E-mail
        if (!TextUtils.isEmpty(editTextEmail.getEditText().getText().toString())){
            // Проверка на пустоту пароля
            if (!TextUtils.isEmpty(editTextPassword.getEditText().getText().toString())){
                // Проверка корректности E-mail
                if (isEmailValid(editTextEmail.getEditText().getText().toString())){
                    // Проверка корректности пароля
                    if (isPasswordValid(editTextPassword.getEditText().getText().toString())){
                        // Проверка на соответствие повторения пароля
                        if (editTextPassword.getEditText().getText().toString().equals(editTextRepeatPassword.getEditText().getText().toString())) {
                            mAuth.createUserWithEmailAndPassword(editTextEmail.getEditText().getText().toString(), editTextPassword.getEditText().getText().toString())
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Успешная регистрация
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                // добавление магазина в бд
                                                if (user != null) {
                                                    // Получаем id пользователя
                                                    String uid = user.getUid();
                                                    Log.d("user", "user is key is:" + uid);
                                                    // Загрузка фото в бд
                                                    Uri file = Uri.fromFile(new File(uri.getPath()));
                                                    String[] extensionFile = file.getLastPathSegment().split("\\.");

                                                    // Загружаем инфу о магазине в БД
                                                    mDatabase.child("shops").child(uid).setValue(new Shop(editTextShopName.getEditText().getText().toString(),
                                                            editTextEmail.getEditText().getText().toString(), extensionFile[1]));

                                                    StorageReference riversRef = storageRef.child("images/"+uid+"/avatar_shop." + extensionFile[1]);
                                                    UploadTask uploadTask = riversRef.putFile(uri);
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
                                                                    String stringUri = uri.toString();
                                                                    mDatabase.child("shops").child(uid).child("urlAvatar").setValue(stringUri);
                                                                }
                                                            });
                                                        }
                                                    });
                                                    startActivity(new Intent(RegActivity.this, PersonalAccountBottomNavigation.class));
                                                }
                                                else{
                                                    Log.d("user", "user is null");
                                                }
                                                //updateUI(user);
                                            } else {
                                                // Ошибка регистрации
                                                Log.w("auth", "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(RegActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                                //updateUI(null);
                                            }
                                        }
                                    });
                        }
                        else{
                            editTextRepeatPassword.setError("Пароли не совпадают");
                            isErrorRepeatPassword = true;
                        }

                    }
                    else{
                        editTextPassword.setError("Некорректный пароль");
                        isErrorPassword = true;
                    }
                }
                else {
                    editTextEmail.setError("Некорректный E-mail");
                    isErrorEmail = true;
                }
            }
            else {
                editTextPassword.setError("Укажите пароль");
                isErrorPassword = true;
            }
        }
        else{
            editTextEmail.setError("Укажите E-mail");
            isErrorEmail = true;
        }
    }
    // Метод проверки типа инпута
    public static boolean isValidType(TextInputLayout textInputLayout){
        boolean isValid;
        if (textInputLayout.getEditText().getInputType() == 33){
            isValid = isEmailValid(textInputLayout.getEditText().getText().toString());
        }
        else {
            isValid = isPasswordValid(textInputLayout.getEditText().getText().toString());
        }
        return isValid;
    }
    // Метод проверки корректности e-mail
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    // Метод проверки корректности пароля
    public static boolean isPasswordValid(String password) {
        String expression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {
    }

    public void chooserImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
        //setResult(RESULT_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                avatarShop.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}