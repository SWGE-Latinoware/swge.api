package celtab.swge.controller;

import celtab.swge.dto.PromotionResponseDTO;
import celtab.swge.dto.RegistrationRequestDTO;
import celtab.swge.dto.RegistrationResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Edition;
import celtab.swge.model.enums.PaymentType;
import celtab.swge.model.registration.Registration;
import celtab.swge.model.registration.pagseguro.*;
import celtab.swge.model.user.User;
import celtab.swge.service.GenericService;
import celtab.swge.service.RegistrationService;
import celtab.swge.util.ClassPathUtils;
import celtab.swge.util.DateTimeUtils;
import celtab.swge.util.email.EmailOptions;
import celtab.swge.util.email.EmailSender;
import celtab.swge.util.template_processor.EmailTemplateProcessor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/registrations/payment")
public class PaymentController extends GenericController<Registration, Long, RegistrationRequestDTO, RegistrationResponseDTO> implements DateTimeUtils, ClassPathUtils {

    private static final String FIELD_STATUS = "status";
    private static final String WRONG_MESSAGE = "Something went wrong with the charge request!";
    private final RegistrationService registrationService;
    private final RestTemplate restTemplate;
    private final EmailSender emailSender;
    private final EmailTemplateProcessor emailTemplateProcessor;
    @Value("${pagseguro.charge.url}")
    private String pagSeguroChargeUrl;
    @Value("${pagseguro.pix.url}")
    private String pagSeguroPixUrl;
    @Value("${pagseguro.token}")
    private String pagSeguroToken;
    @Value("${pagseguro.secure.token}")
    private String pagSeguroSecureToken;
    @Value("${pagseguro.certificate.ssl.password}")
    private String certificatePassword;
    @Value("${paypal.url}")
    private String payPalUrl;
    @Value("${paypal.client.id}")
    private String payPalClientId;
    @Value("${paypal.secret}")
    private String payPalSecret;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${pagseguro.public.key}")
    private String pagSeguroPublicKey;
    @Value("${pagseguro.timestamp}")
    private Long pagSeguroTimestamp;

    @Value("${receiver.name}")
    private String receiverName;

    @Value("${receiver.key}")
    private String receiverKey;

    public PaymentController(GenericService<Registration, Long> service, ModelMapper modelMapper, ObjectMapper objectMapper, RegistrationService registrationService, RestTemplateBuilder builder, EmailSender emailSender, EmailTemplateProcessor emailTemplateProcessor) {
        super(service, modelMapper, objectMapper);
        this.registrationService = registrationService;
        this.restTemplate = builder.build();
        this.emailSender = emailSender;
        this.emailTemplateProcessor = emailTemplateProcessor;
    }

    private RestTemplate createRestTemplate() {
        try {
            var jks = getResourceFromClassPath("ssl/swge.jks");
            var p12 = getResourceFromClassPath("ssl/swge.p12");

            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                    jks.getURL(),
                    certificatePassword.toCharArray(), acceptingTrustStrategy
                )
                .loadKeyMaterial(
                    p12.getURL(),
                    certificatePassword.toCharArray(),
                    certificatePassword.toCharArray())
                .build();

            SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory).build();

            HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, "Something went wrong on RestTemplate Build");
        }
    }

    private void sendPaymentStatusChange(String userEmail, PaymentStatus paymentStatus, Edition edition) throws ControllerException {
        try {
            var options = new EmailOptions();
            options.setFrom(emailFrom);
            options.setTo(userEmail);
            options.setSubject("Estado da Inscrição");
            options.setIsTextHTML(true);

            var html = "";
            switch (paymentStatus) {
                default:
                case CREATED:
                    html = getContentFromClassPath("mail/paymentCreated.html");
                    break;
                case PAID:
                    html = getContentFromClassPath("mail/paymentPaid.html");
                    break;
                case VOIDED:
                    html = getContentFromClassPath("mail/paymentVoided.html");
                    break;
                case REFUNDED:
                    html = getContentFromClassPath("mail/paymentRefunded.html");
                    break;
            }
            var user = new User();
            user.setEmail(userEmail);
            emailTemplateProcessor.setTo(user);
            emailTemplateProcessor.setEdition(edition);
            options.setText(emailTemplateProcessor.processTemplate(html));
            emailSender.sendEmail(options);
        } catch (Exception e) {
            throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public HttpHeaders createHttpHeader(String token) {
        var accept = new ArrayList<MediaType>();
        accept.add(MediaType.valueOf(MediaType.ALL_VALUE));

        var httpHeader = new HttpHeaders();
        httpHeader.setContentType(MediaType.APPLICATION_JSON);
        httpHeader.setAccept(accept);
        httpHeader.setBearerAuth(token);

        return httpHeader;
    }

    private String getPayPalAccessToken() {
        var httpHeader = new HttpHeaders();
        httpHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeader.setBasicAuth(payPalClientId, payPalSecret);

        var map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        var httpEntityForToken = new HttpEntity<>(map, httpHeader);

        var responseForToken = restTemplate.postForEntity(payPalUrl + "/v1/oauth2/token", httpEntityForToken, HashMap.class);
        return Objects.requireNonNull(responseForToken.getBody()).get("access_token").toString();
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @PostMapping("/{registrationId}/card-charge")
    public RegistrationResponseDTO chargeCardRegistration(
        @PathVariable Long registrationId,
        @RequestBody Payment payment
    ) {
        var registration = registrationService.findOne(registrationId);
        var totalValue = registration.getFinalPrice();

        var charge = new CardCharge();

        var card = new Card(payment.getCard());
        var paymentMethod = new PaymentMethod(card, "CREDIT_CARD", payment.getInstallments(), Boolean.TRUE, registration.getEdition().getName());
        charge.setPaymentMethod(paymentMethod);

        var totalPrice = Integer.valueOf(String.valueOf(totalValue).replace(".", "0"));

        var amount = new Amount(totalPrice, "BRL");
        charge.setAmount(amount);

        var description = String.format("Pagamento referente a inscrição no evento %s, no valor de R$ %s.", registration.getEdition().getName(), totalValue);

        charge.setReferenceId(registration.getUserRegistrationId());
        charge.setDescription(description);

        var httpEntity = new HttpEntity<>(charge, createHttpHeader(pagSeguroToken));

        var obj = restTemplate.postForEntity(pagSeguroChargeUrl, httpEntity, HashMap.class);
        if (obj.getStatusCode() != CREATED || obj.getBody() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, WRONG_MESSAGE);
        }

        if (Objects.requireNonNull(obj.getBody()).get(FIELD_STATUS).equals("DECLINED")) {
            throw new ControllerException(BAD_REQUEST, "Charge was declined, if the problem persist, contact your bank!");
        }

        registration.setIdentifier((String) Objects.requireNonNull(obj.getBody()).get("id"));
        registration.setPaymentType(PaymentType.PAGSEGURO);

        registrationService.save(registration);
        sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.CREATED, registration.getEdition());

        return mapTo(registration, RegistrationResponseDTO.class);
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @PostMapping("/{registrationId}/pix-charge")
    public Object chargePixRegistration(
        @PathVariable Long registrationId
    ) {
        var registration = registrationService.findOne(registrationId);
        var charge = new PixCharge();

        var totalPrice = registration.getFinalPrice();

        var amount = new HashMap<String, String>();
        amount.put("original", String.format("%.2f", totalPrice).replace(',', '.'));
        charge.setValue(amount);

        var calendar = new HashMap<String, Integer>();
        calendar.put("expiracao", 86400);
        charge.setCalendar(calendar);

        var description = String.format("Pagamento referente a inscrição no evento %s, no valor de R$ %s.", registration.getEdition().getName(), totalPrice);

        charge.setPayerRequest(description);
        charge.setKey(receiverKey);

        var httpEntity = new HttpEntity<>(charge, createHttpHeader(pagSeguroSecureToken));

        var customRestTemplate = createRestTemplate();
        var txId = (registration.getUserRegistrationId().replace("-", "") + Instant.now().getEpochSecond()).substring(0, 35);
        registration.setIdentifier(txId);

        var obj = customRestTemplate.exchange(String.format("%s/cob/%s", pagSeguroPixUrl, txId), HttpMethod.PUT, httpEntity, HashMap.class);
        if (obj.getStatusCode() != CREATED || obj.getBody() == null) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, WRONG_MESSAGE);
        }

        registration.setPaymentType(PaymentType.PIX);

        registrationService.save(registration);
        sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.CREATED, registration.getEdition());

        return new PixOrder(obj.getBody().get("urlImagemQrCode").toString(), obj.getBody().get("pixCopiaECola").toString());
    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void checkAllRegistrationStatus() {
        var registrations = registrationService.findAll().stream().filter(registration -> registration.getPayed().equals(Boolean.FALSE)).filter(registration -> registration.getIdentifier() != null);

        registrations.forEach(registration -> {
            var accept = new ArrayList<MediaType>();
            accept.add(MediaType.valueOf(MediaType.ALL_VALUE));

            var httpHeader = new HttpHeaders();
            httpHeader.setContentType(MediaType.APPLICATION_JSON);
            httpHeader.setAccept(accept);

            String url;
            switch (registration.getPaymentType()) {
                case PAGSEGURO:
                    url = pagSeguroChargeUrl + "/" + registration.getIdentifier();
                    httpHeader.setBearerAuth(pagSeguroToken);
                    break;
                case PIX:
                    url = pagSeguroPixUrl + "/cob/" + registration.getIdentifier() + "?0";
                    httpHeader.setBearerAuth(pagSeguroSecureToken);
                    break;
                case PAYPAL:
                    var payPalToken = getPayPalAccessToken();
                    url = payPalUrl + "/v2/checkout/orders/" + registration.getIdentifier();
                    httpHeader.setBearerAuth(payPalToken);
                    break;
                case NONE:
                default:
                    return;
            }

            var customRestTemplate = createRestTemplate();

            var httpEntity = new HttpEntity<>(null, httpHeader);
            var response = (registration.getPaymentType() == PaymentType.PIX ? customRestTemplate : restTemplate).exchange(url, HttpMethod.GET, httpEntity, HashMap.class);
            if (response.getStatusCode() != OK && response.getStatusCode() != CREATED || response.getBody() == null) {
                throw new ControllerException(HttpStatus.BAD_REQUEST, WRONG_MESSAGE);
            }
            if (response.getBody().get(FIELD_STATUS).equals("PAID") || response.getBody().get(FIELD_STATUS).equals("COMPLETED") || response.getBody().get(FIELD_STATUS).equals("CONCLUIDA")) {
                registration.setPayed(Boolean.TRUE);
                sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.PAID, registration.getEdition());
            }
            if (response.getBody().get(FIELD_STATUS).equals("VOIDED")) {
                registration.setIdentifier(null);
                registration.setPaymentType(PaymentType.NONE);
                sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.VOIDED, registration.getEdition());
            }
        });
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @GetMapping("/{registrationId}/promotion")
    public PromotionResponseDTO getPromotion(@PathVariable Long registrationId) {
        var registration = registrationService.findOne(registrationId);
        if (registration.getPromotion() != null) return mapTo(registration.getPromotion(), PromotionResponseDTO.class);
        return null;
    }

    @GetMapping("/pix-info")
    public PixInfo getPixInfo() {
        return new PixInfo(receiverName, receiverKey);
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @GetMapping("/{registrationId}/paypal/create-order")
    public String createOrder(@PathVariable Long registrationId) {
        var registration = registrationService.findOne(registrationId);
        var totalValue = registration.getFinalPrice();

        var accessToken = getPayPalAccessToken();

        var httpHeader = new HttpHeaders();
        httpHeader.setContentType(MediaType.APPLICATION_JSON);
        httpHeader.setBearerAuth(accessToken);

        var amount = new HashMap<>();
        amount.put("currency_code", "BRL");
        amount.put("value", totalValue);

        var units = new HashMap<String, Object>();
        units.put("amount", amount);

        var listUnits = new ArrayList<HashMap<String, Object>>();
        listUnits.add(units);

        var data = new HashMap<>();
        data.put("intent", "CAPTURE");
        data.put("purchase_units", listUnits);

        var httpEntity = new HttpEntity<>(data, httpHeader);

        var response = restTemplate.postForEntity(payPalUrl + "/v2/checkout/orders", httpEntity, HashMap.class);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == CREATED) {
            sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.CREATED, registration.getEdition());
            return Objects.requireNonNull(response.getBody()).get("id").toString();
        }

        return null;
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @PostMapping("/{registrationId}/paypal/save-order")
    public void saveOrder(@PathVariable Long registrationId, @RequestBody String order) throws JsonProcessingException {
        var registration = registrationService.findOne(registrationId);
        var objectMapper = new ObjectMapper();
        var orderMap = objectMapper.readValue(order, HashMap.class);

        registration.setIdentifier(orderMap.get("id").toString());
        registration.setPaymentType(PaymentType.PAYPAL);

        if (orderMap.get(FIELD_STATUS).equals("COMPLETED")) {
            sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.PAID, registration.getEdition());
            registration.setPayed(Boolean.TRUE);
        }

        registrationService.save(registration);
    }

    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @PostMapping("/{registrationId}/cancel-order")
    public void cancelOrder(@PathVariable Long registrationId) {
        var registration = registrationService.findOne(registrationId);

        if (registration.getPaymentType() == PaymentType.NONE || registration.getIdentifier() == null) {
            return;
        }

        if (registration.getPaymentType() != PaymentType.NONE && registration.getIdentifier() == null) {
            throw new ControllerException(BAD_REQUEST, "Requested Charge has a type but wasn't made");
        }

        if (Boolean.TRUE.equals(registration.getPayed()) && registration.getEdition().getInitialDate().before(Date.from(Instant.now()))) {
            throw new ControllerException(BAD_REQUEST, "Cannot cancel inscription when event already started");
        }

        var accept = new ArrayList<MediaType>();
        accept.add(MediaType.valueOf(MediaType.ALL_VALUE));

        var httpHeader = new HttpHeaders();
        httpHeader.setContentType(MediaType.APPLICATION_JSON);
        httpHeader.setAccept(accept);

        if (registration.getPaymentType() == PaymentType.PAYPAL) {
            var accessToken = getPayPalAccessToken();
            httpHeader.setBearerAuth(accessToken);

            var httpEntity = new HttpEntity<>(null, httpHeader);
            var url = payPalUrl + "/v2/checkout/orders/" + registration.getIdentifier();
            var responseGet = restTemplate.exchange(url, HttpMethod.GET, httpEntity, HashMap.class);

            if (responseGet.getStatusCode() == OK || responseGet.getStatusCode() == CREATED) {
                var objectMapper = new ObjectMapper();
                CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);
                var body = Objects.requireNonNull(responseGet.getBody());
                ArrayList<HashMap<String, Object>> purchaseUnits = objectMapper.convertValue(body.get("purchase_units"), listType);
                var payment = objectMapper.convertValue(purchaseUnits.get(0).get("payments"), HashMap.class);
                ArrayList<HashMap<String, Object>> captures = objectMapper.convertValue(payment.get("captures"), listType);
                var id = captures.get(0).get("id");
                var status = captures.get(0).get(FIELD_STATUS);

                if (!status.equals("REFUNDED")) {
                    var responsePost = restTemplate.postForEntity(payPalUrl + "/v2/payments/captures/" + id + "/refund", httpEntity, HashMap.class);
                    if (responsePost.getStatusCode() != CREATED) {
                        throw new ControllerException(BAD_REQUEST, "Refund payment went wrong");
                    }
                }

                registration.setIdentifier(null);
                registration.setPaymentType(PaymentType.NONE);
            }
        } else if (registration.getPaymentType() == PaymentType.PAGSEGURO) {
            httpHeader.setBearerAuth(pagSeguroToken);

            var totalValue = registration.getFinalPrice();

            var refundValue = String.valueOf(totalValue).replace(".", "0");

            var value = new HashMap<String, String>();
            value.put("value", refundValue);

            var amount = new HashMap<String, Object>();
            amount.put("amount", value);

            var httpEntity = new HttpEntity<>(null, httpHeader);
            var url = pagSeguroChargeUrl + "/" + registration.getIdentifier();

            var response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, HashMap.class);
            if (response.getStatusCode() == OK || response.getStatusCode() == CREATED) {
                if (Objects.requireNonNull(response.getBody()).get(FIELD_STATUS).equals("PAID") || Objects.requireNonNull(response.getBody()).get(FIELD_STATUS).equals("AUTHORIZED")) {
                    var postHttpEntity = new HttpEntity<>(amount, httpHeader);
                    var responsePost = restTemplate.postForEntity(url + "/cancel", postHttpEntity, HashMap.class);
                    if (responsePost.getStatusCode() != CREATED) {
                        throw new ControllerException(BAD_REQUEST, "Refund payment went wrong!");
                    }
                }
                return;
            }
            throw new ControllerException(BAD_REQUEST, "Retrieve of charge went wrong!");
        } else if (registration.getPaymentType() == PaymentType.PIX) {

            var pixRestTemplate = createRestTemplate();

            var url = pagSeguroPixUrl + "/cob/" + registration.getIdentifier() + "?0";

            var totalValue = registration.getFinalPrice();

            httpHeader.setBearerAuth(pagSeguroSecureToken);

            var httpEntityGet = new HttpEntity<>(null, httpHeader);

            var responseGet = pixRestTemplate.exchange(url, HttpMethod.GET, httpEntityGet, HashMap.class);

            if (responseGet.getStatusCode() != OK && responseGet.getStatusCode() != CREATED) {
                throw new ControllerException(BAD_REQUEST, "Something went wrong with the Http Get request of Pix charge");
            }

            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class);
            var bodyPix = Objects.requireNonNull(responseGet.getBody());
            ArrayList<HashMap<String, Object>> pix = objectMapper.convertValue(bodyPix.get("pix"), listType);
            var e2eid = Boolean.FALSE.equals(pix.isEmpty()) ? pix.get(0).get("endToEndId") : null;

            if (e2eid == null && responseGet.getBody().get(FIELD_STATUS).equals("ATIVA")) {
                sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.REFUNDED, registration.getEdition());
                registration.setPaymentType(PaymentType.NONE);
                registration.setIdentifier(null);

                registrationService.save(registration);
                return;
            }
            if (e2eid == null) {
                throw new ControllerException(BAD_REQUEST, "Charge is not payed to refund");
            }

            var id = registration.getUserRegistrationId().replace("-", "");

            var body = new HashMap<String, String>();
            body.put("valor", String.valueOf(totalValue));

            var httpEntity = new HttpEntity<>(body, httpHeader);

            var refundUrl = pagSeguroPixUrl + "/pix/" + e2eid + "/devolucao/" + id;
            var response = restTemplate.exchange(refundUrl, HttpMethod.PUT, httpEntity, HashMap.class);
            if (response.getStatusCode() != OK || response.getStatusCode() != CREATED) {
                throw new ControllerException(BAD_REQUEST, "Something went wrong with the Pix Refund");
            }
        }

        sendPaymentStatusChange(registration.getUser().getEmail(), PaymentStatus.REFUNDED, registration.getEdition());
        registration.setPaymentType(PaymentType.NONE);
        registration.setIdentifier(null);

        registrationService.save(registration);
    }

    @GetMapping(value = "/public-key", produces = MediaType.APPLICATION_JSON_VALUE)
    public PublicKey getPublicKey() {
        return new PublicKey();
    }

    public enum PaymentStatus {
        CREATED,
        PAID,
        VOIDED,
        REFUNDED
    }

    @Data
    @AllArgsConstructor
    public static class PixOrder {
        @JsonProperty("urlImagemQrCode")
        private String qrCode;
        @JsonProperty("pixCopiaECola")
        private String code;
    }

    @Data
    @AllArgsConstructor
    public static class PixInfo {
        private String name;
        private String key;
    }

    @Data
    public class PublicKey {
        @JsonProperty("public_key")
        private final String pbKey;
        @JsonProperty("created_at")
        private final Long timestamp;

        public PublicKey() {
            this.timestamp = pagSeguroTimestamp;
            this.pbKey = pagSeguroPublicKey;
        }
    }
}
