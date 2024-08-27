package pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MeaageDTO implements Serializable {
    private String username;
    private String message;
}
