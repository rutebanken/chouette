package mobi.chouette.exchange.regtopp.parser.index;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.DaycodeById;

public class DaycodeByIdTest extends AbstractIndexTest{
	

	@Test(dependsOnMethods = {"setupImporter"})
	public void testValidation() throws Exception {
		FileContentParser fileContentParser = createUnderlyingFileParser(new File("src/test/data/regtopp12/R5001.dko"), new Class[] {RegtoppDayCodeHeaderDKO.class,RegtoppDayCodeDKO.class});
		DaycodeById index = new DaycodeById(validationReporter,fileContentParser);
		for(RegtoppDayCodeDKO obj : index) {
			boolean validData = index.validate(obj,importer);
			Assert.assertTrue(validData,"Bean did not validate: "+obj);
		}
		
		RegtoppDayCodeHeaderDKO header = index.getHeader();
		Assert.assertNotNull(header,"Header is missing from index");
		
		Assert.assertEquals(0, validationReporter.getExceptions().size());
	}

}
