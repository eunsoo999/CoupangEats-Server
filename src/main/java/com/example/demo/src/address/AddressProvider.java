package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.address.model.GetAddressesRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AddressProvider {
    private final AddressDao addressDao;
    private final JwtService jwtService;
    private final UserDao userDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AddressProvider(AddressDao addressDao, JwtService jwtService, UserDao userDao) {
        this.addressDao = addressDao;
        this.jwtService = jwtService;
        this.userDao = userDao;
    }

    public GetAddressesRes getAddreses(int userIdx) throws BaseException {
        GetAddressesRes getAddressesRes = new GetAddressesRes();
        try{
            List<GetAddressRes> getAddressRes = addressDao.selectAddressList(userIdx);
            int seletedAddressIdx = userDao.selectUserAddressIdx(userIdx);
            getAddressesRes.setGetAddressList(getAddressRes);
            getAddressesRes.setSeletedAddressIdx(seletedAddressIdx);
            return getAddressesRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
