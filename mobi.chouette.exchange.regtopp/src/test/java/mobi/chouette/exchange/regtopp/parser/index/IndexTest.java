package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;
import java.util.Arrays;

import org.testng.annotations.BeforeClass;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

public class IndexTest {
	protected Context context;
	
	protected RegtoppImporter importer;

	protected RegtoppValidationReporter validationReporter;
	
	@BeforeClass
	protected void setupImporter() {
		validationReporter = new RegtoppValidationReporter();
		context = new Context();
		String path = "src/test/data";
		importer =  new RegtoppImporter(context, path, validationReporter);
	}

	
	protected FileContentParser createUnderlyingFileParser(File file, Class[] regtoppClasses) throws Exception {
		FileContentParser fileContentParser = new FileContentParser();
		RegtoppValidationReporter validationReporter = new RegtoppValidationReporter();
		FileInfo fileInfo = new FileInfo(file.getName(),FILE_STATE.ERROR);
		ParseableFile parseableFile = new ParseableFile(file, Arrays.asList(regtoppClasses), fileInfo);
		Context context = new Context();
		fileContentParser.parse(context , parseableFile, validationReporter);
		return fileContentParser;
	}
}