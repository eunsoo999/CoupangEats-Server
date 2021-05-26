package com.example.demo.src.address;

import com.example.demo.src.address.model.*;
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
        String selectAddressListQuery = "select Address.idx as 'addressIdx', if ((alias is null || length(replace(Address.alias, ' ', '')) = 0), address, alias) as 'mainAddress', " +
                "concat(roadAddress, if((Address.detailAddress is not null), concat(' ', Address.detailAddress), '')) as 'subAddress' " +
                "from Address inner join User on Address.userIdx = User.idx " +
                "where userIdx = ? and Address.status = 'ETC' and User.status != 'N' " +
                "order by Address.updatedAt DESC";
        int selectAddressListParams = userIdx;

        return this.jdbcTemplate.query(selectAddressListQuery,
                (rs,rowNum) -> new GetAddressRes(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress")), selectAddressListParams);
    }

    public int insertAddress(PostAddressReq postAddressReq) {
        String insertAddressQuery = "INSERT INTO Address (address, roadAddress, detailAddress, alias, latitude, longitude, userIdx, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] insertAddressParams = new Object[]{postAddressReq.getAddress(), postAddressReq.getRoadAddress(), postAddressReq.getDetailAddress(),
                postAddressReq.getAlias(), postAddressReq.getLatitude(), postAddressReq.getLongitude(), postAddressReq.getUserIdx(), postAddressReq.getAliasType().toUpperCase()};
        this.jdbcTemplate.update(insertAddressQuery, insertAddressParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public GetHomeAddress selectHomeAddress(int userIdx) {
        String selectHomeAddressQuery = "select idx as 'addressIdx', '집' as 'mainAddress', " +
                "concat(Address.roadAddress, if(Address.detailAddress is not null, concat(' ', Address.detailAddress), '')) as 'subAddress' " +
                "from Address where userIdx = ? and status = 'HOME'";

        int selectHomeAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectHomeAddressQuery,
                (rs,rowNum) -> new GetHomeAddress(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress")), selectHomeAddressParams);
    }

    public GetCompanyAddress selectCompanyAddress(int userIdx) {
        String selectCompanyAddressQuery = "select idx as 'addressIdx', '회사' as 'mainAddress', " +
                "concat(Address.roadAddress, if(Address.detailAddress is not null, concat(' ', Address.detailAddress), '')) as 'subAddress' " +
                "from Address " +
                "where userIdx = ? and status = 'COMPANY'";

        int selectCompanyAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectCompanyAddressQuery,
                (rs,rowNum) -> new GetCompanyAddress(
                        rs.getInt("addressIdx"),
                        rs.getString("mainAddress"),
                        rs.getString("subAddress")), selectCompanyAddressParams);
    }

    public int checkAddressIdx(int addressIdx) {
        String checkAddressQuery = "select exists(select idx from Address where idx = ? and status != 'N')";
        int checkAddressParams = addressIdx;

        return this.jdbcTemplate.queryForObject(checkAddressQuery, int.class, checkAddressParams);
    }

    public GetAddressDetailRes getAddress(int addressIdx) {
        String getAddressDetailQuery = "select address, roadAddress, detailAddress, status as 'aliasType', alias from Address where idx = ?";
        int getAddressParams = addressIdx;

        return this.jdbcTemplate.queryForObject(getAddressDetailQuery,
                (rs,rowNum) -> new GetAddressDetailRes(
                        rs.getString("address"),
                        rs.getString("roadAddress"),
                        rs.getString("detailAddress"),
                        rs.getString("aliasType"),
                        rs.getString("alias")), getAddressParams);
    }

    public int checkAddressByOwner(int addressIdx, int userIdx) {
        String checkAddressQuery = "select exists(select idx from Address where idx = ? and userIdx = ? and status != 'N')";
        Object[] checkAddressParams = new Object[] {addressIdx, userIdx};

        return this.jdbcTemplate.queryForObject(checkAddressQuery, int.class, checkAddressParams);
    }

    public int updateAddress(int addressIdx, PatchAddressReq patchAddressReq) {
        String updateAddressQuery = "update Address set detailAddress = ?, alias = ?, status = ? where idx = ?";
        Object[] updateAddressParams = new Object[]{patchAddressReq.getDetailAddress(), patchAddressReq.getAlias(), patchAddressReq.getAliasType(), addressIdx};

        return this.jdbcTemplate.update(updateAddressQuery, updateAddressParams); // 개수 반환
    }

    public int updateAddressTypeAndInitAlias(int beforeHomeIdx, String typeStr) {
        String updateAddressTypeQuery = "update Address set status = ?, alias = null where idx = ?";
        Object[] updateAddressTypeParams = new Object[]{typeStr, beforeHomeIdx};

        return this.jdbcTemplate.update(updateAddressTypeQuery, updateAddressTypeParams);
    }

    public int updateStatusAddress(int addressIdx) {
        String updateStatusAddressQuery = "update Address set status = 'N' where idx = ?";
        int updateStatusAddressParams = addressIdx;

        return this.jdbcTemplate.update(updateStatusAddressQuery, updateStatusAddressParams);
    }
}
