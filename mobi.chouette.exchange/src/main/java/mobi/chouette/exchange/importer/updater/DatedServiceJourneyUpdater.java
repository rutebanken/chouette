package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.FootnoteDAO;
import mobi.chouette.dao.InterchangeDAO;
import mobi.chouette.dao.JourneyFrequencyDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimebandDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyAtStopDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Company;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.beanutils.BeanUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Stateless(name = DatedServiceJourneyUpdater.BEAN_NAME)
public class DatedServiceJourneyUpdater implements Updater<DatedServiceJourney> {

	public static final String BEAN_NAME = "DatedServiceJourneyUpdater";


	@Override
	public void update(Context context, DatedServiceJourney oldValue, DatedServiceJourney newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
		cache.getDatedServiceJourneys().put(oldValue.getObjectId(), oldValue);

		if (oldValue.isDetached()) {
			// object does not exist in database
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setServiceAlteration(newValue.getServiceAlteration());
			oldValue.setOperatingDay(newValue.getOperatingDay());
			oldValue.setDetached(false);
		} else {
			if (newValue.getObjectId() != null && !newValue.getObjectId().equals(oldValue.getObjectId())) {
				oldValue.setObjectId(newValue.getObjectId());
			}
			if (newValue.getObjectVersion() != null && !newValue.getObjectVersion().equals(oldValue.getObjectVersion())) {
				oldValue.setObjectVersion(newValue.getObjectVersion());
			}
			if (newValue.getCreationTime() != null && !newValue.getCreationTime().equals(oldValue.getCreationTime())) {
				oldValue.setCreationTime(newValue.getCreationTime());
			}
			if (newValue.getCreatorId() != null && !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
				oldValue.setCreatorId(newValue.getCreatorId());
			}

			if (newValue.getServiceAlteration() != null && !newValue.getServiceAlteration().equals(oldValue.getServiceAlteration())) {
				oldValue.setServiceAlteration(newValue.getServiceAlteration());
			}

			if (newValue.getOperatingDay() != null
					&& !newValue.getOperatingDay().equals(oldValue.getOperatingDay())) {
				oldValue.setOperatingDay(newValue.getOperatingDay());
			}

			if (newValue.getVehicleJourney() != null) {
				oldValue.setVehicleJourney(newValue.getVehicleJourney());
			}
			if (newValue.getDerivedFromDatedServiceJourney() != null) {
				oldValue.setDerivedFromDatedServiceJourney(newValue.getDerivedFromDatedServiceJourney());
			}

		}

	}
}
