package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.naming.InitialContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.parser.ChouettePTNetworkParser;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.util.Referential;

import org.apache.commons.io.input.BOMInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
// @Stateless(name = NeptuneParserCommand.COMMAND)
public class NeptuneParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneParserCommand";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		context.put(FILE_URL, fileURL);

		
		Report report = (Report) context.get(REPORT);
		FileInfo fileItem = new FileInfo();
		fileItem.setName(fileURL);

		// prepare validation

		try {

			URL url = new URL(fileURL);
			log.info("[DSU] parsing file : " + url);
			
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null) {
				referential.clear();
			}

			InputStream input = new BOMInputStream(url.openStream());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(input), 8192 * 10);
			XmlPullParser xpp = XmlPullParserFactory.newInstance()
					.newPullParser();
			xpp.setInput(in);
			context.put(PARSER, xpp);
			context.put(REFERENTIAL, new Referential());

			NeptuneObjectFactory factory = (NeptuneObjectFactory) context
					.get(NEPTUNE_OBJECT_FACTORY);
			if (referential == null) {
				factory = new NeptuneObjectFactory();
				context.put(NEPTUNE_OBJECT_FACTORY, factory);
			} else {
				factory.clear();
			}

			Parser parser = ParserFactory.create(ChouettePTNetworkParser.class
					.getName());
			parser.parse(context);

			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			fileItem.setStatus(FileInfo.STATE.OK);
			report.getFiles().getFileInfos().add(fileItem);
			result = SUCCESS;
			return result;
		} catch (Exception e) {
			fileItem.setStatus(FileInfo.STATE.NOK);
			report.getFiles().getFileInfos().add(fileItem);
			fileItem.getErrors().add(e.getMessage());
			throw e;
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneParserCommand();
			// try {
			// String name = "java:app/mobi.chouette.exchange.neptune/"
			// + COMMAND;
			// result = (Command) context.lookup(name);
			// } catch (NamingException e) {
			// log.error(e);
			// }
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
