package com.example.demo.utils;


import com.example.demo.utils.firebaseModel.FcmMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.secret.Secret.FCM_SERVER_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/coupangeats-721e3/messages:send";

    private final ObjectMapper objectMapper;
    private String apiKey = FCM_SERVER_KEY;

    // Bearer 토큰에 해당하는 device로 푸시알림 전송
    public void sendMessageTo(String targetToken, String title, String body, String path) throws Exception {
        String message = makeMessage(targetToken, title, body, path);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, okhttp3.MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response  = client.newCall(request).execute();

        log.info(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body, String path) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        log.info(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);
    }

    // AccessToken 발급 받기
    private String getAccessToken() throws Exception {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        // accessToken 생성
        googleCredentials.refreshIfExpired();

        // GoogleCredential의 getAccessToken으로 토큰 받아오고 getTokenValue로 최종적으로 받는다.
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
