package com.example.demo.src.address;

import com.example.demo.src.address.model.GetAddressRes;
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
                "from Address inner join User on Address.userIdx = User.idx where userIdx = ? and Address.status != 'N' and User.status != 'N' " +
                "order by case when Address.alias = '집' then 0 " +
                "when Address.alias = '회사' then 1 " +
                "else 2 end, Address.updatedAt DESC";
        int selectAddressListParams = userIdx;

        return this.jdbcTemplate.query(selectAddressListQuery,
                (rs,rowNum) -> new GetAddressRes(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")), selectAddressListParams);
    }

}
