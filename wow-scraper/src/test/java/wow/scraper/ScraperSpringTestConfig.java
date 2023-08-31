package wow.scraper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import wow.commons.model.config.Description;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.impl.ActivatedAbilityImpl;
import wow.scraper.fetcher.PageCache;
import wow.scraper.fetcher.PageFetcher;
import wow.scraper.repository.ItemSpellRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
@ComponentScan(basePackages = {
		"wow.scraper"
})
@PropertySource("classpath:test.properties")
public class ScraperSpringTestConfig {
	@Bean
	public PageFetcher pageFetcher(PageCache pageCache) {
		return urlStr -> null;
	}

	@Bean
	public ItemSpellRepository itemSpellRepository() {
		return new ItemSpellRepository() {
			final Set<String> mockSpells = Set.of(
					"Use: Increases spell damage by up to 150 and healing by up to 280 for 15 sec. (1 Min, 30 Sec Cooldown)"
			);
			final Set<String> mockEffects = Set.of(
					"Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health."
			);

			@Override
			public List<ActivatedAbility> getActivatedAbilities() {
				return List.of();
			}

			@Override
			public List<Effect> getItemEffects() {
				return List.of();
			}

			@Override
			public Optional<ActivatedAbility> getActivatedAbility(GameVersionId gameVersion, String tooltip) {
				if (mockSpells.contains(tooltip)) {
					var activatedAbility = new ActivatedAbilityImpl();
					activatedAbility.setDescription(new Description("", null, tooltip));
					return Optional.of(activatedAbility);
				}
				return Optional.empty();
			}

			@Override
			public Optional<Effect> getItemEffect(GameVersionId gameVersion, String tooltip) {
				if (mockEffects.contains(tooltip)) {
					EffectImpl effect = new EffectImpl(null);
					effect.setDescription(new Description("", null, tooltip));
					return Optional.of(effect);
				}
				return Optional.empty();
			}
		};
	}
}
