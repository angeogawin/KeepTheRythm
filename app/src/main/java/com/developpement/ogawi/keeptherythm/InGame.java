package com.developpement.ogawi.keeptherythm;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.zip.ZipOutputStream;

import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;


import com.appolica.flubber.Flubber;

import com.developpement.ogawi.keeptherythm.bdd.ScoreDAO;


import com.github.lzyzsd.circleprogress.DonutProgress;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plattysoft.leonids.ParticleSystem;
import com.skyfishjy.library.RippleBackground;

import static android.view.View.OVER_SCROLL_NEVER;
import static java.util.logging.Logger.global;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
public class InGame extends AppCompatActivity {//  implements OnGesturePerformedListener{
    static final int NUM_ITEMS = 3000;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    Intent i;
    static final int READ_BLOCK_SIZE = 100;
    private Toolbar mTopToolbar;
    static ArrayList<String> sequence;
    static int indiceActuelSequence;
    String dernierSymbole;
    ArrayList<Integer> listeTminToClick;//temps minimum pour chaque animation que le joueur doit attendre avant de cliquer , pour un bon timing

    static StringModified derniereAction;//derniere action effectuée par joueur tap,cr,cl...
    long timeOfDerniereAction;
    int score;

    //View v;
    GestureOverlayView vOverlay;

    private GestureDetector mDetector;
    TextView scoreT;
    Chronometer mChronometer;
    Boolean elementActuelClickable;
    MediaPlayer mPlayer;

    int indicesequence;
    int widthZoneJeu;
    int heightZoneJeu;
    RelativeLayout zoneJeu;
    Button go;
    int heightP;
    int widthP;
    ImageView mImageView;
    ArrayList<String> listeImagesAnimationsActives;
    // ImageView rondCentral;

    String[] couples;
 //   RoundCornerProgressBar progress;
    ProgressBar progress;
    ArrayList<String> nom_txt;
    ArrayList<String> listeUrl;
    String extensionFichier;
    int pos;//commence à 1

    int media_length;
    boolean animationEstLancee;
    String sequenceARealiser;
    ArrayList<String> sequences;

    ImageView nextTrophy;
    View barregauche;
    View barreHorizontal1;
    View barreHorizontal2;
    View barreHorizontal3;
    View barreHorizontal4;
    TextView nextPalierScore;
    ArrayList<Integer> listeIndicesActuels;
    ArrayList<Boolean> listePerfect;


    ProgressDialog mProgressDialog;
    TextView bestScore;
    TextView textBestScore;

    ScoreDAO scoreDAO;
    TextView textPerfect;
    Animation animBounce;
    Toast toast;
    Boolean actualiserTrophy;
    RippleBackground rippleBackground;
    int barreVie;

