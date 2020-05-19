package application.controller;

import application.dao.StatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/service")
public class ServiceController {
    private final StatusDao statusDao;

    @Autowired
    public ServiceController(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public ResponseEntity<?> clear() {
        statusDao.clearTable();
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(statusDao.getStatus());
    }
}
