package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class ReviewService {
    private final ReviewDao reviewDao;
    private final UserDao userDao;

    @Autowired
    public ReviewService(ReviewDao reviewDao,UserDao userDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
    }


    public int updateStatusReview(int userIdx, int reviewIdx) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(reviewIdx) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewDao.checkReviewByuserIdx(userIdx, reviewIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT); // 리뷰 작성자 == 유저 검증
        }

        try {
            // 개별 메뉴 리뷰 status 변경 (삭제)
            reviewDao.updateStatusMenuReviews(reviewIdx);

            // 리뷰 이미지 status 변경 (삭제)
            reviewDao.updateStatusReviewImages(reviewIdx);

            // 리뷰 status 변경 (삭제)
            int updatedCount = reviewDao.updateStatusReview(reviewIdx);

            return updatedCount;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