    private StorageReference mStorageRef;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    RelativeLayout carre;
    Runnable stopperPartie_vieTerminee;
    Handler handler_stopperPartie_vieTerminee;
    Boolean aQuitteJeu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barreVie=10;
        aQuitteJeu=false;
        animBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyAnimatorListener listener1=new MyAnimatorListener();
        animBounce.setAnimationListener(listener1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        carre=findViewById(R.id.carreCentre);

       // imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        //viewPager = (ViewPager) findViewById(R.id.pager);
      //  viewPager.setAdapter(imageFragmentPagerAdapter);

      //  DepthTransformation depthTransformation = new DepthTransformation();
       // viewPager.setPageTransformer(true, depthTransformation);

        i = getIntent();
        pos = i.getExtras().getInt("niveau");
        zoneJeu = findViewById(R.id.zonejeu);
        listeImagesAnimationsActives = new ArrayList<>();
        listeIndicesActuels=new ArrayList<>();
        listePerfect=new ArrayList<>();
        vOverlay = (GestureOverlayView) findViewById(R.id.gOverlay);
        // rondCentral=(ImageView) findViewById(R.id.rondCentral);
        //progress = findViewById(R.id.progress);
        progress=findViewById(R.id.progress1);
        progress.setMax(100);

        progress.setProgress(barreVie*100/10);

        nextTrophy=findViewById(R.id.nextTrophy);
        toast = new Toast(InGame.this);
        nextPalierScore=findViewById(R.id.nextPalierScore);
        textPerfect=findViewById(R.id.textPerfect);
        textPerfect.setTextSize(18);
        bestScore=findViewById(R.id.bestScoreIngame);
        textBestScore=findViewById(R.id.textBestScore);
        actualiserTrophy=false;

        bestScore.setVisibility(View.INVISIBLE);
        textBestScore.setVisibility(View.INVISIBLE);
        scoreDAO=new ScoreDAO(getApplicationContext());
        if(!scoreDAO.obtenirScoreNiveau(String.valueOf(pos)).equals("")){
            bestScore.setText(scoreDAO.obtenirScoreNiveau(String.valueOf(pos)));
            bestScore.setVisibility(View.VISIBLE);
            textBestScore.setVisibility(View.VISIBLE);

        }

       rippleBackground=(RippleBackground)findViewById(R.id.content);
        animationEstLancee = false;


        sequences = new ArrayList<>();
        sequences.add("tap:1250;tap:1691;tap:2188;tap:2523;tap:3049;tap:3880;tap:4449;tap:5040;tap:5531;tap:6145;tap:6585;tap:7081;tap:7590;tap:8051;g:8320;tap:9063;d:9330;tap:10109;g:10366;tap:11087;d:11401;tap:12091;g:12408;tap:13150;d:13420;tap:14165;g:14427;tap:15170;d:15469;tap:16189;g:16479;tap:17199;d:17477;tap:18168;h:18488;tap:19232;h:19601;tap:20200;tap:21285;tap:22290;tap:23853;tap:24811;g:25230;tap:25833;g:26261;tap:26888;tap:27905;d:28263;tap:28866;tap:29888;tap:30977;tap:31995;tap:32927;d:36838;tap:37582;tap:39120;tap:40072;tap:41067;tap:42074;h:42433;tap:43150;d:43397;g:43949;d:44500;tap:45191;tap:49764;tap:50775;tap:51781;tap:52769;tap:54298;tap:55324;tap:56353;tap:57388;d:57745;tap:58348;tap:59423;d:59702;g:60343;d:60857;tap:61432;b:61738;h:62215;tap:62955;tap:63475;tap:64023;tap:64533;d:64713;g:64959;d:65158;tap:65615;tap:66066;tap:66550;tap:67106;tap:67605;d:67792;g:68116;d:68507;g:68739;d:68963;g:69128;tap:69704;tap:70149;b:70407;tap:71117;b:71459;tap:72143;tap:73172;tap:73675;h:73959;tap:74700;tap:75208;d:75574;d:76614;h:77539;tap:78227;tap:79229;tap:81221;tap:81762;h:82087;tap:82829;tap:83873;g:84156;tap:84899;d:85156;tap:85875;h:86116;tap:86890;g:87164;tap:87884;d:88258;tap:88900;g:89588;tap:90424;tap:90924;g:91814;b:92247;h:92762;b:93063;h:93268;b:93512;tap:93999;d:94249;g:94803;d:95084;g:95331;tap:96055;h:96328;b:96812;tap:97544;h:97858;h:99382;tap:100098;g:100465;d:100959;tap:101651;g:102001;tap:102664;d:102931;tap:104194;d:104392;g:104995;d:105418;tap:106172;g:106462;b:107037;h:107550;tap:108235;b:108513;h:109032;b:109280;h:109533;b:109771;tap:110292;d:110524;g:111056;g:112554;tap:113199;d:113386;tap:113961;g:114157;g:115322;d:115587;g:115802;d:116050;g:116247;tap:116876;tap:117419;d:117578;g:117926;tap:118471;h:119273;b:119496;h:119707;d:120538;g:120777;d:120992;g:121290;d:121471;g:121743;d:121942;tap:122458;tap:122984;d:123180;g:123535;d:123824;tap:124543;tap:125048;h:125302;b:125522;h:125975;b:126323;tap:126995;d:127219;g:127678;d:127975;tap:128601;b:128877;tap:129604;h:129878;tap:130618;b:131006;tap:131689;h:132012;tap:132635;d:132924;tap:133645;g:133983;tap:134674;d:134898;b:135545;h:136072;b:136553;tap:137207;tap:137764;tap:138269;tap:138787;d:139035;tap:139753;d:140111;tap:140753;g:140999;d:141508;g:142118;d:142584;g:143164;d:143645;g:144127;g:144699;d:145189;d:145666;g:146183;d:146659;g:147134;d:147661;g:148144;d:148415;g:148705;d:149226;g:150159;d:150422;g:150696;d:151229;g:151776;tap:152494;tap:152991;tap:153539;b:153784;h:154323;tap:155012;tap:155526;tap:156011;tap:156520;tap:157087;tap:157553;tap:158067;tap:158558;tap:159062;");
        sequences.add("tap:1329;tap:2182;tap:3077;tap:3949;tap:4735;d:5497;g:6237;tap:7305;tap:8177;tap:9031;d:9693;g:10487;tap:11556;tap:12460;tap:13311;d:13968;tap:14627;d:14794;tap:15419;d:15674;tap:16276;g:16550;tap:17269;g:17453;tap:18082;g:18321;tap:18946;tap:19751;g:19979;tap:20669;d:20830;tap:21456;d:21719;tap:22323;tap:23146;d:23394;tap:24055;g:24275;tap:24912;g:25193;tap:25768;tap:26600;d:27293;tap:27931;tap:28748;tap:29588;tap:30138;tap:30463;tap:31282;d:31555;g:31981;d:32431;g:32850;d:33284;g:33718;d:34116;g:34542;d:34960;g:35428;d:35817;g:36218;tap:36872;d:37086;g:37544;d:37986;g:38766;d:39235;g:39660;d:40570;g:41399;tap:42073;tap:42970;tap:44168;tap:45856;tap:46818;tap:47595;tap:48443;tap:49309;tap:50154;tap:51046;tap:51892;tap:52768;tap:53629;tap:54499;d:55120;g:55949;d:56362;g:56846;tap:57899;d:58518;g:58955;d:59377;tap:60421;tap:61330;d:61942;d:62844;tap:63897;g:64559;tap:65641;tap:66525;g:67153;tap:67757;d:67973;tap:68608;g:68828;tap:69913;d:70579;tap:71591;tap:72000;d:72298;g:73123;d:74029;tap:75029;tap:75915;tap:76782;tap:77673;tap:78475;tap:78927;tap:80202;tap:81899;g:82550;d:83387;g:84629;d:85052;g:85526;tap:86217;tap:87064;tap:88371;tap:88837;tap:89632;d:90274;tap:91716;tap:92165;g:92772;tap:93931;d:94511;tap:95620;tap:96076;g:96271;tap:96917;d:97132;tap:97793;tap:98226;d:98451;g:98841;d:99282;tap:99908;tap:100324;g:100587;d:101027;tap:101653;tap:102118;g:102288;d:102701;tap:103327;tap:103758;tap:104203;tap:105466;tap:105931;tap:107076;tap:107618;tap:108919;tap:109319;d:109606;g:109993;tap:111056;tap:112374;g:112558;tap:113221;tap:113645;d:114306;tap:114908;tap:115815;d:116022;g:116406;tap:117070;tap:117923;tap:118748;tap:119172;d:119400;g:119838;tap:120464;tap:121164;tap:121803;d:122411;g:122870;d:123270;tap:123914;tap:124764;tap:125994;tap:126477;d:126746;g:127164;d:127580;d:128448;d:129302;d:130164;tap:130738;g:131350;g:132271;g:133177;g:133947;tap:135037;tap:135909;tap:136772;tap:137208;tap:137639;tap:138520;tap:139192;tap:139717;tap:140190;tap:141085;d:141674;g:142604;d:143316;tap:144006;tap:144485;tap:145188;tap:146616;tap:147064;d:147318;g:147731;tap:148767;tap:149622;d:150179;g:151053;tap:151819;tap:152160;tap:153012;tap:153521;tap:153890;d:154130;g:154550;tap:155644;tap:156505;tap:157368;g:157565;d:157972;tap:158617;tap:159046;g:159338;d:160168;tap:160750;tap:161185;g:161418;tap:162515;tap:163364;tap:164196;tap:165049;d:165663;g:166568;d:167383;g:167796;tap:168460;tap:168876;d:169105;tap:169765;tap:170197;g:170485;tap:171113;tap:171499;g:171710;d:172125;tap:172750;tap:173198;g:173479;d:173884;tap:174487;tap:174917;g:175116;d:175574;tap:176234;tap:176633;g:176932;d:177316;d:178147;");
        sequences.add("tap:1741;tap:2834;tap:3896;d:4266;g:5126;tap:6893;tap:7740;tap:8398;tap:9177;tap:9580;tap:10002;tap:10744;d:11264;g:11986;d:12325;g:12758;d:13079;g:13489;d:13678;g:13876;d:14065;d:14288;tap:15729;d:15876;tap:17540;tap:18115;tap:18690;tap:19280;d:19562;tap:20282;d:20551;tap:21355;g:22133;d:23233;g:24272;d:24812;g:25330;tap:26671;tap:27764;tap:28853;d:29182;g:29747;");
        sequences.add("tap:2172;tap:2675;tap:3519;tap:4026;tap:4833;tap:5529;tap:6665;tap:6986;tap:9611;tap:10527;tap:11614;tap:12652;tap:13729;tap:14840;tap:15820;tap:17064;d:17910;g:18926;d:19944;g:20956;d:21936;g:22418;d:22958;g:23408;d:23622;g:23853;d:24092;tap:24782;tap:25323;d:25554;g:26078;d:26520;g:26996;tap:27740;tap:28333;tap:29429;d:30110;g:31080;d:32124;tap:33451;g:34239;g:35417;d:36314;tap:37613;g:38314;d:39327;g:40465;tap:41677;tap:42766;d:43585;g:44531;tap:45806;d:46518;g:47639;g:48627;tap:49817;tap:50903;tap:51902;tap:52953;tap:53976;tap:54770;tap:55836;tap:57007;tap:58041;d:58496;g:58744;d:59277;g:59871;d:60280;g:60519;d:60692;g:60948;d:61333;g:61885;d:62360;tap:63091;tap:63871;tap:64230;g:64496;d:64693;g:65002;tap:65603;d:66010;g:66952;d:67975;tap:69323;d:70049;g:70963;tap:72319;tap:73445;d:74099;g:74756;d:75264;tap:76534;tap:77522;tap:78596;tap:79606;tap:80618;d:81360;g:82386;d:84111;tap:87000;");

        sequences.add("tap:1137;tap:1493;tap:2044;tap:2589;d:3255;g:4267;d:4639;g:4903;d:5143;g:5604;d:6064;g:7034;d:8001;g:8273;d:8512;g:8760;tap:9373;d:9680;tap:10819;g:11473;d:11908;g:12146;d:12411;g:12887;d:13312;g:14319;tap:15329;d:15548;g:15821;d:16085;g:16529;tap:17156;d:17816;tap:18941;d:19136;g:19400;d:19673;b:20079;h:20580;b:21406;h:22365;b:22886;d:23310;g:23785;d:24252;b:25078;tap:26207;h:26450;tap:27138;d:27320;g:27804;d:28703;tap:29858;d:30096;g:30344;tap:31240;tap:32096;tap:32761;tap:33376;d:34470;g:35044;d:36021;g:36560;d:36878;g:38038;d:38721;tap:39813;g:40539;d:41503;tap:42595;tap:43475;b:44148;h:44607;b:45076;h:45485;g:45973;d:46827;b:47869;h:48305;b:48698;h:49144;b:49617;g:50627;d:51472;g:51844;d:52307;b:52786;h:53256;tap:54343;d:55061;g:55553;d:55993;b:56466;h:56953;g:57749;d:58765;g:59463;d:60453;b:61078;h:61699;b:62329;tap:63480;h:64014;b:65053;tap:66142;d:66829;g:67848;d:68710;b:69569;h:70511;b:70955;tap:72623;d:73225;g:74100;d:75031;tap:76273;h:76866;tap:78016;b:78696;h:79648;tap:80771;d:81333;g:82250;d:83301;tap:84457;b:85024;h:85927;tap:87078;b:87765;h:88681;d:89112;g:89621;tap:90690;d:91447;g:92108;d:92738;g:93215;d:94145;b:95138;h:95851;b:96446;h:96909;tap:98092;d:98695;g:99153;d:99620;b:100059;h:100552;tap:101630;tap:102545;d:104139;g:104773;d:105409;g:105978;b:106919;tap:108002;h:108623;b:109553;h:110496;b:111389;tap:112587;d:113227;g:114172;d:115076;g:115967;tap:117122;tap:117928;tap:118419;tap:118940;g:119566;tap:120838;tap:121680;tap:122137;tap:122622;d:123178;tap:124385;g:125105;d:125564;b:126038;h:126971;tap:128127;b:128732;h:129190;g:129648;d:130488;tap:131724;tap:132414;b:133184;h:133797;b:134537;d:134998;g:136908;d:137802;g:138711;b:139580;h:140552;d:141464;tap:142589;tap:143472;d:143925;g:144854;tap:146124;b:146748;h:147244;b:147708;h:148646;b:149644;tap:150745;h:150956;tap:151667;b:152270;tap:153539;h:154127;b:154687;h:155137;tap:156199;b:156900;h:157814;g:158258;d:158708;tap:159800;tap:160761;tap:161719;tap:162609;d:163271;tap:164426;g:165278;tap:166254;tap:167987;tap:169732;tap:171680;d:172384;g:172877;d:173303;tap:174393;tap:175383;tap:177048;tap:177970;tap:178968;tap:179943;tap:180377;tap:180834;d:181482;g:182355;d:183186;g:183669;d:184216;b:185092;tap:186245;d:186406;g:186846;d:187285;g:187768;d:188695;tap:189822;g:190036;d:190487;g:190901;d:191360;g:193330;d:193741;b:194165;h:194556;b:195020;tap:196343;g:197064;");
        sequences.add("tap:1930;d:3587;g:3926;d:4191;g:4479;d:4769;b:5121;h:5448;d:6019;g:6517;d:6823;g:7167;d:7474;g:7747;d:8062;tap:8610;d:9418;g:9754;h:10723;b:11071;h:11976;b:12328;g:13011;d:13454;g:13814;d:14112;tap:14833;tap:15276;d:15406;g:15727;d:15975;g:16676;d:16999;g:17406;d:17687;b:18070;h:18453;tap:19275;tap:20404;g:21237;d:21573;g:21930;d:22557;g:22919;d:23687;b:24203;h:24643;b:25049;h:25543;b:25890;h:26593;b:27141;g:28532;d:29076;g:29382;d:29894;tap:30675;tap:31030;g:31197;d:31562;g:31892;d:32468;tap:33598;g:34131;d:34364;g:34643;d:35309;g:35645;d:35983;h:36267;b:36549;tap:37367;h:37699;tap:38520;b:38750;h:40803;b:41110;h:41456;h:42458;g:42801;d:43122;g:43445;tap:43926;d:44317;g:44686;tap:45723;d:45986;tap:46939;tap:47845;g:48059;d:48433;g:48763;tap:49854;tap:50885;b:51650;h:51986;b:52614;h:53030;b:53332;g:53635;d:54613;g:55593;d:56476;g:57466;d:58421;g:59492;d:60473;b:61244;h:61557;b:61905;h:62443;b:62913;h:63283;b:64267;h:65340;b:66095;g:66462;d:66802;g:67176;d:67440;tap:68337;tap:68687;d:68806;g:69143;d:69432;tap:70591;tap:71601;g:72012;d:72327;g:73296;d:73641;b:74281;h:75230;b:75620;h:76329;b:76682;h:77025;g:77338;d:77758;g:78156;d:78387;b:79207;h:79964;b:80304;h:80943;g:81344;d:81930;g:82767;d:82965;g:83254;tap:84414;g:84580;d:84904;g:85272;g:86607;d:86922;tap:87987;tap:88378;g:88717;d:88965;g:89270;d:89660;g:90572;d:91383;g:93162;");
        sequences.add("tap:2205;d:3182;g:3641;d:4039;tap:4730;tap:5154;tap:5590;g:5751;d:6184;g:6644;d:7045;g:7538;b:8787;h:9222;tap:9784;tap:10242;d:10899;g:11335;d:11732;tap:12423;b:12628;h:13046;tap:13731;b:13935;h:14368;tap:15055;d:15253;g:15668;tap:16295;d:16536;g:17004;tap:17630;d:17850;g:18237;tap:18898;tap:19455;b:19859;h:20051;b:20409;h:20773;tap:21367;b:22309;h:23668;g:24218;d:24591;g:25043;d:25421;b:26509;h:26841;b:27195;h:27574;b:28446;h:28873;g:29328;d:29748;tap:30726;d:31818;g:32412;d:32830;g:33237;d:33680;tap:34371;tap:34807;tap:35217;d:35338;g:35813;d:36196;tap:36859;tap:37285;g:38010;d:38412;g:38826;d:39320;g:39897;d:40868;g:41387;d:41830;g:42128;d:42602;b:43077;h:43480;b:43872;h:44357;tap:44968;h:45189;b:45594;tap:46274;h:46507;b:46931;tap:47529;g:48714;tap:49317;tap:49716;tap:50212;tap:50603;tap:51038;tap:51471;tap:51920;d:52294;g:52626;tap:54011;b:54205;h:54641;tap:55297;h:55526;tap:56119;h:56337;b:56815;h:57196;b:57634;g:58845;d:59369;g:60196;d:61168;g:61516;d:61937;g:62446;d:62897;tap:64399;h:64583;b:64911;tap:65564;h:65785;tap:66384;tap:66840;d:67082;g:67575;d:67911;tap:69413;g:69661;d:70096;g:70485;d:70952;g:71328;d:71795;b:72232;h:73096;b:74308;h:74777;g:75249;d:75668;tap:76311;tap:76831;tap:77232;tap:77837;d:78061;g:78268;tap:79706;d:80700;g:81624;d:82099;g:83267;b:84647;h:85906;g:87106;d:87606;g:88418;d:89718;g:90996;d:91448;g:91915;tap:92542;d:92815;g:93213;b:93593;h:93920;tap:94782;tap:95173;b:95359;h:95776;tap:96450;b:96667;h:97052;tap:97744;b:97946;h:98382;g:98818;d:99786;g:100164;d:100934;g:101340;d:101845;g:102693;d:103123;b:103712;h:103991;tap:105403;b:106100;h:106509;b:106965;h:107318;tap:108009;g:108204;d:108607;g:109084;d:109490;tap:110135;g:110355;d:110815;g:111250;tap:111854;b:112069;b:112514;h:112906;b:113319;h:113787;tap:114386;b:114722;h:115131;tap:115697;g:115942;d:116341;g:116781;tap:117408;d:117654;g:118147;tap:118720;tap:119181;d:119373;g:119834;tap:120438;d:120625;g:121086;d:121537;tap:122111;g:122393;d:123013;g:123227;d:123648;b:124303;h:124589;b:124939;h:125377;tap:125972;g:126245;d:126664;tap:127292;tap:128562;g:128800;d:129628;g:130897;d:131341;g:131776;b:132197;h:132628;b:133083;d:133523;tap:134925;g:136008;d:136516;tap:137180;tap:137576;tap:138893;b:139103;tap:140107;h:140316;tap:141002;b:141268;tap:141921;d:142138;g:142574;tap:143178;d:143770;g:144990;d:146334;g:147453;d:147871;g:148671;d:148968;b:149721;h:150219;b:150705;b:151822;b:152500;tap:153569;b:154072;tap:155167;h:155363;b:155781;tap:156707;d:157128;g:157522;d:158391;tap:159412;tap:160291;d:160866;g:161423;d:162132;g:162631;tap:163557;d:163906;b:164366;h:164991;b:165617;h:166045;tap:166905;tap:167596;d:167801;g:168503;d:169055;g:169544;d:170813;d:171275;d:172137;d:173413;d:174956;g:175458;d:176161;g:177097;d:177657;g:178236;d:178589;g:179074;d:179418;tap:180088;tap:180524;tap:181150;b:181473;h:181903;b:182325;tap:183074;h:183235;b:183676;tap:184289;h:184540;b:185416;h:185891;b:186669;h:187879;b:188615;h:189153;b:189678;h:190523;b:190942;h:191389;g:191827;d:192229;g:192684;d:193033;b:193539;h:194178;b:194763;h:195729;b:196133;h:196512;g:196926;d:197378;g:197828;tap:198439;g:198714;d:199137;tap:199740;g:200029;tap:200604;d:200816;g:201268;d:201844;g:202141;tap:202715;h:202927;b:203406;tap:204038;d:204234;g:204669;tap:205296;tap:205711;tap:206184;h:206388;b:206834;tap:207492;d:207719;tap:208324;h:208592;tap:209226;b:209435;h:209813;b:210206;h:210860;b:211117;h:211774;b:212172;h:212485;tap:213499;g:213690;d:214094;g:214538;d:214955;g:215416;d:216406;d:217283;d:217657;d:218605;d:218903;d:219869;d:220221;tap:221028;d:221396;g:221935;tap:222681;g:222829;tap:223521;g:223679;d:223944;b:224838;h:225222;tap:226167;b:226351;h:226914;b:227400;h:227799;b:228805;h:229179;b:229533;b:230446;tap:231544;h:231698;g:232143;d:232817;g:233116;d:233371;g:234657;d:235500;g:235999;d:236606;b:237215;h:237646;b:238073;h:238502;g:239266;d:239881;g:240285;d:240674;b:241096;h:241577;tap:242178;tap:242603;b:243087;h:243629;b:244110;h:244530;b:244928;g:245432;d:245838;g:246228;b:246991;h:247506;tap:248134;tap:248603;d:248770;g:249223;d:249746;tap:250321;g:250566;d:250994;tap:251597;tap:252066;tap:252518;tap:252936;tap:253362;d:253570;g:253998;d:254415;g:254822;d:255329;g:255659;d:256077;tap:257231;tap:258359;g:259096;d:259510;g:260115;d:260387;g:261252;d:261657;g:262075;d:262538;tap:263199;g:263441;d:263817;tap:264862;g:265112;d:265498;g:265933;tap:266594;d:266915;g:267267;d:268132;g:268537;b:268973;h:269377;tap:270016;b:270262;h:270661;tap:271371;b:271615;h:271978;tap:272633;b:272845;h:273261;b:273771;h:274235;b:275192;h:276029;g:277289;d:277623;g:278074;d:278993;g:279881;d:280797;g:281028;d:281806;g:282137;d:283089;g:284345;tap:286230;");
        sequences.add("tap:1348;tap:2075;tap:3040;tap:3862;tap:4755;tap:5588;tap:6501;tap:7434;tap:8388;tap:9286;tap:10139;tap:11144;tap:12093;tap:13000;tap:13781;d:13984;g:14435;d:14886;tap:15629;b:15796;tap:16514;b:16719;tap:17405;h:17613;tap:18291;d:18522;tap:19211;g:19416;tap:20107;h:20328;tap:21016;b:21228;tap:21884;b:22144;tap:22811;d:23008;tap:23697;h:23895;tap:24575;b:24761;tap:25458;g:25689;tap:26408;d:26620;tap:27280;g:27572;tap:28197;d:28409;g:28860;d:29302;g:29827;tap:30455;d:30665;g:31124;tap:31814;d:32030;g:32522;tap:34067;h:34296;tap:34954;g:35219;d:35725;tap:36327;h:36543;tap:37229;g:37457;tap:38148;d:38380;tap:39041;g:39331;tap:39934;d:40187;tap:40877;g:41069;tap:41789;d:42000;tap:42644;g:42876;tap:43567;d:43787;tap:45366;h:45615;tap:46269;tap:46710;h:46972;tap:47630;tap:48096;tap:48528;tap:49440;d:49640;g:50124;d:50620;g:50989;d:51481;tap:52143;h:52379;tap:52993;b:53234;tap:53947;g:54149;d:54616;tap:55337;g:55556;tap:56182;d:56447;g:56914;tap:58872;b:59128;tap:59784;d:60469;g:61003;d:61410;tap:62036;g:62286;d:62729;tap:63419;g:63630;tap:64349;g:64578;tap:65239;d:65443;tap:66133;b:66345;h:66809;b:67247;h:67702;tap:68383;g:68631;tap:69321;tap:69754;g:69995;d:70412;tap:71102;tap:71553;tap:72008;tap:72483;g:72722;tap:73382;d:73531;tap:74275;g:74477;tap:75167;g:75400;tap:76092;d:76297;tap:76958;d:77208;tap:77836;d:78106;tap:78768;b:78931;tap:79645;b:79863;tap:80582;b:80785;tap:81433;tap:81866;h:82112;b:82612;h:83056;b:83489;tap:84173;h:84384;b:84848;h:85341;tap:85965;g:86181;tap:86874;d:87090;g:87575;d:88034;g:88487;d:88898;tap:89558;tap:90027;tap:90495;d:90684;tap:91411;g:91608;tap:92300;g:92533;tap:93203;d:93412;tap:94134;tap:94568;tap:95017;tap:95485;d:95677;g:96104;d:96311;g:96583;tap:96905;d:97027;g:97480;d:97906;tap:98567;tap:99103;tap:99544;tap:99972;d:100185;tap:100906;g:101064;tap:101783;b:101995;h:102440;tap:103129;g:103274;tap:103993;h:104215;b:104510;h:104703;b:104937;h:105285;tap:105852;");
        sequences.add("tap:992;tap:1752;tap:2082;tap:3630;d:3972;g:4548;d:4887;tap:5387;d:5778;tap:6585;d:6751;g:7007;d:7305;tap:7850;g:8176;d:8818;g:9123;d:9404;tap:10212;g:10535;g:11788;tap:12624;d:13006;g:13883;d:14198;d:15405;tap:16148;g:16353;d:16627;tap:17405;g:17803;d:18400;g:18714;d:18978;g:19293;tap:19837;h:20119;tap:20971;h:21237;tap:22186;h:22525;b:22867;h:23183;b:23528;tap:25178;h:25587;tap:26390;h:26788;b:27295;h:27951;tap:28787;g:29140;d:29801;g:30405;d:30958;g:31631;d:32185;tap:32994;b:33365;h:33962;b:34569;d:35172;g:35487;d:35784;g:36074;d:36372;g:36669;d:36950;g:37265;d:37580;g:37924;d:38171;g:38436;tap:38982;g:39357;tap:40168;g:40573;tap:41383;g:41825;tap:42580;g:42983;tap:43762;g:44175;tap:45013;g:45360;tap:46169;g:46585;tap:47393;d:47719;tap:48597;d:48988;tap:49798;d:50135;tap:50974;d:51356;tap:52165;d:52519;tap:53357;g:53724;tap:54586;b:54888;tap:55748;b:56180;tap:56984;h:57296;tap:58162;b:58557;tap:59412;h:59731;tap:60564;b:60864;tap:61838;h:62147;tap:62978;b:63361;tap:64250;d:64520;g:65212;d:65803;g:66403;d:66967;tap:67781;g:68146;tap:68982;d:69354;tap:70191;g:70588;tap:71367;d:71748;tap:72584;g:72944;tap:73803;d:74209;tap:75016;g:75380;tap:76158;d:76539;g:76852;d:77150;h:78417;b:78760;h:79016;d:79908;g:80188;tap:81025;g:81364;tap:82170;tap:83085;d:83773;g:84366;tap:85202;d:85558;g:86179;d:86451;g:86782;tap:87542;d:87873;g:88600;b:89213;h:89781;b:90457;h:91012;b:91589;h:92194;b:92806;h:93366;tap:94211;d:94576;g:95192;d:95794;tap:96604;g:96928;tap:97765;g:98174;tap:98983;g:99399;tap:100208;d:100585;tap:101396;g:101827;tap:102573;h:102929;tap:103772;g:104189;d:104798;tap:105543;b:105887;h:106531;b:107166;h:107770;b:108332;h:108954;g:109549;d:110143;tap:110980;g:111371;tap:112208;g:112613;tap:113393;g:113766;tap:114604;d:114946;tap:115781;d:116147;tap:116985;d:117409;tap:118186;d:118549;tap:119408;d:119732;tap:120591;g:120948;tap:121783;g:122214;tap:122973;d:123312;tap:124172;g:124557;tap:125392;g:125770;tap:126576;d:126897;tap:127765;d:128161;g:128790;d:129420;tap:130182;g:130555;tap:131390;d:131787;tap:132623;g:133027;tap:133787;d:134168;tap:135028;b:135345;tap:136148;h:136530;tap:137414;b:137732;tap:138568;h:138960;tap:139792;d:139965;g:140221;d:140526;g:140857;d:141138;g:141453;tap:142229;g:142611;d:143194;tap:143970;g:144356;d:144977;tap:145720;b:146118;h:146826;tap:147581;g:147974;tap:148752;d:149133;tap:149993;g:150514;d:151586;g:153908;d:156305;g:157759;d:158201;g:158456;tap:159002;b:159314;tap:160169;b:160495;tap:161356;d:161740;tap:162599;g:162954;tap:163791;d:164180;tap:164987;g:165343;tap:166179;d:166592;tap:167369;g:167739;d:168060;g:168400;tap:169206;g:169594;b:170260;tap:171004;h:171358;tap:172194;g:172558;tap:173392;d:173782;tap:174589;g:174986;tap:175793;d:176173;tap:177006;g:177388;d:178012;tap:178719;b:179137;tap:179969;g:180412;tap:181170;d:181561;tap:182418;g:182824;tap:183602;b:183965;h:184564;b:185175;h:185819;b:186385;h:186693;b:186985;tap:187755;d:188166;tap:189002;d:189308;tap:190231;d:190579;tap:191385;d:191724;tap:192584;g:192953;tap:193788;g:194209;tap:194986;g:195417;tap:196223;tap:196778;g:197153;tap:198011;g:198449;tap:199192;g:199604;d:200097;tap:200931;g:201283;tap:202143;d:202503;tap:203338;g:203718;tap:204552;g:204950;d:205541;g:205872;d:206170;tap:207007;g:207372;tap:208210;b:208597;tap:209453;b:209764;tap:210570;b:210972;tap:211831;h:212192;tap:213005;h:213374;tap:214208;h:214551;tap:215369;d:215722;g:216361;d:217295;g:217618;d:217924;g:218248;tap:219000;d:219349;g:219988;d:220565;g:221165;tap:221974;d:222343;tap:223152;g:223573;d:224160;g:224770;d:225058;g:225381;b:225657;h:225950;tap:226759;tap:228553;tap:230898;tap:232694;g:233126;d:234370;g:235505;d:237841;tap:240554;g:242149;d:242926;");
        sequences.add("tap:1232;tap:2187;tap:2569;g:2836;d:3214;g:3690;tap:4265;d:4506;g:4950;d:5395;g:5878;d:6231;g:6674;d:7047;g:7514;d:7929;g:8364;d:8788;g:9199;d:9641;g:10038;d:10468;g:10903;d:11346;g:11837;b:12248;h:12627;b:13072;h:13492;b:13913;h:14330;b:14796;h:15198;b:15580;h:16019;b:16440;h:16935;h:17366;b:17790;h:18205;b:18631;g:19050;d:19455;g:19921;d:20335;g:20741;d:21159;g:21610;d:22028;g:22437;d:22863;g:23340;d:23737;g:24151;tap:24811;d:25051;g:25461;d:25921;g:26301;d:26728;g:27122;d:27580;d:28043;tap:29132;tap:29973;tap:30480;g:30651;tap:31278;tap:31727;d:32257;tap:32947;tap:33419;tap:33825;g:34008;tap:35097;tap:35522;d:35768;tap:36456;tap:37229;d:37478;tap:38167;tap:39023;tap:39803;tap:40633;g:40874;d:41324;tap:41951;g:42166;tap:42808;d:43057;tap:43684;g:43882;tap:44486;b:44697;tap:45336;h:45576;tap:46239;b:46482;tap:47104;d:47343;tap:47971;d:48177;tap:48780;g:49078;tap:49652;b:49898;tap:50501;h:50809;tap:51409;b:51637;tap:52235;b:52820;tap:53555;g:53754;d:54176;tap:54779;tap:55266;tap:56100;tap:56543;tap:58233;g:58424;d:58875;g:59716;d:60149;g:60591;d:61058;g:61484;d:61873;b:62279;h:62692;b:63163;h:63605;b:64016;h:64458;b:64896;g:65346;d:65735;g:66152;d:66593;g:67004;d:67446;g:67868;d:68262;g:68721;b:69140;h:70017;b:70867;h:71286;b:71692;g:72167;d:72398;g:73083;d:73314;b:73895;h:74124;b:75492;h:75716;g:76921;d:77307;g:77528;d:77856;g:78882;d:79112;g:79368;d:80520;g:80792;d:81032;g:81634;d:82060;g:82457;d:82864;g:83370;b:83767;h:84164;b:84628;h:85059;tap:85866;tap:86765;tap:87386;g:87565;d:88024;g:88475;d:88873;g:89300;d:89719;d:90127;d:90572;d:91014;d:91491;d:91932;d:92331;d:92758;d:93168;d:93595;d:94008;d:94468;d:94886;d:95328;d:95739;g:96132;g:96567;d:96993;g:97427;d:97885;g:98295;g:98770;g:99168;d:99544;g:99955;d:100447;b:100859;b:101641;b:102117;b:102594;b:103092;b:103494;b:103920;b:104361;b:104762;b:105191;h:105585;b:105978;h:106411;tap:107067;g:109598;d:111099;g:111947;d:112405;tap:113067;g:113293;d:113728;g:114187;tap:114790;d:115018;g:115445;d:115867;tap:116493;g:117007;d:117558;tap:118199;g:118440;d:118892;g:119333;tap:119936;d:120530;g:120996;tap:121638;d:121894;g:122347;d:122731;tap:123357;g:123597;d:123961;g:124470;tap:125072;d:125275;g:125736;d:126133;tap:126759;b:127007;h:127455;b:127867;tap:128494;g:128730;d:129180;g:129560;tap:130222;d:130453;g:130814;d:131266;tap:131892;g:132140;d:132558;g:132980;tap:133670;d:133872;g:134287;d:134730;tap:135373;g:135579;d:135998;g:136404;tap:137064;d:137284;g:137741;d:138140;tap:138766;b:139522;h:139895;b:140339;h:140717;g:141167;d:141556;g:142022;g:142870;d:143259;g:143692;g:144638;b:145040;h:145402;b:145836;h:146753;b:147110;h:147556;b:147972;h:148839;g:149314;d:149511;g:149751;d:150561;g:151404;d:151897;g:152287;b:152723;h:153170;b:153596;h:154478;b:154719;h:154961;g:155660;d:156136;g:156350;d:157013;g:157512;d:157884;b:158321;h:158768;b:159106;h:159591;g:160009;d:160406;g:160890;d:161291;b:161694;h:162573;b:163417;h:163880;b:164305;h:164686;g:165125;d:165571;g:166010;d:166449;b:166854;h:167350;b:167729;tap:169193;g:169417;d:169844;tap:170470;d:170689;g:171140;d:171596;tap:172198;g:172406;d:172841;g:173307;d:173740;g:174166;d:174579;g:175019;tap:175621;d:175824;g:176285;tap:176927;d:177158;tap:177761;g:178009;tap:178671;d:178850;g:179334;tap:179936;d:180159;g:180561;tap:181250;d:181441;g:181851;tap:182454;d:182719;g:183162;tap:183823;d:184042;g:184478;tap:185052;d:185272;g:185694;d:186145;g:186572;tap:187199;d:187431;g:187847;d:188273;g:188717;tap:189407;");
        sequences.add("tap:8789;tap:9635;g:9892;d:10359;tap:11049;g:11340;tap:12085;g:12359;tap:13050;g:13415;tap:14076;g:14383;tap:15102;g:15426;tap:16088;g:16411;tap:17016;g:17331;tap:18050;g:18396;tap:19056;d:20328;g:20905;d:21440;g:21779;tap:22936;d:23443;g:23748;d:23963;g:24640;d:26304;g:26868;d:27347;g:27861;d:28322;b:28841;h:29317;tap:30616;g:30835;d:31370;g:31864;tap:32525;d:32832;g:33548;d:33804;g:34913;d:35227;g:35712;d:36296;tap:37040;d:37344;g:37875;tap:38566;tap:39061;tap:39630;tap:40139;d:40372;g:40855;d:41411;b:42796;tap:44119;g:44791;d:45500;g:45882;tap:47252;d:47536;g:47922;d:48382;g:48863;d:50440;g:50892;d:51896;g:52789;d:53255;b:54627;h:55834;tap:56807;d:57315;g:57962;d:59127;g:59407;d:59831;g:60382;b:61049;d:62276;g:62753;d:63405;g:63856;b:64949;h:65416;b:66701;h:67023;d:68190;g:68766;d:69275;g:69810;d:70335;tap:70938;d:71311;tap:71937;d:72279;tap:72941;g:73329;tap:73957;g:74304;tap:74995;g:75377;tap:76038;d:76343;tap:77033;d:77428;tap:78117;d:78463;tap:79066;d:79420;tap:80047;g:80384;d:81441;g:81671;d:81878;tap:82240;g:83149;d:83734;g:84007;d:84816;g:86397;d:86880;d:87365;d:87832;d:88366;g:88810;d:89319;d:90800;tap:92652;g:93135;d:93648;g:93879;g:95253;d:95500;g:95789;g:96321;d:96796;g:98319;g:98832;g:99355;g:99922;g:100439;d:100850;g:101319;tap:102909;d:104146;g:104997;d:105755;g:106127;d:107492;b:108488;h:108942;b:110025;tap:111521;h:111790;tap:112506;h:112824;tap:113564;h:113847;tap:114564;h:114896;tap:115556;b:115869;h:116324;tap:117009;b:117335;tap:118020;h:118280;tap:118971;b:119296;tap:120012;h:120362;tap:120986;b:121328;tap:122016;h:122359;tap:123017;h:123375;tap:124089;b:124369;tap:125052;d:125408;tap:126068;d:126405;tap:127030;g:128442;d:129428;g:131298;d:132688;b:134192;h:135725;b:137282;d:138106;g:138453;tap:139287;d:140665;g:141718;tap:143072;d:143403;tap:144094;g:144350;tap:145071;g:145378;tap:146068;g:146401;tap:147090;g:147352;tap:148073;g:148326;tap:149046;d:149341;tap:150059;tap:150967;d:152211;g:153195;d:153626;g:153940;d:154961;g:155412;d:155764;g:155978;d:156401;g:156892;d:158322;d:158814;d:159353;d:159912;g:160363;d:160847;g:161347;b:162686;tap:164651;tap:165092;d:165545;g:165834;tap:167043;b:167341;h:167700;b:167962;h:168780;tap:170516;g:170822;tap:171483;g:171790;g:172393;g:172938;d:173347;tap:174597;g:174862;d:176381;b:177059;h:177816;b:179159;h:179720;b:180013;tap:181091;g:182359;d:183043;g:183960;tap:185121;d:185367;tap:186114;g:186428;tap:187058;g:187408;tap:188108;d:188337;tap:189029;g:189378;tap:190039;g:190353;tap:191083;d:191369;tap:192033;d:192449;tap:193053;g:193346;tap:194046;d:194437;tap:195050;d:195396;tap:196058;g:196426;tap:197053;d:197428;tap:198099;g:198411;tap:199048;d:200499;tap:203813;g:204012;tap:204615;g:204907;tap:205509;g:205874;tap:206477;g:206905;tap:207531;d:207864;tap:208555;d:208892;tap:209535;d:209860;tap:210519;h:210856;tap:211480;h:211869;tap:212556;h:212882;tap:213569;h:213891;tap:214533;h:214848;b:215430;");
        sequences.add("tap:1023;tap:2449;g:2665;d:3022;g:3254;d:3593;g:3809;d:4023;tap:4685;g:4821;d:5044;g:5326;d:5541;g:5747;d:6248;g:6617;b:6902;h:7239;b:7591;h:8025;b:8412;h:8642;b:8935;tap:9554;g:9803;tap:10407;d:10635;g:11128;d:11588;g:11998;tap:12689;d:12904;tap:13547;g:13768;d:14182;tap:14873;g:15113;tap:15740;d:15981;tap:16643;g:16939;tap:17582;d:17805;tap:18433;g:18711;tap:19339;d:19596;tap:20200;g:20506;tap:21133;d:21781;d:22250;d:22735;d:23136;d:23521;g:23980;g:24474;g:24897;g:25356;g:25783;h:26686;h:27120;h:27588;h:27935;b:28376;b:28875;b:29347;b:29794;d:30209;d:30718;d:31195;d:31568;d:31950;g:32406;g:32930;g:33388;g:33823;d:34220;d:34678;d:35121;d:35555;tap:36216;g:36420;tap:37111;d:37372;tap:38000;b:38229;tap:38857;h:39100;tap:39723;b:39993;tap:40681;h:40877;tap:41497;b:41784;tap:42443;h:42641;tap:43328;d:43583;tap:44187;d:44445;tap:45106;g:45344;tap:46034;tap:46415;tap:46907;tap:47365;g:47569;tap:48231;h:48475;tap:49098;d:49339;tap:50001;b:50244;tap:50905;g:51120;tap:51811;h:52019;tap:52643;d:52870;tap:53531;b:53784;tap:54407;g:54636;d:55143;g:55593;d:56019;g:56461;d:56894;tap:57584;g:57755;g:58315;g:58742;tap:59369;d:59601;g:59979;d:60428;tap:61090;b:61322;tap:61980;g:62216;d:62693;g:63137;tap:63797;tap:64216;d:64435;g:64913;tap:65516;tap:65974;d:66199;g:66625;tap:67344;tap:67793;d:68010;g:68452;d:68894;tap:69585;g:69793;b:70288;h:70687;b:71088;tap:71742;d:72015;g:72501;d:72911;b:73354;h:73781;b:74211;h:74627;tap:75342;d:75580;g:76008;d:76449;g:76859;d:77278;tap:77998;d:78212;tap:78873;d:79139;tap:79766;d:79978;g:80429;d:80896;g:81323;d:81729;tap:82390;d:82672;tap:83315;tap:83790;tap:84232;tap:84681;tap:85130;tap:85564;b:85796;h:86214;b:86679;h:87104;tap:87725;g:87976;d:88475;g:88928;d:89363;g:89815;d:90283;g:90681;b:91162;h:91569;b:92014;h:92463;tap:93097;g:93277;tap:93978;d:94227;tap:94891;g:95107;tap:95770;d:95978;tap:96650;d:96942;tap:97570;d:97794;tap:98457;d:98687;tap:99299;d:99625;tap:100200;g:100462;tap:101098;g:101395;tap:102007;g:102253;d:103070;g:103554;d:104024;g:104431;d:104874;g:105131;d:105354;tap:106017;h:106230;b:106706;tap:107332;g:107541;d:107926;g:108175;d:108423;tap:109115;d:109357;g:109818;d:110221;g:110476;d:110726;tap:111329;tap:111797;tap:112233;d:112482;d:112950;d:113404;d:113864;g:114295;g:114731;g:115151;g:115586;b:116028;b:116418;b:116923;b:117334;h:117785;h:118251;h:118702;h:119119;d:119553;d:120031;d:120455;d:120882;b:121339;b:121810;b:122293;b:122716;b:123198;tap:124203;tap:124653;g:124870;d:125355;g:125805;d:126208;tap:126870;g:127119;tap:127779;d:128013;tap:128704;g:128903;tap:129594;d:129794;tap:130454;g:130692;tap:131319;d:131564;tap:132256;g:132484;tap:133111;d:133318;tap:134009;g:134242;tap:134904;d:135152;tap:135756;b:135997;tap:136685;h:136906;tap:137530;b:137748;tap:138385;h:138655;tap:139295;d:139626;tap:140253;g:140472;tap:141099;d:141373;g:141922;d:142292;tap:142865;g:143140;tap:144677;g:144890;tap:145581;d:145817;tap:146444;g:146693;tap:147383;d:147579;tap:148208;g:148484;tap:149146;d:149362;tap:149990;b:150204;tap:150891;h:151136;tap:151739;b:151984;tap:152584;h:152915;tap:153541;b:153756;tap:154400;h:154694;tap:155293;b:155552;h:156015;b:156430;tap:157083;d:157314;g:157774;d:158224;tap:158866;b:159085;h:159597;b:160008;h:160447;d:160864;g:161037;d:161302;");

        sequences.add("tap:1141;tap:1542;tap:1742;tap:2152;tap:2352;d:2643;g:2969;d:3167;g:3432;d:3705;tap:4206;tap:4406;tap:4881;g:5131;d:5378;g:5577;d:5841;g:6139;tap:6640;tap:6840;tap:7255;g:7429;d:7748;g:7931;d:8220;g:8518;tap:9073;tap:9273;tap:9671;g:9875;d:10186;g:10392;d:10641;g:10939;tap:11484;tap:11747;tap:11947;g:12219;d:12499;g:12722;d:12986;g:13369;tap:13952;tap:14220;tap:14420;tap:14935;tap:15135;tap:15384;tap:15670;tap:15870;g:16065;d:16347;g:16620;tap:17341;tap:17798;tap:17998;tap:18387;tap:18672;tap:18872;tap:19277;g:19466;d:19789;g:19987;d:20227;g:20517;tap:21042;tap:21242;tap:21724;tap:22146;tap:22346;tap:22543;tap:22743;g:23252;d:23516;g:23814;tap:24507;tap:25240;g:25700;d:25956;g:26244;tap:26872;tap:27633;tap:27833;g:28054;d:28319;g:28660;tap:29274;tap:29755;tap:29955;tap:30357;tap:30557;d:30746;g:31027;tap:31692;tap:32182;tap:32382;tap:32685;tap:32885;h:33139;b:33394;tap:34051;tap:34511;tap:34711;tap:35097;tap:35297;b:35550;h:35818;tap:36503;tap:36703;tap:36957;tap:37157;tap:37559;tap:37759;d:37922;tap:38901;tap:39112;tap:39312;tap:39665;tap:39865;g:40070;d:40334;g:40631;tap:41311;tap:41511;tap:41716;tap:41916;g:42131;d:42379;tap:42912;tap:43112;g:43496;d:43734;g:43957;d:44205;tap:44780;tap:45134;tap:45334;tap:45689;b:45830;h:46171;b:46382;h:46647;b:46865;tap:47415;tap:47615;g:48289;d:48570;g:48793;d:49033;g:49346;tap:49852;tap:50052;tap:50472;g:50731;d:50971;g:51194;d:51425;g:51673;tap:52265;tap:52465;tap:52871;g:53091;d:53348;g:53545;d:53785;g:54051;tap:54624;tap:54926;tap:55126;d:55474;g:55773;d:55962;g:56210;d:56458;tap:57005;tap:57326;h:57838;b:58178;h:58362;b:58631;h:58883;g:59193;d:59456;g:59703;tap:60449;tap:60950;tap:61150;tap:61507;d:61640;g:61920;d:62193;tap:62885;tap:63357;tap:63663;tap:63940;tap:64140;g:64304;d:64561;tap:65275;tap:65475;tap:65747;tap:66094;tap:66294;d:66482;g:66730;d:66978;h:67529;b:67822;h:67968;b:68240;h:68548;g:68944;d:69215;g:69596;d:69908;g:70197;d:70388;h:70813;h:71220;g:71505;g:71710;g:72022;d:72340;d:72642;d:72831;g:73176;g:73711;g:74103;d:74708;g:75341;d:75946;g:76497;b:77087;h:77400;b:77580;h:77830;b:78092;tap:78657;tap:78981;tap:79181;d:79475;g:79819;d:80386;g:81338;d:81635;tap:82179;b:82457;h:82709;b:82917;tap:83435;tap:83635;tap:84014;d:84271;g:84647;d:84911;g:85429;d:85710;tap:86896;tap:87096;b:87195;h:87442;b:87717;tap:88280;tap:88575;tap:88775;h:89122;b:89404;h:89612;b:89853;h:90091;tap:90653;tap:90853;tap:91258;g:91453;d:91700;g:91941;d:92189;tap:92704;tap:93124;tap:93324;tap:93648;g:93886;d:94182;g:94397;d:94670;g:94942;b:95246;h:95525;tap:96446;h:96603;b:96826;h:97073;b:97379;tap:97914;tap:98230;tap:98551;h:98690;b:98959;h:99136;b:99407;h:99673;tap:100206;tap:100406;tap:100847;d:101053;g:101354;d:101533;g:101806;b:102121;tap:102703;tap:102903;tap:103285;h:103503;b:103791;h:103978;b:104208;h:104453;tap:104923;tap:105258;d:105746;g:106124;d:106338;g:106636;d:106900;tap:107462;tap:107662;tap:108068;d:108260;g:108575;d:108782;g:109038;d:109311;h:109603;b:109870;tap:110869;tap:111069;b:111194;h:111456;b:111708;tap:112224;tap:112424;tap:112856;d:113122;g:113370;d:113593;g:113849;d:114155;tap:114663;tap:114863;tap:115249;g:115514;g:116031;g:116319;d:116541;g:116806;tap:117331;tap:117648;d:117893;g:118181;d:118396;g:118644;b:118913;tap:119484;tap:119769;tap:119969;d:120303;g:120558;d:120798;g:121063;d:121360;tap:121865;tap:122065;tap:122455;h:122683;b:122960;h:123133;b:123398;h:123693;tap:124226;tap:124426;b:125047;h:125387;b:125585;h:125881;b:126135;tap:126672;tap:126980;tap:127291;d:127503;g:127756;d:127970;g:128293;d:128549;tap:129082;tap:129282;tap:129647;g:129888;d:130216;g:130422;d:130678;g:130943;tap:131476;tap:131676;tap:132065;h:132298;b:132581;h:132782;b:133029;h:133293;tap:133795;tap:133995;tap:134945;tap:135502;tap:136095;tap:136679;tap:136988;tap:137188;tap:137953;tap:138464;tap:138664;tap:139459;tap:139778;tap:140336;tap:140889;tap:142130;tap:142768;tap:143195;tap:143497;tap:143697;tap:144169;tap:144369;h:144610;b:144852;tap:145423;tap:145919;tap:146331;tap:146638;tap:146838;tap:147557;tap:148167;tap:149033;tap:149233;tap:149962;tap:151320;tap:151738;tap:152400;tap:152833;tap:153033;tap:153413;tap:153819;tap:154019;");
        sequences.add("tap:1825;tap:2346;tap:2813;tap:3305;tap:3788;tap:4266;tap:4698;tap:5203;tap:5671;tap:6121;tap:6625;tap:7058;tap:7557;tap:8007;tap:8437;tap:8637;tap:8964;tap:9334;tap:9534;tap:9974;tap:10407;tap:11263;tap:11463;tap:11504;tap:11704;tap:12288;tap:12785;tap:13362;tap:13562;tap:14198;tap:14625;tap:15094;tap:15617;d:15881;g:16117;d:16366;g:16765;d:16986;g:17210;d:17538;g:17935;d:18167;d:18668;d:19175;g:19609;g:20127;g:20553;h:21037;h:21522;h:21959;b:22421;h:22920;b:23396;tap:24079;d:24316;g:24793;d:25302;g:25683;d:26177;tap:26868;d:27128;g:27609;tap:28300;d:28547;tap:29268;g:30468;d:31134;g:31818;d:32032;g:32273;d:33058;g:33266;d:33735;g:33965;d:34197;g:34943;h:35234;b:35698;h:35911;b:36099;h:36855;b:37058;d:37490;g:37721;d:37960;g:38522;d:38982;g:39432;d:39892;g:40415;d:40884;tap:41490;tap:41690;tap:42013;d:42325;g:42540;d:42763;tap:43456;tap:43898;tap:44870;tap:45377;tap:45784;d:46060;g:46300;d:46548;tap:47210;d:47468;tap:48189;g:48455;tap:49147;d:49421;tap:50085;h:50297;tap:50957;h:51246;tap:51933;h:52232;tap:52878;d:53168;tap:53797;b:54074;tap:54737;g:55012;tap:55714;g:56019;tap:56650;tap:57099;tap:57600;tap:58095;tap:58573;d:58807;g:59308;d:59769;g:60225;tap:60888;h:61113;tap:61825;h:62063;tap:62779;h:63035;tap:63754;d:64008;g:64475;tap:65166;h:65496;b:65884;tap:66571;d:66858;h:67281;tap:67999;g:68220;d:68786;tap:69415;tap:69943;tap:70388;d:70651;g:71094;d:71474;tap:71953;tap:72153;d:72512;g:72751;d:73006;g:73392;tap:73829;tap:74029;h:74862;b:75382;tap:76010;d:76274;g:76788;d:77169;tap:77590;tap:77790;d:78040;g:78465;d:78703;g:79047;tap:79502;tap:79702;d:80027;g:80316;d:80547;g:80946;tap:81357;tap:81557;b:81835;h:82396;b:82815;tap:83260;tap:83460;b:83749;h:84166;b:84376;h:84724;tap:85117;tap:85317;d:85593;g:85982;d:86195;tap:86859;tap:87373;d:87644;g:87873;d:88104;tap:88886;tap:89086;d:90014;g:90490;b:90933;h:91417;b:91918;h:92329;b:92565;h:92778;d:93570;h:94175;b:94414;h:94663;b:95412;h:95636;d:96800;g:97309;d:97556;g:97991;d:98262;g:98468;d:99182;g:99428;d:99879;tap:100598;d:100896;tap:101559;d:101846;tap:102508;d:102797;g:103156;d:103617;tap:104307;d:105064;g:105328;d:105567;tap:106258;d:106606;tap:107235;tap:107630;tap:107830;d:107953;g:108224;tap:108806;tap:109006;d:109355;g:109814;d:110320;tap:111000;d:111248;g:111518;d:111694;b:112169;h:112435;b:112634;tap:113301;tap:113501;b:113595;h:114295;b:114521;h:114948;d:115204;g:115418;d:115994;g:116470;d:116859;tap:117288;tap:117488;d:117667;g:118077;d:118300;b:118767;h:119017;tap:119448;d:120230;g:120526;b:122189;h:122556;tap:123208;d:123724;g:124038;tap:124683;tap:125186;d:125296;g:125669;d:125892;tap:126585;d:126833;tap:127726;tap:127926;b:128070;h:128273;tap:128925;d:129407;g:129656;d:130106;g:130625;tap:131521;d:131987;g:132498;d:133264;g:133848;tap:134592;d:135347;g:135883;b:136292;h:136663;b:137004;h:137244;b:137640;tap:138360;d:138542;g:139152;d:139637;tap:140265;g:140400;d:140748;g:141003;b:141402;h:141869;tap:143139;tap:143558;tap:143758;h:143934;b:144187;h:144518;b:144788;tap:145439;h:145769;tap:146278;d:146677;g:147155;d:147385;tap:147835;d:148427;g:148624;d:149006;tap:149670;d:151445;g:152228;d:152859;g:153274;d:154221;d:154763;d:155223;d:155698;g:156108;g:156593;g:157045;b:157627;h:157867;b:158090;h:158432;tap:159123;tap:159785;tap:159985;d:160383;tap:161083;d:161482;g:161770;tap:162464;tap:162922;d:163348;g:163669;d:164076;tap:164508;tap:164876;b:165515;h:165752;tap:166272;tap:166760;b:167518;h:167944;b:168178;tap:168654;h:169293;b:169565;h:169849;tap:170235;tap:170435;d:171298;g:171706;tap:172086;tap:172286;tap:173124;d:173354;g:173609;tap:173982;tap:174182;d:174642;g:174847;d:175054;g:175514;tap:175819;tap:176019;d:176563;g:176793;d:177024;g:177331;tap:178022;b:178466;h:178661;b:178896;h:179226;tap:179910;b:180668;");
        sequences.add("tap:1126;tap:1614;tap:1814;tap:2333;tap:3188;d:3346;tap:3981;tap:4304;tap:4504;g:4815;d:4988;d:5494;g:5855;d:6256;tap:6859;tap:7311;d:7527;g:7862;tap:8225;tap:8425;d:8741;g:9157;d:9566;tap:10169;d:10377;g:10583;d:10952;tap:11493;tap:11693;h:12019;b:12457;h:12842;d:13141;tap:13920;d:14142;tap:14771;d:15030;g:15425;d:15801;g:16144;tap:16772;d:17008;g:17435;d:17843;tap:18198;tap:18398;d:18715;g:19528;tap:20132;h:20398;b:21330;h:21538;tap:21948;d:22371;g:22845;d:23172;g:23713;d:24118;g:24522;d:24919;g:25278;b:25698;h:26153;b:26897;h:27229;d:27850;g:28199;d:28742;g:29283;d:29651;tap:30277;d:30490;g:30925;d:31286;tap:31911;tap:32351;d:32542;g:32946;tap:33573;d:33804;g:34202;d:34506;tap:35225;b:35476;h:35848;b:36315;d:36697;g:37170;d:37533;b:37940;h:38297;b:38754;d:39172;tap:40162;d:40399;g:40825;d:41169;tap:41810;d:42017;g:42518;d:42877;tap:43504;d:43759;g:44194;d:44566;b:44951;h:45367;b:45816;d:46025;g:46288;d:46570;tap:47288;d:47449;tap:48076;d:48245;g:48468;tap:49095;d:49248;g:49486;tap:50113;d:50409;g:51352;tap:51730;tap:51930;tap:53380;d:53629;d:54119;tap:54665;tap:55106;d:55284;g:55621;d:56023;tap:56712;d:56943;g:57362;tap:58101;tap:58301;d:58617;g:59082;d:59369;tap:59860;d:60282;g:60707;tap:61248;tap:61448;b:61787;h:62235;tap:62920;b:63344;tap:64186;h:64393;tap:65010;h:65249;tap:65848;d:66071;tap:66697;g:66904;d:67353;g:67721;tap:68324;h:68512;b:68999;tap:69571;tap:69966;h:70159;b:70541;d:71279;g:72133;b:72967;h:73823;d:74639;g:75511;b:76298;h:76729;tap:77875;tap:78680;tap:80014;d:80219;g:80555;b:81414;h:81791;b:82197;h:82550;d:82958;g:83430;d:83832;b:84124;tap:84855;h:85098;b:85526;tap:86068;d:86328;g:86718;d:87113;tap:87477;tap:87677;d:88006;g:88396;d:88814;tap:89417;d:89673;g:90041;d:90422;tap:90792;tap:90992;b:91442;h:91715;b:92132;tap:92731;h:92982;b:93330;h:93768;tap:94134;tap:94334;d:94798;g:95356;tap:96016;d:96272;g:96694;d:97091;tap:97648;tap:97848;b:98221;h:98721;tap:99379;d:99608;g:100022;d:100390;tap:100934;d:101214;g:101648;d:102045;tap:102705;d:102911;g:103314;d:103689;tap:104316;d:104595;g:104964;d:105365;tap:105991;b:106251;h:106626;b:107030;tap:107483;tap:107683;d:108288;g:108722;tap:109218;tap:109418;d:109779;g:110325;d:110706;h:111131;b:111536;h:111945;b:112357;tap:112978;d:113235;g:113695;tap:114239;tap:114607;d:114898;g:115296;tap:115868;d:116100;g:116559;d:116986;g:117345;tap:117919;d:118181;tap:118808;tap:119241;d:119424;g:119867;d:120293;b:120676;h:121061;b:121487;tap:122133;d:122395;g:122747;d:123221;g:123598;d:123978;h:124372;d:124864;b:125219;g:125624;b:125988;h:126441;d:126816;g:127216;b:127642;h:128026;b:128468;d:129266;tap:129993;d:130956;tap:132929;d:133134;g:133548;tap:134093;d:134382;g:134739;d:135090;tap:135687;tap:135887;d:136170;g:136592;d:136839;tap:137422;d:137688;g:138061;d:138458;tap:139069;d:139319;g:139733;d:140101;tap:140514;tap:140714;d:141047;g:141385;d:141732;tap:142368;d:142621;g:143066;d:143421;tap:143881;tap:144081;d:144435;g:145070;b:145441;tap:146087;h:146363;tap:146942;h:147163;tap:147774;b:147969;tap:148602;d:148791;tap:149492;d:149645;tap:150282;tap:150694;d:150885;tap:151499;tap:151923;tap:152344;tap:152738;tap:153183;tap:153635;d:153800;tap:154414;g:154612;tap:155195;d:155394;tap:156066;d:156239;tap:156875;h:157097;tap:157704;b:157948;tap:158528;d:158721;tap:159334;h:159567;tap:160179;d:160436;tap:160960;tap:162710;tap:164316;tap:165430;");
        sequences.add("tap:1720;d:2214;d:2804;g:3301;tap:4019;d:4338;tap:5080;d:5444;tap:6132;tap:7152;d:7500;tap:8143;tap:8677;d:8947;g:9160;tap:9706;d:10095;tap:10783;d:11173;h:11654;tap:12315;d:12661;g:13200;tap:13827;d:14167;tap:14826;d:15277;g:15824;tap:16466;d:16569;tap:17026;d:17248;g:17480;tap:17996;d:18360;tap:18985;g:19387;d:19919;tap:20609;tap:21116;d:21453;h:21911;tap:22716;b:23814;h:24176;b:24492;tap:25219;d:25552;g:25847;tap:26314;tap:27201;d:27631;g:27885;d:28190;tap:28853;tap:29357;d:29687;g:30218;d:30824;tap:31427;b:31837;h:32313;tap:32978;tap:33470;d:33818;g:34339;tap:35584;d:36289;g:36544;d:36979;tap:37669;d:38010;g:38503;b:39111;tap:39722;tap:40738;d:41067;g:41329;d:41560;b:42105;h:42362;b:42675;tap:43798;tap:44099;tap:44299;tap:44641;tap:44841;tap:45452;tap:45948;d:46049;g:46280;b:46709;h:47295;b:47813;d:48323;g:48824;d:49331;g:49954;d:50404;tap:51094;h:51461;tap:52097;tap:52297;tap:52657;tap:52857;tap:53212;d:53562;g:54068;d:54589;tap:55250;b:55620;d:56338;g:56577;d:56891;g:57154;tap:57836;tap:58036;tap:58424;h:58652;b:58958;h:59225;tap:60319;tap:60627;tap:60934;tap:61241;tap:61667;tap:61867;d:62349;g:62619;d:62891;tap:63552;tap:64460;tap:64822;tap:65022;tap:65364;tap:65564;tap:65891;tap:66091;tap:66368;tap:66568;d:66928;g:67189;tap:67671;d:67999;tap:68720;g:69018;d:69581;g:70055;b:70382;h:70596;tap:71335;d:71508;tap:72344;tap:73328;d:73744;tap:74404;tap:74897;d:75152;g:75408;tap:75953;d:76276;tap:76904;g:77314;d:77773;tap:78493;tap:78760;tap:78960;d:79384;g:79639;d:79960;tap:80651;h:80852;tap:81599;h:81965;tap:82647;tap:82847;h:83031;tap:83639;tap:83839;g:84011;tap:84701;b:85137;tap:85759;h:86119;tap:86805;d:86923;tap:87339;g:87655;d:87885;tap:88326;g:88681;tap:89343;b:89750;h:90019;tap:90732;d:90934;tap:91450;d:91787;g:92365;d:92815;tap:93419;d:93824;tap:94274;tap:95523;d:95904;g:96175;d:96439;tap:97129;tap:97329;h:97473;tap:98444;tap:99662;d:99998;g:100278;tap:100744;d:101537;tap:102299;tap:102499;tap:102878;tap:103374;d:103731;g:104027;d:104234;g:104480;d:104786;g:105074;tap:105896;tap:106096;d:106501;g:106755;d:107027;g:107323;tap:108015;d:108532;g:108966;d:109334;tap:110032;tap:110232;tap:110567;tap:110767;tap:111085;tap:111285;tap:111586;tap:111786;tap:112141;d:112499;g:113045;d:113562;d:114361;g:115101;d:115577;h:115858;tap:116314;h:116580;tap:117298;b:117639;tap:118279;tap:118547;tap:118747;tap:119070;tap:119270;tap:119596;tap:119796;tap:120123;tap:120323;d:120485;g:120757;d:120971;tap:121446;h:121759;b:121993;tap:122434;tap:122634;tap:122970;tap:123170;tap:123528;tap:123728;tap:123981;tap:124181;tap:124507;tap:124707;d:124911;g:125174;d:125413;tap:126556;tap:126756;tap:127127;tap:127380;tap:127580;tap:127905;tap:128105;tap:128399;tap:128599;d:128770;g:129041;d:129247;g:129536;d:129854;g:130126;tap:130687;tap:130887;tap:131246;tap:131446;tap:131797;tap:131997;tap:132298;tap:132498;b:132877;h:133135;b:133358;tap:133870;d:134165;g:134601;d:135322;g:135512;d:136005;g:136211;tap:136931;d:137296;g:137583;d:137822;g:138145;d:138359;tap:139020;d:139400;g:139663;d:139968;h:140411;b:140632;tap:141132;h:141404;b:141717;h:141988;b:142543;tap:143154;d:143518;g:143772;d:144036;g:144559;d:144756;tap:145281;d:145575;g:145830;d:146078;b:146571;tap:147286;h:147610;b:147881;h:148828;tap:149388;b:149697;h:149947;tap:150445;d:150744;g:151024;tap:151498;d:151796;g:152051;d:152549;g:152779;tap:153257;tap:153457;d:153854;g:154116;d:154355;tap:155564;d:155945;g:156511;d:156912;tap:157405;tap:157605;d:157946;g:158208;d:158513;tap:159722;b:160159;h:160411;b:160861;h:161112;tap:161781;b:162152;h:162428;b:162668;tap:163766;d:164209;g:164447;d:164941;g:165221;tap:165911;d:166184;tap:166927;tap:167954;tap:168154;tap:168480;tap:168680;tap:168990;tap:169190;tap:169507;tap:169707;tap:170058;tap:170258;g:170421;d:170611;tap:171126;tap:172112;tap:172312;d:172698;g:173245;d:173466;tap:174157;d:174602;g:175037;d:175494;tap:176213;tap:176489;tap:176689;tap:177030;tap:177230;tap:177549;tap:177749;tap:178062;d:178159;g:179215;d:179549;b:180246;h:180619;d:181302;g:181823;d:182037;g:182342;d:182743;tap:183487;h:183875;tap:184494;tap:184780;tap:184980;tap:185321;tap:185521;tap:185864;tap:186064;tap:186325;tap:186525;g:186729;d:186927;b:187457;tap:188615;tap:188815;tap:189180;tap:189455;tap:189655;tap:189972;tap:190172;d:190285;g:190590;d:190812;tap:191789;h:192160;tap:192774;tap:192974;tap:193341;tap:193541;tap:193892;tap:194092;tap:194411;tap:194611;d:194764;g:195044;d:195258;tap:195977;h:196229;b:196766;h:197271;tap:197746;tap:197946;g:198118;d:198339;g:198611;d:198850;g:199147;d:199344;h:199625;b:199852;tap:201075;d:201903;g:202718;d:203141;");

        //ici news
        sequences.add("tap:875;tap:1075;tap:1439;tap:2076;tap:3994;tap:4194;d:4949;g:5245;d:5509;g:5774;d:6030;g:6337;d:6600;g:6944;d:7200;g:7498;d:7721;g:7977;d:8192;b:8453;d:8806;g:9125;d:9498;g:9795;d:10067;g:10340;d:10604;g:10877;h:11175;b:11464;h:11728;b:11997;h:12261;b:12511;h:12780;d:13080;g:13344;d:13583;g:13880;d:14195;g:14468;d:14724;g:14997;d:15245;g:15536;d:15807;g:16114;d:16394;g:16651;d:16915;g:17163;d:17435;tap:17910;d:18267;tap:19010;tap:19561;tap:20102;tap:20618;tap:21159;tap:21693;h:21827;tap:22293;tap:22827;tap:23362;tap:23866;h:23989;tap:24481;tap:25006;tap:25565;tap:26041;tap:26241;tap:26682;tap:27204;tap:27754;tap:28239;tap:28439;tap:28863;tap:29416;tap:29941;tap:30393;tap:30593;tap:31032;tap:31600;tap:32083;tap:32598;g:32702;d:32983;tap:33726;tap:34253;d:34637;tap:35382;d:35737;g:36288;d:36569;g:36824;d:37073;d:37400;d:37656;d:37920;d:38185;d:38474;d:38746;g:39027;d:39266;b:39539;b:39825;b:40080;b:40347;b:40657;b:40994;b:41257;b:41539;b:41827;b:42114;b:42490;b:42806;b:43102;b:43393;b:43622;h:43903;h:44181;h:44443;h:44726;h:44995;h:45265;h:45564;h:45880;h:46190;h:46446;h:46711;h:46975;h:47239;h:47487;h:47729;h:47969;tap:48398;tap:48598;g:48803;g:49108;g:49398;g:49670;g:49952;g:50231;tap:50636;tap:50836;g:51000;d:51238;g:51520;d:51776;g:52057;d:52329;tap:52812;d:53060;tap:53896;g:54237;tap:54782;tap:55521;d:55860;g:56157;d:56405;g:56662;tap:57177;d:57512;tap:58317;g:58617;d:58890;g:59598;d:60169;tap:60698;tap:60898;g:61036;tap:61534;d:61822;tap:62631;tap:63223;d:63464;g:63812;d:64060;g:64388;d:64677;tap:65131;tap:65331;tap:65651;tap:65851;g:66268;tap:66989;d:67345;g:67912;d:68338;g:68973;d:69254;g:69510;d:69808;h:70166;b:70651;h:71222;g:71748;d:71995;g:72726;d:73306;g:73617;d:73874;g:74122;b:74447;h:74955;b:75515;h:76095;b:76353;h:77081;b:77737;h:77989;b:78226;h:78481;g:78781;g:79332;d:79849;d:80454;d:80997;g:81499;d:82032;d:82351;d:82641;g:83131;d:83649;g:84226;d:84474;g:84781;b:85097;h:85370;b:85647;h:85913;b:86201;h:86446;g:86731;d:86971;g:87211;tap:87757;tap:92101;tap:92978;g:93178;d:93742;tap:96466;g:96855;d:97590;g:98140;b:98908;h:99809;b:100264;tap:100830;d:101155;g:101729;d:102199;tap:103007;tap:103557;tap:104114;tap:104641;g:104917;d:105501;d:106078;d:106629;tap:107356;tap:107917;tap:108485;tap:109037;tap:109562;tap:110112;tap:110679;g:110991;tap:111487;tap:111687;tap:112322;tap:112897;tap:113439;g:113696;tap:114440;tap:115033;tap:115567;d:115897;tap:116678;tap:117233;tap:117770;h:117996;tap:118857;d:119176;tap:119921;tap:120487;tap:121044;b:121437;tap:122090;g:122440;tap:123184;tap:123745;g:124048;tap:124854;tap:125403;d:125723;tap:126484;tap:127067;g:127364;tap:128130;tap:128647;tap:129213;tap:129779;tap:130305;d:130655;tap:131379;b:131698;tap:132477;tap:133010;tap:133594;tap:134115;h:134471;tap:135183;tap:135740;tap:136294;g:136681;d:137191;g:137758;tap:138480;tap:139036;tap:139603;g:139903;d:140414;g:140940;d:141757;d:142471;g:143121;d:143420;g:143701;d:143949;h:144234;b:144750;h:145299;h:146166;h:146891;b:147448;h:147815;b:148033;h:148274;g:148586;d:149193;h:149730;b:150551;h:151233;d:151808;g:152178;d:152433;g:152689;tap:153180;d:153492;g:154094;d:154350;tap:155071;d:155431;g:155676;d:156274;g:156537;d:156794;g:157050;b:157363;h:157912;b:158452;tap:159185;d:159530;tap:160251;g:160596;tap:161405;d:161757;tap:162477;g:162819;tap:163598;d:163954;tap:164673;g:165007;tap:165768;d:166058;b:166664;h:167193;tap:167945;d:168274;tap:168995;h:169322;tap:170099;h:170434;tap:171152;h:171510;g:171819;d:172317;g:173003;d:173653;g:173941;d:174222;tap:175003;d:175447;tap:176141;tap:176604;tap:176804;");
        sequences.add("tap:2632;tap:3204;tap:3776;tap:4261;tap:4710;tap:5201;tap:5716;d:5872;tap:6632;tap:7131;tap:7620;g:7805;tap:8548;tap:9031;tap:9531;d:9732;tap:10452;tap:10961;tap:11427;g:11676;tap:12368;tap:12907;tap:13356;d:13536;tap:14314;tap:14824;tap:15298;d:15538;tap:17685;d:18420;tap:19199;g:19471;d:19922;tap:21032;tap:22008;tap:22962;g:23705;d:24621;tap:25366;d:25631;d:26580;g:27523;d:28495;g:29446;d:30338;g:31343;d:32274;d:32563;tap:33110;g:33357;tap:34022;d:34268;tap:34989;h:35319;tap:35971;h:36204;tap:36871;h:37138;tap:37828;b:38145;tap:38865;b:39091;tap:39827;b:40061;tap:40697;h:40956;tap:41679;g:41936;tap:42683;g:42995;tap:43641;g:43892;tap:44536;d:44842;g:45356;d:45742;tap:46473;g:46761;tap:50598;g:52307;d:54737;g:55433;d:55689;g:56141;b:56783;h:57290;b:57548;tap:59031;tap:60417;g:62629;d:63118;g:63621;b:64548;h:64990;tap:65702;tap:66218;tap:66702;tap:67144;tap:67629;tap:68087;g:68393;d:68616;g:68856;d:69095;b:69814;tap:70534;tap:70990;tap:71464;tap:71981;tap:72434;g:72715;d:72946;g:73219;tap:73884;tap:74350;tap:74843;tap:75307;h:75630;tap:76289;tap:76753;b:77063;tap:77677;d:78528;g:79770;d:80354;tap:81074;d:81338;tap:82032;d:82233;tap:82934;g:83231;tap:83952;g:84233;tap:84878;d:85193;tap:85885;b:86110;tap:86781;b:86980;tap:87716;b:87981;tap:88733;h:88956;tap:89666;tap:90176;tap:90680;h:90943;tap:91651;tap:92110;tap:92613;b:92908;tap:93621;tap:94017;tap:94535;tap:94984;g:95233;d:95722;g:95961;b:96259;h:96707;b:97175;h:97652;h:98121;h:98615;b:99112;b:99580;b:100083;g:100496;tap:101218;d:101465;tap:102211;d:102426;tap:103147;tap:103644;g:103918;tap:104619;tap:105150;d:105362;tap:106007;tap:106493;b:106773;tap:107465;h:107759;tap:108410;tap:108917;d:109209;g:109677;d:111703;g:112984;tap:113685;g:113951;tap:114652;d:114960;h:115410;tap:116105;h:116403;tap:117027;h:117273;tap:117968;h:118269;tap:118940;h:119262;tap:119900;h:120202;tap:120835;h:121196;tap:121799;h:122119;tap:122768;h:123080;tap:123703;b:123983;tap:124706;b:125042;tap:125645;tap:125845;d:126196;g:126501;tap:127138;d:127388;tap:128090;g:128363;tap:129034;d:129299;tap:130030;b:130223;tap:130945;g:131245;tap:131880;h:132135;tap:132815;d:133053;tap:133815;tap:134015;tap:134348;d:134642;tap:135246;g:135511;tap:136203;d:136540;tap:137143;g:137459;tap:138122;b:138446;tap:139075;h:139368;b:139788;d:140385;tap:141076;d:142723;g:144615;b:146784;h:147713;d:148919;g:149472;d:149719;b:150838;h:152779;b:153082;g:155317;d:155568;b:156543;h:157067;tap:157805;d:158109;tap:158802;tap:159305;tap:159832;tap:160257;g:160516;tap:161206;tap:161699;tap:162182;tap:162673;tap:163156;tap:163624;tap:164134;d:164449;tap:165076;tap:165601;tap:166081;tap:166540;tap:166982;g:167256;tap:167920;tap:168409;h:168740;tap:169368;tap:169861;b:170126;h:170617;b:171091;h:171607;tap:172221;tap:172664;tap:172864;tap:173236;tap:173725;tap:174184;d:174289;g:174503;tap:174883;tap:175083;g:175204;d:175427;g:175658;tap:176055;tap:176255;tap:176533;tap:176733;tap:177013;tap:177213;tap:177498;tap:177698;tap:178001;tap:178201;tap:178478;tap:178678;tap:178950;tap:179150;tap:179412;tap:179612;tap:179905;tap:180105;tap:180412;tap:180899;tap:181409;tap:181888;tap:182377;tap:182828;g:183077;tap:183768;d:184075;tap:184704;tap:185200;tap:185714;d:185908;g:186222;d:186470;g:186964;b:187437;h:187871;b:188074;h:188387;h:188867;h:189372;h:189806;h:190321;h:190778;g:191286;d:191527;g:191749;d:191957;g:192630;d:193165;g:193641;d:194122;g:194616;d:195205;g:195623;d:196098;g:196572;d:197087;h:197502;b:197708;h:197974;tap:198656;tap:199152;tap:199669;tap:200107;tap:200582;tap:201067;tap:201517;tap:202033;b:202335;h:202786;b:203735;g:205683;d:206309;g:206632;d:206947;g:207245;b:207532;h:207883;g:210557;d:210813;g:211364;d:211738;g:212189;d:212395;g:212684;b:212973;h:213347;b:213668;h:214030;b:214301;h:214597;g:214927;d:215263;g:215881;tap:219306;g:219583;tap:220247;g:220502;tap:221225;d:221496;tap:222160;d:222428;tap:223120;b:223421;tap:224084;g:224335;tap:225027;h:225252;tap:225937;d:226244;tap:226966;b:227229;tap:227913;g:228153;tap:228913;h:229149;tap:229836;d:230061;tap:230752;b:231033;tap:231749;g:232075;tap:232704;d:233064;g:233482;d:233772;tap:234434;tap:235043;tap:235585;tap:236114;tap:236584;tap:237052;g:237343;d:237565;g:237805;tap:238498;tap:238986;tap:239470;tap:239947;tap:240455;tap:240921;h:241191;b:241425;h:241654;b:241873;tap:242321;tap:242818;tap:243314;d:243623;tap:244315;g:244505;tap:245249;tap:245724;tap:246197;tap:246707;tap:247197;tap:247640;d:247881;g:248146;d:248376;b:248647;h:248874;b:249113;h:249350;tap:249748;tap:249948;d:250271;tap:251018;tap:251443;tap:251949;g:252190;tap:252855;tap:253055;h:253181;b:253405;tap:253856;g:254122;d:254643;d:255141;d:255602;tap:256331;h:256587;b:256794;h:257022;tap:257676;g:257956;g:258533;g:258968;g:259429;d:259927;tap:260629;h:260878;b:261120;tap:261582;tap:262045;tap:262541;tap:263038;tap:263483;d:263756;g:264207;d:264447;g:264695;tap:265396;b:266190;h:267055;d:267971;g:268267;d:268498;g:268763;b:269452;h:269934;b:270890;d:271850;g:272130;d:272370;g:272610;b:273252;h:273602;b:274271;h:274503;d:275108;g:275724;d:276207;g:276463;b:277710;");
        sequences.add("tap:1140;g:2128;d:3558;g:5197;d:6376;g:7908;d:9252;g:10768;d:12174;b:13521;h:15244;b:16700;g:18270;d:19823;g:21316;h:22702;b:24330;g:25764;d:27205;h:28613;g:30271;d:31904;b:33275;h:34761;d:36203;g:36741;b:37743;h:39252;d:40695;g:42199;d:43682;b:45309;h:46813;b:48331;h:49705;g:50513;b:51376;d:52005;b:52770;g:53462;b:54335;d:55047;h:55889;g:56448;b:57323;d:58047;h:58805;g:59536;d:60274;g:60994;d:61390;g:61804;d:62148;g:62521;d:62905;b:63286;h:63705;b:64452;h:64804;b:65170;h:65538;d:66229;g:67004;d:67369;g:67795;d:68168;g:68561;d:68883;b:69302;h:70029;b:70395;h:70769;d:72280;g:73318;d:73780;tap:75455;d:75778;g:76067;d:76365;tap:76939;g:77244;d:77583;g:77951;tap:78534;b:78748;h:79089;b:79455;tap:79973;d:80229;g:80560;d:80929;tap:81542;g:82123;d:82472;g:82858;d:83576;g:83940;d:84296;b:85698;h:87187;g:88060;d:88389;tap:88974;b:89997;h:90333;b:90688;h:91086;d:91475;g:91805;tap:92711;d:92857;g:93257;d:93603;g:94023;tap:94530;tap:94965;d:95505;g:95848;tap:96411;tap:97981;g:99191;d:100763;b:102279;h:102877;b:103817;g:105238;d:106684;b:107543;h:107897;tap:108474;d:109762;g:110211;d:110401;g:110883;d:111063;g:111344;b:111659;h:111875;b:112076;h:112395;b:112587;h:112766;g:113048;d:113205;g:113454;b:113918;h:114125;b:114357;g:114662;d:114851;g:115066;d:115364;g:115774;d:115956;g:116178;b:116517;h:116880;b:117273;h:117660;b:117844;h:118009;b:118357;h:118770;b:119180;h:119374;b:119574;h:119907;g:120264;d:120632;g:121066;d:121393;g:121774;d:122114;g:122481;d:122859;b:123223;h:123637;b:124009;h:124409;b:124770;h:124973;b:125174;h:125348;b:125565;h:125858;g:126288;d:126619;g:127052;d:127379;g:127789;d:128124;g:128356;d:128537;tap:129113;d:129273;tap:129881;d:130035;tap:130638;d:130825;tap:131369;d:131557;tap:132149;tap:132349;b:133022;h:133372;b:133743;h:134219;b:134511;d:134888;g:135236;g:135646;g:136028;g:136407;g:136744;d:136933;g:137164;d:137354;g:137586;g:137930;b:138279;h:138637;b:138853;h:139038;h:139346;h:139755;h:140121;b:140357;h:140533;h:140911;h:141267;b:141630;h:141834;b:142025;h:142335;g:142771;d:143129;g:143517;d:143859;tap:144432;g:144636;d:144988;g:145353;d:145830;g:146144;d:146342;g:146542;d:146724;g:146937;g:147288;g:147680;d:147886;g:148117;b:148383;h:148734;b:148952;h:149151;b:149527;h:149884;h:150227;b:150611;h:150816;b:151050;h:151427;b:151787;h:152157;d:152544;g:152876;d:153224;d:153650;g:153841;d:154074;g:154436;d:154809;d:155235;d:155625;d:155948;g:156256;d:156620;g:156817;d:157041;g:157364;d:157562;g:157794;d:158142;g:158648;d:158929;g:159294;b:159899;h:160093;b:160413;h:160799;b:161450;h:162266;g:163698;d:165223;b:166698;h:168215;g:169546;d:170334;b:171237;g:171917;d:172666;b:173534;h:173861;b:174218;d:174959;g:175686;b:176444;h:177203;g:177896;d:178243;g:178654;b:179400;h:179830;g:180224;d:180951;g:181314;b:181711;h:182195;b:182561;h:182868;g:183242;d:183959;g:184323;b:184696;h:185532;b:185864;g:186271;d:187009;g:187348;b:187780;h:188535;b:188876;g:189316;d:190008;g:190381;b:190779;tap:191331;h:191512;b:191859;g:192240;d:192999;g:193388;b:193790;h:194866;g:195264;d:195947;g:196303;b:196699;tap:197154;h:197823;b:198257;tap:199227;h:199357;tap:199961;b:200136;d:200496;g:200847;tap:201422;d:202024;g:202347;tap:202921;d:203112;g:203518;d:203815;tap:204421;d:204791;g:205098;d:205361;tap:205990;d:206171;g:206527;d:206825;tap:207452;g:207638;d:207861;g:208093;d:208390;tap:209018;h:209187;b:209380;h:209572;d:209907;tap:210481;d:210712;tap:211237;d:211411;tap:211994;d:212185;tap:212760;d:212934;g:213295;d:213680;g:214015;d:214329;tap:214997;h:215152;b:215520;h:215848;tap:216448;b:216703;tap:217240;b:217451;tap:218017;h:218175;b:218549;h:218902;tap:219497;b:219692;h:220052;b:220421;tap:221011;d:221180;g:221578;d:221927;tap:222472;b:222683;h:222867;b:223116;h:223368;tap:223983;h:224139;b:224583;h:224884;tap:225500;g:225649;d:225847;g:226089;d:226377;tap:226951;g:227134;d:227496;g:227848;tap:228460;b:228731;h:228884;b:229136;h:229416;tap:229979;g:230114;d:230340;g:230568;d:230874;tap:231486;b:231651;h:231857;b:232115;h:232365;tap:232970;g:233130;d:233328;g:233576;d:233841;tap:234473;b:234667;h:234860;b:235089;h:235415;tap:236014;d:236163;g:236395;d:236893;g:237292;b:238074;h:238386;b:238761;h:239147;b:239514;h:239814;d:240214;g:240665;d:240946;g:241361;b:241797;h:242106;b:242497;h:242846;tap:243444;d:243585;g:243798;d:244006;g:244383;tap:244992;tap:245192;g:245297;d:245521;g:245873;tap:246474;tap:246674;g:246779;d:247010;g:247405;tap:248012;tap:248201;tap:248401;g:248505;d:248857;d:249277;g:249595;d:249785;g:249986;g:250373;d:250722;d:251020;d:251260;d:251519;d:251885;g:252268;b:253710;h:254805;d:255264;g:255650;d:256067;g:256395;b:256812;h:257161;b:257517;h:257877;d:258253;g:258677;d:258859;g:259049;d:259356;b:259742;h:260466;b:260865;d:261210;g:261718;d:262014;g:262338;b:262742;tap:263366;g:263538;d:263843;g:264286;h:265711;g:266394;d:266691;g:266964;b:267290;h:267921;b:268695;h:269077;b:269506;h:269823;tap:270409;d:270617;g:271023;tap:271920;d:272113;g:272474;d:272826;tap:273430;d:273658;g:273990;d:274347;b:274705;h:275172;b:275493;h:275827;g:276290;b:277007;h:277326;h:277772;d:278139;g:278487;d:278809;d:279220;b:279948;h:280705;b:281070;h:281465;b:281795;g:282228;tap:282973;d:283220;g:283829;d:284197;tap:284770;d:285358;tap:286281;g:286710;d:288329;b:289745;h:290285;b:290593;h:290889;g:291266;d:292384;g:292769;d:293113;g:293482;d:293842;tap:294417;d:295397;g:295795;d:296155;b:296534;h:296867;tap:297434;d:298365;g:298816;b:299589;h:299894;tap:300488;d:300729;g:301115;d:301468;tap:302042;g:302658;d:302921;tap:303496;b:303684;h:304112;g:304767;d:305133;g:305501;b:305829;tap:306454;d:306989;g:307823;d:308688;g:309422;b:309780;h:310399;b:310779;tap:311727;h:312085;b:312733;h:313188;b:313465;h:313777;tap:314398;d:314506;g:314846;tap:315421;d:315827;g:316753;d:317211;g:317597;d:317887;b:318264;h:319066;b:319835;h:321161;g:321881;d:322425;g:322755;b:323514;h:324565;b:325699;h:326462;b:327233;tap:328202;tap:328959;g:329258;d:329555;tap:330451;tap:331202;tap:331937;tap:332730;g:333279;d:334016;g:334777;b:336950;");

        go = findViewById(R.id.go);
        go.setVisibility(View.INVISIBLE);



        go.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                //  zoneJeu = (RelativeLayout)findViewById(R.id.bandeaupourfleche);

                ViewTreeObserver vto = zoneJeu.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                      //  zoneJeu.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        widthZoneJeu = zoneJeu.getMeasuredWidth();
                        heightZoneJeu = zoneJeu.getMeasuredHeight();

                    }
                });
                animationEstLancee = true;
                go.setVisibility(View.INVISIBLE);
                // rondCentral.setVisibility(View.VISIBLE);
                barregauche=findViewById(R.id.barrelimitegauche);
                barreHorizontal1=findViewById(R.id.barreHorizontale1);
                barreHorizontal2=findViewById(R.id.barreHorizontale2);
                barreHorizontal3=findViewById(R.id.barreHorizontale3);
                barreHorizontal4=findViewById(R.id.barreHorizontale4);
               RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)barregauche.getLayoutParams();
                params.setMargins(70*widthZoneJeu/100, 0, 0, 0); //substitute parameters for left, top, right, bottom
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)barreHorizontal1.getLayoutParams();
                params1.setMargins(0, 20*heightZoneJeu/100, 0, 0);
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)barreHorizontal2.getLayoutParams();
                params2.setMargins(0, 40*heightZoneJeu/100, 0, 0);
                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams)barreHorizontal3.getLayoutParams();
                params3.setMargins(0, 60*heightZoneJeu/100, 0, 0);
                RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams)barreHorizontal4.getLayoutParams();
                params4.setMargins(0, 80*heightZoneJeu/100, 0, 0);
                barregauche.setLayoutParams(params);
                barreHorizontal1.setLayoutParams(params1);
                barreHorizontal2.setLayoutParams(params2);
                barreHorizontal3.setLayoutParams(params3);
                barregauche.getLayoutParams().width=10*widthZoneJeu/100;
                barregauche.setVisibility(View.VISIBLE);
                barreHorizontal1.setVisibility(View.VISIBLE);
                barreHorizontal2.setVisibility(View.VISIBLE);
                barreHorizontal3.setVisibility(View.VISIBLE);
                barreHorizontal4.setVisibility(View.VISIBLE);



                //TODO: init MediaPlayer and play the audio

                //get the AudioSessionId from your MediaPlayer and pass it to the visualizer



                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 10 seconds
                                Intent j = new Intent(getApplicationContext(), ResultatsPartie.class);
                                j.putExtra("niveau", pos);
                                j.putExtra("score", score);
                                j.putExtra("nbTotalMvts", 2*couples.length);
                                j.putExtra("manques", 2*couples.length - score);
                                mPlayer.reset();
                              //  mPlayer.release();

                                startActivity(j);
                                finish();
                                toast.cancel();
                                return;
                            }
                        }, 1000);

                    }
                });

                lancerAnimation(sequence);


