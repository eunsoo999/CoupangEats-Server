package com.example.demo.src.review;

import com.example.demo.src.review.model.GetPhotoReview;
import com.example.demo.src.review.model.GetReview;
import com.example.demo.src.review.model.GetReviewsRes;
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
                "(select reviewIdx, min(idx), imageUrl from ReviewImage group by reviewIdx) as FirstReviewImage " +
                "on Review.idx = FirstReviewImage.reviewIdx " +
                "where Review.storeIdx = ? and Review.status != 'N' " +
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

    public List<GetReview> selectStoreReviews(int storeIdx, String type, String sort) {
        String selectStoreReviewsQuery = "select Result.reviewIdx, userName, rating, contents, orderMenus, likeCount, " +
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
                "END AS writingTimeStamp " +
                "from (select ReviewTable.reviewIdx, userName, rating, contents, orderMenus, ReviewTable.createdAt, " +
                "count(case when ReviewLike.likeFlag = 'Y' then 1 end) as likeCount " +
                "from (select Review.idx as 'reviewIdx', User.userName, truncate(avg(Review.rating), 1) as 'rating' ,Review.contents, group_concat(distinct OrderMenu.menuName separator '·') as 'orderMenus', Review.createdAt " +
                "from Review inner join User on Review.userIdx = User.idx inner join Orders on Review.orderIdx = Orders.idx join OrderMenu on OrderMenu.orderIdx = Orders.idx " +
                "where Review.status != 'N' and User.status != 'N' and Orders.status != 'N' and OrderMenu.status != 'N' and Review.storeIdx = ? ";

        if(type != null && type.equalsIgnoreCase("photo")) {
            selectStoreReviewsQuery += "and (select count(*) from ReviewImage where Review.status != 'N' and Review.idx = ReviewImage.reviewIdx) > 0 ";
        }

        selectStoreReviewsQuery += "group by Review.idx) ReviewTable left join ReviewLike on ReviewTable.reviewIdx = ReviewLike.reviewIdx " +
                "group by ReviewTable.reviewIdx) Result ";

        if((sort != null && sort.equalsIgnoreCase("new")) || sort == null) {
            selectStoreReviewsQuery += "order by Result.createdAt desc";
        } else if(sort.equalsIgnoreCase("reviewliked")) {
            selectStoreReviewsQuery += "order by likeCount desc";
        } else if(sort.equalsIgnoreCase("rating-desc")) {
            selectStoreReviewsQuery += "order by rating desc, Result.createdAt desc";
        } else if(sort.equalsIgnoreCase("rating-asc")) {
            selectStoreReviewsQuery += "order by rating asc";
        }

        return this.jdbcTemplate.query(selectStoreReviewsQuery,
                (rs,rowNum) -> new GetReview(
                        rs.getInt("reviewIdx"),
                        rs.getString("userName"),
                        rs.getInt("rating"),
                        rs.getString("writingTimeStamp"),
                        rs.getString("contents"),
                        rs.getString("orderMenus"),
                        rs.getInt("likeCount")), storeIdx);
    }

    public List<String> selectReviewImages(int reviewIdx) {
        String selectReviewImagesQuery = "select imageUrl from ReviewImage where reviewIdx = ?";
        int selectReviewImagesParams = reviewIdx;

        return this.jdbcTemplate.query(selectReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("imageUrl")), selectReviewImagesParams);
    }
}
