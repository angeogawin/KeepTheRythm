package com.developpement.ogawi.keeptherythm.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.developpement.ogawi.keeptherythm.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class MainActivityPayment extends AppCompatActivity
{
    TextView m_response;
    static Cart m_cart;
    EditText montant;
    PayPalConfiguration m_configuration;
    // the id is the link to the paypal account, we have to create an app and get its id
    String m_paypalClientId = "Ae5Gv7fzSt3mQD98XhZL6LrwW_jDC0LRPUsmNlbpNZe71Edv_-R6C_9KBVnMWNv35742oPqOnPQt0O4e";
    Intent m_service;
    int m_paypalRequestCode = 997; // or any number you want
    GifImageView gifI;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpayment);


        montant=findViewById(R.id.montantdon);
        m_response = (TextView) findViewById(R.id.response);
        gifI=findViewById(R.id.gif);
        String[] gifsNames={"thankyoucat","thankyou_applause","thankyou_himym","thankyou_robocop","thankyou_lapin"};
        gifI.setImageResource(getResources().getIdentifier(gifsNames[new Random().nextInt(gifsNames.length)],"drawable",getPackageName()));

        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION) // sandbox for test, production for real
                .clientId(m_paypalClientId);

        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration); // configuration above
        startService(m_service); // paypal service, listening to calls to paypal app

        m_cart = new Cart();


        //m_cart.addToCart(product);

        montant.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                m_response.setText("Total  = " + montant.getText().toString()+ "€");
              }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.length() != 0) {
                    // extension.getText().clear();
                }
            }
        });


    }

    void pay(View view)
    {
        if(!montant.getText().toString().isEmpty()) {
            PayPalPayment cart = new PayPalPayment(new BigDecimal(Double.valueOf(montant.getText().toString())), "EUR", "Cart",
                    PayPalPayment.PAYMENT_INTENT_SALE);


            Intent intent = new Intent(this, PaymentActivity.class); // it's not paypalpayment, it's paymentactivity !
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, cart);
            startActivityForResult(intent, m_paypalRequestCode);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Aucun montant spécifié", Toast.LENGTH_LONG)
                    .show();
        }
    }

    void viewCart(View view)
    {
        Intent intent = new Intent(this, ViewCart.class);
        m_cart = m_cart;
        startActivity(intent);
    }

    void reset(View view)
    {
        m_response.setText("Total du don = 0 €");
        m_cart.empty();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == m_paypalRequestCode)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // we have to confirm that the payment worked to avoid fraud
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null)
                {
                    String state = confirmation.getProofOfPayment().getState();

                    if(state.equals("approved")) // if the payment worked, the state equals approved
                        m_response.setText("Paiement réalisé avec succès");
                    else
                        m_response.setText("Erreur lors du paiement");
                }
                else
                    m_response.setText("Erreur de confirmation lors du paiement");
            }
        }
    }
}
