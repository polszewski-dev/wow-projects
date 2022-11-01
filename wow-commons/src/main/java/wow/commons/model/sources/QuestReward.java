package wow.commons.model.sources;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class QuestReward extends NotSourcedFromInstance {
	private final boolean dungeon;
	private final String name;

	QuestReward(boolean dungeon, String name) {
		this.dungeon = dungeon;
		this.name = name;
	}

	public boolean isDungeon() {
		return dungeon;
	}

	@Override
	public String getQuestName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof QuestReward)) return false;
		QuestReward that = (QuestReward) o;
		return dungeon == that.dungeon &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dungeon, name);
	}

	@Override
	public String toString() {
		if (name != null) {
			return "Quest: " + name;
		}
		return "Quest";
	}
}
