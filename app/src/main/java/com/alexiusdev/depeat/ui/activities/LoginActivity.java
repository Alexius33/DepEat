package com.alexiusdev.depeat.ui.activities;

import static com.alexiusdev.depeat.ui.Utility.EMAIL_KEY;
import static com.alexiusdev.depeat.ui.Utility.MIN_LENGTH_PSW;
import static com.alexiusdev.depeat.ui.Utility.isValidEmail;
import static com.alexiusdev.depeat.ui.Utility.showToast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.services.RestController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener/*, Response.ErrorListener, Response.Listener<String>*/ {

    private Button loginBtn, signinBtn, forgotPasswordBtn, visibilityBtn;
    private EditText emailET, passwordET;
    private TextView creditsTV, versionTV;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    RestController restController;
    private boolean isVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        restController = new RestController(this);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        loginBtn = findViewById(R.id.login_btn);
        signinBtn = findViewById(R.id.signin_btn);
        forgotPasswordBtn = findViewById(R.id.forgot_password_btn);
        emailET = findViewById(R.id.email_et);
        passwordET = findViewById(R.id.password_et);
        creditsTV = findViewById(R.id.credits_tv);
        versionTV = findViewById(R.id.version_tv);
        visibilityBtn = findViewById(R.id.visibility_btn);
        visibilityBtn.setBackground(getDrawable(R.drawable.ic_visibility_black_24dp));

        loginBtn.setOnClickListener(this);
        signinBtn.setOnClickListener(this);
        creditsTV.setOnClickListener(this);
        versionTV.setOnClickListener(this);
        forgotPasswordBtn.setOnClickListener(this);
        visibilityBtn.setOnClickListener(this);


        emailET.addTextChangedListener(loginButtonTextWatcher);
        passwordET.addTextChangedListener(loginButtonTextWatcher);

    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.visibility_btn:
                if(!isVisible){
                    visibilityBtn.setBackground(getDrawable(R.drawable.ic_visibility_off_black_24dp));
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT);
                }else{
                    visibilityBtn.setBackground(getDrawable(R.drawable.ic_visibility_black_24dp));
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isVisible = !isVisible;
                passwordET.setTypeface(Typeface.DEFAULT);
                break;
            case R.id.login_btn:
                login(emailET.getText().toString(), passwordET.getText().toString());
                break;
            case R.id.signin_btn:
                startActivity(new Intent(LoginActivity.this, SignInActivity.class)
                        .putExtra(EMAIL_KEY,emailET.getText().toString()));
                break;
            case R.id.credits_tv:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alexius33/")));
                break;
            case R.id.version_tv:
                //TODO implement a changelog?
                break;
            case R.id.forgot_password_btn:
                passwordReset();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (R.id.forgot_password_menu):
                if(!emailET.getText().toString().isEmpty())
                    passwordReset();
                else
                    showToast(this,"Please insert email first.");
        }
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher loginButtonTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable editable) {
            boolean check = isValidEmail(emailET.getText().toString()) &&   //enable button only if the mail is valid and
                    passwordET.getText().toString().length() >= MIN_LENGTH_PSW;    //password is longer than threshold
            loginBtn.setEnabled(check);    //the password length is greater or equal to MIN_LENGTH_PSW
            int colour = check ? getApplication().getResources().getColor(R.color.primary_text) : getApplication().getResources().getColor(R.color.disabled_text);
            loginBtn.setTextColor(colour);
        }
    };

     void login(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                //Sign in success, update UI with the signed-in user's information
                FirebaseUser user = mAuth.getCurrentUser();
                showToast(LoginActivity.this,getString(R.string.welcome).concat(" " + user.getEmail()));
                setResult(RESULT_OK);
                finish();
            } else {
                //If sign in fails, display a message to the user.
                passwordET.setText("");
                showToast(LoginActivity.this,getString(R.string.wrong_password));
                forgotPasswordBtn.setVisibility(View.VISIBLE);
                setResult(RESULT_CANCELED);
            }
        });
    }
     /*void login(final String email, final String password){
         String endPoint = "auth/local/";
         Map<String, String> mapBody = new HashMap<>();
             mapBody.put("identifier", email);
             mapBody.put("password", password);
         JSONObject body = new JSONObject(mapBody);
         restController.postRequest(endPoint, body,this,this);



     }*/

    private void passwordReset(){
        mAuth.sendPasswordResetEmail(emailET.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                showToast(LoginActivity.this,getString(R.string.forgot_password_toast));
            else
                showToast(LoginActivity.this,getString(R.string.error));
        });
    }
/*
    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject= new JSONObject(response);

            //TODO GET SESSION ID FROM RESPONSE
            String session = jsonObject.getString("jwt");

            editor = sharedPref.edit();
            editor.putString(getString(R.string.session_id_key),session);
            Log.d("sessionID",session);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
