package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.ServiceAlterationEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "dated_service_journeys")
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"journeyPattern", "derivedFromDatedServiceJourney"})
public class DatedServiceJourney extends NeptuneIdentifiedObject {

    private static final long serialVersionUID = 8392587538821947318L;

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
     * Vehicle journey.
     */
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_journey_id")
    private VehicleJourney vehicleJourney;

    public void setVehicleJourney(VehicleJourney vehicleJourney) {
        if (this.vehicleJourney != null) {
            this.vehicleJourney.getDatedServiceJourneys().remove(this);
        }
        this.vehicleJourney = vehicleJourney;
        if (vehicleJourney != null) {
            vehicleJourney.getDatedServiceJourneys().add(this);
        }
    }

    /**
     * Original dated service journey.
     */
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "derived_from_id")
    private DatedServiceJourney derivedFromDatedServiceJourney;

    public void setDerivedFromDatedServiceJourney(DatedServiceJourney derivedFromDatedServiceJourney) {
        if (this.derivedFromDatedServiceJourney != null) {
            this.derivedFromDatedServiceJourney.getDerivedDatedServiceJourneys().remove(this);
        }
        this.derivedFromDatedServiceJourney = derivedFromDatedServiceJourney;
        if (derivedFromDatedServiceJourney != null) {
            derivedFromDatedServiceJourney.getDerivedDatedServiceJourneys().add(this);
        }
    }

    /**
     * Dated service journeys derived from this one.
     */

    @Getter
    @Setter
    @OneToMany(mappedBy = "derivedFromDatedServiceJourney")
    private List<DatedServiceJourney> derivedDatedServiceJourneys = new ArrayList<DatedServiceJourney>(
            0);


    /**
     * Operating day
     */
    @Getter
    @Setter
    @Column(name = "operating_day")
    private LocalDate operatingDay;

    /**
     * Service alteration.
     */
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "service_alteration")
    private ServiceAlterationEnum serviceAlteration;


    /**
     * Return true if the operating day is within the interval [startDate, endDate[ (startDate inclusive, endDate exclusive).
     */
    public boolean isValidOnPeriod(Date startDate, Date endDate) {
        LocalDate localStartDate = new LocalDate(startDate);
        LocalDate localEndDate = new LocalDate(endDate);
        return isValidOnPeriod(localStartDate, localEndDate);
    }

    /**
     * Return true if the operating day is within the interval [startDate, endDate[ (startDate inclusive, endDate exclusive).
     */
    public boolean isValidOnPeriod(LocalDate localStartDate, LocalDate localEndDate) {
        return ( (  operatingDay.isEqual(localStartDate) || operatingDay.isAfter(localStartDate) ) && operatingDay.isBefore(localEndDate));
    }

    /**
     * Return true if the DatedServiceJourney is neither cancelled nor replaced
     */
    public boolean isActive() {
        ServiceAlterationEnum serviceAlterationEnum = getServiceAlteration();
        return ServiceAlterationEnum.Cancellation != serviceAlterationEnum && ServiceAlterationEnum.Replaced != serviceAlterationEnum;
    }

}
