package mobi.chouette.exchange.regtopp.importer.version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.importer.parser.v12novus.RegtoppConnectionLinkParser;
import mobi.chouette.exchange.regtopp.importer.parser.v12novus.RegtoppRouteParser;
import mobi.chouette.exchange.regtopp.importer.parser.v12novus.RegtoppStopParser;
import mobi.chouette.exchange.regtopp.model.v12novus.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12novus.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;

public class Regtopp12NovusVersionHandler extends Regtopp12VersionHandler {

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension) {

		switch (extension) {
		case "HPL": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), RegtoppException.ERROR.HPL_INVALID_FIELD_VALUE);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile);
			break;
		}
		case "TMS": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTMS.class }), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_INDEX.name(), parseableFile);
			break;
		}

		default:
			super.registerFileForIndex(importer, fileName, extension);
		}
	}

	@Override
	public Parser createStopParser() throws ClassNotFoundException, IOException {
		return (RegtoppStopParser) ParserFactory.create(RegtoppStopParser.class.getName());
	}

	@Override
	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException {
		return (RegtoppRouteParser) ParserFactory.create(RegtoppRouteParser.class.getName());
	}

	@Override
	public Parser createConnectionLinkParser() throws ClassNotFoundException, IOException {
		return (RegtoppConnectionLinkParser) ParserFactory.create(RegtoppConnectionLinkParser.class.getName());
	}
}
