package com.smartdsepro.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

public class Login_logout extends Activity {

    GoogleApiClient mGoogleApiClient;
    SignInButton gmailsignInButton;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    final int GOOGLE_LOGIN = 1246;
    final int FB_LOGIN = 7746;
    final String SIGN_IN = "Sign In";
    final String SIGN_OUT = "Sign Out";
    private AccessTokenTracker fbTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_FB_Stuffs();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_logout);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        init_Gmail_Stuffs();
        init_variable_and_listeners();

        if(LoginHelper.getLoggedInUsing(this).equals(Constants.LOGGED_IN_USING_GMAIL))
        {
            loginButton.setEnabled(false);
            setGooglePlusButtonText(gmailsignInButton, SIGN_OUT);
        }
        else if(LoginHelper.getLoggedInUsing(this).equals(Constants.LOGGED_IN_USING_FB))
        {
            gmailsignInButton.setEnabled(false);
        }


    }



    private void init_FB_Stuffs() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        fbTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    //Log.d(Constants.DEBUG_TAG, "User Logged Out.");

                    LoginHelper.setLoggedInUsing(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
                    LoginHelper.setName(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
                    LoginHelper.setUserEmail(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
                    gmailsignInButton.setEnabled(true);
                }
            }
        };
    }

    private void init_Gmail_Stuffs() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    private String getGooglePlusButtonText(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;


                return tv.getText().toString();
            }
        }
        return SIGN_IN;

    }


    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void init_variable_and_listeners() {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //Log.i(Constants.DEBUG_TAG, LoginHelper.getLoggedInUsing(this));
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, mCallBack);



        gmailsignInButton = (SignInButton) findViewById(R.id.google_signin);
        gmailsignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_LOGIN);


            }
        });
    }


    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d(Constants.DEBUG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            if(getGooglePlusButtonText(gmailsignInButton).equals(SIGN_OUT))
            {
                //signOutFromGmail();
                LoginHelper.setName(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
                LoginHelper.setLoggedInUsing(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
                setGooglePlusButtonText(gmailsignInButton, SIGN_IN);
                loginButton.setEnabled(true);
            }
            else
            {
                GoogleSignInAccount acct = result.getSignInAccount();
                String userName = acct.getDisplayName();
                String email = acct.getEmail();
                //Log.d("---------------------", email);
                Toast.makeText(Login_logout.this, "Successfully logged in as: " + userName, Toast.LENGTH_LONG).show();
                LoginHelper.setLoggedInUsing(Login_logout.this, Constants.LOGGED_IN_USING_GMAIL);
                LoginHelper.setName(Login_logout.this, userName);
                LoginHelper.setUserEmail(Login_logout.this, email);
                setGooglePlusButtonText(gmailsignInButton, SIGN_OUT);
                loginButton.setEnabled(false);
            }

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);

        }
        else {
            // Signed out, show unauthenticated UI.
            //signOutFromGmail();
            LoginHelper.setName(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
            LoginHelper.setLoggedInUsing(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);
            LoginHelper.setUserEmail(Login_logout.this, Constants.LOGIN_NAME_NOT_SET);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_LOGIN)
        {
            //Log.i(Constants.DEBUG_TAG, "GOOGLE");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }




    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            System.out.println("Entering...");


            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            //Log.e("response: ", response + "");
                            try {


                                String userName = object.getString("name");
                                String email = object.getString("email");
                                //Log.d("---------------------", email);
                                LoginHelper.setUserEmail(Login_logout.this, email);
                                LoginHelper.setName(Login_logout.this, userName);
                                LoginHelper.setLoggedInUsing(Login_logout.this, Constants.LOGGED_IN_USING_FB);
                                //Log.d("KeyHash:", userName);
                                Toast.makeText(Login_logout.this, "Successfully logged in as: " + userName, Toast.LENGTH_LONG).show();
                                gmailsignInButton.setEnabled(false);

                                //Intent i = new Intent(Home.this, Chat.class);
                                //startActivity(i);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };
    /***
     @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);

     // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
     if (requestCode == 5) {
     GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
     handleSignInResult(result);
     }
     }
     ***/

}