// previously invisible view


// Check if the runtime version is at least Lollipop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // get the center for the clipping circle
                    int cx =vOverlay.getWidth() / 2;
                    int cy = vOverlay.getHeight() / 2;

                    // get the final radius for the clipping circle
                    float finalRadius = (float) Math.hypot(cx, cy);

                    // create the animator for this view (the start radius is zero)
                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(vOverlay, cx, cy, 0, finalRadius);

                    // make the view visible and start the animation
                    vOverlay.setVisibility(View.VISIBLE);
                    anim.start();
                } else {
                    // set the view to visible without a circular reveal animation below Lollipop
                    vOverlay.setVisibility(View.VISIBLE);
                }

            }
        });

        //  sequence = genererSequenceAleatoire(pos);
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


         listeUrl=new ArrayList<>();
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
        listeUrl.add("firebase_petit-pantin");
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

        //chargement musique
        File mFolder = new File(getFilesDir() + "/Music");
        extensionFichier=obtenirExtensionFichier(listeUrl,pos);

        File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+extensionFichier);
        String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+extensionFichier;
       if(file.exists()){
           //on recupère le fichier depuis le repertoire
           go.setVisibility(View.VISIBLE);

           mPlayer = MediaPlayer.create(this, Uri.parse(filePath));

       }

       else {

                //on télécharge
// instantiate it within the onCreate method


           mProgressDialog = new ProgressDialog(InGame.this);

           mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
           mProgressDialog.setMessage("Téléchargement de la musique");
           mProgressDialog.setIndeterminate(true);

           mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
           final DownloadTask downloadTask = new DownloadTask(InGame.this);
           if(!listeUrl.get(pos - 1).contains("firebase")){
               downloadTask.execute(listeUrl.get(pos-1));


               mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                   @Override
                   public void onCancel(DialogInterface dialog) {
                       downloadTask.cancel(true);
                       File mFolder = new File(getFilesDir() + "/Music");

                       File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+extensionFichier);
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
               File file2 = new File(mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier);
               String nomFichierSurFirebase=listeUrl.get(pos - 1).split("_")[1];
               StorageReference refFichier=mStorageRef.child(nomFichierSurFirebase);

               refFichier.getFile(file2)
                       .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                               // Successfully downloaded data to local file
                               // ...
                               go.setVisibility(View.VISIBLE);

                               File mFolder = new File(getFilesDir() + "/Music");
                               String filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier;
                               mProgressDialog.dismiss();
                               mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(filePath));
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

       }

        indiceActuelSequence = -1;
        indicesequence=0;

        score = 0;
        mChronometer = new Chronometer(this);
        derniereAction = new StringModified();
        timeOfDerniereAction = 0;
        //symboleT=(RelativeLayout) findViewById(R.id.symbole);
        //  symboleUtilisateur=(RelativeLayout) findViewById( R.id.symboleMis);

        scoreT = (TextView) findViewById(R.id.score);
        scoreT.setText(String.valueOf(score));
        elementActuelClickable = false;//vaut true s'il ya des mouvements affichés surl'ecran


        //   Toast.makeText(InGame.this, getApplicationContext().getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
