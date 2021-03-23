package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.NetexTimeConversionUtil;
import mobi.chouette.model.BookingArrangement;
import mobi.chouette.model.Company;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.DeadRun;
import org.rutebanken.netex.model.FlexibleServiceProperties;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.Journey_VersionStructure;
import org.rutebanken.netex.model.JourneysInFrame_RelStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.TimetabledPassingTime;

import javax.xml.bind.JAXBElement;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j
public class DeadRunParser extends NetexParser implements Parser, Constant {

	private KeyValueParser keyValueParser = new KeyValueParser();

	private ContactStructureParser contactStructureParser = new ContactStructureParser();

	@Override
	@SuppressWarnings("unchecked")
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		JourneysInFrame_RelStructure journeyStructs = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
		List<Journey_VersionStructure> deadRuns = journeyStructs.getVehicleJourneyOrDatedVehicleJourneyOrNormalDatedVehicleJourney();

		for (Journey_VersionStructure journeyStruct : deadRuns) {
			if (! (journeyStruct instanceof DeadRun)) {
				if(log.isTraceEnabled()) {
					log.trace("Ignoring non-DeadRun with id: " + journeyStruct.getId());
				}
				continue;
			}
			DeadRun deadRun = (DeadRun) journeyStruct;

			mobi.chouette.model.DeadRun chouetteDeadRun = ObjectFactory.getDeadRun(referential, deadRun.getId());

			if (chouetteDeadRun.isFilled()) {
				mobi.chouette.model.DeadRun chouetteDeadRunWithVersion = ObjectFactory.getDeadRun(referential,
						deadRun.getId() + "_" + deadRun.getVersion());
				log.warn("Already parsed " + chouetteDeadRun.getObjectId() + ", will use version field as part of id to separate them: "
						+ chouetteDeadRunWithVersion.getObjectId());
				chouetteDeadRun = chouetteDeadRunWithVersion;
			}

			DayTypeRefs_RelStructure dayTypes = deadRun.getDayTypes();
			if (dayTypes != null) {
				for (JAXBElement<? extends DayTypeRefStructure> dayType : dayTypes.getDayTypeRef()) {
					String timetableId = dayType.getValue().getRef();
					Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
					timetable.addDeadRun(chouetteDeadRun);
				}
			}

			chouetteDeadRun.setObjectVersion(NetexParserUtils.getVersion(deadRun));

			chouetteDeadRun.setFilled(true);

		}
	}

	private void parseTimetabledPassingTimes(Context context, Referential referential, ServiceJourney deadRun, VehicleJourney chouetteDeadRun) {

		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);


		for (int i = 0; i < deadRun.getPassingTimes().getTimetabledPassingTime().size(); i++) {
			TimetabledPassingTime passingTime = deadRun.getPassingTimes().getTimetabledPassingTime().get(i);
			String passingTimeId = passingTime.getId();

			if (passingTimeId == null) {
				// TODO profile should prevent this from happening, creating bogus
				passingTimeId = NetexParserUtils.netexId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLE_JOURNEY_AT_STOP_KEY, UUID.randomUUID().toString());
			}
			VehicleJourneyAtStop chouetteDeadRunAtStop = ObjectFactory.getVehicleJourneyAtStop(referential, passingTimeId);
			chouetteDeadRunAtStop.setObjectVersion(NetexParserUtils.getVersion(passingTime));

			StopPoint stopPoint = ObjectFactory.getStopPoint(referential, passingTime.getPointInJourneyPatternRef().getValue().getRef());
			chouetteDeadRunAtStop.setStopPoint(stopPoint);

			parsePassingTimes(passingTime, chouetteDeadRunAtStop);
			chouetteDeadRunAtStop.setVehicleJourney(chouetteDeadRun);
		}

		chouetteDeadRun.getVehicleJourneyAtStops().sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));
	}

	// TODO add support for other time zones and zone offsets, for now only handling UTC
	private void parsePassingTimes(TimetabledPassingTime timetabledPassingTime, VehicleJourneyAtStop chouetteDeadRunAtStop) {

		NetexTimeConversionUtil.parsePassingTime(timetabledPassingTime, false, chouetteDeadRunAtStop);
		NetexTimeConversionUtil.parsePassingTime(timetabledPassingTime, true, chouetteDeadRunAtStop);

		// TODO copying missing data since Chouette pt does not properly support missing values
		if (chouetteDeadRunAtStop.getArrivalTime() == null && chouetteDeadRunAtStop.getDepartureTime() != null) {
			chouetteDeadRunAtStop.setArrivalTime(chouetteDeadRunAtStop.getDepartureTime());
			chouetteDeadRunAtStop.setArrivalDayOffset(chouetteDeadRunAtStop.getDepartureDayOffset());
		} else if (chouetteDeadRunAtStop.getArrivalTime() != null && chouetteDeadRunAtStop.getDepartureTime() == null) {
			chouetteDeadRunAtStop.setDepartureTime(chouetteDeadRunAtStop.getArrivalTime());
			chouetteDeadRunAtStop.setDepartureDayOffset(chouetteDeadRunAtStop.getArrivalDayOffset());
		}

	}

	static {
		ParserFactory.register(DeadRunParser.class.getName(), new ParserFactory() {
			private DeadRunParser instance = new DeadRunParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
