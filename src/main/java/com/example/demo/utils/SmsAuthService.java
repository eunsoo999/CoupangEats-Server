package com.example.demo.utils;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.PhoneAuthInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.config.BaseResponseStatus.FAILED_TO_SEND_PHONE_AUTH;
import static com.example.demo.config.secret.Secret.*;

@Service
public class SmsAuthService {
    public PhoneAuthInfo sendPhoneAuth(String toPhone) throws BaseException {
        int authNumber = (int) (Math.random() * (99999 - 10000 + 1)) + 10000; // 인증번호 난수 생성
        String accessKey = SENS_ACCESS_KEY;
        String serviceId = SENS_SERVICE_ID;
        String method = "POST";
        String timestamp = Long.toString(System.currentTimeMillis());
        String tophone = toPhone;

        String requestURL = "https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages";

        //JSON을 활용한 doby data 생성
        JSONObject bodyJson = new JSONObject();
        JSONObject toJson = new JSONObject();
        JSONArray toArr = new JSONArray();

        toJson.put("to", tophone); // 메시지 수신자
        toArr.add(toJson);

        bodyJson.put("type", "sms");
        bodyJson.put("contentType", "comm");
        bodyJson.put("countryCode", "82");
        bodyJson.put("from", "01099686284");
        bodyJson.put("content", "[13th-쿠팡이츠] 인증번호\n" + authNumber);
        bodyJson.put("messages", toArr);

        String body = bodyJson.toJSONString();
        //System.out.println("body: : " + body);

        try {
            URL url = new URL(requestURL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("content-type", "application/json; charset=utf-8");
            con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            con.setRequestProperty("x-ncp-iam-access-key", accessKey);
            con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(timestamp));
            con.setRequestMethod(method); // POST 방식
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(body.getBytes());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            //System.out.println("responseCode : " + responseCode);

            if (responseCode == 202) { //정상
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            con.disconnect();
            if (responseCode != 202) { // 정상이 아니라면
                throw new BaseException(FAILED_TO_SEND_PHONE_AUTH);
            }
            return new PhoneAuthInfo(tophone, String.valueOf(authNumber));
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_SEND_PHONE_AUTH);
        }
    }
    private static String makeSignature(String timestamp) throws BaseException {
        String encodeBase64String = "";
        String space = " "; // one space
        String newLine = "\n"; // new line
        String method = "POST";
        String url = String.format("/sms/v2/services/%s/messages", SENS_SERVICE_ID);

        String message = new StringBuilder().append(method).append(space).append(url).append(newLine)
                .append(timestamp).append(newLine).append(SENS_ACCESS_KEY).toString();

        try {
            SecretKeySpec signingKey = new SecretKeySpec(SENS_SECRET_KEY.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = java.util.Base64.getEncoder().encodeToString(rawHmac);
            return encodeBase64String;
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_SEND_PHONE_AUTH);
        }
    }
}
