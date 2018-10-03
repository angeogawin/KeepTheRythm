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
import android.widget.Toast;

import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;
import com.github.lzyzsd.circleprogress.DonutProgress;
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

    DonutProgress donutProgress;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_accueil);
        AppRater.app_launched(this);

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
        if(!sharedPreferences.contains("niveau_max_jouable")){
            sharedPreferences
                    .edit()
                    .putInt("niveau_max_jouable",3)
                    .apply();
        }

        if(sharedPreferences.contains("niveau_max_atteint")){
            viewPager.setCurrentItem(sharedPreferences.getInt("niveau_max_atteint",0)-1);
            Toast.makeText(EcranAccueil.this, String.valueOf(aJoueTousLesNiveauxDeverouille(sharedPreferences.getInt("niveau_max_atteint",0))), Toast.LENGTH_SHORT).show();
            if(sharedPreferences.getInt("niveau_max_atteint",0)>=sharedPreferences.getInt("niveau_max_jouable",0) && aJoueTousLesNiveauxDeverouille(sharedPreferences.getInt("niveau_max_atteint",0))){


                sharedPreferences
                        .edit()
                        .putInt("niveau_max_jouable",sharedPreferences.getInt("niveau_max_atteint",0)+2)
                        .apply();
            }

            i.putExtra("niveau",sharedPreferences.getInt("niveau_max_atteint",0));
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
                if(sharedPreferences.contains("niveau_max_jouable")) {
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


        listeMusiqueAccueil=new ArrayList<>();
        // **** ajout des musiques
        listeMusiqueAccueil.add(R.raw.vj_memes_paint_the_sky);
        listeMusiqueAccueil.add(R.raw.tobias_weber_rescue_me_instrumental);
        listeMusiqueAccueil.add(R.raw.tobias_weber_the_parting_glass_instrumental);
        Random alea = new Random();
        int num_musique = alea.nextInt(listeMusiqueAccueil.size()) ;

        playerAccueil= MediaPlayer.create(this, listeMusiqueAccueil.get(num_musique));
       // float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        playerAccueil.setVolume(0.2f, 0.2f);
        playerAccueil.setLooping(true);
        playerAccueil.start();

        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerAccueil.stop();
                startActivity(i);
                finish();

            }
        });





        animationDrawable = (AnimationDrawable) mDrawer.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(5000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);

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
            liste_titre.add("Music Through Love - Kara Square");
            liste_titre.add("Like Music (cdk Mix) - Analog By Nature");
            liste_titre.add("Good Day - Alex Beroza");
            liste_titre.add("A Brighter Heart - Audionautix");
            liste_titre.add("Alla What Parody - Audionautix");
            liste_titre.add("We wish you a merry Xmas - Audionautix");
            liste_titre.add("Carol of the bells - Audionautix");
            liste_titre.add("Between worlds instrumental - Tobias Weber");
            liste_titre.add("Pool - Audionautix");
            liste_titre.add("Rocker - Audionautix");
            liste_titre.add("There you go - Audionautix");
            liste_titre.add("Two Pianos (ft. Admiral Bob (admiralbob77)) - Stefan Kartenberg");
            liste_titre.add("Adagio -Audionautix");
            liste_titre.add("The Voyage - Audionautix");
            liste_titre.add("Hip Hop - Audionautix");
            liste_titre.add("Persephone - Hans Atom");
            liste_titre.add("Joy to the World - Audionautix");
            liste_titre.add("Epic Series - Audionautix");
            liste_titre.add("Bird in Hand - Audionautix");
            liste_titre.add("Ectoplasm - Audionautix");
            liste_titre.add("Paint The Sky (ft. MissJudged) - Jeris");
            liste_titre.add("Alison - Audionautix");
            liste_titre.add("Pile driver - Audionautix");
            liste_titre.add("Go not Gently - Audionautix");
            liste_titre.add("Drops of H2O ( The Filtered Water Treatment ) (ft. Airtone) - J.Lang");
            liste_titre.add("Leavin the Lights - Audionautix");
            liste_titre.add("Moonlight Sonata (Shifting Sun Mix) (ft. Snowflake) - Speck");
            liste_titre.add("Triangle - Audionautix");
            liste_titre.add("Big car Theft - Audionautix");
            liste_titre.add("What da Funk - Audionautix");





            final TextView titre=swipeView.findViewById(R.id.titre_musique);
            titre.setText(liste_titre.get(position));


            sequences=new ArrayList<>();

            sequences.add("tap:4809;tap:5670;tap:7364;tap:8682;tap:10784;tap:13323;tap:13764;tap:14213;tap:15471;tap:16159;tap:17639;tap:19390;tap:21025;tap:22784;tap:24529;tap:25295;tap:26252;tap:27081;tap:27564;tap:28004;tap:29723;tap:30575;tap:31330;tap:33047;tap:34379;tap:34842;tap:36079;tap:36923;tap:37388;g:37979;d:38916;tap:39952;g:41496;d:42334;g:44037;d:44894;tap:45818;tap:46748;tap:47227;g:47445;d:47887;tap:48514;d:49625;tap:50250;tap:50661;g:50805;d:51287;tap:51914;g:52511;d:52968;tap:53595;tap:54061;g:54240;d:54747;tap:55375;tap:56205;tap:57079;tap:57870;tap:58823;g:59221;d:60239;g:61146;d:62018;tap:63063;tap:63903;tap:64814;tap:65620;tap:66524;tap:67384;g:67947;d:68805;g:69686;d:70663;tap:71688;tap:72451;tap:73357;tap:74238;g:74863;d:75742;tap:76780;tap:77675;tap:78565;g:79112;d:79972;g:80876;tap:81886;tap:82774;tap:84484;tap:86268;d:86844;g:87675;tap:88832;tap:89322;tap:89712;tap:90631;tap:91447;tap:92763;tap:93177;tap:94463;tap:94899;tap:96531;tap:97902;tap:98315;tap:99937;tap:101282;tap:101691;d:102393;g:102745;tap:103371;g:104013;d:104455;g:105717;d:106483;tap:107666;tap:109277;tap:111093;tap:112778;d:114214;g:115041;tap:116228;tap:117057;tap:117996;tap:119131;tap:120381;tap:121275;tap:122139;tap:122995;tap:123882;tap:124747;d:125314;g:126271;d:126671;g:127081;tap:128175;tap:129766;tap:130770;tap:131658;tap:133363;d:133951;g:134924;d:135797;tap:136752;tap:137165;tap:137608;g:138274;d:139079;tap:140262;tap:140720;tap:141075;tap:141866;tap:143584;tap:143992;d:144178;g:145153;d:146089;d:146903;d:147700;d:148541;g:149379;d:149822;g:150239;d:150632;tap:151324;d:151993;g:152907;g:153285;d:153670;g:154130;d:154522;g:155417;g:156827;d:157170;g:160201;d:160596;g:161078;d:162049;");
            sequences.add("tap:1329;tap:2182;tap:3077;tap:3949;tap:4735;d:5497;g:6237;tap:7305;tap:8177;tap:9031;d:9693;g:10487;tap:11556;tap:12460;tap:13311;d:13968;tap:14627;d:14794;tap:15419;d:15674;tap:16276;g:16550;tap:17269;g:17453;tap:18082;g:18321;tap:18946;tap:19751;g:19979;tap:20669;d:20830;tap:21456;d:21719;tap:22323;tap:23146;d:23394;tap:24055;g:24275;tap:24912;g:25193;tap:25768;tap:26600;d:27293;tap:27931;tap:28748;tap:29588;tap:30138;tap:30463;tap:31282;d:31555;g:31981;d:32431;g:32850;d:33284;g:33718;d:34116;g:34542;d:34960;g:35428;d:35817;g:36218;tap:36872;d:37086;g:37544;d:37986;g:38766;d:39235;g:39660;d:40570;g:41399;tap:42073;tap:42970;tap:44168;tap:45856;tap:46818;tap:47595;tap:48443;tap:49309;tap:50154;tap:51046;tap:51892;tap:52768;tap:53629;tap:54499;d:55120;g:55949;d:56362;g:56846;tap:57899;d:58518;g:58955;d:59377;tap:60421;tap:61330;d:61942;d:62844;tap:63897;g:64559;tap:65641;tap:66525;g:67153;tap:67757;d:67973;tap:68608;g:68828;tap:69913;d:70579;tap:71591;tap:72000;d:72298;g:73123;d:74029;tap:75029;tap:75915;tap:76782;tap:77673;tap:78475;tap:78927;tap:80202;tap:81899;g:82550;d:83387;g:84629;d:85052;g:85526;tap:86217;tap:87064;tap:88371;tap:88837;tap:89632;d:90274;tap:91716;tap:92165;g:92772;tap:93931;d:94511;tap:95620;tap:96076;g:96271;tap:96917;d:97132;tap:97793;tap:98226;d:98451;g:98841;d:99282;tap:99908;tap:100324;g:100587;d:101027;tap:101653;tap:102118;g:102288;d:102701;tap:103327;tap:103758;tap:104203;tap:105466;tap:105931;tap:107076;tap:107618;tap:108919;tap:109319;d:109606;g:109993;tap:111056;tap:112374;g:112558;tap:113221;tap:113645;d:114306;tap:114908;tap:115815;d:116022;g:116406;tap:117070;tap:117923;tap:118748;tap:119172;d:119400;g:119838;tap:120464;tap:121164;tap:121803;d:122411;g:122870;d:123270;tap:123914;tap:124764;tap:125994;tap:126477;d:126746;g:127164;d:127580;d:128448;d:129302;d:130164;tap:130738;g:131350;g:132271;g:133177;g:133947;tap:135037;tap:135909;tap:136772;tap:137208;tap:137639;tap:138520;tap:139192;tap:139717;tap:140190;tap:141085;d:141674;g:142604;d:143316;tap:144006;tap:144485;tap:145188;tap:146616;tap:147064;d:147318;g:147731;tap:148767;tap:149622;d:150179;g:151053;tap:151819;tap:152160;tap:153012;tap:153521;tap:153890;d:154130;g:154550;tap:155644;tap:156505;tap:157368;g:157565;d:157972;tap:158617;tap:159046;g:159338;d:160168;tap:160750;tap:161185;g:161418;tap:162515;tap:163364;tap:164196;tap:165049;d:165663;g:166568;d:167383;g:167796;tap:168460;tap:168876;d:169105;tap:169765;tap:170197;g:170485;tap:171113;tap:171499;g:171710;d:172125;tap:172750;tap:173198;g:173479;d:173884;tap:174487;tap:174917;g:175116;d:175574;tap:176234;tap:176633;g:176932;d:177316;d:178147;");
            sequences.add("tap:1081;tap:2634;tap:3771;tap:5450;tap:7072;tap:8687;tap:10333;tap:11998;tap:13606;tap:14198;tap:14868;tap:15303;tap:15681;d:15855;g:16346;tap:16949;tap:17366;d:17577;tap:18204;tap:18593;tap:19002;g:19176;d:19609;tap:20253;tap:20630;g:20812;d:21241;tap:21905;tap:22255;g:22492;d:22929;tap:23543;tap:23943;g:24105;d:24537;tap:25167;tap:25575;tap:26016;tap:26413;tap:26843;tap:27202;g:27384;d:27806;tap:28451;tap:28823;g:29047;d:29451;tap:30140;tap:30533;g:30718;d:31106;tap:31771;tap:32175;tap:32565;tap:32992;g:33177;d:33581;tap:34211;tap:34645;tap:35495;tap:35880;tap:37062;tap:37459;g:38469;d:38873;g:39340;tap:39944;tap:40352;d:40484;g:40949;tap:41552;tap:41976;d:42231;g:42617;tap:43242;tap:43643;d:43807;d:44708;g:45113;tap:45716;tap:46079;d:46682;g:47128;tap:47759;tap:48576;tap:48954;d:49178;g:49666;tap:50642;tap:51446;tap:51851;d:52004;g:52445;tap:53076;tap:53484;g:53716;d:54175;tap:54751;tap:55148;g:55347;tap:55949;tap:56375;d:56575;g:56992;tap:57619;tap:58062;b:58062;tap:58895;tap:59285;h:59285;tap:60062;tap:60454;b:60454;tap:61346;tap:62552;tap:62943;b:62943;tap:63820;tap:64642;b:64642;tap:66238;h:66238;d:67737;b:67737;g:69258;h:69258;d:70835;b:70835;g:72510;h:72510;d:74131;b:74131;g:75741;h:75741;d:77344;b:77344;g:78971;h:78971;tap:80214;tap:81836;tap:82710;h:82710;tap:84303;tap:84743;d:84933;tap:85970;tap:86365;b:86365;tap:87228;tap:87636;g:87813;tap:88557;tap:88921;h:88921;tap:90036;tap:90472;tap:91662;tap:92119;d:93145;g:94116;d:94813;d:95279;g:96398;g:97377;d:98054;g:98960;b:98960;h:98960;b:98960;tap:102000;tap:102445;g:102977;d:103828;h:103828;b:103828;tap:105720;tap:107409;tap:108983;h:108983;b:108983;h:108983;tap:111839;tap:112269;g:112808;d:113762;g:114513;h:114513;tap:116325;tap:117139;tap:118836;d:119437;g:120350;tap:121271;tap:121703;b:121703;tap:122945;tap:123817;tap:124642;h:124642;b:124642;tap:125861;tap:126227;h:126227;tap:127140;tap:127909;b:127909;tap:129517;tap:130375;tap:131172;tap:132061;h:132061;b:132061;tap:133679;tap:134128;tap:135281;tap:135879;tap:136920;tap:137374;g:137518;d:138041;tap:138616;d:139216;g:140750;b:140750;h:140750;tap:143492;tap:144283;tap:145108;tap:145959;tap:146841;tap:147613;tap:148474;tap:149234;tap:150086;tap:150935;tap:151692;d:152307;g:153233;b:153233;h:153233;g:155653;tap:156665;tap:157460;tap:157856;tap:158280;b:158280;tap:160033;h:160033;tap:161639;b:161639;tap:163315;h:163315;tap:164853;b:164853;tap:166524;h:166524;d:167934;g:168355;tap:168961;tap:169899;tap:170633;tap:171500;tap:172279;tap:173539;tap:173912;tap:174728;tap:175141;tap:175573;tap:176751;tap:177207;tap:177673;tap:178045;tap:178825;tap:179696;tap:180506;tap:180958;tap:181347;tap:182167;tap:182995;tap:183393;tap:183748;tap:184590;tap:186241;tap:187113;tap:187925;d:188515;g:189311;d:190223;g:190913;d:191760;g:192561;d:193347;g:194295;d:195034;g:195923;");
            sequences.add("tap:1808;tap:2608;tap:3054;tap:5194;tap:6049;tap:6410;d:7050;tap:7539;tap:7900;tap:8218;tap:10009;tap:10333;g:10988;tap:11515;tap:11857;g:13145;d:13441;tap:14223;g:14979;d:15326;g:17449;d:17754;tap:18366;tap:18909;tap:19678;tap:20205;tap:20557;d:20707;g:21269;d:21575;tap:22083;tap:23324;d:24046;g:24369;tap:25384;tap:25745;d:25887;g:26183;d:26446;tap:27021;g:27708;tap:28202;tap:28836;tap:29356;d:29476;g:29772;d:30103;g:30447;tap:30944;g:31670;d:31909;g:32230;d:32526;b:32526;h:32526;tap:33964;tap:34275;tap:34855;d:35203;g:35521;d:35868;g:36136;d:36433;tap:36949;tap:37307;g:37754;d:38018;g:39503;d:39816;tap:40603;tap:40963;g:41291;d:41612;tap:42453;tap:42771;g:43076;d:43432;tap:44222;tap:44579;tap:45461;tap:45772;b:45772;tap:47306;tap:48274;tap:48839;tap:49146;b:49146;h:49146;d:51109;g:51347;tap:53301;h:53301;tap:54618;b:54618;tap:56417;tap:58131;tap:58519;d:58877;g:60741;tap:63651;b:63651;h:63651;h:63651;b:63651;h:63651;b:63651;h:63651;tap:66703;d:67555;g:68536;tap:70636;tap:71772;tap:72116;tap:72437;g:72728;d:73114;b:73114;h:73114;d:75270;g:75838;d:76439;b:76439;h:76439;b:76439;h:76439;b:76439;h:76439;d:81489;g:81874;d:82209;g:82767;d:83086;g:83683;d:84317;g:84893;d:85122;tap:86285;tap:86620;tap:87027;b:87027;h:87027;b:87027;tap:88883;h:88883;b:88883;tap:90356;tap:90950;h:90950;b:90950;tap:92692;h:92692;b:92692;h:92692;tap:95178;g:95811;d:97008;g:98223;d:98561;g:98857;b:98857;h:98857;b:98857;h:98857;b:98857;d:101013;g:101687;d:101940;g:102230;b:102230;h:102230;b:102230;h:102230;b:102230;g:105481;d:105807;g:106154;tap:107223;g:107466;d:107713;g:107984;tap:108821;d:108964;g:109342;d:109590;g:109917;b:109917;h:109917;b:109917;h:109917;b:109917;h:109917;b:109917;g:115248;d:115871;tap:116964;g:117591;d:117953;g:118787;d:120154;g:121083;b:121083;h:121083;b:121083;b:121083;h:121083;b:121083;h:121083;g:133457;d:134684;g:135068;");
            sequences.add("tap:1741;tap:2834;tap:3896;d:4266;g:5126;tap:6893;tap:7740;tap:8398;tap:9177;tap:9580;tap:10002;tap:10744;d:11264;g:11986;d:12325;g:12758;d:13079;g:13489;d:13678;g:13876;d:14065;d:14288;tap:15729;d:15876;tap:17540;tap:18115;tap:18690;tap:19280;d:19562;tap:20282;d:20551;tap:21355;g:22133;d:23233;g:24272;d:24812;g:25330;tap:26671;tap:27764;tap:28853;d:29182;g:29747;");
            sequences.add("tap:2471;tap:2923;tap:4278;tap:4728;tap:5249;tap:5740;tap:6485;tap:6934;tap:7411;d:7568;g:8007;tap:9101;tap:9570;tap:9994;tap:10384;tap:11682;d:11928;g:12369;tap:12994;tap:13481;d:13715;g:14092;tap:14811;tap:15252;d:15448;g:15897;d:16323;tap:16950;tap:17429;d:17667;g:18064;d:18775;g:19254;tap:19999;tap:20457;tap:20888;tap:22232;tap:23509;g:24635;d:25486;g:25912;tap:27137;d:27241;g:27692;d:28084;tap:28745;tap:29458;tap:30183;tap:30971;tap:31387;d:32212;g:32541;tap:33113;tap:33815;tap:34455;tap:34948;tap:35376;tap:35792;d:36014;g:36440;tap:37068;tap:37544;d:37734;g:38194;d:38620;tap:39249;tap:39701;g:39964;d:40407;tap:41035;tap:41899;tap:43555;tap:44454;g:44726;d:45137;g:45579;d:46038;g:46472;tap:47173;tap:47590;d:47773;g:48237;d:48687;tap:49316;tap:49785;d:49977;g:50436;tap:51071;tap:51539;d:51739;g:52164;");
            sequences.add("tap:2172;tap:2675;tap:3519;tap:4026;tap:4833;tap:5529;tap:6665;tap:6986;tap:9611;tap:10527;tap:11614;tap:12652;tap:13729;tap:14840;tap:15820;tap:17064;d:17910;g:18926;d:19944;g:20956;d:21936;g:22418;d:22958;g:23408;d:23622;g:23853;d:24092;tap:24782;tap:25323;d:25554;g:26078;d:26520;g:26996;tap:27740;tap:28333;tap:29429;d:30110;g:31080;d:32124;tap:33451;g:34239;g:35417;d:36314;tap:37613;g:38314;d:39327;g:40465;tap:41677;tap:42766;d:43585;g:44531;tap:45806;d:46518;g:47639;g:48627;tap:49817;tap:50903;tap:51902;tap:52953;tap:53976;tap:54770;tap:55836;tap:57007;tap:58041;d:58496;g:58744;d:59277;g:59871;d:60280;g:60519;d:60692;g:60948;d:61333;g:61885;d:62360;tap:63091;tap:63871;tap:64230;g:64496;d:64693;g:65002;tap:65603;d:66010;g:66952;d:67975;tap:69323;d:70049;g:70963;tap:72319;tap:73445;d:74099;g:74756;d:75264;tap:76534;tap:77522;tap:78596;tap:79606;tap:80618;d:81360;g:82386;d:84111;tap:87000;");
            sequences.add("tap:1830;tap:4644;tap:7067;tap:8824;tap:10356;tap:11628;tap:13502;tap:14764;tap:16382;tap:17873;tap:19495;tap:21113;tap:22426;tap:23537;d:25192;g:28122;g:31151;d:34070;b:34070;b:34070;h:34070;h:34070;tap:49454;tap:50842;tap:51654;tap:52320;tap:55382;tap:58419;d:61082;g:62558;d:63355;g:64133;d:65537;g:67169;d:68724;tap:70371;tap:73466;g:74753;tap:76399;g:78192;d:78535;tap:79398;b:79398;h:79398;tap:84692;g:85119;tap:86830;b:86830;h:86830;b:86830;tap:89887;d:91133;g:94114;g:97063;tap:98873;d:100152;tap:101949;b:101949;tap:104896;tap:106409;h:106409;b:106409;h:106409;b:106409;tap:115401;tap:118317;d:121165;tap:122867;g:124094;tap:125882;d:127069;tap:128914;g:130070;tap:133362;d:134542;b:134542;d:136211;h:136211;g:139149;tap:142205;tap:145444;");
            sequences.add("tap:1784;tap:5411;tap:5716;tap:6190;tap:7092;tap:7667;g:8024;d:8346;g:8618;d:8906;tap:9455;tap:10070;tap:10681;tap:11288;d:11641;g:12049;d:12236;tap:12811;tap:13945;d:14080;g:14381;d:14644;tap:15772;tap:16086;g:16479;d:16934;g:17188;tap:17931;d:18878;g:19411;b:19411;h:19411;b:19411;h:19411;tap:21517;tap:22411;g:22870;d:23092;tap:23838;tap:24416;tap:24769;d:25121;g:25434;d:25731;tap:26278;g:27243;d:27552;g:27867;d:28123;tap:28670;tap:29762;tap:30524;d:30905;g:31443;d:32624;g:33158;b:33158;h:33158;b:33158;h:33158;tap:35878;tap:36797;tap:37364;tap:37677;d:38566;g:38993;d:40990;tap:41651;g:42924;d:43383;g:43790;tap:44601;tap:44917;d:45246;g:45689;d:46122;b:46122;h:46122;d:48143;g:48560;tap:49420;d:50623;g:51033;tap:51843;tap:52652;b:52652;h:52652;b:52652;h:52652;b:52652;h:52652;d:55488;g:55802;d:57250;g:57646;d:57956;g:58414;d:58940;g:59355;b:59355;b:59355;h:59355;b:59355;h:59355;b:59355;h:59355;b:59355;h:59355;b:59355;h:59355;g:66833;d:67297;tap:67926;g:68963;tap:69507;tap:69976;d:70149;g:70437;d:70782;tap:71271;g:71992;d:72294;g:72594;d:72937;tap:73918;tap:74283;tap:75150;tap:75776;d:76526;tap:77181;tap:77562;h:77562;b:77562;h:77562;g:78857;d:79463;g:80129;g:80727;d:80990;g:81556;h:81556;tap:82997;tap:84700;tap:85668;d:86528;g:87070;b:87070;h:87070;b:87070;h:87070;g:90345;d:90640;g:90992;d:92869;g:93239;tap:94051;g:95227;tap:95855;tap:96503;tap:96844;d:97128;g:97584;d:98101;tap:98910;tap:99462;tap:99792;g:100061;d:100494;b:100494;h:100494;tap:102195;tap:102704;g:102854;d:103209;g:103473;d:103771;tap:104634;tap:105099;g:105510;d:105874;g:106153;d:106488;tap:107065;b:107065;h:107065;tap:108287;tap:108993;tap:109322;b:109322;h:109322;tap:110886;tap:112743;b:112743;h:112743;b:112743;h:112743;b:112743;h:112743;b:112743;h:112743;b:112743;h:112743;b:112743;h:112743;b:112743;h:112743;b:112743;d:122708;g:122988;d:123295;tap:123809;tap:124710;tap:125359;tap:126217;g:126553;g:127715;d:128062;tap:128611;tap:129718;tap:130954;d:131291;g:131621;h:131621;b:131621;h:131621;tap:135751;tap:136316;b:136316;tap:138184;h:138184;b:138184;tap:140553;b:140553;h:140553;h:140553;tap:143000;g:143376;d:143732;tap:144475;h:144475;b:144475;tap:146863;b:146863;tap:148696;b:148696;tap:150217;h:150217;b:150217;g:151486;d:151798;g:152069;tap:152586;");
            sequences.add("tap:1644;tap:2090;tap:3111;tap:3586;tap:4423;tap:4988;tap:5424;tap:6288;tap:7172;tap:8107;tap:8567;tap:9022;tap:9918;tap:10805;tap:11765;tap:12216;tap:12632;tap:13538;tap:14420;tap:15188;d:15656;g:16081;tap:16708;d:16938;g:17379;d:17792;tap:18483;d:18705;g:19195;tap:19858;d:20079;g:20537;d:20971;tap:21632;g:21850;d:22309;g:22766;tap:23428;d:23637;g:24094;d:24536;tap:25197;g:25444;d:25910;tap:26629;g:26843;d:27293;tap:27954;g:28191;d:28621;tap:29364;g:29508;tap:30251;d:30498;tap:31159;d:31470;tap:32071;g:32275;tap:32918;g:33167;tap:33829;d:34005;tap:34727;d:34986;tap:35612;d:35875;tap:36535;tap:37014;g:37254;tap:37880;d:38119;d:38559;tap:39279;tap:39732;g:39942;g:40434;tap:41096;tap:41965;tap:42939;tap:43770;tap:44649;d:44927;tap:45589;tap:46023;tap:46950;g:47141;d:47580;tap:48270;d:48516;tap:49698;b:49698;b:49698;h:49698;tap:52276;d:52494;tap:53213;tap:54100;g:54337;tap:54981;h:54981;tap:55909;tap:56836;d:57511;g:57994;d:58434;tap:59061;g:59317;tap:59978;d:60232;tap:60950;g:61149;tap:61840;tap:62683;g:62988;d:63413;tap:64074;g:64366;tap:64941;tap:65835;d:66501;g:67355;d:68278;g:69192;tap:70376;tap:71287;tap:72217;d:72794;g:73707;tap:74488;d:75087;tap:75807;tap:76736;g:77355;d:78243;tap:79373;tap:80310;g:80922;tap:82029;tap:82971;d:83579;tap:84788;g:85451;tap:86171;g:86863;tap:87525;d:87983;tap:88936;g:89140;tap:89767;tap:90701;b:90701;tap:91631;b:91631;tap:92514;b:92514;tap:93435;b:93435;tap:94337;b:94337;tap:95218;tap:96097;tap:96995;tap:97892;h:97892;tap:98756;h:98756;tap:99708;tap:100197;tap:101068;h:101068;tap:102376;h:102376;tap:103378;h:103378;tap:104202;h:104202;tap:106005;d:106244;g:106751;");
            sequences.add("tap:1615;tap:2064;tap:2965;tap:3400;tap:4282;tap:4724;tap:5608;tap:6067;tap:6939;tap:7404;tap:8284;tap:8744;tap:11368;tap:11776;tap:12745;tap:13168;tap:14917;tap:15376;tap:16265;tap:17109;tap:17640;tap:18059;tap:18541;d:18737;tap:19398;tap:19907;tap:20754;tap:21238;d:21449;g:21850;tap:22477;tap:23373;d:24017;g:24466;tap:25126;d:25358;tap:25985;d:26262;g:26719;tap:27408;d:27689;g:28085;d:28485;tap:29176;g:29357;tap:30048;d:30304;tap:30930;tap:31439;d:31623;g:32104;tap:32732;tap:33628;tap:34497;b:34497;h:34497;b:34497;tap:36243;d:36458;d:37396;tap:38085;d:38305;tap:38909;tap:39869;g:40040;tap:40730;d:41002;tap:41606;g:41852;tap:42512;d:42742;tap:43386;b:43386;tap:44239;h:44239;tap:45111;d:45292;tap:46476;d:46669;tap:47359;tap:48228;g:48402;tap:49120;tap:49892;g:50336;tap:50939;b:50939;h:50939;tap:52283;tap:53151;tap:54072;g:54279;tap:54940;tap:55420;g:55672;tap:56247;tap:57199;tap:58069;g:58355;tap:58958;b:58958;tap:59798;tap:60749;b:60749;tap:61584;tap:62505;tap:62947;tap:64288;h:64288;b:64288;h:64288;tap:65980;d:66215;g:66698;g:67612;g:68501;tap:69190;d:69496;d:70272;d:71118;tap:71837;tap:72228;tap:73157;b:73157;h:73157;tap:74469;tap:75402;tap:76306;tap:77169;b:77169;h:77169;b:77169;tap:78911;d:79139;tap:80295;g:80462;tap:81153;tap:82088;b:82088;h:82088;b:82088;tap:84262;g:84442;d:84946;g:85363;tap:86008;tap:86957;tap:87875;b:87875;tap:88728;tap:90468;h:90468;tap:91770;g:92968;h:92968;d:94640;b:94640;g:96366;h:96366;d:98029;b:98029;g:99930;h:99930;d:101706;h:101706;tap:103788;tap:104728;tap:105643;tap:106567;tap:107433;tap:108301;tap:109162;g:109354;tap:110053;d:110356;tap:110969;b:110969;tap:111821;h:111821;tap:112679;tap:113105;tap:114036;tap:114924;tap:115875;tap:117143;tap:118068;tap:118966;tap:119866;tap:120725;tap:121613;tap:122485;tap:123349;d:124036;g:124476;tap:125603;tap:126523;");
            sequences.add("tap:1222;tap:2633;tap:3601;tap:4584;tap:5384;tap:5817;tap:6234;d:7914;g:8380;d:8806;g:9681;d:10593;b:10593;h:10593;b:10593;b:10593;g:16010;b:16010;tap:19019;d:19643;tap:20768;d:21475;tap:22600;tap:23497;b:23497;b:23497;tap:26261;g:26973;tap:28063;tap:29029;h:29029;d:29659;g:30588;b:30588;tap:32599;h:32599;b:32599;h:32599;b:32599;d:35976;g:36479;b:36479;tap:38985;tap:39994;h:39994;b:39994;tap:41727;d:42332;tap:43576;g:44198;tap:45291;tap:47089;d:47399;b:47399;tap:49024;tap:50840;g:51455;h:51455;b:51455;d:53267;tap:54425;b:54425;g:56039;b:56039;h:56039;tap:58064;d:58701;g:59638;b:59638;tap:61713;h:61713;b:61713;tap:63450;tap:65355;h:65355;b:65355;tap:67174;d:67848;tap:68983;tap:69454;d:69677;g:70553;b:70553;h:70553;b:70553;tap:73471;d:74163;g:74701;d:75185;tap:76306;b:76306;h:76306;b:76306;h:76306;g:78760;d:79627;tap:80845;g:81395;d:81887;b:81887;tap:83543;tap:84451;g:84896;d:85160;b:85160;h:85160;tap:88140;tap:89050;tap:89467;tap:89933;tap:90883;tap:91799;d:92370;g:92844;g:93770;g:94694;tap:95870;g:96486;tap:97148;g:97828;tap:98935;b:98935;b:98935;h:98935;tap:102602;b:102602;tap:104469;h:104469;b:104469;h:104469;tap:107109;tap:109948;g:110484;d:111500;tap:112683;g:113372;tap:117239;tap:118110;d:119705;g:120595;d:121119;g:121921;g:123293;b:123293;h:123293;b:123293;h:123293;g:126022;d:126986;tap:128117;d:128753;b:128753;h:128753;tap:130784;b:130784;tap:132679;h:132679;tap:134432;b:134432;h:134432;tap:136213;tap:138091;tap:139006;g:139643;tap:141721;d:142349;tap:143502;tap:144938;g:145968;b:145968;tap:148032;h:148032;tap:149946;b:149946;h:149946;b:149946;tap:152621;d:153293;g:154205;d:154654;g:155171;d:155563;b:155563;tap:157207;d:157854;g:158326;d:158753;b:158753;tap:160836;tap:162463;tap:164532;tap:166264;tap:168041;b:168041;h:168041;tap:173514;tap:174782;tap:177114;tap:179013;tap:180344;g:180641;g:181534;d:182485;tap:183551;g:183773;d:184237;g:186023;d:186656;g:186969;b:186969;tap:188078;h:188078;tap:189965;b:189965;h:189965;b:189965;h:189965;g:191568;d:192473;b:192473;h:192473;b:192473;tap:194910;d:195178;tap:196415;b:196415;");
            sequences.add("tap:1902;tap:2741;tap:3498;tap:4170;tap:4507;tap:4825;tap:5132;tap:5475;tap:5834;tap:6581;tap:7064;tap:7453;d:7819;g:8211;d:8574;g:8959;d:9271;tap:9787;tap:10120;d:10245;g:10814;d:11217;g:11589;d:11999;b:11999;h:11999;b:11999;h:11999;b:11999;h:11999;b:11999;h:11999;g:18451;d:18791;d:20072;g:20494;d:21777;g:22105;tap:23129;tap:23817;b:23817;h:23817;b:23817;g:26334;d:27284;g:27819;b:27819;h:27819;b:27819;h:27819;b:27819;g:31649;d:32017;g:32322;tap:33521;d:33998;g:34316;tap:34980;g:35919;d:36368;tap:37343;b:37343;h:37343;b:37343;h:37343;b:37343;g:40794;tap:41772;tap:42245;tap:44100;tap:45874;tap:46328;tap:46641;tap:47384;tap:48108;tap:49840;tap:50203;d:50961;g:51782;d:52131;b:52131;h:52131;tap:54496;tap:54901;d:55694;tap:56324;g:56619;tap:58036;d:58712;tap:59806;g:60437;tap:61797;b:61797;h:61797;b:61797;h:61797;b:61797;tap:65035;h:65035;b:65035;h:65035;b:65035;h:65035;tap:67815;d:68465;g:69469;h:69469;b:69469;d:72218;g:72514;d:72894;g:73182;b:73182;h:73182;b:73182;d:75408;g:76377;tap:77587;b:77587;tap:79645;tap:80622;tap:81629;tap:82585;tap:83629;tap:84575;tap:85558;tap:86577;tap:87577;tap:88584;d:88769;g:89066;d:89384;tap:90304;tap:90633;tap:91297;b:91297;h:91297;");
            sequences.add("tap:1300;tap:1862;tap:2394;tap:2923;tap:3418;tap:3917;tap:4459;tap:4960;tap:5420;d:5732;tap:6403;tap:6935;tap:7410;tap:7928;tap:8393;g:8634;tap:9364;tap:9929;tap:10406;tap:10932;tap:11390;d:11661;g:12693;tap:13394;tap:13913;tap:14443;tap:14920;d:15168;g:15498;d:15712;g:15944;d:16182;g:16413;tap:16878;tap:17423;g:17660;tap:18390;tap:18921;d:19163;tap:19863;tap:20374;g:20627;tap:21379;d:21676;tap:22367;tap:23379;tap:23699;tap:25382;tap:25923;tap:26397;tap:26931;tap:27457;tap:27946;tap:28452;g:28631;g:29706;d:30697;d:31671;tap:32424;tap:32931;tap:33884;tap:34932;g:35717;d:36655;g:37626;d:38602;tap:40450;tap:40931;tap:41879;tap:42897;tap:43901;b:43901;tap:44868;b:44868;tap:45889;b:45889;tap:46890;h:46890;tap:47947;d:48166;tap:48911;tap:51848;tap:52895;d:53329;g:53759;d:54176;tap:54838;d:55676;g:56721;tap:58388;tap:58864;d:59614;g:60659;d:61654;tap:62375;tap:62908;tap:63617;tap:64392;tap:65941;d:66194;tap:66885;d:67722;g:68616;tap:69915;tap:70904;tap:71925;tap:72920;d:73174;g:73728;d:74178;tap:74869;g:75629;tap:76838;tap:77905;g:78741;d:79654;g:80720;tap:81466;tap:82360;g:82671;d:83650;g:84698;b:84698;tap:86830;h:86830;tap:88896;tap:90390;b:90390;tap:91887;g:92709;tap:93921;tap:94906;tap:95890;g:96716;tap:97907;d:98742;tap:99931;d:100633;tap:101880;d:102601;tap:103859;d:104620;tap:105866;g:106607;tap:107894;d:108570;tap:109856;g:110641;tap:111891;tap:112903;tap:113905;d:114532;tap:115848;b:115848;h:115848;b:115848;h:115848;b:115848;h:115848;b:115848;tap:122828;d:123157;g:123685;d:124094;tap:124837;g:125134;b:125134;h:125134;d:126587;g:127116;b:127116;h:127116;g:128628;d:129122;b:129122;h:129122;d:130606;");
            sequences.add("tap:1479;tap:2229;tap:3713;tap:5344;tap:6782;tap:8376;tap:9834;tap:11356;tap:12192;tap:12500;tap:12919;tap:13302;tap:13663;tap:14020;tap:14378;tap:14762;d:14910;d:15303;tap:15907;tap:16251;tap:17375;tap:17777;g:17916;g:18287;tap:18891;tap:19561;tap:20394;d:20533;d:20895;g:21268;tap:21850;tap:22493;tap:22991;g:23159;d:23510;tap:24116;d:24626;tap:25666;d:26289;g:26933;tap:27886;g:28406;tap:29358;tap:30160;tap:30568;tap:30944;d:31441;g:32117;b:32117;b:32117;tap:34595;tap:35017;tap:35372;tap:35752;tap:36121;tap:36479;tap:36818;d:36966;g:37450;tap:38407;tap:38746;tap:39099;d:39296;tap:39879;g:40423;tap:41389;d:41875;tap:42917;d:43171;g:43746;tap:44323;d:44490;tap:45065;tap:45475;b:45475;h:45475;b:45475;tap:47807;tap:48151;tap:48492;g:48648;d:49573;g:50050;tap:50770;tap:51132;tap:51508;tap:51897;tap:52258;tap:52600;tap:52960;tap:53332;tap:53694;tap:54102;g:54993;d:55336;tap:55940;tap:56356;b:56356;h:56356;b:56356;tap:57880;d:58233;g:58726;tap:59298;tap:59739;b:59739;h:59739;tap:60823;d:61011;g:61390;tap:61993;tap:62411;tap:62751;tap:63094;tap:63474;g:63633;d:64339;tap:65000;tap:65372;tap:65769;tap:66097;tap:66450;g:66665;d:67197;tap:67975;tap:68359;tap:69878;tap:72882;tap:74387;tap:75847;tap:76613;tap:77420;tap:78080;d:78283;g:78634;tap:79353;tap:79908;tap:80750;tap:81092;tap:81510;g:81648;d:82183;g:82756;tap:83358;tap:83746;tap:84101;tap:84468;tap:84852;tap:85399;tap:85949;g:86484;d:86856;g:87199;tap:87802;tap:88450;tap:88932;tap:90516;tap:92356;g:93275;h:93275;b:93275;h:93275;b:93275;h:93275;tap:96805;tap:97254;tap:98365;tap:99818;tap:102131;tap:102910;tap:104380;tap:104822;tap:105159;tap:105502;g:105614;d:106030;g:106402;d:106782;tap:107355;tap:107780;tap:108085;tap:108465;tap:108823;tap:109496;d:110542;g:110884;d:111227;tap:111888;tap:112360;tap:113399;b:113399;h:113399;b:113399;tap:114849;tap:115471;tap:115975;tap:116801;tap:117121;tap:117491;b:117491;h:117491;tap:118998;tap:119357;tap:119756;b:119756;h:119756;tap:120836;tap:121458;tap:121984;tap:122377;tap:122771;tap:123131;tap:123510;tap:123869;tap:124559;tap:125007;tap:125787;tap:126116;g:126240;d:126574;tap:127471;h:127471;b:127471;tap:129461;tap:129850;g:130079;d:130454;g:130781;tap:131409;tap:131800;tap:132126;g:132242;tap:132846;b:132846;h:132846;b:132846;tap:134779;tap:135098;h:135098;b:135098;h:135098;g:137130;d:137464;tap:138126;tap:138493;tap:138848;b:138848;h:138848;tap:140350;tap:140721;d:140862;g:141218;tap:141863;d:142078;g:142445;d:142740;tap:143346;tap:143703;tap:144106;tap:144466;g:144632;tap:146729;");
            sequences.add("tap:1241;tap:1727;tap:2363;tap:2996;tap:4212;tap:4814;tap:5986;g:6960;tap:7795;d:8105;tap:8982;tap:9592;g:10024;tap:10801;tap:12259;d:12384;g:12672;d:12952;d:14752;g:15056;tap:15633;tap:16200;d:16553;g:17234;tap:17980;tap:18577;tap:19479;g:19586;d:19902;tap:20418;d:21109;g:21382;h:21382;b:21382;h:21382;b:21382;g:25281;d:25568;g:25865;d:26169;tap:26685;tap:27051;tap:27649;tap:29096;d:29202;g:29760;tap:30570;tap:31199;d:32139;g:32758;d:33030;b:33030;h:33030;h:33030;h:33030;b:33030;h:33030;h:33030;h:33030;h:33030;h:33030;b:33030;h:33030;b:33030;h:33030;g:37850;d:38096;g:38389;tap:38992;tap:39955;g:40893;d:41156;tap:41967;g:42326;d:42644;g:42941;d:43246;tap:43791;g:44178;d:44783;tap:45562;h:45562;b:45562;h:45562;tap:46784;g:46972;g:47771;d:48075;g:48393;tap:49157;h:49157;b:49157;tap:50365;h:50365;b:50365;h:50365;b:50365;h:50365;tap:53393;b:53393;h:53393;b:53393;h:53393;d:55623;g:56199;d:56436;g:56995;d:57308;g:57588;tap:58201;tap:58800;tap:59382;d:59707;g:60028;d:60326;tap:61174;tap:61798;d:62743;g:63093;d:63373;tap:64166;tap:64779;tap:65432;tap:66032;g:66350;d:66684;g:66956;d:67282;tap:67836;b:67836;h:67836;b:67836;h:67836;b:67836;h:67836;b:67836;h:67836;b:67836;h:67836;b:67836;h:67836;b:67836;h:67836;tap:72622;tap:73526;tap:75279;tap:76160;d:77162;tap:78024;d:78141;g:78490;d:78986;tap:79791;d:80166;tap:81024;h:81024;tap:82192;b:82192;tap:83382;g:83776;tap:84587;h:84587;tap:85752;d:86114;tap:86976;b:86976;tap:88204;g:88549;tap:89382;h:89382;tap:90580;d:91005;tap:91811;tap:92394;tap:93012;tap:93583;tap:94215;tap:94547;tap:95199;tap:95940;g:96430;tap:97206;b:97206;h:97206;b:97206;h:97206;b:97206;h:97206;g:101851;g:102903;g:104161;g:105345;d:105633;g:105937;tap:106787;tap:107358;b:107358;h:107358;b:107358;b:107358;h:107358;tap:114001;d:114433;g:114694;d:115007;tap:115816;tap:116376;d:116746;tap:117584;g:118051;tap:118823;d:119316;tap:120015;h:120015;tap:121224;tap:121806;tap:122447;tap:122998;b:122998;h:122998;tap:123903;tap:125403;b:125403;tap:126355;tap:127522;h:127522;b:127522;h:127522;b:127522;tap:130208;d:130541;tap:131381;tap:131964;tap:132608;tap:133155;g:133541;d:134149;tap:135019;tap:135599;tap:136198;tap:136817;tap:137415;d:137602;g:137847;d:138144;g:138735;d:139006;g:139293;tap:139839;d:140204;tap:141012;d:141336;d:142593;g:142906;d:143202;tap:143693;g:144109;d:144439;tap:145251;d:145545;g:146674;d:147746;g:148278;d:148583;g:148838;tap:149413;d:149731;tap:150307;tap:150618;g:150995;d:151265;tap:151763;g:152166;d:152492;tap:153331;tap:153661;d:153974;tap:154817;tap:155979;tap:157196;d:157870;g:158141;d:158438;tap:158982;h:158982;tap:160196;h:160196;tap:161398;h:161398;tap:162628;h:162628;tap:163812;h:163812;tap:164973;h:164973;tap:166233;h:166233;h:166233;b:166233;tap:168572;d:168944;g:169274;d:169596;g:169925;d:170197;g:171126;d:171388;g:171731;tap:172543;tap:173381;tap:174023;d:174382;g:174990;d:175612;tap:176401;h:176401;d:178040;b:178040;g:179199;d:179805;tap:180614;g:180984;tap:181830;tap:182431;h:182431;g:184154;tap:184886;d:185209;g:185693;d:186094;g:186453;d:186700;g:186980;b:186980;tap:187784;tap:188438;b:188438;tap:189593;tap:190238;b:190238;tap:191399;tap:192011;b:192011;tap:193292;g:193578;d:194216;tap:195027;g:195436;tap:196215;tap:196768;h:196768;tap:198018;d:198320;g:198666;d:198970;tap:199791;g:200183;d:200487;tap:201012;g:201412;d:201715;tap:202206;tap:202769;b:202769;tap:204000;h:204000;tap:205197;d:206149;g:206446;tap:207022;b:207022;tap:208189;d:209166;g:209527;d:209877;g:210181;d:210436;g:211266;d:211593;tap:212414;d:212771;g:213137;d:213431;tap:214223;tap:214810;g:215237;b:215237;tap:217822;tap:218380;tap:219003;h:219003;b:219003;tap:223706;tap:224378;g:225068;d:225380;g:225660;tap:226217;tap:226850;tap:227988;b:227988;h:227988;b:227988;g:230697;d:231390;g:232489;d:233084;b:233084;h:233084;b:233084;h:233084;g:235559;d:236157;b:236157;h:236157;h:236157;b:236157;h:236157;b:236157;tap:240673;g:240959;d:242155;b:242155;h:242155;");
            sequences.add("tap:1112;tap:1806;tap:2263;tap:2875;tap:3609;tap:4205;tap:4875;tap:6158;tap:7588;tap:8441;tap:8864;tap:9478;tap:10190;d:10602;g:10956;tap:11558;d:11999;g:12370;tap:12836;d:13279;tap:13882;tap:14801;g:15280;tap:16201;tap:16867;b:16867;tap:18101;b:18101;tap:20143;d:20317;g:20656;d:20977;tap:21550;tap:22157;d:22541;tap:23488;g:23920;tap:24843;d:25172;tap:26143;b:26143;tap:27459;b:27459;tap:28775;h:28775;tap:30093;b:30093;tap:31456;tap:32147;tap:32848;tap:33529;tap:34181;d:34537;tap:35487;g:35887;tap:36808;tap:37501;d:37925;g:38593;tap:39485;g:39959;d:40594;tap:41451;g:41859;tap:42835;d:43208;tap:44129;g:44621;tap:45456;tap:46417;tap:46837;tap:48118;tap:48886;tap:49531;d:49741;g:49971;tap:50829;g:51302;d:51910;g:52564;tap:53529;d:53908;g:55064;d:56396;tap:58140;d:59176;tap:60140;g:60536;tap:61463;d:61866;tap:62736;g:63240;tap:64079;d:64474;tap:65380;tap:66128;tap:66763;b:66763;tap:68043;h:68043;tap:69430;g:69884;tap:70730;b:70730;tap:72063;b:72063;tap:73408;d:73778;g:74562;tap:75433;d:75787;tap:76773;g:77161;tap:78088;tap:78795;d:79140;tap:80127;tap:80807;b:80807;tap:82114;b:82114;tap:83395;h:83395;tap:84774;h:84774;tap:86117;b:86117;tap:87433;h:87433;tap:88744;h:88744;tap:90099;g:90515;tap:91421;d:91791;tap:92758;g:93160;tap:94058;d:94402;tap:95415;b:95415;tap:96695;h:96695;tap:98051;b:98051;tap:99417;h:99417;tap:100725;tap:101453;d:101765;g:102513;tap:103358;d:103757;tap:104739;tap:105429;g:105883;tap:106746;d:107171;tap:108015;b:108015;tap:109318;h:109318;tap:110660;b:110660;tap:111995;h:111995;tap:113351;b:113351;tap:114687;d:115090;tap:116158;g:116511;tap:117368;tap:118082;tap:118783;d:119087;tap:120041;tap:121364;tap:122045;tap:122695;tap:123315;tap:124736;tap:125723;tap:126060;tap:127023;tap:127375;tap:128021;tap:128364;tap:128741;tap:129061;tap:129416;tap:130025;tap:130725;tap:131405;tap:131721;tap:132025;tap:132742;tap:133357;tap:134044;tap:134752;tap:135399;tap:136317;tap:136698;tap:137608;tap:138011;tap:138677;tap:139387;tap:139752;tap:140081;tap:140712;tap:141347;d:141730;g:142448;d:143124;b:143124;b:143124;g:144965;d:145767;tap:146665;g:147152;tap:147989;d:148361;tap:149341;b:149341;b:149341;h:149341;tap:151972;d:152342;tap:153280;b:153280;tap:154687;tap:155352;g:156516;tap:157329;d:157672;tap:158652;g:159092;tap:159961;d:160408;g:161083;d:161743;g:162467;d:163168;");
            sequences.add("tap:1540;tap:2019;tap:2856;tap:3396;tap:3978;tap:4605;tap:5692;tap:10550;tap:11133;tap:11926;g:13112;d:13384;g:13656;d:13895;tap:14929;tap:16616;g:18560;g:20476;d:20756;g:22377;d:22632;tap:23123;tap:23707;g:25125;d:25656;tap:26430;d:27771;g:28101;d:28899;g:29477;d:29979;b:29979;h:29979;b:29979;d:32155;g:32725;h:32725;b:32725;tap:34574;d:34689;g:35435;tap:36241;h:36241;tap:37322;b:37322;tap:38414;h:38414;tap:39505;d:39809;g:40377;tap:41136;b:41136;tap:42200;b:42200;h:42200;tap:43859;tap:44422;tap:44994;tap:46070;tap:47135;tap:47708;tap:48173;g:49118;tap:49837;tap:50432;d:50695;g:51266;tap:52045;d:52421;d:52986;tap:53706;tap:54244;g:54594;tap:55284;d:55619;tap:56360;g:56741;tap:57459;d:57740;tap:58545;tap:59141;b:59141;h:59141;tap:60731;d:61037;tap:61846;tap:62963;g:63303;g:63862;tap:64554;d:64815;tap:65674;h:65674;d:67104;tap:67884;tap:68923;g:69251;tap:70061;d:70329;tap:71138;b:71138;tap:72189;g:72538;tap:73318;d:73546;d:74561;g:74849;tap:75493;d:75798;g:76371;d:76969;tap:77688;tap:78207;b:78207;b:78207;h:78207;b:78207;h:78207;tap:81457;tap:82006;d:82277;g:82867;tap:83678;d:83936;tap:84743;d:85029;tap:85836;h:85836;tap:86919;h:86919;tap:88047;h:88047;tap:89103;tap:89678;h:89678;tap:90716;tap:91831;tap:92146;d:93333;g:93953;d:94355;g:94962;g:95476;g:96042;g:96568;g:97111;tap:97867;d:98180;g:98459;d:98714;g:98994;tap:99512;b:99512;tap:100580;tap:101699;b:101699;h:101699;tap:103324;tap:103853;tap:104175;tap:105514;tap:106589;tap:107718;tap:108216;d:108455;g:109078;d:109357;g:110167;tap:110919;b:110919;b:110919;h:110919;b:110919;tap:113083;h:113083;tap:114178;b:114178;tap:115304;d:115620;tap:116392;g:116699;tap:118673;");
            sequences.add("tap:2440;tap:3215;tap:3977;tap:4783;tap:5597;tap:6417;tap:7146;tap:7983;tap:8790;tap:9627;d:10129;tap:11166;g:11776;tap:12788;d:13321;tap:14414;g:14969;tap:15947;b:15947;h:15947;tap:17570;tap:18393;d:18915;g:19755;tap:20784;d:21352;tap:22361;g:23014;tap:23938;d:24504;tap:25574;g:26148;b:26148;tap:27176;tap:28024;h:28024;tap:29557;d:30062;tap:31104;g:31616;tap:32740;d:33251;tap:34407;b:34407;tap:35919;h:35919;tap:37505;d:38040;tap:39164;tap:39954;g:40272;tap:41511;d:42133;tap:43145;b:43145;b:43145;b:43145;tap:46355;h:46355;h:46355;tap:49571;g:50111;tap:51766;d:52491;tap:53118;g:53327;d:53699;g:54324;d:54857;g:55280;tap:55971;tap:56769;tap:57621;d:57738;tap:58341;g:58540;tap:59140;h:59140;tap:59936;d:60090;tap:60750;b:60750;tap:61593;h:61593;tap:62328;d:62498;tap:63158;d:63337;tap:63908;d:64067;tap:65488;h:65488;tap:66348;tap:66761;tap:67220;tap:67592;d:67780;g:68141;tap:68767;b:68767;h:68767;tap:69970;b:69970;h:69970;b:69970;tap:71559;d:71724;tap:72350;h:72350;tap:73175;h:73175;tap:73946;g:74161;d:74504;tap:75131;h:75131;tap:75923;d:76114;tap:76757;b:76757;tap:77569;h:77569;tap:78299;d:78490;g:78956;d:79323;tap:80014;tap:80440;tap:80828;g:80978;tap:81552;b:81552;tap:82333;g:82512;tap:83172;d:83338;tap:83964;h:83964;b:83964;tap:85166;g:85372;tap:85973;d:86152;tap:86756;b:86756;h:86756;tap:87916;h:87916;tap:88743;g:88974;tap:89576;tap:89976;tap:90384;g:91324;d:91766;tap:92728;tap:93178;h:93178;tap:93954;h:93954;b:93954;tap:96384;tap:96749;b:96749;tap:97541;b:97541;tap:98355;b:98355;tap:99151;b:99151;tap:99932;b:99932;tap:100749;d:100912;g:101336;d:101727;tap:102351;d:102536;tap:103164;g:103276;tap:103903;d:104143;tap:104745;d:105008;g:105359;d:106121;g:106531;tap:107133;h:107133;b:107133;h:107133;b:107133;h:107133;b:107133;h:107133;tap:110354;g:110535;d:111653;tap:112691;tap:113952;tap:116300;tap:116715;tap:117126;g:117686;tap:118312;d:118502;tap:119192;tap:119601;g:119774;tap:120346;d:120557;tap:121159;d:121375;tap:121976;d:122146;tap:122751;tap:123577;tap:124359;g:124534;tap:125137;d:125689;tap:126349;b:126349;tap:127547;d:127743;g:128169;d:128565;tap:129139;g:129305;d:129736;tap:130363;d:130566;tap:131169;g:131366;b:131366;h:131366;tap:132716;tap:133557;tap:134398;g:134569;d:134957;tap:135561;h:135561;tap:136341;h:136341;tap:137066;d:137277;tap:137938;g:138136;tap:138710;h:138710;tap:139500;d:139711;g:140138;tap:140740;h:140740;tap:141528;g:141721;tap:142348;tap:143147;g:143310;d:143698;tap:144323;g:144547;d:144923;tap:145552;g:145850;tap:146366;tap:147137;d:147286;tap:147946;h:147946;tap:148739;h:148739;tap:149517;h:149517;tap:150364;b:150364;tap:151169;b:151169;tap:151950;g:152174;tap:152776;tap:153573;tap:154343;tap:155146;d:155349;tap:156010;tap:156775;g:157260;d:157653;b:157653;h:157653;b:157653;h:157653;b:157653;g:160077;g:160535;d:160883;d:161317;tap:161892;tap:162343;h:162343;tap:163197;h:163197;tap:163929;tap:164338;tap:164805;");
            sequences.add("tap:1228;tap:2406;tap:2897;tap:3472;tap:4125;tap:5102;tap:5578;tap:6453;tap:7193;tap:7705;tap:8189;tap:8511;tap:9337;tap:9888;tap:10337;tap:10725;tap:11483;tap:12048;tap:12518;tap:13608;tap:14150;tap:15030;tap:15734;tap:16274;tap:17109;tap:17895;d:18196;tap:18867;tap:19517;d:19873;tap:20594;tap:21028;tap:22230;d:22486;tap:23334;tap:23821;d:24121;tap:24900;g:25099;tap:25948;b:25948;tap:26976;h:26976;tap:28080;d:28442;g:28934;tap:29892;tap:30196;h:30196;tap:31268;d:31564;tap:32353;g:32674;tap:33481;tap:33919;tap:34284;h:34284;d:35302;tap:36099;tap:36438;h:36438;tap:37732;tap:38050;tap:38840;tap:39363;d:39630;tap:40411;tap:40966;g:41271;d:41772;tap:42491;tap:42810;d:43351;tap:44166;tap:44666;tap:45049;d:45325;tap:45824;tap:46322;g:46553;tap:47361;g:47667;tap:48456;d:48623;tap:49285;b:49285;h:49285;tap:51058;tap:51395;tap:52210;tap:52699;tap:53556;tap:54361;tap:54912;tap:55458;d:55672;g:55951;d:56230;b:56230;h:56230;b:56230;h:56230;b:56230;h:56230;g:58008;d:58206;g:58420;d:58676;b:58676;h:58676;b:58676;h:58676;b:58676;h:58676;b:58676;g:60596;d:60835;g:61089;d:61312;b:61312;h:61312;b:61312;h:61312;g:62657;d:62903;g:63175;d:63455;b:63455;h:63455;b:63455;h:63455;tap:65051;tap:65607;tap:66674;g:67502;d:67749;h:67749;b:67749;h:67749;tap:69541;g:70152;d:70397;h:70397;d:72324;g:72555;d:72786;tap:73589;b:73589;h:73589;h:73589;b:73589;h:73589;d:75852;g:76090;g:77441;d:77730;g:77975;d:78790;g:79028;d:79334;g:79589;tap:80071;b:80071;h:80071;b:80071;tap:82207;g:82484;tap:83242;d:83515;tap:84320;b:84320;h:84320;b:84320;h:84320;tap:86973;d:88358;tap:89165;tap:89912;tap:90246;d:90656;g:91124;tap:92903;tap:93459;tap:94758;g:95450;d:95721;g:95960;d:96210;g:96456;tap:97198;d:97467;g:98045;d:98370;g:98593;d:99118;g:99566;d:100058;g:100432;d:100703;tap:101515;h:101515;b:101515;d:103429;tap:104172;h:104172;b:104172;tap:105205;d:105569;tap:106311;h:106311;b:106311;h:106311;b:106311;tap:107946;d:108161;g:108593;d:108928;g:109225;d:109488;tap:110061;d:110276;tap:111152;h:111152;tap:112236;b:112236;tap:113318;d:113587;g:114167;tap:114858;b:114858;tap:115884;b:115884;h:115884;tap:117607;h:117607;tap:118619;b:118619;tap:119665;h:119665;tap:120751;b:120751;tap:121821;h:121821;tap:122878;h:122878;tap:123959;tap:124490;d:124796;g:125362;tap:126106;d:126368;tap:127176;tap:128774;tap:129308;d:129636;tap:130376;tap:130934;tap:131493;b:131493;tap:132551;tap:133137;b:133137;h:133137;tap:134651;tap:135273;b:135273;tap:136298;h:136298;b:136298;tap:137926;h:137926;tap:138955;tap:139510;b:139510;tap:140604;h:140604;tap:141635;tap:142196;d:142461;tap:143552;tap:145425;g:146824;d:147383;tap:148113;d:148451;tap:149178;tap:150740;g:151219;d:151620;tap:152894;d:153208;g:153601;tap:154529;d:154826;");
            sequences.add("tap:993;tap:1483;tap:1891;tap:3778;tap:4354;tap:4766;tap:6264;b:6264;tap:7346;tap:9963;g:11964;tap:12825;tap:14122;tap:14951;tap:15297;tap:15665;tap:17728;tap:18288;tap:20443;tap:20814;tap:21165;g:22444;d:22799;g:23835;d:24140;g:25181;d:25624;g:25928;d:26256;tap:26771;tap:27143;tap:27473;g:27672;d:28001;g:28352;d:28713;g:29017;tap:29563;g:30372;tap:30946;tap:31305;h:31305;b:31305;h:31305;g:32496;d:32793;g:33131;d:33491;tap:34038;tap:34438;tap:34789;tap:35216;tap:35517;tap:36523;g:37371;d:37674;tap:38180;tap:38598;tap:38903;d:39089;g:39408;d:39738;b:39738;h:39738;tap:40933;tap:41259;d:41412;tap:42073;g:43151;d:43454;g:43789;d:44178;tap:44760;tap:45919;tap:46526;tap:47495;tap:48679;tap:49236;tap:49591;tap:50292;tap:50925;tap:51378;tap:52359;tap:53100;tap:53715;tap:54222;tap:54706;tap:55059;tap:55807;tap:56462;d:56732;tap:57837;g:58270;tap:59236;g:59667;tap:60561;d:60977;tap:61941;d:62433;tap:63384;g:63776;tap:64755;tap:65484;tap:66109;d:66556;tap:67445;b:67445;tap:68821;h:68821;tap:70234;h:70234;tap:71579;b:71579;tap:72964;g:73377;tap:74357;d:74790;tap:75778;g:76202;tap:77099;b:77099;tap:78519;h:78519;tap:79779;d:80181;tap:81157;b:81157;tap:82609;g:83077;tap:84053;h:84053;tap:85349;d:85817;tap:86734;tap:87530;tap:88166;b:88166;h:88166;b:88166;tap:89640;tap:89973;g:90080;d:90418;tap:90993;tap:92061;tap:92787;g:93052;d:93492;tap:94066;b:94066;h:94066;b:94066;tap:96511;d:96854;tap:97832;g:98191;tap:99186;h:99186;tap:100524;b:100524;tap:101941;h:101941;tap:103371;b:103371;tap:104724;d:105125;g:105862;tap:106742;b:106742;tap:108191;d:108565;tap:109578;b:109578;tap:110952;d:111348;tap:112322;tap:112689;g:112813;d:113218;tap:113764;tap:114446;tap:115143;d:115295;g:115646;d:115968;tap:116542;b:116542;tap:117844;h:117844;tap:119278;b:119278;tap:120512;h:120512;tap:121974;b:121974;tap:123265;h:123265;tap:124758;tap:125084;b:125084;tap:126089;b:126089;tap:127533;h:127533;tap:128791;b:128791;tap:130142;g:130527;tap:131503;tap:132115;tap:132418;d:132567;g:132909;d:133223;tap:134781;tap:135127;tap:135439;d:135612;tap:136275;tap:137528;tap:137838;tap:139224;d:140041;g:140458;tap:141356;g:141768;d:142487;g:143217;d:143861;g:144544;d:145125;g:145949;d:146556;g:147237;d:147959;tap:148854;g:149297;d:150021;b:150021;b:150021;b:150021;tap:153020;tap:155121;h:155121;b:155121;h:155121;b:155121;h:155121;g:158808;b:158808;h:158808;b:158808;g:161542;b:161542;h:161542;d:163771;g:164260;tap:166062;tap:166734;b:166734;b:166734;h:166734;d:169635;tap:170220;b:170220;tap:172251;b:172251;d:174092;g:174536;d:174831;g:175256;tap:177166;tap:177866;tap:178565;tap:179291;b:179291;tap:180708;b:180708;tap:181989;h:181989;tap:183398;h:183398;tap:184722;d:185173;tap:186126;g:186614;tap:187484;d:187914;tap:188851;g:189310;tap:190214;d:190727;tap:191597;b:191597;tap:192948;h:192948;tap:194317;b:194317;tap:195784;g:196213;tap:197144;g:197631;tap:198499;tap:199236;tap:199929;d:200418;tap:201312;tap:202014;b:202014;tap:203410;tap:204067;h:204067;tap:205455;tap:206210;tap:206840;d:207262;tap:208199;tap:209572;d:210701;tap:211664;g:212131;tap:212975;h:212975;tap:214369;b:214369;tap:215750;d:216338;tap:217263;b:217263;tap:218583;h:218583;tap:219894;b:219894;tap:221285;tap:222053;tap:222756;tap:223404;tap:224084;tap:224764;tap:225451;g:225962;d:226642;tap:227534;g:227930;tap:228853;d:229401;b:229401;tap:230912;g:231417;tap:232338;b:232338;tap:233713;b:233713;tap:235100;h:235100;tap:236403;h:236403;tap:237890;d:238219;tap:239207;g:239580;tap:240620;g:241092;tap:242043;d:242447;tap:243425;b:243425;h:243425;b:243425;h:243425;b:243425;tap:248912;tap:249268;g:249627;d:250000;tap:251623;d:252602;tap:254416;tap:255871;tap:257278;");
            sequences.add("tap:1052;tap:2391;tap:3707;tap:4305;tap:5013;tap:5672;tap:6324;tap:6988;tap:7631;tap:8301;tap:8968;tap:9588;tap:10916;tap:11569;g:12023;tap:12576;tap:12952;tap:13561;tap:14244;d:14597;tap:15496;g:15878;d:16449;tap:17499;tap:18184;tap:18833;tap:19511;tap:20150;tap:20743;tap:22771;g:23117;tap:23721;tap:24056;d:24212;tap:24766;g:25239;d:26186;g:26506;d:26850;g:27193;d:27512;g:27854;tap:28400;tap:28735;b:28735;tap:30040;b:30040;tap:30671;tap:31021;tap:31364;tap:31704;tap:32038;b:32038;tap:32680;tap:33611;d:33765;g:34080;d:34422;tap:35998;b:35998;h:35998;b:35998;tap:38556;d:38975;tap:39861;b:39861;b:39861;b:39861;h:39861;tap:43209;d:43616;tap:44522;g:44931;tap:45777;d:46265;tap:47146;g:47599;tap:48464;d:48882;tap:49782;g:50159;tap:51096;b:51096;tap:52435;h:52435;tap:53713;b:53713;tap:55067;h:55067;tap:56394;d:56733;tap:57689;d:58156;tap:58991;g:59315;tap:60357;d:60666;b:60666;tap:62296;h:62296;tap:63615;g:63839;tap:64336;b:64336;tap:65650;h:65650;tap:66932;d:67227;tap:68204;b:68204;tap:69564;h:69564;tap:70879;b:70879;tap:72205;h:72205;tap:73436;b:73436;tap:74865;h:74865;tap:76161;b:76161;tap:77525;h:77525;tap:78738;b:78738;tap:80127;g:80535;d:81274;d:81875;tap:82796;b:82796;h:82796;tap:84757;b:84757;tap:85481;b:85481;h:85481;b:85481;tap:88058;h:88058;b:88058;tap:89947;h:89947;h:89947;b:89947;h:89947;tap:92681;b:92681;tap:93975;h:93975;b:93975;tap:95983;tap:96966;d:97378;g:98354;tap:99279;d:99731;tap:100568;g:100976;tap:101872;b:101872;b:101872;tap:103829;tap:104462;g:104910;h:104910;tap:106490;h:106490;tap:107830;d:108208;tap:109102;g:109547;tap:110438;d:110816;tap:111771;tap:112414;g:112834;tap:113669;h:113669;b:113669;h:113669;tap:116024;h:116024;b:116024;tap:117083;tap:117710;g:118112;d:118734;tap:119659;g:120059;tap:120985;d:121363;tap:122320;b:122320;b:122320;g:123967;g:124698;tap:125602;h:125602;tap:127568;d:127750;tap:128273;b:128273;h:128273;tap:129233;tap:129595;g:129696;d:130010;g:130345;d:130688;tap:131270;b:131270;h:131270;b:131270;h:131270;tap:132903;b:132903;h:132903;b:132903;tap:135554;h:135554;tap:136855;h:136855;tap:138147;d:138578;tap:139473;b:139473;tap:140767;g:141237;tap:142163;h:142163;tap:143464;d:143809;tap:144767;b:144767;tap:146041;g:146484;h:146484;d:147576;h:147576;d:148345;tap:149332;tap:150036;tap:150694;tap:151353;g:151793;d:152459;tap:153980;tap:154631;h:154631;tap:155935;h:155935;tap:157906;h:157906;tap:159206;h:159206;tap:160563;h:160563;tap:161882;h:161882;tap:163505;h:163505;tap:164568;h:164568;tap:165881;h:165881;tap:167164;h:167164;tap:168440;d:168821;g:169614;tap:170476;");
            sequences.add("tap:3995;tap:4301;tap:5206;tap:6007;tap:6749;tap:7528;tap:8273;tap:9034;d:9175;g:9554;tap:10181;tap:10918;tap:11656;tap:12380;tap:12795;d:12924;g:13287;d:13729;g:14018;tap:14679;tap:15037;tap:16175;b:16175;tap:18431;h:18431;tap:19159;h:19159;h:19159;b:19159;h:19159;b:19159;h:19159;b:19159;h:19159;tap:25158;tap:25516;tap:25942;tap:26286;tap:26697;tap:27061;tap:27435;b:27435;h:27435;b:27435;h:27435;b:27435;tap:31520;tap:31933;tap:32279;tap:32698;tap:33049;tap:33800;tap:34160;g:34678;d:35067;tap:36017;g:36505;tap:37166;d:37372;tap:38297;tap:38657;g:38814;d:39253;g:39597;b:39597;h:39597;tap:40881;b:40881;tap:41968;b:41968;tap:43190;h:43190;tap:43912;b:43912;tap:44636;tap:45052;b:45052;b:45052;h:45052;h:45052;g:48952;d:49688;g:50437;b:50437;h:50437;b:50437;tap:52478;tap:52900;tap:53250;tap:53681;tap:54025;d:54185;g:54874;d:55660;g:56412;tap:57420;b:57420;h:57420;b:57420;h:57420;b:57420;h:57420;b:57420;h:57420;b:57420;h:57420;b:57420;h:57420;b:57420;h:57420;g:63914;d:64604;tap:65640;tap:66040;b:66040;h:66040;tap:67512;d:67685;tap:68259;h:68259;tap:68974;h:68974;tap:69764;h:69764;tap:70494;g:70678;tap:71282;d:71504;tap:72106;h:72106;tap:72749;b:72749;tap:73505;d:73696;tap:74413;tap:74726;d:75503;tap:76128;h:76128;tap:76887;b:76887;tap:78359;b:78359;tap:79147;tap:79538;b:79538;tap:81408;h:81408;tap:82200;b:82200;tap:82982;h:82982;tap:83780;b:83780;tap:84689;g:84919;d:85261;g:85670;d:86006;g:86394;d:86770;tap:87373;tap:87772;tap:88101;tap:88533;tap:88893;tap:89326;tap:89668;tap:90027;tap:91152;g:91262;tap:91899;tap:92232;d:92419;g:93500;tap:94136;b:94136;tap:94942;tap:95320;h:95320;tap:96376;h:96376;tap:98289;h:98289;tap:99728;tap:100147;b:100147;tap:101255;tap:102083;tap:103138;tap:104639;tap:108341;tap:112058;tap:112876;g:113765;d:114153;g:114514;d:114887;tap:115524;tap:115878;d:116031;g:116411;d:116828;g:117163;d:117527;tap:118131;tap:118470;b:118470;h:118470;b:118470;h:118470;b:118470;tap:120767;h:120767;tap:122655;g:122818;d:123206;tap:123780;b:123780;h:123780;b:123780;h:123780;tap:125610;d:125771;g:126149;d:126522;h:126522;b:126522;tap:128257;h:128257;tap:129379;h:129379;b:129379;tap:130547;h:130547;tap:131238;b:131238;tap:132000;h:132000;tap:132694;b:132694;tap:133496;h:133496;tap:134244;b:134244;tap:135730;b:135730;tap:136517;tap:137258;tap:138010;tap:138445;d:139263;g:139983;d:140743;tap:141789;g:141924;tap:142528;d:142910;tap:143663;g:143808;tap:144785;tap:145184;tap:145546;b:145546;tap:147367;b:147367;tap:148091;h:148091;tap:148877;b:148877;tap:149653;h:149653;tap:150371;tap:150772;tap:151154;b:151154;h:151154;b:151154;h:151154;b:151154;tap:153373;g:153504;d:153847;tap:154509;tap:154894;tap:155238;g:155358;d:155737;tap:156399;tap:157138;tap:157949;tap:158705;tap:159074;tap:159457;tap:160146;g:160327;d:160715;tap:161639;tap:162401;g:162910;d:163585;tap:164655;tap:165060;tap:165807;tap:166178;tap:166555;tap:167675;tap:168085;tap:168443;tap:168788;tap:169102;");
            sequences.add("tap:1331;g:2798;d:3029;d:4726;g:4940;h:4940;b:4940;h:4940;b:4940;h:4940;g:8541;d:8754;g:8993;d:9191;g:9397;tap:10025;h:10025;b:10025;h:10025;b:10025;h:10025;b:10025;h:10025;b:10025;h:10025;g:13039;d:13237;g:13468;d:13681;tap:14753;tap:15204;tap:15619;tap:16470;tap:17731;tap:18194;tap:22075;d:22310;g:22699;d:23132;b:23132;h:23132;b:23132;tap:26570;d:28629;g:29060;tap:29750;tap:30200;tap:30606;d:30749;tap:31467;tap:31900;tap:32325;g:32504;tap:33165;tap:33591;tap:34031;d:34224;tap:34852;tap:35293;tap:35725;g:35897;tap:36615;tap:37084;tap:37491;b:37491;tap:38294;tap:38725;tap:39173;h:39173;tap:39940;tap:40412;tap:40860;d:41064;d:41489;tap:42115;h:42115;tap:42949;h:42949;tap:43813;b:43813;tap:44678;h:44678;tap:45544;d:45754;g:46194;tap:46885;tap:47746;d:47939;g:48373;tap:49000;h:49000;tap:49843;h:49843;b:49843;h:49843;tap:51546;b:51546;tap:52405;h:52405;tap:53285;h:53285;tap:54134;b:54134;h:54134;b:54134;tap:55839;h:55839;b:55839;h:55839;b:55839;h:55839;tap:58407;d:58630;tap:59273;g:59503;tap:60113;d:60367;tap:60994;tap:61451;d:61679;g:62038;tap:62710;b:62710;tap:63557;b:63557;tap:64431;h:64431;tap:65264;b:65264;tap:66089;h:66089;tap:66981;d:67233;tap:67861;g:68055;d:68469;g:68894;tap:69521;h:69521;tap:70469;b:70469;tap:71301;h:71301;tap:72163;b:72163;tap:72981;d:73585;g:74423;b:74423;h:74423;h:74423;b:74423;b:74423;tap:79835;d:80520;tap:81598;g:81884;d:82281;g:82666;tap:83271;tap:85052;tap:85928;tap:87604;tap:88902;tap:89310;g:91447;d:91670;g:91925;d:92131;g:92511;d:92717;g:92956;d:93170;tap:93635;tap:94050;h:94050;b:94050;tap:95223;tap:95717;d:95949;g:96374;d:96589;g:96820;d:97244;g:97449;d:97688;g:97911;d:98142;g:98348;d:98579;g:98785;d:99016;g:99222;d:99486;g:99708;d:99972;g:100227;d:100475;g:100714;d:101003;g:101507;d:101864;g:102256;d:102745;g:102988;d:103247;g:103437;d:103659;g:103890;d:104128;g:104351;d:104566;g:104805;d:105011;g:105209;d:105423;g:105671;d:105885;g:106124;d:106338;g:106611;d:106833;b:106833;h:106833;tap:111517;tap:112887;tap:113300;tap:114209;tap:115013;tap:115560;tap:116772;tap:117636;tap:118480;tap:118936;tap:120170;tap:120627;tap:121020;d:121228;tap:121864;tap:122332;g:122496;tap:123124;b:123124;b:123124;tap:124394;h:124394;tap:125288;h:125288;tap:126131;h:126131;tap:126987;h:126987;tap:127850;h:127850;tap:128696;b:128696;tap:129552;h:129552;tap:130394;tap:130881;b:130881;tap:131693;h:131693;tap:132546;b:132546;tap:133397;h:133397;tap:134291;g:134465;g:135372;tap:136529;tap:137263;b:137263;tap:139020;tap:139840;h:139840;tap:141579;b:141579;tap:143330;h:143330;tap:144982;g:145608;tap:146715;h:146715;tap:148398;b:148398;tap:150155;h:150155;tap:151839;b:151839;tap:153569;h:153569;tap:155296;b:155296;tap:157011;b:157011;tap:158724;tap:159581;h:159581;tap:161324;tap:161718;tap:162163;tap:163025;tap:163543;tap:164398;tap:165225;tap:165600;tap:167365;tap:169894;tap:170729;tap:171180;tap:171620;tap:172442;tap:173335;tap:174199;tap:175067;b:175067;h:175067;b:175067;h:175067;b:175067;h:175067;g:177802;d:178601;g:179050;d:179526;g:179952;d:180348;g:180808;d:181230;b:181230;h:181230;b:181230;g:182909;d:183348;g:183799;d:184200;b:184200;h:184200;b:184200;h:184200;tap:186114;h:186114;b:186114;h:186114;b:186114;h:186114;g:188132;d:188329;g:188543;b:188543;h:188543;tap:190468;");
            sequences.add("tap:1525;tap:2225;tap:2851;tap:3166;tap:3497;tap:3839;tap:4250;tap:4618;tap:5645;tap:6079;tap:7242;tap:7819;tap:8347;tap:10201;tap:10760;tap:12856;tap:13227;d:13661;g:14243;tap:14844;tap:15662;d:16572;d:17195;g:17635;tap:18262;tap:18665;tap:19766;tap:20492;tap:20932;tap:21281;tap:21650;tap:22821;d:23136;g:23608;d:24004;tap:24607;b:24607;h:24607;b:24607;h:24607;tap:27230;d:27461;g:27984;tap:28729;tap:29270;tap:29810;tap:30649;d:30947;g:31463;tap:32065;tap:32853;tap:33229;d:33369;tap:34228;g:34511;tap:35349;tap:35868;tap:36250;g:36400;tap:37261;tap:37801;d:38096;g:38583;d:38972;tap:39631;b:39631;h:39631;tap:41317;tap:42174;b:42174;tap:43184;tap:43742;h:43742;tap:44816;b:44816;tap:45596;h:45596;g:46491;d:46871;tap:47615;tap:48187;b:48187;tap:49339;tap:49766;tap:50125;tap:50553;tap:51286;tap:51704;tap:52254;tap:52751;g:53461;d:53691;g:54026;tap:54630;tap:55234;tap:55766;tap:56511;b:56511;tap:57268;tap:57675;h:57675;tap:58782;tap:59537;h:59537;b:59537;tap:60640;tap:61180;tap:61735;h:61735;b:61735;h:61735;tap:63628;g:63982;d:64500;g:64915;d:65161;g:65359;d:65614;g:66048;tap:66649;b:66649;h:66649;b:66649;h:66649;b:66649;tap:69299;tap:69700;d:70161;g:70902;d:71539;g:72386;tap:73164;tap:73754;tap:74499;tap:74920;d:75065;g:75406;b:75406;h:75406;tap:77465;tap:77886;tap:78236;b:78236;tap:79202;tap:79783;tap:80112;tap:80741;tap:81233;b:81233;h:81233;tap:82752;b:82752;h:82752;tap:84235;b:84235;h:84235;tap:85765;b:85765;tap:86878;tap:87236;h:87236;tap:88156;b:88156;tap:89370;tap:89926;tap:91218;tap:91748;h:91748;tap:92800;d:92989;g:93394;tap:94287;tap:94773;tap:95161;h:95161;b:95161;tap:96641;h:96641;b:96641;tap:98384;d:98564;g:98990;tap:99617;h:99617;b:99617;tap:101372;tap:101774;h:101774;b:101774;tap:104365;tap:104939;g:105392;d:105987;tap:106731;tap:107179;tap:107846;g:108380;tap:109219;tap:109828;g:110168;d:110577;g:110958;tap:111619;h:111619;tap:112794;tap:113216;tap:113954;tap:114326;tap:114640;b:114640;tap:115733;tap:116167;tap:116793;tap:117214;g:117368;d:117955;tap:118741;g:119192;d:119650;g:120010;tap:120623;tap:121322;tap:121755;h:121755;b:121755;h:121755;tap:123594;g:123873;d:124464;d:125106;g:125558;d:125954;tap:126583;tap:127174;tap:127768;tap:128125;tap:128900;tap:129267;tap:129663;b:129663;h:129663;tap:131177;b:131177;h:131177;tap:132190;g:132380;d:132979;tap:133735;tap:134406;tap:134902;tap:135238;g:135434;tap:136215;tap:136761;g:137105;d:137547;g:137965;tap:138637;tap:139300;tap:139823;tap:140381;tap:140871;g:141357;d:141876;tap:142736;tap:143167;tap:144491;g:145050;d:145514;tap:146322;g:146541;d:146958;tap:147648;g:147989;d:148479;tap:149113;h:149113;b:149113;h:149113;tap:150642;g:150972;tap:151732;h:151732;b:151732;tap:153588;h:153588;tap:154740;b:154740;tap:155890;h:155890;tap:156681;h:156681;b:156681;tap:158351;g:158617;d:159004;tap:159588;b:159588;h:159588;tap:161136;g:161466;d:161967;tap:162593;g:162971;tap:163722;d:164020;tap:164831;tap:165284;g:165412;tap:166174;tap:166762;d:166982;g:167565;d:167941;tap:168603;tap:169313;h:169313;tap:170800;h:170800;tap:172350;h:172350;tap:173719;h:173719;tap:175293;b:175293;tap:176826;b:176826;h:176826;tap:178885;g:179153;d:179529;g:179978;tap:180605;h:180605;tap:182086;tap:182507;tap:182844;tap:183283;tap:183619;g:184132;tap:184759;tap:185471;b:185471;tap:186251;tap:186652;b:186652;tap:187800;tap:188157;b:188157;tap:189291;tap:189625;b:189625;tap:190712;b:190712;tap:191488;b:191488;tap:192244;b:192244;tap:193022;h:193022;tap:193789;tap:194253;h:194253;tap:195226;h:195226;tap:196248;d:196677;tap:197555;h:197555;tap:198221;g:198426;d:198991;g:199572;tap:200126;tap:200843;h:200843;tap:201678;tap:202177;tap:202746;h:202746;g:203294;d:203665;g:204038;tap:204614;tap:205236;tap:205785;h:205785;b:205785;h:205785;tap:207632;d:207935;g:208508;tap:209135;tap:209517;h:209517;b:209517;tap:210599;tap:211203;tap:211779;h:211779;b:211779;h:211779;tap:213649;g:213945;d:214479;tap:215434;tap:215901;d:217102;g:217579;tap:218827;tap:219256;tap:219644;b:219644;h:219644;tap:221533;g:221719;d:222039;tap:222584;b:222584;tap:223736;tap:225233;g:225372;d:226023;g:227305;d:227715;g:228045;tap:228589;h:228589;h:228589;b:228589;tap:231622;h:231622;tap:232851;b:232851;tap:233877;h:233877;tap:234592;b:234592;tap:235735;h:235735;b:235735;tap:237594;d:238072;g:238576;d:239125;g:239468;d:239689;g:240036;tap:240583;b:240583;tap:241634;b:241634;h:241634;b:241634;h:241634;g:243406;d:243980;tap:244725;g:245161;tap:245850;d:245990;tap:246651;b:246651;h:246651;b:246651;h:246651;tap:249234;g:249396;d:249977;tap:250721;g:251024;tap:251832;tap:252254;d:252418;tap:253224;d:253478;tap:254402;g:254664;d:255073;tap:255676;b:255676;h:255676;tap:257116;tap:257855;g:258296;d:259037;tap:259730;b:259730;h:259730;b:259730;h:259730;tap:261594;g:262090;tap:263135;tap:263907;tap:264655;d:264982;tap:265738;b:265738;tap:266822;h:266822;tap:267619;b:267619;tap:268715;h:268715;tap:269831;b:269831;tap:270637;h:270637;tap:271750;b:271750;h:271750;b:271750;tap:273652;tap:274739;tap:275504;d:275634;g:276014;tap:276597;tap:277172;tap:277725;tap:278553;tap:278929;tap:279264;d:279422;g:279978;d:280488;tap:281580;tap:282237;g:282368;d:282985;g:283511;tap:284466;d:284676;g:285036;tap:285588;d:286020;g:286534;d:286860;tap:287911;g:288078;d:288421;g:288996;d:289529;g:291357;d:291905;g:292497;b:292497;b:292497;tap:294583;tap:295158;g:295511;tap:296433;tap:296908;b:296908;tap:297647;tap:298203;tap:298758;g:299335;d:299706;g:300070;tap:300646;tap:301201;tap:301759;b:301759;tap:304128;tap:306660;tap:309649;");
            sequences.add("tap:1506;tap:2171;tap:4277;tap:5018;tap:7301;tap:7913;tap:9563;tap:10308;tap:12910;tap:13316;d:13736;tap:14398;g:14803;d:15337;g:15705;tap:16250;tap:17051;h:17051;tap:18572;b:18572;tap:20070;h:20070;tap:21542;h:21542;tap:23033;g:23202;d:23570;g:23963;tap:24575;b:24575;b:24575;tap:26758;b:26758;tap:28314;b:28314;tap:29814;h:29814;tap:31305;g:31862;d:32553;g:33346;tap:34324;d:34872;d:35651;g:36406;tap:37300;tap:38095;tap:39126;tap:39566;tap:39900;d:40048;g:40639;tap:42025;tap:42539;tap:42911;tap:43269;b:43269;tap:44589;h:44589;tap:45512;d:45677;tap:46252;tap:47075;g:47535;tap:48545;tap:49323;b:49323;tap:51703;h:51703;tap:53058;b:53058;tap:54590;h:54590;tap:56060;d:56505;g:57293;tap:58272;tap:59082;b:59082;tap:60507;tap:61335;tap:62059;tap:62839;tap:63572;tap:63999;d:64459;g:64860;d:65187;tap:65812;tap:66561;d:66993;g:67774;d:68538;tap:69546;tap:69961;tap:70273;tap:70697;tap:71085;b:71085;h:71085;b:71085;tap:73337;g:73832;d:74167;tap:74770;tap:75542;tap:75942;tap:76329;tap:76694;tap:77079;tap:77423;tap:77820;tap:78195;tap:78560;tap:78933;tap:79304;tap:79626;tap:80017;tap:80361;d:80538;g:80954;tap:81528;tap:81960;tap:82271;tap:82714;tap:83055;g:83570;d:84312;tap:85291;tap:86067;tap:86461;tap:87172;tap:87560;tap:87965;tap:88344;tap:88673;tap:89028;h:89028;b:89028;tap:90144;tap:90569;tap:90880;tap:91307;tap:91641;tap:92060;b:92060;tap:93519;tap:93928;tap:94299;tap:94716;tap:95082;tap:95482;tap:95805;b:95805;h:95805;tap:96908;b:96908;tap:97660;tap:98061;b:98061;tap:98775;h:98775;tap:99519;g:99702;d:100843;g:101223;tap:101770;d:102350;g:103065;d:103567;g:104021;d:104219;g:104425;tap:104883;b:104883;h:104883;b:104883;h:104883;g:107483;d:108050;g:108543;d:108960;g:109682;tap:110317;tap:110861;tap:111351;g:112147;tap:112848;d:113029;tap:113580;g:113804;tap:115163;tap:116664;tap:117447;tap:118839;tap:120333;tap:121754;tap:123344;tap:125295;b:125295;h:125295;b:125295;h:125295;b:125295;h:125295;b:125295;h:125295;g:129546;d:129859;g:130247;d:130635;g:131007;d:131350;g:131751;d:132077;b:132077;b:132077;h:132077;b:132077;h:132077;b:132077;h:132077;g:135192;d:135854;tap:137060;tap:138557;tap:139283;tap:140054;tap:140868;tap:141564;tap:142328;tap:143063;tap:143821;tap:144549;b:144549;tap:146083;tap:146827;tap:147574;tap:149990;tap:150546;tap:151273;tap:151718;tap:152058;tap:152418;h:152418;tap:153523;h:153523;tap:155056;b:155056;tap:156550;h:156550;tap:158032;d:158492;tap:159532;g:159963;tap:161031;d:161448;tap:162516;b:162516;h:162516;b:162516;tap:164795;h:164795;tap:166266;g:166806;tap:167785;d:168210;tap:169250;g:169805;tap:170727;b:170727;tap:172292;h:172292;tap:173774;b:173774;tap:175243;tap:176069;h:176069;tap:177555;b:177555;tap:179081;d:179543;tap:180579;tap:181271;tap:182098;tap:182836;tap:183547;tap:184305;b:184305;h:184305;b:184305;tap:186546;h:186546;b:186546;h:186546;b:186546;tap:189567;h:189567;tap:191070;tap:192548;tap:194056;tap:194918;b:194918;tap:196341;g:196891;d:197194;g:197553;tap:198545;g:199082;d:199594;g:199873;d:200194;g:200545;tap:201558;b:201558;tap:203073;h:203073;tap:205289;b:205289;tap:206762;h:206762;tap:208281;b:208281;tap:209800;h:209800;tap:211254;b:211254;tap:212771;h:212771;tap:214299;b:214299;tap:215739;h:215739;tap:217275;b:217275;tap:218810;h:218810;tap:220215;b:220215;tap:221773;h:221773;tap:223287;b:223287;tap:224799;h:224799;tap:226285;b:226285;tap:227762;h:227762;tap:229306;g:229767;tap:230780;d:231206;tap:232243;g:232770;d:233454;tap:234448;tap:235249;tap:236053;b:236053;h:236053;tap:238205;b:238205;h:238205;b:238205;tap:241348;d:242117;g:243088;tap:244271;d:244764;g:245908;d:246113;g:246576;d:246775;g:246964;d:247170;b:247170;h:247170;b:247170;h:247170;b:247170;h:247170;g:249931;d:250143;g:250636;d:251311;g:251557;d:252165;b:252165;h:252165;b:252165;h:252165;b:252165;h:252165;b:252165;h:252165;b:252165;h:252165;g:263271;d:263518;g:265172;d:265378;g:265795;d:266074;g:266297;tap:266958;b:266958;h:266958;");
            sequences.add("tap:2433;tap:3033;tap:3456;tap:3968;tap:4401;tap:4784;tap:5285;tap:5743;tap:6118;tap:6704;tap:7091;tap:7607;tap:8112;tap:8609;tap:8956;tap:9437;tap:9781;tap:10206;tap:10638;tap:11117;tap:11541;tap:12435;tap:12854;tap:13386;tap:13852;tap:14322;tap:14715;tap:15165;tap:15532;d:15787;g:16270;tap:16815;tap:17338;tap:17746;tap:18146;tap:18512;tap:18953;tap:19324;tap:19858;tap:20188;g:20413;d:21334;g:21979;d:22789;tap:24938;tap:25467;tap:25931;tap:26390;tap:26811;tap:27218;g:27335;tap:27963;tap:28401;tap:28783;tap:29969;b:29969;tap:31324;tap:31747;h:31747;tap:32582;tap:32980;tap:33422;h:33422;b:33422;h:33422;tap:35008;tap:35518;tap:35935;tap:36350;b:36350;h:36350;tap:37447;tap:37919;tap:38267;g:38396;d:38791;g:39171;tap:39746;b:39746;h:39746;tap:41018;b:41018;h:41018;tap:42178;g:42361;d:42729;tap:43334;b:43334;h:43334;tap:44549;b:44549;h:44549;tap:45771;tap:46250;tap:47458;tap:47883;h:47883;b:47883;tap:49174;h:49174;tap:50396;tap:50979;tap:51361;g:51582;d:51912;g:52337;d:52717;tap:53319;b:53319;h:53319;tap:54569;b:54569;h:54569;tap:55920;b:55920;tap:56759;tap:57137;h:57137;tap:57934;tap:58359;tap:58774;tap:59206;tap:59564;tap:59965;h:59965;tap:60755;h:60755;tap:61596;tap:61954;b:61954;tap:62739;tap:63202;tap:63571;tap:64073;g:64256;d:64636;tap:65279;tap:65753;tap:66132;tap:66747;tap:67127;tap:67508;h:67508;tap:68266;b:68266;tap:69037;tap:69462;tap:69894;tap:70303;h:70303;tap:71060;tap:71422;tap:71965;tap:72443;tap:72998;tap:74648;tap:75814;tap:76300;tap:76643;tap:76972;tap:77357;tap:77792;h:77792;tap:78583;b:78583;tap:79403;h:79403;tap:80224;d:80398;g:80838;d:81239;tap:81852;tap:82321;tap:82716;b:82716;h:82716;b:82716;tap:84337;tap:84801;h:84801;d:85342;g:85777;d:86145;tap:86756;tap:87174;b:87174;h:87174;tap:88331;tap:88790;tap:89178;tap:89605;g:89748;d:90079;g:90508;tap:91137;tap:91561;tap:91996;g:92465;d:92946;g:93379;tap:94043;tap:94484;tap:94877;tap:95312;tap:96533;tap:97275;tap:97661;b:97661;h:97661;tap:98883;b:98883;h:98883;tap:99986;tap:100367;tap:100742;tap:101170;tap:101556;b:101556;tap:102694;tap:103078;tap:103458;tap:103899;tap:104316;h:104316;tap:105112;d:105700;tap:106563;tap:106944;tap:107557;tap:107860;g:108383;tap:109028;tap:109424;b:109424;h:109424;tap:111050;tap:111638;b:111638;tap:112878;tap:113310;h:113310;tap:114071;b:114071;tap:115271;tap:115638;h:115638;tap:116469;b:116469;tap:117635;h:117635;tap:118365;b:118365;tap:119161;tap:119481;h:119481;tap:120356;b:120356;tap:121212;h:121212;tap:122062;b:122062;tap:122954;tap:123378;tap:123986;g:124284;d:124716;tap:125808;g:125932;tap:126675;tap:127079;d:127215;tap:127841;tap:128192;g:128365;tap:129053;b:129053;tap:129936;tap:130395;h:130395;tap:131409;tap:131828;h:131828;tap:132608;tap:133017;tap:133412;tap:133835;tap:134149;g:134305;tap:134932;b:134932;tap:135725;tap:136166;h:136166;tap:136926;tap:137310;g:137483;tap:138056;tap:138495;d:138661;tap:139235;tap:139607;b:139607;tap:140410;h:140410;tap:141235;tap:141621;b:141621;tap:142437;tap:142803;h:142803;tap:143646;b:143646;b:143646;tap:144904;d:145127;g:145481;d:146417;g:146940;d:147918;g:149103;d:150497;tap:152143;tap:152617;tap:153188;tap:153696;tap:154061;tap:154494;tap:154935;tap:156118;tap:156454;d:156598;g:157008;tap:157670;tap:158071;tap:158482;g:159488;tap:160582;tap:161026;tap:161410;d:161537;tap:162162;tap:162586;tap:162898;tap:163340;tap:163692;d:163861;g:164617;tap:165311;tap:165984;tap:166400;tap:166784;tap:167228;tap:167670;tap:168329;tap:168904;tap:169302;tap:169733;tap:170282;tap:170936;tap:171393;tap:172754;tap:173257;tap:173769;tap:174403;tap:174740;tap:175437;tap:176025;tap:176369;tap:176797;tap:177132;tap:177558;tap:177971;tap:178888;d:179005;g:180045;d:181331;tap:182879;tap:184232;tap:185791;tap:187046;b:187046;tap:188891;h:188891;tap:191337;g:192323;tap:193744;b:193744;tap:194961;tap:195324;tap:195751;tap:196086;tap:196555;tap:196898;tap:197279;tap:197646;tap:198160;h:198160;tap:198899;tap:199234;d:199421;g:199776;d:200119;tap:200733;tap:201740;tap:202307;tap:202692;b:202692;tap:203424;tap:203743;h:203743;tap:204491;tap:204931;tap:205333;b:205333;tap:206533;tap:206981;tap:207423;tap:207844;tap:208356;h:208356;tap:209111;tap:209515;b:209515;tap:210273;tap:210676;tap:211127;h:211127;tap:211919;tap:212316;b:212316;tap:213030;h:213030;tap:213806;tap:214919;tap:216120;b:216120;tap:217244;h:217244;tap:217993;tap:219217;b:219217;tap:219945;h:219945;tap:220753;tap:221074;d:221258;g:221646;d:221998;tap:222660;tap:223048;tap:223474;b:223474;tap:224223;tap:224582;tap:224933;h:224933;tap:225593;tap:226616;tap:226944;tap:227295;tap:227637;tap:227941;tap:228881;tap:229379;tap:229762;tap:230081;b:230081;h:230081;tap:231845;tap:232262;b:232262;tap:233346;tap:233702;tap:234044;h:234044;tap:234731;tap:235050;b:235050;tap:235779;h:235779;tap:236554;tap:236979;g:237106;d:237515;tap:238412;d:238671;g:239245;d:240315;tap:241674;tap:242089;tap:242463;tap:242945;h:242945;tap:244259;b:244259;tap:245041;tap:245345;h:245345;b:245345;tap:246910;h:246910;b:246910;h:246910;tap:249092;b:249092;tap:249933;b:249933;tap:250633;tap:251030;b:251030;tap:251921;tap:252331;tap:252871;h:252871;tap:253994;b:253994;tap:254827;h:254827;tap:255603;b:255603;tap:256378;g:256488;d:256909;g:257273;d:257722;b:257722;h:257722;tap:259215;tap:260258;tap:260946;tap:261775;tap:262797;tap:263980;tap:264635;tap:265040;tap:265428;tap:265953;tap:268036;tap:268502;tap:268936;tap:269256;tap:269585;tap:269940;tap:270299;tap:270654;g:272219;d:272561;tap:273077;tap:274800;tap:275121;tap:275478;tap:275935;tap:276589;tap:277210;tap:277546;tap:278298;tap:278699;tap:279336;tap:279810;tap:280190;tap:280821;tap:281553;tap:282267;tap:283407;d:283684;g:284032;d:284337;tap:284913;tap:285294;tap:285620;b:285620;tap:286778;tap:287098;h:287098;tap:287892;b:287892;tap:288672;tap:289046;tap:289409;b:289409;h:289409;b:289409;tap:290966;tap:291645;tap:292049;g:292835;d:293156;tap:293730;tap:294148;b:294148;h:294148;h:294148;tap:296874;tap:297225;g:297335;d:297695;tap:298321;tap:298682;h:298682;tap:299727;b:299727;tap:301048;h:301048;tap:302034;tap:302394;tap:302736;tap:303074;tap:303489;h:303489;b:303489;h:303489;tap:304996;g:305174;d:305496;g:305880;d:306168;g:306465;tap:307019;tap:307386;tap:307787;g:307929;d:308257;g:308665;d:309025;b:309025;h:309025;b:309025;h:309025;b:309025;g:311622;d:312031;g:312403;d:312700;g:313060;g:313826;tap:314437;h:314437;b:314437;h:314437;b:314437;h:314437;tap:316603;tap:317005;h:317005;tap:318037;tap:318437;h:318437;tap:319219;tap:319618;h:319618;b:319618;h:319618;tap:321112;tap:321529;tap:321930;b:321930;h:321930;b:321930;h:321930;g:323787;d:324130;g:324540;tap:325427;tap:325769;g:326015;d:326321;g:326827;b:326827;h:326827;tap:328481;tap:328829;tap:329247;tap:329598;tap:330047;tap:330351;tap:330748;tap:331147;tap:331518;tap:331902;tap:332325;tap:332630;tap:333003;tap:333340;tap:333742;tap:334054;tap:334495;tap:334855;tap:335290;tap:335796;tap:336211;tap:336570;tap:336933;tap:337662;tap:338075;tap:338464;tap:339238;tap:339661;tap:340057;tap:340392;tap:341550;tap:342318;tap:342743;tap:343824;tap:344975;tap:345710;tap:346150;tap:347180;tap:348047;tap:348350;tap:349488;tap:350602;tap:351390;d:351499;");
            sequences.add("tap:8893;tap:9366;tap:10079;tap:10645;tap:11110;d:11410;tap:12099;g:12365;tap:13085;tap:13679;tap:14155;tap:14581;d:14884;tap:15604;tap:16120;tap:16645;tap:17051;g:17282;tap:18060;tap:18583;tap:19095;d:19365;tap:20113;b:20113;h:20113;tap:21582;tap:22097;g:22368;tap:23089;b:23089;tap:24079;h:24079;tap:25111;g:25324;d:25855;tap:26576;b:26576;tap:27599;h:27599;tap:28613;g:28898;d:29432;tap:30151;h:30151;tap:31111;h:31111;b:31111;h:31111;tap:33075;b:33075;tap:34078;h:34078;tap:35055;b:35055;tap:36069;h:36069;tap:37101;g:37343;tap:38064;d:38301;tap:39110;g:39329;tap:40075;d:40326;tap:41070;tap:41732;g:41939;tap:42629;tap:43686;tap:44636;tap:45169;tap:45625;tap:46151;g:46404;d:46869;tap:47613;tap:48139;g:48334;tap:49077;tap:49641;g:49955;d:50411;tap:51131;h:51131;tap:52115;h:52115;tap:53063;d:53344;tap:54074;g:54321;tap:55043;b:55043;tap:56015;h:56015;tap:57082;g:57376;tap:58107;b:58107;h:58107;tap:59561;d:59796;g:60326;tap:60998;h:60998;tap:62075;h:62075;tap:63069;b:63069;tap:64051;h:64051;tap:65074;d:65345;g:65895;tap:66558;d:66781;g:67362;tap:68575;d:68863;tap:69584;g:69839;tap:70594;d:70823;tap:71603;g:71864;tap:72609;d:72866;tap:73648;b:73648;tap:74577;h:74577;b:74577;tap:76079;d:76419;tap:77147;h:77147;tap:78093;b:78093;tap:79004;h:79004;tap:80400;b:80400;h:80400;tap:81979;b:81979;h:81979;tap:83532;tap:84004;tap:84543;d:84798;g:85297;tap:86945;tap:87517;tap:88083;tap:89128;tap:89576;tap:90110;tap:91096;tap:92144;tap:93133;b:93133;h:93133;tap:94572;g:94808;d:95289;tap:96098;g:96350;d:96883;g:97366;tap:98110;tap:98639;tap:99207;tap:99648;tap:100163;tap:100663;tap:101152;tap:101686;tap:102146;tap:102656;tap:103143;tap:103677;g:103915;d:104365;g:104939;tap:105628;h:105628;tap:106614;b:106614;tap:107614;h:107614;tap:108645;b:108645;tap:109551;b:109551;h:109551;tap:111126;g:111377;tap:112125;h:112125;tap:113098;g:113334;tap:114054;d:114369;tap:115087;b:115087;b:115087;b:115087;h:115087;tap:117615;h:117615;tap:118580;h:118580;h:118580;tap:120092;g:120399;tap:121120;tap:121646;tap:122166;tap:122663;tap:123149;d:123420;tap:124140;b:124140;tap:125112;tap:125621;h:125621;tap:126615;tap:127117;b:127117;tap:128528;h:128528;b:128528;tap:130049;h:130049;tap:131031;b:131031;tap:132053;h:132053;tap:133091;h:133091;tap:134100;b:134100;tap:135117;h:135117;tap:136189;b:136189;tap:137149;h:137149;tap:138110;b:138110;tap:139142;h:139142;tap:140829;b:140829;tap:143530;h:143530;tap:146627;d:147708;tap:149354;g:150561;tap:152581;b:152581;h:152581;tap:154094;b:154094;tap:155176;h:155176;tap:156110;b:156110;tap:157074;h:157074;tap:158054;d:158336;tap:159112;g:159406;tap:160123;b:160123;h:160123;tap:161635;d:161900;tap:162618;b:162618;tap:163615;h:163615;tap:164539;b:164539;tap:165555;h:165555;tap:166618;b:166618;tap:167551;d:167799;g:168372;tap:169118;d:169348;tap:170095;g:170405;tap:171136;h:171136;tap:172097;h:172097;tap:173096;g:173383;tap:174019;b:174019;b:174019;tap:176124;b:176124;tap:177104;b:177104;tap:178098;h:178098;tap:179119;b:179119;tap:180093;h:180093;tap:181095;d:181371;tap:182092;b:182092;tap:183149;h:183149;tap:184091;h:184091;tap:185095;b:185095;tap:186108;h:186108;tap:187029;b:187029;tap:188009;h:188009;d:188798;g:189315;tap:189985;b:189985;h:189985;tap:191554;g:191874;tap:192564;b:192564;tap:193501;tap:193983;g:194265;tap:195034;tap:195530;g:195802;tap:196531;d:196772;tap:197523;b:197523;tap:198556;h:198556;tap:199577;tap:200080;tap:200538;b:200538;h:200538;b:200538;tap:202563;tap:203108;tap:203619;tap:204134;g:204390;tap:205090;tap:205596;d:205835;tap:206534;tap:207093;tap:207570;g:207877;tap:208623;tap:209105;tap:209600;tap:210051;tap:210608;tap:211097;tap:212046;tap:213131;tap:214141;tap:215111;d:215349;");
            sequences.add("tap:1056;tap:1404;tap:1742;tap:2605;tap:3078;tap:4870;tap:6238;tap:6665;tap:7026;g:7266;tap:7985;d:8173;d:9986;b:9986;h:9986;b:9986;h:9986;g:11701;d:12057;g:12353;d:12650;tap:13275;tap:13727;tap:14210;b:14210;tap:15949;tap:16834;b:16834;h:16834;tap:18642;b:18642;h:18642;b:18642;tap:20441;tap:20841;g:20964;d:21243;tap:22197;tap:22603;tap:23625;h:23625;tap:25715;h:25715;tap:26542;tap:27001;tap:27458;h:27458;tap:28316;b:28316;g:29831;tap:31102;tap:31498;tap:32860;tap:33293;g:33440;d:33677;g:33996;d:34201;g:34416;d:34833;g:35200;d:35480;g:35726;tap:36417;d:36607;tap:37297;g:37487;tap:38229;d:38389;tap:39079;g:39249;tap:39991;d:40155;tap:40847;g:41034;tap:41755;d:41926;tap:42646;b:42646;tap:43488;h:43488;tap:44404;h:44404;tap:45247;h:45247;tap:46145;h:46145;tap:47064;h:47064;tap:47969;h:47969;tap:48816;b:48816;tap:50054;tap:50652;tap:51076;tap:51569;tap:52003;tap:52436;tap:52876;d:53071;g:53503;d:53921;g:54353;d:54837;tap:55499;h:55499;b:55499;h:55499;tap:57276;g:57446;tap:58135;b:58135;tap:59032;d:59269;tap:59930;b:59930;tap:60822;h:60822;g:61523;tap:62183;b:62183;tap:63090;h:63090;tap:63918;d:64164;tap:64855;b:64855;tap:65701;h:65701;tap:66608;tap:67075;tap:67992;tap:68407;tap:68887;tap:70196;tap:70628;tap:72007;tap:72386;b:72386;h:72386;b:72386;h:72386;g:74370;g:74804;d:75285;g:75695;tap:76357;tap:76831;g:77009;b:77009;h:77009;b:77009;h:77009;b:77009;h:77009;b:77009;tap:80817;tap:81316;tap:81766;tap:82208;g:82402;d:82803;g:83261;d:83644;g:84168;tap:84794;tap:85271;tap:85735;tap:86185;tap:87929;d:88170;g:88586;d:88816;g:89439;b:89439;tap:91547;tap:91966;tap:92877;tap:93278;tap:93742;tap:95518;tap:95913;h:95913;b:95913;h:95913;b:95913;h:95913;b:95913;h:95913;b:95913;h:95913;tap:100363;d:100591;tap:101283;g:101511;tap:102233;g:102436;tap:103060;d:103272;d:103739;tap:104365;b:104365;h:104365;tap:105702;b:105702;h:105702;tap:107065;b:107065;tap:107972;h:107972;tap:108815;b:108815;tap:109746;h:109746;tap:110631;b:110631;tap:111508;h:111508;tap:112433;b:112433;tap:113271;h:113271;tap:114194;b:114194;tap:115059;h:115059;tap:115959;d:116188;tap:116846;tap:117329;tap:117775;tap:118200;d:118405;tap:119125;d:119330;tap:119959;d:120180;tap:120869;d:121107;tap:121733;d:121980;tap:122604;d:122876;tap:123537;g:123727;tap:124388;b:124388;h:124388;b:124388;tap:126200;tap:126596;h:126596;b:126596;tap:127912;tap:128364;h:128364;tap:129275;b:129275;tap:130187;h:130187;tap:131040;h:131040;tap:131896;b:131896;tap:132777;h:132777;g:133456;d:133913;g:134347;tap:134992;tap:135466;g:135712;tap:136400;d:136593;tap:137285;b:137285;tap:138182;h:138182;tap:139035;b:139035;tap:139957;h:139957;b:139957;tap:141255;h:141255;h:141255;h:141255;b:141255;b:141255;b:141255;g:144187;g:144628;g:145070;d:145459;d:145925;d:146351;h:146351;h:146351;h:146351;b:146351;tap:148793;g:149029;tap:149729;d:149961;tap:150631;b:150631;tap:151487;h:151487;tap:152362;tap:152903;tap:153372;tap:153766;tap:154211;tap:154654;tap:155071;h:155071;tap:155961;g:156181;tap:156826;d:157047;tap:157720;h:157720;tap:158564;h:158564;tap:159492;h:159492;tap:160339;b:160339;tap:161248;g:161478;");
            sequences.add("tap:989;tap:1308;tap:1663;tap:2219;tap:3158;d:7374;g:7688;tap:8233;g:10194;d:10532;g:10854;d:11151;g:11480;d:11793;g:12082;d:12362;g:12651;tap:13234;b:13234;h:13234;tap:16102;tap:16407;h:16407;b:16407;tap:18263;tap:18918;tap:19417;tap:19819;tap:20164;b:20164;tap:20867;tap:21410;tap:21816;h:21816;tap:22718;tap:23052;tap:23357;h:23357;b:23357;h:23357;b:23357;h:23357;tap:25860;g:26595;d:26883;g:27291;tap:28292;h:28292;tap:30566;tap:30888;h:30888;tap:32184;h:32184;tap:33476;g:33745;d:34461;b:34461;tap:36608;b:36608;tap:38458;h:38458;tap:39776;b:39776;h:39776;tap:41643;tap:42020;tap:42603;tap:42925;b:42925;h:42925;tap:45789;tap:46131;g:46851;d:47157;tap:47888;g:48355;d:48698;g:49008;b:49008;b:49008;h:49008;b:49008;h:49008;tap:51444;g:52548;d:52818;g:53107;tap:53651;tap:58665;h:58665;tap:59951;h:59951;tap:61217;h:61217;tap:62489;h:62489;tap:63748;b:63748;h:63748;b:63748;h:63748;b:63748;h:63748;g:66074;tap:66908;tap:67331;tap:68304;tap:68852;tap:69408;tap:69858;tap:70387;tap:70707;tap:71012;tap:71371;d:71710;tap:72567;tap:73248;tap:73874;tap:74320;g:75200;d:75472;g:75694;d:75917;g:76213;tap:77020;tap:77668;b:77668;tap:78872;tap:79559;tap:80197;b:80197;h:80197;tap:82696;tap:83910;tap:84567;tap:84941;tap:85275;tap:85601;b:85601;h:85601;b:85601;h:85601;b:85601;b:85601;h:85601;b:85601;tap:89684;tap:90311;h:90311;b:90311;tap:91559;g:91903;d:92234;g:92560;tap:94080;b:94080;h:94080;b:94080;h:94080;tap:96247;tap:96590;tap:97250;tap:97868;g:98183;d:98530;tap:99454;tap:100071;tap:100687;tap:101003;g:101402;d:101770;g:102057;tap:102921;tap:103403;tap:103837;tap:104795;g:105222;d:105528;d:105880;g:106159;d:106447;tap:107313;tap:107676;tap:108875;g:109065;d:109387;g:109658;tap:110496;tap:111049;d:111175;g:111576;b:111576;h:111576;tap:113363;tap:113674;tap:113993;tap:114892;g:115341;d:115671;g:115976;tap:116521;tap:116850;b:116850;tap:118119;h:118119;tap:119075;h:119075;b:119075;h:119075;b:119075;h:119075;b:119075;h:119075;b:119075;tap:125988;tap:126628;tap:126990;tap:127572;d:128295;g:128645;d:128942;tap:129440;g:130215;d:130560;g:130882;d:131154;g:132370;d:132716;tap:133232;tap:133545;tap:133850;g:134002;d:134274;g:134592;tap:135437;tap:136966;tap:137456;tap:137984;tap:140564;d:140820;g:141107;d:141346;g:141585;b:141585;tap:142440;tap:145121;g:146581;d:146775;tap:147485;tap:147801;tap:148105;b:148105;h:148105;tap:150515;b:150515;h:150515;b:150515;h:150515;tap:152174;d:152310;g:152589;tap:153133;tap:153460;tap:154012;tap:154982;tap:155951;b:155951;h:155951;b:155951;tap:157210;tap:157776;tap:158146;h:158146;b:158146;h:158146;b:158146;h:158146;tap:160635;g:161062;d:161285;g:161515;d:161721;b:161721;h:161721;b:161721;tap:163520;tap:164027;b:164027;h:164027;b:164027;h:164027;tap:167307;tap:167831;b:167831;h:167831;tap:169887;h:169887;b:169887;h:169887;tap:173000;tap:173688;b:173688;tap:174846;h:174846;tap:176191;b:176191;b:176191;tap:177414;h:177414;h:177414;tap:178695;h:178695;b:178695;tap:179973;h:179973;tap:180959;h:180959;b:180959;h:180959;tap:182120;tap:182425;tap:183137;tap:183897;tap:184506;tap:185109;tap:185648;g:185961;d:186341;g:186654;d:186934;tap:187537;h:187537;tap:188831;tap:189423;tap:190018;tap:190762;d:190875;g:191186;d:191501;tap:192614;h:192614;b:192614;tap:193873;g:194255;tap:195208;d:195542;tap:196417;tap:196726;tap:197039;tap:197649;tap:198292;tap:198687;tap:199908;tap:200220;tap:200817;tap:201296;tap:201772;tap:202084;tap:202414;tap:202757;tap:203358;g:204336;d:204700;tap:205246;tap:205866;g:206303;d:206609;g:206914;d:207194;tap:207769;tap:208111;tap:208843;tap:209659;tap:209995;tap:210363;tap:210963;tap:211433;tap:212893;tap:213469;tap:213791;g:214846;b:214846;b:214846;h:214846;h:214846;h:214846;tap:221423;h:221423;tap:222948;h:222948;tap:223885;tap:224204;h:224204;tap:225507;g:225850;d:226185;g:226499;b:226499;b:226499;h:226499;tap:230354;b:230354;tap:231191;tap:231749;g:232173;d:232453;tap:233000;h:233000;tap:233646;b:233646;h:233646;tap:235542;tap:236147;h:236147;tap:236782;tap:237175;h:237175;tap:238028;b:238028;h:238028;tap:239301;tap:239928;tap:240231;tap:240608;tap:241197;h:241197;b:241197;h:241197;tap:243126;tap:243475;g:243584;d:243922;g:244244;b:244244;h:244244;tap:246614;b:246614;h:246614;tap:249184;h:249184;h:249184;b:249184;g:252399;d:252662;tap:253265;tap:253859;g:254027;d:254315;g:255264;d:255560;g:255864;tap:256409;tap:256737;tap:257078;h:257078;b:257078;h:257078;tap:258298;b:258298;h:258298;b:258298;g:260315;d:260603;g:260953;d:261226;tap:261771;b:261771;tap:263355;tap:263976;h:263976;b:263976;tap:265136;tap:266187;tap:266520;b:266520;h:266520;tap:268110;tap:268437;b:268437;h:268437;b:268437;h:268437;b:268437;tap:270937;d:271566;g:271885;d:272100;tap:272532;");

            if(position<sequences.size()) {
                sequenceARealiser = traiterSequences( sequences.get(position));
                couples = sequenceARealiser.split(";");
                int nbTotalMvts = couples.length;
                TextView nbTotal = swipeView.findViewById(R.id.nbTotal);
                nbTotal.setText(" / " + String.valueOf(2*nbTotalMvts));

                TextView scoreOr=swipeView.findViewById(R.id.scoreOr);
                TextView scoreAr=swipeView.findViewById(R.id.scoreArgent);
                TextView scoreBr=swipeView.findViewById(R.id.scoreBronze);

                int sOr=Math.round( (90*2*nbTotalMvts/100));
                int sAr=Math.round( (80*2*nbTotalMvts/100));
                int sBr=Math.round ( (70*2*nbTotalMvts/100));
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
            int i=1;
            String dernierCouple="";
            dernierCouple=couples[0];
            while (i<couples.length-1){


                if(!dernierCouple.split(":")[1].equals(couples[i].split(":")[1])){
                    couplesTraites.add(couples[i]);


                }
                dernierCouple=couples[i];
                i++;

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
