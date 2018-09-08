package com.developpement.ogawi.keeptherythm;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;

import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;
import com.github.lzyzsd.circleprogress.DonutProgress;

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

    DonutProgress donutProgress;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_accueil);
        final TextView messageVerrou=findViewById(R.id.messageVerrouille);
        final Animation myAnimDisparait = AnimationUtils.loadAnimation(this, R.anim.disappear);
        final Animation myAnimAparait = AnimationUtils.loadAnimation(this, R.anim.appear);


        donutProgress=findViewById(R.id.donut_progress);
        donutProgress.setProgress(Math.round((int)(obtenirAvancement()*100/NUM_ITEMS)));
        btnJouer=(Button) findViewById(R.id.btnJouer);
        i=new Intent(getApplicationContext(),InGame.class);
        i.putExtra("niveau",1);
        int maxVolume = 50;

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);


        sharedPreferences = getApplicationContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);

        sharedPreferences
                .edit()
                .putInt("niveau_max_jouable",5)
                .apply();
        if(sharedPreferences.contains("niveau_max_atteint")){
            viewPager.setCurrentItem(sharedPreferences.getInt("niveau_max_atteint",0)-1);
            if(sharedPreferences.getInt("niveau_max_atteint",0)>=5){

                sharedPreferences
                        .edit()
                        .putInt("niveau_max_jouable",sharedPreferences.getInt("niveau_max_atteint",0)+1)
                        .apply();
            }
        }


        isDrawerOpen=false;


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.

                i.putExtra("niveau",position+1);
                sharedPreferences = getApplicationContext().getSharedPreferences("prefs_joueur", MODE_PRIVATE);
                if(sharedPreferences.contains("niveau_max_jouable")){
                   if(position+1>sharedPreferences.getInt("niveau_max_jouable",0)) {
                       if (btnJouer.isClickable()) {
                           btnJouer.setClickable(false);
                           btnJouer.startAnimation(myAnimDisparait);
                           messageVerrou.setVisibility(View.VISIBLE);
                       }
                   }
                   else{
                       if(!btnJouer.isClickable()){
                           btnJouer.setClickable(true);
                           btnJouer.startAnimation(myAnimAparait);
                           messageVerrou.setVisibility(View.INVISIBLE);
                       }

                   }
                }


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

        String[] couples;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment_accueil, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            final Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
           TextView numeroNiveau=swipeView.findViewById(R.id.numeroNiveau);
            numeroNiveau.setText(String.valueOf(position+1));

            //Initialisation bdd
            final ScoreDAO scoreDAO=new ScoreDAO(getContext());

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


            sequences=new ArrayList<>();

            sequences.add("tap:6778;tap:8689;tap:10637;tap:12552;tap:14464;tap:16392;d:18073;g:19978;tap:22261;tap:26000;d:27658;g:29638;tap:31752;tap:33681;tap:36032;tap:37034;tap:37950;tap:39439;tap:40416;tap:43275;g:44029;d:44926;d:45931;g:46908;g:47875;d:49807;d:50737;d:51716;g:52680;g:53606;g:54595;tap:56264;tap:56746;tap:57213;tap:57696;tap:58662;tap:60546;tap:62522;d:63651;g:64217;d:65642;g:66082;tap:69186;tap:72072;tap:72476;tap:75898;tap:76275;tap:77836;tap:78241;g:81475;d:81907;g:82441;d:82939;tap:83641;tap:85550;tap:87423;d:89126;g:89674;g:90596;d:91051;d:91522;tap:93194;tap:95091;tap:98962;tap:100860;tap:102781;tap:103219;tap:104741;tap:105102;tap:106195;tap:106652;b:106652;h:106652;tap:113367;tap:113900;tap:114367;tap:116272;tap:116616;tap:116983;d:120849;d:121357;g:121829;g:123661;g:125129;g:125621;tap:127731;tap:128764;tap:129697;");
            sequences.add("tap:2985;tap:4251;tap:5537;tap:8099;tap:9060;tap:10581;d:11023;tap:12289;d:12811;tap:13775;d:14102;tap:15058;d:15368;tap:16258;d:16537;tap:17525;d:17949;tap:18825;tap:19258;g:19752;tap:20674;d:21054;tap:22556;g:22957;tap:23823;d:24178;tap:25157;tap:26340;tap:27638;d:28656;g:29331;d:29934;tap:30773;b:30773;b:30773;tap:33968;tap:35286;d:36259;d:37528;tap:38370;g:38726;d:39122;tap:39903;g:40073;tap:40919;d:41281;tap:42835;tap:44107;tap:45354;d:46320;g:47691;d:48851;tap:50342;tap:50684;d:50832;tap:51667;tap:52940;d:53910;g:55160;tap:56660;tap:57975;b:57975;h:57975;tap:59782;tap:60482;tap:60815;d:61006;tap:61765;tap:62967;tap:64257;g:65200;d:66443;tap:67998;tap:69296;g:70339;d:71550;tap:73052;tap:74388;tap:75654;tap:76935;d:77775;b:77775;h:77775;tap:80681;b:80681;tap:82561;tap:83260;tap:84478;d:85468;tap:86971;tap:88230;tap:89550;tap:90804;tap:92103;tap:93314;d:94296;g:95547;d:96848;tap:98379;tap:99615;g:100574;tap:102164;tap:103998;tap:109115;tap:110424;d:113263;g:114555;b:114555;h:114555;b:114555;d:119513;tap:120185;tap:120785;tap:121419;g:121565;tap:122384;");
            sequences.add("tap:4512;tap:6826;tap:8543;tap:9720;d:10689;g:11262;tap:12018;tap:14302;tap:16599;tap:18855;d:19769;g:20311;d:20575;tap:21132;d:21451;tap:22265;d:22665;tap:23465;g:23755;tap:24565;d:24994;tap:25765;g:26130;tap:26931;d:27261;tap:28031;g:28392;tap:29114;d:30112;tap:30898;g:31779;tap:32564;d:32858;tap:33697;g:34088;tap:34880;d:35175;tap:36531;g:36921;tap:37679;tap:38824;d:39155;tap:39979;g:40297;g:40932;tap:41712;d:42032;b:42032;b:42032;tap:43995;tap:44545;b:44545;tap:45762;tap:48590;h:48590;tap:49657;h:49657;tap:50876;h:50876;tap:52010;h:52010;d:52908;tap:53710;tap:54928;tap:55985;tap:57189;g:57522;tap:58292;tap:59493;tap:60569;tap:61795;tap:62837;g:63767;tap:64624;tap:66315;d:67118;tap:67990;g:68394;tap:69157;g:69527;tap:70256;d:71768;tap:72556;g:72959;tap:73705;d:74056;tap:74973;tap:77686;tap:79922;tap:82858;tap:84603;d:85495;g:86648;d:87719;tap:89176;tap:90342;b:90342;h:90342;b:90342;g:93543;tap:94018;d:94178;tap:94900;tap:96082;tap:97169;g:98105;tap:99405;tap:100613;d:101526;d:102092;g:102757;tap:103432;tap:105763;tap:107446;tap:108568;tap:109759;d:111182;g:111495;d:111735;g:112014;tap:112597;");
            sequences.add("tap:4080;tap:5651;tap:7234;tap:8554;tap:9450;tap:10367;d:10496;tap:11586;g:12227;tap:12949;d:13563;tap:14166;tap:15033;tap:15449;tap:15882;d:16075;d:17350;g:19120;d:20739;b:20739;h:20739;b:20739;h:20739;b:20739;h:20739;b:20739;tap:27861;tap:30476;tap:31279;d:31875;g:32376;tap:33011;d:33217;tap:33895;g:34089;tap:34728;tap:36421;tap:38129;d:38369;g:38843;d:39239;tap:39877;tap:40328;b:40328;h:40328;tap:43294;b:43294;h:43294;b:43294;d:49864;g:51642;tap:53545;b:53545;tap:58711;tap:60403;g:61013;d:61356;g:61720;tap:62223;d:64412;g:64775;d:65097;b:65097;d:67890;g:68244;d:68559;b:68559;d:70473;tap:70854;g:71320;d:71647;g:71994;b:71994;tap:75900;d:78117;g:78548;d:78836;b:78836;tap:82830;tap:84439;tap:86136;tap:89567;b:89567;h:89567;g:96169;d:99631;b:99631;h:99631;tap:106733;tap:107545;tap:108408;tap:109326;tap:110161;");
            sequences.add("tap:3227;tap:3609;tap:4042;tap:6614;tap:7041;tap:9616;tap:10007;tap:12605;tap:13023;tap:15664;tap:16057;d:17654;g:18008;tap:19365;tap:21576;tap:22021;tap:23899;tap:24238;tap:24621;d:26516;g:26909;d:27389;d:30358;g:30800;d:31148;b:31148;b:31148;b:31148;b:31148;d:38661;g:39078;d:39358;tap:40385;tap:41151;tap:41884;tap:42251;tap:42617;tap:45309;tap:45700;tap:48593;tap:50178;b:50178;h:50178;d:54331;g:56661;d:57441;g:60408;b:60408;h:60408;d:65256;g:65648;d:65962;tap:66579;d:69006;g:69353;tap:69961;d:71653;g:72053;d:72341;tap:72960;b:72960;b:72960;b:72960;h:72960;d:81379;tap:82445;tap:83141;tap:84648;tap:86126;g:86648;d:87379;b:87379;b:87379;b:87379;h:87379;g:93372;g:94099;tap:96221;tap:96638;tap:99618;tap:100421;tap:101137;tap:102605;tap:103403;tap:104119;tap:108619;tap:109402;tap:110169;tap:111643;tap:112435;tap:113185;tap:115407;tap:116134;tap:116884;tap:119883;tap:120666;d:122635;g:123052;d:123349;g:125210;d:125635;g:125991;d:126356;d:129373;g:130099;d:130835;b:130835;h:130835;b:130835;h:130835;b:130835;h:130835;d:138360;g:139137;b:139137;h:139137;b:139137;b:139137;tap:148312;tap:149077;tap:149876;tap:150611;tap:151377;tap:153609;tap:155886;tap:156691;tap:157424;tap:158124;");
            sequences.add("d:6091;tap:8469;g:10000;tap:11568;d:13297;tap:15427;g:16755;tap:18826;b:18826;tap:22272;h:22272;tap:25670;h:25670;tap:29042;b:29042;tap:32622;d:33883;tap:35419;d:35726;tap:37378;g:37522;tap:39466;d:40813;tap:42795;g:44276;tap:46280;b:46280;h:46280;b:46280;h:46280;d:49944;tap:51269;g:52888;tap:54649;d:56452;tap:58183;g:59724;tap:61572;g:62679;b:62679;tap:66978;tap:68759;d:70038;tap:71799;tap:73443;g:75121;tap:77138;d:78574;b:78574;h:78574;tap:83993;d:85308;tap:87418;d:88748;tap:90857;b:90857;tap:93972;h:93972;b:93972;tap:95403;d:95710;tap:96520;h:96520;tap:98897;d:99161;tap:101178;g:102497;tap:105998;tap:108056;g:109403;tap:111419;d:112760;tap:114471;tap:114899;tap:116128;tap:117754;h:117754;tap:119588;tap:120165;tap:121355;h:121355;tap:122756;g:123040;tap:125400;tap:138090;");
            sequences.add("tap:3856;tap:4286;tap:6152;tap:7086;tap:8002;tap:8452;tap:8952;tap:9369;tap:9819;tap:11700;tap:12152;tap:12601;tap:13085;tap:13518;tap:15413;tap:15867;tap:16284;tap:16784;tap:17201;tap:19076;tap:20889;tap:22729;tap:23666;tap:24166;tap:24582;d:25301;g:25759;d:26176;tap:28286;tap:28748;tap:29215;g:29485;d:29857;tap:31447;tap:31980;tap:32480;tap:32963;d:33112;g:33570;b:33570;h:33570;tap:36130;d:37318;tap:39285;g:40045;tap:41668;tap:42145;tap:42612;d:42768;b:42768;tap:45748;b:45748;tap:47178;h:47178;b:47178;tap:48594;g:50227;d:51090;tap:52300;g:53411;tap:55045;d:55704;tap:57772;tap:58258;tap:58742;tap:59275;tap:59725;tap:60175;tap:60675;tap:61147;tap:61725;tap:62341;d:62655;tap:63324;g:64958;d:66792;tap:67472;g:67719;d:68149;tap:68839;tap:69306;h:69306;b:69306;h:69306;b:69306;d:72270;d:72765;d:73273;b:73273;h:73273;tap:75803;tap:76304;tap:76771;tap:77238;tap:77655;tap:78088;tap:79038;d:80642;g:81139;d:81545;b:81545;h:81545;b:81545;tap:85472;tap:85936;tap:86403;tap:88208;h:88208;b:88208;d:89343;g:89883;d:90284;tap:92420;d:92573;g:94031;d:94481;tap:95638;tap:96117;tap:96550;d:97249;g:97765;d:98126;b:98126;h:98126;b:98126;h:98126;b:98126;h:98126;b:98126;d:104146;d:105088;d:105504;g:106972;d:107347;b:107347;h:107347;tap:110825;d:110975;g:112475;d:112809;tap:115008;d:115190;b:115190;tap:116792;h:116792;b:116792;tap:120087;tap:120562;b:120562;g:123084;g:124027;d:124927;tap:126099;d:126792;tap:127945;g:128665;tap:129788;d:130466;tap:132497;g:133240;tap:134860;d:135071;tap:136717;g:136915;tap:138501;d:138723;tap:140744;tap:141741;tap:142773;b:142773;h:142773;b:142773;h:142773;tap:149611;tap:150538;tap:151539;tap:152773;tap:153771;g:154897;g:155856;g:156769;tap:158440;");
            sequences.add("tap:1830;tap:4644;tap:7067;tap:8824;tap:10356;tap:11628;tap:13502;tap:14764;tap:16382;tap:17873;tap:19495;tap:21113;tap:22426;tap:23537;d:25192;g:28122;g:31151;d:34070;b:34070;b:34070;h:34070;h:34070;tap:49454;tap:50842;tap:51654;tap:52320;tap:55382;tap:58419;d:61082;g:62558;d:63355;g:64133;d:65537;g:67169;d:68724;tap:70371;tap:73466;g:74753;tap:76399;g:78192;d:78535;tap:79398;b:79398;h:79398;tap:84692;g:85119;tap:86830;b:86830;h:86830;b:86830;tap:89887;d:91133;g:94114;g:97063;tap:98873;d:100152;tap:101949;b:101949;tap:104896;tap:106409;h:106409;b:106409;h:106409;b:106409;tap:115401;tap:118317;d:121165;tap:122867;g:124094;tap:125882;d:127069;tap:128914;g:130070;tap:133362;d:134542;b:134542;d:136211;h:136211;g:139149;tap:142205;tap:145444;");
            sequences.add("tap:6548;tap:7186;tap:8521;tap:9055;tap:9472;tap:10288;tap:10638;tap:11271;tap:12647;tap:13927;tap:14838;tap:15238;tap:15605;tap:15970;tap:16654;tap:17370;tap:17870;tap:18353;tap:18853;tap:19237;tap:20220;tap:21311;tap:22662;tap:23922;tap:24568;tap:25218;tap:26613;tap:27959;tap:28551;tap:29202;tap:29834;d:30737;g:31648;tap:32601;d:33833;tap:35245;g:36395;d:37689;b:37689;tap:40502;d:41712;h:41712;tap:43315;g:45620;tap:47156;d:47544;tap:48638;b:48638;h:48638;b:48638;h:48638;g:53335;g:54110;g:54652;g:55286;tap:55912;tap:56611;tap:58575;h:58575;tap:59595;b:59595;tap:61244;h:61244;tap:63648;tap:66266;d:66837;tap:69587;b:69587;tap:74377;tap:74924;tap:75641;tap:76458;d:76747;tap:77774;tap:78174;tap:79874;tap:82533;b:82533;tap:83573;d:84284;tap:85223;h:85223;tap:87980;g:88251;tap:88922;h:88922;tap:90522;d:90815;b:90815;h:90815;tap:94052;tap:95995;tap:97139;tap:98019;g:98300;d:98851;g:99410;b:99410;h:99410;b:99410;tap:103212;d:104226;g:105123;d:106267;d:107683;tap:108467;tap:109383;g:110833;d:111121;g:111618;d:112096;g:112584;b:112584;h:112584;b:112584;h:112584;g:114289;d:116313;g:117783;d:118171;b:118171;h:118171;b:118171;d:119616;g:120156;d:120511;b:120511;h:120511;d:122394;g:123461;g:124258;g:125022;g:127072;d:127392;g:128189;g:128987;g:129607;b:129607;b:129607;h:129607;b:129607;tap:132596;g:133064;d:133464;tap:134162;d:135383;tap:135929;g:136355;g:137774;d:138287;tap:139407;tap:139993;tap:140577;d:140928;tap:141910;d:142220;tap:143280;d:143664;tap:144526;g:144928;tap:145943;b:145943;tap:147110;h:147110;tap:148563;d:148796;g:149567;d:150188;b:150188;tap:151841;g:152135;tap:152844;d:152980;tap:154531;g:154705;tap:155445;d:156179;tap:157190;h:157190;g:157932;tap:159835;");
            sequences.add("tap:2206;tap:2737;tap:3304;tap:3803;tap:4320;tap:4803;tap:5336;tap:5820;tap:6888;tap:7337;tap:7836;tap:8786;tap:9336;tap:9869;tap:10802;tap:11286;tap:11819;tap:12585;tap:13336;tap:14103;tap:14819;tap:15819;tap:17289;tap:17801;d:18087;tap:18801;g:19060;g:20819;d:21099;tap:21800;d:21997;tap:22783;d:23022;tap:23783;g:24558;tap:25283;g:25544;tap:26299;b:26299;tap:27832;tap:28815;d:29511;b:29511;b:29511;tap:33276;h:33276;tap:34231;tap:35295;tap:36574;tap:37297;b:37297;tap:38197;g:38546;tap:39246;d:39508;tap:40263;h:40263;b:40263;tap:41763;tap:42762;d:43020;tap:43778;b:43778;tap:44778;d:45016;tap:45762;tap:47259;d:47474;tap:48261;h:48261;tap:49277;tap:51277;g:51507;tap:52776;d:53054;tap:53811;g:54499;tap:55260;b:55260;tap:56813;d:57047;tap:57777;b:57777;tap:58759;d:58985;tap:59742;b:59742;tap:60792;d:61017;tap:61742;b:61742;tap:62841;d:63474;tap:64458;b:64458;tap:65325;d:65619;tap:69332;d:69544;tap:70356;d:70531;tap:71289;g:71478;tap:72288;h:72288;tap:73800;b:73800;tap:74755;d:74984;b:74984;tap:76255;d:76462;tap:77289;b:77289;tap:78172;g:78444;tap:79238;h:79238;tap:80288;d:80600;tap:81287;b:81287;tap:82320;d:82519;tap:83271;h:83271;tap:84732;d:84956;tap:85805;b:85805;tap:86804;g:87010;tap:88244;b:88244;tap:89302;d:89507;tap:90255;g:90506;tap:91269;h:91269;tap:92241;d:92469;tap:93764;tap:94784;tap:95735;tap:96766;tap:99245;tap:100234;tap:101234;tap:102233;tap:103199;tap:104215;tap:104750;tap:105249;g:105528;d:106075;g:106992;d:107997;g:109006;d:109904;g:110873;d:111998;g:112950;d:114002;g:114940;d:115968;g:116890;d:117947;g:118815;d:119918;b:119918;b:119918;b:119918;b:119918;b:119918;b:119918;b:119918;h:119918;b:119918;h:119918;b:119918;h:119918;d:142490;g:144210;d:144966;g:146465;d:147510;g:148614;d:149470;b:149470;h:149470;b:149470;tap:155716;d:155961;tap:156771;d:156996;tap:157754;d:158021;tap:158755;d:159009;tap:159703;d:160034;g:160546;b:160546;h:160546;tap:162737;b:162737;h:162737;tap:164759;d:165432;tap:166729;g:167408;tap:168253;b:168253;tap:169286;");
            sequences.add("tap:2444;tap:2805;tap:3421;tap:3804;tap:4137;tap:4454;tap:5071;tap:5487;tap:5954;tap:6354;tap:6753;tap:7187;tap:7554;tap:7958;tap:8371;tap:8770;tap:9187;tap:9603;tap:9986;tap:10403;tap:10820;tap:11603;tap:12019;tap:12403;tap:13640;tap:14018;tap:15233;tap:15652;tap:16892;tap:17318;tap:18512;tap:18917;tap:20161;tap:20567;tap:21775;tap:22183;tap:23407;tap:23817;tap:25047;tap:25449;tap:26669;tap:27918;tap:28732;d:29308;tap:30352;g:30914;tap:31615;b:31615;tap:33198;d:34149;tap:34864;h:34864;tap:36481;g:37451;tap:39712;d:39932;tap:41002;b:41002;tap:42986;g:43105;tap:44591;h:44591;tap:46237;d:46405;tap:47857;g:48035;tap:49502;d:49663;tap:51125;b:51125;tap:52741;h:52741;tap:56068;tap:56892;tap:57692;tap:58575;tap:59341;tap:60177;tap:60572;tap:61022;tap:62224;tap:62657;tap:63041;tap:64242;tap:64657;tap:65855;tap:66723;d:67294;tap:68273;g:68879;tap:69960;tap:70389;tap:70773;h:70773;tap:72417;h:72417;tap:73993;d:74457;tap:75616;b:75616;tap:77231;h:77231;tap:78897;tap:79804;tap:81785;b:81785;g:83580;d:85151;tap:86278;g:86860;tap:88750;tap:90351;tap:91019;tap:91985;tap:92635;tap:93601;tap:95235;tap:95834;tap:96284;tap:97566;d:98396;g:99173;d:99542;b:99542;b:99542;b:99542;tap:101766;tap:105047;tap:105865;d:106491;g:106830;d:107304;tap:107915;d:108069;tap:108681;b:108681;b:108681;d:110472;d:110922;d:111316;tap:111981;g:112332;d:112989;g:113741;d:114621;b:114621;b:114621;h:114621;d:116575;g:117032;tap:117663;h:117663;d:118248;tap:118896;g:119091;tap:119712;h:119712;d:120360;tap:120978;b:120978;tap:121811;g:121993;tap:122611;tap:124619;h:124619;d:125233;g:125691;b:125691;tap:126660;h:126660;h:126660;g:127695;d:128093;tap:128761;b:128761;g:130176;tap:131161;d:131329;tap:132750;g:133294;tap:134414;b:134414;tap:135995;d:136468;tap:137683;h:137683;tap:139313;g:139894;tap:140907;b:140907;tap:142594;d:142929;tap:144231;b:144231;tap:145903;b:145903;tap:147459;h:147459;tap:149164;d:149651;tap:150752;d:151267;tap:152384;b:152384;tap:154005;tap:155620;tap:157289;tap:158870;tap:160586;tap:161453;tap:162236;tap:163787;d:164415;tap:165456;g:166029;tap:167072;d:167764;tap:168735;g:169331;tap:170352;d:170899;tap:171970;b:171970;b:171970;b:171970;b:171970;tap:176121;tap:176917;tap:177633;tap:180149;tap:180999;tap:181816;tap:183385;h:183385;tap:185013;g:185496;tap:186634;d:187242;tap:188326;d:188869;tap:189917;g:190504;tap:191551;d:192131;tap:193180;g:193767;tap:194799;d:195273;tap:196456;tap:198091;tap:199728;tap:201413;");
            sequences.add("tap:1222;tap:2633;tap:3601;tap:4584;tap:5384;tap:5817;tap:6234;d:7914;g:8380;d:8806;g:9681;d:10593;b:10593;h:10593;b:10593;b:10593;g:16010;b:16010;tap:19019;d:19643;tap:20768;d:21475;tap:22600;tap:23497;b:23497;b:23497;tap:26261;g:26973;tap:28063;tap:29029;h:29029;d:29659;g:30588;b:30588;tap:32599;h:32599;b:32599;h:32599;b:32599;d:35976;g:36479;b:36479;tap:38985;tap:39994;h:39994;b:39994;tap:41727;d:42332;tap:43576;g:44198;tap:45291;tap:47089;d:47399;b:47399;tap:49024;tap:50840;g:51455;h:51455;b:51455;d:53267;tap:54425;b:54425;g:56039;b:56039;h:56039;tap:58064;d:58701;g:59638;b:59638;tap:61713;h:61713;b:61713;tap:63450;tap:65355;h:65355;b:65355;tap:67174;d:67848;tap:68983;tap:69454;d:69677;g:70553;b:70553;h:70553;b:70553;tap:73471;d:74163;g:74701;d:75185;tap:76306;b:76306;h:76306;b:76306;h:76306;g:78760;d:79627;tap:80845;g:81395;d:81887;b:81887;tap:83543;tap:84451;g:84896;d:85160;b:85160;h:85160;tap:88140;tap:89050;tap:89467;tap:89933;tap:90883;tap:91799;d:92370;g:92844;g:93770;g:94694;tap:95870;g:96486;tap:97148;g:97828;tap:98935;b:98935;b:98935;h:98935;tap:102602;b:102602;tap:104469;h:104469;b:104469;h:104469;tap:107109;tap:109948;g:110484;d:111500;tap:112683;g:113372;tap:117239;tap:118110;d:119705;g:120595;d:121119;g:121921;g:123293;b:123293;h:123293;b:123293;h:123293;g:126022;d:126986;tap:128117;d:128753;b:128753;h:128753;tap:130784;b:130784;tap:132679;h:132679;tap:134432;b:134432;h:134432;tap:136213;tap:138091;tap:139006;g:139643;tap:141721;d:142349;tap:143502;tap:144938;g:145968;b:145968;tap:148032;h:148032;tap:149946;b:149946;h:149946;b:149946;tap:152621;d:153293;g:154205;d:154654;g:155171;d:155563;b:155563;tap:157207;d:157854;g:158326;d:158753;b:158753;tap:160836;tap:162463;tap:164532;tap:166264;tap:168041;b:168041;h:168041;tap:173514;tap:174782;tap:177114;tap:179013;tap:180344;g:180641;g:181534;d:182485;tap:183551;g:183773;d:184237;g:186023;d:186656;g:186969;b:186969;tap:188078;h:188078;tap:189965;b:189965;h:189965;b:189965;h:189965;g:191568;d:192473;b:192473;h:192473;b:192473;tap:194910;d:195178;tap:196415;b:196415;");

            if(position<sequences.size()) {
                sequenceARealiser = traiterSequences( sequences.get(position));
                couples = sequenceARealiser.split(";");
                int nbTotalMvts = couples.length;
                TextView nbTotal = swipeView.findViewById(R.id.nbTotal);
                nbTotal.setText(" / " + String.valueOf(nbTotalMvts));

                TextView scoreOr=swipeView.findViewById(R.id.scoreOr);
                TextView scoreAr=swipeView.findViewById(R.id.scoreArgent);
                TextView scoreBr=swipeView.findViewById(R.id.scoreBronze);

                int sOr=Math.round((int) (90*nbTotalMvts/100));
                int sAr=Math.round((int) (80*nbTotalMvts/100));
                int sBr=Math.round ((int) (70*nbTotalMvts/100));
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

        public String traiterSequences(String entree){

            String[] couples=entree.split(";");
            ArrayList<String> couplesTraites = new ArrayList<>();
            int i=0;
            while (i<couples.length-1){

                if(couples[i+1].split(":")[1].equals(couples[i].split(":")[1])){
                    couplesTraites.add(couples[i]);
                    i++;
                    String tempsActuel=couples[i].split(":")[1];

                    while (couples[i].split(":")[1].equals(tempsActuel)) {
                        if(i<couples.length-1) {
                            i++;
                        }
                    }

                }
                else {
                    couplesTraites.add(couples[i]);
                    i++;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String s : couplesTraites)
            {
                sb.append(s);
                sb.append(";");
            }


            return sb.toString();
        }
    }


}
