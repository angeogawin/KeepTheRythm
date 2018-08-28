package com.developpement.ogawi.keeptherythm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
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
import java.io.OutputStreamWriter;
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

import static java.util.logging.Logger.global;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager;
//import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class InGame extends AppCompatActivity {//  implements OnGesturePerformedListener{
    Intent i;
    static final int READ_BLOCK_SIZE = 100;
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


    ArrayList<Integer> listeMusique;

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
    ProgressBar progress;
    ArrayList<String> nom_txt;
    int pos;
    String sequenceARealiser;
    int media_length;
    boolean animationEstLancee;
    ArrayList<String> sequences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);
        i = getIntent();
        pos = i.getExtras().getInt("niveau");
        zoneJeu = findViewById(R.id.zonejeu);
        listeImagesAnimationsActives = new ArrayList<>();
        // rondCentral=(ImageView) findViewById(R.id.rondCentral);
        progress = findViewById(R.id.progress);
        animationEstLancee=false;
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


        go = findViewById(R.id.go);


        go.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //  zoneJeu = (RelativeLayout)findViewById(R.id.bandeaupourfleche);
               /* ViewTreeObserver vto = zoneJeu.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                      //  zoneJeu.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        widthZoneJeu = zoneJeu.getMeasuredWidth();
                        heightZoneJeu = zoneJeu.getMeasuredHeight();

                    }
                });*/
               animationEstLancee=true;
                go.setVisibility(View.INVISIBLE);
                // rondCentral.setVisibility(View.VISIBLE);
                lancerAnimation(sequence);
               /* Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.start();
                        mPlayer.start();
                    }
                }, 100);*/

            }
        });

        //  sequence = genererSequenceAleatoire(pos);
        nom_txt = new ArrayList<>();
        nom_txt.add("london_night.txt");
        nom_txt.add("new_begining.txt");
        nom_txt.add("night_life2.txt");
        nom_txt.add("progress.txt");
        nom_txt.add("reiswerk_lies_on_lies_update.txt");
        nom_txt.add("sleeperspaceborn_better.txt");
        nom_txt.add("reiswerk_tick_tock.txt");
        nom_txt.add("tobias_weber_between_worlds_instrumental.txt");
        nom_txt.add("tobias_weber_rooted_in_soil.txt");
        nom_txt.add("veezyn_i_want_to_see_you_again.txt");
        nom_txt.add("copperhead_platinum_donation.txt");
        nom_txt.add("jeffspeed68_two_pianos.txt");
        nom_txt.add("life_is_beautiful.txt");
        nom_txt.add("alexberoza_supernatural_instrumental.txt");
        nom_txt.add("one_project_blue.txt");
        nom_txt.add("pumping.txt");
        nom_txt.add("siobhand_the_quiet_hours.txt");
        nom_txt.add("stellarartwars_floating_through_time_saw_mix.txt");
        nom_txt.add("texasradiofish_knock_on_my_door.txt");
        nom_txt.add("unreal_dm_come_back.txt");
        nom_txt.add("vj_memes_paint_the_sky.txt");
        nom_txt.add("sackjo22_trala_the_let_s_never_be_far_mix.txt");
        nom_txt.add("snowflake_miracles.txt");
        nom_txt.add("unreal_dm_talk_to_me.txt");
        nom_txt.add("djlang59_drops_of_h2o_the_filtered_water_treatment.txt");
        nom_txt.add("snowflake_fever.txt");
        nom_txt.add("speck_moonlight_sonata_shifting_sun_mix.txt");
        nom_txt.add("duckett_what_cha_waitin_simple_club_mix.txt");
        nom_txt.add("daguilar_remember_the_name.txt");
        nom_txt.add("scomber_end_of_the_game.txt");
        listeMusique = new ArrayList<>();
        // **** ajout des musiques
        listeMusique.add(R.raw.london_night);//niveau 1
        listeMusique.add(R.raw.new_begining);//niveau 2
        listeMusique.add(R.raw.night_life2);//niveau 3
        listeMusique.add(R.raw.progress);//niveau 4
        listeMusique.add(R.raw.reiswerk_lies_on_lies_update);//niveau 5
        listeMusique.add(R.raw.sleeperspaceborn_better);//niveau 6
        listeMusique.add(R.raw.reiswerk_tick_tock);//niveau 7
        listeMusique.add(R.raw.tobias_weber_between_worlds_instrumental);//niveau 8
        listeMusique.add(R.raw.tobias_weber_rooted_in_soil);//niveau 9
        listeMusique.add(R.raw.veezyn_i_want_to_see_you_again);//niveau 10
        listeMusique.add(R.raw.copperhead_platinum_donation);//niveau 11
        listeMusique.add(R.raw.jeffspeed68_two_pianos);//niveau 12
        listeMusique.add(R.raw.life_is_beautiful);//niveau 13
        listeMusique.add(R.raw.alexberoza_supernatural_instrumental);//niveau 14
        listeMusique.add(R.raw.one_project_blue);//niveau 15
        listeMusique.add(R.raw.pumping);//niveau 16
        listeMusique.add(R.raw.siobhand_the_quiet_hours);//niveau 17
        listeMusique.add(R.raw.stellarartwars_floating_through_time_saw_mix);//niveau 18
        listeMusique.add(R.raw.texasradiofish_knock_on_my_door);//niveau 19
        listeMusique.add(R.raw.unreal_dm_come_back);//niveau 20
        listeMusique.add(R.raw.vj_memes_paint_the_sky);//niveau 21
        listeMusique.add(R.raw.sackjo22_trala_the_let_s_never_be_far_mix);//niveau 22
        listeMusique.add(R.raw.snowflake_miracles);//niveau 23
        listeMusique.add(R.raw.unreal_dm_talk_to_me);//niveau 24
        listeMusique.add(R.raw.djlang59_drops_of_h2o_the_filtered_water_treatment);//niveau 25
        listeMusique.add(R.raw.snowflake_fever);//niveau 26
        listeMusique.add(R.raw.speck_moonlight_sonata_shifting_sun_mix);//niveau 27
        listeMusique.add(R.raw.duckett_what_cha_waitin_simple_club_mix);//niveau 28
        listeMusique.add(R.raw.daguilar_remember_the_name);//niveau 29
        listeMusique.add(R.raw.scomber_end_of_the_game);//niveau 30

        // **** fin ajout des musiques
        mPlayer = MediaPlayer.create(this, listeMusique.get(pos - 1));

        indiceActuelSequence = 80;
        score = 0;
        mChronometer = new Chronometer(this);
        derniereAction = new StringModified();
        timeOfDerniereAction = 0;
        //symboleT=(RelativeLayout) findViewById(R.id.symbole);
        //  symboleUtilisateur=(RelativeLayout) findViewById( R.id.symboleMis);
        v = (GestureOverlayView) findViewById(R.id.gOverlay);
        scoreT = (TextView) findViewById(R.id.score);
        scoreT.setText(String.valueOf(score));
        elementActuelClickable = false;


        //   Toast.makeText(InGame.this, getApplicationContext().getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
