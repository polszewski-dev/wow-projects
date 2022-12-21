package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.PetType;
import wow.commons.model.spells.Spell;
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
	private Talents talents;
	private PveRole role;
	private Spell damagingSpell;
	private List<Spell> relevantSpells;
	private PetType activePet;
	private BuffSets buffSets;

	public Build() {
		reset();
	}

	@Override
	public Build copy() {
		return new Build(
				buildId,
				talentLink,
				talents,
				role,
				damagingSpell,
				relevantSpells,
				activePet,
				buffSets
		);
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		talents.collectAttributes(collector);
	}

	public boolean hasTalent(TalentId talentId) {
		return talents.hasTalent(talentId);
	}

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return buffSets.getBuffSet(buffSetId);
	}

	public void reset() {
		this.buildId = null;
		this.talentLink = null;
		this.talents = Talents.EMPTY;
		this.role = null;
		this.damagingSpell = null;
		this.relevantSpells = List.of();
		this.activePet = null;
		this.buffSets = BuffSets.EMPTY;
	}
}
