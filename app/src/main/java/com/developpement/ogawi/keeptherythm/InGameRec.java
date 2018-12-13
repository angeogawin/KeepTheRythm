package com.developpement.ogawi.keeptherythm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InGameRec extends AppCompatActivity {//implements OnGesturePerformedListener{
    Intent i;
    static final int NUM_ITEMS = 50;
    //   ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private Toolbar mTopToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    TextView messageTextView;
    ListView mDrawerListView;
    Boolean isDrawerOpen;
    float x1, x2;
    float y1, y2;
    static ArrayList<String> sequence;
    static int indiceActuelSequence;
    static ImageView flecheactuelle;
    static int dernierePositionConnue = 24;//on part de 0 donc à 25,positionActuelle=24
    boolean leftToRight = false;
    boolean rightToLeft = false;
    String dernierSymbole;
    ArrayList<Integer> listeTminToClick;//temps minimum pour chaque animation que le joueur doit attendre avant de cliquer , pour un bon timing
    int tmin;

    //private JazzyViewPager vpage;

    static StringModified derniereAction;//derniere action effectuée par joueur tap,cr,cl...
    long timeOfDerniereAction;
    int score;
    //View v;
    GestureOverlayView v;
    private GestureDetector mDetector;
    TextView scoreT;
    int widthView;
    int heightView;
    private GestureLibrary gLibrary;
    RelativeLayout symboleT;
    RelativeLayout symboleUtilisateur;
    Chronometer mChronometer;
    Boolean elementActuelClickable;
    MediaPlayer mPlayer;

    ArrayList<String> listeUrl;
    int indicesequence;
    File gpxfile;
    int pos;
    String coupleMvtTemps;
    ArrayList<String> nom_txt;
    ProgressDialog mProgressDialog;
    GestureOverlayView vOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);
        i = getIntent();
        pos = i.getExtras().getInt("niveau");

        coupleMvtTemps = "";
        nom_txt = new ArrayList<>();
        nom_txt.add("cybersdf_dolling.txt");
        nom_txt.add("cdk_like_music_cdk_mix.txt");
        nom_txt.add("alla_what_parody.txt");
        nom_txt.add("carol_of_the_bells.txt");
        nom_txt.add("jeffspeed68_two_pianos.txt");
        nom_txt.add("adagioinc.txt");
        nom_txt.add("tender_remains.txt");
        nom_txt.add("rocker.txt");
        nom_txt.add("hansatom_persephone.txt");
        nom_txt.add("go_not_gently.txt");
        nom_txt.add("triangle.txt");
        nom_txt.add("bigcartheft.txt");

        nom_txt.add("arroz_con_pollo.txt");
        nom_txt.add("tango_de_manzana.txt");
        nom_txt.add("no_frills_salsa.txt");
        nom_txt.add("what_is_love.txt");

        listeUrl = new ArrayList<>();
        // **** ajout des musiques
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://archive.org/download/auboutdufil-archives/502/Cybersdf-Dolling.mp3");
        listeUrl.add("http://ccmixter.org/content/cdk/cdk_-_Like_Music_(cdk_Mix)_1.mp3");
        listeUrl.add("https://audionautix.com/Music/AllaWhatParody.mp3");
        listeUrl.add("https://audionautix.com/Music/CarolOfTheBells.mp3");
        listeUrl.add("http://ccmixter.org/content/JeffSpeed68/JeffSpeed68_-_Two_Pianos.mp3");
        listeUrl.add("https://audionautix.com/Music/AdagioInC.mp3");
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://archive.org/download/auboutdufil-archives/492/Myuu-TenderRemains.mp3");
        listeUrl.add("https://audionautix.com/Music/Rocker.mp3");
        listeUrl.add("http://ccmixter.org/content/hansatom/hansatom_-_Persephone.mp3");
        listeUrl.add("https://audionautix.com/Music/GoNotGently.mp3");
        listeUrl.add("https://audionautix.com/Music/Triangle.mp3");
        listeUrl.add("https://audionautix.com/Music/BigCarTheft.mp3");

        listeUrl.add("https://incompetech.com/music/royalty-free/mp3-royaltyfree/Arroz%20Con%20Pollo.mp3");
        listeUrl.add("https://incompetech.com/music/royalty-free/mp3-royaltyfree/Tango%20de%20Manzana.mp3");
        listeUrl.add("https://incompetech.com/music/royalty-free/mp3-royaltyfree/No%20Frills%20Salsa.mp3");
        listeUrl.add("https://incompetech.com/music/royalty-free/mp3-royaltyfree/What%20Is%20Love.mp3");


        vOverlay = (GestureOverlayView) findViewById(R.id.gOverlay);
        vOverlay.setVisibility(View.VISIBLE);
        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch1 listener to the view
        // The touch1 listener passes all its events on to the gesture detector

        vOverlay.setOnTouchListener(touchListener);
        //chargement musique
        File mFolder = new File(getFilesDir() + "/Music");
        File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3");
        String filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3";
        if (file.exists()) {
            //on recupère le fichier depuis le repertoire


            mPlayer = MediaPlayer.create(this, Uri.parse(filePath));
            mChronometer = new Chronometer(this);
            mChronometer.setBase(SystemClock.elapsedRealtime());


            mPlayer.start();
            mChronometer.start();
        } else {
            //on télécharge
// instantiate it within the onCreate method


            mProgressDialog = new ProgressDialog(InGameRec.this);

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Téléchargement de la musique");
            mProgressDialog.setIndeterminate(true);

            mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(InGameRec.this);
            downloadTask.execute(listeUrl.get(pos - 1));


            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                    File mFolder = new File(getFilesDir() + "/Music");
                    File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3");
                    //  String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
                    file.delete();
                    Intent i = new Intent(getApplicationContext(), EcranAccueil.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            });


            // **** fin ajout des musiques


            indiceActuelSequence = 0;
            score = 0;


            derniereAction = new StringModified();
            timeOfDerniereAction = 0;
            //symboleT = (RelativeLayout) findViewById(R.id.symbole);
            // symboleUtilisateur = (RelativeLayout) findViewById(R.id.symboleMis);
            v = (GestureOverlayView) findViewById(R.id.gOverlay);
            scoreT = (TextView) findViewById(R.id.score);
            scoreT.setText(String.valueOf(score));
            elementActuelClickable = false;


            // Toast.makeText(InGame.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