//flecheactuelle = findViewById(R.id.flecheactuelle);

        //dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);

        //dernierSymbole=sequence.get(0);
        dernierSymbole = ((sequences.get(pos - 1)).split(";")[0]).split(":")[0];
       // dernierSymbole = (sequences.get(pos - 1).split(";")[0]).split(":")[0];

        //isDrawerOpen = false;

        //  mDrawer = (DrawerLayout) findViewById(R.id.drawer_layoutingame);

       // mTopToolbar = (Toolbar) findViewById(R.id.ingame_toolbar);
//        setSupportActionBar(mTopToolbar);
//        // These lines are needed to display the top-left hamburger button
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);




        derniereAction.setValueChangeListener(new StringModified.onValueChangeListener() {
            @Override
            public void onChange() {

                LinearLayout layout = new LinearLayout(InGame.this);
                //  layout.setBackgroundResource(R.color.orange);


                // indiceActuelSequence += 1;
                timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                //tmin=listeTminToClick.get(indiceActuelSequence);
                // elementActuelClickable = false;
//                if (listeImagesAnimationsActives.contains(derniereAction.getValeur())) {
//                    elementActuelClickable = true;
//
//                } else {
//                    elementActuelClickable = false;
//                }



                    if (listeIndicesActuels.size()>0) {
                        elementActuelClickable = true;
                    } else {
                        elementActuelClickable = false;
                    }
                    if (elementActuelClickable) {


                        //Toast.makeText(InGame.this, String.valueOf(elementActuelClickable), Toast.LENGTH_SHORT).show();

                        dernierSymbole = ((sequences.get(pos - 1)).split(";")[listeIndicesActuels.get(0)]).split(":")[0];

                        if (dernierSymbole.equals(derniereAction.getValeur())) {


                            //  Toast.makeText(InGame.this, "Amaz", Toast.LENGTH_SHORT).show();


                            if (listeIndicesActuels.get(0)<= couples.length - 1) {

                                int precision=0;//1 correspond à perfect, 2 à normal et 0 à aucun rythme
                                precision=verifierBonRythmeJoueur();
                                if (precision!=0) {

                                    if(barreVie!=10 && barreVie!=0){
                                        barreVie+=1;

                                    }
                                    actualiserScore(precision);
                                   // progress.setProgress(score * 100 / (2*couples.length));
                                    progress.setProgress(barreVie*100/10);
                                    //actualiser score en conséquence

                                }
                                else{
                                    if (listeIndicesActuels.size() > 0) {

                                          zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);
                                        textPerfect.setText("Mauvais timing!");
                                        textPerfect.setTextColor(getResources().getColor(R.color.colorRed_A400));
                                        textPerfect.setVisibility(View.VISIBLE);


                                        textPerfect.startAnimation(animBounce);

                                    }
                                }
                                // dernierSymbole = sequence.get(indiceActuelSequence);

                                //Toast.makeText(InGame.this, dernierSymbole, Toast.LENGTH_SHORT).show();

                            } else if (listeIndicesActuels.get(0) > couples.length - 1) {
                                //    Toast.makeText(InGame.this, "Jeu terminé", Toast.LENGTH_SHORT).show();
                                //  zoneJeu.removeAllViews();
                                //  mPlayer.stop();
                            }
                        } else {

                            textPerfect.setText("Nope!");
                            textPerfect.setTextColor(getResources().getColor(R.color.colorRed_A400));
                            textPerfect.setVisibility(View.VISIBLE);


                            textPerfect.startAnimation(animBounce);

                            //   Toast.makeText(InGame.this, "Erreur mouvement", Toast.LENGTH_SHORT).show();
                            if (listeIndicesActuels.size() > 0) {

                                zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);


                            }


                        }



                        if (listeIndicesActuels.size() > 0) {

                            listeIndicesActuels.remove(0);

                        }
                } else {
                    //   Toast.makeText(InGame.this, "Jeu terminé", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch1 listener to the view
        // The touch1 listener passes all its events on to the gesture detector

        vOverlay.setOnTouchListener(touchListener);


        vOverlay.post(new Runnable() {
            @Override
            public void run() {
                //zoneJeu= findViewById(R.id.bandeaupourfleche);
                // widthView = v.getWidth(); //height is ready
                // widthZoneJeu = zoneJeu.getWidth();
                //   heightZoneJeu = zoneJeu.getHeight();
            }
        });

        widthZoneJeu = zoneJeu.getWidth();
        heightZoneJeu = zoneJeu.getHeight();

        zoneJeu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                zoneJeu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                heightZoneJeu = zoneJeu.getHeight(); //height is ready
                widthZoneJeu = zoneJeu.getWidth();


            }
        });

        mImageView = new ImageView(getApplicationContext());


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

    public int verifierBonRythmeJoueur() {

       int precision=0;

        int tempsCorrectPourActionEnRyhtme = Integer.valueOf(couples[listeIndicesActuels.get(0)].split(":")[1]);
       // Toast.makeText(InGame.this, String.valueOf(SystemClock.elapsedRealtime() - mChronometer.getBase() - tempsCorrectPourActionEnRyhtme), Toast.LENGTH_LONG).show();
        long diff=SystemClock.elapsedRealtime() - mChronometer.getBase() - tempsCorrectPourActionEnRyhtme;

            if (Math.abs(diff) < 300 ) {
                precision = 1;
                //normal
            } else if (Math.abs(diff)< 500) {
                //1000 ms=1s
                //perfect
                precision = 2;
            }
            if(listePerfect.get(listeIndicesActuels.get(0))==true){
                //perfect
                precision=1;
            }
            else{
                //normal
                precision=2;
            }

        return precision;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationEstLancee) {
            mPlayer.seekTo(media_length);
            mPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer!=null ) {

            try {
                //erreur ici sur LA CONdition isPlaying
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    media_length = mPlayer.getCurrentPosition();
                }

            }
            catch (Exception e){

            }
        }
        toast.cancel();
    }
    @Override
    protected void onStop () {
        super.onStop();
        toast.cancel();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            if(handler_stopperPartie_vieTerminee!=null){
                handler_stopperPartie_vieTerminee.removeCallbacks(stopperPartie_vieTerminee);
            }
            aQuitteJeu=true;
            mPlayer.stop();
            mPlayer.release();

            Intent i = new Intent(this, EcranAccueil.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            toast.cancel();


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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        heightP = mImageView.getHeight();
        widthP = mImageView.getWidth();
        mImageView.setVisibility(View.GONE);
        //   Toast.makeText(InGame.this, String.valueOf(heightP), Toast.LENGTH_SHORT).show();
    }

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
                go.setVisibility(View.VISIBLE);

                File mFolder = new File(getFilesDir() + "/Music");
                String filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + extensionFichier;

                mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(filePath));

            }
        }
    }


    // In the SimpleOnGestureListener subclass you should override
