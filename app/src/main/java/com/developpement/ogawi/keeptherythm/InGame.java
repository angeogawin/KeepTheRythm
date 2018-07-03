package com.developpement.ogawi.keeptherythm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class InGame extends AppCompatActivity {//implements OnGesturePerformedListener{

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
    Chronometer mChronometer;
    Boolean elementActuelClickable;
    MediaPlayer mPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);
        Intent i = getIntent();
        int pos = i.getExtras().getInt("niveau");
        sequence = genererSequenceAleatoire(pos);
        indiceActuelSequence = 0;
        score = 0;
        mChronometer = new Chronometer(this);
        derniereAction = new StringModified();
        timeOfDerniereAction=0;
         symboleT=(RelativeLayout) findViewById(R.id.symbole);
        v = (GestureOverlayView) findViewById(R.id.gOverlay);
        scoreT=(TextView) findViewById(R.id.score);
        scoreT.setText(String.valueOf(score));
        elementActuelClickable=false;
        mPlayer = MediaPlayer.create(this, R.raw.tobias_weber_rooted_in_soil);

        // Toast.makeText(InGame.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
        /*flecheactuelle = findViewById(R.id.flecheactuelle);

        dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);*/
        dernierSymbole=sequence.get(0);

        isDrawerOpen = false;

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layoutingame);

        mTopToolbar = (Toolbar) findViewById(R.id.ingame_toolbar);
        setSupportActionBar(mTopToolbar);
        // These lines are needed to display the top-left hamburger button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Make the hamburger button work
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Change the TextView message when ListView item is clicked
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    messageTextView.setText("Menu Item at position " + position + " clicked.");;
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });


        derniereAction.setValueChangeListener(new StringModified.onValueChangeListener() {
            @Override
            public void onChange() {
                timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                tmin=listeTminToClick.get(indiceActuelSequence);
                if(timeOfDerniereAction>=tmin){
                    elementActuelClickable=true;

                }
                else{
                    elementActuelClickable=false;
                }

                if(elementActuelClickable) {
                    if (dernierSymbole.equals(derniereAction.getValeur())) {
                        indiceActuelSequence += 1;
                        actualiserScore();
                        if (indiceActuelSequence <= sequence.size() - 1) {
                            dernierSymbole = sequence.get(indiceActuelSequence);
                            Toast.makeText(InGame.this, dernierSymbole, Toast.LENGTH_SHORT).show();

                        } else if (indiceActuelSequence > sequence.size() - 1) {
                            Toast.makeText(InGame.this, "Jeu terminé", Toast.LENGTH_SHORT).show();
                            symboleT.removeAllViews();
                            mPlayer.stop();
                        }
                    } else {
                        Toast.makeText(InGame.this, "Erreur mouvement", Toast.LENGTH_SHORT).show();
                        //fin jeu
                        symboleT.removeAllViews();
                        mPlayer.stop();

                    }

                }
                else{
                    Toast.makeText(InGame.this, "Erreur timing", Toast.LENGTH_SHORT).show();
                    //fin jeu
                    symboleT.removeAllViews();
                    mPlayer.stop();
                }

                   // dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);





            }
        });


        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        v.setOnTouchListener(touchListener);
        v.post(new Runnable() {
            @Override
            public void run() {
                widthView=v.getWidth(); //height is ready
            }
        });

        lancerAnimation(sequence);

    }



    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
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

    // In the SimpleOnGestureListener subclass you should override
// onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {



            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            derniereAction.setVariable("tap");
          //  Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
           derniereAction.setVariable("touch");
          //  Toast.makeText(InGame.this, "touch", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

           // String direction=identifierDirectionGeste(e1,e2);
          //  Toast.makeText(InGame.this, String.valueOf(Math.abs(e2.getX()-e1.getX())), Toast.LENGTH_SHORT).show();



               /*if (Math.abs(e2.getX() - e1.getX()) < Math.abs(e2.getY() - e1.getY())) {


                   if (e2.getY() - e1.getY() > 0) {

                           derniereAction.setVariable("h");
                           Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                   } else if (e2.getY() - e1.getY() < 0) {

                           derniereAction.setVariable("b");
                           Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();

                   }
               }*/


            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

           if(Math.abs(event2.getY()-event1.getY())<Math.abs(event2.getX()-event1.getX())) {
                if (event2.getX() - event1.getX() > 0) {
                    derniereAction.setVariable("g");
            //        Toast.makeText(InGame.this, "left", Toast.LENGTH_SHORT).show();
                } else if (event2.getX() - event1.getX() < 0) {
                    derniereAction.setVariable("d");
                 //   Toast.makeText(InGame.this, "right", Toast.LENGTH_SHORT).show();
                }
            }
           else if (Math.abs(event2.getX() - event1.getX()) < Math.abs(event2.getY() - event1.getY())) {


                if (event2.getY() - event1.getY() > 0) {

                    derniereAction.setVariable("h");
                //    Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                } else if (event2.getY() - event1.getY() < 0) {

                    derniereAction.setVariable("b");
                    //Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();

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
        if (id == R.id.action_pauseplay) {
            Toast.makeText(InGame.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }


        else if(id==R.id.action_stop) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                mDrawer.openDrawer(GravityCompat.START);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



   /* @Override
    protected void onStart()
    {
        super.onStart();

        gLibrary =
                GestureLibraries.fromRawResource(this,
                        R.raw.gesture);

        if (!gLibrary.load()) {
            finish();
        }
        GestureOverlayView gOverlay = (GestureOverlayView)
                findViewById(R.id.gOverlay);

        gOverlay.addOnGesturePerformedListener(this);

    }
    public void onGesturePerformed(GestureOverlayView overlay, Gesture
            gesture) {
        ArrayList<Prediction> predictions =
                gLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {

            String action = predictions.get(0).name;
            if(action.equals("cl1")){
                derniereAction.setVariable("cl");
            }
            else if(action.equals("cr1")){
                derniereAction.setVariable("cr");

            }




            Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
        }
    }*/



    public void identifierDerniereAction(){

        //actualise string derniereAction;
    }

    public void actualiserScore(){
        //actualise int score

             score+=1;

             scoreT.setText(String.valueOf(score));




    }


    public ArrayList<String> genererSequenceAleatoire(int niveau){
        ArrayList<String> sequence=new ArrayList<>();
        //g: gauche d:droite, h:haut , b:bas tap: toucher bref, touch:toucher long, cl:circleleft,cr:circle right

        if(niveau<=5){
              int longueur=25;
              for(int i=0; i<longueur;i++){
                  String[] chars = {"g","d","tap"};
                  double aleatoire= (Math.random());
                  if(aleatoire<0.3){
                      sequence.add(chars[0]);
                  }
                  else if(aleatoire<=0.6){
                      sequence.add(chars[1]);
                  }
                  else if(aleatoire<=1){
                      sequence.add(chars[2]);
                  }

              }
        }
        else if(niveau<=10){
            int longueur=35;
            for(int i=0; i<longueur;i++){
                String[] chars = {"g","d","tap","touch","h","b"};
                double aleatoire= (Math.random());
                if(aleatoire<0.15){
                    sequence.add(chars[0]);
                }
                else if(aleatoire<0.30){
                    sequence.add(chars[1]);
                }
                else if(aleatoire<0.45){
                    sequence.add(chars[2]);
                }

                else if(aleatoire<0.60){
                    sequence.add(chars[3]);
                }
                else if(aleatoire<0.75){
                    sequence.add(chars[4]);
                }
                else if(aleatoire<=1){
                    sequence.add(chars[5]);
                }

            }
        }
        else if(niveau<=15){
            int longueur=55;
            for(int i=0; i<longueur;i++){
                String[] chars = {"g","d","tap","touch","h","b","cr","cl"};
                double aleatoire= (Math.random());
                if(aleatoire<0.12){
                    sequence.add(chars[0]);
                }
                else if(aleatoire<0.24){
                    sequence.add(chars[1]);
                }
                else if(aleatoire<0.36){
                    sequence.add(chars[2]);
                }
                else if(aleatoire<0.48){
                    sequence.add(chars[3]);
                }
                else if(aleatoire<0.60){
                    sequence.add(chars[4]);
                }

                else if(aleatoire<0.72){
                    sequence.add(chars[5]);
                }
                else if(aleatoire<0.84){
                    sequence.add(chars[6]);
                }
                else if(aleatoire<=1){
                    sequence.add(chars[7]);
                }


            }

        }
     /*   public static int randInt(int min, int max) {

            Random rand = new Random();
            return rand.nextInt((max - min) + 1) + min;
        }*/

        return sequence;
    }

    public String actualiserSymbole(ImageView image, ArrayList<String> sequence, int indiceActuel ){
        String directionFleche=sequence.get(indiceActuel);
        if(sequence.get(indiceActuel).equals("g")){
            image.setImageDrawable(getDrawable(R.drawable.fleche_gauche2));
        }
        else if(sequence.get(indiceActuel).equals("d")){
            image.setImageDrawable(getDrawable(R.drawable.fleche_droite2));
        }
        else if(sequence.get(indiceActuel).equals("h")){
            image.setImageDrawable(getDrawable(R.drawable.fleche_haut2));
        }
        else if(sequence.get(indiceActuel).equals("b")){
            image.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
        }
        else if(sequence.get(indiceActuel).equals("cr")){
            image.setImageDrawable(getDrawable(R.drawable.cr2));
        }
        else if(sequence.get(indiceActuel).equals("cl")){
            image.setImageDrawable(getDrawable(R.drawable.cl));
        }
        else if(sequence.get(indiceActuel).equals("tap")){
            image.setImageDrawable(getDrawable(R.drawable.tap));
        }
        else if(sequence.get(indiceActuel).equals("touch")){
            image.setImageDrawable(getDrawable(R.drawable.touch));
        }

        return directionFleche;
    }


    public  void lancerAnimation(ArrayList<String> sequence){
        listeTminToClick=new ArrayList<>();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mPlayer.start();
        int sommeduration=4000;
        listeTminToClick.add(sommeduration);


        int offset=0;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Toast.makeText(this, sequence.get(0)+sequence.get(1)+sequence.get(2), Toast.LENGTH_SHORT).show();
        for(int j=0;j<sequence.size();j++){
            String symbole=sequence.get(j);
            ImageView i=new ImageView(getApplicationContext());
            if(symbole.equals("g")){
                i.setImageDrawable(getDrawable(R.drawable.fleche_gauche2));
            }
            else if(symbole.equals("d")){
                i.setImageDrawable(getDrawable(R.drawable.fleche_droite2));
            }
            else if(symbole.equals("h")){
                i.setImageDrawable(getDrawable(R.drawable.fleche_haut2));
            }
            else if(symbole.equals("b")){
                i.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
            }
            else if(symbole.equals("cr")){
                i.setImageDrawable(getDrawable(R.drawable.cr2));
            }
            else if(symbole.equals("cl")){
                i.setImageDrawable(getDrawable(R.drawable.cl));
            }
            else if(symbole.equals("tap")){
                i.setImageDrawable(getDrawable(R.drawable.tap));
            }
            else if(symbole.equals("touch")){
                i.setImageDrawable(getDrawable(R.drawable.touch));
            }
           // Toast.makeText(this, String.valueOf(width), Toast.LENGTH_SHORT).show();
            final TranslateAnimation moveLefttoRight = new TranslateAnimation(
                    0, width, 0, 0);
            moveLefttoRight.setInterpolator(new LinearInterpolator());
            moveLefttoRight.setDuration(8000);
            moveLefttoRight.setFillAfter(true);
            moveLefttoRight.setStartOffset(offset);
            offset+=3000;
            sommeduration+=3000;
            listeTminToClick.add(sommeduration);
            symboleT.addView(i,j);


            i.startAnimation(moveLefttoRight);


        }






    }

}
