package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.primitive.*;
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
	protected final AttributeId id;
	protected final AttributeCondition condition;

	protected Attribute(AttributeId id, AttributeCondition condition) {
		if (id == null) {
			throw new NullPointerException();
		}
		this.id = id;
		this.condition = condition;
	}

	public static DoubleAttribute of(AttributeId id, double value) {
		return of(id, value, null);
	}

	public static DoubleAttribute of(AttributeId id, double value, AttributeCondition condition) {
		return new DoubleAttribute(id, value, condition);
	}

	public static PercentAttribute of(AttributeId id, Percent value) {
		return of(id, value, null);
	}

	public static PercentAttribute of(AttributeId id, Percent value, AttributeCondition condition) {
		return new PercentAttribute(id, value, condition);
	}

	public static BooleanAttribute of(AttributeId id, boolean value) {
		return of(id, value, null);
	}

	public static BooleanAttribute of(AttributeId id, boolean value, AttributeCondition condition) {
		return new BooleanAttribute(id, value, condition);
	}

	public static DurationAttribute of(AttributeId id, Duration value) {
		return of(id, value, null);
	}

	public static DurationAttribute of(AttributeId id, Duration value, AttributeCondition condition) {
		return new DurationAttribute(id, value, condition);
	}

	public static DoubleAttribute ofNullable(AttributeId id, double value) {
		return ofNullable(id, value, null);
	}

	public static DoubleAttribute ofNullable(AttributeId id, double value, AttributeCondition condition) {
		return value != 0 ? of(id, value, condition) : null;
	}

	public static PercentAttribute ofNullable(AttributeId id, Percent value) {
		return ofNullable(id, value, null);
	}

	public static PercentAttribute ofNullable(AttributeId id, Percent value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public static BooleanAttribute ofNullable(AttributeId id, boolean value) {
		return ofNullable(id, value, null);
	}

	public static BooleanAttribute ofNullable(AttributeId id, boolean value, AttributeCondition condition) {
		return value ? of(id, value, condition) : null;
	}

	public static DurationAttribute ofNullable(AttributeId id, Duration value) {
		return ofNullable(id, value, null);
	}

	public static DurationAttribute ofNullable(AttributeId id, Duration value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public AttributeId getId() {
		return id;
	}

	public double getDouble() {
		throw new IllegalArgumentException(id + " isn't a double attribute");
	}

	public Percent getPercent() {
		throw new IllegalArgumentException(id + " isn't a percent attribute");
	}

	public boolean getBoolean() {
		throw new IllegalArgumentException(id + " isn't a boolean attribute");
	}

	public Duration getDuration() {
		throw new IllegalArgumentException(id + " isn't a duration attribute");
	}

	public AttributeCondition getCondition() {
		return condition;
	}

	public abstract Attribute attachCondition(AttributeCondition condition);

	public boolean matches(AttributeCondition filter) {
		if (filter == null) {
			return true;
		}
		return matches(filter.getTalentTree(), filter.getSpellSchool(), filter.getSpellId(), filter.getPetType(), filter.getCreatureType());
	}

	public boolean matches(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		if (condition == null) {
			return true;
		}

		return matches(this.getCondition().getTalentTree(), talentTree, TalentTree.None) &&
				matches(this.getCondition().getSpellSchool(), spellSchool, SpellSchool.None) &&
				matches(this.getCondition().getSpellId(), spellId, SpellId.None) &&
				matches(this.getCondition().getPetType(), petType, PetType.None) &&
				matches(this.getCondition().getCreatureType(), creatureType, CreatureType.None);
	}

	private static <T> boolean matches(T condition, T filter, T none) {
		if (filter == null) {
			return true;
		}
		if (filter == none) {
			return condition == null;
		}
		if (condition == null) {
			return true;
		}
		return condition == filter;
	}

	public PrimitiveAttribute scale(double factor) {
		throw new IllegalArgumentException("Can't scale " + id);
	}

	@Override
	public String toString() {
		String condStr = condition != null ? condition.toString() : "";
		String idFmt = " %s";
		if (id.toString().startsWith("%")) {
			idFmt = "%s";
		}
		return getValueString(idFmt) + (condStr.isEmpty() ? "" : " | " + condStr);
	}

	protected abstract String getValueString(String idFmt);
}
