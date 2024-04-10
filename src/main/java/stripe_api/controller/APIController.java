package stripe_api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //REST returns a ResponseBody object that contains data in a format such as JSON or XML
public class APIController {
    @Value("${stripe.apiKey}")
    String stripeKey;

    private static int calculateOrderAmount(Object[] items) {
        //se calculeaza suma totala de plata
        return 1050; //10,50 lei
    }

    static class CreatePaymentItem {
        @JsonProperty("id")
        String id;
        public String getId() {
            return id;
        }
    }

    static class CreatePayment {
        @JsonProperty("items")
        CreatePaymentItem[] items;
        @JsonProperty("savePaymentMethod")
        boolean savePaymentMethod;
        @JsonProperty("userID")
        String userID;
        public CreatePaymentItem[] getItems() {
            return items;
        }
        public boolean getSavePaymentMethod() {
            return savePaymentMethod;
        }
        public String getUserID() {
            return userID;
        }
    }

    static class CreatePaymentResponse {
        private String clientSecret;
        public CreatePaymentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }
        public String getClientSecret() {
            return clientSecret;
        }
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment paymentRequest) throws StripeException {
        Stripe.apiKey = stripeKey;

        int orderAmount = calculateOrderAmount(paymentRequest.getItems());

        String customerId = paymentRequest.getUserID(); // ID-ul clientului din aplicatie
        Customer customer = null;
        try {
            customer = Customer.retrieve(customerId);//se cauta in Stripe Cuustomers dupa id
        } catch (StripeException e) { //daca nu exista(cel mai prob  e la prima plata) se creeaza unul nou pt a putea crea PaymentIntent
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setDescription(customerId)
                    .build();
            customer = Customer.create(params);
        }

        //payment intent ne returneaza ClientSecret - un id secret care ofera informatii despre plata
        PaymentIntentCreateParams paymentParams = PaymentIntentCreateParams.builder()
                .setAmount((long)orderAmount)
                .setCustomer(customer.getId())
                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                .setCurrency("ron")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(paymentParams);
        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }

}

