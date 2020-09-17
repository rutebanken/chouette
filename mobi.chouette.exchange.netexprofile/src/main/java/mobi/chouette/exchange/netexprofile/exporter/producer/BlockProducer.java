package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Block;
import mobi.chouette.model.Line;
import mobi.chouette.model.VehicleJourney;
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

        // vehicle journeys
        for (VehicleJourney vehicleJourney : block.getVehicleJourneys()) {
            VehicleJourneyRefStructure vehicleJourneyRefStructure = netexFactory.createVehicleJourneyRefStructure();
            vehicleJourneyRefStructure.setRef(vehicleJourney.getObjectId());
            NetexProducerUtils.populateReference(vehicleJourney, vehicleJourneyRefStructure, true);
            JAXBElement<?> vehicleJourneyRef = netexFactory.createVehicleJourneyRef(vehicleJourneyRefStructure);
            netexBlock.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator().add(vehicleJourneyRef);
        }

        return netexBlock;

    }
}
