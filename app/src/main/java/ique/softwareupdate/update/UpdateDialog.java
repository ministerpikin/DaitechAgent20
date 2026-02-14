package ique.softwareupdate.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import ique.daitechagent.R;

public class UpdateDialog {
	  static void show(final Context context, String content, final String downloadUrl) {
	        if (isContextValid(context)) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
	            builder.setTitle("Download");
	            builder.setMessage(Html.fromHtml(content))
	                    .setPositiveButton("Continue?", new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	                            goToDownload(context, downloadUrl);
	                        }
	                    })
	                    .setNegativeButton("Reminde me later", new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	                        }
	                    });

	            AlertDialog dialog = builder.create();

	            dialog.setCanceledOnTouchOutside(false);
	            dialog.show();
	        }
	    }

	    private static boolean isContextValid(Context context) {
	        return context instanceof Activity && !((Activity) context).isFinishing();
	    }


	    private static void goToDownload(Context context, String downloadUrl) {
	        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
	        intent.putExtra(Constants.APK_DOWNLOAD_URL, downloadUrl);
	        context.startService(intent);
	    }

}
