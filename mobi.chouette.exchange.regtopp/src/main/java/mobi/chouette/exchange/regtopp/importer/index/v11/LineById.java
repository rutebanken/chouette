package mobi.chouette.exchange.regtopp.importer.index.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.IndexImpl;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class LineById extends IndexImpl<RegtoppLineLIN> {

	public LineById(RegtoppValidationReporter validationReporter, FileContentParser fileParser) throws Exception {
		super(validationReporter, fileParser);
	}

	@Override
	public boolean validate(RegtoppLineLIN bean, RegtoppImporter dao) {
		boolean result = true;

		// Mulige valideringssteg

		// TODO

		log.warn("Validation code for RegtoppLine not implemented");

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
			return new LineById(validationReporter, parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(LineById.class.getName(), factory);
	}

	@Override
	public void index() throws Exception {
		for (Object obj : parser.getRawContent()) {
			RegtoppLineLIN newRecord = (RegtoppLineLIN) obj;
			RegtoppLineLIN existingRecord = index.put(newRecord.getLineId(), newRecord);
			if (existingRecord != null) {
				log.error("Duplicate key in LIN file. Existing: "+existingRecord+" Ignored duplicate: "+newRecord);
				validationReporter.reportError(new Context(), new RegtoppException(new FileParserValidationError(RegtoppLineLIN.FILE_EXTENSION,
						newRecord.getRecordLineNumber(), "Linjenr", newRecord.getLineId(), ERROR.DUPLICATE_KEY, "Duplicate key")), null);
			}
		}
	}
}