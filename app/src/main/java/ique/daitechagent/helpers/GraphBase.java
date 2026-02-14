package ique.daitechagent.helpers;

import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.github.mikephil.charting.charts.Chart;
import ique.daitechagent.R;

public abstract class GraphBase extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    private static final int PERMISSION_STORAGE = 0;
    protected final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"};
    protected final String[] parties = {"Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H", "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P", "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X", "Party Y", "Party Z"};
    protected Typeface tfLight;
    protected Typeface tfRegular;

    /* access modifiers changed from: protected */
    public abstract void saveToGallery();

    public GraphBase() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        this.tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
    }

    /* access modifiers changed from: protected */
    public float getRandom(float range, float start) {
        return ((float) (Math.random() * ((double) range))) + start;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 0) {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == 0) {
            saveToGallery();
        } else {
            Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: protected */
    public void requestStoragePermission(View view) {
        String str = "android.permission.WRITE_EXTERNAL_STORAGE";
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, str)) {
            Snackbar.make(view, "Write permission is required to save image to gallery", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Request", new OnClickListener() {
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(GraphBase.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
                }
            }).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Permission Required!", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, new String[]{str}, 0);
    }

    /* access modifiers changed from: protected */
    public void saveToGallery(Chart chart, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("_");
        sb.append(System.currentTimeMillis());
        if (chart.saveToGallery(sb.toString(), 70)) {
            Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
        }
    }
}
