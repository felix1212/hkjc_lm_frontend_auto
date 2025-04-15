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

    /**
     * Query table via DD backend
     */
    @GetMapping("/dd/query")
    public ResponseEntity<String> query() {
        logger.info(ddQueryUrl + " called");
        return sendRequest(ddQueryUrl, HttpMethod.GET, null);
    }

    /**
     * Insert into table via DD backend
     */
    @PutMapping("/dd/insert")
    public ResponseEntity<String> insert(@RequestBody String jsonPayload) {
        logger.info(ddInsertUrl + " called");
        return sendRequest(ddInsertUrl, HttpMethod.PUT, jsonPayload);
    }

    /**
     * Truncate table via DD backend
     */
    @PostMapping("/dd/truncate")
    public ResponseEntity<String> truncate() {
        logger.info(ddTruncateUrl + " called");
        return sendRequest(ddTruncateUrl, HttpMethod.POST, null);
    }

    private ResponseEntity<String> sendRequest(String url, HttpMethod method, String body) {
        // Set headers with content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create HTTP entity with body and headers
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Make the HTTP request
        ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);

        // Return the response
        return response;
    }
}
