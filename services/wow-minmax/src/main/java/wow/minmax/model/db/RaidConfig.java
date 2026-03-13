package wow.minmax.model.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@Document("raid")
@AllArgsConstructor
@Getter
@Setter
public class RaidConfig {
	@Id
	private String playerId;
	private List<List<String>> parties;
}
