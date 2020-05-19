package application.controller;

import application.service.api.IStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/service")
public class ServiceController {
    private final IStatusService statusService;


    @Autowired
    public ServiceController(IStatusService statusService) {
        this.statusService = statusService;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public ResponseEntity<?> clear() {
        statusService.clear();
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(statusService.getStatus());
    }
}
