package mobi.chouette.exchange.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.BlockDAO;
import mobi.chouette.exchange.importer.updater.BlockUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.Block;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

import static mobi.chouette.exchange.importer.BlocksRegisterCommand.COMMAND;

@Log4j
@Stateless(name = COMMAND)
public class BlocksRegisterCommand implements Command {

    public static final String COMMAND = "BlocksRegisterCommand";

    @EJB
    private BlockDAO blockDAO;

    @EJB(beanName = BlockUpdater.BEAN_NAME)
    private Updater<Block> blockUpdater;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean execute(Context context) throws Exception {

        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);

        if (!context.containsKey(OPTIMIZED)) {
            context.put(OPTIMIZED, Boolean.TRUE);
        }
        Boolean optimized = (Boolean) context.get(OPTIMIZED);
        Referential cache = new Referential();
        context.put(CACHE, cache);

        Referential referential = (Referential) context.get(REFERENTIAL);

        try {

            for (Block newValue : referential.getSharedBlocks().values()) {

                Block oldValue = ObjectFactory.getBlock(cache, newValue.getObjectId());
                blockUpdater.update(context, oldValue, newValue);
                blockDAO.create(oldValue);
            }

            blockDAO.flush(); // to prevent SQL error outside method

            result = SUCCESS;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
            log.info(Color.LIGHT_GREEN + monitor.toString() + Color.NORMAL);
        }

        return result;
    }


    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = null;
            try {
                String name = "java:app/mobi.chouette.exchange/" + COMMAND;
                result = (Command) context.lookup(name);
            } catch (NamingException e) {
                // try another way on test context
                String name = "java:module/" + COMMAND;
                try {
                    result = (Command) context.lookup(name);
                } catch (NamingException e1) {
                    log.error(e);
                }
            }
            return result;
        }
    }

    static {
        CommandFactory.factories.put(BlocksRegisterCommand.class.getName(), new DefaultCommandFactory());
    }

}
