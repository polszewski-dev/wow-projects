package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.Copyable;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Pet;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.PetType;
import wow.commons.model.talents.TalentId;

import static wow.commons.model.character.PetType.NONE;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@AllArgsConstructor
@Getter
@Setter
public class Build implements AttributeCollection, Copyable<Build> {
	private final GameVersion gameVersion;
	private final Talents talents;
	private PveRole role;
	private Rotation rotation;
	private Pet activePet;

	public Build(GameVersion gameVersion, Talents talents) {
		this.gameVersion = gameVersion;
		this.talents = talents;
		reset();
	}

	@Override
	public Build copy() {
		return new Build(
				gameVersion,
				talents.copy(),
				role,
				rotation.copy(),
				activePet
		);
	}

	public void reset() {
		this.talents.reset();
		this.role = null;
		this.rotation = null;
		setActivePet(NONE);
	}

	public void setActivePet(PetType petType) {
		if (petType == null) {
			throw new IllegalArgumentException();
		}
		this.activePet = gameVersion.getPet(petType);
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		talents.collectAttributes(collector);
	}

	public boolean hasTalent(TalentId talentId) {
		return talents.hasTalent(talentId);
	}
}
