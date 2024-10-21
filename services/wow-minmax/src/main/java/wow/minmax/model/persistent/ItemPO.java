package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@Getter
@Setter
public class ItemPO implements Serializable {
	private int id;
	private String name;
}
