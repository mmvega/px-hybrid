package com.mercadopago.cordova.sdk;


import android.app.Activity;
import android.content.Intent;
import android.text.BidiFormatter;
import android.util.StringBuilderPrinter;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardIssuer;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Sites;
import com.mercadopago.model.Token;
import com.mercadopago.util.MercadoPagoUtil;


import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


public class MercadoPagoPlugin extends CordovaPlugin {
    private CallbackContext callback = null;
    
    
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        
        
        if (action.equals("startActivity")) {
            cordova.setActivityResultCallback (this);
            new MercadoPago.StartActivityBuilder()
            .setActivity(this.cordova.getActivity())
            .setPublicKey("TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a")
            .setCheckoutPreferenceId("150216849-ceed1ee4-8ab9-4449-869f-f4a8565d386f")
            .startCheckoutActivity();
            
            callback = callbackContext;
            
            
            return true;
            
            
        } else if (action.equals("startPaymentVault")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;
            BigDecimal b = new BigDecimal(data.getInt(1));
            new MercadoPago.StartActivityBuilder()
            .setActivity(this.cordova.getActivity())
            .setPublicKey(data.getString(0))
            .setAmount(b)
            .setSite(Sites.ARGENTINA)
            .startPaymentVaultActivity();
            
            return true;

        } else if (action.equals("startCardWithoutInstallments")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .startGuessingCardActivity();

            return true;
        } else if (action.equals("startCardWithInstallments")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            BigDecimal amount = new BigDecimal(data.getInt(1));
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .setAmount(amount)
                    .setSite(Sites.ARGENTINA)
                    .startCardVaultActivity();

            return true;
        } else if (action.equals("startPaymentMethods")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .startPaymentMethodsActivity();

            return true;
        } else if (action.equals("startIssuers")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            Gson gson = new Gson();
            PaymentMethod paymentMethod = gson.fromJson(data.getString(1), PaymentMethod.class);

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .setPaymentMethod(paymentMethod)
                    .startIssuersActivity();

            return true;
        } else if (action.equals("startInstallments")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            Gson gson = new Gson();
            PaymentMethod paymentMethod = gson.fromJson(data.getString(2), PaymentMethod.class);
            Issuer issuer = gson.fromJson(data.getString(3), Issuer.class);
            BigDecimal amount = new BigDecimal(data.getInt(1));
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .setSite(Sites.ARGENTINA)
                    .setAmount(amount)
                    .setIssuer(issuer)
                    .setPaymentMethod(paymentMethod)
                    .startInstallmentsActivity();

            return true;
        } else if (action.equals("startBankDeals")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .startBankDealsActivity();

            return true;
        } else if (action.equals("startCongrats")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

            Gson gson = new Gson();
            Payment payment = gson.fromJson(data.getString(1), Payment.class);
            PaymentMethod paymentMethod = gson.fromJson(data.getString(2), PaymentMethod.class);

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .setPayment(payment)
                    .setPaymentMethod(paymentMethod)
                    .startCongratsActivity();

            return true;
        } else if (action.equals("startInstructions")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;

           Gson gson = new Gson();
            Payment payment = gson.fromJson(data.getString(1), Payment.class);
            PaymentMethod paymentMethod = gson.fromJson(data.getString(2), PaymentMethod.class);

            new MercadoPago.StartActivityBuilder()
                    .setActivity(this.cordova.getActivity())
                    .setPublicKey(data.getString(0))
                    .setPayment(payment)
                    .setPaymentMethod(paymentMethod)
                    .startInstructionsActivity();

            return true;
        } else if (action.equals("createPayment")){
            cordova.setActivityResultCallback (this);
            callback = callbackContext;
            Gson gson = new Gson();
            final PaymentMethod paymentMethod = gson.fromJson(data.getString(8), PaymentMethod.class);
            int installments = data.getInt(9);
            Long cardIssuerId = data.getLong(10);
            String token = data.getString(11);

            BigDecimal amount = new BigDecimal(data.getInt(3));
            if (paymentMethod != null) {

                Item item = new Item(data.getString(1), data.getInt(2), amount);

                String paymentMethodId = paymentMethod.getId();

                MerchantPayment payment = new MerchantPayment(item, installments,
                        cardIssuerId, token, paymentMethodId, data.getLong(4), data.getString(5));

                // Enviar los datos a tu servidor
                MerchantServer.createPayment(this.cordova.getActivity(), data.getString(6), data.getString(7),
                        payment, new Callback<Payment>() {
                            @Override
                            public void success(Payment payment) {

                                if (MercadoPagoUtil.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
                                    Gson gson = new Gson();
                                    String mpPayment = gson.toJson(payment);
                                    String mpPaymentMethod = gson.toJson(paymentMethod);
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("payment", mpPayment);
                                        js.put("payment_methods", mpPaymentMethod);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    callback.success(js.toString());
                                } else {

                                    Gson gson = new Gson();
                                    String mpPayment = gson.toJson(payment);
                                    String mpPaymentMethod = gson.toJson(paymentMethod);
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("payment", mpPayment);
                                        js.put("payment_methods", mpPaymentMethod);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    callback.success(js.toString());
                                }
                            }

                            @Override
                            public void failure(ApiException apiException) {

                                // Ups, ha ocurrido un error.

                            }
                        });
            } else {
                Toast.makeText(this.cordova.getActivity(), "Invalid payment method", Toast.LENGTH_LONG).show();
            }

            return true;
        } else {

            return false;

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethod mppaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                Issuer mpissuer = (Issuer) data.getSerializableExtra("issuer");
                Token mptoken = (Token) data.getSerializableExtra("token");
                PayerCost mppayerCost = (PayerCost) data.getSerializableExtra("payerCost");
                Gson gson = new Gson();
                String paymentMethod = gson.toJson(mppaymentMethod);
                String issuer = gson.toJson(mpissuer);
                String token = gson.toJson(mptoken);
                String payerCost = gson.toJson(mppayerCost);
                JSONObject js = new JSONObject();
                try {
                    js.put("payment_method", paymentMethod);
                    js.put("issuer", issuer);
                    js.put("token", token);
                    js.put("payer_cost", payerCost);
                    
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                callback.success(js.toString());
            }
        }  else if(requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                PaymentMethod paymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                Issuer mpissuer = (Issuer) data.getSerializableExtra("issuer");
                Token mptoken = (Token) data.getSerializableExtra("token");

                Gson gson = new Gson();
                String pm = gson.toJson(paymentMethod);
                String issuer = gson.toJson(mpissuer);
                String token = gson.toJson(mptoken);

                JSONObject js = new JSONObject();

                try {
                    js.put("payment_method", pm);
                    js.put("issuer", issuer);
                    js.put("token", token);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                callback.success(js.toString());

            }
        } else if (requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                PaymentMethod mppaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                Issuer mpissuer = (Issuer) data.getSerializableExtra("issuer");
                Token mptoken = (Token) data.getSerializableExtra("token");
                PayerCost mppayerCost = (PayerCost) data.getSerializableExtra("payerCost");


                Gson gson = new Gson();
                String paymentMethod = gson.toJson(mppaymentMethod);
                String issuer = gson.toJson(mpissuer);
                String token = gson.toJson(mptoken);
                String payerCost = gson.toJson(mppayerCost);
                JSONObject js = new JSONObject();
                try {
                    js.put("payment_method", paymentMethod);
                    js.put("issuer", issuer);
                    js.put("token", token);
                    js.put("payer_cost", payerCost);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                callback.success(js.toString());

            } else {
                if ((data != null) &&
                        (data.getSerializableExtra("mpException") != null)) {
                    MPException exception
                            = (MPException) data.getSerializableExtra("mpException");
                }
            }
        }else if(requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                PaymentMethod paymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

                Gson gson = new Gson();
                callback.success(gson.toJson(paymentMethod).toString());
            } else {
                if ((data != null) &&
                        (data.getSerializableExtra("mpException") != null)) {
                    MPException exception
                            = (MPException) data.getSerializableExtra("mpException");
                }
            }
        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Issuer issuer = (Issuer) data.getSerializableExtra("issuer");

                Gson gson = new Gson();
                callback.success(gson.toJson(issuer).toString());

            } else {
                if ((data != null) &&
                        (data.getSerializableExtra("mpException") != null)) {
                    MPException exception
                            = (MPException) data.getSerializableExtra("mpException");
                }
            }
        }else if(requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                PayerCost payerCost = (PayerCost) data.getSerializableExtra("payerCost");

                Gson gson = new Gson();
                callback.success(gson.toJson(payerCost).toString());
            } else {
                if ((data != null) &&
                        (data.getSerializableExtra("mpException") != null)) {
                    MPException exception
                            = (MPException) data.getSerializableExtra("mpException");
                }
            }
        }else if (requestCode == MercadoPago.CHECKOUT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Payment payment=(Payment)data.getSerializableExtra("payment");
                Gson gson = new Gson();
                String json = gson.toJson(payment);
                callback.success(json);
            }
        }
        
        
    }
}
