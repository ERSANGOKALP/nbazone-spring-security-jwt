package org.ersandev.nbazone.security.response;

import java.util.List;

public class UserJwtInfoResponse {

    private Long id;
    private String token;
    private String username;
    private List<String> roles;

    public UserJwtInfoResponse(Long id, String token, List<String> roles, String username) {
        this.id = id;
        this.token = token;
        this.roles = roles;
        this.username = username;
    }

    public UserJwtInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
