package com.example.demo.src.coupon;

import com.example.demo.src.coupon.model.GetCartCoupon;
import com.example.demo.src.coupon.model.GetCouponsRes;
import com.example.demo.src.coupon.model.GetStoreCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CouponDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 사용 여부 체크없이 쿠폰을 지급받았는지 있는지 체크
    public int checkUserCoupon(int couponIdx, Integer userIdx) {
        String checkCouponByStoreQuery = "select exists(select UserCoupon.idx " +
                "from Coupon inner join UserCoupon on Coupon.idx = UserCoupon.couponIdx " +
                "where UserCoupon.userIdx = ? and Coupon.idx = ? and UserCoupon.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, userIdx, couponIdx);
    }

    public int checkAvailableUserCouponInStore(int couponIdx, Integer userIdx) {
        String query = "select exists(select UserCoupon.idx " +
                "from Coupon inner join UserCoupon on Coupon.idx = UserCoupon.couponIdx " +
                "where UserCoupon.userIdx = ? and Coupon.idx = ? and UserCoupon.status != 'N' " +
                "and UserCoupon.useDate is null)";

        return this.jdbcTemplate.queryForObject(query, int.class, userIdx, couponIdx);
    }

    public int checkCouponByStoreIdx(int storeIdx) {
        String checkCouponByStoreQuery = "select exists(select Coupon.idx " +
                "from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.ExpirationDate >= date_format(now(), '%Y%m%d') and Coupon.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, storeIdx);
    }

    public GetStoreCoupon selectStoreCoupon(int storeIdx) {
        String selectStoreCoupon = "select idx as 'couponIdx', discountPrice from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.status != 'N' and Coupon.ExpirationDate >= date_format(now(), '%Y%m%d')";

        return this.jdbcTemplate.queryForObject(selectStoreCoupon,
                (rs,rowNum) -> new GetStoreCoupon(
                        rs.getInt("couponIdx"),
                        rs.getInt("discountPrice")), storeIdx);
    }

    public List<GetCouponsRes> selectUserCoupons(int userIdx) {
        String selectUserCouponsQuery = "select UserCoupon.idx as 'userCouponIdx', couponName, " +
                "concat(format(discountPrice, 0), '원 할인') as 'discountPrice', " +
                "if (Coupon.minOrderPrice = 0, null, concat(format(Coupon.minOrderPrice, 0), '원 이상 주문 시')) as 'minOrderPrice', " +
                "case when Coupon.expirationDate < date_format(now(), '%Y%m%d') then '기간만료' " +
                "when Coupon.expirationDate >= date_format(now(), '%Y%m%d') and UserCoupon.useDate is not null then '사용완료' " +
                "when Coupon.expirationDate >= date_format(now(), '%Y%m%d') then DATE_FORMAT(Coupon.expirationDate, '%m/%d 까지') " +
                "end as 'expirationDate', " +
                "if (UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d'), 'Y', 'N') as 'isAvailable' " +
                "from UserCoupon inner join Coupon on Coupon.idx = UserCoupon.couponIdx " +
                "where UserCoupon.status != 'N' and UserCoupon.userIdx = ? and Coupon.status != 'N' " +
                "order by case when UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d') then 1 " +
                "else 2 end, Coupon.expirationDate desc";

        return this.jdbcTemplate.query(selectUserCouponsQuery,
                (rs,rowNum) -> new GetCouponsRes(
                        rs.getInt("userCouponIdx"),
                        rs.getString("couponName"),
                        rs.getString("discountPrice"),
                        rs.getString("minOrderPrice"),
                        rs.getString("expirationDate"),
                        rs.getString("isAvailable")), userIdx);
    }

    public int insertUserCoupon(int couponIdx, int userIdx) {
        String insertUserCouponQuery = "insert into UserCoupon (couponIdx, userIdx) values (?, ?);";
        Object[] insertUserCouponParams = new Object[]{couponIdx, userIdx};
        this.jdbcTemplate.update(insertUserCouponQuery, insertUserCouponParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int selectCouponByCouponNumber(String couponNumber) {
        String selectCouponByCouponNumberQuery = "select idx from Coupon where Coupon.status != 'N' and Coupon.couponNumber = ?";

        return this.jdbcTemplate.queryForObject(selectCouponByCouponNumberQuery, int.class, couponNumber);
    }

    public int checkCouponByCouponNumber(String couponNumber) {
        String checkCouponByCouponNumberQuery = "select exists(select idx from Coupon where couponNumber = ? and Coupon.status != 'N' and Coupon.expirationDate >= date_format(now(), '%Y%m%d'))";
        String checkCouponByCouponNumberParams = couponNumber;

        return this.jdbcTemplate.queryForObject(checkCouponByCouponNumberQuery, int.class, checkCouponByCouponNumberParams);
    }

    public int checkUserCouponByCouponNumber(String couponNumber, int userIdx) {
        String query = "select exists(select UserCoupon.idx " +
                "from Coupon inner join UserCoupon on Coupon.idx = UserCoupon.couponIdx " +
                "where UserCoupon.userIdx = ? and Coupon.couponNumber = ? and UserCoupon.status != 'N' and Coupon.status != 'N')";

        return this.jdbcTemplate.queryForObject(query, int.class, userIdx, couponNumber);
    }

    public int updateUserCoupon(int userIdx, int couponIdx) {
        String updateUserCouponQuery = "update UserCoupon set useDate = NOW() where idx = ? and userIdx = ? and status != 'N'";
        Object[] updateUserCouponParams = new Object[]{couponIdx, userIdx};

        return this.jdbcTemplate.update(updateUserCouponQuery, updateUserCouponParams);
    }

    public int checkIsAvailableUserCouponInStore(int userCouponIdx, int storeIdx) {
        String query = "select exists(select UserCoupon.idx " +
                "from UserCoupon inner join Coupon on UserCoupon.couponIdx = Coupon.idx " +
                "where UserCoupon.status != 'N' and Coupon.status != 'N' and UserCoupon.useDate is null and " +
                "Coupon.expirationDate >= date_format(now(), '%Y%m%d') and " +
                "UserCoupon.idx = ? and Coupon.storeIdx = ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, userCouponIdx, storeIdx);
    }

    // 가게에서 사용가능한 쿠폰 limit 1개
    public GetCartCoupon getUserCouponInStore(int userIdx, int storeIdx) {
        String query = "select case UserCoupon.redeemStatus when 'AUTO' then '쿠폰 자동적용' " +
                "when 'SELECTED' then '쿠폰 적용' " +
                "when 'NONE' then '쿠폰 적용가능' end as 'redeemStatus', " +
                "if(UserCoupon.redeemStatus = 'NONE', null, UserCoupon.idx) as 'couponIdx', " +
                "if(UserCoupon.redeemStatus = 'NONE', 0, Coupon.discountPrice) as 'discountPrice' " +
                "from UserCoupon inner join Coupon on UserCoupon.couponIdx = Coupon.idx " +
                "where UserCoupon.status != 'N' and userIdx = ? and (Coupon.storeIdx = ? or Coupon.storeIdx = 0) " +
                "and Coupon.status != 'N' and UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d') " +
                "order by UserCoupon.updatedAt desc " +
                "limit 1";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum) -> new GetCartCoupon(
                        rs.getString("redeemStatus"),
                        rs.getInt("couponIdx"),
                        rs.getInt("discountPrice")), userIdx, storeIdx);
    }

    public int getUserCouponCountInStore(int userIdx, int storeIdx) {
        String query = "select count(*) " +
                "from UserCoupon inner join Coupon on UserCoupon.couponIdx = Coupon.idx " +
                "where UserCoupon.status != 'N' and userIdx = ? and (Coupon.storeIdx = ? or Coupon.storeIdx = 0) " +
                "and Coupon.status != 'N' and UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d')";

        return this.jdbcTemplate.queryForObject(query, int.class, userIdx, storeIdx);
    }

    public List<GetCouponsRes> selectUserCouponsInStore(int userIdx, int storeIdx) {
        String query = "select UserCoupon.idx as 'userCouponIdx', couponName, concat(format(Coupon.discountPrice, 0), '원 할인') as 'discountPrice', " +
                "concat(format(Coupon.minOrderPrice, 0), '원 이상 주문 시') as 'minOrderPrice', " +
                "case when Coupon.expirationDate < date_format(now(), '%Y%m%d') then '기간만료' " +
                "when Coupon.expirationDate >= date_format(now(), '%Y%m%d') and UserCoupon.useDate is not null then '사용완료' " +
                "when Coupon.expirationDate >= date_format(now(), '%Y%m%d') then DATE_FORMAT(Coupon.expirationDate, '%m/%d 까지') " +
                "end as 'expirationDate', " +
                "if((Coupon.storeIdx = ? or Coupon.storeIdx = 0) and UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d'), 'Y', 'N') as 'isAvailable' " +
                "from UserCoupon join Coupon on UserCoupon.couponIdx = Coupon.idx " +
                "where UserCoupon.status != 'N' and userIdx = ? and Coupon.status != 'N' " +
                "order by case when UserCoupon.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d') and (Coupon.storeIdx = ? or Coupon.storeIdx = 0) then 1 " +
                "else 3 end, Coupon.expirationDate desc";

        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> new GetCouponsRes(
                        rs.getInt("userCouponIdx"),
                        rs.getString("couponName"),
                        rs.getString("discountPrice"),
                        rs.getString("minOrderPrice"),
                        rs.getString("expirationDate"),
                        rs.getString("isAvailable")), storeIdx, userIdx, storeIdx);
    }

    public int checkCoupon(int couponIdx) {
        String checkCouponQuery = "select exists(select Coupon.idx " +
                "from Coupon " +
                "where Coupon.ExpirationDate >= date_format(now(), '%Y%m%d') and Coupon.status != 'N' and " +
                "idx = ?)";

        return this.jdbcTemplate.queryForObject(checkCouponQuery, int.class, couponIdx);

    }

    // 현재 가게에서 지급중인 쿠폰을 사용했는지 확인
    public int checkUsedUserCoupon(int couponIdx, Integer userIdx) {
        String checkQuery = "select exists(select 1 from UserCoupon " +
                "where couponIdx = ? and userIdx = ? and useDate is not null and status != 'N')";

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, couponIdx, userIdx);
    }
}
