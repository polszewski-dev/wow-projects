package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.PetType;
import wow.commons.model.talents.TalentId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@AllArgsConstructor
@Getter
@Setter
public class Build implements AttributeCollection, Copyable<Build> {
	private BuildId buildId;
	private String talentLink;
	private final Talents talents;
	private PveRole role;
	private Rotation rotation;
	private PetType activePet;
	private BuffSets buffSets;

	public Build(Talents talents) {
		this.talents = talents;
		reset();
	}

	@Override
	public Build copy() {
		return new Build(
				buildId,
				talentLink,
				talents.copy(),
				role,
				rotation,
				activePet,
				buffSets
		);
	}

	public void reset() {
		this.buildId = null;
		this.talentLink = null;
		this.role = null;
		this.rotation = null;
		this.activePet = null;
		this.buffSets = BuffSets.EMPTY;
		this.talents.reset();
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		talents.collectAttributes(collector);
	}

	public boolean hasTalent(TalentId talentId) {
		return talents.hasTalent(talentId);
	}

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return buffSets.getBuffSet(buffSetId);
	}
}
