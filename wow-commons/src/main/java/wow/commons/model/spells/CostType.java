package wow.commons.model.spells;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public enum CostType {
	MANA {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.onMana();
		}
	},

	HEALTH {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.onHealth();
		}
	},

	PET_MANA {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.onPetMana();
		}
	},

	;

	public static CostType parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}

	public interface Visitor<T> {
		T onMana();

		T onHealth();

		T onPetMana();
	}

	public abstract <T> T visit(Visitor<T> visitor);
}
