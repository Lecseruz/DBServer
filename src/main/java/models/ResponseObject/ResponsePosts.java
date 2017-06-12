package models.ResponseObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.post.Post;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magomed on 26.04.17.
 */
public class ResponsePosts {
    @JsonProperty
    String marker;
    @JsonProperty
    List<Post> posts;

    @JsonCreator
    public ResponsePosts(@JsonProperty("marker") String marker, @JsonProperty("posts") List<Post> posts){
        this.marker = marker;
        this.posts = posts;
    }

    public ResponsePosts(){

    }
    public String getMarker() {
        return marker;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void setPosts(@Nullable List<Post> posts) {
        this.posts = posts;
    }
}
