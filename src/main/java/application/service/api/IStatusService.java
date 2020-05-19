package application.service.api;

import application.models.Status;

public interface IStatusService {
    void clear();

    Status getStatus();
}
