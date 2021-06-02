package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetMenuReview;
import com.example.demo.src.review.model.GetReview;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.store.StoreDao;
import com.example.demo.src.user.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReviewDao reviewDao;
    private final StoreDao storeDao;
    private final UserDao userDao;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, StoreDao storeDao, UserDao userDao) {
        this.reviewDao = reviewDao;
        this.storeDao = storeDao;
        this.userDao = userDao;
    }

    public GetReviewsRes getStoreReviews(Integer userIdx, int storeIdx, String type, String sort) throws BaseException {
        // 가게 존재 확인
        if (storeDao.checkStore(storeIdx) == 0) {
            throw new BaseException(STORES_NOT_FOUND);
        }
        try {
            // 가게명, 별점, 리뷰 개수
            GetReviewsRes getReviewsRes = reviewDao.selectStoreReviewInfo(storeIdx);
            // 리뷰 목록
            List<GetReview> reviews = reviewDao.selectStoreReviews(userIdx, storeIdx, type, sort);

            // 각 리뷰의 이미지 배열
            for (GetReview review: reviews) {
                List<String> reviewImages = reviewDao.selectReviewImages(review.getReviewIdx());
                review.setImageUrls(reviewImages);
            }

            getReviewsRes.setReviews(reviews);
            return getReviewsRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetReviewRes getReview(int userIdx, int reviewIdx) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(reviewIdx) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewDao.checkReviewByuserIdx(userIdx, reviewIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT); // 리뷰 작성자 == 유저 검증
        }

        try {
            // 리뷰 기본 정보
            GetReviewRes getReviewRes = reviewDao.selectReview(reviewIdx);

            // 리뷰 이미지
            List<String> images = reviewDao.selectReviewImages(reviewIdx);
            getReviewRes.setImages(images);

            // 메뉴 개별 리뷰
            List<GetMenuReview> menuReviews = reviewDao.selectMenuReviews(reviewIdx);
            getReviewRes.setMenuReviews(menuReviews);
            
            return getReviewRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
