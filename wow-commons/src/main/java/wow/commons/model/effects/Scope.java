package wow.commons.model.effects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public enum Scope {
	PERSONAL {
		@Override
		public boolean visit(Visitor visitor) {
			return visitor.visitPersonal();
		}
	},
	PARTY {
		@Override
		public boolean visit(Visitor visitor) {
			return visitor.visitParty();
		}
	},
	RAID {
		@Override
		public boolean visit(Visitor visitor) {
			return visitor.visitRaid();
		}
	},
	GLOBAL {
		@Override
		public boolean visit(Visitor visitor) {
			return visitor.visitGlobal();
		}
	};

	public interface Visitor {
		boolean visitPersonal();
		boolean visitParty();
		boolean visitRaid();
		boolean visitGlobal();
	}

	public abstract boolean visit(Visitor visitor);
}
