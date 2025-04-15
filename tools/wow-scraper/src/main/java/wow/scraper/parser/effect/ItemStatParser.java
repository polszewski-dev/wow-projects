package wow.scraper.parser.effect;

import lombok.RequiredArgsConstructor;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.CollectionUtil;
import wow.scraper.parser.stat.StatParser;
import wow.scraper.repository.ItemSpellRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
@RequiredArgsConstructor
public class ItemStatParser {
	private final GameVersionId gameVersion;
	private final Function<GameVersionId, StatParser> statParserFactory;
	private final ItemSpellRepository itemSpellRepository;
	private final List<Effect> itemEffects = new ArrayList<>();

	public List<Effect> getItemEffects() {
		return itemEffects;
	}

	public Optional<Effect> getUniqueItemEffect() {
		return CollectionUtil.getUniqueResult(itemEffects);
	}

	public boolean hasAnyEffects() {
		return !itemEffects.isEmpty();
	}

	public boolean tryParseItemEffect(String line) {
		var permanentEffect = parsePermanentEffect(line);

		if (permanentEffect != null) {
			itemEffects.add(permanentEffect);
			return true;
		}

		var itemAttributes = parseItemAttributes(line);

		if (itemAttributes != null) {
			itemEffects.add(itemAttributes);
			return true;
		}

		return false;
	}

	public void parseItemEffect(String line) {
		if (!tryParseItemEffect(line)) {
			throw new IllegalArgumentException("Could not parse: " + line);
		}
	}

	private Effect parsePermanentEffect(String line) {
		var effect = itemSpellRepository.getItemEffect(gameVersion, line);

		return effect.orElse(null);
	}

	private Effect parseItemAttributes(String line) {
		var statParser = statParserFactory.apply(gameVersion);

		if (!statParser.tryParse(line)) {
			return null;
		}

		return EffectImpl.newAttributeEffect(statParser.getParsedStats(), line);
	}
}
