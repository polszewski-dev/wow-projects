package wow.commons.model.spell;

import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.component.DirectComponent;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
public interface Spell extends Described, TimeRestricted {
	int getId();

	SpellType getType();

	SpellSchool getSchool();

	List<DirectComponent> getDirectComponents();

	Conversion getConversion();

	EffectApplication getEffectApplication();

	default Effect getAppliedEffect() {
		return getEffectApplication() != null ? getEffectApplication().effect() : null;
	}

	default Set<SpellTarget> getTargets() {
		var result = EnumSet.noneOf(SpellTarget.class);

		for (var directComponent : getDirectComponents()) {
			result.add(directComponent.target());
		}

		var effectApplication = getEffectApplication();

		if (effectApplication != null) {
			result.add(effectApplication.target());
		}

		return  result;
	}

	boolean hasDamagingComponent();

	boolean hasHealingComponent();
}
