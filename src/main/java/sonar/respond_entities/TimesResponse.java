package sonar.respond_entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sonar.entities.TimesHistory;
import sonar.entities.TimesMeasures;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimesResponse {
    private List<TimesHistory> measures;


}
