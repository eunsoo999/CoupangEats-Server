package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetMainRes;
import com.example.demo.src.store.model.GetStoreMainBox;
import com.example.demo.src.store.model.GetStoreSmallBox;
import com.example.demo.src.store.model.SearchOption;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class StoreProvider {
    private final StoreDao storeDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    public GetMainRes getMainStores(SearchOption searchOption) throws BaseException {
        GetMainRes getMainRes = new GetMainRes();
        try{
            // 할인중인 맛집
            List<GetStoreSmallBox> getStoreSmallBoxList = storeDao.selectOnsaleStoresUptoTen(searchOption.getLat(), searchOption.getLon());
            if (getStoreSmallBoxList.isEmpty()) {
                getMainRes.setOnSaleStores(null);
            } else {
                getMainRes.setOnSaleStores(getStoreSmallBoxList);
            }

            // 주변맛집추천
            List<GetStoreMainBox> getStoreMainBoxList = storeDao.selectStoreMainBoxes(searchOption);
            if (getStoreMainBoxList.isEmpty()) {
                getMainRes.setRecommendStores(null); //주변맛집이 없다면 null
            } else {
                for (GetStoreMainBox storeMainBox : getStoreMainBoxList) {
                    int storeIdx = storeMainBox.getStoreIdx();
                    List<String> getImageUrls = storeDao.selectStoreImageUrls(storeIdx);
                    storeMainBox.setImageUrls(getImageUrls);
                }
                getMainRes.setRecommendStores(getStoreMainBoxList);
            }

            // 주변맛집추천 가게 개수
            getMainRes.setTotalCount(getStoreMainBoxList.size());

            return getMainRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
