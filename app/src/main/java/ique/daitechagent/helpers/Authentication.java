package ique.daitechagent.helpers;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;

import ique.daitechagent.R;
import ique.daitechagent.common.Common;
import ique.daitechagent.model.AuthenticationQueryResult;
import ique.daitechagent.retrofit.AuthenticationClient;
import ique.daitechagent.retrofit.IRetroAuthentication;
import retrofit2.Call;

public class Authentication extends AsyncTask<String, Void, AuthenticationQueryResult> {

    String email, errmessage;
    AppCompatActivity activity;
    FirebaseHelper.AutheticationCallback autheticationCallback;

    public Authentication(AppCompatActivity activity, String email, FirebaseHelper.AutheticationCallback autheticationCallback) {
        this.email = email;
        this.autheticationCallback = autheticationCallback;
        this.activity = activity;
    }


    @Override
    protected void onPostExecute(AuthenticationQueryResult authenticationQueryResult) {

        if (authenticationQueryResult != null) {
            if (!authenticationQueryResult.isResult()) {
                autheticationCallback.AuthFailed( activity.getResources().getString(R.string.failed_existence) + ":" + authenticationQueryResult.getErrormsg());
            } else {
                autheticationCallback.AuthSuccess("Success");
            }
        }else{
            autheticationCallback.AuthFailed(errmessage);
        }

    }

    @Override
    protected AuthenticationQueryResult doInBackground(String... strings) {

        try {

            // Lets check to see if this email exists on the platform
            if ( !Common.SYS_ADMIN_EMAIL.toLowerCase().contains(email)) {
                IRetroAuthentication apiAuthentication = AuthenticationClient
                        .getRetrofitAuthenticationInstance()
                        .create(IRetroAuthentication.class);

                Call<AuthenticationQueryResult> call = apiAuthentication.doesUserExist(email);

                AuthenticationQueryResult authenticationQueryResult = call.execute().body();
                return authenticationQueryResult;
            }else{
                AuthenticationQueryResult authenticationQueryResult = new AuthenticationQueryResult(true, "");
                return authenticationQueryResult;
            }

        } catch (IOException e) {

            errmessage = "Please check your internet connection.";

        } catch (Exception ex) {

            errmessage = "A general error occured.";

        }

        return null;
    }

    public static AuthenticationQueryResult doesUserExistInCloud(String email, AppCompatActivity activity){


        try {

            IRetroAuthentication apiAuthentication = AuthenticationClient
                    .getRetrofitAuthenticationInstance()
                    .create(IRetroAuthentication.class);

            Call<AuthenticationQueryResult> call = apiAuthentication.doesUserExist(email);

            return call.execute().body();

        } catch (IOException e) {

            Toast.makeText( activity, "doesUserExistInCloud: Please check your internet connection.", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {

            Toast.makeText( activity, "doesUserExistInCloud: A general error occured.", Toast.LENGTH_SHORT).show();

        }

        return null;

    }
}
