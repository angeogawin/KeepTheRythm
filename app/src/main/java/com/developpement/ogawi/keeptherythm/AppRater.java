package com.developpement.ogawi.keeptherythm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater {
    private final static String APP_TITLE = "Keep The Rythm";// App Name
    private final static String APP_PNAME = "com.developpement.ogawi.keeptherythm";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 1;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
       // ll.setBackgroundResource(R.drawable.fragment_accueil_shape);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ll.setOrientation(LinearLayout.VERTICAL);


        ImageView emoj=new ImageView(mContext);

        emoj.setImageDrawable(mContext.getResources().getDrawable(R.drawable.emoji_muscle));
        ll.addView(emoj);
        TextView tv = new TextView(mContext);
        tv.setText("Si tu apprécies " + APP_TITLE + ", prends un moment pour le noter. Merci pour le soutien!");
        tv.setTextSize(17);
        tv.setTextColor(mContext.getResources().getColor(R.color.black));
        tv.setWidth(700);
        tv.setPadding(7, 0, 4, 10);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setText("Noter " + APP_TITLE);

        params.setMargins(3, 60, 3, 0);
        b1.setLayoutParams(params);
        b1.setTextColor(mContext.getResources().getColor(R.color.white));
        b1.setAllCaps(false);
        b1.setTextSize(17);
        b1.setBackgroundResource(R.drawable.jouer_shape);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText("Plus tard");
        b2.setTextColor(mContext.getResources().getColor(R.color.white));
        b2.setAllCaps(false);

        params.setMargins(3, 20, 3, 20);
        b2.setLayoutParams(params);
        b2.setTextSize(17);
        b2.setBackgroundResource(R.drawable.jouer_shape);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("Non, merci");
        params.setMargins(3, 20, 3, 20);
        b3.setLayoutParams(params);
        b3.setTextSize(17);
        b3.setTextColor(mContext.getResources().getColor(R.color.white));
        b3.setAllCaps(false);
        b3.setBackgroundResource(R.drawable.jouer_shape);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();

    }

    public static void showRateDialog2(final Context mContext) {


        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));



    }

}