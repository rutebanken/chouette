package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Presentation
 */
@Entity
@Table(name = "presentations")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Presentation extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -6223882293110225313L;

	@Getter
	@Setter
	@GenericGenerator(name = "presentation_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "presentation_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "presentation_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * colour
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "colour")
	private byte[] colour;

	/**
	 * textColour
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "textColour")
	private byte[] textColour;

	/**
	 * textFont
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "textFont")
	private String textFont;

}
