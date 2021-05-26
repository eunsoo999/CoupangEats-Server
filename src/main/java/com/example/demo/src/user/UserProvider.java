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

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
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
}
