package com.example.demo.src.reviewLike;

import com.example.demo.src.review.model.GetReviewsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ReviewLikeDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // LikeFlag 확인
    public String selectReviewLikeFlag(int userIdx, int reviewIdx) {
        String selectReviewLikeFlagQuery = "select likeFlag " +
                "from ReviewLike " +
                "where status = 'Y' and userIdx = ? and reviewIdx = ?";
        return this.jdbcTemplate.queryForObject(selectReviewLikeFlagQuery, String.class, userIdx, reviewIdx);
    }

    // 해당 리뷰에 대한 도움 체크 표시를 했는지 확인
    public int checkReviewLike(int userIdx, int reviewIdx) {
        String query = "select exists(select 1 from ReviewLike " +
                "where status = 'Y' and userIdx = ? and reviewIdx = ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, userIdx, reviewIdx);
    }

    // 리뷰 "도움이돼요/안돼요" 삽입
    public int insertReviewLike(String likeFlag, int userIdx, int reviewIdx) {
        String query = "INSERT INTO ReviewLike (likeFlag, userIdx, reviewIdx) VALUES (?, ?, ?)";
        this.jdbcTemplate.update(query, likeFlag, userIdx, reviewIdx);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    // 리뷰 "도움이돼요/안돼요" Flag 수정
    public int updateReviewLikeFlag(String likeFlag, int userIdx, int reviewIdx) {
        String query = "update ReviewLike set ReviewLike.likeFlag = ? where userIdx =  ? and reviewIdx = ? and status = 'Y'";
        return this.jdbcTemplate.update(query, likeFlag, userIdx, reviewIdx);
    }

    // 리뷰 "도움이돼요/안돼요" 취소
    public int updateReviewLikeStatus(int userIdx, int reviewIdx) {
        String query = "update ReviewLike set ReviewLike.status = 'N' where userIdx =  ? and reviewIdx = ?";
        return this.jdbcTemplate.update(query, userIdx, reviewIdx);
    }
}
