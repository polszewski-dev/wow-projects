package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.character.CreatureType;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@AllArgsConstructor
@Getter
public class Enemy implements AttributeCollection, Copyable<Enemy> {
	private final CreatureType enemyType;

	@Override
	public Enemy copy() {
		return new Enemy(enemyType);
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		// VOID atm
	}

	public Set<AttributeCondition> getConditions() {
		return Set.of(AttributeCondition.of(enemyType));
	}
}
