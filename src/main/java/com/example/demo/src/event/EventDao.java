package com.example.demo.src.event;

import com.example.demo.src.event.model.GetEventBannerRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EventDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetEventBannerRes> selectEventBanners() {
        String selectEventBannersQuery = "select Event.idx as 'eventIdx', Event.bannerUrl from Event " +
                "where now() < endDate and status != 'N'";

        return this.jdbcTemplate.query(selectEventBannersQuery,
                (rs,rowNum) -> new GetEventBannerRes(rs.getInt("eventIdx"),
                        rs.getString("bannerUrl")));
    }
}
