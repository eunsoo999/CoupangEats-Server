package com.example.demo.utils;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.GetLocationRes;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.secret.Secret.*;

public class GeoCoding {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static GetLocationRes getLocation(String address) throws BaseException, UnsupportedEncodingException {
        String addr = URLEncoder.encode(address, "utf-8");
        String api = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(api);
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json"); // 원하는 포맷 JSON(기본값)
            http.setRequestProperty("X-NCP-APIGW-API-KEY-ID", GEO_CLIENT_ID); // 앱 등록 시 발급받은 Client ID
            http.setRequestProperty("X-NCP-APIGW-API-KEY", GEO_CLIENT_SECRET_KEY); // 앱 등록 시 발급 받은 Client Secret
            http.setRequestMethod("GET");
            http.connect();

            InputStreamReader in = new InputStreamReader(http.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObject1;
            JSONObject jsonObject2;
            JSONArray jsonArray;
            String longitude = "";
            String latitude = "";
            jsonObject1 = (JSONObject) parser.parse(sb.toString());
            jsonArray = (JSONArray) jsonObject1.get("addresses");

            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject2 = (JSONObject) jsonArray.get(i);
                if (jsonObject2.get("x") != null) {
                    longitude = (String) jsonObject2.get("x").toString();
                }
                if (jsonObject2.get("y") != null) {
                    latitude = (String) jsonObject2.get("y").toString();
                }
            }
            br.close();
            in.close();
            http.disconnect();
            if (latitude.isEmpty() || longitude.isEmpty()) {
                throw new BaseException(ADDRESSES_NOT_FOUND_LOCATION);
            }
            GetLocationRes getLocationRes = new GetLocationRes(latitude, longitude);
            return getLocationRes;
        } catch (Exception exception) {
            throw new BaseException(ADDRESSES_NOT_FOUND_LOCATION);
        }
    }
}
