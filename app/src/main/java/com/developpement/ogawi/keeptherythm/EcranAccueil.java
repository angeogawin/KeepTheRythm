package com.developpement.ogawi.keeptherythm;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Random;

public class EcranAccueil extends AppCompatActivity {

    static final int NUM_ITEMS = 30;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    private Toolbar mTopToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    TextView messageTextView;
    ListView mDrawerListView;
    Boolean isDrawerOpen;
    Button btnJouer;
    static Intent i;

    private DrawerLayout intLayout;
    private AnimationDrawable animationDrawable;
    ArrayList<Integer> listeMusiqueAccueil;
    MediaPlayer playerAccueil;
    int media_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_accueil);
        btnJouer=(Button) findViewById(R.id.btnJouer);
        i=new Intent(getApplicationContext(),InGame.class);
        i.putExtra("niveau",1);
        int maxVolume = 50;
        isDrawerOpen=false;

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.

                i.putExtra("niveau",position+1);


            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layoutaccueil);

        mTopToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mTopToolbar);
        // These lines are needed to display the top-left hamburger button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        // Change the TextView message when ListView item is clicked
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //    messageTextView.setText("Menu Item at position " + position + " clicked.");;
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });


        listeMusiqueAccueil=new ArrayList<>();
        // **** ajout des musiques
        listeMusiqueAccueil.add(R.raw.vj_memes_paint_the_sky);
        listeMusiqueAccueil.add(R.raw.tobias_weber_rescue_me_instrumental);
        listeMusiqueAccueil.add(R.raw.tobias_weber_the_parting_glass_instrumental);
        Random alea = new Random();
        int num_musique = alea.nextInt(listeMusiqueAccueil.size()) ;

        playerAccueil= MediaPlayer.create(this, listeMusiqueAccueil.get(num_musique));
       // float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        playerAccueil.setVolume(0.4f, 0.4f);
        playerAccueil.setLooping(true);
        playerAccueil.start();

        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerAccueil.stop();
                startActivity(i);

            }
        });





        animationDrawable = (AnimationDrawable) mDrawer.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(5000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);
        final  View vi=findViewById(R.id.emiter_top_left);

//        vi.post(new Runnable() {
//            @Override
//            public void run() {
//              /*  new ParticleSystem(ModeSelectionActivity.this, 100, R.drawable.ic_gem_green, 30000)
//                        .setSpeedRange(0.2f, 0.5f)
//                        .oneShot(findViewById(R.id.center_anchor), 100);*/
//
//                new ParticleSystem(EcranAccueil.this, 8, R.drawable.notemusique, 20000)
//                        .setSpeedByComponentsRange(0f, -0.1f, 0f, 0.1f)
//                        .setAcceleration(0.00005f, 45)
//                        .emit(findViewById(R.id.emiter_top_left), 4);
//
//
//            }
//        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        playerAccueil.seekTo(media_length);
        playerAccueil.start();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        playerAccueil.pause();
        media_length=playerAccueil.getCurrentPosition();
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
        if (id == R.id.action_heart) {
            Toast.makeText(EcranAccueil.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(id==R.id.action_plus){

        }

        else if(id==R.id.action_settings) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                mDrawer.openDrawer(GravityCompat.START);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);

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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment_accueil, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            final Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
           TextView numeroNiveau=swipeView.findViewById(R.id.numeroNiveau);
            numeroNiveau.setText(String.valueOf(position+1));


            liste_titre=new ArrayList<>();
            liste_titre.add("London night - Twisterium");
            liste_titre.add("New begining - Twisterium");
            liste_titre.add("Night life - Twisterium");
            liste_titre.add("Progress - Twisterium");
            liste_titre.add("Lies on lies (update) (ft. Narva9) - Reiswerk");
            liste_titre.add("Better - Sleeperspaceborn");
            liste_titre.add("Tick Tock - Reiswerk");
            liste_titre.add("Between Worlds (Instrumental) (ft. (Smiling Cynic)) - Tobias Weber ");
            liste_titre.add("Rooted in soil - Tobias Weber");
            liste_titre.add("I want to see you again (ft. Unreal dm) - Veezyn");
            liste_titre.add("Platinium donation - Copperhead");
            liste_titre.add("Two Pianos (ft. Admiral Bob (admiralbob77)) - Stefan Kartenberg");
            liste_titre.add("Life is Beautiful - Twisterium");
            liste_titre.add("Supernatural(Instrumental) (ft. Snowflake) - Alex Beroza");
            liste_titre.add("Blue (ft. Offlinebouncer) - One Project");
            liste_titre.add("Pumping - Twisterium");
            liste_titre.add("The Quiet Hours (ft. SackJo22) - Siobhan Dakay");
            liste_titre.add("Floating Through Time (SAW mix) (ft. Jeris) - Stellarartwars");
            liste_titre.add("Knock on My Door (ft. Carrol, unreal_dm, ElRon XChile, Admiral Bob, Snake Davis) - Texasradiofish");
            liste_titre.add("Come Back (ft. brad sucks) - Unreal_dm");
            liste_titre.add("Paint The Sky (ft. MissJudged) - Jeris");
            liste_titre.add("TraLa (the Let's Never be Far mix) (ft. Jeris) - SackJo22");
            liste_titre.add("Miracles (ft. Patronski, Brad Stanfield) - Snowflake");
            liste_titre.add("Talk To Me (ft. Silke Schmiemann  Stefan Kartenberg) - Unreal_dm");
            liste_titre.add("Drops of H2O ( The Filtered Water Treatment ) (ft. Airtone) - J.Lang");
            liste_titre.add("Fever (ft. 7OOP3D) - Snowflake");
            liste_titre.add("Moonlight Sonata (Shifting Sun Mix) (ft. Snowflake) - Speck");
            liste_titre.add("What Cha Waitin 4 (simple club mix) (ft. J.Lang's latest find) - Duckett");
            liste_titre.add("Remember the name - Daguilar");
            liste_titre.add("End of the Game (ft. Maddy & Zep Hurme) - Scomber");

            final TextView titre=swipeView.findViewById(R.id.titre_musique);
            titre.setText(liste_titre.get(position));

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


}
