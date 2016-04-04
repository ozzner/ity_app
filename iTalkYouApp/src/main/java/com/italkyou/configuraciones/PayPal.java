package com.italkyou.configuraciones;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created by RenzoD on 01/07/2015.
 */
public class PayPal {

    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String CONFIG_CLIENT_ID = "AYsx7R5LL_LP5mAegK5fHQWJWbvznM4fyhkdPkpudzPc0Oatm6BKW2IZagpdoYDIMWfuWjQ4qXk14apA"; //"credential-from-developer.paypal.com"
    public static final String CONFIG_RECEIVER_EMAIL = "rsantillanc-facilitator@gmail.com";
    public static final String PAYER_ID = "your-customer-id-in-your-system";
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";
    public static final String ES = "es";
    public static final String EN = "en";
    public static final int REQUEST_CODE_PAYMENT = 19;
}
