package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.orders.OrderDao;
import com.example.demo.src.review.model.PatchMenuReviewReq;
import com.example.demo.src.review.model.PatchReviewReq;
import com.example.demo.src.review.model.PostMenuReviewReq;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.store.StoreDao;
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
    private final OrderDao orderDao;
    private final StoreDao storeDao;

    @Autowired
    public ReviewService(ReviewDao reviewDao,UserDao userDao, OrderDao orderDao, StoreDao storeDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.storeDao = storeDao;
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

            if(updatedCount == 0) {
                throw new BaseException(FAILED_TO_UPDATE_STATUS_REVIEW); // 수정된 데이터 수가 0개면 삭제가 안된 것
            }
            return updatedCount;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int createReview(PostReviewReq postReviewReq) throws BaseException {
        if(userDao.checkUserIdx(postReviewReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if(orderDao.checkOrderIdx(postReviewReq.getOrderIdx()) == 0) {
            throw new BaseException(ORDERS_NOT_FOUND); // 주문 존재 검증
        } else if(storeDao.checkStore(postReviewReq.getStoreIdx()) == 0) {
            throw new BaseException(STORES_NOT_FOUND); // 가게 존재 검증
        } else if(orderDao.checkOrderInStore(postReviewReq.getOrderIdx(), postReviewReq.getStoreIdx()) == 0) {
            throw new BaseException(ORDERS_NOT_IN_STORE); // 해당 가게에서 주문한 내역인지 검증
        } else if(orderDao.checkOrderByUserIdx(postReviewReq.getOrderIdx(), postReviewReq.getUserIdx()) == 0) {
            throw new BaseException(ORDER_NOT_ORDERER); // 유저가 주문한 주문번호가 맞는지 검증
        } else if(reviewDao.checkReviewByOrderIdx(postReviewReq.getOrderIdx()) == 1) {
            throw new BaseException(ORDER_EXISTS_REVIEW); // 주문 당 리뷰는 1개 작성 가능. 해당 주문 번호로 작성한 리뷰가 있는지 검증
        }
        if(postReviewReq.getMenuReviews() != null) {
            for (PostMenuReviewReq menuReview : postReviewReq.getMenuReviews()) {
                if(orderDao.checkOrderMenuInOrder(postReviewReq.getOrderIdx(), menuReview.getOrderMenuIdx()) == 0) {
                    throw new BaseException(ORDERMENU_NOT_IN_ORDERS); // 해당 주문내역에 주문메뉴가 있는지 검증
                }
            }
        }

        try {
            // insert Review
            int createdReviewIdx = reviewDao.insertReview(postReviewReq);

            // insert Review Images
            if (postReviewReq.getImageUrls() != null) {
                for (String imageUrl : postReviewReq.getImageUrls()) {
                    reviewDao.insertReviewImage(createdReviewIdx, imageUrl);
                }
            }

            // insert MenuReviews(개별메뉴리뷰)
            if (postReviewReq.getMenuReviews() != null) {
                for (PostMenuReviewReq menuReview : postReviewReq.getMenuReviews()) {
                    reviewDao.insertMenuReview(createdReviewIdx, menuReview);
                }
            }

            return createdReviewIdx;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateReview(int userIdx, int reviewIdx, PatchReviewReq patchReviewReq) throws BaseException {
        if (userDao.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_NOT_FOUND); // 유저 존재 검증
        } else if (reviewDao.checkReviewIdx(reviewIdx) == 0) {
            throw new BaseException(REVIEWS_NOT_FOUND); // 리뷰 존재 검증
        } else if (reviewDao.checkReviewByuserIdx(userIdx, reviewIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT); // 리뷰 작성자 == 유저 검증
        }

        try {
            // 리뷰 수정
            reviewDao.updateReview(reviewIdx, patchReviewReq);
            // 리뷰 이미지가 수정된 기록이 있을 경우에만 update.
            if (patchReviewReq.getModifiedImageFlag().equalsIgnoreCase("Y")) {
                reviewDao.updateStatusReviewImages(reviewIdx); // 리뷰 이미지 삭제
                // 리뷰 이미지 삽입
                if (patchReviewReq.getImageUrls() != null) {
                    for (String imageUrl : patchReviewReq.getImageUrls()) {
                        System.out.println(imageUrl);
                        reviewDao.insertReviewImage(reviewIdx, imageUrl);
                    }
                }
            }

            // insert MenuReviews(개별메뉴리뷰)
            if (patchReviewReq.getMenuReviews() != null) {
                for (PatchMenuReviewReq menuReview : patchReviewReq.getMenuReviews()) {
                    reviewDao.updateMenuReviews(reviewIdx, menuReview);
                }
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
