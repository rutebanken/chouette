package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.NetexTimeConversionUtil;
import mobi.chouette.model.BookingArrangement;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.FlexibleServiceProperties;
import org.rutebanken.netex.model.JourneyPatternRefStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingDayRefStructure;
import org.rutebanken.netex.model.OperatorRefStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.ServiceAlterationEnumeration;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPatternRefStructure;
import org.rutebanken.netex.model.TimetabledPassingTime;
import org.rutebanken.netex.model.TimetabledPassingTimes_RelStructure;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DatedServiceJourneyProducer extends NetexProducer {

	public org.rutebanken.netex.model.DatedServiceJourney produce(Context context, DatedServiceJourney datedServiceJourney, Line line) {
        ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

		org.rutebanken.netex.model.DatedServiceJourney netexDatedServiceJourney = netexFactory.createDatedServiceJourney();
		NetexProducerUtils.populateId(datedServiceJourney, netexDatedServiceJourney);


		// operating day
		LocalDate operatingDay = datedServiceJourney.getOperatingDay();
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);
		String operatingDayId = NetexProducerUtils.netexId(configuration.getDefaultCodespacePrefix(),"OperatingDay",operatingDay.toString());
		OperatingDayRefStructure operatingDayRefStructure = netexFactory.createOperatingDayRefStructure();
		operatingDayRefStructure.withRef(operatingDayId);
		netexDatedServiceJourney.setOperatingDayRef(operatingDayRefStructure);
		if (!exportableNetexData.getSharedOperatingDays().containsKey(operatingDayId)) {
			OperatingDay netexOperatingDay= netexFactory.createOperatingDay();
			netexOperatingDay.setVersion("0");
			netexOperatingDay.setId(operatingDayId);
			netexOperatingDay.setCalendarDate(TimeUtil.toLocalDateFromJoda(operatingDay).atStartOfDay());
			exportableNetexData.getSharedOperatingDays().put(netexOperatingDay.getId(), netexOperatingDay);
		}

		// service journey
		//ServiceJourneyRefStructure serviceJourneyRefStructure =  netexFactory.createServiceJourneyRefStructure();
		//netexDatedServiceJourney.setServiceJourneyRef(serviceJourneyRefStructure);

		// derived from dated service journey
		if(datedServiceJourney.getDerivedFromDatedServiceJourney() != null) {
			netexDatedServiceJourney.setDerivedFromObjectRef(datedServiceJourney.getDerivedFromDatedServiceJourney().getObjectId());
		}

		// service alteration
		if(datedServiceJourney.getServiceAlteration() != null) {
			netexDatedServiceJourney.setServiceAlteration(ConversionUtil.toServiceAlterationEnumeration(datedServiceJourney.getServiceAlteration()));
		}




		return netexDatedServiceJourney;

	}
}
