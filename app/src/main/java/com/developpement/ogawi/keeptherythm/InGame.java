package com.developpement.ogawi.keeptherythm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
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

import com.plattysoft.leonids.ParticleSystem;
import com.skyfishjy.library.RippleBackground;

import static java.util.logging.Logger.global;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
public class InGame extends AppCompatActivity {//  implements OnGesturePerformedListener{
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barreVie=10;
        animBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyAnimatorListener listener1=new MyAnimatorListener();
        animBounce.setAnimationListener(listener1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);
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
                                startActivity(j);
                                mPlayer.release();
                                toast.cancel();
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
        //nom_txt.add("mindmapthat_music_through_love.txt");
        // nom_txt.add("alexberoza_good_day.txt");
        //nom_txt.add("abrighterheart.txt");
        //nom_txt.add("we_wish_you_a_merry_xmas.txt");
        // nom_txt.add("there_you_go.txt");
        // nom_txt.add("tobias_weber_between_worlds_instrumental.txt");
        //   nom_txt.add("pool.txt");
        //  nom_txt.add("the_voyage.txt");
        // nom_txt.add("hiphop.txt");
        // nom_txt.add("joy_to_the_world.txt");
        //   nom_txt.add("epicseries.txt");
        //  nom_txt.add("bird_in_hand.txt");
        // nom_txt.add("ectoplasm.txt");
        //  nom_txt.add("vj_memes_paint_the_sky.txt");
        // nom_txt.add("alison");
        //  nom_txt.add("piledriver.txt");
        //   nom_txt.add("djlang59_drops_of_h2o_the_filtered_water_treatment.txt");
        //  nom_txt.add("leavinthelights.txt");
        //  nom_txt.add("speck_moonlight_sonata_shifting_sun_mix.txt");
     //   nom_txt.add("what_da_funk.txt");

        ArrayList<String> listeUrl=new ArrayList<>();
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
        //listeUrl.add("https://soundcloud.com/twisterium/london-night-free-instrumental-background-music?in=twisterium/sets/free-commercial-instrumental");
        //   listeUrl.add("http://ccmixter.org/content/mindmapthat/mindmapthat_-_Music_Through_Love.mp3");
        //  listeUrl.add("http://ccmixter.org/content/AlexBeroza/AlexBeroza_-_Good_Day.mp3");
        //   listeUrl.add("https://audionautix.com/Music/ABrighterHeart.mp3");
        // listeUrl.add("https://audionautix.com/Music/WeWishYouAMerryXmas.mp3");
        //  listeUrl.add("http://ccmixter.org/content/tobias_weber/tobias_weber_-_Between_Worlds_(Instrumental).mp3");
        //  listeUrl.add("https://audionautix.com/Music/Pool.mp3");
        // listeUrl.add("https://audionautix.com/Music/ThereYouGo.mp3");
        // listeUrl.add("https://audionautix.com/Music/TheVoyage.mp3");
        //  listeUrl.add("https://audionautix.com/Music/HipHop1.mp3");
        // listeUrl.add("https://audionautix.com/Music/JoyToTheWorld.mp3");
        //  listeUrl.add("https://audionautix.com/Music/EpicSeries.mp3");
        // listeUrl.add("https://audionautix.com/Music/BirdInHand.mp3");
        // listeUrl.add("https://audionautix.com/Music/Ectoplasm.mp3");
        // listeUrl.add("http://ccmixter.org/content/VJ_Memes/VJ_Memes_-_Paint_The_Sky.mp3");
        // listeUrl.add("https://audionautix.com/Music/Alison.mp3");
        // listeUrl.add("https://audionautix.com/Music/Piledriver.mp3");
        //  listeUrl.add("http://ccmixter.org/content/djlang59/djlang59_-_Drops_of_H2O_(_The_Filtered_Water_Treatment_).mp3");
        // listeUrl.add("https://audionautix.com/Music/LeavinTheLights.mp3");
        // listeUrl.add("http://ccmixter.org/content/speck/speck_-_Moonlight_Sonata_(Shifting_Sun_Mix)_1.mp3");
      //  listeUrl.add("https://audionautix.com/Music/Whatdafunk.mp3");




        //chargement musique
        File mFolder = new File(getFilesDir() + "/Music");
        File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3");
        String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
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
           downloadTask.execute(listeUrl.get(pos-1));


           mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
               @Override
               public void onCancel(DialogInterface dialog) {
                   downloadTask.cancel(true);
                   File mFolder = new File(getFilesDir() + "/Music");
                   File file = new File(mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3");
                 //  String filePath=mFolder.getAbsolutePath()+"/" + nom_txt.get(pos-1)+".mp3";
                   file.delete();
                   Intent i = new Intent(getApplicationContext(), EcranAccueil.class);
                   startActivity(i);
                   finish();
                   overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

               }
           });


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

                                    if(barreVie!=10){
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
        if(mPlayer!=null && mPlayer.isPlaying()) {
            mPlayer.pause();
            media_length = mPlayer.getCurrentPosition();

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
                go.setVisibility(View.VISIBLE);

                File mFolder = new File(getFilesDir() + "/Music");
                String filePath = mFolder.getAbsolutePath() + "/" + nom_txt.get(pos - 1) + ".mp3";
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
            rippleBackground.clearAnimation();
            rippleBackground.startRippleAnimation();
          //  rippleBackground.stopRippleAnimation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                   rippleBackground.stopRippleAnimation();
                }
            }, 800);

            // .emit(x,y,5,1000);

            derniereAction.setVariable("tap");

            //  Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            derniereAction.setVariable("touch1");

            //  Toast.makeText(InGame.this, "touch1", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            new ParticleSystem(InGame.this, 20, R.drawable.notemusique, 30000)
                    .setSpeedByComponentsRange(-0.5f, 0.5f, 0f, 0.5f)
