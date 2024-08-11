package pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSet {
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
}
