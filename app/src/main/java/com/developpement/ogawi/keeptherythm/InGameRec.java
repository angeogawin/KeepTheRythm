package com.developpement.ogawi.keeptherythm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jetradarmobile.snowfall.SnowfallView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

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
   // GestureOverlayView v;
    //private GestureDetector mDetector;
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

    String extensionFichier;
    int indicesequence;
    File gpxfile;
    int pos;
    String coupleMvtTemps;
    ArrayList<String> nom_txt;
    ProgressDialog mProgressDialog;
   // GestureOverlayView vOverlay;

    private StorageReference mStorageRef;


    Button tuile_1;
    Button tuile_2;
    Button tuile_3;
    Button tuile_4;
    Button tuile_5;
    RelativeLayout zoneJeu;
    int widthZoneJeu;
    int heightZoneJeu;
    Button go;

    View barregauche;
    View barreHorizontal1;
    View barreHorizontal2;
    View barreHorizontal3;
    View barreHorizontal4;
    SnowfallView snowfallView;
    RelativeLayout viewbarre1;
    RelativeLayout viewbarre2;

    String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame1);

        mStorageRef = FirebaseStorage.getInstance().getReference();
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

        //ici new
        nom_txt.add("airwaves.txt");
        nom_txt.add("daybreak.txt");
        nom_txt.add("canon_d_major.txt");
        nom_txt.add("petit_pantin.txt");
        nom_txt.add("what_i_know_about_you.txt");
        nom_txt.add("furelise.txt");
        nom_txt.add("night_life.txt");
        nom_txt.add("greenleaves.txt");
        nom_txt.add("moonlight_sonata.txt");
        nom_txt.add("idunno.txt");
        nom_txt.add("brightbrazil.txt");
        nom_txt.add("smile_its_me.txt");
        nom_txt.add("life_is_beautiful.txt");
        nom_txt.add("shine_gold_light.txt");
        nom_txt.add("sky_seed.txt");

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

        //ici news
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://archive.org/download/auboutdufil-archives/491/Olivaw-Airwaves.mp3");
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://archive.org/download/auboutdufil-archives/483/Jens_East_-_Daybreak_feat_Henk.mp3");
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://archive.org/download/Classical_Sampler-9615/Kevin_MacLeod_-_Canon_in_D_Major.mp3");
        listeUrl.add("firebase_petit-pantin.mp3");
        listeUrl.add("https://www.auboutdufil.com/get.php?web=https://www.archive.org/download/NicolasFalcon-NicolasFalconaaahh011/09WhatIKnowAboutYou.mp3");
        listeUrl.add("https://audionautix.com/Music/FurElise.mp3");
        listeUrl.add("https://www.free-stock-music.com/music/twisterium-night-life.mp3");
        listeUrl.add("https://audionautix.com/Music/GreenLeaves.mp3");
        listeUrl.add("http://ccmixter.org/content/speck/speck_-_Moonlight_Sonata_(Shifting_Sun_Mix)_1.mp3");
        listeUrl.add("http://ccmixter.org/content/grapes/grapes_-_I_dunno.mp3");
        listeUrl.add("firebase_Danosongs - Bright Brazil.mp3");
        listeUrl.add("firebase_Danosongs - Smile Its Me!.mp3");
        listeUrl.add("firebase_life-is-beautiful.mp3");
        listeUrl.add("firebase_Danosongs - Shine Gold Light - PIano Mix.mp3");
        listeUrl.add("firebase_Danosongs - Sky Seeds - Brit Pop Mix.mp3");

        //vOverlay = (GestureOverlayView) findViewById(R.id.gOverlay);
        //vOverlay.setVisibility(View.VISIBLE);
       // mDetector = new GestureDetector(this, new MyGestureListener());


        // Add a touch1 listener to the view
        // The touch1 listener passes all its events on to the gesture detector

       // vOverlay.setOnTouchListener(touchListener);
        //chargement musique



        tuile_1=findViewById(R.id.tuile1);
        tuile_2=findViewById(R.id.tuile2);
        tuile_3=findViewById(R.id.tuile3);
        tuile_4=findViewById(R.id.tuile4);
        tuile_5=findViewById(R.id.tuile5);

        zoneJeu = findViewById(R.id.zonejeu);

        go = findViewById(R.id.go);
        ViewTreeObserver vto = zoneJeu.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //  zoneJeu.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                widthZoneJeu = zoneJeu.getMeasuredWidth();
                heightZoneJeu = zoneJeu.getMeasuredHeight();


            }
        });
        go.setVisibility(View.INVISIBLE);
        go.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(filePath));
                mChronometer = new Chronometer(getApplicationContext());
                mChronometer.setBase(SystemClock.elapsedRealtime());


                mPlayer.start();
                mChronometer.start();

                go.setVisibility(View.INVISIBLE);
                tuile_1.setWidth(20*widthZoneJeu/100);
                tuile_2.setWidth(20*widthZoneJeu/100);
                tuile_3.setWidth(20*widthZoneJeu/100);
                tuile_4.setWidth(20*widthZoneJeu/100);
                tuile_5.setWidth(20*widthZoneJeu/100);

                RelativeLayout.LayoutParams paramsTuile1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams paramsTuile2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams paramsTuile3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams paramsTuile4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams paramsTuile5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                paramsTuile1.width = 20*widthZoneJeu/100;
                paramsTuile2.width = 20*widthZoneJeu/100;
                paramsTuile3.width = 20*widthZoneJeu/100;
                paramsTuile4.width = 20*widthZoneJeu/100;
                paramsTuile5.width = 20*widthZoneJeu/100;

                paramsTuile2.addRule(RelativeLayout.RIGHT_OF, tuile_1.getId());
                paramsTuile3.addRule(RelativeLayout.RIGHT_OF, tuile_2.getId());
                paramsTuile4.addRule(RelativeLayout.RIGHT_OF, tuile_3.getId());
                paramsTuile5.addRule(RelativeLayout.RIGHT_OF, tuile_4.getId());

                //  tuile_1.setElevation(-2);
                //tuile_2.setElevation(-2);
                //tuile_3.setElevation(-2);
                //tuile_4.setElevation(-2);
                //tuile_5.setElevation(-2);


                tuile_1.setLayoutParams(paramsTuile1);
                tuile_2.setLayoutParams(paramsTuile2);
                tuile_3.setLayoutParams(paramsTuile3);
                tuile_4.setLayoutParams(paramsTuile4);
                tuile_5.setLayoutParams(paramsTuile5);


                tuile_1.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        // derniereAction.setVariable("b");
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                            coupleMvtTemps+="b:" + String.valueOf(timeOfDerniereAction)+";";
                            // Do what you want
                            tuile_1.setForeground(getDrawable(R.drawable.tuilepressed));
                            return true;
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP) {
                            tuile_1.setForeground(getDrawable(R.drawable.tuilenormale));
                        }

                        return false;
                    }
                });
                tuile_2.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        //derniereAction.setVariable("g");
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                            coupleMvtTemps+="g:" + String.valueOf(timeOfDerniereAction)+";";
                            tuile_2.setForeground(getDrawable(R.drawable.tuilepressed));
                            // Do what you want
                            return true;
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP) {
                            tuile_2.setForeground(getDrawable(R.drawable.tuilenormale));
                        }

                        return false;
                    }
                });
                tuile_3.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        // derniereAction.setVariable("tap");
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                            coupleMvtTemps+="tap:" + String.valueOf(timeOfDerniereAction)+";";
                            tuile_3.setForeground(getDrawable(R.drawable.tuilepressed));
                            // Do what you want
                            return true;
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP) {
                            tuile_3.setForeground(getDrawable(R.drawable.tuilenormale));
                        }
                        return false;
                    }
                });
                tuile_4.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        // derniereAction.setVariable("d");
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                            coupleMvtTemps+="d:" + String.valueOf(timeOfDerniereAction)+";";
                            tuile_4.setForeground(getDrawable(R.drawable.tuilepressed));
                            // Do what you want
                            return true;
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP) {
                            tuile_4.setForeground(getDrawable(R.drawable.tuilenormale));
                        }
                        return false;
                    }
                });
                tuile_5.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        // derniereAction.setVariable("h");
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                            coupleMvtTemps+="h:" + String.valueOf(timeOfDerniereAction)+";";
                            tuile_5.setForeground(getDrawable(R.drawable.tuilepressed));
                            // Do what you want
                            return true;
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP) {
                            tuile_5.setForeground(getDrawable(R.drawable.tuilenormale));
                        }
                        return false;
                    }
                });

                barregauche=findViewById(R.id.barrelimitegauche);
                barreHorizontal1=findViewById(R.id.barreHorizontale1);
                barreHorizontal2=findViewById(R.id.barreHorizontale2);
                barreHorizontal3=findViewById(R.id.barreHorizontale3);
                barreHorizontal4=findViewById(R.id.barreHorizontale4);
                snowfallView=findViewById(R.id.snowfall_view);
                viewbarre1=findViewById(R.id.viewBarre1);
                viewbarre2=findViewById(R.id.viewBarre2);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)barregauche.getLayoutParams();
                params.setMargins(0, 70*heightZoneJeu/100, 0, 0); //substitute parameters for left, top, right, bottom
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)viewbarre1.getLayoutParams();
                params1.setMargins(20*widthZoneJeu/100, 0, 0, 0);
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)viewbarre2.getLayoutParams();
                params2.setMargins(40*widthZoneJeu/100, 0, 0, 0);
                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams)barreHorizontal3.getLayoutParams();
                params3.setMargins(60*widthZoneJeu/100, 0, 0, 0);
                RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams)barreHorizontal4.getLayoutParams();
                params4.setMargins(80*widthZoneJeu/100, 0, 0, 0);
                barregauche.setLayoutParams(params);
                viewbarre1.setLayoutParams(params1);
                viewbarre2.setLayoutParams(params2);
                barreHorizontal3.setLayoutParams(params3);
                barregauche.getLayoutParams().height=10*heightZoneJeu/100;
                barregauche.setVisibility(View.VISIBLE);
                barreHorizontal1.setVisibility(View.VISIBLE);
                barreHorizontal2.setVisibility(View.VISIBLE);
                barreHorizontal3.setVisibility(View.VISIBLE);
                barreHorizontal4.setVisibility(View.VISIBLE);

                barreHorizontal1.setBackgroundColor(Color.WHITE);
                barreHorizontal2.setBackgroundColor(Color.WHITE);
                barreHorizontal3.setBackgroundColor(Color.WHITE);
                barreHorizontal4.setBackgroundColor(Color.WHITE);

                barreHorizontal1.setElevation(2);
                barreHorizontal2.setElevation(2);
                barreHorizontal3.setElevation(2);
                barreHorizontal4.setElevation(2);



                //     for(int i=0;i<10;i++){
                Blurry.with(getApplicationContext())
                        .radius(10)
                        .sampling(8)
                        .color(Color.argb(67, 255, 255, 255))
                        .async()
                        .animate(500)

                        .onto(viewbarre1);
                Blurry.with(getApplicationContext())
                        .radius(25)
                        .sampling(2)
                        .color(Color.argb(67, 255, 255, 255))
                        .async()
                        .animate(500)
                        .onto(viewbarre2);
                                  }
                              });



        File mFolder = new File(getFilesDir() + "/Music");
        extensionFichier=obtenirExtensionFichier(listeUrl,pos);
        File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
         filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier;
        if (file.exists()) {
            //on recupère le fichier depuis le repertoire
            go.setVisibility(View.VISIBLE);


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
            if(!listeUrl.get(pos - 1).contains("firebase")){
                downloadTask.execute(listeUrl.get(pos - 1));


                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                        File mFolder = new File(getFilesDir() + "/Music");
                        File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
                        //  String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
                        file.delete();
                        Intent i = new Intent(getApplicationContext(), EcranAccueil.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });
            }
            else if(listeUrl.get(pos - 1).contains("firebase")){
                mProgressDialog.show();
                //Le fichier doit etre telechargé en utilisant firebase
                File mFolder2 = new File(getFilesDir() + "/Music");
                File file2 = new File(mFolder2.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
                String nomFichierSurFirebase=listeUrl.get(pos - 1).split("_")[1];
                StorageReference refFichier=mStorageRef.child(nomFichierSurFirebase);

                refFichier.getFile(file2)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Successfully downloaded data to local file
                                // ...
                                File mFolder = new File(getFilesDir() + "/Music");
                                filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier;
                                go.setVisibility(View.VISIBLE);
                                mProgressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        // ...
                        File mFolder = new File(getFilesDir() + "/Music");
                        File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
                        //  String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
                       file.delete();


                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Erreur téléchargement de la musique", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), EcranAccueil.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                });
            }



            // **** fin ajout des musiques


            indiceActuelSequence = 0;
            score = 0;


            derniereAction = new StringModified();
            timeOfDerniereAction = 0;
            //symboleT = (RelativeLayout) findViewById(R.id.symbole);
            // symboleUtilisateur = (RelativeLayout) findViewById(R.id.symboleMis);
         //   v = (GestureOverlayView) findViewById(R.id.gOverlay);
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


          //  mDetector = new GestureDetector(this, new MyGestureListener());

            // Add a touch1 listener to the view
            // The touch1 listener passes all its events on to the gesture detector
          //  v.setOnTouchListener(touchListener);
           /* v.post(new Runnable() {
                @Override
                public void run() {
                    widthView = v.getWidth(); //height is ready
                }
            });*/

            //  lancerAnimation(sequence);


        }
    }
    public  String obtenirExtensionFichier(ArrayList<String> liste,int pos){

        String extensionFichier="";
        if(liste.get(pos-1).contains("_wav")){
            extensionFichier=".wav";
        }
        else{
            extensionFichier=".mp3";
        }

        return  extensionFichier;
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
   /* View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event

            return mDetector.onTouchEvent(event);

        }
    };
*/
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
                File file = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
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
                    File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+extensionFichier);
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
                    filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier;
                    go.setVisibility(View.VISIBLE);

                }
            }
        }

    // In the SimpleOnGestureListener subclass you should override
// onDown and any other gesture that you want to detect.
   /* class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
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
*/

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

