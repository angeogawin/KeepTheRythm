package com.developpement.ogawi.keeptherythm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.List;
import java.util.Random;

public class ConfettiTest extends Activity {
    LinearLayout parent;
    CommonConfetti c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confettitest);
        parent=(LinearLayout) findViewById(R.id.parentConfetti);


     //   final List<Bitmap> allPossibleConfetti = constructBitmapsForConfetti();
// Alternatively, we provide some helper methods inside `Utils` to generate square, circle,
// and triangle bitmaps.
// Utils.generateConfettiBitmaps(new int[] { Color.BLACK }, 20 /* size */);

    /*   // final int numConfetti = allPossibleConfetti.size();
        final ConfettoGenerator confettoGenerator = new ConfettoGenerator() {
            @Override
            public Confetto generateConfetto(Random random) {
         //       final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
         //       return new BitmapConfetto(bitmap);
           // }
   //     };
*/

    }

}
