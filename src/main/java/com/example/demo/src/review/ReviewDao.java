package com.example.demo.src.review;

import com.example.demo.src.review.model.GetPhotoReview;
import com.example.demo.src.store.model.GetStoreMainBox;
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
}