// onDown and any other gesture that you want to detect.

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        ImageView i = new ImageView(getApplicationContext());
        //   View viewOverLay=findViewById(R.id.viewOverlay);
        //  GestureOverlayView v=findViewById(R.id.gOverlay);


        @Override
        public boolean onDown(MotionEvent event) {


            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            new ParticleSystem(InGame.this, 20, R.drawable.notemusique, 30000)
                    .setSpeedByComponentsRange(-0.5f, 0.5f, 0f, 0.5f)
//                        .setAcceleration(0.00005f, 45)
                    .oneShot(findViewById(R.id.gOverlay), 5);
            rippleBackground=(RippleBackground)findViewById(R.id.content);
           // rippleBackground.clearAnimation();
            rippleBackground.startRippleAnimation();
          //  rippleBackground.stopRippleAnimation();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                   rippleBackground.stopRippleAnimation();
                }
            }, 600);

            // .emit(x,y,5,1000);

            derniereAction.setVariable("tap");
            Flubber.with()
                    .animation(Flubber.AnimationPreset.POP) // Slide up animation

                  //  .repeatCount(1)                              // Repeat once
                    .duration(300)                              // Last for 1000 milliseconds(1 second)
                    .createFor(carre)                             // Apply it to the view
                    .start();
            //  Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

           // derniereAction.setVariable("touch1");

            //  Toast.makeText(InGame.this, "touch1", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            new ParticleSystem(InGame.this, 20, R.drawable.notemusique, 30000)
                    .setSpeedByComponentsRange(-0.5f, 0.5f, 0f, 0.5f)
