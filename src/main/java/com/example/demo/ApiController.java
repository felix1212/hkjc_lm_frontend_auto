/*
 *  Change log:
 *  1.0.3 - Added getRandomNumber() to return a random value for Dynamic Instrumentation testing
 *  1.0.4 - Added /dyninstr endpoint for Dynamic Instrumentation testing
 *  1.0.5 - Add latency settings
 */

package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @Value("${dd.query.url}")
    private String ddQueryUrl;

    @Value("${dd.insert.url}")
    private String ddInsertUrl;

    @Value("${dd.truncate.url}")
    private String ddTruncateUrl;

    @Value("${query.delay}")
    private int queryDelay;

    @Value("${insert.delay}")
    private int insertDelay;

    @Value("${truncate.delay}")
    private int truncateDelay;

    /**
     * Query table via DD backend
     */
    @GetMapping("/dd/query")
    public ResponseEntity<String> query() {
        try {
            logger.info(ddQueryUrl + " called with a delay of " + queryDelay + "ms");
            Thread.sleep(queryDelay);
            int randomValue = getRandomNumber();
            logger.info("Generated random value: {}", randomValue);
            return sendRequest(ddQueryUrl, HttpMethod.GET, null);
		} catch (Exception e) {
			logger.error("e");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Insert into table via DD backend
     */
    @PutMapping("/dd/insert")
    public ResponseEntity<String> insert(@RequestBody String jsonPayload) {
        try {
            logger.info(ddInsertUrl + " called" + insertDelay + "ms");
            Thread.sleep(insertDelay);
            int randomValue = getRandomNumber();
            logger.info("Generated random value: {}", randomValue);
            return sendRequest(ddInsertUrl, HttpMethod.PUT, jsonPayload);
		} catch (Exception e) {
			logger.error("e");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Truncate table via DD backend
     */
    @PostMapping("/dd/truncate")
    public ResponseEntity<String> truncate() {
        try {
            logger.info(ddTruncateUrl + " called" + truncateDelay + "ms");
            Thread.sleep(truncateDelay);
            int randomValue = getRandomNumber();
            logger.info("Generated random value: {}", randomValue);
            return sendRequest(ddTruncateUrl, HttpMethod.POST, null);
		} catch (Exception e) {
			logger.error("e");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Simulate a 5XX error
     */
    @GetMapping("/error")
    public ResponseEntity<String> generateError() {
        logger.info("error endpoint called");
        // Simulate a 500 Internal Server Error
        int randomValue = getRandomNumber();
        logger.info("Generated random value: {}", randomValue);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Simulate Dynamic Instrumentation
     */
    @GetMapping("/dyninstr")
    public boolean dynInstr() {
        logger.info("dyninstr endpoint called");
        int randomValue = getRandomNumber();
        logger.info("Generated random value: {}", randomValue);
        return true; 
    }


    private ResponseEntity<String> sendRequest(String url, HttpMethod method, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
    
        // Filter out Transfer-Encoding headers
        HttpHeaders responseHeaders = new HttpHeaders();
        for (String headerName : response.getHeaders().keySet()) {
            if (!headerName.equalsIgnoreCase(HttpHeaders.TRANSFER_ENCODING)) {
                responseHeaders.put(headerName, response.getHeaders().get(headerName));
            }
        }
    
        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 10) + 1;
    }
}
