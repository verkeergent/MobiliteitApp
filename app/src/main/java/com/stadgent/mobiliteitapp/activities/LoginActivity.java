package com.stadgent.mobiliteitapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.model.User;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.repo.OnLoggedInListener;
import com.stadgent.mobiliteitapp.session.UserSessionManager;

public class LoginActivity extends Activity implements OnLoggedInListener {

    Button btnLogin;

    EditText txtUsername, txtPassword;

    private String username="";
    private String password="";

    private ProgressBar progress;
    private LinearLayout progressLayout;

    // User Session Manager Class
    UserSessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // User Session Manager
        session = new UserSessionManager(getApplicationContext());

        // get Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progress = (ProgressBar) findViewById(R.id.loginProgress);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);

        progressLayout.setVisibility(View.GONE);

        ItemRepository.registerListener(this);


        //final OnLoggedInListener onLoggedInListener = this;


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();


                // Validate if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                    //new LoginCallback(onLoggedInListener, username, password);
                    ItemRepository.login(username, password);
                    progressLayout.setVisibility(View.VISIBLE);

                }else{

                    // user didn't enter username or password
                    Toast.makeText(getApplicationContext(),
                            "Vul alle velden in!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onLoginSuccess(User user) {
        session.createUserLoginSession(username,
                password);

        progressLayout.setVisibility(View.GONE);

        // Starting MainActivity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public void onLoginFailed() {
        progressLayout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),"Gebruikersnaam/Wachtwoord is incorrect", Toast.LENGTH_LONG).show();
    }
}
