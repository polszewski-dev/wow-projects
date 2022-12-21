package wow.character.model.build;

import lombok.AllArgsConstructor;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.Collection;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Talents implements AttributeCollection {
	private final Map<TalentId, Talent> talentById;

	public static final Talents EMPTY = new Talents(Map.of());

	public Collection<Talent> getList() {
		return talentById.values();
	}

	public boolean hasTalent(TalentId talentId) {
		return talentById.containsKey(talentId);
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		collector.addAttributes(talentById.values());
	}
}
