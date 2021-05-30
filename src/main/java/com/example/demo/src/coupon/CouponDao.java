package com.example.demo.src.coupon;

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

    public int checkCouponUser(int couponIdx, Integer userIdx) {
        String checkCouponByStoreQuery = "select exists(select CouponUser.idx " +
                "from Coupon inner join CouponUser on Coupon.idx = CouponUser.couponIdx " +
                "where CouponUser.userIdx = ? and Coupon.idx = ? and CouponUser.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, userIdx, couponIdx);
    }

    public int checkCouponByStoreIdx(int storeIdx) {
        String checkCouponByStoreQuery = "select exists(select Coupon.idx " +
                "from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.ExpirationDate > now() and Coupon.status != 'N')";

        return this.jdbcTemplate.queryForObject(checkCouponByStoreQuery, int.class, storeIdx);
    }

    public GetStoreCoupon selectStoreCoupon(int storeIdx) {
        String selectStoreCoupon = "select idx as 'couponIdx', discountPrice from Coupon " +
                "where Coupon.storeIdx = ? and Coupon.status != 'N' and Coupon.ExpirationDate > now()";

        return this.jdbcTemplate.queryForObject(selectStoreCoupon,
                (rs,rowNum) -> new GetStoreCoupon(
                        rs.getInt("couponIdx"),
                        rs.getInt("discountPrice")), storeIdx);
    }

    public List<GetCouponsRes> selectUserCoupons(int userIdx) {
        String selectUserCouponsQuery = "select CouponUser.couponIdx as 'userCouponIdx', couponName, " +
                "concat(format(discountPrice, 0), '원 할인') as 'discountPrice', " +
                "if (Coupon.minOrderPrice = 0, null, concat(format(Coupon.minOrderPrice, 0), '원 이상 주문 시')) as 'minOrderPrice', " +
                "case when Coupon.expirationDate < date_format(now(), '%Y%m%d') then '기간만료' " +
                "when Coupon.expirationDate >= date_format(now(), '%Y%m%d') then DATE_FORMAT(Coupon.expirationDate, '%m/%d 까지') " +
                "end as 'expirationDate', " +
                "case when Coupon.expirationDate < date_format(now(), '%Y%m%d') then 'expiry' " +
                "when CouponUser.useDate is not null then 'used' " +
                "when CouponUser.useDate is null and Coupon.expirationDate >= date_format(now(), '%Y%m%d') then 'available' " +
                "end as 'status' " +
                "from CouponUser inner join Coupon on Coupon.idx = CouponUser.couponIdx " +
                "where CouponUser.status != 'N' and CouponUser.userIdx = ?";

        return this.jdbcTemplate.query(selectUserCouponsQuery,
                (rs,rowNum) -> new GetCouponsRes(
                        rs.getInt("userCouponIdx"),
                        rs.getString("couponName"),
                        rs.getString("discountPrice"),
                        rs.getString("minOrderPrice"),
                        rs.getString("expirationDate"),
                        rs.getString("status")), userIdx);
    }
}
