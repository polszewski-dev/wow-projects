package wow.scraper.model;

import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2023-06-21
 */
public interface HasRequiredVersion {
	GameVersionId getReqVersion();

	void setReqVersion(GameVersionId reqVersion);
}
