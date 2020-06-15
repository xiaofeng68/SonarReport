package sonar.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issues {
    private String severity;//类型
    private String component;//路径
    private String project;
    private String line;//所在行数
    private String status;//状态
    private String message;//信息
    private String author;
    private String assignee;
    private String creationDate;
    private Date updateDate;
    private String type;
    private List<Comment> comments;
    public String toString(){
        return project+","+severity+","+component+","+line+","+status+","+message;
    }
}
