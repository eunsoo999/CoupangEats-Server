package com.example.demo.src.address;

import com.example.demo.src.address.model.*;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AddressDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetAddressRes> selectAddressList(int userIdx) {
        String selectAddressListQuery = "select Address.idx as 'addressIdx', if (alias is null, address, alias) as 'mainAddress', " +
                "concat(roadAddress, if(Address.detailAddress is not null, concat(' ', Address.detailAddress), '')) as 'subAddress', " +
                "latitude, longitude " +
                "from Address inner join User on Address.userIdx = User.idx " +
                "where userIdx = ? and Address.status != 'N' and User.status != 'N' and Address.idx != User.companyAddressIdx and Address.idx != User.homeAddressIdx " +
                "order by Address.updatedAt DESC";
        int selectAddressListParams = userIdx;

        return this.jdbcTemplate.query(selectAddressListQuery,
                (rs,rowNum) -> new GetAddressRes(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")), selectAddressListParams);
    }

    public int insertAddress(PostAddressReq postAddressReq) {
        String insertAddressQuery = "INSERT INTO Address (address, roadAddress, detailAddress, alias, latitude, longitude, userIdx) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Object[] insertAddressParams = new Object[]{postAddressReq.getAddress(), postAddressReq.getRoadAddress(), postAddressReq.getDetailAddress(),
                postAddressReq.getAlias(), postAddressReq.getLatitude(), postAddressReq.getLongitude(), postAddressReq.getUserIdx()};
        this.jdbcTemplate.update(insertAddressQuery, insertAddressParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public GetHomeAddress selectHomeAddress(int userIdx) {
        String selectHomeAddressQuery = "select User.homeAddressIdx as 'addressIdx', '집' as 'mainAddress', " +
                "concat(Address.roadAddress, if(Address.detailAddress is not null, concat(' ', Address.detailAddress), '')) as 'subAddress', " +
                "Address.latitude, Address.longitude " +
                "from User inner join Address on User.homeAddressIdx = Address.idx where User.status != 'N' and User.idx = ?";

        int selectHomeAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectHomeAddressQuery,
                (rs,rowNum) -> new GetHomeAddress(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")), selectHomeAddressParams);
    }

    public GetCompanyAddress selectCompanyAddress(int userIdx) {
        String selectCompanyAddressQuery = "select User.companyAddressIdx as 'addressIdx', '회사' as 'mainAddress', " +
                "concat(Address.roadAddress, if(Address.detailAddress is not null, concat(' ', Address.detailAddress), '')) as 'subAddress', " +
                "Address.latitude, Address.longitude " +
                "from User inner join Address on User.companyAddressIdx = Address.idx where User.status != 'N' and User.idx = ?";

        int selectCompanyAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectCompanyAddressQuery,
                (rs,rowNum) -> new GetCompanyAddress(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")), selectCompanyAddressParams);
    }
}
