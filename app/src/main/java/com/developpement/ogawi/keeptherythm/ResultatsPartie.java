package com.developpement.ogawi.keeptherythm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.developpement.ogawi.keeptherythm.bdd.Score;
import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;
import com.plattysoft.leonids.ParticleSystem;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ResultatsPartie  extends AppCompatActivity {
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
    ImageView trophee;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_resultats_partie);
        i=getIntent();
        niveau=i.getExtras().getInt("niveau");
        score=i.getExtras().getInt("score");
        manques=i.getExtras().getInt("manques");
        nbTotalMvts=i.getExtras().getInt("nbTotalMvts");

        niveauValue=findViewById(R.id.niveauValue);
        scoreValue=findViewById(R.id.scoreValue);
        manquesValue=findViewById(R.id.manquesValue);
        trophee=findViewById(R.id.trophy);

        niveauValue.setText(String.valueOf(niveau));
        scoreValue.setText(String.valueOf(score)+" / "+String.valueOf(nbTotalMvts));
        manquesValue.setText(String.valueOf(manques));
        rejouer=findViewById(R.id.rejouer);
        accepter=findViewById(R.id.accepter);


        sharedPreferences = getBaseContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);

        if((int)(score*100/nbTotalMvts)>=70){
            //on actualise le niveau maximum atteint
            if(sharedPreferences.contains("niveau_max_atteint")){
                if(niveau>sharedPreferences.getInt("niveau_max_atteint",0)) {
                    sharedPreferences
                            .edit()
                            .putInt("niveau_max_atteint",niveau+1)
                            .apply();

                }
            }
            else{
                sharedPreferences
                        .edit()
                        .putInt("niveau_max_atteint",niveau+1)
                        .apply();
            }

            //fonction pour debloquer si necessaire niveau suivant

            if(Math.round((int)(score*100/nbTotalMvts))>=90){
                //trophee or
                trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_or));
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
                }

            }
           else if(Math.round((int)(score*100/nbTotalMvts))>=80){
                //trophee argent
                trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_argent));
                sharedPreferences
                        .edit()
                        .putString("trophy_niveau"+String.valueOf(niveau),"argent")
                        .apply();
            }


            else {
                //trophee bronze

                trophee.setImageDrawable(getResources().getDrawable(R.drawable.trophy_bronze));
                sharedPreferences
                        .edit()
                        .putString("trophy_niveau"+String.valueOf(niveau),"bronze")
                        .apply();


            }

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

                startActivity(new Intent(ResultatsPartie.this,EcranAccueil.class));
                finish();


            }
        });



    }
}
