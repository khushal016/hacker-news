package com.paytm.insider.controller;

import com.paytm.insider.dto.StoriesResponseDTO;
import com.paytm.insider.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    private final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @RequestMapping(value = "/top-stories", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoriesResponseDTO> getTopStories() {
        logger.info("Request for top 10 stories");
        try {
            return new ResponseEntity<>(newsService.getTopStories(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception fetching top stories---", e);
            return new ResponseEntity<>(new StoriesResponseDTO(false, "Something went wrong"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/past-stories", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoriesResponseDTO> getPastStories() {
        logger.info("Request for past stories");
        try {
            return new ResponseEntity<>(newsService.getPastStories(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception fetching past stories---", e);
            return new ResponseEntity<>(new StoriesResponseDTO(false, "Something went wrong"), HttpStatus.OK);
        }
    }
}
