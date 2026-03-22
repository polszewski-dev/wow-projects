package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import wow.character.model.character.OptionStatus;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.OptionGroupDTO;
import wow.minmax.client.dto.OptionStatusDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-19
 */
@AllArgsConstructor
public abstract class OptionStatusConverter<F, T> implements Converter<OptionStatus<F>, OptionStatusDTO<T>> {
	private final Converter<F, T> converter;

	@Override
	public OptionStatusDTO<T> doConvert(OptionStatus<F> source) {
		return new OptionStatusDTO<>(
				converter.convert(source.option()),
				source.enabled()
		);
	}

	public List<OptionGroupDTO<T>> convertAndGroup(List<OptionStatus<F>> optionStatuses) {
		var groups = new LinkedHashMap<String, List<OptionStatusDTO<T>>>();

		for (var optionStatus : optionStatuses) {
			var groupKey = getGroupKey(optionStatus.option());
			var dto = convert(optionStatus);

			groups.computeIfAbsent(groupKey, x -> new ArrayList<>()).add(dto);
		}

		return groups.entrySet().stream()
				.map(x -> new OptionGroupDTO<>(x.getKey(), x.getValue()))
				.toList();
	}

	protected abstract String getGroupKey(F source);
}
