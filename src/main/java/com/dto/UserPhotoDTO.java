package com.dto;

public class UserPhotoDTO {
    private Long id;
    private String username;
    private String foto;

    public UserPhotoDTO() {
    }

    public UserPhotoDTO(Long id, String username, String foto) {
        this.id = id;
        this.username = username;
        this.foto = foto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
