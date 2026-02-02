package wow.commons.model.effect;

import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.ItemSet;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.Talent;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public sealed interface EffectSource extends Described {
	record AbilitySource(Ability ability) implements EffectSource {
		public AbilityId getAbilityId() {
			return ability.getAbilityId();
		}

		@Override
		public Description getDescription() {
			return ability.getDescription();
		}
	}

	record BuffSource(Description description) implements EffectSource, Comparable<BuffSource> {
		@Override
		public Description getDescription() {
			return description;
		}

		@Override
		public int compareTo(BuffSource o) {
			return this.getName().compareTo(o.getName());
		}
	}

	record EnchantSource(Enchant enchant) implements EffectSource, Comparable<EnchantSource> {
		@Override
		public Description getDescription() {
			return enchant.getDescription();
		}

		@Override
		public int compareTo(EnchantSource o) {
			return Integer.compare(this.enchant.getId().value(), o.enchant.getId().value());
		}
	}

	record ItemSetSource(ItemSet itemSet, int numPieces) implements EffectSource, Comparable<ItemSetSource> {
		@Override
		public Description getDescription() {
			return new Description(
					"%s (%s)".formatted(itemSet.getName(), numPieces),
					itemSet.getIcon(),
					null
			);
		}

		@Override
		public int compareTo(ItemSetSource o) {
			int cmp = this.itemSet.getName().compareTo(o.itemSet.getName());

			if (cmp != 0) {
				return cmp;
			}

			return this.numPieces - o.numPieces;
		}
	}

	record ItemSource(AbstractItem<?> item) implements EffectSource, Comparable<ItemSource> {
		@Override
		public Description getDescription() {
			return item.getDescription();
		}

		@Override
		public int compareTo(ItemSource o) {
			return Integer.compare(this.item.getId().value(), o.item.getId().value());
		}
	}

	record RacialSource(Description description) implements EffectSource, Comparable<RacialSource> {
		@Override
		public Description getDescription() {
			return description;
		}

		@Override
		public int compareTo(RacialSource o) {
			return this.getName().compareTo(o.getName());
		}
	}

	record TalentSource(Talent talent) implements EffectSource, Comparable<TalentSource> {
		@Override
		public Description getDescription() {
			return talent.getDescription();
		}

		@Override
		public int compareTo(TalentSource o) {
			return this.talent.getName().compareTo(o.talent.getName());
		}
	}
}
