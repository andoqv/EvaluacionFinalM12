package com.example.andres.evaluacionfinalm12;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private CallbackManager cM;
    LoginButton lB;
    Button btn_IrAGoogle;

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    YouTubePlayerView yPV;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        cM = CallbackManager.Factory.create();

        getFbKeyHash("857522834416969");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        yPV = (YouTubePlayerView) findViewById(R.id.youtube);
        yPV.initialize("AIzaSyBBdR0NrvuftVzoOSwIbVxZeASlZm5EJ9A", this);

        lB = (LoginButton) findViewById(R.id.login_facebook);
        lB.registerCallback(cM, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "¡Inicio de sesión exitoso!"
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "¡Inicio de sesión cancelado!"
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "¡Inicio de sesión NO exitoso!"
                        , Toast.LENGTH_LONG).show();
            }
        });

        btn_IrAGoogle = (Button) findViewById(R.id.bn_IrAGoogle);
        btn_IrAGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this
                        , GoogleSignIn.class));
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    public void getFbKeyHash(String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash :", Base64.encodeToString(md.digest()
                        , Base64.DEFAULT));
                System.out.println("KeyHash: " + Base64.encodeToString(
                        md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException |
                NoSuchAlgorithmException ignored) {

        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        cM.onActivityResult(reqCode, resCode, data);
        if (reqCode == RECOVERY_DIALOG_REQUEST) {
            getYoutubePlayerProvider().initialize(
                    "AIzaSyBBdR0NrvuftVzoOSwIbVxZeASlZm5EJ9A", this);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider
            , YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.loadVideo("TieksFvD-7o");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider
            , YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this
                    , RECOVERY_DIALOG_REQUEST)
                    .show();
        } else {
            @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
            String errorMessage = String.format(getString(R.string
                            .Error_Reproduccion)
                    , youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage
                    , Toast.LENGTH_LONG).show();
        }

    }

    private YouTubePlayer.Provider getYoutubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube);
    }

}
