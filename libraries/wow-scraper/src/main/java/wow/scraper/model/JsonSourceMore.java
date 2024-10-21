package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({
		"icon",
})
public class JsonSourceMore {
	/**
	 * "c' identifies a spell sub-category (ie browse by database - spells, class skills, talents, professions, .."
	 */

	@JsonProperty(value = "c")
	private Integer c;

	/**
	 * "s" and "c2" can identify the sub category of the item's source (something in the form wowhead.com/?category=c.c2 so category="spells", c="11", c2="164" gives blacksmithing spells )
	 */

	@JsonProperty(value = "c2")
	private Integer c2;

	@JsonProperty(value = "n")
	private String n;

	@JsonProperty(value = "t")
	private Integer t;

	/**
	 * "ti" gives the id of the spell or quest the item is created by or a reward for.
	 */

	@JsonProperty(value = "ti")
	private Integer ti;

	@JsonProperty(value = "bd")
	private Integer bd;

	@JsonProperty(value = "z")
	private Integer z;

	@JsonProperty(value = "p")
	private Integer p;

	@JsonProperty(value = "s")
	private Integer s;

	@JsonProperty(value = "dd")
	private Integer dd;

	@JsonProperty(value = "q")
	private Integer q;


	@Override
	public String toString() {
		return Stream.of(
				c!=null ? ("c=" + c) : "",
				c2!=null ? ("c2=" + c2) : "",
				n!=null ? ("n='" + n + '\'') : "",
				t!=null ? ("t=" + t) : "",
				ti!=null ? ("ti=" + ti) : "",
				bd!=null ? ("bd=" + bd) : "",
				z!=null ? ("z=" + z) : "",
				p!=null ? ("p=" + p) : "",
				s!=null ? ("s=" + s) : "",
				dd!=null ? ("dd=" + dd) : "",
				q!=null ? ("q=" + q) : ""
			)
				.filter(x -> !x.isBlank())
				.collect(Collectors.joining(", ", "{", "}"));
	}
}
