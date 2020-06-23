package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.ServiceAlterationEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "dated_service_journeys")
@NoArgsConstructor
@ToString(callSuper = true)
public class DatedServiceJourney extends NeptuneIdentifiedObject {

    private static final long serialVersionUID = 304336286208135064L;

    @Getter
    @Setter
    @GenericGenerator(name = "dated_service_journeys_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "dated_service_journeys_id_seq"),
            @Parameter(name = "increment_size", value = "100")})
    @GeneratedValue(generator = "dated_service_journeys_id_seq")
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

	/**
	 *  Service journey.
	 */
	@Getter
	@Setter
	@Column(name = "service_journey")
	private VehicleJourney serviceJourney;

	/**
	 *  Service journey.
	 */
	@Getter
	@Setter
	@Column(name = "derived_from_service_journey")
	private VehicleJourney derivedFromServiceJourney;

	/**
	 * Operating day
	 */
	@Getter
	@Setter
	@Column(name = "operating_day")
	private LocalDate operatingDay;

    /**
     * Service alteration.
     *
     */
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "service_alteration")
    private ServiceAlterationEnum serviceAlteration;




}
