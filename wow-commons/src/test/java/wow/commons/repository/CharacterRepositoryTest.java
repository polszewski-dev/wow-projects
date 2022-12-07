package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.BuildTemplate;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.PveRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.BuffSetId.*;
import static wow.commons.model.character.BuildId.DESTRO_SHADOW;
import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.character.Race.ORC;
import static wow.commons.model.spells.SpellId.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
class CharacterRepositoryTest extends RepositoryTest {
	@Autowired
	CharacterRepository underTest;

	@Test
	@DisplayName("BaseStatInfo is read correctly")
	void baseStatInfoIsCorrect() {
		Optional<BaseStatInfo> optionalBaseStatInfo = underTest.getBaseStats(WARLOCK, ORC, 70);

		assertThat(optionalBaseStatInfo).isPresent();

		BaseStatInfo baseStatInfo = optionalBaseStatInfo.orElseThrow();

		assertThat(baseStatInfo.getLevel()).isEqualTo(70);
		assertThat(baseStatInfo.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(baseStatInfo.getRace()).isEqualTo(ORC);
		assertThat(baseStatInfo.getBaseStrength()).isEqualTo(48);
		assertThat(baseStatInfo.getBaseAgility()).isEqualTo(47);
		assertThat(baseStatInfo.getBaseStamina()).isEqualTo(78);
		assertThat(baseStatInfo.getBaseIntellect()).isEqualTo(130);
		assertThat(baseStatInfo.getBaseSpirit()).isEqualTo(114);
		assertThat(baseStatInfo.getBaseHP()).isEqualTo(1964);
		assertThat(baseStatInfo.getBaseMana()).isEqualTo(2698);
		assertThat(baseStatInfo.getBaseSpellCritPct().getValue()).isEqualTo(3.29, PRECISION);
		assertThat(baseStatInfo.getIntellectPerCritPct()).isEqualTo(80, PRECISION);
	}

	@Test
	@DisplayName("CombatRatingInfo is read correctly")
	void combatRatingInfoIsCorrect() {
		Optional<CombatRatingInfo> optionalCombatRatingInfo = underTest.getCombatRatings(70);

		assertThat(optionalCombatRatingInfo).isPresent();

		CombatRatingInfo combatRatingInfo = optionalCombatRatingInfo.orElseThrow();

		assertThat(combatRatingInfo.getLevel()).isEqualTo(70);
		assertThat(combatRatingInfo.getSpellCrit()).isEqualTo(22.22, PRECISION);
		assertThat(combatRatingInfo.getSpellHit()).isEqualTo(12.62, PRECISION);
		assertThat(combatRatingInfo.getSpellHaste()).isEqualTo(15.76, PRECISION);
	}

	@Test
	@DisplayName("BuildTemplate is read correctly")
	void buildTemplateIsCorrect() {
		Optional<BuildTemplate> optionalBuildTemplate = underTest.getBuildTemplate(DESTRO_SHADOW, WARLOCK, 70);

		assertThat(optionalBuildTemplate).isPresent();

		BuildTemplate buildTemplate = optionalBuildTemplate.orElseThrow();

		assertThat(buildTemplate.getBuildId()).isEqualTo(DESTRO_SHADOW);
		assertThat(buildTemplate.getLevel()).isEqualTo(70);
		assertThat(buildTemplate.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(buildTemplate.getTalentLink()).isEqualTo("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250");
		assertThat(buildTemplate.getRole()).isEqualTo(PveRole.CASTER_DPS);
		assertThat(buildTemplate.getDamagingSpell()).isEqualTo(SHADOW_BOLT);
		assertThat(buildTemplate.getRelevantSpells()).hasSameElementsAs(List.of(
				SHADOW_BOLT, CURSE_OF_DOOM, CURSE_OF_AGONY, CORRUPTION, IMMOLATE,
				UNSTABLE_AFFLICTION, SIPHON_LIFE, SEED_OF_CORRUPTION_DIRECT, DRAIN_LIFE,
				CONFLAGRATE, INCINERATE, SEARING_PAIN, DEATH_COIL, HELLFIRE, RAIN_OF_FIRE
		));
		assertThat(buildTemplate.getActivePet()).isNull();
		assertThat(buildTemplate.getBuffSets()).isEqualTo(Map.of(
				SELF_BUFFS, List.of("Fel Armor", "Touch of Shadow"),
				PARTY_BUFFS, List.of("Arcane Brilliance", "Gift of the Wild", "Greater Blessing of Kings"),
				RAID_BUFFS, List.of("Curse of the Elements", "Wrath of Air Totem", "Totem of Wrath"),
				WORLD_BUFFS, List.of(),
				CONSUMES, List.of("Well Fed (sp)", "Brilliant Wizard Oil", "Flask of Pure Death")
		));
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
