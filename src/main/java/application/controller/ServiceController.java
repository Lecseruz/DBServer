package application.controller;

import application.models.status.StatusJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by magomed on 22.03.17.
 */

@RestController
@RequestMapping(value = "/api/service")
public class ServiceController {
    private final StatusJDBCTemplate statusJDBCTemplate;

    @Autowired
    public ServiceController(StatusJDBCTemplate statusJDBCTemplate) {
        this.statusJDBCTemplate = statusJDBCTemplate;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public ResponseEntity<?> clear() throws IOException {
        statusJDBCTemplate.clearTable();
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<?> status() throws IOException {
        return ResponseEntity.ok(statusJDBCTemplate.getStatus());
    }
}
