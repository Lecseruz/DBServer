package application.service.impl;

import application.dao.StatusDao;
import application.models.Status;
import application.service.api.IStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusService implements IStatusService {
    private StatusDao statusDao;

    @Autowired
    public StatusService(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    @Override
    public void clear() {
        statusDao.clearTable();
    }

    @Override
    public Status getStatus() {
        return statusDao.getStatus();
    }
}