//flecheactuelle = findViewById(R.id.flecheactuelle);

        //dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);

        //dernierSymbole=sequence.get(0);

        dernierSymbole = (sequences.get(pos-1).split(";")[0]).split(":")[0];

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

                LinearLayout layout = new LinearLayout(InGame.this);
                //  layout.setBackgroundResource(R.color.orange);


                // indiceActuelSequence += 1;
                timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                //tmin=listeTminToClick.get(indiceActuelSequence);
                elementActuelClickable = false;
                if (listeImagesAnimationsActives.contains(derniereAction.getValeur())) {
                    elementActuelClickable = true;

                } else {
                    elementActuelClickable = false;
                }


                if (elementActuelClickable) {

                    //Toast.makeText(InGame.this, String.valueOf(elementActuelClickable), Toast.LENGTH_SHORT).show();
                    if (zoneJeu.getChildCount() > 0) {
                         (zoneJeu.getChildAt(indiceActuelSequence)).clearAnimation();
                             ((ImageView) (zoneJeu.getChildAt(indiceActuelSequence))).setVisibility(View.GONE);
                        //     zoneJeu.removeViewAt(0);
//                   Drawable d=((ImageView)(zoneJeu.getChildAt(0))).getDrawable();
//                   ((ImageView)(zoneJeu.getChildAt(0))).setImageDrawable(trouverSymbole2( d));
                        if (dernierSymbole.equals(derniereAction.getValeur())) {

                            //  Toast.makeText(InGame.this, "Amaz", Toast.LENGTH_SHORT).show();

                            ImageView img = new ImageView(InGame.this);

                            // give the drawble resource for the ImageView
                            img.setImageResource(R.drawable.emoji_muscle);
                            layout.addView(img);
                            Toast toast = new Toast(InGame.this); //context is object of Context write "this" if you are an Activity
                            // Set The layout as Toast View
                            toast.setView(layout);

                            // Position you toast here toast position is 50 dp from bottom you can give any integral value
                            toast.setGravity(Gravity.BOTTOM, 0, 50);
                            toast.show();



                            progress.setProgress(score * 100 / couples.length);
                            if (indiceActuelSequence <= couples.length - 1) {
                                if (verifierBonRythmeJoueur()) {
                                    //actualiser score en conséquence
                                    actualiserScore();
                                }
                                // dernierSymbole = sequence.get(indiceActuelSequence);

                                //Toast.makeText(InGame.this, dernierSymbole, Toast.LENGTH_SHORT).show();

                            } else if (indiceActuelSequence > couples.length - 1) {
                            //    Toast.makeText(InGame.this, "Jeu terminé", Toast.LENGTH_SHORT).show();
                                //  zoneJeu.removeAllViews();
                              //  mPlayer.stop();
                            }
                        } else {
                            ImageView img = new ImageView(InGame.this);

                            // give the drawble resource for the ImageView
                            img.setImageResource(R.drawable.emoji_surpris);

                            // add both the Views TextView and ImageView in layout
                            layout.addView(img);
                            // add both the Views TextView and ImageView in layout
                            Toast toast = new Toast(InGame.this); //context is object of Context write "this" if you are an Activity
                            // Set The layout as Toast View
                            toast.setView(layout);

                            // Position you toast here toast position is 50 dp from bottom you can give any integral value
                            toast.setGravity(Gravity.BOTTOM, 0, 50);
                            toast.show();
                            //   Toast.makeText(InGame.this, "Erreur mouvement", Toast.LENGTH_SHORT).show();

                            //fin jeu
                            //zoneJeu.removeAllViews();
                            //mPlayer.stop();

                        }
                      //  (zoneJeu.getChildAt()).clearAnimation();
                    }


//                else{
//
//                   Drawable d=((ImageView)(zoneJeu.getChildAt(0))).getDrawable();
//                   ((ImageView)(zoneJeu.getChildAt(0))).setImageDrawable(trouverSymbole2( d));
//                    Toast.makeText(InGame.this, "Erreur timing", Toast.LENGTH_SHORT).show();
//                    //fin jeu
//
//                    mPlayer.stop();
//                }

                    // dernierSymbole = actualiserSymbole(flecheactuelle, sequence, indiceActuelSequence);


                }
                else{
                    Toast.makeText(InGame.this, "Jeu terminé", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector

        v.setOnTouchListener(touchListener);
        v.post(new Runnable() {
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
        mImageView.setImageDrawable(getDrawable(R.drawable.fleche_gauche4_vert));
        //  mImageView.setVisibility(View.GONE);
        //   zoneJeu.addView(mImageView);

    }

    public boolean verifierBonRythmeJoueur() {

        boolean aLeRythme = false;
        int tempsCorrectPourActionEnRyhtme = Integer.valueOf(couples[indiceActuelSequence].split(":")[1]);

        if (Math.abs(SystemClock.elapsedRealtime() - mChronometer.getBase() - tempsCorrectPourActionEnRyhtme) < 1000) {
            //1000 ms=1s
            aLeRythme = true;
        }
        return aLeRythme;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(animationEstLancee) {
            mPlayer.seekTo(media_length);
            mPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
        media_length = mPlayer.getCurrentPosition();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            mPlayer.stop();
            finish();
            Intent i = new Intent(this, EcranAccueil.class);
            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);
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

            derniereAction.setVariable("tap");
            // i.setImageDrawable(getDrawable(R.drawable.tap));
            //symboleUtilisateur.removeAllViews();
            // symboleUtilisateur.addView(i);
            //  i.startAnimation(fadeOut);
            //  Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            derniereAction.setVariable("touch");
            //  i.setImageDrawable(getDrawable(R.drawable.touch));
            //  symboleUtilisateur.removeAllViews();
            // symboleUtilisateur.addView(i);
            //  i.startAnimation(fadeOut);
            //  Toast.makeText(InGame.this, "touch", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (Math.abs(event2.getY() - event1.getY()) < Math.abs(event2.getX() - event1.getX())) {


                if (event2.getX() - event1.getX() > 0) {
                    derniereAction.setVariable("g");
                    //  i.setImageDrawable(getDrawable(R.drawable.fleche_gauche4_vert));
                    //   symboleUtilisateur.removeAllViews();
                    //  symboleUtilisateur.addView(i);
                    //   i.startAnimation(fadeOut);
                    //   Toast.makeText(InGame.this, "left", Toast.LENGTH_SHORT).show();
                } else if (event2.getX() - event1.getX() < 0) {
                    derniereAction.setVariable("d");
                    //   i.setImageDrawable(getDrawable(R.drawable.fleche_droite4_vert));
                    // symboleUtilisateur.removeAllViews();
                    //   symboleUtilisateur.addView(i);
                    //    i.startAnimation(fadeOut);
                    // Toast.makeText(InGame.this, "right", Toast.LENGTH_SHORT).show();
                }
            } else if (Math.abs(event2.getX() - event1.getX()) < Math.abs(event2.getY() - event1.getY())) {


                if (event2.getY() - event1.getY() > 0) {

                    derniereAction.setVariable("h");
                    //     i.setImageDrawable(getDrawable(R.drawable.fleche_haut4_vert));
                    //   symboleUtilisateur.removeAllViews();
                    //   symboleUtilisateur.addView(i);
                    //    i.startAnimation(fadeOut);
                    Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                } else if (event2.getY() - event1.getY() < 0) {

                    derniereAction.setVariable("b");
                    //    i.setImageDrawable(getDrawable(R.drawable.fleche_bas4_vert));
                    //    symboleUtilisateur.removeAllViews();
                    //    symboleUtilisateur.addView(i);
                    //  i.startAnimation(fadeOut);
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
        if (id == R.id.action_pauseplay) {
            Toast.makeText(InGame.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_stop) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                mDrawer.openDrawer(GravityCompat.START);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void identifierDerniereAction() {

        //actualise string derniereAction;
    }

    public void actualiserScore() {
        //actualise int score

        score += 1;

        scoreT.setText(String.valueOf(score));


    }


    public ArrayList<String> genererSequenceAleatoire(int niveau) {
        ArrayList<String> sequence = new ArrayList<>();
        //g: gauche d:droite, h:haut , b:bas tap: toucher bref, touch:toucher long, cl:circleleft,cr:circle right

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
                String[] chars = {"g", "d", "tap", "touch", "h", "b"};
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
                String[] chars = {"g", "d", "tap", "touch", "h", "b", "cr", "cl"};
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

    public String actualiserSymbole(ImageView image, ArrayList<String> sequence, int indiceActuel) {
        String directionFleche = sequence.get(indiceActuel);
        if (sequence.get(indiceActuel).equals("g")) {
            image.setImageDrawable(getDrawable(R.drawable.fleche_gauche2));
        } else if (sequence.get(indiceActuel).equals("d")) {
            image.setImageDrawable(getDrawable(R.drawable.fleche_droite2));
        } else if (sequence.get(indiceActuel).equals("h")) {
            image.setImageDrawable(getDrawable(R.drawable.fleche_haut2));
        } else if (sequence.get(indiceActuel).equals("b")) {
            image.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
        } else if (sequence.get(indiceActuel).equals("cr")) {
            image.setImageDrawable(getDrawable(R.drawable.cr2));
        } else if (sequence.get(indiceActuel).equals("cl")) {
            image.setImageDrawable(getDrawable(R.drawable.cl));
        } else if (sequence.get(indiceActuel).equals("tap")) {
            image.setImageDrawable(getDrawable(R.drawable.tap));
        } else if (sequence.get(indiceActuel).equals("touch")) {
            image.setImageDrawable(getDrawable(R.drawable.touch));
        }

        return directionFleche;
    }


    public void lancerAnimation(ArrayList<String> sequence) {
        v.setOnTouchListener(touchListener);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mPlayer.reset();
        mPlayer = MediaPlayer.create(this, listeMusique.get(pos - 1));
        mPlayer.start();

        zoneJeu.removeAllViews();
        zoneJeu.clearAnimation();
       // sequenceARealiser = ReadTxt();
        sequenceARealiser=sequences.get(pos-1);
        couples = sequenceARealiser.split(";");
        //   Toast.makeText(InGame.this, s, Toast.LENGTH_LONG).show();
        listeTminToClick = new ArrayList<>();

        int sommeduration = 4000;
        listeTminToClick.add(sommeduration);


        int offset = Integer.valueOf(couples[0].split(":")[1]);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        int widthZoneJeu = zoneJeu.getWidth();
        int heightZoneJeu = zoneJeu.getHeight();

        ArrayList<AnimationSet> listeAnimations = new ArrayList<>();

        for (int j = 0; j < couples.length; j++) {
            indicesequence = j;
            String symbole = couples[j].split(":")[0];
            // Toast.makeText(InGame.this, symbole, Toast.LENGTH_LONG).show();
            ImageView i = new ImageView(getApplicationContext());


            if (j == 0) {
                i.setImageDrawable(trouverSymbole(symbole, "rouge"));
            } else {
                i.setImageDrawable(trouverSymbole(symbole, "vert"));
            }
            // Toast.makeText(this, String.valueOf(width), Toast.LENGTH_SHORT).show();

            // Génération des trajectoires des images

            //Determination du point de départ
            int xDepart;
            int yDepart;



            Random n = new Random();
            xDepart = n.nextInt(widthZoneJeu - 500) + 250;//n.nextInt(max-min)+min
            n = new Random();
            yDepart = n.nextInt(heightZoneJeu - 500) + 250;
            i.setX(xDepart);
            i.setY(yDepart);
            offset = Integer.valueOf(couples[j].split(":")[1]);

            int dureeAnimation = 1000;//1s par defaut
            if (j < couples.length - 1) {
                if (Integer.valueOf(couples[j + 1].split(":")[1]) - offset < 1000) {
                    //on adapte la durée des animations lorsque le ryhtme s'accelere
                   // dureeAnimation = Integer.valueOf(couples[j + 1].split(":")[1]) - offset;
                }
            }
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration((int) (dureeAnimation / 2));
            fadeIn.setStartOffset(offset);
            // fadeIn.setFillAfter(true);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setStartOffset(offset + (int) (dureeAnimation / 2));
            fadeOut.setDuration((int) (dureeAnimation / 2));
            // fadeOut.setFillAfter(true);

            AnimationSet animation = new AnimationSet(false); //change to false
            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);
            animation.setFillAfter(true);
          //  animation.setDuration(dureeAnimation);
          //  i.setAnimation(animation);
            //this.setAnimation(animation);
            MyAnimationListener1 listener1 = new MyAnimationListener1();
            listener1.addAction(symbole);
            listener1.setImage(i);
            listener1.setIndice(j);
            animation.setAnimationListener(listener1);

            listeAnimations.add(animation);

            zoneJeu.addView(i, j);


          //  i.startAnimation(animation);



        }
        Toast.makeText(getApplicationContext(), String.valueOf(zoneJeu.getChildCount()), Toast.LENGTH_SHORT).show();
        ArrayList<AnimationSet> anim2=new ArrayList<>();
       anim2=listeAnimations;
        List<Animator> mAnimatorList = new ArrayList<>();
        RelativeLayout zoneJeuCopie=new RelativeLayout(getApplicationContext());
        zoneJeuCopie=zoneJeu;
        for (int q = 0; q < zoneJeuCopie.getChildCount() ; q++) {
            zoneJeu.getChildAt(q).startAnimation(anim2.get(q));

        }
/*
        final ArrayList<AnimationSet> anim2=listeAnimations;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds


                for (int q = 0; q < zoneJeu.getChildCount() ; q++) {
                    zoneJeu.getChildAt(q).startAnimation(anim2.get(q));
                }
            }
        }, 100);*/



       /* final ArrayList<AnimationSet> anim2=listeAnimations;

                for(int q=0;q<zoneJeu.getChildCount()-1;q++){
                    zoneJeu.getChildAt(q).startAnimation(anim2.get(q));
                }*/











    }

    public Drawable trouverSymbole(String symbole, String couleur ){
        Drawable retour;
        retour=getDrawable(R.drawable.fleche_gauche4_vert);
        if(couleur.equals("vert")) {
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
                retour = getDrawable(R.drawable.tap);

            } else if (symbole.equals("touch")) {
                retour = getDrawable(R.drawable.touch);

            }

        }

        else if(couleur.equals("rouge")){
            if (symbole.equals("g")) {
                retour = getDrawable(R.drawable.fleche_gauche4_rouge);


            } else if (symbole.equals("d")) {
                retour = getDrawable(R.drawable.fleche_droite4_rouge);

            } else if (symbole.equals("h")) {
                retour = getDrawable(R.drawable.fleche_haut4_rouge);

            } else if (symbole.equals("b")) {
                retour = getDrawable(R.drawable.fleche_bas4_rouge);

            } else if (symbole.equals("cr")) {
                retour = getDrawable(R.drawable.cr2);

            } else if (symbole.equals("cl")) {
                retour = getDrawable(R.drawable.cl);

            } else if (symbole.equals("tap")) {
                retour = getDrawable(R.drawable.touch);

            } else if (symbole.equals("touch")) {
                retour = getDrawable(R.drawable.touch);

            }
        }

        return retour;
    }

    public Drawable trouverSymbole2(Drawable d ) {
        Drawable retour;
        retour = getDrawable(R.drawable.fleche_gauche4_vert);

            if (d.getConstantState().equals(getDrawable((R.drawable.fleche_gauche4_vert)).getConstantState())){
                retour = getDrawable(R.drawable.fleche_gauche4_rouge);


            } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_droite4_vert)).getConstantState())) {
                retour = getDrawable(R.drawable.fleche_droite4_rouge);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_haut4_vert)).getConstantState())) {
                retour = getDrawable(R.drawable.fleche_haut4_rouge);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.fleche_bas4_vert)).getConstantState())) {
                retour = getDrawable(R.drawable.fleche_bas4_rouge);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.cr2)).getConstantState())) {
                retour = getDrawable(R.drawable.cr2);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.cl)).getConstantState())) {
                retour = getDrawable(R.drawable.cl);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.tap)).getConstantState())) {
                retour = getDrawable(R.drawable.touch);

            } else if (d.getConstantState().equals(getDrawable((R.drawable.touch)).getConstantState())) {
                retour = getDrawable(R.drawable.touch);

            }


        return  retour;
    }

    public String ReadTxt() {
        //reading text from file
        //g:6302;d:6612;g:6928;d:7134;g:7364;d:8440;g:8804;d:9099;tap:9793;tap:10275;tap:10592;d:10741;g:11022;d:11374;g:11523;d:11832;b:11832;b:11832;h:11832;d:13751;g:14190;d:14520;tap:16631;tap:17174;tap:17707;tap:19197;tap:20495;d:21696;g:22114;d:22489;g:22846;b:22846;h:22846;b:22846;h:22846;
        String s="";
       // String big_deal="g:1301;tap:1867;tap:2400;tap:3699;tap:4233;d:5196;tap:6082;tap:6532;tap:6982;g:7385;d:7640;h:7640;b:7640;h:7640;d:10000;g:10624;d:11148;d:11520;d:11817;g:12056;d:12425;b:12425;h:12425;b:12425;h:12425;b:12425;h:12425;b:12425;d:17803;d:18165;g:18740;d:19046;g:19678;g:20533;g:21156;g:21428;d:21758;g:22029;b:22029;h:22029;b:22029;b:22029;h:22029;b:22029;d:30231;d:30799;d:31070;b:31070;h:31070;d:35531;g:35812;d:36212;tap:40275;tap:40825;tap:41475;b:41813;d:44173;g:44512;d:44751;g:45160;d:45399;b:45755;h:45755;b:45755;h:45755;d:48914;d:49332;d:49849;d:50129;d:50352;g:50559;tap:51107;tap:51690;tap:52291;tap:52889;tap:53506;tap:54123;g:55025;d:55351;tap:55906;tap:56839;d:57730;b:58028;h:58028;g:61942;d:62185;g:62516;d:62796;g:63414;d:63716;g:64383;d:64664;g:65265;d:66707;g:67016;d:67338;g:67632;d:68016;g:69167;d:69709;g:70071;d:72217;g:72440;d:72848;g:73936;d:74226;g:74562;d:74833;g:77192;d:77637;d:78059;tap:79069;g:80215;d:81149;g:81420;tap:82252;d:82559;g:82960;d:83218;g:83546;d:83826;g:84140;d:84429;tap:85202;tap:85668;g:85862;d:86839;tap:87651;tap:88200;g:89806;g:90719;g:91018;d:91879;g:92172;d:92700;g:93078;d:93402;g:93975;h:93975;b:93975;h:93975;b:93975;b:93975;h:93975;b:93975;b:93975;h:93975;b:93975;h:93975;b:93975;b:93975;b:93975;b:93975;b:93975;b:93975;b:93975;b:93975;d:110751;d:111392;d:112115;d:112580;g:113114;d:113825;g:114796;d:115063;tap:115843;tap:116428;tap:117078;tap:118297;b:118297";
       String retour="tap:6778;tap:8689;tap:10637;tap:12552;tap:14464;tap:16392;d:18073;g:19978;tap:22261;tap:26000;d:27658;g:29638;tap:31752;tap:33681;tap:36032;tap:37034;tap:37950;tap:39439;tap:40416;tap:43275;g:44029;d:44926;d:45931;g:46908;g:47875;d:49807;d:50737;d:51716;g:52680;g:53606;g:54595;tap:56264;tap:56746;tap:57213;tap:57696;tap:58662;tap:60546;tap:62522;d:63651;g:64217;d:65642;g:66082;tap:69186;tap:72072;tap:72476;tap:75898;tap:76275;tap:77836;tap:78241;g:81475;d:81907;g:82441;d:82939;tap:83641;tap:85550;tap:87423;d:89126;g:89674;g:90596;d:91051;d:91522;tap:93194;tap:95091;tap:98962;tap:100860;tap:102781;tap:103219;tap:104741;tap:105102;tap:106195;tap:106652;b:106652;h:106652;tap:113367;tap:113900;tap:114367;tap:116272;tap:116616;tap:116983;d:120849;d:121357;g:121829;g:123661;g:125129;g:125621;tap:127731;tap:128764;tap:129697";
        try {
            //FileInputStream fileIn=openFileInput(getApplicationContext().getFilesDir().getAbsolutePath() + "/sequence/"+nom_txt.get(pos-1));
            FileInputStream fileIn= new FileInputStream(getApplicationContext().getFilesDir().getAbsolutePath() + "/sequence/"+nom_txt.get(pos-1));
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];

            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }



    public class MyAnimationListener1 implements Animation.AnimationListener {
        ImageView view;
        int indiceVue;
        String symbole;
        public void setImage(ImageView view) {
            this.view = view;
        }
        public void addAction(String symbole) {


            this.symbole=symbole;
        }
        public  void setIndice(int indice){this.indiceVue=indice;}
        public void onAnimationEnd(Animation animation) {
// Do whatever you want
           /* if (zoneJeu.getChildCount() > 1 && zoneJeu.getChildAt(0).getClass()==ImageView.class) {
                zoneJeu.removeViewAt(0);
            }
            listeImagesAnimationsActives.remove(symbole);
            indiceActuelSequence++;
            if (indiceActuelSequence < couples.length) {
                dernierSymbole = (ReadTxt().split(";")[indiceActuelSequence]).split(":")[0];
            }*/

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // remove fragment from here

            if (zoneJeu.getChildCount() > 1 && zoneJeu.getChildAt(0).getClass()==ImageView.class) {
                //zoneJeu.removeViewAt(0);
                indiceActuelSequence++;
                //if(zoneJeu.getChildAt(0).getClass()==ImageView.class){




                    if (indiceActuelSequence < couples.length) {
                        Drawable d = ((ImageView) (zoneJeu.getChildAt(indiceActuelSequence))).getDrawable();
                        ((ImageView) (zoneJeu.getChildAt(indiceActuelSequence))).setImageDrawable(trouverSymbole2(d));
                        dernierSymbole = (sequences.get(pos-1).split(";")[indiceActuelSequence]).split(":")[0];
                    }
                    else {
                        dernierSymbole="";
                        listeImagesAnimationsActives.clear();

                        v.setOnTouchListener(null);

                        Intent j=new Intent(getApplicationContext(),ResultatsPartie.class);
                        j.putExtra("niveau",pos);
                        j.putExtra("score",score);
                        j.putExtra("nbTotalMvts",couples.length);
                        j.putExtra("manques",couples.length-score);
                        startActivity(j);

                    //    RelativeLayout popupLayout=(RelativeLayout) findViewById(R.id.fragment_resultats);
                        //((TextView) (popupLayout.findViewById(R.id.scoreValue))).setText(String.valueOf(score));

//                        View popupContentView = LayoutInflater.from(InGame.this).inflate(R.layout.fragment_resultats_partie, null);
//                        popupContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//
//                        final PopupWindow popupWindow = new PopupWindow(getApplicationContext());
//                        popupWindow.setContentView(popupContentView);
//
//                        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setAnimationStyle(R.style.popup_window_animation_phone);
//                        popupWindow.setBackgroundDrawable(new ColorDrawable(
//                                android.graphics.Color.TRANSPARENT));
//
//                        ((TextView) popupContentView.findViewById(R.id.niveauValue)).setText(String.valueOf(pos));
//                        ((TextView) popupContentView.findViewById(R.id.scoreValue)).setText(String.valueOf(score));
//                        ((TextView) popupContentView.findViewById(R.id.manquesValue)).setText(String.valueOf(score));
//
//
//                        Button rejouer=popupContentView.findViewById(R.id.rejouer);
//                        Button accepter=popupContentView.findViewById(R.id.accepter);
//
//                        rejouer.setOnClickListener(new View.OnClickListener() {
//
//                            public void onClick(View v) {
//                                // TODO Auto-generated method stub
//                                //  zoneJeu = (RelativeLayout)findViewById(R.id.bandeaupourfleche);
//                                 popupWindow.dismiss();
//
//                                 indiceActuelSequence=80;
//
//                                 lancerAnimation(sequence);
//
//
//                    }
//                });
//
//                        accepter.setOnClickListener(new View.OnClickListener() {
//
//                            public void onClick(View v) {
//                                // TODO Auto-generated method stub
//                                //  zoneJeu = (RelativeLayout)findViewById(R.id.bandeaupourfleche);
//                                popupWindow.dismiss();
//                                startActivity(new Intent(InGame.this,EcranAccueil.class));
//
//
//                            }
//                        });
//

                       // popupWindow.showAtLocation(getCurrentFocus(),Gravity.CENTER,0,0);
                    }
                }
            }
              //  }
            });
        }
        public void onAnimationRepeat(Animation animation) {
        }
        public void onAnimationStart(Animation animation) {
            listeImagesAnimationsActives.add(symbole);
          /*  zoneJeu.getChildAt(indiceVue).setVisibility(View.VISIBLE);

            zoneJeu.invalidate();*/

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

    ArrayList<Integer> listeMusique;
    int indicesequence;
    File gpxfile;
    int pos;
    String coupleMvtTemps;
    ArrayList<String> nom_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecran_ingame);
        i = getIntent();
        pos = i.getExtras().getInt("niveau");

        coupleMvtTemps="";
        nom_txt=new ArrayList<>();
        nom_txt.add("london_night.txt");
        nom_txt.add("new_begining.txt");
        nom_txt.add("night_life2.txt");
        nom_txt.add("progress.txt");
        nom_txt.add("reiswerk_lies_on_lies_update.txt");
        nom_txt.add("sleeperspaceborn_better.txt");
        nom_txt.add("reiswerk_tick_tock.txt");
        nom_txt.add("tobias_weber_between_worlds_instrumental.txt");
        nom_txt.add("tobias_weber_rooted_in_soil.txt");
        nom_txt.add("veezyn_i_want_to_see_you_again.txt");
        nom_txt.add("copperhead_platinum_donation.txt");
        nom_txt.add("jeffspeed68_two_pianos.txt");
        nom_txt.add("life_is_beautiful.txt");
        nom_txt.add("alexberoza_supernatural_instrumental.txt");
        nom_txt.add("one_project_blue.txt");
        nom_txt.add("pumping.txt");
        nom_txt.add("siobhand_the_quiet_hours.txt");
        nom_txt.add("stellarartwars_floating_through_time_saw_mix.txt");
        nom_txt.add("texasradiofish_knock_on_my_door.txt");
        nom_txt.add("unreal_dm_come_back.txt");
        nom_txt.add("vj_memes_paint_the_sky.txt");
        nom_txt.add("sackjo22_trala_the_let_s_never_be_far_mix.txt");
        nom_txt.add("snowflake_miracles.txt");
        nom_txt.add("unreal_dm_talk_to_me.txt");
        nom_txt.add("djlang59_drops_of_h2o_the_filtered_water_treatment.txt");
        nom_txt.add("snowflake_fever.txt");
        nom_txt.add("speck_moonlight_sonata_shifting_sun_mix.txt");
        nom_txt.add("duckett_what_cha_waitin_simple_club_mix.txt");
        nom_txt.add("daguilar_remember_the_name.txt");
        nom_txt.add("scomber_end_of_the_game.txt");
        listeMusique = new ArrayList<>();
        // **** ajout des musiques
      //  listeMusique.add(R.raw.tobias_weber_rooted_in_soil);
        listeMusique.add(R.raw.london_night);//niveau 1
        listeMusique.add(R.raw.new_begining);//niveau 2
        listeMusique.add(R.raw.night_life2);//niveau 3
        listeMusique.add(R.raw.progress);//niveau 4
        listeMusique.add(R.raw.reiswerk_lies_on_lies_update);//niveau 5
        listeMusique.add(R.raw.sleeperspaceborn_better);//niveau 6
        listeMusique.add(R.raw.reiswerk_tick_tock);//niveau 7
        listeMusique.add(R.raw.tobias_weber_between_worlds_instrumental);//niveau 8
        listeMusique.add(R.raw.tobias_weber_rooted_in_soil);//niveau 9
        listeMusique.add(R.raw.veezyn_i_want_to_see_you_again);//niveau 10
        listeMusique.add(R.raw.copperhead_platinum_donation);//niveau 11
        listeMusique.add(R.raw.jeffspeed68_two_pianos);//niveau 12
        listeMusique.add(R.raw.life_is_beautiful);//niveau 13
        listeMusique.add(R.raw.alexberoza_supernatural_instrumental);//niveau 14
        listeMusique.add(R.raw.one_project_blue);//niveau 15
        listeMusique.add(R.raw.pumping);//niveau 16
        listeMusique.add(R.raw.siobhand_the_quiet_hours);//niveau 17
        listeMusique.add(R.raw.stellarartwars_floating_through_time_saw_mix);//niveau 18
        listeMusique.add(R.raw.texasradiofish_knock_on_my_door);//niveau 19
        listeMusique.add(R.raw.unreal_dm_come_back);//niveau 20
        listeMusique.add(R.raw.vj_memes_paint_the_sky);//niveau 21
        listeMusique.add(R.raw.sackjo22_trala_the_let_s_never_be_far_mix);//niveau 22
        listeMusique.add(R.raw.snowflake_miracles);//niveau 23
        listeMusique.add(R.raw.unreal_dm_talk_to_me);//niveau 24
        listeMusique.add(R.raw.djlang59_drops_of_h2o_the_filtered_water_treatment);//niveau 25
        listeMusique.add(R.raw.snowflake_fever);//niveau 26
        listeMusique.add(R.raw.speck_moonlight_sonata_shifting_sun_mix);//niveau 27
        listeMusique.add(R.raw.duckett_what_cha_waitin_simple_club_mix);//niveau 28
        listeMusique.add(R.raw.daguilar_remember_the_name);//niveau 29
        listeMusique.add(R.raw.scomber_end_of_the_game);//niveau 30
        // **** fin ajout des musiques
        mPlayer = MediaPlayer.create(this, listeMusique.get(pos - 1));
        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());


        mPlayer.start();
        mChronometer.start();


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


        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        v.setOnTouchListener(touchListener);
        v.post(new Runnable() {
            @Override
            public void run() {
                widthView = v.getWidth(); //height is ready
            }
        });

        //  lancerAnimation(sequence);

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
            derniereAction.setVariable("tap");
            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
            coupleMvtTemps+="tap:" + String.valueOf(timeOfDerniereAction)+";";

            i.setImageDrawable(getDrawable(R.drawable.tap));
           // symboleUtilisateur.removeAllViews();
          //  symboleUtilisateur.addView(i);
            i.startAnimation(fadeOut);
            //  Toast.makeText(InGame.this, "tap", Toast.LENGTH_SHORT).show();
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
            derniereAction.setVariable("touch");
            timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
            coupleMvtTemps+="touch:" + String.valueOf(timeOfDerniereAction)+";";
            i.setImageDrawable(getDrawable(R.drawable.touch));
          //  symboleUtilisateur.removeAllViews();
          //  symboleUtilisateur.addView(i);
            i.startAnimation(fadeOut);
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



