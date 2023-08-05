package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.buffs.BuffIdAndRank;
import wow.commons.model.character.CreatureType;

import java.util.Collection;
import java.util.Set;

import static wow.character.model.character.BuffListType.TARGET_DEBUFF;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@AllArgsConstructor
@Getter
public class Enemy implements AttributeCollection, Copyable<Enemy> {
	private final CreatureType enemyType;
	private final int levelDifference;
	private final Buffs debuffs;

	public Enemy(CreatureType enemyType, int levelDifference) {
		this.enemyType = enemyType;
		this.levelDifference = levelDifference;
		this.debuffs = new Buffs(TARGET_DEBUFF);
	}

	@Override
	public Enemy copy() {
		return new Enemy(enemyType, levelDifference, debuffs.copy());
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		debuffs.collectAttributes(collector);
	}

	public Set<AttributeCondition> getConditions() {
		return Set.of(AttributeCondition.of(enemyType));
	}

	public void setDebuffs(Collection<BuffIdAndRank> debuffIds) {
		this.debuffs.set(debuffIds);
	}

	public void resetDebuffs() {
		debuffs.reset();
	}
}
