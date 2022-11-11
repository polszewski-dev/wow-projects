package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public abstract class Attribute {
	protected final AttributeCondition condition;

	protected Attribute(AttributeCondition condition) {
		this.condition = condition;
		if (condition == null) {
			throw new IllegalArgumentException();
		}
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, double value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return value != 0 ? of(id, value, condition) : null;
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public abstract AttributeId getId();

	public AttributeCondition getCondition() {
		return condition;
	}

	public abstract Attribute attachCondition(AttributeCondition condition);

	public boolean hasCondition() {
		return !condition.isEmpty();
	}

	public boolean isTheSameOrNull(TalentTree talentTree) {
		return !hasCondition() || condition.isTheSameOrNull(talentTree);
	}

	public boolean isTheSameOrNull(SpellSchool spellSchool) {
		return !hasCondition() || condition.isTheSameOrNull(spellSchool);
	}

	public boolean isTheSameOrNull(SpellId spellId) {
		return !hasCondition() || condition.isTheSameOrNull(spellId);
	}

	public boolean isTheSameOrNull(PetType petType) {
		return !hasCondition() || condition.isTheSameOrNull(petType);
	}

	public boolean isTheSameOrNull(CreatureType creatureType) {
		return !hasCondition() || condition.isTheSameOrNull(creatureType);
	}

	public boolean isMatchedBy(AttributeFilter filter) {
		return filter == null || filter.matchesCondition(condition);
	}

	@Override
	public abstract String toString();

	protected String getConditionString() {
		return condition.isEmpty() ? "" : " | " + condition;
	}
}
