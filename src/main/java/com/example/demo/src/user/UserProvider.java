package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        if (userDao.checkEmail(postLoginReq.getEmail()) == 0) {  //ID를 가진 활성 유저가 없다.
            if (userDao.checkDeletedUser(postLoginReq.getEmail()) == 1) { //탈퇴한 회원이라면
                throw new BaseException(USERS_DELETED);
            } else {
                // 활성유저도 아니고 탈퇴한 회원도 아니라면 로그인 X
                throw new BaseException(FAILED_TO_LOGIN);
            }
        }

        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
    
    public GetUserRes retrieveUser(int userIdx) throws BaseException{
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        try{
            return userDao.getUser(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPhone(String phone) throws BaseException {
        try {
            return userDao.checkPhone(phone);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getMaskingEmailByPhone(String phone) throws BaseException {
        try{
            return userDao.selectMaskingEmailByPhone(phone);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetExistAddressRes checkExistsBasicAddress(int userIdx, String type) throws BaseException {
        GetExistAddressRes getExistAddressRes = new GetExistAddressRes();
        try{
            if(type.equalsIgnoreCase("HOME")) {
                int result = userDao.checkUserHomeAddress(userIdx);
                if (result == 1) {
                    getExistAddressRes.setExistsStatus("Y");
                } else {
                    getExistAddressRes.setExistsStatus("N");
                }
            } else if(type.equalsIgnoreCase("COMPANY")) {
                int result = userDao.checkUserCompanyAddress(userIdx);
                if (result == 1) {
                    getExistAddressRes.setExistsStatus("Y");
                } else {
                    getExistAddressRes.setExistsStatus("N");
                }
            }
            return getExistAddressRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserAddressRes getUserAddress(int userIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        // 유저가 기본 주소를 선택한 상태인지 확인
        Integer seletedAddressIdx = userDao.selectUserAddressIdx(userIdx);
        if (seletedAddressIdx == null) {
            return new GetUserAddressRes(0, null, null, null);
        }
        try {
            return userDao.selectUserAddress(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostLoginRes kakaoLogin(KaKaoUserInfo kaKaoUserInfo) throws BaseException {
        int userIdx;
        String jwt;
        // 카카오에서 받아온 사용자 정보의 이메일을 가지고 User테이블에 있는지 확인한다.
        if (userDao.checkEmail(kaKaoUserInfo.getEmail()) == 1) {
            // 해당 이메일이 카카오 가입으로 가입된 계정이 맞는지 확인한다.
            if (userDao.checkKakaoUserEmail(kaKaoUserInfo.getEmail()) == 1) {
                //카카오 가입 이메일이 맞다면 로그인 처리
                userIdx = userDao.getUserIdxByEmail(kaKaoUserInfo.getEmail());
                jwt = jwtService.createJwt(userIdx);
            } else {
                throw new BaseException(USERS_INAPP_EXISTS); // 해당 이메일로 자체 이메일가입한 상태라면 카카오로그인, 가입 X, 자체로그인으로.
            }
        } else { // 가입이 되어 있지 않다면 가입 진행
            PostUserReq kakaoSignUp = new PostUserReq(kaKaoUserInfo.getEmail(), null, kaKaoUserInfo.getUserName(), null);
            userIdx = userDao.createUser(kakaoSignUp, "KAKAO");
            jwt = jwtService.createJwt(userIdx);
        }
        return new PostLoginRes(userIdx, jwt);
    }
}
