package pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class UserDataDTO implements Serializable {
    private String username;
//    private Map<String, Integer> folderCounts;
    private Map<String, Integer> eloScores;

}
