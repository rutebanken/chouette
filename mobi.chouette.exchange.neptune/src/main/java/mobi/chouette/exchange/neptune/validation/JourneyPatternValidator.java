package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.JourneyPattern;

public class JourneyPatternValidator extends AbstractValidator implements Validator<JourneyPattern> , Constant{

	public static final String LINE_ID_SHORTCUT = "lineIdShortcut";

	public static final String ROUTE_ID = "routeId";

	public static final String STOP_POINT_LIST = "stopPointList";

	public static String NAME = "JourneyPatternValidator";

	private static final String JOURNEY_PATTERN_1 = "2-NEPTUNE-JourneyPattern-1";
	private static final String JOURNEY_PATTERN_2 = "2-NEPTUNE-JourneyPattern-2";
	private static final String JOURNEY_PATTERN_3 = "2-NEPTUNE-JourneyPattern-3";

	public static final String LOCAL_CONTEXT = "JourneyPattern";



	public JourneyPatternValidator(Context context) 
	{
		addItemToValidation(context, prefix, "JourneyPattern", 3, "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addLineIdShortcut(Context  context, String objectId, String lineIdShortcut)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_ID_SHORTCUT, lineIdShortcut);

	}

	public void addRouteId(Context  context, String objectId, String routeId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(ROUTE_ID, routeId);

	}

	@SuppressWarnings("unchecked")
	public void addStopPointList(Context context, String objectId, String stopId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(STOP_POINT_LIST);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(STOP_POINT_LIST, contains);
		}
		contains.add(stopId);

	}



	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, JourneyPattern target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context stopPointsContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context routesContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
		Context linesContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		String fileName = (String) context.get(FILE_NAME);

		// 2-NEPTUNE-JourneyPattern-1 : check existence of route
		prepareCheckPoint(context, JOURNEY_PATTERN_1);
		// 2-NEPTUNE-JourneyPattern-2 : check existence of StopPoints
		prepareCheckPoint(context, JOURNEY_PATTERN_2);


		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			// 2-NEPTUNE-JourneyPattern-1 : check existence of route
			if (objectContext.containsKey(ROUTE_ID))
			{
				String routeId = (String) objectContext.get(ROUTE_ID);
				if (!routesContext.containsKey(routeId))
				{
					Detail errorItem = new Detail(
							JOURNEY_PATTERN_1,
							new Location(sourceLocation,objectId), routeId);
					addValidationError(context,JOURNEY_PATTERN_1, errorItem);

				}
			}
	         // 2-NEPTUNE-JourneyPattern-2 : check existence of StopPoints
			List<String> stopPointIds = (List<String>) objectContext.get(STOP_POINT_LIST);
	         for (String stopPointId : stopPointIds)
	         {
	            if (!stopPointsContext.containsKey(stopPointId))
	            {
	            	Detail errorItem = new Detail(
	            			JOURNEY_PATTERN_2,
							new Location(sourceLocation,objectId), stopPointId);
					addValidationError(context,JOURNEY_PATTERN_2, errorItem);
	             
	            }
	         }
	         // 2-NEPTUNE-JourneyPattern-3 : check existence of line
	         if (objectContext.containsKey(LINE_ID_SHORTCUT))
	         {
	            prepareCheckPoint(context, JOURNEY_PATTERN_3);
	            String lineIdShortCut = (String) objectContext.get(LINE_ID_SHORTCUT);
	            if (!linesContext.containsKey(lineIdShortCut))
	            {
	            	Detail errorItem = new Detail(
	            			JOURNEY_PATTERN_3,
							new Location(sourceLocation,objectId), lineIdShortCut);
					addValidationError(context,JOURNEY_PATTERN_3, errorItem);
	            }

	         }
			
			
		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<JourneyPattern> create(Context context) {
			JourneyPatternValidator instance = (JourneyPatternValidator) context.get(NAME);
			if (instance == null) {
				instance = new JourneyPatternValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(JourneyPatternValidator.class.getName(), new DefaultValidatorFactory());
	}



}