//flecheactuelle = findViewById(R.id.flecheactuelle);

            // dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);


            isDrawerOpen = false;


            // mTopToolbar = (Toolbar) findViewById(R.id.ingame_toolbar);
          //  setSupportActionBar(mTopToolbar);
            // These lines are needed to display the top-left hamburger button
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);


            mDetector = new GestureDetector(this, new MyGestureListener());

            // Add a touch1 listener to the view
            // The touch1 listener passes all its events on to the gesture detector
            v.setOnTouchListener(touchListener);
            v.post(new Runnable() {
                @Override
                public void run() {
                    widthView = v.getWidth(); //height is ready
                }
            });

            //  lancerAnimation(sequence);

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            generateNoteOnSD(getApplicationContext(), nom_txt.get(pos - 1), coupleMvtTemps);
            mPlayer.stop();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // This touch1 listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch1 events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event

            return mDetector.onTouchEvent(event);

        }
    };

        private class DownloadTask extends AsyncTask<String, Integer, String> {

            private Context context;
            private PowerManager.WakeLock mWakeLock;

            public DownloadTask(Context context) {
                this.context = context;
            }

            @Override
            protected String doInBackground(String... sUrl) {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;

                File mFolder = new File(getFilesDir() + "/Music");
                File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();

                    output = new FileOutputStream(file);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // take CPU lock to prevent CPU from going off if the user
                // presses the power button during download
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        getClass().getName());
                mWakeLock.acquire();
                mProgressDialog.show();
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                super.onProgressUpdate(progress);
                // if we get here, length is known, now set indeterminate to false
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(progress[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                mWakeLock.release();
                mProgressDialog.dismiss();
                if (result != null) {
                    Toast.makeText(context, "Erreur téléchargement de la musique", Toast.LENGTH_LONG).show();
                    File mFolder = new File(getFilesDir() + "/Music");
                    File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3");
                    //  String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
                    file.delete();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 10 seconds
                            Intent i = new Intent(getApplicationContext(), EcranAccueil.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }, 1000);

                } else {
                    //Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();


                    File mFolder = new File(getFilesDir() + "/Music");
                    String filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3";
                    mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(filePath));
                    mChronometer = new Chronometer(getApplicationContext());
                    mChronometer.setBase(SystemClock.elapsedRealtime());


                    mPlayer.start();
                    mChronometer.start();

                }
            }
        }

    // In the SimpleOnGestureListener subclass you should override
// onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        ImageView i = new ImageView(getApplicationContext());

        @Override
        public boolean onDown(MotionEvent event) {


            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int fadeOutDuration = 1000;
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this

            fadeOut.setDuration(fadeOutDuration);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                 //   symboleUtilisateur.removeAllViews();
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
         //   derniereAction.setVariable("tap");
            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
            coupleMvtTemps+="tap:" + String.valueOf(timeOfDerniereAction)+";";

            i.setImageDrawable(getDrawable(R.drawable.tap));
           // symboleUtilisateur.removeAllViews();
          //  symboleUtilisateur.addView(i);
            i.startAnimation(fadeOut);
              Toast.makeText(InGameRec.this, "tap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int fadeOutDuration = 1000;
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this

            fadeOut.setDuration(fadeOutDuration);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                   // symboleUtilisateur.removeAllViews();
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
          //  derniereAction.setVariable("touch1");
            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
            coupleMvtTemps+="touch1:" + String.valueOf(timeOfDerniereAction)+";";
            i.setImageDrawable(getDrawable(R.drawable.touch1));
          //  symboleUtilisateur.removeAllViews();
          //  symboleUtilisateur.addView(i);
            i.startAnimation(fadeOut);
            //  Toast.makeText(InGame.this, "touch1", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
            coupleMvtTemps+="tap:" + String.valueOf(timeOfDerniereAction)+";";
            coupleMvtTemps+="tap:" + String.valueOf(timeOfDerniereAction+200)+";";
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            // String direction=identifierDirectionGeste(e1,e2);
            //  Toast.makeText(InGame.this, String.valueOf(Math.abs(e2.getX()-e1.getX())), Toast.LENGTH_SHORT).show();



if (Math.abs(e2.getX() - e1.getX()) < Math.abs(e2.getY() - e1.getY())) {


                   if (e2.getY() - e1.getY() > 0) {

                         //  derniereAction.setVariable("h");
                           Toast.makeText(InGameRec.this, "up", Toast.LENGTH_SHORT).show();

                   } else if (e2.getY() - e1.getY() < 0) {

                       //    derniereAction.setVariable("b");
                           Toast.makeText(InGameRec.this, "down", Toast.LENGTH_SHORT).show();

                   }
               }



            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            int fadeOutDuration = 1000;
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this

            fadeOut.setDuration(fadeOutDuration);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                 //   symboleUtilisateur.removeAllViews();
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            if (Math.abs(event2.getY() - event1.getY()) < Math.abs(event2.getX() - event1.getX())) {


                if (event2.getX() - event1.getX() > 0) {
                  //  derniereAction.setVariable("g");
                    timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    coupleMvtTemps+="g:" + String.valueOf(timeOfDerniereAction)+";";

                    i.setImageDrawable(getDrawable(R.drawable.fleche_gauche2));
                 //   symboleUtilisateur.removeAllViews();
                 //   symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                    Toast.makeText(InGameRec.this, "left", Toast.LENGTH_SHORT).show();
                } else if (event2.getX() - event1.getX() < 0) {
                 //   derniereAction.setVariable("d");
                    timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    coupleMvtTemps+="d:" + String.valueOf(timeOfDerniereAction)+";";
                    i.setImageDrawable(getDrawable(R.drawable.fleche_droite2));
                 //   symboleUtilisateur.removeAllViews();
                 //   symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                       Toast.makeText(InGameRec.this, "right", Toast.LENGTH_SHORT).show();
                }
            } else if (Math.abs(event2.getX() - event1.getX()) < Math.abs(event2.getY() - event1.getY())) {


                if (event2.getY() - event1.getY() > 0) {
                    timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                 //   derniereAction.setVariable("h");
                    coupleMvtTemps+="h:" + String.valueOf(timeOfDerniereAction)+";";

                    i.setImageDrawable(getDrawable(R.drawable.fleche_haut2));
                  //  symboleUtilisateur.removeAllViews();
                  //  symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                        Toast.makeText(InGameRec.this, "up", Toast.LENGTH_SHORT).show();

                } else if (event2.getY() - event1.getY() < 0) {
                       timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
             //       derniereAction.setVariable("b");
                    coupleMvtTemps+="b:" + String.valueOf(timeOfDerniereAction)+";";


                    i.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
                   // symboleUtilisateur.removeAllViews();
                  //  symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                    Toast.makeText(InGameRec.this, "down", Toast.LENGTH_SHORT).show();

                }
            }
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root= new File(getFilesDir().getAbsolutePath(),"sequence");
            if (!root.exists()) {
                root.mkdirs();
            }
            if(gpxfile==null) {
                gpxfile = new File(root, sFileName);

            }
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Create a new output file stream





    }

}
