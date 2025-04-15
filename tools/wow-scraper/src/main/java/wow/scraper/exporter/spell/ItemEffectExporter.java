package wow.scraper.exporter.spell;

import wow.commons.model.effect.Effect;
import wow.commons.model.spell.Spell;
import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;
import wow.scraper.util.SpellTraverser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-09-02
 */
public class ItemEffectExporter extends MultiPageSpellBaseExporter {
	private final List<Spell> spells = new ArrayList<>();
	private final List<Effect> effects = new ArrayList<>();

	@Override
	protected void prepareData() {
		getItemSpellRepository().getActivatedAbilities()
				.forEach(spell -> getTraverser().traverse(spell));
		getItemSpellRepository().getItemEffects()
				.forEach(effect -> getTraverser().traverse(effect));

		spells.sort(getSpellComparator());
		effects.sort(getEffectComparator());
	}

	@Override
	protected void exportPreparedData(SpellBaseExcelBuilder builder) {
		exportItemSpells(builder);
		exportItemEffects(builder);
	}

	private void exportItemSpells(SpellBaseExcelBuilder builder) {
		builder.addItemSpellHeader();
		spells.forEach(builder::addItemSpell);
	}

	private void exportItemEffects(SpellBaseExcelBuilder builder) {
		builder.addItemEffectHeader();
		effects.forEach(builder::addItemEffect);
	}

	private SpellTraverser getTraverser() {
		return new SpellTraverser()
				.setSpellHandler((spell, level, index) -> spells.add(spell))
				.setEffectHandler(((effect, level, index) -> effects.add(effect)));
	}

	private static Comparator<Effect> getEffectComparator() {
		return Comparator
				.comparing(Effect::getName)
				.thenComparing(Effect::getEffectId)
				.thenComparing(Effect::getGameVersionId);
	}

	private static Comparator<Spell> getSpellComparator() {
		return Comparator
				.comparing(Spell::getName)
				.thenComparing(Spell::getId)
				.thenComparing(Spell::getGameVersionId);
	}
}
