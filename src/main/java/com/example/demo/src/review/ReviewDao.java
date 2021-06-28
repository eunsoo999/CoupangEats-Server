package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPhotoReview> selectPhotoReviewsUptoThree(int storeIdx) {
        String selectPhotoReviewsQuery = "select Review.idx as 'reviewIdx', Review.contents, Review.rating, FirstReviewImage.imageUrl " +
                "from Review inner join " +
                "(select reviewIdx, min(idx), imageUrl, ReviewImage.status from ReviewImage group by reviewIdx) as FirstReviewImage " +
                "on Review.idx = FirstReviewImage.reviewIdx " +
                "where Review.storeIdx = ? and Review.status != 'N' and FirstReviewImage.status != 'N' " +
                "order by Review.idx desc limit 3";

        return this.jdbcTemplate.query(selectPhotoReviewsQuery,
                (rs,rowNum) -> new GetPhotoReview(
                        rs.getInt("reviewIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("contents"),
                        rs.getDouble("rating")), storeIdx);
    }

    public GetReviewsRes selectStoreReviewInfo(int storeIdx) {
        String selectStoreReviewInfoQuery = "select concat(Store.storeName, ' 리뷰') as 'title', " +
                "(select truncate(avg(rating), 1) from Review where Review.status != 'N' and Review.storeIdx = Store.idx) as 'totalRating', " +
                "(select count(*) as 'reviewCount' from Review where Review.status != 'N' and Review.storeIdx = Store.idx) as 'reviewCount' " +
                "from Store where Store.status != 'N' and Store.idx = ?";

        return this.jdbcTemplate.queryForObject(selectStoreReviewInfoQuery,
                (rs,rowNum) -> new GetReviewsRes(
                        rs.getString("title"),
                        rs.getDouble("totalRating"),
                        rs.getString("reviewCount")), storeIdx);
    }

    public List<GetReview> selectStoreReviews(Integer userIdx, int storeIdx, String type, String sort) {
        String selectStoreReviewsQuery = "select Result.reviewIdx, RPAD(left(userName, 1), char_length(userName), '*') as 'userName', rating, contents, orderMenus, likeCount, " +
                "CASE WHEN timestampdiff(SECOND, Result.createdAt, current_timestamp) < 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, Result.createdAt, CURRENT_TIMESTAMP()), '초 전') " +
                "WHEN timestampdiff(MINUTE, Result.createdAt, current_timestamp) < 60 " +
                "THEN concat(timestampdiff(MINUTE, Result.createdAt, current_timestamp), '분 전') " +
                "WHEN timestampdiff(HOUR, Result.createdAt, current_timestamp) < 24 " +
                "THEN concat(timestampdiff(HOUR, Result.createdAt, current_timestamp), '시간 전') " +
                "WHEN timestampdiff(DAY, Result.createdAt, current_timestamp) < 7 " +
                "THEN concat(timestampdiff(DAY, Result.createdAt, current_timestamp), '일 전') " +
                "WHEN timestampdiff(WEEK, Result.createdAt, current_timestamp) <= 1 THEN '지난 주' " +
                "WHEN timestampdiff(MONTH , Result.createdAt, current_timestamp) <= 1 THEN '지난 달' " +
                "ELSE date_format(Result.createdAt, '%Y-%m-%d') " +
                "END AS writingTimeStamp ";

        // 로그인상태일 경우 유저의 리뷰 도움이돼요/안돼요 표시
        if(userIdx != null) {
            selectStoreReviewsQuery += ", case when (select exists(select ReviewLike.idx from ReviewLike " +
                    "where ReviewLike.reviewIdx = Result.reviewIdx and ReviewLike.status != 'N' and ReviewLike.likeFlag = 'Y' and ReviewLike.userIdx = ?)) " +
                    "then 'YES' " +
                    "when (select exists(select ReviewLike.idx from ReviewLike " +
                    "where ReviewLike.reviewIdx = Result.reviewIdx and ReviewLike.status != 'N' and ReviewLike.likeFlag = 'N' and ReviewLike.userIdx = ?)) " +
                    "then 'NO' else null end as isLiked ";
        } else {
            selectStoreReviewsQuery += ", null as 'isLiked' "; // 비로그인
        }

        // 로그인상태일 경우 리뷰의 작성자가 자신인지 확인
        if(userIdx != null) {
            selectStoreReviewsQuery +=  ", if(writerIdx = ?, 'Y', 'N') as 'isWriter', " +
                    "if(timestampdiff(DAY, current_timestamp, date_add(Result.createdAt, INTERVAL 3 DAY)) > 0, 'Y', null) as isModifiable " +
                    "from (select ReviewTable.reviewIdx, userName, writerIdx, rating, contents, orderMenus, ReviewTable.createdAt, ";
        } else {
            selectStoreReviewsQuery += ", 'N' as 'isWriter', " +
                    "null as 'isModifiable' " +
                    "from (select ReviewTable.reviewIdx, userName, rating, contents, orderMenus, ReviewTable.createdAt, ";
        }

        selectStoreReviewsQuery += "count(case when ReviewLike.likeFlag = 'Y' then 1 end) as likeCount " +
                "from (select Review.idx as 'reviewIdx', User.userName, User.idx as writerIdx, truncate(avg(Review.rating), 1) as 'rating' ,Review.contents, group_concat(distinct OrderMenu.menuName separator '·') as 'orderMenus', Review.createdAt " +
                "from Review inner join User on Review.userIdx = User.idx inner join Orders on Review.orderIdx = Orders.idx join OrderMenu on OrderMenu.orderIdx = Orders.idx " +
                "where Review.status != 'N' and User.status != 'N' and Orders.status != 'N' and OrderMenu.status != 'N' and Review.storeIdx = ? ";

        // 포토리뷰만 보기
        if(type != null && type.equalsIgnoreCase("photo")) {
            selectStoreReviewsQuery += "and (select count(*) from ReviewImage where Review.status != 'N' and ReviewImage.status != 'N' and Review.idx = ReviewImage.reviewIdx) > 0 ";
        }

        selectStoreReviewsQuery += "group by Review.idx) ReviewTable left join ReviewLike on ReviewTable.reviewIdx = ReviewLike.reviewIdx " +
                "group by ReviewTable.reviewIdx) Result ";

        // 정렬 옵션
        if((sort != null && sort.equalsIgnoreCase("new")) || sort == null) {
            selectStoreReviewsQuery += "order by Result.createdAt desc";
        } else if(sort.equalsIgnoreCase("reviewliked")) {
            selectStoreReviewsQuery += "order by likeCount desc";
        } else if(sort.equalsIgnoreCase("rating-desc")) {
            selectStoreReviewsQuery += "order by rating desc, Result.createdAt desc";
        } else if(sort.equalsIgnoreCase("rating-asc")) {
            selectStoreReviewsQuery += "order by rating asc";
        }

        Object[] params;
        if (userIdx == null) {
            params = new Object[] {storeIdx}; // 비로그인 유저
        } else {
            params = new Object[] {userIdx, userIdx, userIdx, storeIdx}; // 로그인 유저
        }

        System.out.println(selectStoreReviewsQuery);
        return this.jdbcTemplate.query(selectStoreReviewsQuery,
                (rs,rowNum) -> new GetReview(
                        rs.getInt("reviewIdx"),
                        rs.getString("userName"),
                        rs.getInt("rating"),
                        rs.getString("writingTimeStamp"),
                        rs.getString("contents"),
                        rs.getString("orderMenus"),
                        rs.getInt("likeCount"),
                        rs.getString("isLiked"),
                        rs.getString("isWriter"),
                        rs.getString("isModifiable")), params);
    }

    public List<String> selectReviewImages(int reviewIdx) {
        String selectReviewImagesQuery = "select imageUrl from ReviewImage where reviewIdx = ? and status != 'N'";
        int selectReviewImagesParams = reviewIdx;

        return this.jdbcTemplate.query(selectReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("imageUrl")), selectReviewImagesParams);
    }

    public int checkReviewIdx(int reviewIdx) {
        String checkReviewIdxQuery = "select exists(select idx from Review where idx = ? and status != 'N')";

        return this.jdbcTemplate.queryForObject(checkReviewIdxQuery, int.class, reviewIdx);
    }

    public int checkReviewByuserIdx(int userIdx, int reviewIdx) {
        String checkReviewByuserIdxQuery = "select exists(select idx from Review where idx = ? and userIdx = ? and status != 'N')";

        return this.jdbcTemplate.queryForObject(checkReviewByuserIdxQuery, int.class, reviewIdx, userIdx);
    }

    public GetReviewRes selectReview(int reviewIdx) {
        String selectReviewQuery = "select Store.idx as 'storeIdx', Store.storeName, truncate(Review.rating, 1) as 'rating', Review.storeBadReason, " +
                "Review.contents, Review.deliveryLiked, Review.deliveryBadReason, Review.deliveryComment " +
                "from Review inner join Store on Review.storeIdx = Store.idx " +
                "where Review.status != 'N' and Review.idx = ?";

        return this.jdbcTemplate.queryForObject(selectReviewQuery,
                (rs,rowNum) -> new GetReviewRes(
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getInt("rating"),
                        rs.getString("storeBadReason"),
                        rs.getString("contents"),
                        new GetDeliveryReviewRes(
                                rs.getString("deliveryLiked"),
                                rs.getString("deliveryBadReason"),
                                rs.getString("deliveryComment"))), reviewIdx);
    }

    public List<GetMenuReview> selectMenuReviews(int reviewIdx) {
        String selectMenuReviewsQuery = "select OrderMenu.idx as 'orderMenuIdx', OrderMenu.menuName, OrderMenu.menuDetail, MenuReview.menuLiked, MenuReview.menuBadReason, MenuReview.comment " +
                "from (select idx, menuLiked, comment, orderMenuIdx, menuBadReason " +
                "from MenuReview " +
                "where reviewIdx = ? and MenuReview.status != 'N') MenuReview right outer join (select OrderMenu.idx, menuName, menuDetail " +
                "from OrderMenu join Review on OrderMenu.orderIdx = Review.orderIdx " +
                "where Review.idx = ? and OrderMenu.status != 'N') OrderMenu on MenuReview.orderMenuIdx = OrderMenu.idx";

        return this.jdbcTemplate.query(selectMenuReviewsQuery,
                (rs,rowNum) -> new GetMenuReview(
                        rs.getInt("orderMenuIdx"),
                        rs.getString("menuName"),
                        rs.getString("menuDetail"),
                        rs.getString("menuLiked"),
                        rs.getString("menuBadReason"),
                        rs.getString("comment")), reviewIdx, reviewIdx);
    }

    public int updateStatusReview(int reviewIdx) {
        String updateStatusReviewQuery = "update Review set status = 'N' where idx = ?";
        return this.jdbcTemplate.update(updateStatusReviewQuery, reviewIdx);
    }

    public int updateStatusMenuReviews(int reviewIdx) {
        String updateStatusMenuReviewsQuery = "update MenuReview set status = 'N' where reviewIdx = ?";
        return this.jdbcTemplate.update(updateStatusMenuReviewsQuery, reviewIdx);
    }

    public int updateStatusReviewImages(int reviewIdx) {
        String query = "update ReviewImage set status = 'N' where reviewIdx = ?";
        return this.jdbcTemplate.update(query, reviewIdx);
    }

    public int insertReview(PostReviewReq postReviewReq) {
        String insertReviewQuery = "INSERT INTO Review (contents, rating, storeBadReason, userIdx, orderIdx, "
                                    + "storeIdx, deliveryLiked, deliveryBadReason, deliveryComment) "
                                    + "VALUES (?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[]{postReviewReq.getContents(), postReviewReq.getRating(), postReviewReq.getBadReason(), postReviewReq.getUserIdx(),
                postReviewReq.getOrderIdx(), postReviewReq.getStoreIdx(), postReviewReq.getDeliveryReview().getDeliveryLiked(), postReviewReq.getDeliveryReview().getDeliveryBadReason(), postReviewReq.getDeliveryReview().getDeliveryComment()};
        this.jdbcTemplate.update(insertReviewQuery, params);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public void insertReviewImage(int reviewIdx, String imageUrl) {
        String insertImageQuery = "INSERT INTO ReviewImage (imageUrl, reviewIdx) VALUES (?, ?)";
        this.jdbcTemplate.update(insertImageQuery, imageUrl, reviewIdx);
    }

    public void insertMenuReview(int reviewIdx, PostMenuReviewReq menuReview) {
        String insertMenuReviewQuery = "INSERT INTO MenuReview (OrderMenuIdx, reviewIdx, menuLiked, menuBadReason, comment) " +
                                        "VALUES (?, ?, ?, ?, ?)";
        Object[] params = new Object[]{menuReview.getOrderMenuIdx(), reviewIdx, menuReview.getMenuLiked(), menuReview.getBadReason(), menuReview.getMenuComment()};

        this.jdbcTemplate.update(insertMenuReviewQuery, params);
    }

    public int checkReviewByOrderIdx(int orderIdx) {
        String checkReviewByOrderIdxQuery = "select exists(select idx from Review " +
                "where Review.status != 'N' and Review.orderIdx = ?)";

        return this.jdbcTemplate.queryForObject(checkReviewByOrderIdxQuery, int.class, orderIdx);
    }

    public GetReviewPreviewRes selectReviewPreview(int reviewIdx) {
        String selectReviewPreview = "select Review.idx as reviewIdx, Review.storeIdx, Store.storeName, Review.rating, " +
                "CASE WHEN timestampdiff(SECOND, Review.updatedAt, current_timestamp) < 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, Review.updatedAt, CURRENT_TIMESTAMP()), '초 전') " +
                "WHEN timestampdiff(MINUTE, Review.updatedAt, current_timestamp) < 60 " +
                "THEN concat(timestampdiff(MINUTE, Review.updatedAt, current_timestamp), '분 전') " +
                "WHEN timestampdiff(HOUR, Review.updatedAt, current_timestamp) < 24 " +
                "THEN concat(timestampdiff(HOUR, Review.updatedAt, current_timestamp), '시간 전') " +
                "WHEN timestampdiff(DAY, Review.updatedAt, current_timestamp) = 0 " +
                "THEN '오늘' " +
                "WHEN timestampdiff(DAY, Review.updatedAt, current_timestamp) < 7 " +
                "THEN concat(timestampdiff(DAY, Review.updatedAt, current_timestamp), '일 전') " +
                "WHEN timestampdiff(WEEK, Review.updatedAt, current_timestamp) <= 1 " +
                "THEN '지난 주' " +
                "WHEN timestampdiff(MONTH , Review.updatedAt, current_timestamp) <= 1 " +
                "THEN '지난 달' " +
                "ELSE date_format(Review.updatedAt, '%Y-%m-%d') " +
                "END AS writingTimeStamp, " +
                "Review.contents, " +
                "(select group_concat(distinct OrderMenu.menuName separator '·') from OrderMenu where OrderMenu.orderIdx = Orders.idx) as 'orderMenus', " +
                "count(case when ReviewLike.likeFlag = 'Y' then 1 end) as likeCount, " +
                "if(timestampdiff(DAY, current_timestamp, date_add(Orders.createdAt, INTERVAL 3 DAY)) > 0, timestampdiff(DAY, current_timestamp, date_add(Orders.createdAt, INTERVAL 3 DAY)), 0) as remainingReviewTime " +
                "from Review join Store on Review.storeIdx = Store.idx left join ReviewLike on Review.idx = ReviewLike.reviewIdx join Orders on Review.orderIdx = Orders.idx " +
                "where Review.idx = ? and ReviewLike.status != 'N'";

        return this.jdbcTemplate.queryForObject(selectReviewPreview,
                (rs,rowNum) -> new GetReviewPreviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getInt("rating"),
                        rs.getString("writingTimeStamp"),
                        rs.getString("contents"),
                        rs.getString("orderMenus"),
                        rs.getInt("likeCount"),
                        rs.getInt("remainingReviewTime")), reviewIdx);

    }
}
