package sonar.respond_entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sonar.entities.Issues;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuesResponse {
    private List<Issues> issues;
    private Integer total;
    private Integer ps;
    private Integer p;
}
