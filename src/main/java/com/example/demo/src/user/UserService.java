package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.address.AddressDao;
import com.example.demo.src.address.model.GetLocationRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
@Transactional(rollbackOn = BaseException.class)
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final AddressDao addressDao;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, AddressDao addressDao) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.addressDao = addressDao;
    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(DUPLICATED_EMAIL);
        }
        if(userProvider.checkPhone(postUserReq.getPhone()) == 1) {
            throw new BaseException(DUPLICATED_PHONE);
        }

        String pwd;
        try{
            //비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            return new PostUserRes(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetLocationRes updateUserAddressIdx(int userIdx, int addressIdx) throws BaseException {
        // 유저 존재 확인
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND);
        }
        // 유저가 등록한 주소인지 확인
        if (addressDao.checkAddressByOwner(addressIdx, userIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }

        try {
            // 선택한 주소의 위도, 경도 값 조회
            GetLocationRes location = addressDao.getLocation(addressIdx);

            // 유저 기본 주소 업데이트
            int updatedCount = userDao.updateUserAddressIdx(userIdx, addressIdx);
            if (updatedCount != 1) {
                throw new BaseException(FAILED_TO_UPDATE_USER_ADDRESS);
            }
            return location;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