//                        .setAcceleration(0.00005f, 45)
                    .oneShot(findViewById(R.id.gOverlay), 5);

            // .emit(x,y,5,1000);

            Flubber.with()
                    .animation(Flubber.AnimationPreset.POP) // Slide up animation

                    .repeatCount(1)                              // Repeat once
                    .duration(300)                              // Last for 1000 milliseconds(1 second)
                    .createFor(carre)                             // Apply it to the view
                    .start();
            rippleBackground=(RippleBackground)findViewById(R.id.content);
            rippleBackground.clearAnimation();
            rippleBackground.startRippleAnimation();
            //  rippleBackground.stopRippleAnimation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    rippleBackground.stopRippleAnimation();
                }
            }, 800);
            derniereAction.setVariable("tap");
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    derniereAction.setVariable("tap");
                }
            }, 200);

            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (Math.abs(event2.getY() - event1.getY()) < Math.abs(event2.getX() - event1.getX())) {


                if (event2.getX() - event1.getX() > 0) {
                    derniereAction.setVariable("g");

                    //   Toast.makeText(InGame.this, "left", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.notemusique2, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(0.5f, 0.5f, 0f, 0.0f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.colorPurple_900));

                    Flubber.with()
                            .animation(Flubber.AnimationPreset.FADE_IN_LEFT) // Slide up animation

                            //  .repeatCount(1)                              // Repeat once
                            .duration(300)                              // Last for 1000 milliseconds(1 second)
                            .createFor(carre)                             // Apply it to the view
                            .start();
                } else if (event2.getX() - event1.getX() < 0) {
                    derniereAction.setVariable("d");

                    // Toast.makeText(InGame.this, "right", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.notemusique2, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.5f, -0.5f, 0f, 0.0f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    Flubber.with()
                            .animation(Flubber.AnimationPreset.FADE_IN_RIGHT) // Slide up animation

                            //  .repeatCount(1)                              // Repeat once
                            .duration(300)                              // Last for 1000 milliseconds(1 second)
                            .createFor(carre)                             // Apply it to the view
                            .start();
                }
            } else if (Math.abs(event2.getX() - event1.getX()) < Math.abs(event2.getY() - event1.getY())) {


                if (event2.getY() - event1.getY() > 0) {

                    derniereAction.setVariable("h");

                    //    Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.notemusique2, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.0f, 0.0f, 0.5f, 0.5f)
//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                  //  vOverlay.setBackgroundColor(getResources().getColor(R.color.cyan));

                    Flubber.with()
                            .animation(Flubber.AnimationPreset.FADE_IN_UP) // Slide up animation

                            //  .repeatCount(1)                              // Repeat once
                            .duration(200)                              // Last for 1000 milliseconds(1 second)
                            .createFor(carre)                             // Apply it to the view
                            .start();

                } else if (event2.getY() - event1.getY() < 0) {

                    derniereAction.setVariable("b");

                    //   Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.notemusique2, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.0f, 0.0f, -0.5f, -0.5f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                  //  vOverlay.setBackgroundColor(getResources().getColor(R.color.bleu1));

                    Flubber.with()
                            .animation(Flubber.AnimationPreset.FADE_IN_DOWN) // Slide up animation

                            //  .repeatCount(1)                              // Repeat once
                            .duration(200)                              // Last for 1000 milliseconds(1 second)
                            .createFor(carre)                             // Apply it to the view
                            .start();

                }
            }
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_ingame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }



   public  class MyAnimatorListener implements Animation.AnimationListener {







       @Override
       public void onAnimationStart(Animation animation) {

       }

       @Override
       public void onAnimationEnd(Animation animation) {
           textPerfect.setVisibility(View.INVISIBLE);
       }

       @Override
       public void onAnimationRepeat(Animation animation) {

       }
   }

    public void actualiserScore(int precision) {
        //actualise int score

        final int indiceActuel=listeIndicesActuels.get(0);
        if(precision==1){
            //perfect
            score+=2;
          //  if(textPerfect.getVisibility()==View.INVISIBLE) {
                textPerfect.setText("Parfait!");
                textPerfect.setTextColor(getResources().getColor(R.color.colorPurple_A400));
                textPerfect.setVisibility(View.VISIBLE);
          //  }

            textPerfect.startAnimation(animBounce);
          //  zoneJeu.getChildAt(listeIndicesActuels.get(0)).setBackground(getDrawable(R.drawable.emoji_muscle));

            if (listeIndicesActuels.size() > 0) {
                ((ImageView)zoneJeu.getChildAt(listeIndicesActuels.get(0))).setImageDrawable(getDrawable(R.drawable.plusdeux2));
             //   zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        zoneJeu.getChildAt(indiceActuel).setVisibility(View.GONE);
                    }
                }, 600);
            }


        }
        else if(precision==2){
            score += 1;
         //   if(textPerfect.getVisibility()==View.INVISIBLE) {
                textPerfect.setText("Cool!");
                textPerfect.setTextColor(getResources().getColor(R.color.colorAccent));
                textPerfect.setVisibility(View.VISIBLE);

        //    }

            textPerfect.startAnimation(animBounce);
            if (listeIndicesActuels.size() > 0) {
                ((ImageView)zoneJeu.getChildAt(listeIndicesActuels.get(0))).setImageDrawable(getDrawable(R.drawable.plusun));
                //  zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                          zoneJeu.getChildAt(indiceActuel).setVisibility(View.GONE);
                    }
                }, 600);
            }
 //zoneJeu.getChildAt(listeIndicesActuels.get(0)).setBackground(getDrawable(R.drawable.emoji_langue));


        }


        scoreT.setText(String.valueOf(score));

         if(score>=Math.round((int)(0.8*2*couples.length))  ){
             if(actualiserTrophy==true) {
                 nextTrophy.setImageDrawable(getDrawable(R.drawable.trophy_or));
                 actualiserTrophy = false;
                 nextPalierScore.setText(String.valueOf(Math.round((int) (0.95 * 2 * couples.length))));
                 Flubber.with()
                         .animation(Flubber.AnimationPreset.WOBBLE) // Slide up animation
                         .repeatCount(1)                              // Repeat once
                         .duration(1000)                              // Last for 1000 milliseconds(1 second)
                         .createFor(nextTrophy)                             // Apply it to the view
                         .start();
             }
        }
         else if(score>=Math.round((int)(0.75*2*couples.length)) ){
             if(actualiserTrophy==false) {
                 nextTrophy.setImageDrawable(getDrawable(R.drawable.trophy_argent));
                 nextPalierScore.setText(String.valueOf(Math.round((int) (0.8 * 2 * couples.length))));
                 actualiserTrophy = true;
                 Flubber.with()
                         .animation(Flubber.AnimationPreset.WOBBLE) // Slide up animation
                         .repeatCount(1)                              // Repeat once
                         .duration(1000)                              // Last for 1000 milliseconds(1 second)
                         .createFor(nextTrophy)                             // Apply it to the view
                         .start();
             }
         }



    }


    public ArrayList<String> genererSequenceAleatoire(int niveau) {
        ArrayList<String> sequence = new ArrayList<>();
        //g: gauche d:droite, h:haut , b:bas tap: toucher bref, touch1:toucher long, cl:circleleft,cr:circle right

        if (niveau <= 5) {
            int longueur = 25;
            for (int i = 0; i < longueur; i++) {
                String[] chars = {"g", "d", "tap"};
                double aleatoire = (Math.random());
                if (aleatoire < 0.3) {
                    sequence.add(chars[0]);
                } else if (aleatoire <= 0.6) {
                    sequence.add(chars[1]);
                } else if (aleatoire <= 1) {
                    sequence.add(chars[2]);
                }

            }
        } else if (niveau <= 10) {
            int longueur = 35;
            for (int i = 0; i < longueur; i++) {
                String[] chars = {"g", "d", "tap", "touch1", "h", "b"};
                double aleatoire = (Math.random());
                if (aleatoire < 0.15) {
                    sequence.add(chars[0]);
                } else if (aleatoire < 0.30) {
                    sequence.add(chars[1]);
                } else if (aleatoire < 0.45) {
                    sequence.add(chars[2]);
                } else if (aleatoire < 0.60) {
                    sequence.add(chars[3]);
                } else if (aleatoire < 0.75) {
                    sequence.add(chars[4]);
                } else if (aleatoire <= 1) {
                    sequence.add(chars[5]);
                }

            }
        } else if (niveau <= 15) {
            int longueur = 55;
            for (int i = 0; i < longueur; i++) {
                String[] chars = {"g", "d", "tap", "touch1", "h", "b", "cr", "cl"};
                double aleatoire = (Math.random());
                if (aleatoire < 0.12) {
                    sequence.add(chars[0]);
                } else if (aleatoire < 0.24) {
                    sequence.add(chars[1]);
                } else if (aleatoire < 0.36) {
                    sequence.add(chars[2]);
                } else if (aleatoire < 0.48) {
                    sequence.add(chars[3]);
                } else if (aleatoire < 0.60) {
                    sequence.add(chars[4]);
                } else if (aleatoire < 0.72) {
                    sequence.add(chars[5]);
                } else if (aleatoire < 0.84) {
                    sequence.add(chars[6]);
                } else if (aleatoire <= 1) {
                    sequence.add(chars[7]);
                }


            }

        }


        return sequence;
    }




    public void lancerAnimation(ArrayList<String> sequence) {

        indiceActuelSequence=0;
        vOverlay.setOnTouchListener(touchListener);

        zoneJeu.clearAnimation();

        sequenceARealiser = ( sequences.get(pos - 1));
       // sequenceARealiser = traiterSequences( sequences.get(pos - 1));
        couples = sequenceARealiser.split(";");
        nextPalierScore.setText(String.valueOf (Math.round((int)(0.75*2*couples.length))));
        nextTrophy.setImageDrawable(getDrawable(R.drawable.trophy_bronze));
        //   Toast.makeText(InGame.this, s, Toast.LENGTH_LONG).show();
        listeTminToClick = new ArrayList<>();

        int sommeduration = 4000;
        listeTminToClick.add(sommeduration);


        int offset = Integer.valueOf(couples[0].split(":")[1]);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int widthZoneJeu = zoneJeu.getWidth();
        int heightZoneJeu = zoneJeu.getHeight();


        int tempsDemarrageMusique=(int)((0.7*5000)-Integer.valueOf(couples[0].split(":")[1]));


        for (int j = 0; j < couples.length; j++) {
            listePerfect.add(j,false);
            indicesequence = j;
            String symbole = couples[j].split(":")[0];
            // Toast.makeText(InGame.this, symbole, Toast.LENGTH_LONG).show();
            final ImageView i = new ImageView(getApplicationContext());


            if (j == 0) {
                i.setImageDrawable(trouverSymbole(symbole, "vert"));

            } else {
                i.setImageDrawable(trouverSymbole(symbole, "vert"));
            }

            if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.fleche_bas4_vert))){
               i.setY(5*heightZoneJeu/100);

            }

            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.fleche_gauche4_vert))){
                i.setY(24*heightZoneJeu/100);
            }
            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.cercle_vert))){
                i.setY(46*heightZoneJeu/100);
            }
            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.fleche_droite5_sombre))){
                i.setY(64*heightZoneJeu/100);
            }
            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.fleche_haut4_vert))){
               i.setY(85*heightZoneJeu/100);

            }
            // Toast.makeText(this, String.valueOf(width), Toast.LENGTH_SHORT).show();

            // Génération des trajectoires des images

            //Determination du point de départ


            zoneJeu.addView(i, j);

            i.setVisibility(View.INVISIBLE);




            ValueAnimator va = ValueAnimator.ofFloat(0f, zoneJeu.getWidth());
            int mDuration = 5000; //in millis
            va.setDuration(mDuration);
            MyValueAnimatorListener listener = new MyValueAnimatorListener();

            listener.setIndice(j);
            listener.setDrawable(i.getDrawable());
            va.addUpdateListener(listener);

            if(tempsDemarrageMusique>=0){

                if(j==0){
                    va.setStartDelay(0);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.start();
                        //    mPlayer.setVolume(0.3f,0.3f);

                            mPlayer.start();
                        }
                    }, tempsDemarrageMusique);
                }
                else{
                    va.setStartDelay(Integer.valueOf(couples[j].split(":")[1])-Integer.valueOf(couples[0].split(":")[1]));
                }



            }

            else if(tempsDemarrageMusique<0){

                if(j==0){

                    va.setStartDelay(-tempsDemarrageMusique);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.start();
                        //    mPlayer.setVolume(0.3f,0.3f);

                            mPlayer.start();
                        }
                    }, 0);
                }

                else{
                    va.setStartDelay(-tempsDemarrageMusique+(Integer.valueOf(couples[j].split(":")[1])-Integer.valueOf(couples[0].split(":")[1])));
                }

            }



            va.start();






        }



    }

    public Drawable trouverSymbole(String symbole, String couleur) {
        Drawable retour;
        retour = getDrawable(R.drawable.fleche_gauche4_vert);
        if (couleur.equals("vert")) {
            if (symbole.equals("g")) {
                retour = getDrawable(R.drawable.fleche_gauche4_vert);


            } else if (symbole.equals("d")) {
                retour = getDrawable(R.drawable.fleche_droite5_sombre);

            } else if (symbole.equals("h")) {
                retour = getDrawable(R.drawable.fleche_haut4_vert);

            } else if (symbole.equals("b")) {
                retour = getDrawable(R.drawable.fleche_bas4_vert);

            } else if (symbole.equals("cr")) {
                retour = getDrawable(R.drawable.cr2);

            } else if (symbole.equals("cl")) {
                retour = getDrawable(R.drawable.cl);

            } else if (symbole.equals("tap")) {
                retour = getDrawable(R.drawable.cercle_vert);

            }


       }

        return retour;
    }

    public Drawable trouverSymbole2(Drawable d) {
        Drawable retour;
        retour = getDrawable(R.drawable.fleche_bas4_vert);

        if (areDrawablesIdentical(d,getDrawable(R.drawable.fleche_gauche4_vert))) {
            retour = getDrawable(R.drawable.fleche_gauche4_rouge);


        } else if (d.getConstantState().equals(getDrawable(R.drawable.fleche_droite5_sombre).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_droite4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_haut4_vert)).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_haut4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_bas4_vert)).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_bas4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.cr2)).getConstantState())) {
            retour = getDrawable(R.drawable.cr2);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.cl)).getConstantState())) {
            retour = getDrawable(R.drawable.cl);

        } else if (areDrawablesIdentical(d,getDrawable(R.drawable.cercle_vert))) {
            retour = getDrawable(R.drawable.cercle_rouge);

        }


        return retour;
    }
    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }



    public class MyValueAnimatorListener implements ValueAnimator.AnimatorUpdateListener{
        int indiceVue;
        Drawable d;
        boolean dejaActualise=false;
        boolean dejaSupprime=false;
        boolean vueRendueVisible=false;
        boolean dejaPasseNormal=false;

        public void setDrawable(Drawable d){
            this.d=d;
        }
        public void setIndice(int indice) {
            this.indiceVue = indice;
        }
        public void onAnimationUpdate(ValueAnimator animation) {
            if(!vueRendueVisible){
                zoneJeu.getChildAt(indiceVue).setVisibility(View.VISIBLE);
                vueRendueVisible=true;
            }

            zoneJeu.getChildAt(indiceVue).setTranslationX((float)animation.getAnimatedValue());
            if((float)animation.getAnimatedValue()>=0.97*zoneJeu.getWidth()){
                if(!dejaSupprime) {
                    if (listeIndicesActuels.size() > 0 ) {
                        listeIndicesActuels.remove(Integer.valueOf(indiceVue));
                        dejaSupprime = true;

                         if(!(areDrawablesIdentical(((ImageView)  zoneJeu.getChildAt(indiceVue)).getDrawable(),getDrawable(R.drawable.plusun))) && !(areDrawablesIdentical(((ImageView)  zoneJeu.getChildAt(indiceVue)).getDrawable(),getDrawable(R.drawable.plusdeux))) && zoneJeu.getChildAt(indiceVue).getVisibility()==View.VISIBLE){
                             if(barreVie>1 && aQuitteJeu==false){
                                 barreVie-=1;
                                 progress.setProgress(barreVie*100/10);

                             }
                             else if(barreVie==1 && aQuitteJeu==false){
                                 barreVie-=1;
                                 //barre vie vaut 0 - on stop la partie
                                 progress.setProgress(0);
                               //  vOverlay.setOnTouchListener(null);
                                 handler_stopperPartie_vieTerminee = new Handler();
                                 stopperPartie_vieTerminee=new Runnable() {
                                     public void run() {
                                         // Actions to do after 10 seconds
                                         Intent j = new Intent(getApplicationContext(), ResultatsPartie.class);
                                         j.putExtra("niveau", pos);
                                         j.putExtra("score", score);
                                         j.putExtra("nbTotalMvts", 2*couples.length);
                                         j.putExtra("manques", 2*couples.length - score);

                                         //  mPlayer.stop();
                                         // mPlayer.reset();
                                         mPlayer.release();


                                         startActivity(j);
                                         finish();
                                         toast.cancel();
                                         return;
                                     }
                                 };
                                 handler_stopperPartie_vieTerminee.postDelayed(stopperPartie_vieTerminee, 1000);

                             }
                             textPerfect.setText("Manqué!");
                             textPerfect.setTextColor(getResources().getColor(R.color.colorRed_A400));
                             textPerfect.setVisibility(View.VISIBLE);


                             textPerfect.startAnimation(animBounce);
                         }



                    }

                }


            }

            else if((float)animation.getAnimatedValue()>=0.80*zoneJeu.getWidth()){
                if(!dejaPasseNormal){

                    listePerfect.set(indiceVue,false);
                    dejaPasseNormal=true;
                }
            }

            else if((float)animation.getAnimatedValue()>=0.66*zoneJeu.getWidth()){
                if(!dejaActualise) {




                           Drawable d = ((ImageView) (zoneJeu.getChildAt(indiceVue))).getDrawable();
                           ((ImageView) (zoneJeu.getChildAt(indiceVue))).setImageDrawable(trouverSymbole2(d));
                           //dernierSymbole = (traiterSequences(sequences.get(pos - 1)).split(";")[indiceVue]).split(":")[0];

                           listeIndicesActuels.add(indiceVue);
                           listePerfect.set(indiceVue,true);

                           dejaActualise=true;
                       }





            }




        }

    }

