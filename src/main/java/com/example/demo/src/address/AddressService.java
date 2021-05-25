package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.PatchAddressReq;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.FAILED_TO_UPDATE_ADDRESSES;

@Service
@Transactional(rollbackOn = BaseException.class)
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
        // alias가 공백인 경우 null로 저장
        if (postAddressReq.getAlias() != null && postAddressReq.getAlias().replace(" ", "").length() == 0) {
            postAddressReq.setAlias(null);
        }

        try {
            // 집 주소를 등록할 때, 기존 집을 대체하는 경우
            if (postAddressReq.getAliasType().equalsIgnoreCase("HOME") && userDao.checkUserHomeAddress(postAddressReq.getUserIdx()) == 1) {
                int beforeHomeIdx = userDao.getUserHomeIdx(postAddressReq.getUserIdx());
                addressDao.updateAddressTypeAndInitAlias(beforeHomeIdx, "ETC"); // 기존 집 "ETC" 변경, alias 제거
            }
            // 회사 주소를 등록할 때, 기존 회사를 대체하는 경우
            if (postAddressReq.getAliasType().equalsIgnoreCase("COMPANY") && userDao.checkUserCompanyAddress(postAddressReq.getUserIdx()) == 1) {
                int beforeCompanyIdx = userDao.getUserCompanyIdx(postAddressReq.getUserIdx());
                addressDao.updateAddressTypeAndInitAlias(beforeCompanyIdx,"ETC"); // 기존 회사 "ETC" 변경, alias 제거
                }
            return addressDao.insertAddress(postAddressReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateAddress(PatchAddressReq patchAddressReq, int addressIdx, int userIdx) throws BaseException {
        // 주소 존재 확인
        if (addressDao.checkAddressIdx(addressIdx) == 0) {
            throw new BaseException(ADDRESSES_NOT_FOUND);
        }
        // 주소를 등록한 유저인지 확인
        if (addressDao.checkAddressByOwner(addressIdx, userIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        // 입력받은 alias가 공백인 경우 null로 저장하기위해 변경
        if (patchAddressReq.getAlias() != null && patchAddressReq.getAlias().replace(" ", "").length() == 0) {
            patchAddressReq.setAlias(null);
        }

        try {
            // 집 주소 대체일 경우 기존 집 주소 "ETC"으로 변경, , alias 제거
            if (patchAddressReq.getAliasType().equalsIgnoreCase("HOME") && userDao.checkUserHomeAddress(userIdx) == 1) {
                int beforeHomeIdx = userDao.getUserHomeIdx(userIdx);
                addressDao.updateAddressTypeAndInitAlias(beforeHomeIdx, "ETC");
            } else if (patchAddressReq.getAliasType().equalsIgnoreCase("COMPANY") && userDao.checkUserCompanyAddress(userIdx) == 1) {
                // 회사 주소 대체일 경우 기존 회사 주소 "ETC"으로 변경, alias 제거
                int beforeCompanyIdx = userDao.getUserCompanyIdx(userIdx);
                addressDao.updateAddressTypeAndInitAlias(beforeCompanyIdx, "ETC");
            }

            int updatedCount = addressDao.updateAddress(addressIdx, patchAddressReq);
            if (updatedCount != 1) {
                throw new BaseException(FAILED_TO_UPDATE_ADDRESSES);
            }
            return updatedCount;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int updateStatusAddress(int addressIdx, int userIdx) throws BaseException {
        // 주소 존재 확인
        if (addressDao.checkAddressIdx(addressIdx) == 0) {
            throw new BaseException(ADDRESSES_NOT_FOUND);
        }
        // 본인이 등록한 주소가 맞는지 확인
        if (addressDao.checkAddressByOwner(addressIdx, userIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        // 기본주소 삭제 시 유저의 기본주소 null처리
        if (userDao.checkUserAddressIdx(userIdx, addressIdx) == 1) {
            userDao.updateUserAddressIdx(userIdx, null);
        }
        try{
            int updatedCount = addressDao.updateStatusAddress(addressIdx);
            if(updatedCount == 0){
                throw new BaseException(FAILED_TO_UPDATE_STATUS_ADDRESSES);
            }
            return updatedCount;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
