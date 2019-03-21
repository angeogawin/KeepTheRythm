package com.developpement.ogawi.keeptherythm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.flubber.Flubber;
import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;
import com.developpement.ogawi.keeptherythm.google.example.games.basegameutils.BaseGameActivity;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.plattysoft.leonids.ParticleSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import com.google.android.gms.games.Games;

public class EcranAccueil extends BaseGameActivity {
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private static final int RC_SIGN_IN=9008;

    static final int NUM_ITEMS = 31;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private Toolbar mTopToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    TextView messageTextView;
    ListView mDrawerListView;
    Boolean isDrawerOpen;
    Button btnJouer;
    Button btnRec;
    static Intent i;
    static Intent j;

    private DrawerLayout intLayout;
    private AnimationDrawable animationDrawable;

    int media_length;

    DonutProgress donutProgress;

    SharedPreferences sharedPreferences;


    private AudioManager mgr;
    GestureOverlayView detecteurGeste;
    private GestureDetector mDetector;
    final static int NIV_MAX_ATTEIGNABLE=31;


    ImageView showachievement;
    ImageView showleaderboard;
    ImageView actionsettings;
    ImageView muteUnmute;
    Boolean mute;

    GoogleSignInClient mGoogleSignInClient;
    static ArrayList<Integer> listeMusiqueAccueil;
    static MediaPlayer playerAccueil;
    ArrayList<String> nom_txt;
    static int num_musique;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_accueil);


        // Configure sign-in to request the user's ID, email address, and basic
