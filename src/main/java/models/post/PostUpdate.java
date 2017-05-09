package models.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by magomed on 08.05.17.
 */
public class PostUpdate {
    @JsonProperty
    private String message;

    @JsonCreator
    public PostUpdate(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
