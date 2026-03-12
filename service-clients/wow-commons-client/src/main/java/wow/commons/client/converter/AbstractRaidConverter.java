package wow.commons.client.converter;

import lombok.RequiredArgsConstructor;
import wow.character.model.character.Party;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;
import wow.commons.client.dto.PartyDTO;
import wow.commons.client.dto.PlayerDTO;
import wow.commons.client.dto.RaidDTO;

/**
 * User: POlszewski
 * Date: 2026-03-11
 */
@RequiredArgsConstructor
public abstract class AbstractRaidConverter<P extends PlayerCharacter> implements Converter<Raid<P>, RaidDTO>, BackConverter<Raid<P>, RaidDTO> {
	private final AbstractPlayerConverter<P> playerConverter;

	@Override
	public RaidDTO doConvert(Raid<P> source) {
		var parties = source.getParties().stream()
				.map(this::getParty)
				.toList();

		return new RaidDTO(parties);
	}

	@Override
	public Raid<P> doConvertBack(RaidDTO source) {
		var raid = new Raid<P>();
		var partyDTOs = source.parties();

		for (int partyIdx = 0; partyIdx < partyDTOs.size(); partyIdx++) {
			var partyDTO = partyDTOs.get(partyIdx);
			var partyMembers = playerConverter.convertBackList(partyDTO.members());

			raid.getParty(partyIdx).add(partyMembers);
		}

		return raid;
	}

	private PartyDTO getParty(Party<P> party) {
		var players = party.getMembers().stream()
				.map(this::getPlayer)
				.toList();

		return new PartyDTO(players);
	}

	private PlayerDTO getPlayer(P player) {
		return playerConverter.convert(player);
	}
}
