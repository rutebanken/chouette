package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Block;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import org.rutebanken.netex.model.Block_VersionStructure;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypeRefs_RelStructure;
import org.rutebanken.netex.model.JourneyRefs_RelStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

import javax.xml.bind.JAXBElement;

public class BlockProducer extends NetexProducer {

    public org.rutebanken.netex.model.Block produce(Context context, Block block, Line line) {
        ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

        org.rutebanken.netex.model.Block netexBlock = netexFactory.createBlock();
        NetexProducerUtils.populateId(block, netexBlock);

        // private code
        PrivateCodeStructure privateCodeStructure = netexFactory.createPrivateCodeStructure();
        privateCodeStructure.setValue(block.getPrivateCode());
        netexBlock.setPrivateCode(privateCodeStructure);

        // timetables
        if (!block.getTimetables().isEmpty()) {
            Block_VersionStructure.DayTypes daytypes = new Block_VersionStructure.DayTypes();
            netexBlock.setDayTypes(daytypes);
            for (Timetable t : block.getTimetables()) {
                if (exportableData.getTimetables().contains(t)) {
                    DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
                    NetexProducerUtils.populateReference(t, dayTypeRefStruct, true);
                    JAXBElement<? extends DayTypeRefStructure> dayTypeRef = netexFactory.createDayTypeRef(dayTypeRefStruct);
                    netexBlock.getDayTypes().withDayTypeRef(dayTypeRef);
                }
            }
        }

        // vehicle journeys
        JourneyRefs_RelStructure journeyRefs_relStructure = netexFactory.createJourneyRefs_RelStructure();
        netexBlock.setJourneys(journeyRefs_relStructure);
        for (VehicleJourney vehicleJourney : block.getVehicleJourneys()) {
            VehicleJourneyRefStructure vehicleJourneyRefStructure = netexFactory.createVehicleJourneyRefStructure();
            vehicleJourneyRefStructure.setRef(vehicleJourney.getObjectId());
            NetexProducerUtils.populateReference(vehicleJourney, vehicleJourneyRefStructure, false);
            JAXBElement<?> vehicleJourneyRef = netexFactory.createVehicleJourneyRef(vehicleJourneyRefStructure);
            netexBlock.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator().add(vehicleJourneyRef);
        }

        return netexBlock;

    }
}