if (Math.abs(e2.getX() - e1.getX()) < Math.abs(e2.getY() - e1.getY())) {


                   if (e2.getY() - e1.getY() > 0) {

                           derniereAction.setVariable("h");
                           Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                   } else if (e2.getY() - e1.getY() < 0) {

                           derniereAction.setVariable("b");
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
                    derniereAction.setVariable("g");
                    timeOfDerniereAction = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    coupleMvtTemps+="g:" + String.valueOf(timeOfDerniereAction)+";";

                    i.setImageDrawable(getDrawable(R.drawable.fleche_gauche2));
                 //   symboleUtilisateur.removeAllViews();
                 //   symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                    Toast.makeText(InGame.this, "left", Toast.LENGTH_SHORT).show();
                } else if (event2.getX() - event1.getX() < 0) {
                    derniereAction.setVariable("d");
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

                    derniereAction.setVariable("h");
                    coupleMvtTemps+="h:" + String.valueOf(timeOfDerniereAction)+";";

                    i.setImageDrawable(getDrawable(R.drawable.fleche_haut2));
                  //  symboleUtilisateur.removeAllViews();
                  //  symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
                    //    Toast.makeText(InGame.this, "up", Toast.LENGTH_SHORT).show();

                } else if (event2.getY() - event1.getY() < 0) {

                    derniereAction.setVariable("b");
                    coupleMvtTemps+="b:" + String.valueOf(timeOfDerniereAction)+";";


                    i.setImageDrawable(getDrawable(R.drawable.fleche_bas2));
                   // symboleUtilisateur.removeAllViews();
                  //  symboleUtilisateur.addView(i);
                    i.startAnimation(fadeOut);
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
        } else if (id == R.id.action_stop) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                mDrawer.openDrawer(GravityCompat.START);
            }

            return true;
        }
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
