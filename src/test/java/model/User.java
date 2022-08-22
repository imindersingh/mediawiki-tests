package model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
public class User {
    private String name;
    private String password;
    private String username;
    private int userId;
}