//////// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        showleaderboard=findViewById(R.id.show_leaderboard);
        showachievement=findViewById(R.id.show_achievements);
        actionsettings=findViewById(R.id.action_settings);
        muteUnmute = findViewById(R.id.mute_unmute);
        AppRater.app_launched(this);

        final TextView messageVerrou=findViewById(R.id.messageVerrouille);
        final Animation myAnimDisparait = AnimationUtils.loadAnimation(this, R.anim.disappear);
        final Animation myAnimAparait = AnimationUtils.loadAnimation(this, R.anim.appear);

        detecteurGeste=findViewById(R.id.detecteurGeste);
        mDetector = new GestureDetector(this, new EcranAccueil.MyGestureListener());
        detecteurGeste.setOnTouchListener(touchListener);
        donutProgress=findViewById(R.id.donut_progress);



        mgr=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        sharedPreferences = getApplicationContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);

        listeMusiqueAccueil=new ArrayList<>();
        // **** ajout des musiques
        listeMusiqueAccueil.add(R.raw.vj_memes_paint_the_sky);
        listeMusiqueAccueil.add(R.raw.tobias_weber_rescue_me_instrumental);
        listeMusiqueAccueil.add(R.raw.tobias_weber_the_parting_glass_instrumental);
        Random alea = new Random();
        num_musique = alea.nextInt(listeMusiqueAccueil.size()) ;

        mute=false;
        if(sharedPreferences.contains("mute")){
            mute=sharedPreferences.getBoolean("mute",false);

        }

        else{
            sharedPreferences
                    .edit()
                    .putBoolean("mute",false)
                    .apply();
        }


        donutProgress.setProgress(Math.round((int)(obtenirAvancement()*100/NUM_ITEMS)));
        btnJouer=(Button) findViewById(R.id.btnJouer);
        btnRec=findViewById(R.id.btnRec);
        i=new Intent(getApplicationContext(),InGame.class);
        j=new Intent(getApplicationContext(),InGameRec.class);
        i.putExtra("niveau",1);
        j.putExtra("niveau",1);
        int maxVolume = 50;


        if(sharedPreferences.contains("dernier_niveau_joue")){
            i.putExtra("niveau",sharedPreferences.getInt("dernier_niveau_joue",0));
            j.putExtra("niveau",sharedPreferences.getInt("dernier_niveau_joue",0));
        }
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        DepthTransformation depthTransformation = new DepthTransformation();
       viewPager.setPageTransformer(true, depthTransformation);

        if(!sharedPreferences.contains("niveau_max_jouable")){
            sharedPreferences
                    .edit()
                    .putInt("niveau_max_jouable",3)
                    .apply();
        }

        if(sharedPreferences.contains("niveau_max_atteint")){
            viewPager.setCurrentItem(sharedPreferences.getInt("dernier_niveau_joue",0)-1);
          //  Toast.makeText(EcranAccueil.this, String.valueOf(aJoueTousLesNiveauxDeverouille(sharedPreferences.getInt("niveau_max_atteint",0))), Toast.LENGTH_SHORT).show();
            if(sharedPreferences.getInt("niveau_max_atteint",0)>=sharedPreferences.getInt("niveau_max_jouable",0) && aJoueTousLesNiveauxDeverouille(sharedPreferences.getInt("niveau_max_atteint",0))){


                sharedPreferences
                        .edit()
                        .putInt("niveau_max_jouable",sharedPreferences.getInt("niveau_max_atteint",0)+2)
                        .apply();
            }

           // i.putExtra("niveau",sharedPreferences.getInt("niveau_max_atteint",0));
          //  j.putExtra("niveau",sharedPreferences.getInt("niveau_max_atteint",0));
        }
        //Gestion du deverrouillage des 100 % du jeu
        if(!sharedPreferences.contains("confetti_jeu_termine")){
            sharedPreferences
                    .edit()
                    .putInt("confetti_jeu_termine",0)
                    .apply();
        }

        if(aJoueTousLesNiveauxDeverouille(NIV_MAX_ATTEIGNABLE) && sharedPreferences.getInt("confetti_jeu_termine",0)==0){
            sharedPreferences
                    .edit()
                    .putInt("confetti_jeu_termine",1)
                    .apply();
            KonfettiView viewKonfetti=findViewById(R.id.viewKonfetti);
            viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(5000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(new Size(7,5f))
                    .setPosition(-50f, viewKonfetti.getWidth() + 700f, -50f, -50f)
                    .stream(150, 3000L);

            AppRater.app_launched_after100(this);

            //Reussite 100%

                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlock(getString(R.string.terminer_tout));


        }
        isDrawerOpen=false;
        playerAccueil=getInstanceMediaplayer();
        if(mute==true){
            playerAccueil.setVolume(0,0);
            muteUnmute.setImageDrawable(getDrawable(R.drawable.mute2));

        }
        else{

            muteUnmute.setImageDrawable(getDrawable(R.drawable.unmuted2));

        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.

                i.putExtra("niveau",position+1);
                j.putExtra("niveau",position+1);
                sharedPreferences = getApplicationContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);
     /*           if(sharedPreferences.contains("niveau_max_jouable")) {
                    if (position + 1 > sharedPreferences.getInt("niveau_max_jouable", 0)) {
                        if (btnJouer.isClickable()) {
                            btnJouer.setClickable(false);
                            btnJouer.startAnimation(myAnimDisparait);
                            messageVerrou.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (!btnJouer.isClickable()) {
                            btnJouer.setClickable(true);
                            btnJouer.startAnimation(myAnimAparait);
                            messageVerrou.setVisibility(View.INVISIBLE);
                        }

                    }


                }*/


                playerAccueil=getInstanceMediaplayer();



                nom_txt = new ArrayList<>();
                /*26*/nom_txt.add("idunno.txt");
                /*31*/nom_txt.add("sky_seed.txt");
                /*3*/nom_txt.add("alla_what_parody.txt");
                /*17*/nom_txt.add("airwaves.txt");
                /*29*/nom_txt.add("life_is_beautiful.txt");
               /*1*/ nom_txt.add("cybersdf_dolling.txt");
                /*6*/ nom_txt.add("adagioinc.txt");
                /*27*/nom_txt.add("brightbrazil.txt");
                /*4*/ nom_txt.add("carol_of_the_bells.txt");
                /*10*/ nom_txt.add("go_not_gently.txt");
                /*16*/nom_txt.add("what_is_love.txt");
                /*18*/nom_txt.add("daybreak.txt");
                /*28*/nom_txt.add("smile_its_me.txt");
                /*2*/nom_txt.add("cdk_like_music_cdk_mix.txt");
                /*13*/ nom_txt.add("arroz_con_pollo.txt");
                /*12*/ nom_txt.add("bigcartheft.txt");
                /*9*/ nom_txt.add("hansatom_persephone.txt");
                /*8*/ nom_txt.add("rocker.txt");
                /*14*/nom_txt.add("tango_de_manzana.txt");
                /*7*/ nom_txt.add("tender_remains.txt");
                /*5*/nom_txt.add("jeffspeed68_two_pianos.txt");
                /*11*/nom_txt.add("triangle.txt");
                /*19*/nom_txt.add("canon_d_major.txt");
                /*24*/nom_txt.add("greenleaves.txt");
                /*23*/nom_txt.add("night_life.txt");
                /*15*/ nom_txt.add("no_frills_salsa.txt");
                //ici new
                /*20*/ nom_txt.add("petit_pantin.txt");
                /*21*/nom_txt.add("what_i_know_about_you.txt");
                /*22*/nom_txt.add("furelise.txt");
                /*25*/nom_txt.add("moonlight_sonata.txt");
                /*30*/nom_txt.add("shine_gold_light.txt");



                File mFolder = new File(getFilesDir() + "/Music");


                File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(position)+".mp3");
                String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(position)+".mp3";
                if(file.exists()){
                    //on recupère le fichier depuis le repertoire
                    playerAccueil.stop();
                 //   playerAccueil.reset();
                    playerAccueil.release();

                    playerAccueil = MediaPlayer.create(getApplicationContext(), Uri.parse(filePath));

                }
                playerAccueil.setLooping(true);
                playerAccueil.start();

                if(mute==true){
                    playerAccueil.setVolume(0,0);
                    muteUnmute.setImageDrawable(getDrawable(R.drawable.mute2));

                }

            }
        });



        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layoutaccueil);

    //    mTopToolbar = (Toolbar) findViewById(R.id.main_toolbar);
       // setSupportActionBar(mTopToolbar);
        // These lines are needed to display the top-left hamburger button
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Make the hamburger button work
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawer,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        actionsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });
        // Change the TextView message when ListView item is clicked
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //    messageTextView.setText("Menu Item at position " + position + " clicked.");;
                if(position==1){
                    Intent e=new Intent(EcranAccueil.this,ReglesJeu.class);
                    startActivity(e);
                }
                else if(position==2){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
                else if(position==3){

                    AppRater.showRateDialog2(getApplicationContext());
                }
                else if(position==4){
                    Intent e=new Intent(EcranAccueil.this,APropos.class);
                    startActivity(e);
                }
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });



       // float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
       // playerAccueil.setVolume(0.2f, 0.2f);




        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               playerAccueil.stop();
                startActivity(i);
                finish();

            }
        });

        muteUnmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(mute==false){
                  //mute est actualisee a true
                  muteUnmute.setImageDrawable(getDrawable(R.drawable.mute2));
                  mute=true;

                  playerAccueil.setVolume(0,0);

              }
                else{
                    //mute est actualisee a false
                    muteUnmute.setImageDrawable(getDrawable(R.drawable.unmuted2));
                    mute=false;
                  playerAccueil.setVolume(1,1);
                }
                sharedPreferences
                        .edit()
                        .putBoolean("mute",mute)
                        .apply();
            }
        });
        btnRec.setVisibility(View.INVISIBLE);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerAccueil.stop();


                startActivity(j);
                finish();

            }
        });


       showachievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account!=null){
                    //SI LE COMPTE EXISTE
                    if (GoogleSignIn.hasPermissions(account, Games.SCOPE_GAMES_LITE)) {
                        showAchievements(account);
                    }
                    else {
                        //SI LE COMPTE N'A PAS LES PERMISSIONS
                        mGoogleSignInClient
                                .silentSignIn()
                                .addOnCompleteListener(
                                        getParent(),
                                        task -> {
                                            if (task.isSuccessful()) {
                                                showAchievements(task.getResult());
                                            } else {
                                                demanderJoueurConnexion();
                                            }
                                        });
                    }


                }
                else{
                    //SI AUCUN COMPTE DETECTE
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent,RC_SIGN_IN);

                }




            }
        });
       showleaderboard.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
              if(account!=null){
                  //SI LE COMPTE EXISTE
                  if (GoogleSignIn.hasPermissions(account, Games.SCOPE_GAMES_LITE)) {
                      //SI LE COMPTE A LES PERMISSIONS
                     showLeaderBoard(account);
                  } else {
                      //SI LE COMPTE N'A PAS LES PERMISSIONS
                      mGoogleSignInClient
                              .silentSignIn()
                              .addOnCompleteListener(
                                      getParent(),
                                      task -> {
                                          if (task.isSuccessful()) {
                                              showLeaderBoard(task.getResult());
                                          } else {
                                              demanderJoueurConnexion();
                                          }
                                      });
                  }
              }


               else {
                  //SI AUCUN COMPTE DETECTE
                   Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                   startActivityForResult(signInIntent,RC_SIGN_IN);
               }





           }
       });



        animationDrawable = (AnimationDrawable) mDrawer.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(5000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);

    }

    public  MediaPlayer getInstanceMediaplayer(){
        if(playerAccueil==null){
            playerAccueil= MediaPlayer.create(getApplicationContext(), listeMusiqueAccueil.get(num_musique));
        }
        return playerAccueil;
    }
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

    public void showLeaderBoard(GoogleSignInAccount account){
        Games.getLeaderboardsClient(getApplicationContext(), account)
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    public  void showAchievements(GoogleSignInAccount account){
        Games.getAchievementsClient(getApplicationContext(), account)
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }
    public void demanderJoueurConnexion(){
        Toast.makeText(EcranAccueil.this, "Un compte Google Play est nécessaire", Toast.LENGTH_LONG).show();
    }

    public boolean aJoueTousLesNiveauxDeverouille(int niveauMax){

        boolean retour=true;

        for(int i=1;i<=niveauMax;i++){
            if(!sharedPreferences.contains("trophy_niveau"+String.valueOf(i))){
                retour=false;
                return  retour;
            }
        }
        return retour;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInstanceMediaplayer().seekTo(media_length);
        getInstanceMediaplayer().start();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();

        }

    }
    @Override
    protected  void onStart(){
        super.onStart();


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("erreur_signin", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        getInstanceMediaplayer().pause();
        media_length=getInstanceMediaplayer().getCurrentPosition();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_heart) {
//            Toast.makeText(EcranAccueil.this, "Action clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        else if(id==R.id.action_plus){
//
//        }

         if(id==R.id.action_settings) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                mDrawer.openDrawer(GravityCompat.START);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public int obtenirAvancement(){
        int retour=0;

        sharedPreferences = getApplication().getSharedPreferences("prefs_joueur", MODE_PRIVATE);
        for(int i=0;i<NUM_ITEMS;i++){
            if(sharedPreferences.contains("trophy_niveau"+String.valueOf(i+1))){
                retour++;
            }
        }

        return retour;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        mDetector.onTouchEvent(e);

        return super.dispatchTouchEvent(e);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    public class DepthTransformation implements ViewPager.PageTransformer{
        @Override
        public void transformPage(View page, float position) {

            if (position < -1){    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            }
            else if (position <= 0){    // [-1,0]
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);

            }
            else if (position <= 1){    // (0,1]
                page.setTranslationX(-position*page.getWidth());
                page.setAlpha(1-Math.abs(position));
                page.setScaleX(1-Math.abs(position));
                page.setScaleY(1-Math.abs(position));

            }
            else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);

            }


        }
    }
    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {

        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();

            return SwipeFragment.newInstance(position);

        }
    }

    public static class SwipeFragment extends Fragment {
        ArrayList<String> liste_titre;
        SharedPreferences sharedPreferences;
        String sequenceARealiser;
        ArrayList<String> sequences;
        ArrayList<String> listehashtags;


        String[] couples;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment_accueil, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            final Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
           TextView numeroNiveau=swipeView.findViewById(R.id.numeroNiveau);
            Flubber.with()
                    .animation(Flubber.AnimationPreset.MORPH) // Slide up animation

                    .repeatCount(1)                              // Repeat once
                    .duration(1000)                              // Last for 1000 milliseconds(1 second)
                    .createFor(numeroNiveau)                             // Apply it to the view
                    .start();
            numeroNiveau.setText(String.valueOf(position+1));

            //Initialisation bdd
            final ScoreDAO scoreDAO=new ScoreDAO(getContext());


            liste_titre=new ArrayList<>();
            /*26*/ liste_titre.add("I dunno - Grapes");
            /*31*/ liste_titre.add("Sky Seeds-Brit Pop Mix - Dan-O");
            /*3*/  liste_titre.add("Alla What Parody - Audionautix");
            /*17*/ liste_titre.add("Airwaves - Olivaw");
            /*29*/ liste_titre.add("Life is Beautiful - Twisterium");
            /*1*/ liste_titre.add("Dolling - Cybersdf");
            /*6*/  liste_titre.add("Adagio -Audionautix");
            /*27*/ liste_titre.add("Bright Brazil - Dan-O");
            /*4*/  liste_titre.add("Carol of the bells - Audionautix");
            /*10*/ liste_titre.add("Go not Gently - Audionautix");
            /*16*/ liste_titre.add("What is Love - Kevin MacLeod");
            /*18*/ liste_titre.add("Daybreak feat Henk - Jen East");
            /*28*/ liste_titre.add("Smile Its Me - Dan-O");
            /*2*/ liste_titre.add("Like Music (cdk Mix) - Analog By Nature");
            /*13*/ liste_titre.add("Arroz Con Pollo - Kevin MacLeod");
            /*12*/ liste_titre.add("Big car Theft - Audionautix");
            /*9*/ liste_titre.add("Persephone - Hans Atom");
            /*8*/ liste_titre.add("Rocker - Audionautix");
            /*14*/ liste_titre.add("Tango de Manzana - Kevin MacLeod");
            /*7*/  liste_titre.add("Tender Remains - Kyuu");
            /*5*/ liste_titre.add("Two Pianos (ft. Admiral Bob (admiralbob77)) - Stefan Kartenberg");
            /*11*/ liste_titre.add("Triangle - Audionautix");
            /*19*/ liste_titre.add("Canon in D Major - Kevin MacLeod");
            /*24*/ liste_titre.add("GreenLeaves - Audionautix");
            /*23*/ liste_titre.add("Night Life - Twisterium");
            /*15*/ liste_titre.add("No Frills Salsa - Kevin MacLeod");







            //ici new



            /*20*/ liste_titre.add("Petit pantin au coeur de glace - Laei");
            /*21*/ liste_titre.add("What I Know About You - Nicolas Falcon");
            /*22*/ liste_titre.add("FurElise - Audionautix");


            /*25*/ liste_titre.add("Moonlight Sonata (Shifting Sun Mix) - Speck ft SnowFlake ");




            /*30*/ liste_titre.add("Shine Gold Light - Dan-O");





            listehashtags=new ArrayList<>();
            /*26*/ listehashtags.add("#Hip hop #Instrumental");
            /*31*/ listehashtags.add("#Rock");
            /*3*/ listehashtags.add("#Symphony #Piano");
            /*17*/ listehashtags.add("#Electro");
            /*29*/ listehashtags.add("#Happy #Uplifting");
            /*1*/ listehashtags.add("#Calm #Electro #House #Groove");
            /*6*/ listehashtags.add("#Symphony #Pensive");
            /*27*/ listehashtags.add("#Electronic Upbeat");
            /*4*/ listehashtags.add("#Symphony #Driving");
            /*10*/ listehashtags.add("#Relaxing #Uplifting");
            /*16*/ listehashtags.add("#Calming #Synths #Percussion");
            /*18*/ listehashtags.add("#Electro #House #Pop");
            /*28*/ listehashtags.add("#Electronic Upbeat");
            /*2*/ listehashtags.add("#Piano #Chill #Instrumental");
            /*13*/ listehashtags.add("#Latin #Grooving #Percussion #Guitar");
            /*12*/ listehashtags.add("#Electronic #Fast #Aggressive");
            /*9*/ listehashtags.add("#Electronic #Rock #Alternative");
            /*8*/ listehashtags.add("#Driving #Aggressive #Action");
            /*14*/ listehashtags.add("#Latin #Violin #Drums #Dark");
            /*7*/ listehashtags.add("#Piano #Sad #Instrumental");
            /*5*/ listehashtags.add("#Ambiant #Piano #Ballad");
            /*11*/ listehashtags.add("#Relaxing #Dark #Calming");
            /*19*/ listehashtags.add("#Classique");
            /*24*/ listehashtags.add("#Slow #Relaxing");
            /*23*/ listehashtags.add("#Electronica #Pop");
            /*15*/ listehashtags.add("#Percussion #Latin #Trumpet");







            //ici new



            /*20*/ listehashtags.add("#Instrumental  #Piano");
            /*21*/ listehashtags.add("#Acoustique #Folk");
            /*22*/ listehashtags.add("#Slow #Relaxing");


            /*25*/ listehashtags.add("#Classical #Piano");




            /*30*/ listehashtags.add("#Piano");



            final TextView titre=swipeView.findViewById(R.id.titre_musique);
            titre.setText(liste_titre.get(position));


            sequences=new ArrayList<>();
            /*26*/ sequences.add("g:738;tap:1172;g:1527;d:1867;tap:2179;tap:2523;g:2854;d:3235;tap:3522;tap:3895;g:4151;d:4479;tap:4840;d:5220;tap:5516;h:5918;tap:6184;d:6512;tap:6825;d:7221;tap:7501;d:7822;h:8126;tap:8501;d:8838;tap:9142;h:9472;tap:9835;d:10139;g:10444;b:10828;d:11159;tap:11399;g:11815;tap:12209;b:12490;d:12844;tap:13188;d:13538;tap:13898;tap:14186;g:14506;d:14858;tap:15114;b:15494;d:15853;tap:16157;d:16559;b:16838;tap:17247;d:17544;tap:17872;b:18203;d:18500;tap:18821;d:19173;b:19461;d:19886;tap:20192;d:20535;b:20823;d:21174;tap:21496;d:21834;b:22182;d:22563;tap:22826;d:23202;b:23489;d:23871;tap:24168;d:24547;b:24818;d:25191;tap:25495;d:25876;b:26182;d:26507;tap:26821;d:27165;b:27453;d:27869;tap:28165;d:28516;b:28830;d:29185;tap:29482;d:29864;b:30141;d:30514;tap:30818;d:31185;b:31440;d:31841;tap:32155;g:32836;g:33814;d:34173;g:34821;d:35439;g:36463;d:36813;g:37465;d:38105;g:39123;d:39506;g:40161;d:40764;g:41746;d:42151;tap:42502;d:42870;tap:43533;d:43896;tap:44209;d:44537;b:44834;d:45197;tap:45486;d:45874;tap:46233;d:46613;tap:46867;d:47194;b:47469;d:47824;tap:48104;d:48513;b:48809;d:49209;tap:49523;d:49915;b:50154;d:50526;tap:50848;d:51207;b:51520;d:51863;tap:52143;d:52477;b:52781;d:53162;tap:53450;d:53824;b:54160;d:54535;tap:54868;d:55184;b:55435;d:55817;tap:56135;d:56534;b:56802;d:57217;tap:57484;d:57851;b:58156;d:58536;tap:58807;d:59160;b:59463;d:59865;tap:60155;d:60479;b:60783;d:61201;tap:61481;d:61845;b:62133;d:62497;tap:62801;d:63154;b:63441;d:63858;g:64156;d:64731;b:65174;d:65550;tap:65862;d:66214;b:66864;tap:67488;d:68511;b:68854;d:69505;g:70154;d:71145;b:71518;d:72118;g:72784;d:73799;b:74138;d:74830;g:75430;d:76444;g:76820;d:77487;g:78161;d:79143;g:79491;d:80116;g:80787;d:81785;g:82144;d:82793;g:83484;d:84483;g:84829;tap:85190;d:85523;g:86124;d:87142;g:87502;d:88112;b:88809;d:89804;b:90187;d:90794;b:91491;d:92510;b:92877;d:93500;b:94156;d:95127;b:95502;g:95836;d:96171;b:96770;d:97194;tap:97553;d:97920;tap:98200;d:98593;b:98864;d:99207;tap:99520;d:99903;b:100200;d:100559;tap:100864;d:101231;b:101536;d:101878;tap:102175;d:102591;b:102856;d:103215;tap:103528;d:103849;b:104204;d:104538;tap:104828;d:105215;b:105520;d:105863;tap:106125;d:106505;b:106785;d:107177;tap:107474;d:107833;b:108154;d:108514;tap:108794;d:109194;b:109481;d:109890;tap:110154;d:110542;b:110847;d:111198;tap:111502;d:111894;b:112144;d:112533;tap:112805;d:113177;b:113506;d:113833;tap:114138;d:114522;b:114806;d:115189;tap:115489;d:115841;b:116170;d:116514;tap:116810;d:117169;b:117458;d:117858;tap:118193;d:118549;b:118861;d:119204;tap:119493;d:119881;b:120177;d:120528;tap:120816;d:121217;b:121492;d:121833;tap:122120;d:122553;b:122833;d:123213;tap:123485;d:123865;b:124169;d:124550;tap:124847;d:125197;b:125518;d:125882;tap:126178;d:126522;b:126852;d:127194;tap:127441;d:127850;b:128155;b:129137;b:129820;d:130184;b:130805;d:131472;g:132465;d:132845;g:133466;h:134133;g:135114;h:135478;g:136112;h:136884;g:137840;h:138178;d:138853;tap:139419;d:139860;b:140215;d:140549;tap:140829;d:141218;b:141497;d:141899;tap:142179;d:142576;b:142889;d:143254;tap:143524;d:143904;b:144193;d:144557;tap:144828;d:145254;b:145526;d:145848;tap:146152;d:146860;b:147560;d:148460;b:148844;d:149512;b:150171;tap:150920;b:151545;tap:152186;b:152869;tap:153525;b:154207;tap:154857;b:155523;tap:156530;b:156868;tap:157517;b:158155;tap:159130;b:159500;");
            /*31*/ sequences.add("g:786;g:1170;g:1709;g:2201;d:2643;d:3201;d:3761;tap:4234;tap:4694;tap:5226;tap:5751;d:6257;d:6723;d:7276;g:7668;tap:7816;b:7932;d:8258;g:8716;d:9221;tap:9451;h:9691;g:10225;d:10693;tap:11213;h:11451;g:11682;d:12215;tap:12736;g:13445;d:13543;tap:13694;h:14235;g:14683;d:15209;h:15691;g:16233;d:16713;g:17459;d:17723;g:18231;h:18715;g:19215;d:19478;g:19684;h:20197;g:20714;d:21073;h:21733;g:22240;d:22677;g:23238;d:23729;b:23951;d:24190;g:24718;d:24981;tap:25205;d:25492;g:25747;h:26251;g:26733;d:27209;g:27447;d:27718;b:28218;d:28734;tap:29022;h:29476;g:29714;d:29987;b:30216;d:30725;g:30947;tap:31221;b:31448;d:31712;g:31971;tap:32240;b:32720;d:32991;g:33189;tap:33477;b:33691;d:34215;g:34682;h:34945;tap:35185;d:35472;g:35694;b:36181;d:36713;g:36993;h:37223;tap:37756;d:38223;g:38738;g:39218;b:39713;d:39976;g:40199;d:40730;tap:41170;tap:41673;tap:41937;h:42208;h:42752;h:43229;h:43711;d:44202;d:44717;g:45213;d:45717;g:46173;d:46723;g:47196;d:47492;b:47689;d:47969;g:48200;h:48687;tap:48934;d:49233;g:49470;tap:49749;b:50022;g:50662;d:50966;tap:51212;d:51726;g:51965;h:52211;b:52703;g:53183;tap:53483;b:53693;g:54236;d:54739;g:55232;d:55765;g:56182;tap:56749;b:56987;tap:57498;b:57726;h:58215;g:58656;tap:59182;d:59715;b:59963;g:60184;tap:60407;b:61164;tap:61706;h:62219;d:62713;g:63193;d:63678;b:64144;tap:64719;g:65177;d:65457;b:65680;g:66212;d:66729;b:67178;d:67712;g:67951;tap:68214;b:68689;d:69214;g:69453;tap:69733;b:70233;d:70712;g:71180;tap:71451;b:71690;d:72192;g:72724;tap:73249;b:73447;d:73702;g:74237;tap:74737;b:75204;d:75468;g:75682;tap:75978;b:76210;h:76455;tap:76702;d:77212;g:77443;h:77705;tap:78210;d:78490;g:78687;tap:79184;b:79447;d:79718;tap:80192;d:80750;g:81208;d:81487;tap:81718;d:82194;g:82726;tap:83195;g:83483;d:83730;tap:84179;tap:84701;g:85242;d:85505;tap:85695;tap:86191;b:86683;h:87193;g:87448;d:87719;tap:87949;d:88213;b:88703;d:88958;g:89164;d:89503;b:89726;d:90047;g:90237;d:90509;b:90740;b:91235;d:91498;g:91713;b:92241;b:92707;b:93215;b:93711;h:94237;h:94779;h:95247;tap:95744;d:95990;b:96213;d:96739;g:97196;h:97692;b:98213;tap:98493;tap:98764;d:99198;g:99457;h:99741;tap:100224;g:100692;d:101233;b:101690;d:102205;g:102724;d:103234;g:103726;d:104316;g:104733;d:105152;g:105656;d:106174;g:106675;");
            /*3*/ sequences.add("tap:1741;tap:2834;tap:3896;d:4266;g:5126;tap:6893;tap:7740;tap:8398;tap:9177;tap:9580;tap:10002;tap:10744;d:11264;g:11986;d:12325;g:12758;d:13079;g:13489;d:13678;g:13876;d:14065;d:14288;tap:15729;d:15876;tap:17540;tap:18115;tap:18690;tap:19280;d:19562;tap:20282;d:20551;tap:21355;g:22133;d:23233;g:24272;d:24812;g:25330;tap:26671;tap:27764;tap:28853;d:29182;g:29747;");
            /*17*/ sequences.add("tap:875;tap:1075;tap:1439;tap:2076;tap:3994;tap:4194;d:4949;g:5245;d:5509;g:5774;d:6030;g:6337;d:6600;g:6944;d:7200;g:7498;d:7721;g:7977;d:8192;b:8453;d:8806;g:9125;d:9498;g:9795;d:10067;g:10340;d:10604;g:10877;h:11175;b:11464;h:11728;b:11997;h:12261;b:12511;h:12780;d:13080;g:13344;d:13583;g:13880;d:14195;g:14468;d:14724;g:14997;d:15245;g:15536;d:15807;g:16114;d:16394;g:16651;d:16915;g:17163;d:17435;tap:17910;d:18267;tap:19010;tap:19561;tap:20102;tap:20618;tap:21159;tap:21693;h:21827;tap:22293;tap:22827;tap:23362;tap:23866;h:23989;tap:24481;tap:25006;tap:25565;tap:26041;tap:26241;tap:26682;tap:27204;tap:27754;tap:28239;tap:28439;tap:28863;tap:29416;tap:29941;tap:30393;tap:30593;tap:31032;tap:31600;tap:32083;tap:32598;g:32702;d:32983;tap:33726;tap:34253;d:34637;tap:35382;d:35737;g:36288;d:36569;g:36824;d:37073;d:37400;d:37656;d:37920;d:38185;d:38474;d:38746;g:39027;d:39266;b:39539;b:39825;b:40080;b:40347;b:40657;b:40994;b:41257;b:41539;b:41827;b:42114;b:42490;b:42806;b:43102;b:43393;b:43622;h:43903;h:44181;h:44443;h:44726;h:44995;h:45265;h:45564;h:45880;h:46190;h:46446;h:46711;h:46975;h:47239;h:47487;h:47729;h:47969;tap:48398;tap:48598;g:48803;g:49108;g:49398;g:49670;g:49952;g:50231;tap:50636;tap:50836;g:51000;d:51238;g:51520;d:51776;g:52057;d:52329;tap:52812;d:53060;tap:53896;g:54237;tap:54782;tap:55521;d:55860;g:56157;d:56405;g:56662;tap:57177;d:57512;tap:58317;g:58617;d:58890;g:59598;d:60169;tap:60698;tap:60898;g:61036;tap:61534;d:61822;tap:62631;tap:63223;d:63464;g:63812;d:64060;g:64388;d:64677;tap:65131;tap:65331;tap:65651;tap:65851;g:66268;tap:66989;d:67345;g:67912;d:68338;g:68973;d:69254;g:69510;d:69808;h:70166;b:70651;h:71222;g:71748;d:71995;g:72726;d:73306;g:73617;d:73874;g:74122;b:74447;h:74955;b:75515;h:76095;b:76353;h:77081;b:77737;h:77989;b:78226;h:78481;g:78781;g:79332;d:79849;d:80454;d:80997;g:81499;d:82032;d:82351;d:82641;g:83131;d:83649;g:84226;d:84474;g:84781;b:85097;h:85370;b:85647;h:85913;b:86201;h:86446;g:86731;d:86971;g:87211;tap:87757;tap:92101;tap:92978;g:93178;d:93742;tap:96466;g:96855;d:97590;g:98140;b:98908;h:99809;b:100264;tap:100830;d:101155;g:101729;d:102199;tap:103007;tap:103557;tap:104114;tap:104641;g:104917;d:105501;d:106078;d:106629;tap:107356;tap:107917;tap:108485;tap:109037;tap:109562;tap:110112;tap:110679;g:110991;tap:111487;tap:111687;tap:112322;tap:112897;tap:113439;g:113696;tap:114440;tap:115033;tap:115567;d:115897;tap:116678;tap:117233;tap:117770;h:117996;tap:118857;d:119176;tap:119921;tap:120487;tap:121044;b:121437;tap:122090;g:122440;tap:123184;tap:123745;g:124048;tap:124854;tap:125403;d:125723;tap:126484;tap:127067;g:127364;tap:128130;tap:128647;tap:129213;tap:129779;tap:130305;d:130655;tap:131379;b:131698;tap:132477;tap:133010;tap:133594;tap:134115;h:134471;tap:135183;tap:135740;tap:136294;g:136681;d:137191;g:137758;tap:138480;tap:139036;tap:139603;g:139903;d:140414;g:140940;d:141757;d:142471;g:143121;d:143420;g:143701;d:143949;h:144234;b:144750;h:145299;h:146166;h:146891;b:147448;h:147815;b:148033;h:148274;g:148586;d:149193;h:149730;b:150551;h:151233;d:151808;g:152178;d:152433;g:152689;tap:153180;d:153492;g:154094;d:154350;tap:155071;d:155431;g:155676;d:156274;g:156537;d:156794;g:157050;b:157363;h:157912;b:158452;tap:159185;d:159530;tap:160251;g:160596;tap:161405;d:161757;tap:162477;g:162819;tap:163598;d:163954;tap:164673;g:165007;tap:165768;d:166058;b:166664;h:167193;tap:167945;d:168274;tap:168995;h:169322;tap:170099;h:170434;tap:171152;h:171510;g:171819;d:172317;g:173003;d:173653;g:173941;d:174222;tap:175003;d:175447;tap:176141;tap:176604;tap:176804;");
            /*29*/ sequences.add("g:514;tap:903;g:1378;tap:1891;d:2303;g:2711;tap:3152;g:3457;h:3720;g:4153;tap:4603;d:4940;g:5474;tap:6024;g:6501;d:6972;g:7361;tap:7781;g:8029;d:8346;g:8762;tap:9198;g:9536;d:10078;h:10662;g:11122;d:11532;g:11957;tap:12406;h:12686;g:12959;tap:13384;tap:13782;h:14125;g:14641;tap:15232;g:15689;d:16094;tap:16483;h:16839;g:17119;b:17603;b:17776;b:17948;b:18105;b:18311;b:18517;g:18771;tap:19364;g:19915;d:20516;b:20973;tap:21270;g:21653;d:22014;g:22235;d:22649;g:23004;h:23426;tap:23952;h:24578;tap:24995;h:25445;tap:25842;d:26263;g:26551;b:26815;g:27240;b:27628;g:27966;b:28541;tap:29162;g:29604;b:30061;g:30450;b:30838;g:31111;tap:31453;b:31858;d:32271;g:32631;tap:33180;b:33771;tap:34231;g:34665;d:35041;g:35477;tap:35796;b:36068;d:36331;g:36612;g:36751;g:36917;tap:37263;b:37809;d:38367;g:38816;tap:39276;b:39701;d:40089;g:40394;h:40682;tap:41118;g:41458;b:41788;tap:42389;g:43026;b:43459;g:43827;tap:44269;g:44704;b:44983;tap:45287;g:45713;d:46169;g:46475;d:47049;b:47599;tap:48059;g:48464;d:48907;tap:49330;tap:49626;g:49923;b:50358;g:50738;tap:51105;b:51643;d:52125;g:52639;tap:53089;b:53526;d:53885;g:54181;h:54486;tap:54982;g:55351;h:55393;g:55722;tap:56255;g:57139;tap:57384;g:58017;d:58535;g:59385;d:59794;g:60345;h:60879;g:61638;b:61973;g:62606;b:63149;g:63993;b:64382;tap:64940;d:65505;d:66483;h:66723;tap:67228;g:67732;b:68611;tap:68971;g:69545;d:70119;tap:71002;d:71306;d:71988;h:72477;g:73160;tap:73490;g:73671;g:74176;d:74694;b:75228;g:75848;g:76497;tap:77075;d:77683;h:78200;b:78977;g:79399;tap:79958;d:80502;h:81066;b:81650;g:82193;tap:82782;d:83383;h:84010;b:84570;g:85172;tap:85749;d:86326;h:86918;b:87478;g:88021;tap:88648;d:89267;h:89785;b:90353;g:90921;tap:91505;d:92044;b:92605;tap:93216;g:93754;d:94201;tap:94620;g:95024;tap:95454;d:95785;g:96065;b:96497;g:96884;tap:97272;g:97804;d:98381;b:98803;h:99217;g:99607;tap:100028;b:100341;d:100654;g:101047;tap:101499;b:101853;d:102421;g:102947;d:103407;b:103875;tap:104300;g:104702;d:105070;tap:105341;g:105738;b:106127;g:106414;tap:106955;b:107544;d:108039;g:108452;tap:108885;d:109272;b:109535;g:109839;tap:110297;b:110681;g:111052;tap:111610;d:112159;tap:112633;g:113059;b:113442;tap:113933;d:114230;tap:114634;g:115015;b:115365;tap:115708;d:116189;h:116750;d:117242;tap:117663;g:118051;h:118492;d:118851;tap:119115;g:119557;h:119945;b:120313;tap:120931;g:121474;d:121908;tap:122363;h:122703;g:123123;d:123498;b:123763;tap:124151;g:124624;d:124970;tap:125551;h:126080;g:126507;tap:126926;d:127330;tap:127720;g:128019;d:128401;tap:128815;g:129196;d:129555;b:130063;h:130849;g:131162;d:131384;g:131859;d:132407;b:133135;g:133458;tap:133614;d:134143;h:134667;b:135497;g:135811;tap:136016;d:136490;h:137006;b:137709;tap:138048;tap:138338;d:138795;h:139301;b:140032;g:140328;tap:140501;d:141058;h:141624;b:142346;g:142681;tap:142845;d:143337;h:143869;b:144637;g:144933;tap:145106;d:145655;h:146178;b:146953;g:147249;tap:147406;d:147981;h:148541;b:148945;g:149334;tap:149621;d:149787;h:150252;b:150768;g:151242;tap:151725;d:152012;b:152585;g:153151;tap:153569;d:154061;h:154357;b:154905;g:155244;tap:155392;d:155713;h:156203;b:156491;g:156672;tap:157221;d:157802;h:158132;b:158566;g:158854;tap:159035;d:159508;h:159942;b:160384;g:160871;tap:161126;d:161340;h:161824;b:162199;g:162355;tap:162694;d:163116;h:163395;b:163568;g:164067;tap:164385;d:164574;h:164926;b:165378;g:165648;tap:165831;tap:166027;tap:166167;d:166514;g:167061;d:167577;tap:168035;g:168439;tap:168873;g:169267;b:169565;d:169867;g:170280;tap:170706;b:171090;d:171630;h:172160;g:172652;h:173136;tap:173539;d:173982;g:174229;b:174525;tap:174943;g:175336;d:175703;tap:176233;h:176699;g:177257;tap:177691;b:178120;d:178497;g:178828;tap:179123;b:179563;h:179929;g:180304;d:180871;g:181418;d:181891;b:182341;h:182701;tap:183060;g:183356;d:183753;tap:184163;g:184529;d:184906;b:185466;d:186031;tap:186514;h:186956;g:187370;d:187770;b:188078;tap:188354;g:188764;d:189221;b:189568;h:190066;g:190647;d:191089;b:191531;g:191960;tap:192328;h:192693;b:192972;tap:193369;g:193782;d:194120;tap:194627;g:195218;b:195711;d:196099;tap:196564;d:197011;g:197348;h:197612;b:197984;d:198348;tap:198748;g:199299;b:199891;h:200311;tap:200761;d:201210;g:201581;b:201885;d:202174;tap:202612;g:203000;b:203344;h:204151;");
            /*1*/ sequences.add("tap:1250;tap:1691;tap:2188;tap:2523;tap:3049;tap:3880;tap:4449;tap:5040;tap:5531;tap:6145;tap:6585;tap:7081;tap:7590;tap:8051;g:8320;tap:9063;d:9330;tap:10109;g:10366;tap:11087;d:11401;tap:12091;g:12408;tap:13150;d:13420;tap:14165;g:14427;tap:15170;d:15469;tap:16189;g:16479;tap:17199;d:17477;tap:18168;h:18488;tap:19232;h:19601;tap:20200;tap:21285;tap:22290;tap:23853;tap:24811;g:25230;tap:25833;g:26261;tap:26888;tap:27905;d:28263;tap:28866;tap:29888;tap:30977;tap:31995;tap:32927;d:36838;tap:37582;tap:39120;tap:40072;tap:41067;tap:42074;h:42433;tap:43150;d:43397;g:43949;d:44500;tap:45191;tap:49764;tap:50775;tap:51781;tap:52769;tap:54298;tap:55324;tap:56353;tap:57388;d:57745;tap:58348;tap:59423;d:59702;g:60343;d:60857;tap:61432;b:61738;h:62215;tap:62955;tap:63475;tap:64023;tap:64533;d:64713;g:64959;d:65158;tap:65615;tap:66066;tap:66550;tap:67106;tap:67605;d:67792;g:68116;d:68507;g:68739;d:68963;g:69128;tap:69704;tap:70149;b:70407;tap:71117;b:71459;tap:72143;tap:73172;tap:73675;h:73959;tap:74700;tap:75208;d:75574;d:76614;h:77539;tap:78227;tap:79229;tap:81221;tap:81762;h:82087;tap:82829;tap:83873;g:84156;tap:84899;d:85156;tap:85875;h:86116;tap:86890;g:87164;tap:87884;d:88258;tap:88900;g:89588;tap:90424;tap:90924;g:91814;b:92247;h:92762;b:93063;h:93268;b:93512;tap:93999;d:94249;g:94803;d:95084;g:95331;tap:96055;h:96328;b:96812;tap:97544;h:97858;h:99382;tap:100098;g:100465;d:100959;tap:101651;g:102001;tap:102664;d:102931;tap:104194;d:104392;g:104995;d:105418;tap:106172;g:106462;b:107037;h:107550;tap:108235;b:108513;h:109032;b:109280;h:109533;b:109771;tap:110292;d:110524;g:111056;g:112554;tap:113199;d:113386;tap:113961;g:114157;g:115322;d:115587;g:115802;d:116050;g:116247;tap:116876;tap:117419;d:117578;g:117926;tap:118471;h:119273;b:119496;h:119707;d:120538;g:120777;d:120992;g:121290;d:121471;g:121743;d:121942;tap:122458;tap:122984;d:123180;g:123535;d:123824;tap:124543;tap:125048;h:125302;b:125522;h:125975;b:126323;tap:126995;d:127219;g:127678;d:127975;tap:128601;b:128877;tap:129604;h:129878;tap:130618;b:131006;tap:131689;h:132012;tap:132635;d:132924;tap:133645;g:133983;tap:134674;d:134898;b:135545;h:136072;b:136553;tap:137207;tap:137764;tap:138269;tap:138787;d:139035;tap:139753;d:140111;tap:140753;g:140999;d:141508;g:142118;d:142584;g:143164;d:143645;g:144127;g:144699;d:145189;d:145666;g:146183;d:146659;g:147134;d:147661;g:148144;d:148415;g:148705;d:149226;g:150159;d:150422;g:150696;d:151229;g:151776;tap:152494;tap:152991;tap:153539;b:153784;h:154323;tap:155012;tap:155526;tap:156011;tap:156520;tap:157087;tap:157553;tap:158067;tap:158558;tap:159062;");
            /*6*/ sequences.add("tap:1930;d:3587;g:3926;d:4191;g:4479;d:4769;b:5121;h:5448;d:6019;g:6517;d:6823;g:7167;d:7474;g:7747;d:8062;tap:8610;d:9418;g:9754;h:10723;b:11071;h:11976;b:12328;g:13011;d:13454;g:13814;d:14112;tap:14833;tap:15276;d:15406;g:15727;d:15975;g:16676;d:16999;g:17406;d:17687;b:18070;h:18453;tap:19275;tap:20404;g:21237;d:21573;g:21930;d:22557;g:22919;d:23687;b:24203;h:24643;b:25049;h:25543;b:25890;h:26593;b:27141;g:28532;d:29076;g:29382;d:29894;tap:30675;tap:31030;g:31197;d:31562;g:31892;d:32468;tap:33598;g:34131;d:34364;g:34643;d:35309;g:35645;d:35983;h:36267;b:36549;tap:37367;h:37699;tap:38520;b:38750;h:40803;b:41110;h:41456;h:42458;g:42801;d:43122;g:43445;tap:43926;d:44317;g:44686;tap:45723;d:45986;tap:46939;tap:47845;g:48059;d:48433;g:48763;tap:49854;tap:50885;b:51650;h:51986;b:52614;h:53030;b:53332;g:53635;d:54613;g:55593;d:56476;g:57466;d:58421;g:59492;d:60473;b:61244;h:61557;b:61905;h:62443;b:62913;h:63283;b:64267;h:65340;b:66095;g:66462;d:66802;g:67176;d:67440;tap:68337;tap:68687;d:68806;g:69143;d:69432;tap:70591;tap:71601;g:72012;d:72327;g:73296;d:73641;b:74281;h:75230;b:75620;h:76329;b:76682;h:77025;g:77338;d:77758;g:78156;d:78387;b:79207;h:79964;b:80304;h:80943;g:81344;d:81930;g:82767;d:82965;g:83254;tap:84414;g:84580;d:84904;g:85272;g:86607;d:86922;tap:87987;tap:88378;g:88717;d:88965;g:89270;d:89660;g:90572;d:91383;g:93162;");
            /*27*/ sequences.add("tap:647;g:2087;d:4392;g:6254;d:8520;g:10532;tap:12639;g:14583;tap:16882;g:17448;tap:18104;g:18602;tap:19152;g:19675;tap:20232;g:20779;d:21226;g:21767;tap:22324;g:22878;h:23295;g:23858;tap:24399;g:24947;h:25387;g:25952;tap:26471;g:26988;b:27453;d:27965;g:28501;d:29038;h:29527;g:30081;d:30595;g:31141;b:31654;d:32203;g:32718;tap:33247;b:33754;tap:34302;g:34826;tap:35383;b:35892;tap:36388;g:36919;tap:37401;h:37926;g:38492;d:38982;b:39571;tap:40058;g:40599;tap:41111;d:41585;g:42124;tap:42597;b:43167;tap:43658;g:44198;tap:44711;b:45256;tap:45767;g:46324;tap:46843;b:47401;tap:47926;g:48408;d:48899;tap:49435;d:50010;tap:50466;d:50972;g:52469;d:53546;b:54595;d:55613;g:56689;d:57725;b:58744;d:59780;g:60911;d:61906;b:62941;d:64023;b:65030;b:65578;b:66095;b:66366;b:66571;b:66820;g:67161;tap:67756;b:68220;tap:68728;g:69265;d:69776;tap:70297;g:70536;d:70823;b:71335;tap:71851;g:72351;tap:72886;b:73402;d:73914;g:74152;tap:74440;b:74998;b:75288;d:75534;g:76026;tap:76568;b:77086;tap:77591;b:78067;g:78668;b:79183;tap:79641;g:80187;d:80708;tap:81246;d:81760;tap:82317;g:82603;d:83052;b:83340;tap:83612;g:83834;tap:84360;d:84906;tap:85406;g:85944;d:86458;g:87007;d:87533;d:87767;g:88002;d:88486;g:89044;d:89549;b:90115;d:90665;g:90910;tap:91165;b:91647;b:91903;d:92198;g:92689;d:93248;g:93790;d:94334;g:94853;d:95367;b:95807;b:96080;d:96422;g:96880;d:97422;g:97904;d:98450;g:98996;tap:99495;b:100004;g:100520;h:102590;b:104662;d:106768;tap:108831;g:110982;d:113038;g:115129;d:117259;g:117725;d:118292;g:118825;h:119332;d:119870;g:120450;d:120909;b:121466;d:121967;g:122458;d:123040;b:123538;d:124036;g:124560;d:125053;h:125552;g:126118;d:126656;g:127147;b:127684;d:128189;g:128748;d:129255;b:129761;d:130270;g:130832;d:131382;h:131873;g:132387;d:132844;g:133398;tap:133911;b:134427;tap:135004;b:135526;tap:136044;g:136519;d:137081;g:137597;tap:138096;b:138659;d:139157;g:139638;tap:140182;b:140729;tap:141269;g:141774;d:142265;b:142784;tap:143332;g:143857;d:144376;b:144925;tap:145465;g:145955;d:146462;b:146991;tap:147510;g:148051;d:148508;tap:149084;b:149613;tap:150088;g:150629;d:151293;g:152209;b:152717;tap:153496;d:154785;g:155506;tap:155857;d:156836;tap:157611;g:157949;d:158931;tap:159689;g:160010;d:160510;d:161068;d:161632;d:162187;d:162690;d:163215;d:163719;d:164175;d:164456;d:164760;d:165032;tap:165715;tap:165997;d:166283;d:166555;d:166736;d:166892;d:167024;g:167315;tap:167847;b:168359;tap:168848;b:169438;g:169935;tap:170493;b:170975;tap:171483;g:172018;tap:172393;b:172626;tap:173025;g:173576;tap:174150;b:174678;b:175219;tap:175678;g:176233;tap:176729;b:177244;tap:177761;g:178294;tap:178787;b:179216;b:179521;tap:179856;g:180365;tap:180877;g:181410;d:181877;tap:182335;b:182926;tap:183468;g:184017;d:185048;g:185557;tap:186115;tap:186661;d:187191;g:187665;d:188177;g:188752;d:189294;g:189802;d:190302;g:190778;d:191343;g:191873;tap:192137;b:192391;d:192875;g:193430;d:193870;g:194453;d:194937;g:195475;d:196024;g:196533;d:197025;g:197623;d:198089;g:198622;d:199122;g:199654;d:200208;b:200713;");
            /*4*/ sequences.add("tap:2172;tap:2675;tap:3519;tap:4026;tap:4833;tap:5529;tap:6665;tap:6986;tap:9611;tap:10527;tap:11614;tap:12652;tap:13729;tap:14840;tap:15820;tap:17064;d:17910;g:18926;d:19944;g:20956;d:21936;g:22418;d:22958;g:23408;d:23622;g:23853;d:24092;tap:24782;tap:25323;d:25554;g:26078;d:26520;g:26996;tap:27740;tap:28333;tap:29429;d:30110;g:31080;d:32124;tap:33451;g:34239;g:35417;d:36314;tap:37613;g:38314;d:39327;g:40465;tap:41677;tap:42766;d:43585;g:44531;tap:45806;d:46518;g:47639;g:48627;tap:49817;tap:50903;tap:51902;tap:52953;tap:53976;tap:54770;tap:55836;tap:57007;tap:58041;d:58496;g:58744;d:59277;g:59871;d:60280;g:60519;d:60692;g:60948;d:61333;g:61885;d:62360;tap:63091;tap:63871;tap:64230;g:64496;d:64693;g:65002;tap:65603;d:66010;g:66952;d:67975;tap:69323;d:70049;g:70963;tap:72319;tap:73445;d:74099;g:74756;d:75264;tap:76534;tap:77522;tap:78596;tap:79606;tap:80618;d:81360;g:82386;d:84111;tap:87000;");
            /*10*/ sequences.add("tap:1232;tap:2187;tap:2569;g:2836;d:3214;g:3690;tap:4265;d:4506;g:4950;d:5395;g:5878;d:6231;g:6674;d:7047;g:7514;d:7929;g:8364;d:8788;g:9199;d:9641;g:10038;d:10468;g:10903;d:11346;g:11837;b:12248;h:12627;b:13072;h:13492;b:13913;h:14330;b:14796;h:15198;b:15580;h:16019;b:16440;h:16935;h:17366;b:17790;h:18205;b:18631;g:19050;d:19455;g:19921;d:20335;g:20741;d:21159;g:21610;d:22028;g:22437;d:22863;g:23340;d:23737;g:24151;tap:24811;d:25051;g:25461;d:25921;g:26301;d:26728;g:27122;d:27580;d:28043;tap:29132;tap:29973;tap:30480;g:30651;tap:31278;tap:31727;d:32257;tap:32947;tap:33419;tap:33825;g:34008;tap:35097;tap:35522;d:35768;tap:36456;tap:37229;d:37478;tap:38167;tap:39023;tap:39803;tap:40633;g:40874;d:41324;tap:41951;g:42166;tap:42808;d:43057;tap:43684;g:43882;tap:44486;b:44697;tap:45336;h:45576;tap:46239;b:46482;tap:47104;d:47343;tap:47971;d:48177;tap:48780;g:49078;tap:49652;b:49898;tap:50501;h:50809;tap:51409;b:51637;tap:52235;b:52820;tap:53555;g:53754;d:54176;tap:54779;tap:55266;tap:56100;tap:56543;tap:58233;g:58424;d:58875;g:59716;d:60149;g:60591;d:61058;g:61484;d:61873;b:62279;h:62692;b:63163;h:63605;b:64016;h:64458;b:64896;g:65346;d:65735;g:66152;d:66593;g:67004;d:67446;g:67868;d:68262;g:68721;b:69140;h:70017;b:70867;h:71286;b:71692;g:72167;d:72398;g:73083;d:73314;b:73895;h:74124;b:75492;h:75716;g:76921;d:77307;g:77528;d:77856;g:78882;d:79112;g:79368;d:80520;g:80792;d:81032;g:81634;d:82060;g:82457;d:82864;g:83370;b:83767;h:84164;b:84628;h:85059;tap:85866;tap:86765;tap:87386;g:87565;d:88024;g:88475;d:88873;g:89300;d:89719;d:90127;d:90572;d:91014;d:91491;d:91932;d:92331;d:92758;d:93168;d:93595;d:94008;d:94468;d:94886;d:95328;d:95739;g:96132;g:96567;d:96993;g:97427;d:97885;g:98295;g:98770;g:99168;d:99544;g:99955;d:100447;b:100859;b:101641;b:102117;b:102594;b:103092;b:103494;b:103920;b:104361;b:104762;b:105191;h:105585;b:105978;h:106411;tap:107067;g:109598;d:111099;g:111947;d:112405;tap:113067;g:113293;d:113728;g:114187;tap:114790;d:115018;g:115445;d:115867;tap:116493;g:117007;d:117558;tap:118199;g:118440;d:118892;g:119333;tap:119936;d:120530;g:120996;tap:121638;d:121894;g:122347;d:122731;tap:123357;g:123597;d:123961;g:124470;tap:125072;d:125275;g:125736;d:126133;tap:126759;b:127007;h:127455;b:127867;tap:128494;g:128730;d:129180;g:129560;tap:130222;d:130453;g:130814;d:131266;tap:131892;g:132140;d:132558;g:132980;tap:133670;d:133872;g:134287;d:134730;tap:135373;g:135579;d:135998;g:136404;tap:137064;d:137284;g:137741;d:138140;tap:138766;b:139522;h:139895;b:140339;h:140717;g:141167;d:141556;g:142022;g:142870;d:143259;g:143692;g:144638;b:145040;h:145402;b:145836;h:146753;b:147110;h:147556;b:147972;h:148839;g:149314;d:149511;g:149751;d:150561;g:151404;d:151897;g:152287;b:152723;h:153170;b:153596;h:154478;b:154719;h:154961;g:155660;d:156136;g:156350;d:157013;g:157512;d:157884;b:158321;h:158768;b:159106;h:159591;g:160009;d:160406;g:160890;d:161291;b:161694;h:162573;b:163417;h:163880;b:164305;h:164686;g:165125;d:165571;g:166010;d:166449;b:166854;h:167350;b:167729;tap:169193;g:169417;d:169844;tap:170470;d:170689;g:171140;d:171596;tap:172198;g:172406;d:172841;g:173307;d:173740;g:174166;d:174579;g:175019;tap:175621;d:175824;g:176285;tap:176927;d:177158;tap:177761;g:178009;tap:178671;d:178850;g:179334;tap:179936;d:180159;g:180561;tap:181250;d:181441;g:181851;tap:182454;d:182719;g:183162;tap:183823;d:184042;g:184478;tap:185052;d:185272;g:185694;d:186145;g:186572;tap:187199;d:187431;g:187847;d:188273;g:188717;tap:189407;");
            /*16*/ sequences.add("tap:1720;d:2214;d:2804;g:3301;tap:4019;d:4338;tap:5080;d:5444;tap:6132;tap:7152;d:7500;tap:8143;tap:8677;d:8947;g:9160;tap:9706;d:10095;tap:10783;d:11173;h:11654;tap:12315;d:12661;g:13200;tap:13827;d:14167;tap:14826;d:15277;g:15824;tap:16466;d:16569;tap:17026;d:17248;g:17480;tap:17996;d:18360;tap:18985;g:19387;d:19919;tap:20609;tap:21116;d:21453;h:21911;tap:22716;b:23814;h:24176;b:24492;tap:25219;d:25552;g:25847;tap:26314;tap:27201;d:27631;g:27885;d:28190;tap:28853;tap:29357;d:29687;g:30218;d:30824;tap:31427;b:31837;h:32313;tap:32978;tap:33470;d:33818;g:34339;tap:35584;d:36289;g:36544;d:36979;tap:37669;d:38010;g:38503;b:39111;tap:39722;tap:40738;d:41067;g:41329;d:41560;b:42105;h:42362;b:42675;tap:43798;tap:44099;tap:44299;tap:44641;tap:44841;tap:45452;tap:45948;d:46049;g:46280;b:46709;h:47295;b:47813;d:48323;g:48824;d:49331;g:49954;d:50404;tap:51094;h:51461;tap:52097;tap:52297;tap:52657;tap:52857;tap:53212;d:53562;g:54068;d:54589;tap:55250;b:55620;d:56338;g:56577;d:56891;g:57154;tap:57836;tap:58036;tap:58424;h:58652;b:58958;h:59225;tap:60319;tap:60627;tap:60934;tap:61241;tap:61667;tap:61867;d:62349;g:62619;d:62891;tap:63552;tap:64460;tap:64822;tap:65022;tap:65364;tap:65564;tap:65891;tap:66091;tap:66368;tap:66568;d:66928;g:67189;tap:67671;d:67999;tap:68720;g:69018;d:69581;g:70055;b:70382;h:70596;tap:71335;d:71508;tap:72344;tap:73328;d:73744;tap:74404;tap:74897;d:75152;g:75408;tap:75953;d:76276;tap:76904;g:77314;d:77773;tap:78493;tap:78760;tap:78960;d:79384;g:79639;d:79960;tap:80651;h:80852;tap:81599;h:81965;tap:82647;tap:82847;h:83031;tap:83639;tap:83839;g:84011;tap:84701;b:85137;tap:85759;h:86119;tap:86805;d:86923;tap:87339;g:87655;d:87885;tap:88326;g:88681;tap:89343;b:89750;h:90019;tap:90732;d:90934;tap:91450;d:91787;g:92365;d:92815;tap:93419;d:93824;tap:94274;tap:95523;d:95904;g:96175;d:96439;tap:97129;tap:97329;h:97473;tap:98444;tap:99662;d:99998;g:100278;tap:100744;d:101537;tap:102299;tap:102499;tap:102878;tap:103374;d:103731;g:104027;d:104234;g:104480;d:104786;g:105074;tap:105896;tap:106096;d:106501;g:106755;d:107027;g:107323;tap:108015;d:108532;g:108966;d:109334;tap:110032;tap:110232;tap:110567;tap:110767;tap:111085;tap:111285;tap:111586;tap:111786;tap:112141;d:112499;g:113045;d:113562;d:114361;g:115101;d:115577;h:115858;tap:116314;h:116580;tap:117298;b:117639;tap:118279;tap:118547;tap:118747;tap:119070;tap:119270;tap:119596;tap:119796;tap:120123;tap:120323;d:120485;g:120757;d:120971;tap:121446;h:121759;b:121993;tap:122434;tap:122634;tap:122970;tap:123170;tap:123528;tap:123728;tap:123981;tap:124181;tap:124507;tap:124707;d:124911;g:125174;d:125413;tap:126556;tap:126756;tap:127127;tap:127380;tap:127580;tap:127905;tap:128105;tap:128399;tap:128599;d:128770;g:129041;d:129247;g:129536;d:129854;g:130126;tap:130687;tap:130887;tap:131246;tap:131446;tap:131797;tap:131997;tap:132298;tap:132498;b:132877;h:133135;b:133358;tap:133870;d:134165;g:134601;d:135322;g:135512;d:136005;g:136211;tap:136931;d:137296;g:137583;d:137822;g:138145;d:138359;tap:139020;d:139400;g:139663;d:139968;h:140411;b:140632;tap:141132;h:141404;b:141717;h:141988;b:142543;tap:143154;d:143518;g:143772;d:144036;g:144559;d:144756;tap:145281;d:145575;g:145830;d:146078;b:146571;tap:147286;h:147610;b:147881;h:148828;tap:149388;b:149697;h:149947;tap:150445;d:150744;g:151024;tap:151498;d:151796;g:152051;d:152549;g:152779;tap:153257;tap:153457;d:153854;g:154116;d:154355;tap:155564;d:155945;g:156511;d:156912;tap:157405;tap:157605;d:157946;g:158208;d:158513;tap:159722;b:160159;h:160411;b:160861;h:161112;tap:161781;b:162152;h:162428;b:162668;tap:163766;d:164209;g:164447;d:164941;g:165221;tap:165911;d:166184;tap:166927;tap:167954;tap:168154;tap:168480;tap:168680;tap:168990;tap:169190;tap:169507;tap:169707;tap:170058;tap:170258;g:170421;d:170611;tap:171126;tap:172112;tap:172312;d:172698;g:173245;d:173466;tap:174157;d:174602;g:175037;d:175494;tap:176213;tap:176489;tap:176689;tap:177030;tap:177230;tap:177549;tap:177749;tap:178062;d:178159;g:179215;d:179549;b:180246;h:180619;d:181302;g:181823;d:182037;g:182342;d:182743;tap:183487;h:183875;tap:184494;tap:184780;tap:184980;tap:185321;tap:185521;tap:185864;tap:186064;tap:186325;tap:186525;g:186729;d:186927;b:187457;tap:188615;tap:188815;tap:189180;tap:189455;tap:189655;tap:189972;tap:190172;d:190285;g:190590;d:190812;tap:191789;h:192160;tap:192774;tap:192974;tap:193341;tap:193541;tap:193892;tap:194092;tap:194411;tap:194611;d:194764;g:195044;d:195258;tap:195977;h:196229;b:196766;h:197271;tap:197746;tap:197946;g:198118;d:198339;g:198611;d:198850;g:199147;d:199344;h:199625;b:199852;tap:201075;d:201903;g:202718;d:203141;");
            /*18*/ sequences.add("tap:2632;tap:3204;tap:3776;tap:4261;tap:4710;tap:5201;tap:5716;d:5872;tap:6632;tap:7131;tap:7620;g:7805;tap:8548;tap:9031;tap:9531;d:9732;tap:10452;tap:10961;tap:11427;g:11676;tap:12368;tap:12907;tap:13356;d:13536;tap:14314;tap:14824;tap:15298;d:15538;tap:17685;d:18420;tap:19199;g:19471;d:19922;tap:21032;tap:22008;tap:22962;g:23705;d:24621;tap:25366;d:25631;d:26580;g:27523;d:28495;g:29446;d:30338;g:31343;d:32274;d:32563;tap:33110;g:33357;tap:34022;d:34268;tap:34989;h:35319;tap:35971;h:36204;tap:36871;h:37138;tap:37828;b:38145;tap:38865;b:39091;tap:39827;b:40061;tap:40697;h:40956;tap:41679;g:41936;tap:42683;g:42995;tap:43641;g:43892;tap:44536;d:44842;g:45356;d:45742;tap:46473;g:46761;tap:50598;g:52307;d:54737;g:55433;d:55689;g:56141;b:56783;h:57290;b:57548;tap:59031;tap:60417;g:62629;d:63118;g:63621;b:64548;h:64990;tap:65702;tap:66218;tap:66702;tap:67144;tap:67629;tap:68087;g:68393;d:68616;g:68856;d:69095;b:69814;tap:70534;tap:70990;tap:71464;tap:71981;tap:72434;g:72715;d:72946;g:73219;tap:73884;tap:74350;tap:74843;tap:75307;h:75630;tap:76289;tap:76753;b:77063;tap:77677;d:78528;g:79770;d:80354;tap:81074;d:81338;tap:82032;d:82233;tap:82934;g:83231;tap:83952;g:84233;tap:84878;d:85193;tap:85885;b:86110;tap:86781;b:86980;tap:87716;b:87981;tap:88733;h:88956;tap:89666;tap:90176;tap:90680;h:90943;tap:91651;tap:92110;tap:92613;b:92908;tap:93621;tap:94017;tap:94535;tap:94984;g:95233;d:95722;g:95961;b:96259;h:96707;b:97175;h:97652;h:98121;h:98615;b:99112;b:99580;b:100083;g:100496;tap:101218;d:101465;tap:102211;d:102426;tap:103147;tap:103644;g:103918;tap:104619;tap:105150;d:105362;tap:106007;tap:106493;b:106773;tap:107465;h:107759;tap:108410;tap:108917;d:109209;g:109677;d:111703;g:112984;tap:113685;g:113951;tap:114652;d:114960;h:115410;tap:116105;h:116403;tap:117027;h:117273;tap:117968;h:118269;tap:118940;h:119262;tap:119900;h:120202;tap:120835;h:121196;tap:121799;h:122119;tap:122768;h:123080;tap:123703;b:123983;tap:124706;b:125042;tap:125645;tap:125845;d:126196;g:126501;tap:127138;d:127388;tap:128090;g:128363;tap:129034;d:129299;tap:130030;b:130223;tap:130945;g:131245;tap:131880;h:132135;tap:132815;d:133053;tap:133815;tap:134015;tap:134348;d:134642;tap:135246;g:135511;tap:136203;d:136540;tap:137143;g:137459;tap:138122;b:138446;tap:139075;h:139368;b:139788;d:140385;tap:141076;d:142723;g:144615;b:146784;h:147713;d:148919;g:149472;d:149719;b:150838;h:152779;b:153082;g:155317;d:155568;b:156543;h:157067;tap:157805;d:158109;tap:158802;tap:159305;tap:159832;tap:160257;g:160516;tap:161206;tap:161699;tap:162182;tap:162673;tap:163156;tap:163624;tap:164134;d:164449;tap:165076;tap:165601;tap:166081;tap:166540;tap:166982;g:167256;tap:167920;tap:168409;h:168740;tap:169368;tap:169861;b:170126;h:170617;b:171091;h:171607;tap:172221;tap:172664;tap:172864;tap:173236;tap:173725;tap:174184;d:174289;g:174503;tap:174883;tap:175083;g:175204;d:175427;g:175658;tap:176055;tap:176255;tap:176533;tap:176733;tap:177013;tap:177213;tap:177498;tap:177698;tap:178001;tap:178201;tap:178478;tap:178678;tap:178950;tap:179150;tap:179412;tap:179612;tap:179905;tap:180105;tap:180412;tap:180899;tap:181409;tap:181888;tap:182377;tap:182828;g:183077;tap:183768;d:184075;tap:184704;tap:185200;tap:185714;d:185908;g:186222;d:186470;g:186964;b:187437;h:187871;b:188074;h:188387;h:188867;h:189372;h:189806;h:190321;h:190778;g:191286;d:191527;g:191749;d:191957;g:192630;d:193165;g:193641;d:194122;g:194616;d:195205;g:195623;d:196098;g:196572;d:197087;h:197502;b:197708;h:197974;tap:198656;tap:199152;tap:199669;tap:200107;tap:200582;tap:201067;tap:201517;tap:202033;b:202335;h:202786;b:203735;g:205683;d:206309;g:206632;d:206947;g:207245;b:207532;h:207883;g:210557;d:210813;g:211364;d:211738;g:212189;d:212395;g:212684;b:212973;h:213347;b:213668;h:214030;b:214301;h:214597;g:214927;d:215263;g:215881;tap:219306;g:219583;tap:220247;g:220502;tap:221225;d:221496;tap:222160;d:222428;tap:223120;b:223421;tap:224084;g:224335;tap:225027;h:225252;tap:225937;d:226244;tap:226966;b:227229;tap:227913;g:228153;tap:228913;h:229149;tap:229836;d:230061;tap:230752;b:231033;tap:231749;g:232075;tap:232704;d:233064;g:233482;d:233772;tap:234434;tap:235043;tap:235585;tap:236114;tap:236584;tap:237052;g:237343;d:237565;g:237805;tap:238498;tap:238986;tap:239470;tap:239947;tap:240455;tap:240921;h:241191;b:241425;h:241654;b:241873;tap:242321;tap:242818;tap:243314;d:243623;tap:244315;g:244505;tap:245249;tap:245724;tap:246197;tap:246707;tap:247197;tap:247640;d:247881;g:248146;d:248376;b:248647;h:248874;b:249113;h:249350;tap:249748;tap:249948;d:250271;tap:251018;tap:251443;tap:251949;g:252190;tap:252855;tap:253055;h:253181;b:253405;tap:253856;g:254122;d:254643;d:255141;d:255602;tap:256331;h:256587;b:256794;h:257022;tap:257676;g:257956;g:258533;g:258968;g:259429;d:259927;tap:260629;h:260878;b:261120;tap:261582;tap:262045;tap:262541;tap:263038;tap:263483;d:263756;g:264207;d:264447;g:264695;tap:265396;b:266190;h:267055;d:267971;g:268267;d:268498;g:268763;b:269452;h:269934;b:270890;d:271850;g:272130;d:272370;g:272610;b:273252;h:273602;b:274271;h:274503;d:275108;g:275724;d:276207;g:276463;b:277710;");
            /*28*/ sequences.add("tap:710;g:2042;d:3402;g:4858;d:6426;g:8015;d:9726;b:10968;b:11208;d:12821;g:13213;d:13680;g:14101;d:14539;g:14934;d:15338;g:15765;d:16161;g:16564;d:16940;g:17320;d:17676;g:18122;d:18526;g:18963;d:19372;g:19780;d:20157;g:20549;d:20932;g:21325;d:21724;g:22137;d:22552;g:22946;d:23339;g:23739;d:24110;g:24560;d:24881;g:25320;b:25728;d:26144;b:26578;d:27035;b:27402;d:27782;d:28410;b:28574;d:28970;d:30161;b:30581;d:31201;d:31757;d:31940;b:32178;d:33411;b:33800;d:34951;d:35166;b:35387;d:36593;b:36986;d:37783;d:38178;g:38554;d:38980;g:39366;d:39818;g:40190;h:40528;g:40969;d:41405;h:41759;g:42192;h:42605;g:42985;d:43361;g:43758;h:44125;g:44543;d:44911;g:45361;h:45720;g:46154;d:46547;g:46939;h:47307;g:47716;d:48113;g:48533;h:48918;g:49351;d:49743;g:50156;h:50531;g:50957;d:51334;g:51748;d:52212;g:52533;d:52968;tap:53566;g:54164;d:54565;g:55178;d:55384;tap:56162;g:56524;d:56990;tap:57364;d:57762;g:58156;tap:58558;d:58992;g:59362;d:59768;tap:60174;d:60584;tap:61365;d:61754;tap:62161;d:62546;g:62935;tap:63316;tap:63713;b:64106;d:64913;g:65363;d:65757;d:66356;d:66780;d:66952;b:67334;tap:67982;g:68548;tap:68964;b:69553;d:70028;d:70176;d:70601;b:71175;tap:71576;g:71765;d:71995;b:72168;tap:72391;g:72564;d:72778;b:72977;tap:73198;g:73347;d:73765;b:74120;tap:74358;g:74541;d:74737;b:74927;g:75324;tap:75763;b:76101;d:76526;b:76942;d:78573;g:79353;tap:79741;b:80153;d:81040;g:81352;h:81736;g:82364;d:82577;tap:82775;d:83006;b:83303;h:83975;d:84539;tap:84906;h:85562;g:85760;tap:86164;b:86523;d:87146;g:87372;h:87749;b:88108;d:88532;g:88916;tap:89333;b:89697;tap:90972;g:91376;d:92189;tap:92565;h:92961;tap:93554;d:93757;g:94171;tap:94522;b:95359;tap:95755;g:96159;d:97377;g:97791;d:98607;tap:98975;d:99402;tap:99785;d:100189;g:100578;tap:100785;b:100990;d:101336;tap:101709;tap:102152;g:102547;d:104161;b:105718;h:107323;g:108936;d:110565;b:112162;h:113728;b:115357;b:115759;b:116184;b:116527;d:116968;g:118545;h:119781;g:120170;tap:121775;h:122983;g:123371;h:124138;h:124552;b:125001;d:126142;g:126572;d:127359;tap:127735;b:128139;d:129388;b:129756;d:130173;b:130557;d:130998;b:131381;d:131781;b:132148;d:132936;b:133756;d:134519;b:135324;d:135757;b:136152;d:136569;b:136982;d:137790;b:138546;d:139351;b:140163;g:140920;d:142145;b:142566;d:143247;g:143411;tap:143726;d:144120;g:145012;d:145371;b:145759;g:146522;d:146926;b:147347;d:148126;g:148536;d:148932;tap:149761;d:150173;g:150585;d:151356;b:151770;d:152129;g:152542;d:152962;g:153324;b:153760;d:154140;g:154532;d:154980;tap:155348;h:155781;g:156173;d:156533;tap:156964;d:157406;g:157802;h:158132;b:158559;d:158910;g:159344;tap:159761;b:160128;tap:160539;g:160918;d:161344;g:161728;g:162088;g:162512;tap:162979;d:163413;g:163593;d:163758;g:163881;d:164013;g:164137;d:164288;g:164400;d:164549;g:164672;d:164838;g:164971;d:165110;g:165233;d:165389;g:165521;d:165661;g:165771;d:165900;g:166048;d:166172;b:166538;tap:167322;g:167731;h:168164;d:168814;g:168970;d:169330;b:169727;h:170332;g:170545;d:170913;g:171303;b:171711;g:172137;b:172554;d:172926;g:173542;d:173772;g:174169;h:174565;b:174961;d:175346;g:175752;d:176160;b:176753;d:176990;g:177382;tap:177755;g:178172;b:178544;g:178895;d:179337;b:180985;d:181651;g:181788;d:182185;h:182561;g:183151;d:183356;g:183779;d:183976;b:184173;h:184795;g:184959;d:185336;tap:185500;h:185750;b:186386;d:186557;g:186963;d:187135;tap:187325;d:187776;g:188144;h:188571;g:188959;d:189351;g:189782;d:190216;b:190578;d:190808;g:190949;d:191138;g:191666;d:191844;b:192143;d:193355;b:193769;d:194337;g:194518;d:194944;h:195384;g:195968;d:196149;g:196567;b:196971;d:197377;g:197736;d:197983;b:198123;g:198513;d:199186;g:199359;h:200174;g:200567;d:200976;b:201364;h:201562;g:201792;d:202189;b:202589;h:202965;g:203384;d:203779;tap:204180;h:204378;g:204552;b:204932;d:206163;g:206591;h:206963;b:207382;d:207799;h:208163;d:208585;g:208939;d:209389;b:209773;tap:209995;g:210177;d:210516;g:210946;b:211356;d:211757;g:212149;h:212537;b:212963;d:213378;d:213813;g:214144;d:214519;g:214939;d:215398;b:215795;h:216026;g:216181;d:216478;b:216929;d:217325;g:217450;h:217998;b:219362;g:220904;h:222543;b:224094;h:225699;g:227310;h:228911;");
            /*2*/ sequences.add("tap:1329;tap:2182;tap:3077;tap:3949;tap:4735;d:5497;g:6237;tap:7305;tap:8177;tap:9031;d:9693;g:10487;tap:11556;tap:12460;tap:13311;d:13968;tap:14627;d:14794;tap:15419;d:15674;tap:16276;g:16550;tap:17269;g:17453;tap:18082;g:18321;tap:18946;tap:19751;g:19979;tap:20669;d:20830;tap:21456;d:21719;tap:22323;tap:23146;d:23394;tap:24055;g:24275;tap:24912;g:25193;tap:25768;tap:26600;d:27293;tap:27931;tap:28748;tap:29588;tap:30138;tap:30463;tap:31282;d:31555;g:31981;d:32431;g:32850;d:33284;g:33718;d:34116;g:34542;d:34960;g:35428;d:35817;g:36218;tap:36872;d:37086;g:37544;d:37986;g:38766;d:39235;g:39660;d:40570;g:41399;tap:42073;tap:42970;tap:44168;tap:45856;tap:46818;tap:47595;tap:48443;tap:49309;tap:50154;tap:51046;tap:51892;tap:52768;tap:53629;tap:54499;d:55120;g:55949;d:56362;g:56846;tap:57899;d:58518;g:58955;d:59377;tap:60421;tap:61330;d:61942;d:62844;tap:63897;g:64559;tap:65641;tap:66525;g:67153;tap:67757;d:67973;tap:68608;g:68828;tap:69913;d:70579;tap:71591;tap:72000;d:72298;g:73123;d:74029;tap:75029;tap:75915;tap:76782;tap:77673;tap:78475;tap:78927;tap:80202;tap:81899;g:82550;d:83387;g:84629;d:85052;g:85526;tap:86217;tap:87064;tap:88371;tap:88837;tap:89632;d:90274;tap:91716;tap:92165;g:92772;tap:93931;d:94511;tap:95620;tap:96076;g:96271;tap:96917;d:97132;tap:97793;tap:98226;d:98451;g:98841;d:99282;tap:99908;tap:100324;g:100587;d:101027;tap:101653;tap:102118;g:102288;d:102701;tap:103327;tap:103758;tap:104203;tap:105466;tap:105931;tap:107076;tap:107618;tap:108919;tap:109319;d:109606;g:109993;tap:111056;tap:112374;g:112558;tap:113221;tap:113645;d:114306;tap:114908;tap:115815;d:116022;g:116406;tap:117070;tap:117923;tap:118748;tap:119172;d:119400;g:119838;tap:120464;tap:121164;tap:121803;d:122411;g:122870;d:123270;tap:123914;tap:124764;tap:125994;tap:126477;d:126746;g:127164;d:127580;d:128448;d:129302;d:130164;tap:130738;g:131350;g:132271;g:133177;g:133947;tap:135037;tap:135909;tap:136772;tap:137208;tap:137639;tap:138520;tap:139192;tap:139717;tap:140190;tap:141085;d:141674;g:142604;d:143316;tap:144006;tap:144485;tap:145188;tap:146616;tap:147064;d:147318;g:147731;tap:148767;tap:149622;d:150179;g:151053;tap:151819;tap:152160;tap:153012;tap:153521;tap:153890;d:154130;g:154550;tap:155644;tap:156505;tap:157368;g:157565;d:157972;tap:158617;tap:159046;g:159338;d:160168;tap:160750;tap:161185;g:161418;tap:162515;tap:163364;tap:164196;tap:165049;d:165663;g:166568;d:167383;g:167796;tap:168460;tap:168876;d:169105;tap:169765;tap:170197;g:170485;tap:171113;tap:171499;g:171710;d:172125;tap:172750;tap:173198;g:173479;d:173884;tap:174487;tap:174917;g:175116;d:175574;tap:176234;tap:176633;g:176932;d:177316;d:178147;");
            /*13*/ sequences.add("tap:1141;tap:1542;tap:1742;tap:2152;tap:2352;d:2643;g:2969;d:3167;g:3432;d:3705;tap:4206;tap:4406;tap:4881;g:5131;d:5378;g:5577;d:5841;g:6139;tap:6640;tap:6840;tap:7255;g:7429;d:7748;g:7931;d:8220;g:8518;tap:9073;tap:9273;tap:9671;g:9875;d:10186;g:10392;d:10641;g:10939;tap:11484;tap:11747;tap:11947;g:12219;d:12499;g:12722;d:12986;g:13369;tap:13952;tap:14220;tap:14420;tap:14935;tap:15135;tap:15384;tap:15670;tap:15870;g:16065;d:16347;g:16620;tap:17341;tap:17798;tap:17998;tap:18387;tap:18672;tap:18872;tap:19277;g:19466;d:19789;g:19987;d:20227;g:20517;tap:21042;tap:21242;tap:21724;tap:22146;tap:22346;tap:22543;tap:22743;g:23252;d:23516;g:23814;tap:24507;tap:25240;g:25700;d:25956;g:26244;tap:26872;tap:27633;tap:27833;g:28054;d:28319;g:28660;tap:29274;tap:29755;tap:29955;tap:30357;tap:30557;d:30746;g:31027;tap:31692;tap:32182;tap:32382;tap:32685;tap:32885;h:33139;b:33394;tap:34051;tap:34511;tap:34711;tap:35097;tap:35297;b:35550;h:35818;tap:36503;tap:36703;tap:36957;tap:37157;tap:37559;tap:37759;d:37922;tap:38901;tap:39112;tap:39312;tap:39665;tap:39865;g:40070;d:40334;g:40631;tap:41311;tap:41511;tap:41716;tap:41916;g:42131;d:42379;tap:42912;tap:43112;g:43496;d:43734;g:43957;d:44205;tap:44780;tap:45134;tap:45334;tap:45689;b:45830;h:46171;b:46382;h:46647;b:46865;tap:47415;tap:47615;g:48289;d:48570;g:48793;d:49033;g:49346;tap:49852;tap:50052;tap:50472;g:50731;d:50971;g:51194;d:51425;g:51673;tap:52265;tap:52465;tap:52871;g:53091;d:53348;g:53545;d:53785;g:54051;tap:54624;tap:54926;tap:55126;d:55474;g:55773;d:55962;g:56210;d:56458;tap:57005;tap:57326;h:57838;b:58178;h:58362;b:58631;h:58883;g:59193;d:59456;g:59703;tap:60449;tap:60950;tap:61150;tap:61507;d:61640;g:61920;d:62193;tap:62885;tap:63357;tap:63663;tap:63940;tap:64140;g:64304;d:64561;tap:65275;tap:65475;tap:65747;tap:66094;tap:66294;d:66482;g:66730;d:66978;h:67529;b:67822;h:67968;b:68240;h:68548;g:68944;d:69215;g:69596;d:69908;g:70197;d:70388;h:70813;h:71220;g:71505;g:71710;g:72022;d:72340;d:72642;d:72831;g:73176;g:73711;g:74103;d:74708;g:75341;d:75946;g:76497;b:77087;h:77400;b:77580;h:77830;b:78092;tap:78657;tap:78981;tap:79181;d:79475;g:79819;d:80386;g:81338;d:81635;tap:82179;b:82457;h:82709;b:82917;tap:83435;tap:83635;tap:84014;d:84271;g:84647;d:84911;g:85429;d:85710;tap:86896;tap:87096;b:87195;h:87442;b:87717;tap:88280;tap:88575;tap:88775;h:89122;b:89404;h:89612;b:89853;h:90091;tap:90653;tap:90853;tap:91258;g:91453;d:91700;g:91941;d:92189;tap:92704;tap:93124;tap:93324;tap:93648;g:93886;d:94182;g:94397;d:94670;g:94942;b:95246;h:95525;tap:96446;h:96603;b:96826;h:97073;b:97379;tap:97914;tap:98230;tap:98551;h:98690;b:98959;h:99136;b:99407;h:99673;tap:100206;tap:100406;tap:100847;d:101053;g:101354;d:101533;g:101806;b:102121;tap:102703;tap:102903;tap:103285;h:103503;b:103791;h:103978;b:104208;h:104453;tap:104923;tap:105258;d:105746;g:106124;d:106338;g:106636;d:106900;tap:107462;tap:107662;tap:108068;d:108260;g:108575;d:108782;g:109038;d:109311;h:109603;b:109870;tap:110869;tap:111069;b:111194;h:111456;b:111708;tap:112224;tap:112424;tap:112856;d:113122;g:113370;d:113593;g:113849;d:114155;tap:114663;tap:114863;tap:115249;g:115514;g:116031;g:116319;d:116541;g:116806;tap:117331;tap:117648;d:117893;g:118181;d:118396;g:118644;b:118913;tap:119484;tap:119769;tap:119969;d:120303;g:120558;d:120798;g:121063;d:121360;tap:121865;tap:122065;tap:122455;h:122683;b:122960;h:123133;b:123398;h:123693;tap:124226;tap:124426;b:125047;h:125387;b:125585;h:125881;b:126135;tap:126672;tap:126980;tap:127291;d:127503;g:127756;d:127970;g:128293;d:128549;tap:129082;tap:129282;tap:129647;g:129888;d:130216;g:130422;d:130678;g:130943;tap:131476;tap:131676;tap:132065;h:132298;b:132581;h:132782;b:133029;h:133293;tap:133795;tap:133995;tap:134945;tap:135502;tap:136095;tap:136679;tap:136988;tap:137188;tap:137953;tap:138464;tap:138664;tap:139459;tap:139778;tap:140336;tap:140889;tap:142130;tap:142768;tap:143195;tap:143497;tap:143697;tap:144169;tap:144369;h:144610;b:144852;tap:145423;tap:145919;tap:146331;tap:146638;tap:146838;tap:147557;tap:148167;tap:149033;tap:149233;tap:149962;tap:151320;tap:151738;tap:152400;tap:152833;tap:153033;tap:153413;tap:153819;tap:154019;");
            /*12*/ sequences.add("tap:1023;tap:2449;g:2665;d:3022;g:3254;d:3593;g:3809;d:4023;tap:4685;g:4821;d:5044;g:5326;d:5541;g:5747;d:6248;g:6617;b:6902;h:7239;b:7591;h:8025;b:8412;h:8642;b:8935;tap:9554;g:9803;tap:10407;d:10635;g:11128;d:11588;g:11998;tap:12689;d:12904;tap:13547;g:13768;d:14182;tap:14873;g:15113;tap:15740;d:15981;tap:16643;g:16939;tap:17582;d:17805;tap:18433;g:18711;tap:19339;d:19596;tap:20200;g:20506;tap:21133;d:21781;d:22250;d:22735;d:23136;d:23521;g:23980;g:24474;g:24897;g:25356;g:25783;h:26686;h:27120;h:27588;h:27935;b:28376;b:28875;b:29347;b:29794;d:30209;d:30718;d:31195;d:31568;d:31950;g:32406;g:32930;g:33388;g:33823;d:34220;d:34678;d:35121;d:35555;tap:36216;g:36420;tap:37111;d:37372;tap:38000;b:38229;tap:38857;h:39100;tap:39723;b:39993;tap:40681;h:40877;tap:41497;b:41784;tap:42443;h:42641;tap:43328;d:43583;tap:44187;d:44445;tap:45106;g:45344;tap:46034;tap:46415;tap:46907;tap:47365;g:47569;tap:48231;h:48475;tap:49098;d:49339;tap:50001;b:50244;tap:50905;g:51120;tap:51811;h:52019;tap:52643;d:52870;tap:53531;b:53784;tap:54407;g:54636;d:55143;g:55593;d:56019;g:56461;d:56894;tap:57584;g:57755;g:58315;g:58742;tap:59369;d:59601;g:59979;d:60428;tap:61090;b:61322;tap:61980;g:62216;d:62693;g:63137;tap:63797;tap:64216;d:64435;g:64913;tap:65516;tap:65974;d:66199;g:66625;tap:67344;tap:67793;d:68010;g:68452;d:68894;tap:69585;g:69793;b:70288;h:70687;b:71088;tap:71742;d:72015;g:72501;d:72911;b:73354;h:73781;b:74211;h:74627;tap:75342;d:75580;g:76008;d:76449;g:76859;d:77278;tap:77998;d:78212;tap:78873;d:79139;tap:79766;d:79978;g:80429;d:80896;g:81323;d:81729;tap:82390;d:82672;tap:83315;tap:83790;tap:84232;tap:84681;tap:85130;tap:85564;b:85796;h:86214;b:86679;h:87104;tap:87725;g:87976;d:88475;g:88928;d:89363;g:89815;d:90283;g:90681;b:91162;h:91569;b:92014;h:92463;tap:93097;g:93277;tap:93978;d:94227;tap:94891;g:95107;tap:95770;d:95978;tap:96650;d:96942;tap:97570;d:97794;tap:98457;d:98687;tap:99299;d:99625;tap:100200;g:100462;tap:101098;g:101395;tap:102007;g:102253;d:103070;g:103554;d:104024;g:104431;d:104874;g:105131;d:105354;tap:106017;h:106230;b:106706;tap:107332;g:107541;d:107926;g:108175;d:108423;tap:109115;d:109357;g:109818;d:110221;g:110476;d:110726;tap:111329;tap:111797;tap:112233;d:112482;d:112950;d:113404;d:113864;g:114295;g:114731;g:115151;g:115586;b:116028;b:116418;b:116923;b:117334;h:117785;h:118251;h:118702;h:119119;d:119553;d:120031;d:120455;d:120882;b:121339;b:121810;b:122293;b:122716;b:123198;tap:124203;tap:124653;g:124870;d:125355;g:125805;d:126208;tap:126870;g:127119;tap:127779;d:128013;tap:128704;g:128903;tap:129594;d:129794;tap:130454;g:130692;tap:131319;d:131564;tap:132256;g:132484;tap:133111;d:133318;tap:134009;g:134242;tap:134904;d:135152;tap:135756;b:135997;tap:136685;h:136906;tap:137530;b:137748;tap:138385;h:138655;tap:139295;d:139626;tap:140253;g:140472;tap:141099;d:141373;g:141922;d:142292;tap:142865;g:143140;tap:144677;g:144890;tap:145581;d:145817;tap:146444;g:146693;tap:147383;d:147579;tap:148208;g:148484;tap:149146;d:149362;tap:149990;b:150204;tap:150891;h:151136;tap:151739;b:151984;tap:152584;h:152915;tap:153541;b:153756;tap:154400;h:154694;tap:155293;b:155552;h:156015;b:156430;tap:157083;d:157314;g:157774;d:158224;tap:158866;b:159085;h:159597;b:160008;h:160447;d:160864;g:161037;d:161302;");
            /*9*/ sequences.add("tap:992;tap:1752;tap:2082;tap:3630;d:3972;g:4548;d:4887;tap:5387;d:5778;tap:6585;d:6751;g:7007;d:7305;tap:7850;g:8176;d:8818;g:9123;d:9404;tap:10212;g:10535;g:11788;tap:12624;d:13006;g:13883;d:14198;d:15405;tap:16148;g:16353;d:16627;tap:17405;g:17803;d:18400;g:18714;d:18978;g:19293;tap:19837;h:20119;tap:20971;h:21237;tap:22186;h:22525;b:22867;h:23183;b:23528;tap:25178;h:25587;tap:26390;h:26788;b:27295;h:27951;tap:28787;g:29140;d:29801;g:30405;d:30958;g:31631;d:32185;tap:32994;b:33365;h:33962;b:34569;d:35172;g:35487;d:35784;g:36074;d:36372;g:36669;d:36950;g:37265;d:37580;g:37924;d:38171;g:38436;tap:38982;g:39357;tap:40168;g:40573;tap:41383;g:41825;tap:42580;g:42983;tap:43762;g:44175;tap:45013;g:45360;tap:46169;g:46585;tap:47393;d:47719;tap:48597;d:48988;tap:49798;d:50135;tap:50974;d:51356;tap:52165;d:52519;tap:53357;g:53724;tap:54586;b:54888;tap:55748;b:56180;tap:56984;h:57296;tap:58162;b:58557;tap:59412;h:59731;tap:60564;b:60864;tap:61838;h:62147;tap:62978;b:63361;tap:64250;d:64520;g:65212;d:65803;g:66403;d:66967;tap:67781;g:68146;tap:68982;d:69354;tap:70191;g:70588;tap:71367;d:71748;tap:72584;g:72944;tap:73803;d:74209;tap:75016;g:75380;tap:76158;d:76539;g:76852;d:77150;h:78417;b:78760;h:79016;d:79908;g:80188;tap:81025;g:81364;tap:82170;tap:83085;d:83773;g:84366;tap:85202;d:85558;g:86179;d:86451;g:86782;tap:87542;d:87873;g:88600;b:89213;h:89781;b:90457;h:91012;b:91589;h:92194;b:92806;h:93366;tap:94211;d:94576;g:95192;d:95794;tap:96604;g:96928;tap:97765;g:98174;tap:98983;g:99399;tap:100208;d:100585;tap:101396;g:101827;tap:102573;h:102929;tap:103772;g:104189;d:104798;tap:105543;b:105887;h:106531;b:107166;h:107770;b:108332;h:108954;g:109549;d:110143;tap:110980;g:111371;tap:112208;g:112613;tap:113393;g:113766;tap:114604;d:114946;tap:115781;d:116147;tap:116985;d:117409;tap:118186;d:118549;tap:119408;d:119732;tap:120591;g:120948;tap:121783;g:122214;tap:122973;d:123312;tap:124172;g:124557;tap:125392;g:125770;tap:126576;d:126897;tap:127765;d:128161;g:128790;d:129420;tap:130182;g:130555;tap:131390;d:131787;tap:132623;g:133027;tap:133787;d:134168;tap:135028;b:135345;tap:136148;h:136530;tap:137414;b:137732;tap:138568;h:138960;tap:139792;d:139965;g:140221;d:140526;g:140857;d:141138;g:141453;tap:142229;g:142611;d:143194;tap:143970;g:144356;d:144977;tap:145720;b:146118;h:146826;tap:147581;g:147974;tap:148752;d:149133;tap:149993;g:150514;d:151586;g:153908;d:156305;g:157759;d:158201;g:158456;tap:159002;b:159314;tap:160169;b:160495;tap:161356;d:161740;tap:162599;g:162954;tap:163791;d:164180;tap:164987;g:165343;tap:166179;d:166592;tap:167369;g:167739;d:168060;g:168400;tap:169206;g:169594;b:170260;tap:171004;h:171358;tap:172194;g:172558;tap:173392;d:173782;tap:174589;g:174986;tap:175793;d:176173;tap:177006;g:177388;d:178012;tap:178719;b:179137;tap:179969;g:180412;tap:181170;d:181561;tap:182418;g:182824;tap:183602;b:183965;h:184564;b:185175;h:185819;b:186385;h:186693;b:186985;tap:187755;d:188166;tap:189002;d:189308;tap:190231;d:190579;tap:191385;d:191724;tap:192584;g:192953;tap:193788;g:194209;tap:194986;g:195417;tap:196223;tap:196778;g:197153;tap:198011;g:198449;tap:199192;g:199604;d:200097;tap:200931;g:201283;tap:202143;d:202503;tap:203338;g:203718;tap:204552;g:204950;d:205541;g:205872;d:206170;tap:207007;g:207372;tap:208210;b:208597;tap:209453;b:209764;tap:210570;b:210972;tap:211831;h:212192;tap:213005;h:213374;tap:214208;h:214551;tap:215369;d:215722;g:216361;d:217295;g:217618;d:217924;g:218248;tap:219000;d:219349;g:219988;d:220565;g:221165;tap:221974;d:222343;tap:223152;g:223573;d:224160;g:224770;d:225058;g:225381;b:225657;h:225950;tap:226759;tap:228553;tap:230898;tap:232694;g:233126;d:234370;g:235505;d:237841;tap:240554;g:242149;d:242926;");
            /*8*/ sequences.add("tap:1348;tap:2075;tap:3040;tap:3862;tap:4755;tap:5588;tap:6501;tap:7434;tap:8388;tap:9286;tap:10139;tap:11144;tap:12093;tap:13000;tap:13781;d:13984;g:14435;d:14886;tap:15629;b:15796;tap:16514;b:16719;tap:17405;h:17613;tap:18291;d:18522;tap:19211;g:19416;tap:20107;h:20328;tap:21016;b:21228;tap:21884;b:22144;tap:22811;d:23008;tap:23697;h:23895;tap:24575;b:24761;tap:25458;g:25689;tap:26408;d:26620;tap:27280;g:27572;tap:28197;d:28409;g:28860;d:29302;g:29827;tap:30455;d:30665;g:31124;tap:31814;d:32030;g:32522;tap:34067;h:34296;tap:34954;g:35219;d:35725;tap:36327;h:36543;tap:37229;g:37457;tap:38148;d:38380;tap:39041;g:39331;tap:39934;d:40187;tap:40877;g:41069;tap:41789;d:42000;tap:42644;g:42876;tap:43567;d:43787;tap:45366;h:45615;tap:46269;tap:46710;h:46972;tap:47630;tap:48096;tap:48528;tap:49440;d:49640;g:50124;d:50620;g:50989;d:51481;tap:52143;h:52379;tap:52993;b:53234;tap:53947;g:54149;d:54616;tap:55337;g:55556;tap:56182;d:56447;g:56914;tap:58872;b:59128;tap:59784;d:60469;g:61003;d:61410;tap:62036;g:62286;d:62729;tap:63419;g:63630;tap:64349;g:64578;tap:65239;d:65443;tap:66133;b:66345;h:66809;b:67247;h:67702;tap:68383;g:68631;tap:69321;tap:69754;g:69995;d:70412;tap:71102;tap:71553;tap:72008;tap:72483;g:72722;tap:73382;d:73531;tap:74275;g:74477;tap:75167;g:75400;tap:76092;d:76297;tap:76958;d:77208;tap:77836;d:78106;tap:78768;b:78931;tap:79645;b:79863;tap:80582;b:80785;tap:81433;tap:81866;h:82112;b:82612;h:83056;b:83489;tap:84173;h:84384;b:84848;h:85341;tap:85965;g:86181;tap:86874;d:87090;g:87575;d:88034;g:88487;d:88898;tap:89558;tap:90027;tap:90495;d:90684;tap:91411;g:91608;tap:92300;g:92533;tap:93203;d:93412;tap:94134;tap:94568;tap:95017;tap:95485;d:95677;g:96104;d:96311;g:96583;tap:96905;d:97027;g:97480;d:97906;tap:98567;tap:99103;tap:99544;tap:99972;d:100185;tap:100906;g:101064;tap:101783;b:101995;h:102440;tap:103129;g:103274;tap:103993;h:104215;b:104510;h:104703;b:104937;h:105285;tap:105852;");
            /*14*/ sequences.add("tap:1825;tap:2346;tap:2813;tap:3305;tap:3788;tap:4266;tap:4698;tap:5203;tap:5671;tap:6121;tap:6625;tap:7058;tap:7557;tap:8007;tap:8437;tap:8637;tap:8964;tap:9334;tap:9534;tap:9974;tap:10407;tap:11263;tap:11463;tap:11504;tap:11704;tap:12288;tap:12785;tap:13362;tap:13562;tap:14198;tap:14625;tap:15094;tap:15617;d:15881;g:16117;d:16366;g:16765;d:16986;g:17210;d:17538;g:17935;d:18167;d:18668;d:19175;g:19609;g:20127;g:20553;h:21037;h:21522;h:21959;b:22421;h:22920;b:23396;tap:24079;d:24316;g:24793;d:25302;g:25683;d:26177;tap:26868;d:27128;g:27609;tap:28300;d:28547;tap:29268;g:30468;d:31134;g:31818;d:32032;g:32273;d:33058;g:33266;d:33735;g:33965;d:34197;g:34943;h:35234;b:35698;h:35911;b:36099;h:36855;b:37058;d:37490;g:37721;d:37960;g:38522;d:38982;g:39432;d:39892;g:40415;d:40884;tap:41490;tap:41690;tap:42013;d:42325;g:42540;d:42763;tap:43456;tap:43898;tap:44870;tap:45377;tap:45784;d:46060;g:46300;d:46548;tap:47210;d:47468;tap:48189;g:48455;tap:49147;d:49421;tap:50085;h:50297;tap:50957;h:51246;tap:51933;h:52232;tap:52878;d:53168;tap:53797;b:54074;tap:54737;g:55012;tap:55714;g:56019;tap:56650;tap:57099;tap:57600;tap:58095;tap:58573;d:58807;g:59308;d:59769;g:60225;tap:60888;h:61113;tap:61825;h:62063;tap:62779;h:63035;tap:63754;d:64008;g:64475;tap:65166;h:65496;b:65884;tap:66571;d:66858;h:67281;tap:67999;g:68220;d:68786;tap:69415;tap:69943;tap:70388;d:70651;g:71094;d:71474;tap:71953;tap:72153;d:72512;g:72751;d:73006;g:73392;tap:73829;tap:74029;h:74862;b:75382;tap:76010;d:76274;g:76788;d:77169;tap:77590;tap:77790;d:78040;g:78465;d:78703;g:79047;tap:79502;tap:79702;d:80027;g:80316;d:80547;g:80946;tap:81357;tap:81557;b:81835;h:82396;b:82815;tap:83260;tap:83460;b:83749;h:84166;b:84376;h:84724;tap:85117;tap:85317;d:85593;g:85982;d:86195;tap:86859;tap:87373;d:87644;g:87873;d:88104;tap:88886;tap:89086;d:90014;g:90490;b:90933;h:91417;b:91918;h:92329;b:92565;h:92778;d:93570;h:94175;b:94414;h:94663;b:95412;h:95636;d:96800;g:97309;d:97556;g:97991;d:98262;g:98468;d:99182;g:99428;d:99879;tap:100598;d:100896;tap:101559;d:101846;tap:102508;d:102797;g:103156;d:103617;tap:104307;d:105064;g:105328;d:105567;tap:106258;d:106606;tap:107235;tap:107630;tap:107830;d:107953;g:108224;tap:108806;tap:109006;d:109355;g:109814;d:110320;tap:111000;d:111248;g:111518;d:111694;b:112169;h:112435;b:112634;tap:113301;tap:113501;b:113595;h:114295;b:114521;h:114948;d:115204;g:115418;d:115994;g:116470;d:116859;tap:117288;tap:117488;d:117667;g:118077;d:118300;b:118767;h:119017;tap:119448;d:120230;g:120526;b:122189;h:122556;tap:123208;d:123724;g:124038;tap:124683;tap:125186;d:125296;g:125669;d:125892;tap:126585;d:126833;tap:127726;tap:127926;b:128070;h:128273;tap:128925;d:129407;g:129656;d:130106;g:130625;tap:131521;d:131987;g:132498;d:133264;g:133848;tap:134592;d:135347;g:135883;b:136292;h:136663;b:137004;h:137244;b:137640;tap:138360;d:138542;g:139152;d:139637;tap:140265;g:140400;d:140748;g:141003;b:141402;h:141869;tap:143139;tap:143558;tap:143758;h:143934;b:144187;h:144518;b:144788;tap:145439;h:145769;tap:146278;d:146677;g:147155;d:147385;tap:147835;d:148427;g:148624;d:149006;tap:149670;d:151445;g:152228;d:152859;g:153274;d:154221;d:154763;d:155223;d:155698;g:156108;g:156593;g:157045;b:157627;h:157867;b:158090;h:158432;tap:159123;tap:159785;tap:159985;d:160383;tap:161083;d:161482;g:161770;tap:162464;tap:162922;d:163348;g:163669;d:164076;tap:164508;tap:164876;b:165515;h:165752;tap:166272;tap:166760;b:167518;h:167944;b:168178;tap:168654;h:169293;b:169565;h:169849;tap:170235;tap:170435;d:171298;g:171706;tap:172086;tap:172286;tap:173124;d:173354;g:173609;tap:173982;tap:174182;d:174642;g:174847;d:175054;g:175514;tap:175819;tap:176019;d:176563;g:176793;d:177024;g:177331;tap:178022;b:178466;h:178661;b:178896;h:179226;tap:179910;b:180668;");
            /*7*/ sequences.add("tap:2205;d:3182;g:3641;d:4039;tap:4730;tap:5154;tap:5590;g:5751;d:6184;g:6644;d:7045;g:7538;b:8787;h:9222;tap:9784;tap:10242;d:10899;g:11335;d:11732;tap:12423;b:12628;h:13046;tap:13731;b:13935;h:14368;tap:15055;d:15253;g:15668;tap:16295;d:16536;g:17004;tap:17630;d:17850;g:18237;tap:18898;tap:19455;b:19859;h:20051;b:20409;h:20773;tap:21367;b:22309;h:23668;g:24218;d:24591;g:25043;d:25421;b:26509;h:26841;b:27195;h:27574;b:28446;h:28873;g:29328;d:29748;tap:30726;d:31818;g:32412;d:32830;g:33237;d:33680;tap:34371;tap:34807;tap:35217;d:35338;g:35813;d:36196;tap:36859;tap:37285;g:38010;d:38412;g:38826;d:39320;g:39897;d:40868;g:41387;d:41830;g:42128;d:42602;b:43077;h:43480;b:43872;h:44357;tap:44968;h:45189;b:45594;tap:46274;h:46507;b:46931;tap:47529;g:48714;tap:49317;tap:49716;tap:50212;tap:50603;tap:51038;tap:51471;tap:51920;d:52294;g:52626;tap:54011;b:54205;h:54641;tap:55297;h:55526;tap:56119;h:56337;b:56815;h:57196;b:57634;g:58845;d:59369;g:60196;d:61168;g:61516;d:61937;g:62446;d:62897;tap:64399;h:64583;b:64911;tap:65564;h:65785;tap:66384;tap:66840;d:67082;g:67575;d:67911;tap:69413;g:69661;d:70096;g:70485;d:70952;g:71328;d:71795;b:72232;h:73096;b:74308;h:74777;g:75249;d:75668;tap:76311;tap:76831;tap:77232;tap:77837;d:78061;g:78268;tap:79706;d:80700;g:81624;d:82099;g:83267;b:84647;h:85906;g:87106;d:87606;g:88418;d:89718;g:90996;d:91448;g:91915;tap:92542;d:92815;g:93213;b:93593;h:93920;tap:94782;tap:95173;b:95359;h:95776;tap:96450;b:96667;h:97052;tap:97744;b:97946;h:98382;g:98818;d:99786;g:100164;d:100934;g:101340;d:101845;g:102693;d:103123;b:103712;h:103991;tap:105403;b:106100;h:106509;b:106965;h:107318;tap:108009;g:108204;d:108607;g:109084;d:109490;tap:110135;g:110355;d:110815;g:111250;tap:111854;b:112069;b:112514;h:112906;b:113319;h:113787;tap:114386;b:114722;h:115131;tap:115697;g:115942;d:116341;g:116781;tap:117408;d:117654;g:118147;tap:118720;tap:119181;d:119373;g:119834;tap:120438;d:120625;g:121086;d:121537;tap:122111;g:122393;d:123013;g:123227;d:123648;b:124303;h:124589;b:124939;h:125377;tap:125972;g:126245;d:126664;tap:127292;tap:128562;g:128800;d:129628;g:130897;d:131341;g:131776;b:132197;h:132628;b:133083;d:133523;tap:134925;g:136008;d:136516;tap:137180;tap:137576;tap:138893;b:139103;tap:140107;h:140316;tap:141002;b:141268;tap:141921;d:142138;g:142574;tap:143178;d:143770;g:144990;d:146334;g:147453;d:147871;g:148671;d:148968;b:149721;h:150219;b:150705;b:151822;b:152500;tap:153569;b:154072;tap:155167;h:155363;b:155781;tap:156707;d:157128;g:157522;d:158391;tap:159412;tap:160291;d:160866;g:161423;d:162132;g:162631;tap:163557;d:163906;b:164366;h:164991;b:165617;h:166045;tap:166905;tap:167596;d:167801;g:168503;d:169055;g:169544;d:170813;d:171275;d:172137;d:173413;d:174956;g:175458;d:176161;g:177097;d:177657;g:178236;d:178589;g:179074;d:179418;tap:180088;tap:180524;tap:181150;b:181473;h:181903;b:182325;tap:183074;h:183235;b:183676;tap:184289;h:184540;b:185416;h:185891;b:186669;h:187879;b:188615;h:189153;b:189678;h:190523;b:190942;h:191389;g:191827;d:192229;g:192684;d:193033;b:193539;h:194178;b:194763;h:195729;b:196133;h:196512;g:196926;d:197378;g:197828;tap:198439;g:198714;d:199137;tap:199740;g:200029;tap:200604;d:200816;g:201268;d:201844;g:202141;tap:202715;h:202927;b:203406;tap:204038;d:204234;g:204669;tap:205296;tap:205711;tap:206184;h:206388;b:206834;tap:207492;d:207719;tap:208324;h:208592;tap:209226;b:209435;h:209813;b:210206;h:210860;b:211117;h:211774;b:212172;h:212485;tap:213499;g:213690;d:214094;g:214538;d:214955;g:215416;d:216406;d:217283;d:217657;d:218605;d:218903;d:219869;d:220221;tap:221028;d:221396;g:221935;tap:222681;g:222829;tap:223521;g:223679;d:223944;b:224838;h:225222;tap:226167;b:226351;h:226914;b:227400;h:227799;b:228805;h:229179;b:229533;b:230446;tap:231544;h:231698;g:232143;d:232817;g:233116;d:233371;g:234657;d:235500;g:235999;d:236606;b:237215;h:237646;b:238073;h:238502;g:239266;d:239881;g:240285;d:240674;b:241096;h:241577;tap:242178;tap:242603;b:243087;h:243629;b:244110;h:244530;b:244928;g:245432;d:245838;g:246228;b:246991;h:247506;tap:248134;tap:248603;d:248770;g:249223;d:249746;tap:250321;g:250566;d:250994;tap:251597;tap:252066;tap:252518;tap:252936;tap:253362;d:253570;g:253998;d:254415;g:254822;d:255329;g:255659;d:256077;tap:257231;tap:258359;g:259096;d:259510;g:260115;d:260387;g:261252;d:261657;g:262075;d:262538;tap:263199;g:263441;d:263817;tap:264862;g:265112;d:265498;g:265933;tap:266594;d:266915;g:267267;d:268132;g:268537;b:268973;h:269377;tap:270016;b:270262;h:270661;tap:271371;b:271615;h:271978;tap:272633;b:272845;h:273261;b:273771;h:274235;b:275192;h:276029;g:277289;d:277623;g:278074;d:278993;g:279881;d:280797;g:281028;d:281806;g:282137;d:283089;g:284345;tap:286230;");
            /*5*/ sequences.add("tap:1137;tap:1493;tap:2044;tap:2589;d:3255;g:4267;d:4639;g:4903;d:5143;g:5604;d:6064;g:7034;d:8001;g:8273;d:8512;g:8760;tap:9373;d:9680;tap:10819;g:11473;d:11908;g:12146;d:12411;g:12887;d:13312;g:14319;tap:15329;d:15548;g:15821;d:16085;g:16529;tap:17156;d:17816;tap:18941;d:19136;g:19400;d:19673;b:20079;h:20580;b:21406;h:22365;b:22886;d:23310;g:23785;d:24252;b:25078;tap:26207;h:26450;tap:27138;d:27320;g:27804;d:28703;tap:29858;d:30096;g:30344;tap:31240;tap:32096;tap:32761;tap:33376;d:34470;g:35044;d:36021;g:36560;d:36878;g:38038;d:38721;tap:39813;g:40539;d:41503;tap:42595;tap:43475;b:44148;h:44607;b:45076;h:45485;g:45973;d:46827;b:47869;h:48305;b:48698;h:49144;b:49617;g:50627;d:51472;g:51844;d:52307;b:52786;h:53256;tap:54343;d:55061;g:55553;d:55993;b:56466;h:56953;g:57749;d:58765;g:59463;d:60453;b:61078;h:61699;b:62329;tap:63480;h:64014;b:65053;tap:66142;d:66829;g:67848;d:68710;b:69569;h:70511;b:70955;tap:72623;d:73225;g:74100;d:75031;tap:76273;h:76866;tap:78016;b:78696;h:79648;tap:80771;d:81333;g:82250;d:83301;tap:84457;b:85024;h:85927;tap:87078;b:87765;h:88681;d:89112;g:89621;tap:90690;d:91447;g:92108;d:92738;g:93215;d:94145;b:95138;h:95851;b:96446;h:96909;tap:98092;d:98695;g:99153;d:99620;b:100059;h:100552;tap:101630;tap:102545;d:104139;g:104773;d:105409;g:105978;b:106919;tap:108002;h:108623;b:109553;h:110496;b:111389;tap:112587;d:113227;g:114172;d:115076;g:115967;tap:117122;tap:117928;tap:118419;tap:118940;g:119566;tap:120838;tap:121680;tap:122137;tap:122622;d:123178;tap:124385;g:125105;d:125564;b:126038;h:126971;tap:128127;b:128732;h:129190;g:129648;d:130488;tap:131724;tap:132414;b:133184;h:133797;b:134537;d:134998;g:136908;d:137802;g:138711;b:139580;h:140552;d:141464;tap:142589;tap:143472;d:143925;g:144854;tap:146124;b:146748;h:147244;b:147708;h:148646;b:149644;tap:150745;h:150956;tap:151667;b:152270;tap:153539;h:154127;b:154687;h:155137;tap:156199;b:156900;h:157814;g:158258;d:158708;tap:159800;tap:160761;tap:161719;tap:162609;d:163271;tap:164426;g:165278;tap:166254;tap:167987;tap:169732;tap:171680;d:172384;g:172877;d:173303;tap:174393;tap:175383;tap:177048;tap:177970;tap:178968;tap:179943;tap:180377;tap:180834;d:181482;g:182355;d:183186;g:183669;d:184216;b:185092;tap:186245;d:186406;g:186846;d:187285;g:187768;d:188695;tap:189822;g:190036;d:190487;g:190901;d:191360;g:193330;d:193741;b:194165;h:194556;b:195020;tap:196343;g:197064;");
            /*11*/ sequences.add("tap:8789;tap:9635;g:9892;d:10359;tap:11049;g:11340;tap:12085;g:12359;tap:13050;g:13415;tap:14076;g:14383;tap:15102;g:15426;tap:16088;g:16411;tap:17016;g:17331;tap:18050;g:18396;tap:19056;d:20328;g:20905;d:21440;g:21779;tap:22936;d:23443;g:23748;d:23963;g:24640;d:26304;g:26868;d:27347;g:27861;d:28322;b:28841;h:29317;tap:30616;g:30835;d:31370;g:31864;tap:32525;d:32832;g:33548;d:33804;g:34913;d:35227;g:35712;d:36296;tap:37040;d:37344;g:37875;tap:38566;tap:39061;tap:39630;tap:40139;d:40372;g:40855;d:41411;b:42796;tap:44119;g:44791;d:45500;g:45882;tap:47252;d:47536;g:47922;d:48382;g:48863;d:50440;g:50892;d:51896;g:52789;d:53255;b:54627;h:55834;tap:56807;d:57315;g:57962;d:59127;g:59407;d:59831;g:60382;b:61049;d:62276;g:62753;d:63405;g:63856;b:64949;h:65416;b:66701;h:67023;d:68190;g:68766;d:69275;g:69810;d:70335;tap:70938;d:71311;tap:71937;d:72279;tap:72941;g:73329;tap:73957;g:74304;tap:74995;g:75377;tap:76038;d:76343;tap:77033;d:77428;tap:78117;d:78463;tap:79066;d:79420;tap:80047;g:80384;d:81441;g:81671;d:81878;tap:82240;g:83149;d:83734;g:84007;d:84816;g:86397;d:86880;d:87365;d:87832;d:88366;g:88810;d:89319;d:90800;tap:92652;g:93135;d:93648;g:93879;g:95253;d:95500;g:95789;g:96321;d:96796;g:98319;g:98832;g:99355;g:99922;g:100439;d:100850;g:101319;tap:102909;d:104146;g:104997;d:105755;g:106127;d:107492;b:108488;h:108942;b:110025;tap:111521;h:111790;tap:112506;h:112824;tap:113564;h:113847;tap:114564;h:114896;tap:115556;b:115869;h:116324;tap:117009;b:117335;tap:118020;h:118280;tap:118971;b:119296;tap:120012;h:120362;tap:120986;b:121328;tap:122016;h:122359;tap:123017;h:123375;tap:124089;b:124369;tap:125052;d:125408;tap:126068;d:126405;tap:127030;g:128442;d:129428;g:131298;d:132688;b:134192;h:135725;b:137282;d:138106;g:138453;tap:139287;d:140665;g:141718;tap:143072;d:143403;tap:144094;g:144350;tap:145071;g:145378;tap:146068;g:146401;tap:147090;g:147352;tap:148073;g:148326;tap:149046;d:149341;tap:150059;tap:150967;d:152211;g:153195;d:153626;g:153940;d:154961;g:155412;d:155764;g:155978;d:156401;g:156892;d:158322;d:158814;d:159353;d:159912;g:160363;d:160847;g:161347;b:162686;tap:164651;tap:165092;d:165545;g:165834;tap:167043;b:167341;h:167700;b:167962;h:168780;tap:170516;g:170822;tap:171483;g:171790;g:172393;g:172938;d:173347;tap:174597;g:174862;d:176381;b:177059;h:177816;b:179159;h:179720;b:180013;tap:181091;g:182359;d:183043;g:183960;tap:185121;d:185367;tap:186114;g:186428;tap:187058;g:187408;tap:188108;d:188337;tap:189029;g:189378;tap:190039;g:190353;tap:191083;d:191369;tap:192033;d:192449;tap:193053;g:193346;tap:194046;d:194437;tap:195050;d:195396;tap:196058;g:196426;tap:197053;d:197428;tap:198099;g:198411;tap:199048;d:200499;tap:203813;g:204012;tap:204615;g:204907;tap:205509;g:205874;tap:206477;g:206905;tap:207531;d:207864;tap:208555;d:208892;tap:209535;d:209860;tap:210519;h:210856;tap:211480;h:211869;tap:212556;h:212882;tap:213569;h:213891;tap:214533;h:214848;b:215430;");
            /*19*/ sequences.add("tap:1140;g:2128;d:3558;g:5197;d:6376;g:7908;d:9252;g:10768;d:12174;b:13521;h:15244;b:16700;g:18270;d:19823;g:21316;h:22702;b:24330;g:25764;d:27205;h:28613;g:30271;d:31904;b:33275;h:34761;d:36203;g:36741;b:37743;h:39252;d:40695;g:42199;d:43682;b:45309;h:46813;b:48331;h:49705;g:50513;b:51376;d:52005;b:52770;g:53462;b:54335;d:55047;h:55889;g:56448;b:57323;d:58047;h:58805;g:59536;d:60274;g:60994;d:61390;g:61804;d:62148;g:62521;d:62905;b:63286;h:63705;b:64452;h:64804;b:65170;h:65538;d:66229;g:67004;d:67369;g:67795;d:68168;g:68561;d:68883;b:69302;h:70029;b:70395;h:70769;d:72280;g:73318;d:73780;tap:75455;d:75778;g:76067;d:76365;tap:76939;g:77244;d:77583;g:77951;tap:78534;b:78748;h:79089;b:79455;tap:79973;d:80229;g:80560;d:80929;tap:81542;g:82123;d:82472;g:82858;d:83576;g:83940;d:84296;b:85698;h:87187;g:88060;d:88389;tap:88974;b:89997;h:90333;b:90688;h:91086;d:91475;g:91805;tap:92711;d:92857;g:93257;d:93603;g:94023;tap:94530;tap:94965;d:95505;g:95848;tap:96411;tap:97981;g:99191;d:100763;b:102279;h:102877;b:103817;g:105238;d:106684;b:107543;h:107897;tap:108474;d:109762;g:110211;d:110401;g:110883;d:111063;g:111344;b:111659;h:111875;b:112076;h:112395;b:112587;h:112766;g:113048;d:113205;g:113454;b:113918;h:114125;b:114357;g:114662;d:114851;g:115066;d:115364;g:115774;d:115956;g:116178;b:116517;h:116880;b:117273;h:117660;b:117844;h:118009;b:118357;h:118770;b:119180;h:119374;b:119574;h:119907;g:120264;d:120632;g:121066;d:121393;g:121774;d:122114;g:122481;d:122859;b:123223;h:123637;b:124009;h:124409;b:124770;h:124973;b:125174;h:125348;b:125565;h:125858;g:126288;d:126619;g:127052;d:127379;g:127789;d:128124;g:128356;d:128537;tap:129113;d:129273;tap:129881;d:130035;tap:130638;d:130825;tap:131369;d:131557;tap:132149;tap:132349;b:133022;h:133372;b:133743;h:134219;b:134511;d:134888;g:135236;g:135646;g:136028;g:136407;g:136744;d:136933;g:137164;d:137354;g:137586;g:137930;b:138279;h:138637;b:138853;h:139038;h:139346;h:139755;h:140121;b:140357;h:140533;h:140911;h:141267;b:141630;h:141834;b:142025;h:142335;g:142771;d:143129;g:143517;d:143859;tap:144432;g:144636;d:144988;g:145353;d:145830;g:146144;d:146342;g:146542;d:146724;g:146937;g:147288;g:147680;d:147886;g:148117;b:148383;h:148734;b:148952;h:149151;b:149527;h:149884;h:150227;b:150611;h:150816;b:151050;h:151427;b:151787;h:152157;d:152544;g:152876;d:153224;d:153650;g:153841;d:154074;g:154436;d:154809;d:155235;d:155625;d:155948;g:156256;d:156620;g:156817;d:157041;g:157364;d:157562;g:157794;d:158142;g:158648;d:158929;g:159294;b:159899;h:160093;b:160413;h:160799;b:161450;h:162266;g:163698;d:165223;b:166698;h:168215;g:169546;d:170334;b:171237;g:171917;d:172666;b:173534;h:173861;b:174218;d:174959;g:175686;b:176444;h:177203;g:177896;d:178243;g:178654;b:179400;h:179830;g:180224;d:180951;g:181314;b:181711;h:182195;b:182561;h:182868;g:183242;d:183959;g:184323;b:184696;h:185532;b:185864;g:186271;d:187009;g:187348;b:187780;h:188535;b:188876;g:189316;d:190008;g:190381;b:190779;tap:191331;h:191512;b:191859;g:192240;d:192999;g:193388;b:193790;h:194866;g:195264;d:195947;g:196303;b:196699;tap:197154;h:197823;b:198257;tap:199227;h:199357;tap:199961;b:200136;d:200496;g:200847;tap:201422;d:202024;g:202347;tap:202921;d:203112;g:203518;d:203815;tap:204421;d:204791;g:205098;d:205361;tap:205990;d:206171;g:206527;d:206825;tap:207452;g:207638;d:207861;g:208093;d:208390;tap:209018;h:209187;b:209380;h:209572;d:209907;tap:210481;d:210712;tap:211237;d:211411;tap:211994;d:212185;tap:212760;d:212934;g:213295;d:213680;g:214015;d:214329;tap:214997;h:215152;b:215520;h:215848;tap:216448;b:216703;tap:217240;b:217451;tap:218017;h:218175;b:218549;h:218902;tap:219497;b:219692;h:220052;b:220421;tap:221011;d:221180;g:221578;d:221927;tap:222472;b:222683;h:222867;b:223116;h:223368;tap:223983;h:224139;b:224583;h:224884;tap:225500;g:225649;d:225847;g:226089;d:226377;tap:226951;g:227134;d:227496;g:227848;tap:228460;b:228731;h:228884;b:229136;h:229416;tap:229979;g:230114;d:230340;g:230568;d:230874;tap:231486;b:231651;h:231857;b:232115;h:232365;tap:232970;g:233130;d:233328;g:233576;d:233841;tap:234473;b:234667;h:234860;b:235089;h:235415;tap:236014;d:236163;g:236395;d:236893;g:237292;b:238074;h:238386;b:238761;h:239147;b:239514;h:239814;d:240214;g:240665;d:240946;g:241361;b:241797;h:242106;b:242497;h:242846;tap:243444;d:243585;g:243798;d:244006;g:244383;tap:244992;tap:245192;g:245297;d:245521;g:245873;tap:246474;tap:246674;g:246779;d:247010;g:247405;tap:248012;tap:248201;tap:248401;g:248505;d:248857;d:249277;g:249595;d:249785;g:249986;g:250373;d:250722;d:251020;d:251260;d:251519;d:251885;g:252268;b:253710;h:254805;d:255264;g:255650;d:256067;g:256395;b:256812;h:257161;b:257517;h:257877;d:258253;g:258677;d:258859;g:259049;d:259356;b:259742;h:260466;b:260865;d:261210;g:261718;d:262014;g:262338;b:262742;tap:263366;g:263538;d:263843;g:264286;h:265711;g:266394;d:266691;g:266964;b:267290;h:267921;b:268695;h:269077;b:269506;h:269823;tap:270409;d:270617;g:271023;tap:271920;d:272113;g:272474;d:272826;tap:273430;d:273658;g:273990;d:274347;b:274705;h:275172;b:275493;h:275827;g:276290;b:277007;h:277326;h:277772;d:278139;g:278487;d:278809;d:279220;b:279948;h:280705;b:281070;h:281465;b:281795;g:282228;tap:282973;d:283220;g:283829;d:284197;tap:284770;d:285358;tap:286281;g:286710;d:288329;b:289745;h:290285;b:290593;h:290889;g:291266;d:292384;g:292769;d:293113;g:293482;d:293842;tap:294417;d:295397;g:295795;d:296155;b:296534;h:296867;tap:297434;d:298365;g:298816;b:299589;h:299894;tap:300488;d:300729;g:301115;d:301468;tap:302042;g:302658;d:302921;tap:303496;b:303684;h:304112;g:304767;d:305133;g:305501;b:305829;tap:306454;d:306989;g:307823;d:308688;g:309422;b:309780;h:310399;b:310779;tap:311727;h:312085;b:312733;h:313188;b:313465;h:313777;tap:314398;d:314506;g:314846;tap:315421;d:315827;g:316753;d:317211;g:317597;d:317887;b:318264;h:319066;b:319835;h:321161;g:321881;d:322425;g:322755;b:323514;h:324565;b:325699;h:326462;b:327233;tap:328202;tap:328959;g:329258;d:329555;tap:330451;tap:331202;tap:331937;tap:332730;g:333279;d:334016;g:334777;b:336950;");
            /*24*/ sequences.add("g:1451;tap:1962;b:2294;d:2528;g:2880;tap:3142;b:3439;d:3798;g:4150;tap:4492;b:4781;d:5093;g:5482;tap:5788;b:6135;d:6470;g:7434;tap:7722;tap:8148;d:8508;g:8834;tap:9259;g:10525;tap:10905;b:11177;tap:11587;g:11842;d:12750;tap:12931;tap:13162;g:13529;tap:13880;b:14021;tap:14230;g:14474;d:15491;tap:15879;tap:16553;g:16850;b:17172;g:18135;tap:18507;d:18820;tap:19142;b:19456;g:19761;tap:20817;d:21177;tap:21789;g:22153;b:22459;tap:23394;tap:23671;d:23820;g:24184;h:24315;tap:24570;d:24703;g:24883;tap:25256;b:26152;d:26524;g:27198;tap:27540;d:27891;tap:28843;g:29117;tap:29521;b:29852;d:30132;g:30500;tap:31558;b:31905;d:32243;g:32462;tap:32821;d:33135;tap:34189;g:34510;b:34870;tap:35182;g:35478;d:35878;tap:36821;g:37208;b:37552;g:37831;tap:38153;b:38483;tap:39852;g:40153;d:40486;tap:40785;h:41152;g:42137;d:42888;b:43186;tap:43486;tap:43819;d:44851;g:45212;tap:45852;b:46191;tap:46487;d:47461;g:47866;b:48522;d:48838;tap:49140;tap:50195;g:50533;d:50853;tap:51167;g:51488;b:51839;d:52849;tap:53180;g:53799;tap:54167;b:54471;d:55768;tap:56016;tap:56262;g:56542;d:56831;b:57161;g:58198;tap:58549;tap:58894;d:59207;g:59496;b:59839;g:60857;tap:61189;b:61509;d:61805;g:62102;b:62491;tap:63469;g:63825;d:64229;tap:64543;g:64940;b:65203;tap:66200;tap:66529;d:67077;g:67452;h:67794;tap:68909;g:70539;d:71761;tap:72195;b:72483;g:72788;d:73132;g:74115;tap:74515;g:74910;d:75237;b:75550;g:75856;tap:76520;b:76857;g:77245;b:77541;b:77856;g:78037;tap:78276;b:78547;tap:78932;g:79271;d:79605;tap:79885;d:80245;g:80532;tap:80876;b:81219;tap:81841;g:82146;d:82513;g:82843;tap:83187;tap:83501;h:83828;g:84263;tap:84536;b:84872;tap:85244;g:85524;d:85874;b:86188;tap:86510;g:87489;tap:87819;b:88174;tap:88562;g:88892;tap:89225;g:90142;tap:90574;b:90860;g:91192;g:91509;tap:91890;g:92774;d:93108;tap:93440;tap:93840;g:94208;d:94493;d:95457;tap:95790;d:96439;tap:96806;tap:97203;g:98190;d:98829;tap:99159;g:99490;b:99845;d:100823;tap:101190;d:101864;g:102198;tap:102496;d:103531;g:103826;tap:104123;tap:104499;h:104829;g:105173;d:106081;tap:106494;tap:106874;g:107203;h:107869;d:108778;tap:109166;g:109509;d:109859;tap:110148;b:110492;h:111415;d:111848;tap:112209;g:112531;b:112844;tap:113195;g:114141;d:114440;tap:114799;g:115440;d:115812;b:116762;d:117409;tap:117776;g:118119;tap:118433;d:119581;g:120215;tap:120968;b:122067;d:122330;g:122868;tap:123139;h:123515;tap:123907;g:124789;tap:125111;b:125479;d:125774;tap:126146;g:126423;d:127493;tap:127807;g:128142;b:128514;tap:128779;d:129146;tap:130238;tap:130843;tap:131165;tap:132188;tap:133083;tap:133809;g:134169;d:134528;g:135445;d:136127;b:136495;tap:136789;tap:137164;d:138118;g:138453;d:139103;g:139435;h:139739;b:140768;h:141114;g:141485;d:141871;b:142110;tap:142307;d:142579;g:143469;d:144092;b:144469;g:144813;h:145189;b:146187;g:146530;g:146889;tap:147240;b:147512;g:147839;tap:148478;g:148848;h:149433;d:149807;tap:150112;g:150472;d:151259;tap:151566;h:151892;g:152221;tap:152551;b:152840;g:153173;d:154114;tap:154505;g:154841;b:155188;g:155460;tap:155828;d:156418;g:156826;tap:157481;g:157849;d:158480;d:159064;d:159409;tap:160119;h:160532;tap:160848;b:161184;g:162096;g:162449;b:162842;tap:163148;b:163515;g:163803;b:164230;tap:165115;d:165454;tap:165834;d:166164;g:166520;tap:167438;g:168118;b:168469;tap:169035;g:170089;d:170494;tap:170833;h:171154;tap:171478;b:171828;b:172586;tap:172815;g:173246;tap:173568;d:173882;g:174234;g:174540;d:175718;b:176107;h:176549;g:176803;tap:177171;b:178070;d:178460;g:178826;tap:179198;tap:179532;tap:182222;g:182516;d:183417;h:183784;d:184108;tap:184452;g:184658;g:184832;b:185112;d:186435;tap:186798;g:187078;b:187408;h:187751;tap:188713;d:189095;g:189425;tap:189806;g:190076;d:190307;h:191401;d:192131;tap:192442;d:192813;b:193101;d:194000;tap:194359;h:194740;g:195062;d:195243;tap:195465;b:195778;tap:197150;d:197472;tap:197744;h:197933;b:198360;d:199515;tap:199865;h:200212;g:200484;b:201083;tap:202151;d:202535;tap:202832;tap:203219;g:203550;b:203883;d:204832;h:205216;g:205497;tap:205788;b:206161;d:206525;g:207829;b:208237;g:208543;b:208873;h:209236;tap:210222;d:210648;tap:211288;g:211604;b:211991;g:213370;b:213702;g:214064;b:214691;b:216103;h:217428;");
            /*23*/ sequences.add("g:946;tap:1269;g:1525;tap:1813;b:1954;g:2248;tap:2538;g:2818;tap:3000;g:3230;b:3502;d:3757;g:4037;h:4195;b:4537;tap:4864;g:5086;d:5244;tap:5561;d:5828;g:6083;tap:6307;h:6520;g:6800;tap:7130;g:7394;d:7576;g:7856;tap:8170;b:8367;d:8639;g:8772;h:9085;b:9412;tap:9701;g:9833;d:10105;b:10377;h:10632;g:10872;d:11028;b:11350;tap:11659;g:11953;d:12117;b:12421;d:12727;g:12982;h:13237;b:13394;d:13700;g:13939;d:14244;b:14393;d:14680;g:14936;d:15267;b:15506;h:15655;g:15951;d:16305;b:16532;tap:16697;g:16961;b:17275;d:17430;g:17669;g:17843;g:17983;g:18290;d:18599;b:19084;d:19428;b:19811;d:20260;b:20565;d:20861;g:21126;d:21397;g:21781;g:22094;d:22398;g:22557;tap:23098;h:23474;tap:23605;h:24011;tap:24349;tap:24580;h:24786;h:25504;tap:25914;h:26285;tap:26442;tap:26681;h:26993;tap:27150;b:27693;tap:28203;b:28617;tap:29041;b:29305;tap:29437;g:29913;d:30455;g:30845;d:31241;g:31521;d:31672;tap:32244;h:32786;tap:33188;tap:33729;h:33894;tap:34487;h:35025;tap:35466;tap:35837;d:36048;g:36262;g:36828;tap:38311;g:38812;tap:39102;g:40019;d:40343;b:40623;d:40778;g:41092;tap:41415;tap:41703;h:41836;g:42106;d:42475;tap:42671;d:42951;b:43124;g:43674;d:43954;tap:44126;d:44441;g:44687;d:44984;tap:45247;d:45379;g:45651;b:45956;d:46278;g:46411;d:46740;b:47012;d:47275;g:47525;d:47679;tap:47968;h:48223;g:48536;d:48691;b:49106;d:49456;g:49787;d:49943;tap:50290;g:50537;d:50850;tap:51000;d:51329;g:51779;d:52075;tap:52232;d:52537;g:52816;d:53121;tap:53277;d:53557;g:53838;g:54109;g:54382;g:54753;tap:55058;d:55500;b:55889;d:56420;tap:56716;d:56873;g:57153;h:57408;b:57913;d:58302;g:58689;d:58969;b:59109;h:59657;g:60238;d:60594;b:60932;d:61186;g:61376;d:61958;tap:62246;d:62395;g:62741;d:63080;g:63461;d:63592;b:63913;tap:64236;d:64794;b:65180;d:65557;g:65805;d:65961;tap:66510;d:66854;g:66954;d:67334;tap:67816;d:68093;g:68226;tap:68778;d:69234;g:69351;h:69719;tap:70186;d:70441;g:70581;tap:71141;d:71396;g:71536;d:71896;b:72338;b:72544;b:72799;b:73097;g:73352;d:73785;g:74235;d:74640;tap:75017;d:75348;g:75610;d:76086;g:76484;d:76910;tap:77286;h:77640;tap:77895;d:78338;g:78726;tap:79177;tap:79636;d:79924;g:80187;tap:80653;tap:81097;d:81485;g:81822;tap:82169;d:82493;g:82914;d:83371;tap:83814;d:84273;g:84487;d:84921;tap:85244;d:85670;g:86054;d:86504;d:86839;g:87111;d:87516;tap:87921;d:88310;g:88702;d:89015;d:89362;h:89780;tap:90152;d:90557;b:90713;d:90887;g:91067;d:91240;b:91380;tap:91716;d:92050;g:92458;d:92890;tap:93302;d:93615;b:93887;d:94359;tap:94726;d:95211;b:95662;d:95958;tap:96213;d:96678;b:97078;d:97512;tap:97895;d:98273;g:98485;d:98952;tap:99381;g:100185;g:100556;d:100822;tap:101250;d:101708;g:102113;d:102502;tap:102782;h:103071;g:103497;d:103901;tap:104395;d:104791;g:105087;tap:105342;b:105793;d:106189;g:106624;d:107012;tap:107324;h:107656;g:108057;tap:108516;g:108949;d:109237;b:109387;g:109996;d:110404;b:110788;d:111026;g:111290;tap:111578;b:111719;d:111998;g:112262;d:112613;b:112728;h:113009;g:113306;d:113577;g:113841;d:113997;b:114285;tap:114548;g:114871;tap:115035;b:115283;g:115596;tap:115835;b:116107;d:116271;g:116535;tap:116831;b:117103;g:117508;tap:117830;b:118093;tap:118357;g:118489;tap:118848;d:119128;tap:119452;h:119581;tap:119869;d:120255;tap:120639;d:120795;g:121084;tap:121443;g:121986;d:122395;g:122691;tap:122963;b:123096;d:123467;g:123681;tap:124041;b:124180;d:124486;g:124774;tap:125062;b:125252;d:125399;g:125933;tap:126229;b:126394;d:126738;g:126984;tap:127224;b:127405;d:127602;g:127726;d:127890;b:128022;tap:128311;g:128815;d:129195;b:129508;d:129832;g:129962;d:130285;b:130539;tap:130912;g:131043;d:131444;b:131828;d:132154;g:132320;d:132609;b:132823;d:133145;g:133278;d:133607;b:133871;d:134224;g:134403;d:134568;b:134840;d:135136;g:135416;d:135589;b:135861;d:136166;g:136437;h:136677;tap:136807;d:136966;g:137121;b:137385;g:137940;d:138292;tap:138539;d:138835;g:139033;d:139156;tap:139661;d:139975;g:140099;d:140555;tap:140680;d:140967;g:141214;d:141379;tap:141929;d:142296;g:142404;h:142698;tap:143050;d:143330;g:143535;d:143676;tap:143956;d:144262;g:144524;h:144680;tap:144969;d:145240;g:145496;h:145668;h:145858;h:146015;h:146253;b:146551;d:146951;g:147368;d:147784;b:148242;d:148514;g:148801;d:149251;tap:149655;d:150096;g:150495;d:150788;b:151044;d:151501;g:151902;d:152334;g:152754;tap:153067;b:153347;tap:153805;g:154207;tap:154624;b:155012;tap:155375;g:155615;d:156055;b:156472;tap:156865;g:157289;h:157593;b:157927;d:158369;g:158733;h:159176;b:159529;d:159859;g:160194;d:160664;b:161052;h:161462;g:161902;d:162159;tap:162446;d:162878;b:163295;g:163621;h:163940;h:164219;h:164516;b:164822;d:165255;g:165607;tap:166025;d:166245;b:166475;d:166615;g:167091;d:167441;b:167640;g:168002;tap:168353;d:168634;g:168790;b:169394;g:169925;d:170333;b:170696;g:171092;d:171331;b:171628;d:172080;tap:172516;g:172896;b:173311;b:173591;b:173946;g:174338;d:174838;tap:175205;d:175623;g:175911;b:176199;h:176657;g:177038;d:177446;tap:177839;tap:178105;b:178437;d:178829;tap:179317;tap:179733;g:180149;d:180471;tap:180751;h:181216;b:182112;tap:182496;g:182785;h:183090;");
            /*15*/ sequences.add("tap:1126;tap:1614;tap:1814;tap:2333;tap:3188;d:3346;tap:3981;tap:4304;tap:4504;g:4815;d:4988;d:5494;g:5855;d:6256;tap:6859;tap:7311;d:7527;g:7862;tap:8225;tap:8425;d:8741;g:9157;d:9566;tap:10169;d:10377;g:10583;d:10952;tap:11493;tap:11693;h:12019;b:12457;h:12842;d:13141;tap:13920;d:14142;tap:14771;d:15030;g:15425;d:15801;g:16144;tap:16772;d:17008;g:17435;d:17843;tap:18198;tap:18398;d:18715;g:19528;tap:20132;h:20398;b:21330;h:21538;tap:21948;d:22371;g:22845;d:23172;g:23713;d:24118;g:24522;d:24919;g:25278;b:25698;h:26153;b:26897;h:27229;d:27850;g:28199;d:28742;g:29283;d:29651;tap:30277;d:30490;g:30925;d:31286;tap:31911;tap:32351;d:32542;g:32946;tap:33573;d:33804;g:34202;d:34506;tap:35225;b:35476;h:35848;b:36315;d:36697;g:37170;d:37533;b:37940;h:38297;b:38754;d:39172;tap:40162;d:40399;g:40825;d:41169;tap:41810;d:42017;g:42518;d:42877;tap:43504;d:43759;g:44194;d:44566;b:44951;h:45367;b:45816;d:46025;g:46288;d:46570;tap:47288;d:47449;tap:48076;d:48245;g:48468;tap:49095;d:49248;g:49486;tap:50113;d:50409;g:51352;tap:51730;tap:51930;tap:53380;d:53629;d:54119;tap:54665;tap:55106;d:55284;g:55621;d:56023;tap:56712;d:56943;g:57362;tap:58101;tap:58301;d:58617;g:59082;d:59369;tap:59860;d:60282;g:60707;tap:61248;tap:61448;b:61787;h:62235;tap:62920;b:63344;tap:64186;h:64393;tap:65010;h:65249;tap:65848;d:66071;tap:66697;g:66904;d:67353;g:67721;tap:68324;h:68512;b:68999;tap:69571;tap:69966;h:70159;b:70541;d:71279;g:72133;b:72967;h:73823;d:74639;g:75511;b:76298;h:76729;tap:77875;tap:78680;tap:80014;d:80219;g:80555;b:81414;h:81791;b:82197;h:82550;d:82958;g:83430;d:83832;b:84124;tap:84855;h:85098;b:85526;tap:86068;d:86328;g:86718;d:87113;tap:87477;tap:87677;d:88006;g:88396;d:88814;tap:89417;d:89673;g:90041;d:90422;tap:90792;tap:90992;b:91442;h:91715;b:92132;tap:92731;h:92982;b:93330;h:93768;tap:94134;tap:94334;d:94798;g:95356;tap:96016;d:96272;g:96694;d:97091;tap:97648;tap:97848;b:98221;h:98721;tap:99379;d:99608;g:100022;d:100390;tap:100934;d:101214;g:101648;d:102045;tap:102705;d:102911;g:103314;d:103689;tap:104316;d:104595;g:104964;d:105365;tap:105991;b:106251;h:106626;b:107030;tap:107483;tap:107683;d:108288;g:108722;tap:109218;tap:109418;d:109779;g:110325;d:110706;h:111131;b:111536;h:111945;b:112357;tap:112978;d:113235;g:113695;tap:114239;tap:114607;d:114898;g:115296;tap:115868;d:116100;g:116559;d:116986;g:117345;tap:117919;d:118181;tap:118808;tap:119241;d:119424;g:119867;d:120293;b:120676;h:121061;b:121487;tap:122133;d:122395;g:122747;d:123221;g:123598;d:123978;h:124372;d:124864;b:125219;g:125624;b:125988;h:126441;d:126816;g:127216;b:127642;h:128026;b:128468;d:129266;tap:129993;d:130956;tap:132929;d:133134;g:133548;tap:134093;d:134382;g:134739;d:135090;tap:135687;tap:135887;d:136170;g:136592;d:136839;tap:137422;d:137688;g:138061;d:138458;tap:139069;d:139319;g:139733;d:140101;tap:140514;tap:140714;d:141047;g:141385;d:141732;tap:142368;d:142621;g:143066;d:143421;tap:143881;tap:144081;d:144435;g:145070;b:145441;tap:146087;h:146363;tap:146942;h:147163;tap:147774;b:147969;tap:148602;d:148791;tap:149492;d:149645;tap:150282;tap:150694;d:150885;tap:151499;tap:151923;tap:152344;tap:152738;tap:153183;tap:153635;d:153800;tap:154414;g:154612;tap:155195;d:155394;tap:156066;d:156239;tap:156875;h:157097;tap:157704;b:157948;tap:158528;d:158721;tap:159334;h:159567;tap:160179;d:160436;tap:160960;tap:162710;tap:164316;tap:165430;");




            //ici news

            /*20*/ sequences.add("tap:2029;g:2316;d:2685;tap:3165;tap:3446;g:3685;d:3940;tap:4154;tap:4467;g:4682;tap:4954;d:5197;g:5498;tap:5794;tap:6090;d:6346;g:6585;tap:6848;d:7105;g:7367;tap:7656;tap:7878;d:8118;g:8307;tap:8612;tap:8992;d:9272;g:9470;tap:9734;d:9981;g:10253;tap:10542;tap:10796;d:11060;g:11259;tap:11546;g:11974;d:12261;b:12516;d:12855;g:13061;tap:13375;g:13581;d:13878;b:14092;h:14340;g:14546;tap:14850;g:15114;d:15386;g:15600;h:15814;b:16028;d:16292;g:16475;tap:16778;b:17000;d:17277;tap:17504;d:17953;g:18226;tap:18497;b:18728;h:18984;g:19480;tap:19768;b:19998;d:20466;g:20805;tap:21036;tap:21275;d:21522;g:21720;h:22062;g:22351;d:22591;tap:22813;d:23084;b:23273;h:23588;g:23981;d:24236;tap:24524;d:24775;b:25035;d:25316;g:25513;tap:25834;b:26081;tap:26433;tap:26884;d:27197;g:27435;d:27658;tap:27848;h:28095;g:28366;tap:28573;g:28803;tap:29059;b:29240;h:29523;g:29817;d:30048;g:30256;tap:30493;b:30675;h:30988;g:31301;tap:31540;tap:31822;d:32100;g:32306;b:32591;d:32875;g:33114;d:33369;tap:33599;d:33814;g:33970;h:34193;g:34449;d:34638;g:34907;tap:35108;g:35314;b:35571;tap:35866;g:36080;tap:36311;g:36500;d:36748;b:37092;d:37401;g:37574;d:37846;g:38071;tap:38340;b:38570;tap:38889;g:39058;d:39346;g:39560;d:39808;b:40055;tap:40399;g:40555;tap:40860;g:41101;d:41420;g:41659;d:41922;g:42136;d:42425;g:42664;b:43028;d:43436;g:43609;tap:43914;tap:44186;d:44425;b:44680;d:44944;g:45133;h:45380;b:45587;d:45858;tap:46115;d:46444;g:46609;tap:46907;tap:47128;d:47402;b:47631;d:47937;tap:48142;d:48423;g:48645;h:48868;b:49131;d:49446;g:49652;d:49933;tap:50196;d:50460;b:50670;h:50946;g:51177;d:51435;tap:51687;d:51976;b:52174;d:52454;g:52685;d:52935;tap:53187;h:53442;b:53673;d:53937;g:54176;tap:54481;tap:54696;h:54934;b:55181;d:55479;g:55668;d:55957;tap:56212;d:56468;b:56690;h:56962;g:57193;d:57456;tap:57688;d:57959;b:58186;d:58488;g:58719;d:58983;b:59205;h:59427;tap:59683;d:60018;g:60215;d:60479;b:60727;h:60975;tap:61237;d:61549;b:61721;tap:61959;g:62183;h:62437;b:62676;d:62956;g:63195;tap:63475;b:63682;d:63970;h:64274;g:64522;d:64777;b:65000;tap:65215;g:65445;h:65716;g:65988;d:66203;b:66450;d:66697;g:66911;h:67183;b:67455;tap:67719;g:67941;d:68188;b:68402;h:68667;b:68948;d:69193;g:69441;d:69688;tap:69935;h:70207;b:70542;d:70740;g:71013;d:71259;tap:71457;h:71713;b:72027;d:72266;g:72505;d:72752;tap:72958;h:73214;b:73510;d:73757;g:74004;tap:74276;tap:74531;h:74771;b:75002;d:75232;g:75454;d:75718;tap:75924;h:76220;b:76500;d:76723;g:76946;d:77210;tap:77390;h:77671;b:77950;d:78197;b:78378;d:78658;g:78847;h:79179;b:79451;d:79738;g:79953;d:80224;tap:80448;h:80736;g:81145;d:81432;b:81679;d:81951;h:82294;b:82774;d:83037;g:83368;d:83616;h:83886;b:84295;d:84574;g:84821;h:85078;b:85293;d:85664;g:85951;d:86231;b:86495;h:86826;g:87257;d:87596;b:87785;d:88024;h:88338;b:88626;d:88873;g:89112;d:89360;b:89566;h:89863;g:90185;d:90425;b:90597;d:90885;g:91148;h:91379;b:91635;d:91906;g:92145;d:92434;b:92672;h:92936;g:93249;d:93556;b:93779;d:94058;g:94305;h:94577;b:94848;d:95155;g:95378;d:95649;b:95846;h:96118;g:96366;d:96637;tap:96942;d:97190;g:97419;b:97726;tap:98217;g:98473;tap:98753;b:98992;h:99273;g:99691;d:100063;g:100269;d:100532;h:100830;g:101102;tap:101374;d:101604;g:101868;b:102090;h:102354;g:102600;d:102872;tap:103129;d:103376;g:103573;h:103861;g:104125;tap:104355;d:104644;g:104892;d:105155;b:105369;d:105741;g:105930;d:106244;tap:106482;h:106917;g:107350;d:107693;tap:107941;d:108237;h:108517;g:108789;d:109070;tap:109283;d:109546;g:109762;h:110076;b:110401;d:110665;g:110888;d:111143;b:111341;h:111612;g:111910;d:112158;tap:112380;d:112653;b:112832;h:113113;g:113393;d:113670;tap:113870;b:114239;b:114437;h:114700;g:114980;d:115236;b:115416;d:115688;g:115894;h:116174;tap:116455;d:116753;g:116974;d:117228;b:117452;h:117766;g:117979;d:118252;tap:118507;h:118753;g:119002;d:119290;b:119496;d:119784;g:120007;d:120304;tap:120558;h:120823;g:121086;d:121335;b:121589;d:121852;g:122052;h:122321;tap:122577;d:122832;g:123096;d:123376;b:123582;h:123897;g:124151;d:124407;tap:124646;d:124943;g:125149;b:125404;h:125702;g:125908;d:126214;tap:126460;d:126819;g:127075;d:127863;tap:128136;d:128907;g:129146;d:129642;b:129921;tap:130227;g:130744;d:131025;tap:131271;d:131550;h:131790;g:132054;d:132317;tap:132549;h:132795;d:133066;d:133351;g:133586;tap:133885;g:134121;tap:134393;d:134673;g:134887;tap:135206;b:135378;d:135667;g:135883;tap:136208;tap:136389;d:136754;g:136926;d:137286;b:137484;tap:137733;g:137962;d:138269;tap:138507;d:138800;g:139001;tap:139290;b:139521;d:139808;g:140023;tap:140311;tap:140567;d:140906;g:141099;d:141410;b:141640;tap:141903;g:142126;d:142381;tap:142603;d:142884;g:143124;tap:143378;b:143600;d:143889;h:144161;g:144416;d:144648;b:144893;tap:145166;tap:145396;h:145651;g:145931;d:146180;tap:146442;d:146722;g:146931;h:147225;g:147543;d:147790;tap:148013;d:148277;g:148490;h:148729;b:149009;d:149273;g:149513;d:149768;tap:149998;h:150247;g:150560;d:150815;b:151054;tap:151352;g:151558;h:151829;tap:152118;d:152382;g:152620;tap:152884;g:153188;h:153403;g:153737;d:153946;b:154157;d:154421;g:154636;h:154924;tap:155197;d:155430;g:155640;d:155921;g:156185;h:156466;b:156713;d:156936;g:157158;d:157388;tap:157678;h:157917;b:158204;d:158429;g:158666;d:158921;tap:159151;h:159415;b:159662;d:159929;tap:160157;d:160428;g:160661;b:161031;d:161253;g:161485;d:161819;tap:162009;h:162339;g:162547;d:162826;tap:163089;d:163369;g:163560;tap:163839;b:164052;d:164316;g:164505;tap:164761;tap:165059;h:165272;g:165528;d:165863;b:166076;h:166373;g:166587;d:166834;tap:167123;d:167411;b:167642;h:167889;tap:168103;d:168342;g:168560;d:168862;tap:169109;h:169405;d:169686;d:169934;g:170181;d:170532;tap:170795;d:171051;g:171249;b:171742;h:173382;b:174830;h:176431;g:178129;d:179736;g:181343;d:182844;tap:184390;h:186159;g:187813;tap:189476;d:191076;g:192630;h:194196;tap:195293;tap:195852;g:196345;d:196946;b:197429;d:198755;tap:198977;d:199269;g:199480;d:199794;tap:200024;h:200272;g:200551;b:200858;d:201185;g:201368;d:201654;tap:201886;d:202187;b:202461;d:202846;tap:203060;d:203320;g:203538;d:203794;h:204129;");
            /*21*/ sequences.add("g:1176;tap:1416;tap:1641;d:1869;g:2068;tap:2297;b:2486;tap:2742;g:2948;tap:3211;b:3408;tap:4268;g:4491;d:4730;tap:4902;h:5116;g:5828;tap:6066;b:6272;d:6495;g:6716;d:7572;g:7836;tap:8074;b:8272;tap:8511;g:8741;d:9233;tap:9439;h:9655;g:9867;tap:10081;b:10255;d:10460;g:10885;tap:11099;b:11321;d:11544;tap:11765;h:12600;g:12856;tap:13095;tap:13333;h:13555;g:14238;d:17346;b:20935;g:21766;d:22065;tap:22295;tap:22701;g:23274;tap:23814;b:24037;d:24309;g:25028;tap:25315;b:25964;h:26691;g:26971;tap:27329;b:27713;d:28597;g:28813;tap:29012;b:29431;d:30296;g:30528;h:31029;b:31763;tap:31981;g:32226;d:32585;b:32775;g:33266;tap:33486;b:33686;d:33892;g:34425;tap:34705;b:34960;tap:35257;g:35496;g:36060;tap:36316;g:36529;tap:36760;g:36925;tap:37173;g:37370;tap:37585;d:38196;tap:38435;d:38715;g:38962;tap:39234;g:40699;tap:41021;g:41743;tap:42022;b:42261;d:42483;g:42665;tap:43471;g:43676;tap:43940;b:44281;tap:44719;g:44924;tap:45172;b:45370;tap:45641;g:46414;tap:46660;g:46908;tap:47154;tap:47485;d:48042;g:48273;tap:48487;tap:48734;d:48972;g:49720;tap:50071;tap:50401;d:50814;g:51646;tap:51891;b:52096;h:52479;g:53229;tap:53475;tap:53739;h:54135;b:54730;tap:54982;g:55220;b:55733;tap:56280;g:56486;d:56684;tap:56980;tap:57473;g:58086;g:58577;b:58898;tap:60611;g:61341;tap:61603;b:61835;g:62081;g:62453;tap:63326;tap:63738;b:64043;g:65087;d:65840;b:66636;g:67491;b:68310;tap:69115;g:69937;d:70175;b:70398;tap:70778;g:71608;d:72463;b:73308;tap:74159;g:74906;d:75691;b:76520;g:76970;b:77345;tap:78168;g:78969;d:79841;b:80292;tap:80658;g:81528;d:82001;tap:82298;b:82657;tap:82908;g:83106;d:83345;b:83518;tap:83889;g:84439;d:84645;b:84834;tap:85081;g:85295;d:85567;tap:86199;d:86538;g:86710;d:86958;tap:87188;d:87637;g:87834;d:88122;tap:88337;d:88609;g:88847;b:89619;tap:89899;g:90080;h:90498;b:91072;tap:91294;g:91488;d:91747;b:91945;h:92249;b:92649;d:92873;g:93053;tap:93287;tap:93490;h:93849;b:94345;tap:94609;g:94822;d:95502;b:96223;tap:96429;g:96643;d:97023;b:97579;tap:97826;g:97999;tap:98230;b:98444;d:98825;b:99645;d:100085;b:100461;d:100945;b:101337;d:101762;b:102168;tap:102526;g:102931;g:103364;b:103720;tap:104145;g:104375;d:104597;b:104836;tap:105334;g:105636;d:105875;b:106146;tap:106613;g:107046;d:107530;b:107747;tap:107951;g:108528;d:109417;b:109882;tap:110290;g:111162;d:111458;b:111664;tap:111960;g:112781;d:113165;b:113569;tap:113710;g:114397;d:114879;b:115209;tap:115700;g:116108;d:116938;b:117805;tap:118665;b:120367;h:121982;g:122821;tap:123067;b:123266;h:123667;g:124505;d:124971;h:125395;g:126216;tap:126487;b:126701;h:127082;g:127904;tap:128167;b:128372;h:128782;g:129616;d:129880;tap:130093;b:130511;tap:131341;g:131579;d:131810;h:132190;g:133041;tap:133297;tap:133486;h:133887;g:134727;d:134964;b:135162;h:135595;g:136419;tap:136706;b:136920;h:137301;g:138110;tap:138349;b:138554;h:139006;g:139673;tap:139927;b:140242;h:140649;g:141544;d:141772;tap:141960;b:142379;tap:142859;g:143259;tap:143691;b:144100;d:144516;g:144884;tap:145342;");
            /*22*/ sequences.add("g:1091;tap:1322;g:1528;tap:1766;g:1947;tap:2145;d:2417;g:2782;tap:2979;d:3477;g:4170;d:4401;g:5291;tap:5587;g:5802;tap:6007;g:6189;tap:6394;tap:6560;d:6881;g:7432;tap:7653;d:8005;g:8583;tap:8763;tap:8993;d:9193;g:9860;tap:10122;g:10394;g:10743;tap:10889;g:11111;d:11553;g:12112;tap:12309;d:12606;g:13144;tap:13329;tap:13577;h:13776;g:14497;tap:14695;tap:14925;tap:15189;g:15403;d:15609;h:15931;g:16574;tap:16742;d:17078;g:17792;tap:17965;d:18212;g:18909;tap:19107;b:19337;g:19998;tap:20204;b:20516;g:21043;tap:21297;b:21619;g:22246;tap:22460;b:22640;d:22863;g:23814;d:24831;g:25128;tap:25399;b:25640;d:25893;g:26100;tap:26340;g:26690;tap:27055;g:27310;tap:27532;g:27722;tap:27961;g:28108;d:28408;g:28957;tap:29220;b:29525;g:30112;tap:30317;h:30735;g:31358;tap:31589;g:31789;tap:32033;g:32223;tap:32429;g:32585;d:32933;g:33465;tap:33646;b:34027;g:34650;tap:34848;d:35281;g:35976;tap:36165;b:36471;d:37074;g:37354;tap:37667;b:38143;tap:38464;g:38727;d:39138;b:39455;tap:40111;g:40308;d:40523;b:40712;tap:41324;g:41472;d:41661;b:42425;tap:42648;g:42772;d:43247;tap:43428;g:43642;d:44271;b:44444;tap:44634;g:44774;h:45314;tap:45503;g:45693;g:45874;g:46089;g:46237;g:46369;g:46518;tap:46756;b:46896;d:47186;tap:47400;g:47647;g:47894;b:48035;b:48218;tap:48527;b:48717;g:48905;tap:49061;b:49402;g:49516;d:50096;b:50343;tap:50559;tap:50735;d:50938;tap:51143;g:51308;tap:51489;g:51688;tap:51902;d:52725;g:52942;tap:53256;g:54070;tap:54511;g:54915;tap:55328;g:55871;tap:56159;g:56422;tap:56810;tap:57322;d:57602;tap:57841;d:58115;tap:58326;d:58573;h:58854;tap:59342;d:59564;tap:59721;h:59969;tap:60449;d:60664;b:61023;tap:61773;d:61970;tap:62201;d:62415;tap:62572;d:62778;tap:62943;h:63257;tap:63708;d:63922;tap:64104;b:64335;tap:64837;d:65025;tap:65199;h:65371;g:66063;d:66292;tap:66475;d:66959;tap:67123;d:67297;tap:67453;tap:68022;d:68180;tap:68350;h:68537;g:68959;d:69158;b:69353;tap:69725;tap:70484;d:70754;g:71571;tap:71972;d:72278;g:72598;tap:72966;tap:73180;d:73444;tap:73848;d:74112;tap:74392;d:74574;tap:74721;d:74887;tap:75035;h:75391;tap:75943;d:76091;tap:76264;b:76527;tap:77137;d:77286;g:77532;tap:78284;d:78515;tap:78762;d:79000;tap:79148;d:79338;tap:79478;g:79744;tap:80268;d:80432;tap:80580;b:80835;tap:81329;d:81468;tap:81633;g:81831;tap:82590;d:82787;tap:83470;d:83961;g:84553;h:84904;tap:85143;d:85382;g:85809;d:86359;tap:86763;g:87646;tap:87919;g:88425;tap:88687;d:89120;tap:89359;g:89615;tap:90247;d:90695;g:91616;h:92289;tap:92568;d:92995;g:93378;d:93873;tap:94315;g:94995;b:95250;g:95759;tap:96102;g:96774;b:97134;g:97906;g:98789;g:100608;d:100813;g:100947;d:101119;tap:101217;d:101399;g:101563;d:101720;b:101877;d:102025;tap:102157;h:102305;g:102455;d:102626;b:102775;tap:102973;tap:103171;d:103376;g:103541;d:103755;tap:103962;d:104151;g:104298;d:104489;tap:104694;d:104908;tap:105106;g:105532;g:106020;g:106324;b:106539;d:106728;g:107425;d:107572;tap:107737;d:107984;g:108472;d:108629;tap:108761;h:108983;g:109578;d:109767;tap:109949;d:110139;tap:110303;d:110484;b:110873;tap:111356;d:111504;g:111735;tap:112260;d:112399;tap:112572;h:112786;tap:113550;d:113676;tap:114049;g:114506;g:114663;b:114887;g:115348;b:115538;tap:115818;b:116469;g:116639;tap:116894;tap:118168;d:118579;tap:118876;d:119260;tap:119451;d:119795;tap:120042;d:120339;tap:120611;d:121070;tap:121390;d:121825;tap:122063;d:122269;tap:122459;d:122615;tap:122772;h:123002;tap:123512;d:123692;g:123973;tap:124549;d:124730;tap:124861;b:125133;tap:125792;d:126039;tap:126327;d:126525;tap:126683;d:126846;tap:127019;g:127266;tap:127863;d:128028;b:128349;tap:128925;d:129106;tap:129246;g:129672;");
            /*25*/ sequences.add("tap:4765;d:5161;g:5603;d:6038;g:6470;d:6960;g:7502;d:8092;tap:8532;d:8959;tap:9359;d:9728;tap:10129;d:10579;tap:10996;d:11479;tap:11930;d:12355;tap:12865;d:13357;d:13791;h:14216;d:14666;h:15058;d:15518;h:15923;d:16328;d:16812;tap:17214;h:17599;d:18017;h:18474;tap:18900;h:19592;d:20189;h:20706;d:21211;h:21796;d:22322;h:22822;tap:24512;g:26571;tap:26866;g:27148;tap:27583;g:27982;tap:28367;g:28781;tap:29215;g:29598;d:29882;g:30340;tap:30764;g:31215;tap:31633;h:32043;g:32468;tap:32851;d:33248;g:33732;h:34092;b:34459;g:34994;tap:35436;g:35779;tap:36237;g:36604;tap:36968;g:37415;d:37824;g:38122;tap:38554;g:38906;b:39204;d:39484;g:39859;tap:40227;b:40578;g:40959;tap:41310;d:41691;g:42173;tap:42503;b:42945;tap:43337;g:43684;h:44212;g:45487;tap:45866;g:46516;tap:46993;g:47356;tap:47862;g:48237;tap:48678;g:49119;tap:49604;g:50021;tap:50547;g:50898;d:51282;g:51671;tap:52080;g:52498;tap:52879;g:53312;tap:53714;d:54118;g:54568;tap:54977;h:55382;g:55791;tap:56233;b:56675;g:57075;tap:57517;g:57901;tap:58319;h:58706;h:59073;d:59499;g:59879;tap:60238;g:60687;tap:61100;d:61462;g:61859;tap:62285;g:62697;tap:63165;g:63519;b:63944;d:64360;g:64781;tap:65178;g:65626;h:66014;b:66578;tap:67057;g:67450;d:67830;b:68235;g:68635;h:69015;b:70299;d:70645;g:71046;tap:71451;g:75616;tap:76089;g:76481;tap:76886;g:77319;d:77702;g:78159;d:78548;g:78968;tap:79400;d:79789;g:80175;tap:80569;d:81010;g:81391;tap:81786;d:82203;g:82595;tap:83036;d:83460;g:83791;tap:84225;d:84665;h:85079;b:85458;tap:85916;d:86305;g:86680;tap:87084;d:87474;g:87891;tap:88272;d:88680;g:89052;tap:89458;d:89862;g:90250;tap:90635;d:91023;g:91453;tap:91804;d:92176;b:92659;tap:93131;g:93528;tap:93977;d:94818;g:96045;d:96455;g:96820;d:97203;g:97620;d:98009;b:98330;tap:98755;g:99154;d:99502;g:99928;d:100333;b:100693;d:101119;g:101465;d:101870;g:102238;d:102672;b:103022;d:103478;g:103801;tap:104248;g:104616;d:105066;b:105616;d:106047;h:106430;g:106781;d:107231;tap:107615;d:108080;g:108472;d:109065;g:109586;d:109994;g:110479;d:110791;g:111242;d:111684;b:111989;tap:112414;g:112839;d:113258;tap:113679;d:114469;g:114850;d:115367;g:115965;d:116333;b:116734;h:117149;g:117513;d:117978;tap:118320;d:118746;g:119150;h:119526;b:119935;d:120335;g:120757;tap:121145;d:122028;b:123257;d:123744;g:124099;tap:124499;b:125325;d:125733;g:126647;d:127033;g:127660;d:128102;tap:128506;d:128947;b:129298;d:129723;g:130064;tap:130487;b:130913;d:131675;g:132273;tap:132721;b:133319;d:133700;g:134046;d:134538;tap:134909;d:135280;g:135702;d:136112;tap:136486;d:136903;g:137278;d:137637;b:138047;d:138471;g:138767;d:139268;tap:139611;d:139989;g:140431;d:140856;tap:141237;d:141684;g:142127;d:142468;b:142789;d:143277;g:143581;d:143973;b:144440;d:144819;d:145276;g:145944;d:146328;h:146696;tap:147170;h:147547;g:147927;h:148327;tap:148799;h:149138;g:149572;h:150326;tap:150718;h:151078;g:151453;h:152277;g:152691;h:153654;g:154026;h:154984;g:155274;h:156404;g:156601;d:156906;tap:157236;d:157640;g:158046;d:158450;b:158807;d:159178;g:159811;d:160141;b:160575;d:160955;tap:161324;h:161732;g:162113;d:162473;tap:162889;d:163257;g:163602;h:163979;tap:164383;d:164751;g:165152;d:165512;g:165946;h:166792;g:167512;d:167776;b:168079;d:168531;g:168835;d:169327;b:169760;h:170739;d:171210;tap:171507;h:171712;g:171920;d:172108;tap:172347;g:172620;g:172857;b:173079;g:173589;d:173872;tap:174236;h:174541;d:174892;tap:175237;g:175548;b:175924;g:176220;tap:176596;d:176985;h:177299;tap:177658;g:177971;b:178330;g:178686;tap:179054;d:179413;h:179789;tap:180164;g:180520;b:180920;g:181335;tap:181701;d:182061;h:182469;tap:182855;d:183355;g:183706;d:184223;g:185022;d:185422;h:186621;b:186967;g:187326;b:187752;g:188087;b:188512;g:188901;tap:189239;g:189652;b:190037;g:190425;tap:190831;b:191263;g:191705;d:192059;tap:192457;h:192884;g:193258;d:193646;b:194038;g:194452;tap:194866;b:195253;d:195600;g:195984;h:196405;tap:196765;d:197190;b:197588;d:198018;g:198385;d:198781;b:199144;d:199521;g:199808;h:200226;tap:200639;d:201036;g:201511;h:201893;b:202334;d:202622;g:202918;tap:203492;g:203888;b:204281;g:204632;tap:205031;b:205442;g:205823;tap:206174;b:206583;g:206935;tap:207335;b:207769;g:208186;tap:208553;b:208988;g:209376;tap:209680;h:209869;g:210270;d:210609;tap:210955;h:211309;g:211713;h:212107;d:212347;h:212698;tap:212996;h:213307;g:213579;h:213908;tap:214139;d:214412;g:214824;tap:215218;g:215553;tap:216016;g:216416;tap:216838;h:216891;g:217238;h:217644;tap:217989;h:218386;g:218807;d:219178;tap:219583;h:219943;g:220271;d:220627;tap:220956;h:221310;g:221672;d:221985;tap:222322;h:222659;g:222999;d:223395;tap:223743;h:224045;g:224394;h:224736;b:225044;g:225421;g:225802;b:226127;tap:226478;h:226763;tap:227092;d:227448;g:227760;h:228128;tap:228549;d:228925;g:229330;h:229627;b:229963;d:230331;g:230645;h:230999;tap:231383;d:231767;g:232168;h:232544;tap:232927;d:233256;g:233641;h:233946;tap:234258;d:234575;tap:234984;h:235368;tap:235706;d:235978;g:236378;d:236820;tap:237141;h:237542;g:237950;d:238280;tap:238600;h:239059;g:239447;d:239860;b:240174;h:240541;g:240946;d:241267;g:241667;d:242068;tap:242416;h:242817;g:243250;d:243675;b:244084;h:244494;g:244848;d:245249;tap:245732;h:246161;g:246611;d:247077;b:247487;h:247928;b:248282;d:248699;g:249033;h:249458;b:249787;d:250092;g:250380;h:250716;tap:251073;d:251420;g:251839;h:252331;tap:252748;d:253141;g:253545;h:253935;tap:254330;d:254560;g:254766;h:255078;tap:255520;d:255768;g:255947;h:256253;tap:256657;g:256921;h:257193;tap:257564;g:258107;d:258410;b:258774;d:259163;g:259359;h:259672;tap:259869;d:260059;b:260240;g:260446;tap:260636;tap:260841;b:261259;g:261474;tap:261703;d:261974;b:262155;g:262471;tap:262858;d:263122;g:263287;h:263534;b:263714;d:264029;g:264250;d:264547;tap:264728;d:264966;b:265133;g:265453;tap:265716;tap:265947;h:266210;g:266615;d:266853;b:267068;g:267274;tap:267473;d:267832;d:268144;g:268462;b:268727;g:269199;tap:269454;d:269677;h:269882;tap:270204;b:270561;h:270831;g:271228;d:271542;tap:271889;d:272235;g:272540;h:272907;tap:273204;d:273571;g:273859;h:274197;tap:274585;d:274930;g:275331;h:275693;tap:276045;d:276396;g:276763;h:277076;tap:277427;d:277739;g:278115;h:278483;tap:278755;d:278993;g:279332;h:279571;b:279826;d:280185;tap:280507;h:280753;g:281042;d:281347;b:281660;tap:281881;tap:282299;h:282587;g:282962;d:283202;tap:283606;b:283837;h:284150;tap:284413;d:284745;g:285086;tap:285467;b:285875;d:286218;tap:286606;h:286902;d:287369;g:287715;tap:287987;b:288347;h:288628;g:288947;h:289307;tap:289537;d:289995;g:290348;d:290831;b:291245;h:291660;g:291923;d:292279;tap:292577;h:292871;g:293268;d:293611;b:293971;h:294396;tap:294660;d:294988;b:295364;h:295723;tap:296027;d:296345;g:296699;h:297116;b:297420;h:297796;tap:298168;h:298551;g:298864;d:299260;tap:299548;h:299904;g:300210;d:300521;g:300866;d:301212;b:301541;h:301900;g:302259;d:302660;g:303028;d:303411;tap:303799;h:304104;tap:304545;d:304892;tap:305347;d:305794;tap:306164;d:306493;g:306814;d:307264;b:307627;d:308044;g:308396;d:308804;tap:309200;d:309514;g:309842;d:310226;tap:310574;h:310974;g:311357;d:311694;tap:312087;h:312407;g:312828;d:313207;tap:313563;d:313984;h:314356;g:314742;tap:315090;d:315402;h:315741;g:316092;tap:316471;d:316827;h:317206;g:317590;tap:317965;d:318358;h:318725;g:319121;tap:319497;d:319910;h:320244;tap:320633;d:321073;g:321477;d:322025;tap:322330;d:322690;b:322989;d:323388;g:323737;h:323983;tap:324190;d:324511;g:324724;b:325037;b:325260;g:325424;d:325813;tap:326287;d:326609;g:326880;h:327249;b:327608;h:327988;g:328287;d:328681;tap:329098;d:329478;g:329821;h:330230;b:330618;d:330954;tap:331341;d:331725;g:332096;d:332464;b:332836;d:333169;g:333563;d:333801;tap:334305;h:334704;g:335068;d:335330;tap:335672;d:336069;g:336387;d:336778;tap:337107;d:337512;g:337917;d:338305;tap:338739;d:339160;g:339651;d:340276;b:340668;h:341019;g:341395;d:341829;tap:342184;h:342601;b:342952;d:343335;g:343729;h:344146;tap:344501;d:344890;g:345257;h:345604;b:346000;d:346394;g:346744;h:347124;tap:347529;g:347926;b:348939;h:350102;g:350835;d:351219;b:351598;");
            /*30*/ sequences.add("tap:4758;d:5170;g:5547;d:5981;tap:6369;g:7209;b:7622;g:8020;tap:8544;g:8839;b:9265;d:9732;g:10117;tap:10566;g:11040;tap:11457;g:11842;d:12268;g:12640;tap:13057;b:13475;tap:13892;g:14342;tap:14775;g:15114;d:15532;b:15957;tap:16408;g:16805;tap:17248;b:17622;d:18090;g:18524;tap:18896;b:19338;h:19731;g:20181;tap:20404;b:20609;d:20989;g:21422;tap:21831;h:22215;b:22595;tap:22776;g:22949;d:23490;g:23878;tap:24303;g:24732;tap:25138;g:25591;tap:25962;h:26397;b:26813;tap:27229;g:27644;d:28067;g:28488;tap:28937;g:29317;h:29684;b:30080;g:30328;tap:30560;b:31013;g:31380;tap:31779;g:32189;b:32709;tap:33052;g:33399;tap:33817;g:34321;d:34795;d:34968;d:35140;tap:35581;g:35812;tap:36026;d:36406;tap:36846;g:37254;b:37664;g:38059;tap:38485;g:38955;tap:39356;g:39719;b:40129;g:40318;tap:40525;g:40992;tap:41180;d:41435;h:41698;d:41887;tap:42259;g:42668;tap:43109;g:43550;tap:43765;d:43970;tap:44334;g:44524;d:44737;tap:44975;g:45173;b:45574;d:46016;tap:46392;g:46779;g:46945;b:47142;b:48066;g:48900;tap:49696;d:49927;h:50165;tap:50723;g:51423;b:52202;g:53048;g:53255;g:53501;tap:53926;d:54334;g:54751;g:54941;g:55104;tap:55547;d:55981;b:56389;g:56778;tap:57227;d:57668;tap:58077;g:58478;tap:58910;g:59360;d:59732;tap:60158;h:60546;d:61004;tap:61421;d:61834;h:62218;tap:62627;d:63117;h:63469;d:63699;tap:63955;tap:64359;g:65117;d:65600;b:65976;h:66364;g:66813;tap:67270;g:68108;d:68880;h:69626;d:69856;tap:70062;g:70612;g:71424;d:72252;b:72643;d:73024;g:73915;tap:74728;b:75556;h:75961;b:76766;h:77257;b:77645;g:78005;h:78462;b:78859;g:79232;h:79649;b:80073;h:80529;g:80970;tap:81412;b:81816;h:82205;g:82616;tap:83041;b:83461;h:83888;g:84345;tap:84771;b:85154;h:85543;g:85931;tap:86365;g:86783;d:87139;g:87631;tap:88089;b:88495;h:88875;tap:89308;b:89705;h:90123;tap:90572;b:90977;h:91370;tap:91804;tap:92230;b:92633;h:93006;g:93423;d:93849;b:94299;h:94732;g:95151;d:95555;b:95955;tap:96319;g:96779;h:97196;b:97654;h:98079;g:98488;d:98865;b:99290;h:99670;g:100104;d:100517;b:100950;h:101367;g:101765;d:102248;b:102642;h:103066;g:103433;d:103883;b:104289;d:104701;g:105143;b:105562;g:105966;b:106354;g:106780;b:107194;g:107596;tap:108022;g:108455;tap:108897;d:109281;g:109722;tap:110165;g:110583;tap:110983;d:111379;g:111788;tap:112221;d:112655;g:113064;tap:113458;d:113898;tap:114349;d:114775;tap:115176;g:115626;g:116035;b:116373;b:116530;b:116703;g:117204;tap:117671;d:118060;h:118902;h:119310;d:119632;tap:119946;g:120159;tap:120578;g:120982;tap:121383;d:121622;h:121844;tap:122220;g:122609;b:123026;tap:123265;d:123480;tap:123860;g:124273;tap:124690;d:125124;tap:125574;g:125966;b:126334;tap:126740;d:127197;tap:127631;g:128052;b:128449;g:128833;tap:129267;d:129706;tap:130148;g:130554;b:130941;tap:131330;d:131831;tap:132244;g:132604;b:132954;tap:133437;g:133911;b:134307;tap:134704;d:135138;tap:135539;g:135948;tap:136359;d:136604;h:136752;tap:137202;g:137607;d:138089;tap:138492;d:138919;h:139297;tap:139698;g:140148;b:140549;g:140965;d:141414;tap:141864;g:142240;tap:142615;d:143028;tap:143487;g:143928;b:144385;g:144698;tap:145081;d:145503;h:145920;d:146361;tap:146803;g:147236;tap:147641;d:148074;tap:148500;g:148900;tap:149280;d:149693;tap:150150;g:150554;tap:150962;d:151375;tap:151796;g:152192;tap:152576;d:152977;tap:153400;g:153811;tap:154220;d:154678;tap:155070;g:155474;tap:155896;d:156288;tap:156733;g:157198;tap:157582;d:158019;tap:158411;g:158812;tap:159223;d:159688;tap:160072;g:160539;tap:161367;d:161823;b:162212;h:162633;b:163016;h:163458;b:163851;h:164307;b:164749;h:165164;g:165561;d:165991;b:166375;h:166801;g:167225;d:167657;b:168070;h:168543;g:168889;d:169322;b:169693;h:170150;g:170537;d:170934;b:171367;h:171785;g:172172;d:172590;b:172982;h:173440;g:173837;d:174269;b:174666;h:175090;g:175491;d:175916;b:176337;h:176712;g:177134;d:177610;b:178005;h:178406;g:178811;d:179227;b:179669;h:180103;g:180483;d:180923;b:181373;h:181800;g:182223;d:182640;b:183032;h:183481;g:183857;d:184340;b:184753;h:185170;g:185574;d:185967;tap:186375;d:186832;g:187208;tap:187641;g:188063;tap:188497;d:188884;g:189524;d:189746;tap:189935;h:190117;g:190582;d:191024;tap:191378;h:192174;g:192608;d:193048;tap:193279;g:193484;b:193877;g:194293;b:194702;b:194882;b:195064;tap:195557;g:195936;d:196385;tap:196582;h:196763;g:197182;d:197665;tap:198079;h:198528;g:198940;tap:199331;b:199724;tap:200156;d:200445;g:200625;d:200881;tap:201037;d:201317;g:201514;d:201740;tap:202230;d:202648;g:203065;h:203289;b:203484;d:203732;g:203922;tap:204371;g:204746;b:205131;g:205544;d:205994;g:206407;h:206808;h:207284;b:207669;d:208078;b:208504;h:208871;tap:209304;g:209543;b:209732;b:209932;tap:210137;b:210571;tap:210979;b:211389;g:211803;d:212290;b:213028;g:213462;g:215318;h:216682;b:218335;");

            if(position<sequences.size()) {
               // sequenceARealiser = traiterSequences( sequences.get(position));
                sequenceARealiser = ( sequences.get(position));
                couples = sequenceARealiser.split(";");
                int nbTotalMvts = couples.length;
                TextView nbTotal = swipeView.findViewById(R.id.nbTotal);
                nbTotal.setText(" / " + String.valueOf(2*nbTotalMvts));

                TextView scoreOr=swipeView.findViewById(R.id.scoreOr);
                TextView scoreAr=swipeView.findViewById(R.id.scoreArgent);
                TextView scoreBr=swipeView.findViewById(R.id.scoreBronze);
                TextView hashtag=swipeView.findViewById(R.id.hashtags);
                hashtag.setText(listehashtags.get(position));

                int sOr=Math.round( (95*2*nbTotalMvts/100));
                int sAr=Math.round( (80*2*nbTotalMvts/100));
                int sBr=Math.round ( (75*2*nbTotalMvts/100));
                scoreOr.setText(String.valueOf(sOr));
                scoreAr.setText(String.valueOf(sAr));
                scoreBr.setText(String.valueOf(sBr));
            }
            sharedPreferences = getActivity().getSharedPreferences("prefs_joueur", MODE_PRIVATE);
            if(sharedPreferences.contains("trophy_niveau"+String.valueOf(position+1))){
                ImageView trophee;
                trophee=swipeView.findViewById(R.id.trophy);
                String trophy=sharedPreferences.getString("trophy_niveau"+String.valueOf(position+1),"");
                if(trophy.equals("or")){

                    trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_or));
                }
               else  if(trophy.equals("argent")){

                    trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_argent));
                }
               else if(trophy.equals("bronze")){

                    trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_bronze));
                }

            }

            //affichage du meilleur score pour le niveau

            if(!scoreDAO.obtenirScoreNiveau(String.valueOf(position+1)).equals("")){
                TextView meilleurScore=swipeView.findViewById(R.id.bestscore);
                meilleurScore.setText(scoreDAO.obtenirScoreNiveau(String.valueOf(position+1)));

            }

//            final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);
//            animator.setRepeatCount(ValueAnimator.INFINITE);
//            animator.setInterpolator(new LinearInterpolator());
//            animator.setDuration(9000L);
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    final float progress = (float) animation.getAnimatedValue();
//                    final float width = titre.getWidth();
//                    final float translationX = width * progress;
//                   titre.setTranslationX(translationX);
//
//                }
//            });
//            animator.start();


          /*  String imageFileName = IMAGE_NAME[position];
            int imgResId = getResources().getIdentifier(imageFileName, "drawable", "com.developpement.ogawi.scrollyourlife");
            imageView.setImageResource(imgResId);*/


            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }



    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {


            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {


            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {



        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {



            return true;
        }
    }


}
