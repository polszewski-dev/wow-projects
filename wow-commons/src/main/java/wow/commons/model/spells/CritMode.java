package wow.commons.model.spells;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
public enum CritMode {
	ALWAYS {
		@Override
		public double getActualCritChance(Snapshot snapshot) {
			return 1;
		}
	},
	NEVER {
		@Override
		public double getActualCritChance(Snapshot snapshot) {
			return 0;
		}
	},
	AVERAGE {
		@Override
		public double getActualCritChance(Snapshot snapshot) {
			return snapshot.getCritChance();
		}
	};

	public abstract double getActualCritChance(Snapshot snapshot);
}
