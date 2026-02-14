package ique.daitechagent.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ique.daitechagent.R;

public class NetworkAvailability extends AppCompatActivity {


  Button btnTryAgain;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_no_network_try_again);

    btnTryAgain = findViewById(R.id.btnTryagain);

    btnTryAgain.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(NetworkAvailability.this, Login.class);
        startActivity(intent);
        finish();
      }
    });
  }
}
