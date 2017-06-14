package application.models.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by magomed on 07.05.17.
 */
public class ThreadUpdate {
    @JsonProperty
    private String message;
    @JsonProperty
    private String title;

    @JsonCreator
    public ThreadUpdate(@JsonProperty("message") String message, @JsonProperty("title") String title) {
        this.title = title;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
