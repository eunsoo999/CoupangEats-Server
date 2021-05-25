package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AddressService {
    private final AddressProvider addressProvider;
    private final AddressDao addressDao;
    private final JwtService jwtService;
    private final UserDao userDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AddressService(AddressProvider addressProvider, JwtService jwtService, AddressDao addressDao,UserDao userDao) {
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;
        this.addressDao = addressDao;
        this.userDao = userDao;
    }

    public int createAddress(PostAddressReq postAddressReq) throws BaseException {
            try {
                if (postAddressReq.getAliasType() == null) {
                    postAddressReq.setAliasType("ETC");
                }
                // 집 주소를 등록할 때, 기존 집을 대체하는 경우
                if (postAddressReq.getAliasType().equalsIgnoreCase("HOME") && userDao.checkUserHomeAddress(postAddressReq.getUserIdx()) == 1) {
                    int beforeHomeIdx = userDao.getUserHomeIdx(postAddressReq.getUserIdx());
                    userDao.updateStatusAddress(beforeHomeIdx); // 기존 집 삭제
                }
                // 회사 주소를 등록할 때, 기존 회사를 대체하는 경우
                if (postAddressReq.getAliasType().equalsIgnoreCase("COMPANY") && userDao.checkUserCompanyAddress(postAddressReq.getUserIdx()) == 1) {
                    int beforeCompanyIdx = userDao.getUserCompanyIdx(postAddressReq.getUserIdx());
                    userDao.updateStatusAddress(beforeCompanyIdx); // 기존 회사 삭제
                }
                return addressDao.insertAddress(postAddressReq);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
    }
}