/*    public static class CardView extends Fragment {
        private static float CARDS_SWIPE_LENGTH = 250;
        private float originalX = 0;
        private float originalY = 0;
        private float startMoveX = 0;
        private float startMoveY = 0;

        public CardView() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.swipe_fragment_ingame, container, false);

            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    derniereAction.setVariable("d");
                    final float X = event.getRawX();
                    final float Y =  event.getRawY();
                    float deltaX = X - startMoveX;
                    float deltaY = Y - startMoveY;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            startMoveX = X;
                            startMoveY = Y;
                            break;
                        case MotionEvent.ACTION_UP:
                         //   childView.getBackground().setColorFilter(R.color.color_card_background, PorterDuff.Mode.DST);
                            if ( Math.abs(deltaY) < CARDS_SWIPE_LENGTH ) {
                                rootView.setX(originalX);
                                rootView.setY(originalY);
                            } else if ( deltaY > 0 ) {
                                onCardSwipeDown();
                            } else {
                                onCardSwipeUp();
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int newColor = 0;
                            if ( deltaY < 0 ) {
                                int rb = (int)(255+deltaY/10);
                                newColor = Color.argb(170, rb, 255, rb);
                            } else {
                                int gb = (int)(255-deltaY/10);
                                newColor = Color.argb(170, 255, gb, gb);
                            }
                            rootView.getBackground().setColorFilter(newColor, PorterDuff.Mode.DARKEN);
                            rootView.setTranslationY(deltaY);
                            break;
                    }
                    rootView.invalidate();
                    return true;
                }
            });
            return rootView;
        }



        static CardView newInstance(int position) {
            CardView swipeFragment = new CardView();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
        protected void onCardSwipeUp() {
            Log.i("infoKtr", "Swiped Up");
        }

        protected void onCardSwipeDown() {
            Log.i("infoKtr", "Swiped Down");
        }
        *//**
         * Swaps the X and Y coordinates of your touch event.
         *//*


    }*/



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



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment_ingame, container, false);
            FrameLayout frame = (FrameLayout) swipeView.findViewById(R.id.swiper);
            final Bundle bundle = getArguments();
            final int position = bundle.getInt("position");




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