//                        .setAcceleration(0.00005f, 45)
                    .oneShot(findViewById(R.id.gOverlay), 5);

            // .emit(x,y,5,1000);
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
                    new ParticleSystem(InGame.this, 5, R.drawable.fleche_gauche4_vert, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(0.5f, 0.5f, 0f, 0.0f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.colorPurple_900));
                } else if (event2.getX() - event1.getX() < 0) {
                    derniereAction.setVariable("d");

                    // Toast.makeText(InGame.this, "right", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.fleche_droite5_sombre, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.5f, -0.5f, 0f, 0.0f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            } else if (Math.abs(event2.getX() - event1.getX()) < Math.abs(event2.getY() - event1.getY())) {


                if (event2.getY() - event1.getY() > 0) {

                    derniereAction.setVariable("h");

                    //    Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.fleche_haut4_vert, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.0f, 0.0f, 0.5f, 0.5f)
//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.cyan));

                } else if (event2.getY() - event1.getY() < 0) {

                    derniereAction.setVariable("b");

                    //   Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();
                    new ParticleSystem(InGame.this, 5, R.drawable.fleche_bas4_vert, 1000,R.id.gOverlay)
                            .setSpeedByComponentsRange(-0.0f, 0.0f, -0.5f, -0.5f)

//                        .setAcceleration(0.00005f, 45)
                            .oneShot(findViewById(R.id.gOverlay), 5);
                    vOverlay.setBackgroundColor(getResources().getColor(R.color.bleu1));

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
                ((ImageView)zoneJeu.getChildAt(listeIndicesActuels.get(0))).setImageDrawable(getDrawable(R.drawable.plusdeux));
             //   zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                       // zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);
                    }
                }, 200);
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


            }
 //zoneJeu.getChildAt(listeIndicesActuels.get(0)).setBackground(getDrawable(R.drawable.emoji_langue));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                  //  zoneJeu.getChildAt(listeIndicesActuels.get(0)).setVisibility(View.GONE);
                }
            }, 0);

        }


        scoreT.setText(String.valueOf(score));

         if(score>=Math.round((int)(0.8*2*couples.length))  ){
             if(actualiserTrophy==true) {
                 nextTrophy.setImageDrawable(getDrawable(R.drawable.trophy_or));
                 actualiserTrophy = false;
                 nextPalierScore.setText(String.valueOf(Math.round((int) (0.9 * 2 * couples.length))));
                 Flubber.with()
                         .animation(Flubber.AnimationPreset.WOBBLE) // Slide up animation
                         .repeatCount(1)                              // Repeat once
                         .duration(1000)                              // Last for 1000 milliseconds(1 second)
                         .createFor(nextTrophy)                             // Apply it to the view
                         .start();
             }
        }
         else if(score>=Math.round((int)(0.7*2*couples.length)) ){
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
        nextPalierScore.setText(String.valueOf (Math.round((int)(0.7*2*couples.length))));
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
            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.tap1))){
                i.setY(45*heightZoneJeu/100);
            }
            else if(areDrawablesIdentical(i.getDrawable(),getDrawable(R.drawable.fleche_droite4_vert))){
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
                retour = getDrawable(R.drawable.fleche_droite4_vert);

            } else if (symbole.equals("h")) {
                retour = getDrawable(R.drawable.fleche_haut4_vert);

            } else if (symbole.equals("b")) {
                retour = getDrawable(R.drawable.fleche_bas4_vert);

            } else if (symbole.equals("cr")) {
                retour = getDrawable(R.drawable.cr2);

            } else if (symbole.equals("cl")) {
                retour = getDrawable(R.drawable.cl);

            } else if (symbole.equals("tap")) {
                retour = getDrawable(R.drawable.tap1);

            }


       }

        return retour;
    }

    public Drawable trouverSymbole2(Drawable d) {
        Drawable retour;
        retour = getDrawable(R.drawable.fleche_bas4_vert);

        if (areDrawablesIdentical(d,getDrawable(R.drawable.fleche_gauche4_vert))) {
            retour = getDrawable(R.drawable.fleche_gauche4_rouge);


        } else if (d.getConstantState().equals(getDrawable(R.drawable.fleche_droite4_vert).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_droite4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_haut4_vert)).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_haut4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_bas4_vert)).getConstantState())) {
            retour = getDrawable(R.drawable.fleche_bas4_rouge);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.cr2)).getConstantState())) {
            retour = getDrawable(R.drawable.cr2);

        } else if (d.getConstantState().equals(getDrawable((R.drawable.cl)).getConstantState())) {
            retour = getDrawable(R.drawable.cl);

        } else if (areDrawablesIdentical(d,getDrawable(R.drawable.tap1))) {
            retour = getDrawable(R.drawable.touch);

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
                             if(barreVie>1){
                                 barreVie-=1;
                                 progress.setProgress(barreVie*100/10);

                             }
                             else if(barreVie==1){
                                 //barre vie vaut 0 - on stop la partie
                                 progress.setProgress(0);
                                 Handler handler = new Handler();
                                 handler.postDelayed(new Runnable() {
                                     public void run() {
                                         // Actions to do after 10 seconds
                                         Intent j = new Intent(getApplicationContext(), ResultatsPartie.class);
                                         j.putExtra("niveau", pos);
                                         j.putExtra("score", score);
                                         j.putExtra("nbTotalMvts", 2*couples.length);
                                         j.putExtra("manques", 2*couples.length - score);
                                         startActivity(j);
                                         mPlayer.stop();
                                         mPlayer.release();
                                         toast.cancel();
                                     }
                                 }, 1000);

                             }
                             textPerfect.setText("Manqué!");
                             textPerfect.setTextColor(getResources().getColor(R.color.colorRed_A400));
                             textPerfect.setVisibility(View.VISIBLE);


                             textPerfect.startAnimation(animBounce);
                         }



                    }
                  //  zoneJeu.getChildAt(indiceVue).setVisibility(View.GONE);
                    //animation.removeAllUpdateListeners();
                }


               // indiceActuelSequence++;
               // animation.cancel();
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




               // animation.removeAllUpdateListeners();
            }




        }

    }

}
/*
public class InGame extends AppCompatActivity {//implements OnGesturePerformedListener{
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

//        nom_txt.add("mindmapthat_music_through_love.txt");
//        nom_txt.add("cdk_like_music_cdk_mix.txt");
//        nom_txt.add("alexberoza_good_day.txt");
//        nom_txt.add("abrighterheart.txt");
//        nom_txt.add("alla_what_parody.txt");
//        nom_txt.add("we_wish_you_a_merry_xmas.txt");
//        nom_txt.add("carol_of_the_bells.txt");
//        nom_txt.add("tobias_weber_between_worlds_instrumental.txt");
//        nom_txt.add("pool.txt");
//        nom_txt.add("rocker.txt");
//        nom_txt.add("there_you_go.txt");
//        nom_txt.add("jeffspeed68_two_pianos.txt");
//        nom_txt.add("adagioinc.txt");
//        nom_txt.add("the_voyage.txt");
//        nom_txt.add("hiphop.txt");
//        nom_txt.add("hansatom_persephone.txt");
//        nom_txt.add("joy_to_the_world.txt");
//        nom_txt.add("epicseries.txt");
//        nom_txt.add("bird_in_hand.txt");
//        nom_txt.add("ectoplasm.txt");
//        nom_txt.add("vj_memes_paint_the_sky.txt");
//        nom_txt.add("alison");
//        nom_txt.add("piledriver.txt");
//        nom_txt.add("go_not_gently.txt");
//        nom_txt.add("djlang59_drops_of_h2o_the_filtered_water_treatment.txt");
//        nom_txt.add("leavinthelights.txt");
//        nom_txt.add("speck_moonlight_sonata_shifting_sun_mix.txt");
//        nom_txt.add("triangle.txt");
//        nom_txt.add("bigcartheft.txt");
//        nom_txt.add("what_da_funk.txt");

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


            mProgressDialog = new ProgressDialog(InGame.this);

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Téléchargement de la musique");
            mProgressDialog.setIndeterminate(true);

            mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(InGame.this);
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
              Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
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
                           Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                   } else if (e2.getY() - e1.getY() < 0) {

                       //    derniereAction.setVariable("b");
                           Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(InGame.this, "left", Toast.LENGTH_SHORT).show();
                } else if (event2.getX() - event1.getX() < 0) {
                 //   derniereAction.setVariable("d");
                    timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    coupleMvtTemps+="d:" + String.valueOf(timeOfDerniereAction)+";";
                    i.setImageDrawable(getDrawable(R.drawable.fleche_droite2));
                 //   symboleUtilisateur.removeAllViews();
                 //   symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                       Toast.makeText(InGame.this, "right", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                } else if (event2.getY() - event1.getY() < 0) {
                       timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
             //       derniereAction.setVariable("b");
                    coupleMvtTemps+="b:" + String.valueOf(timeOfDerniereAction)+";";


                    i.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
                   // symboleUtilisateur.removeAllViews();
                  //  symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                    Toast.makeText(InGame.this, "down", Toast.LENGTH_SHORT).show();

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
*/
