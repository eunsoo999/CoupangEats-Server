package com.example.demo.src.event;

import com.example.demo.src.coupon.model.GetCouponsRes;
import com.example.demo.src.event.model.GetEventBannerRes;
import com.example.demo.src.event.model.GetEventContentsRes;
import com.example.demo.src.event.model.GetEvents;
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

    public List<GetEvents> selectEventsInfo() {
        String selectEventsInfoQuery = "select Event.idx as 'eventIdx', Event.bannerUrl, date_format(Event.endDate, '~ %m.%d까지') as 'endDate' from Event " +
                "where now() < endDate and status != 'N'";

        return this.jdbcTemplate.query(selectEventsInfoQuery,
                (rs,rowNum) -> new GetEvents(rs.getInt("eventIdx"),
                        rs.getString("bannerUrl"),
                        rs.getString("endDate")));
    }

    public GetEventContentsRes selectEventContent(int eventIdx) {
        String selectEventContent = "select title, contentsUrl from Event where idx = ?";

        return this.jdbcTemplate.queryForObject(selectEventContent,
                (rs,rowNum) -> new GetEventContentsRes(
                        rs.getString("title"),
                        rs.getString("contentsUrl")), eventIdx);
    }

    public int checkEvent(int eventIdx) {
        String checkEventQuery = "select exists(select idx from Event where idx = ? and now() < endDate and status != 'N')";

        return this.jdbcTemplate.queryForObject(checkEventQuery, int.class, eventIdx);
    }
}
