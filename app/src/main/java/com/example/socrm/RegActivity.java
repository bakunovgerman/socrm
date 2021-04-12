package com.example.socrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socrm.data.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity {

    private TextInputLayout editTextEmail, editTextPassword, editTextRepeatPassword, editTextShopName;
    // Объект авторизации
    private FirebaseAuth mAuth;
    // Объект подключения БД
    private DatabaseReference mDatabase;
    private boolean isErrorEmail, isErrorPassword, isErroreRepeatPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        // подключение аутентификации
        mAuth = FirebaseAuth.getInstance();
        // подключения БД
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextRepeatPassword = findViewById(R.id.editTextTextPasswordRepeat);
        editTextShopName = findViewById(R.id.editTextTitleShop);

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
                    if (isErroreRepeatPassword) {
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

        //TextInputLayout til = (TextInputLayout) findViewById(R.id.av_phoneNO);
        //til.setError("You need to enter a name");
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
                                                mDatabase.child("shops").child("1").setValue(new Shop(editTextShopName.getEditText().getText().toString(),
                                                        editTextEmail.getEditText().getText().toString()));
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
                            isErroreRepeatPassword = true;
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
}