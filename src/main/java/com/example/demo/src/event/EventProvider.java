package com.example.demo.src.event;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.event.model.GetEventBannerRes;
import com.example.demo.src.event.model.GetEventContentsRes;
import com.example.demo.src.event.model.GetEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.EVENTS_NOT_FOUND;

@Service
public class EventProvider {
    private final EventDao eventDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EventProvider(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<GetEvents> getAllEvent() throws BaseException {
        try {
            List<GetEvents> result = eventDao.selectEventsInfo();
            return result;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetEventContentsRes getEventContents(int eventIdx) throws BaseException {
        if (eventDao.checkEvent(eventIdx) == 0) {
            throw new BaseException(EVENTS_NOT_FOUND);
        }

        try {
            GetEventContentsRes eventContents = eventDao.selectEventContent(eventIdx);
            return eventContents;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
