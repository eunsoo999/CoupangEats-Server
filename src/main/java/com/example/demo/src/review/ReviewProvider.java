package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReview;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.store.StoreDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.STORES_NOT_FOUND;

@Service
public class ReviewProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReviewDao reviewDao;
    private final StoreDao storeDao;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, StoreDao storeDao) {
        this.reviewDao = reviewDao;
        this.storeDao = storeDao;
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
}
