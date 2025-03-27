package wow.commons.repository.impl.item;

import org.springframework.stereotype.Component;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.EnchantExcelParser;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
public class EnchantRepositoryImpl implements EnchantRepository {
	private final PhaseMap<Integer, Enchant> enchantById = new PhaseMap<>();
	private final PhaseMap<String, List<Enchant>> enchantByName = new PhaseMap<>();

	public EnchantRepositoryImpl(EnchantExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getEnchants().forEach(this::addEnchant);
	}

	@Override
	public Optional<Enchant> getEnchant(int enchantId, PhaseId phaseId) {
		return enchantById.getOptional(phaseId, enchantId);
	}

	@Override
	public Optional<Enchant> getEnchant(String name, PhaseId phaseId) {
		return enchantByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Enchant> getEnchants(ItemType itemType, ItemSubType itemSubType, PhaseId phaseId) {
		return enchantByName.values(phaseId).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableDuring(phaseId))
				.filter(x -> x.matches(itemType, itemSubType))
				.toList();
	}

	private void addEnchant(Enchant enchant) {
		putForEveryPhase(enchantById, enchant.getId(), enchant);
		addEntryForEveryPhase(enchantByName, enchant.getName(), enchant);
	}
}
