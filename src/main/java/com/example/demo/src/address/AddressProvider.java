package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.address.model.GetAddressesRes;
import com.example.demo.src.address.model.GetCompanyAddress;
import com.example.demo.src.address.model.GetHomeAddress;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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
            // 집 주소
            GetHomeAddress homeAddress;
            if (userDao.getUserHomeIdx(userIdx) != null) {
                homeAddress =  addressDao.selectHomeAddress(userIdx);
                getAddressesRes.setHome(homeAddress);
            }
            // 회사 주소
            GetCompanyAddress companyAddress;
            if (userDao.getCompanyIdx(userIdx) != null) {
                companyAddress = addressDao.selectCompanyAddress(userIdx);
                getAddressesRes.setCompany(companyAddress); // 회사 주소
            }
            //전체 주소
            List<GetAddressRes> getAddressRes = addressDao.selectAddressList(userIdx); //전체 주소
            if (getAddressRes.isEmpty()) {
                getAddressesRes.setAddressList(null);
            } else {
                getAddressesRes.setAddressList(getAddressRes);
            }
            //유저가 기본선택한 주소
            Integer seletedAddressIdx = userDao.selectUserAddressIdx(userIdx);
            if (seletedAddressIdx != null) {
                getAddressesRes.setSeletedAddressIdx(seletedAddressIdx);
            }
            return getAddressesRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
