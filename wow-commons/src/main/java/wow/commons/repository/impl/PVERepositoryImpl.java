package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Raid;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.parsers.PVEExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Repository
public class PVERepositoryImpl implements PVERepository {
	private final Map<String, Instance> instanceByName = new TreeMap<>();
	private final Map<String, Boss> bossByName = new TreeMap<>();
	private final Map<String, Faction> factionByName = new TreeMap<>();
	private final List<BaseStatInfo> baseStatInfos = new ArrayList<>();
	private final List<CombatRatingInfo> combatRatingInfos = new ArrayList<>();

	@Override
	public Instance getInstance(String name) {
		return instanceByName.get(name);
	}

	@Override
	public Boss getBoss(String name) {
		return bossByName.get(name);
	}

	@Override
	public Faction getFaction(String name) {
		return factionByName.get(name);
	}

	@Override
	public Collection<Instance> getAllInstances() {
		return instanceByName.values();
	}

	@Override
	public Collection<Raid> getAllRaids() {
		return getAllInstances().stream().filter(Instance::isRaid).map(Raid.class::cast).collect(Collectors.toList());
	}

	@Override
	public BaseStatInfo getBaseStats(CharacterClass characterClass, Race race, int level) {
		return baseStatInfos.stream()
				.filter(x -> x.getCharacterClass() == characterClass && x.getRace() == race && x.getLevel() == level)
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public CombatRatingInfo getCombatRatings(int level) {
		return combatRatingInfos.stream()
				.filter(x -> x.getLevel() == level)
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var pveExcelParser = new PVEExcelParser(this);
		pveExcelParser.readFromXls();

		for (Instance instance : instanceByName.values()) {
			instance.setBosses(new ArrayList<>());
		}

		for (Boss boss : bossByName.values()) {
			boss.getInstance().getBosses().add(boss);
		}
	}

	public void addInstanceByName(Instance instance) {
		instanceByName.put(instance.getName(), instance);
	}

	public void addBossByName(Boss boss) {
		bossByName.put(boss.getName(), boss);
	}

	public void addFactionByName(Faction faction) {
		factionByName.put(faction.getName(), faction);
	}

	public void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		baseStatInfos.add(baseStatInfo);
	}

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfos.add(combatRatingInfo);
	}
}
