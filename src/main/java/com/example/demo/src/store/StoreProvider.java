package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.coupon.CouponDao;
import com.example.demo.src.coupon.model.GetStoreCoupon;
import com.example.demo.src.event.EventDao;
import com.example.demo.src.event.model.GetEventBannerRes;
import com.example.demo.src.menu.MenuDao;
import com.example.demo.src.menu.model.GetMenuByCategory;
import com.example.demo.src.menu.model.GetMenus;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.model.GetPhotoReview;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.STORES_NOT_FOUND;

@Service
public class StoreProvider {
    private final StoreDao storeDao;
    private final EventDao eventDao;
    private final ReviewDao reviewDao;
    private final CouponDao couponDao;
    private final MenuDao menuDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoreProvider(StoreDao storeDao, EventDao eventDao, ReviewDao reviewDao, CouponDao couponDao, MenuDao menuDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.eventDao = eventDao;
        this.reviewDao = reviewDao;
        this.couponDao = couponDao;
        this.menuDao = menuDao;
        this.jwtService = jwtService;
    }

    public GetMainRes getMainStores(SearchOption searchOption) throws BaseException {
        GetMainRes getMainRes = new GetMainRes();
        try{
            // 이벤트 배너
            List<GetEventBannerRes> getEventBannerList = eventDao.selectEventBanners();
            getMainRes.setEvents(getEventBannerList);

            // 가게 카테고리
            List<GetStoreCategoryRes> getStoryCategoryList = storeDao.selectStoreCategories();
            getMainRes.setStoreCategories(getStoryCategoryList);

            // 할인중인 맛집
            List<GetStoreSmallBox> getStoreSmallBoxList = storeDao.selectOnsaleStoresUptoTen(searchOption.getLat(), searchOption.getLon());
            if (getStoreSmallBoxList.isEmpty()) {
                getMainRes.setOnSaleStores(null);
            } else {
                getMainRes.setOnSaleStores(getStoreSmallBoxList);
            }

            // 새로 들어온 가게 (최근 2주)
            List<GetNewStoreBox> getNewStoreBoxList = storeDao.selectNewStoreBoxesUptoTen(searchOption.getLat(), searchOption.getLon());
            if (getNewStoreBoxList.isEmpty()) {
                getMainRes.setNewStores(null);
            } else {
                getMainRes.setNewStores(getNewStoreBoxList);
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

    public List<GetStoreCategoryRes> getStoreCategories() throws BaseException {
        try{
            return storeDao.selectStoreCategories();
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetOnSaleStoresRes getOnsaleStores(SearchOption searchOption) throws BaseException {
        try {
            List<GetStoreMainBox> getStoreMainBoxList = storeDao.selectOnsaleStores(searchOption);

            if (getStoreMainBoxList.isEmpty()) {
                return new GetOnSaleStoresRes(0, null);
            } else {
                for (GetStoreMainBox storeMainBox : getStoreMainBoxList) {
                    int storeIdx = storeMainBox.getStoreIdx();
                    List<String> getImageUrls = storeDao.selectStoreImageUrls(storeIdx);
                    storeMainBox.setImageUrls(getImageUrls);
                }
                return new GetOnSaleStoresRes(getStoreMainBoxList.size(), getStoreMainBoxList);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetNewStoresRes getNewStores(SearchOption searchOption) throws BaseException {
        try {
            List<GetStoreMainBox> getStoreMainBoxList = storeDao.selectNewStores(searchOption);
            if (getStoreMainBoxList.isEmpty()) {
                return new GetNewStoresRes(0, null);
            } else {
                for (GetStoreMainBox storeMainBox : getStoreMainBoxList) {
                    int storeIdx = storeMainBox.getStoreIdx();
                    List<String> getImageUrls = storeDao.selectStoreImageUrls(storeIdx);
                    storeMainBox.setImageUrls(getImageUrls);
                }
                return new GetNewStoresRes(getStoreMainBoxList.size(), getStoreMainBoxList);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreRes getStore(int storeIdx, Integer userIdx) throws BaseException {
        // 가게 존재 확인
        if (storeDao.checkStore(storeIdx) == 0) {
            throw new BaseException(STORES_NOT_FOUND);
        }

        try {
            // 가게 정보
            GetStoreRes getStoreRes = storeDao.selectStoreInfo(storeIdx);

            // 가게 이미지
            List<String> getImageUrls = storeDao.selectStoreImageUrls(storeIdx);
            getStoreRes.setImageUrls(getImageUrls);

            // 가게 할인쿠폰 정보
            if (couponDao.checkCouponByStoreIdx(storeIdx) == 1) {
                GetStoreCoupon storeCoupon = couponDao.selectStoreCoupon(storeIdx);
                // 가게의 할인쿠폰을 소유하고 있는지 확인
                if (userIdx != null) {
                    if (couponDao.checkUserCoupon(storeCoupon.getCouponIdx(), userIdx) == 1) {
                        // 유저가 이미 가게의 쿠폰을 소유하고있음.
                        storeCoupon.setHasCoupon("Y");
                    } else {
                        // 유저가 가게의 쿠폰을 소유하고 있지않음.
                        storeCoupon.setHasCoupon("N");
                    }
                } else {
                    // 비로그인 유저는 쿠폰을 소유하고 있지않음.
                    storeCoupon.setHasCoupon("N");
                }
                getStoreRes.setCoupon(storeCoupon);
            }

            // 가게 포토리뷰 미리보기
            List<GetPhotoReview> photoReviews = reviewDao.selectPhotoReviewsUptoThree(storeIdx);
            // 가게 포토리뷰 미리보기 가능한 리뷰 개수가 3개 미만이면 화면에 출력하지않음.
            if (photoReviews.size() < 3) {
                getStoreRes.setPhotoReviews(null);
            } else {
                getStoreRes.setPhotoReviews(photoReviews);
            }

            // 가게 메뉴
            // 추천메뉴 카테고리
            GetMenuByCategory bestMenu = new GetMenuByCategory("추천메뉴", null);
            // 추천 메뉴 리스트
            List<GetMenus> recommendedMenus = menuDao.selectBestMenusByStoreIdx(storeIdx);
            bestMenu.setMenuList(recommendedMenus);
            // 추천 메뉴가 있는 경우에 list에 추가
            if (!recommendedMenus.isEmpty()) {
                getStoreRes.getMenuCategories().add(bestMenu);
            }

            // 일반메뉴 카테고리
            List<GetMenuByCategory> menuCategories = menuDao.selectStoreCategories(storeIdx);
            for (GetMenuByCategory category : menuCategories) {
                List<GetMenus> menus = menuDao.selectMenusBycategoryName(category.getMenuCategoryName());
                category.setMenuList(menus);
                getStoreRes.getMenuCategories().add(category);
            }

            // 해당 가게에 메뉴가 없는 경우
            if (getStoreRes.getMenuCategories().isEmpty()) {
                getStoreRes.setMenuCategories(null);
            }
            return getStoreRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
