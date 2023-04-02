package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.character.model.build.BuildId;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@AllArgsConstructor
public class BuildPO implements Serializable {
	private BuildId buildId;
}
