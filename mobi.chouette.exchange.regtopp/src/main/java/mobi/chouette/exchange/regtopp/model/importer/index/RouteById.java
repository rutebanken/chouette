package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

@Log4j
public class RouteById extends IndexImpl<RegtoppRouteTMS>   {

	public RouteById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}

	@Override
	public boolean validate(RegtoppRouteTMS bean, RegtoppImporter dao) {
		boolean result = true;
		

		// Mulige valideringssteg
		
		// Koordinater ulike
		// Sone 1 og 2 forskjellige
		// Fullstendig navn !§= kortnavn
		
		// Holdeplassnummer X antall siffer
		
		
		log.error("Validation code for RegtoppStopp not implemented");
	
		return result;
	}
	

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new RouteById(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteById.class.getName(), factory);
	}


	@Override
	public void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppRouteTMS stop = (RegtoppRouteTMS) obj;
			_index.put(stop.getRouteId(), stop);
		}
	}
}
