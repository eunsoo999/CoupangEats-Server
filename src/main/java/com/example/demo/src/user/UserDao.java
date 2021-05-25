package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (email, password, userName, phone) values (?, ?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPassword(), postUserReq.getUserName(), postUserReq.getPhone()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select idx as 'userIdx', email, password, userName, phone from User where email = ? and status != 'N'";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("userName"),
                        rs.getString("phone")
                ), getPwdParams);
    }

    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select userName, concat(left(phone, 3), '-****-', right(phone, 4)) as 'phone' from User where status != 'N' and idx = ?";
        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs,rowNum)-> new GetUserRes(
                        rs.getString("userName"),
                        rs.getString("phone")
                ), getUserParams);
    }

    // 이메일 중복 확인
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ? and status != 'N')";
        String checkEmailParams = email;

        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }

    // 전화번호 중복 확인
    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select phone from User where phone = ? and status != 'N')";
        String checkPhoneParams = phone;

        return this.jdbcTemplate.queryForObject(checkPhoneQuery, int.class, checkPhoneParams);
    }

    // 탈퇴한 유저 확인
    public int checkDeletedUser(String email) {
        String checkDeletedUserQuery = "select exists(select idx from User where email = ? AND User.status = 'N')";
        String checkDeletedUserParams = email;

        return this.jdbcTemplate.queryForObject(checkDeletedUserQuery, int.class, checkDeletedUserParams);
    }

    // 활성화 userIdx가 있는지 확인
    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select idx from User where idx = ? AND User.status != 'N')";
        int checkUserIdxParams = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserIdxQuery, int.class, checkUserIdxParams);
    }

    public Integer selectUserAddressIdx(int userIdx) {
        String selectUserAddressIdxQuery = "select addressIdx from User where idx= ? and status != 'N'";
        int selectUserAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectUserAddressIdxQuery, Integer.class, selectUserAddressParams);
    }

    public String selectMaskingEmailByPhone(String phone) {
        String selectMaskingEmailByPhoneQuery = "select concat(REGEXP_REPLACE(substring_index(email, '@', 1), " +
                "'(?<=.{2}).', '*'), '@',SUBSTRING_INDEX(email, '@', -1)) AS duplicatedEmail " +
                "from User where status != 'N' and phone = ?";
        String selectMaskingEmailByPhoneParams = phone;

        return this.jdbcTemplate.queryForObject(selectMaskingEmailByPhoneQuery, String.class, selectMaskingEmailByPhoneParams);
    }

    public int getUserHomeIdx(int userIdx) {
        String getUserHomeIdxQuery = "select idx from Address where userIdx = ? and status = 'HOME'";
        int checkHomeIdxParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserHomeIdxQuery, int.class, checkHomeIdxParams);
    }

    public int checkUserHomeAddress(int userIdx) {
        String checkUserHomeAddressQuery = "select exists(select idx from Address where userIdx = ? and status = 'HOME')";
        int checkUserHomeAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserHomeAddressQuery, int.class, checkUserHomeAddressParams);
    }

    public int getUserCompanyIdx(int userIdx) {
        String getUserCompanyIdxQuery = "select idx from Address where userIdx = ? and status = 'COMPANY'";
        int getUserCompanyIdxParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserCompanyIdxQuery, int.class, getUserCompanyIdxParams);
    }

    public int checkUserCompanyAddress(int userIdx) {
        String checkUserCompanyAddressQuery = "select exists(select idx from Address where userIdx = ? and status = 'COMPANY')";
        int checkUserCompanyAddressParams = userIdx;

        return this.jdbcTemplate.queryForObject(checkUserCompanyAddressQuery, int.class, checkUserCompanyAddressParams);
    }


    public int updateStatusAddress(int homeIdx) {
        String updateStatusAddressQuery = "update Address set status = 'N' where idx = ? ";
        int updateStatusAddressParams = homeIdx;

        return this.jdbcTemplate.update(updateStatusAddressQuery, updateStatusAddressParams); // 개수 반환
    }
}
