package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;

public class CalendarParser extends ParserImpl<GtfsCalendar>
{

   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsCalendar build(GtfsReader reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsCalendar bean, GtfsDao dao)
   {
      return true;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new CalendarParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(CalendarParser.class.getName(), factory);
   }

}
