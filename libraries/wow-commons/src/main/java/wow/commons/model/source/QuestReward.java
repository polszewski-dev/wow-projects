package wow.commons.model.source;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record QuestReward(
		boolean dungeon,
		String questName
) implements Source {
	@Override
	public boolean isQuestReward() {
		return true;
	}

	@Override
	public String toString() {
		if (questName != null) {
			return "Quest: " + questName;
		}
		return "Quest";
	}
}
