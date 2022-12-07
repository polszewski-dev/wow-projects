package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-06
 */
public abstract class RankedElementSheetParser<K, E extends TimeRestricted> extends WowExcelSheetParser {
	protected final Map<K, List<E>> elementInfoById;

	protected RankedElementSheetParser(String sheetName, Map<K, List<E>> elementInfoById) {
		super(sheetName);
		this.elementInfoById = elementInfoById;
	}

	protected void addElement(K elementId, E element) {
		assertNoRecordWithSimilarVersionRestrictionExist(elementId, element.getTimeRestriction());
		elementInfoById.computeIfAbsent(elementId, x -> new ArrayList<>()).add(element);
	}

	protected List<E> getCorrespondingElements(K elementId) {
		TimeRestriction timeRestriction = getTimeRestriction();

		return elementInfoById.getOrDefault(elementId, List.of()).stream()
				.filter(x -> hasCommonVersionRestriction(x, timeRestriction))
				.collect(Collectors.toList());
	}

	private void assertNoRecordWithSimilarVersionRestrictionExist(K elementId, TimeRestriction timeRestriction) {
		boolean match = elementInfoById.getOrDefault(elementId, List.of()).stream()
				.anyMatch(x -> hasCommonVersionRestriction(x, timeRestriction));

		if (match) {
			throw new IllegalArgumentException("Duplicate restriction for: " + elementId);
		}
	}

	private boolean hasCommonVersionRestriction(TimeRestricted first, TimeRestriction second) {
		return CollectionUtil.hasCommonCriteria(first.getTimeRestriction().getVersions(), second.getVersions());
	}
}
