package wow.character.model.snapshot;

import wow.commons.model.attribute.Attribute;

import java.util.function.Predicate;

import static wow.commons.model.attribute.AttributeTarget.*;

/**
 * User: POlszewski
 * Date: 2026-08-30
 */
public final class AttributePredicates {
	public static final Predicate<Attribute> OWNER_OR_AURAS = attribute -> attribute.target() == OWNER || attribute.target() == PARTY;

	public static final Predicate<Attribute> PET_ONLY = attribute -> attribute.target() == PET;

	public static final Predicate<Attribute> ANY_TARGET = attribute -> true;
}
