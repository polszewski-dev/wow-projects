package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class BuffPO implements Serializable {
	private int id;
	private String name;
}
