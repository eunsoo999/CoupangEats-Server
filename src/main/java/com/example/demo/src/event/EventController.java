package com.example.demo.src.event;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.event.model.GetEventContentsRes;
import com.example.demo.src.event.model.GetEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final EventProvider eventProvider;

    public EventController(EventProvider eventProvider) {
        this.eventProvider = eventProvider;
    }

    /**
     * 46. 이벤트 전체 조회 API
     * [GET] /events
     * @return BaseResponse<List<GetEvents>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetEvents>> getAllEvent() {
        try {
            List<GetEvents> result = eventProvider.getAllEvent();
            if (result.isEmpty()) {
                result = null;
            }
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            logger.warn("#46. " + exception.getStatus().getMessage());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 47. 이벤트 상세 조회 API
     * [GET] /events/:eventIdx
     * @return BaseResponse<GetEventContentsRes>
     */
    @ResponseBody
    @GetMapping("/{eventIdx}")
    public BaseResponse<GetEventContentsRes> getAllEvent(@PathVariable int eventIdx) {
        try {
            GetEventContentsRes eventContents = eventProvider.getEventContents(eventIdx);
            return new BaseResponse<>(eventContents);
        } catch (BaseException exception) {
            logger.warn("#47. " + exception.getStatus().getMessage());
            logger.warn("(" + eventIdx + ")");
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
