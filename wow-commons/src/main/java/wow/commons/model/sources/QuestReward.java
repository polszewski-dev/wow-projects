package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class QuestReward extends Source {
	private final boolean dungeon;
	private final String questName;

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
