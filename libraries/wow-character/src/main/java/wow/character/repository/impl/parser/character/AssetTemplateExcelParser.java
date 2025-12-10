package wow.character.repository.impl.parser.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.asset.AssetOption;
import wow.character.model.asset.AssetTemplate;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static wow.character.model.asset.AssetOption.OneOfManyOption;
import static wow.character.model.asset.AssetOption.SingleOption;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class AssetTemplateExcelParser extends ExcelParser {
	@Value("${asset.templates.xls.file.path}")
	private final String xlsFilePath;

	private final List<AssetTemplate> assetTemplates = new ArrayList<>();
	private final List<AssetOptionAndRestriction> assetTemplateOptions = new ArrayList<>();

	private record AssetOptionAndRestriction(SingleOption assetOption, CharacterClassId characterClassId, GameVersionId gameVersionId, List<String> specificToAssets) {}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new AssetTemplateSheetParser("templates", this),
				new AssetTemplateOptionSheetParser("options", this)
		);
	}

	void addAssetTemplate(AssetTemplate assetTemplate) {
		assetTemplates.add(assetTemplate);
	}

	void addAssetTemplateOption(SingleOption assetTemplateOption, CharacterClassId characterClassId, GameVersionId gameVersionId, List<String> specificToAssets) {
		var optionAndRestriction = new AssetOptionAndRestriction(assetTemplateOption, characterClassId, gameVersionId, specificToAssets);

		assetTemplateOptions.add(optionAndRestriction);
	}

	public List<AssetTemplate> getAssetTemplates() {
		return assetTemplates.stream()
				.map(this::attachOptions)
				.toList();
	}

	private AssetTemplate attachOptions(AssetTemplate template) {
		return new AssetTemplate(
				template.name(),
				template.characterClassId(),
				template.gameVersionId(),
				getOptions(template)
		);
	}

	private List<AssetOption> getOptions(AssetTemplate template) {
		var filteredOptions = getSingleOptions(template);

		var groups = filteredOptions.stream()
				.collect(groupingBy(this::getGroupKey, LinkedHashMap::new, toList()));

		return groups.values().stream()
				.map(this::getOption)
				.toList();
	}

	private AssetOption getOption(List<SingleOption> group) {
		return group.size() == 1
				? group.getFirst()
				: new OneOfManyOption(group);
	}

	private String getGroupKey(SingleOption singleOption) {
		return singleOption.exclusionGroup() != null
				? singleOption.exclusionGroup().name()
				: singleOption.name();
	}

	private List<SingleOption> getSingleOptions(AssetTemplate template) {
		return assetTemplateOptions.stream()
				.filter(x -> x.gameVersionId == template.gameVersionId())
				.filter(x -> x.characterClassId == template.characterClassId())
				.filter(x -> x.specificToAssets.isEmpty() || x.specificToAssets.contains(template.name()))
				.map(x -> x.assetOption)
				.toList();
	}
}
