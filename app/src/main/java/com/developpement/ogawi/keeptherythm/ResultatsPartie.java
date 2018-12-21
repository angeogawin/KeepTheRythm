package com.developpement.ogawi.keeptherythm;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.flubber.Flubber;
import com.developpement.ogawi.keeptherythm.bdd.Score;
import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;
import com.developpement.ogawi.keeptherythm.google.example.games.basegameutils.BaseGameActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.plattysoft.leonids.ParticleSystem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ResultatsPartie  extends BaseGameActivity {
    Button rejouer;
    Button accepter;
    Intent i;
    int niveau;//va de 1 a ...
    int score;
    int manques;
    int nbTotalMvts;//correspond au score max atteignable
    TextView niveauValue;
    TextView scoreValue;
    TextView manquesValue;
    TextView nbTotal;
    ImageView trophee;

    SharedPreferences sharedPreferences;
    MediaPlayer playerResults;
    int media_length;


    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_resultats_partie);
        GoogleSignInAccount googleAccount=GoogleSignIn.getLastSignedInAccount(this);
        if(googleAccount!=null){
            GamesClient gamesClient = Games.getGamesClient(ResultatsPartie.this, GoogleSignIn.getLastSignedInAccount(this));
            gamesClient.setViewForPopups(findViewById(R.id.container_pop_up));

        }

        playerResults= MediaPlayer.create(this, R.raw.airtone_nightrain);
        // float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
      //  playerResults.setVolume(0.8f, 0.8f);
        playerResults.setLooping(true);
        playerResults.start();
        i=getIntent();
        niveau=i.getExtras().getInt("niveau");
        score=i.getExtras().getInt("score");
        manques=i.getExtras().getInt("manques");
        nbTotalMvts=i.getExtras().getInt("nbTotalMvts");

        niveauValue=findViewById(R.id.niveauValue);
        scoreValue=findViewById(R.id.scoreValue);
        manquesValue=findViewById(R.id.manquesValue);
        nbTotal=findViewById(R.id.nbTotalResultats);
        trophee=findViewById(R.id.trophy);

        niveauValue.setText(String.valueOf(niveau));
        nbTotal.setText(" / "+String.valueOf(nbTotalMvts));
        ValueAnimator animator = ValueAnimator.ofInt(0, score);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                scoreValue.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();




     //   scoreValue.setText(String.valueOf(score)+" / "+String.valueOf(nbTotalMvts));
        manquesValue.setText(String.valueOf(manques));
        rejouer=findViewById(R.id.rejouer);
        accepter=findViewById(R.id.accepter);

        // Creating and load a  new InterstitialAd .
        mInterstitialAd = createNewIntAd();
        loadIntAdd();
        int sOr=Math.round( (95*nbTotalMvts/100));
        int sAr=Math.round( (80*nbTotalMvts/100));
        int sBr=Math.round ( (75*nbTotalMvts/100));

        sharedPreferences = getBaseContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);

        if(score>=sBr){
            //on actualise le niveau maximum atteint
            if(sharedPreferences.contains("niveau_max_atteint")){
                if(niveau>sharedPreferences.getInt("niveau_max_atteint",0)) {
                    sharedPreferences
                            .edit()
                            .putInt("niveau_max_atteint",niveau)
                            .apply();

                }

            }
            else{
                sharedPreferences
                        .edit()
                        .putInt("niveau_max_atteint",niveau)
                        .apply();
            }

            //actualisation de XP
            if(sharedPreferences.contains("scorexp")){

                    sharedPreferences
                            .edit()
                            .putInt("scorexp",sharedPreferences.getInt("scorexp",0)+score)
                            .apply();



            }
            else{
                sharedPreferences
                        .edit()
                        .putInt("scorexp",score)
                        .apply();
            }

               // Games.Achievements.unlock(getApiClient(),
                 //       getString(R.string.correct_guess_achievement));

            if(googleAccount!=null) {
                Games.getLeaderboardsClient(this, googleAccount).submitScore(
                        getString(R.string.leaderboard_id),
                        sharedPreferences.getInt("scorexp", 0));
            }


            //fonction pour debloquer si necessaire niveau suivant

            if(score>=sOr){
                //trophee or
                trophee.setImageDrawable(getResources().getDrawable(R.drawable.cup_gold));

                sharedPreferences
                        .edit()
                        .putString("trophy_niveau"+String.valueOf(niveau),"or")
                        .apply();

                KonfettiView viewKonfetti=findViewById(R.id.viewKonfetti);
                viewKonfetti.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(7,5f))
                        .setPosition(-50f, viewKonfetti.getWidth() + 700f, -50f, -50f)
                        .stream(150, 3000L);

                if(score==nbTotalMvts){
                    TextView text100pourcent=findViewById(R.id.text100pourcent);
                    ImageView clap=findViewById(R.id.clap);
                    text100pourcent.setVisibility(View.VISIBLE);
                    clap.setVisibility(View.VISIBLE);

                    //Reussite 100% niveau
                    if(googleAccount!=null) {
                        Games.getAchievementsClient(this, googleAccount).unlock(
                                getString(R.string.obtenir_100pourcent));
                    }
                }

                if(niveau==1){
                    //Reussite niveau1 avec or
                    if(googleAccount!=null) {
                        Games.getAchievementsClient(this, googleAccount).unlock(
                                getString(R.string.terminer_niveau1_or));
                    }
                }

                //Actualisation nombre total de trophées en or
                int nbTotalTorpheesOr=nombreTotalTropheesOrDifferents(sharedPreferences.getInt("niveau_max_atteint",0));

                if(googleAccount!=null) {
                    //Reussites trophees or
                    if(nbTotalTorpheesOr==3){

                        Games.getAchievementsClient(this,googleAccount).unlock(
                                getString(R.string.obtenir_3trophees_or));

                    }
                    else if(nbTotalTorpheesOr==6){

                        Games.getAchievementsClient(this,googleAccount).unlock(
                                getString(R.string.obtenir_6trophees_or));

                    }
                    else if(nbTotalTorpheesOr==9){

                        Games.getAchievementsClient(this,googleAccount).unlock(
                                getString(R.string.obtenir_9trophees_or));

                    }
                    else if(nbTotalTorpheesOr==15){

                        Games.getAchievementsClient(this,googleAccount).unlock(
                                getString(R.string.obtenir_15trophees_or));

                    }
                    else if(nbTotalTorpheesOr==30){

                        Games.getAchievementsClient(this,googleAccount).unlock(
                                getString(R.string.obtenir_30trophees_or));

                    }
                }









            }
           else if(score>=sAr){
                //trophee argent
                trophee.setImageDrawable(getResources().getDrawable(R.drawable.cup_silver));
                if(sharedPreferences.contains("trophy_niveau"+String.valueOf(niveau))){
                    if(sharedPreferences.getString("trophy_niveau"+String.valueOf(niveau),"").equals("bronze")){
                        sharedPreferences
                                .edit()
                                .putString("trophy_niveau"+String.valueOf(niveau),"argent")
                                .apply();
                    }
                }
                else {
                    sharedPreferences
                            .edit()
                            .putString("trophy_niveau" + String.valueOf(niveau), "argent")
                            .apply();
                }
            }


            else {
                //trophee bronze

                trophee.setImageDrawable(getResources().getDrawable(R.drawable.cup_bronze));
                if(!sharedPreferences.contains("trophy_niveau"+String.valueOf(niveau))){
                    sharedPreferences
                            .edit()
                            .putString("trophy_niveau" + String.valueOf(niveau), "bronze")
                            .apply();
                }



            }
            Flubber.with()
                    .animation(Flubber.AnimationPreset.ZOOM_IN) // Slide up animation
                    .repeatCount(0)                              // Repeat once
                    .duration(1000)                              // Last for 1000 milliseconds(1 second)
                    .createFor(trophee)                             // Apply it to the view
                    .start();

            //Reussites XP
            if(googleAccount!=null) {
            if(sharedPreferences.contains("scorexp")) {


                if (sharedPreferences.getInt("scorexp", 0) >= 50000) {
                    //verifier que achievement non debloques


                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_50000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 20000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_20000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 10000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_10000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 6000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_6000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 4000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_4000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 2000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_2000XP));

                } else if (sharedPreferences.getInt("scorexp", 0) >= 1000) {
                    //verifier que achievement non debloques

                    Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(
                            getString(R.string.obtenir_1000XP));

                }
            }

            }






        }
        else{
            String encouragement1="Allez, retente ta chance...";
            Drawable emoji1=getDrawable(R.drawable.emoji_muscle);
            String encouragement2="On repassera pour le rythme...";
            Drawable emoji2=getDrawable(R.drawable.emoji_langue);
            String encouragement3="Bien tenté, y'a du progrès à faire...";
            Drawable emoji3=getDrawable(R.drawable.emoji_langue);
            String encouragement4="La prochaine fois sera la bonne...";
            Drawable emoji4=getDrawable(R.drawable.emoji_muscle);
            ArrayList<String> encouragements=new ArrayList<>();
            ArrayList<Drawable> emojis=new ArrayList<>();
            emojis.add(emoji1);
            emojis.add(emoji2);
            emojis.add(emoji3);
            emojis.add(emoji4);

            encouragements.add(encouragement1);
            encouragements.add(encouragement2);
            encouragements.add(encouragement3);
            encouragements.add(encouragement4);
            Random alea = new Random();
            int num_musique = alea.nextInt(4) ;
            TextView textencouragement=findViewById(R.id.textencouragement);
            ImageView i=findViewById(R.id.emojiEncouragement);
            textencouragement.setText(encouragements.get(num_musique));
            i.setImageDrawable(emojis.get(num_musique));

        }

        if(aJoueTousLesNiveauxDeverouille(sharedPreferences.getInt("niveau_max_jouable",0))){
            TextView textNiveauDebloque=findViewById(R.id.textNiveauDebloque);
            textNiveauDebloque.setVisibility(View.VISIBLE);
            ImageView img=findViewById(R.id.cadenas);
            img.setVisibility(View.VISIBLE);

        }

        //Initialisation bdd
        final ScoreDAO scoreDAO=new ScoreDAO(getApplicationContext());
        final long id=scoreDAO.getIdMax();
        Score s=new Score(id,String.valueOf(niveau),String.valueOf(score));
        if(scoreDAO.obtenirScoreNiveau(String.valueOf(niveau)).equals("")){
            //on ajoute un nouveau score
            scoreDAO.ajouter(s);

        }
        else if(!scoreDAO.obtenirScoreNiveau(String.valueOf(niveau)).equals("")){

            if(Integer.valueOf(scoreDAO.obtenirScoreNiveau(String.valueOf(niveau)))<score){
                //on modifie un score existant en actualisant avec le score maxi atteint
                TextView msgNouveauMeilleurScore=findViewById(R.id.textScoreBattu);
                msgNouveauMeilleurScore.setVisibility(View.VISIBLE);
                Flubber.with()
                        .animation(Flubber.AnimationPreset.ZOOM_IN) // Slide up animation
                        .repeatCount(0)                              // Repeat once
                        .duration(1000)                              // Last for 1000 milliseconds(1 second)
                        .createFor(msgNouveauMeilleurScore)                             // Apply it to the view
                        .start();
                scoreDAO.modifier(s);
            }


        }





        rejouer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub


                Intent i=new Intent(getApplicationContext(),InGame.class);
               i.putExtra("niveau",niveau);
               startActivity(i);
               finish();



            }
        });

        accepter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //  zoneJeu = (RelativeLayout)findViewById(R.id.bandeaupourfleche);
                showIntAdd();
               /* startActivity(new Intent(ResultatsPartie.this,EcranAccueil.class));
                finish();*/


            }
        });



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

    public int nombreTotalTropheesOrDifferents(int niveauMax){

        int retour=0;

        for(int i=1;i<=niveauMax;i++){
            if(sharedPreferences.contains("trophy_niveau"+String.valueOf(i))){
                if("or".equals(sharedPreferences.getString("trophy_niveau"+String.valueOf(i),""))){
                   retour+=1;
                }


            }
        }
        return retour;
    }


    @Override
    protected void onResume() {
        super.onResume();
        playerResults.seekTo(media_length);
        playerResults.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        playerResults.pause();
        media_length=playerResults.getCurrentPosition();

    }

    private InterstitialAd createNewIntAd() {
        InterstitialAd intAd = new InterstitialAd(this);
        // set the adUnitId (defined in values/strings.xml)
        intAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        intAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                accepter.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                accepter.setEnabled(true);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                quitterPub();
            }
        });
        return intAd;
    }

    private void showIntAdd() {

// Show the ad if it's ready. Otherwise, toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            quitterPub();
        }
    }
    private void loadIntAdd() {
        // Disable the  level two button and load the ad.
        accepter.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
              //  .addTestDevice("29B51012AB141B85B95D278930B1EAAB")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }


    private void quitterPub() {

        startActivity(new Intent(ResultatsPartie.this,EcranAccueil.class));
        finish();
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
