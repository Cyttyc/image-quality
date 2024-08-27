package pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long id;
    //姓名
    private String username;
    //密码
    private String password;
    private Integer status;

    //注册时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;

    private Integer DA;
    private Integer HTBA;
    private Integer CLBA;
    private Integer lsbPanda;
    private Integer refool;
    private Integer ours;
    private Integer SAA;
    private Integer clean;
    private Integer Inv;
    private Integer Sig;

    private String leaveMessage;

}
