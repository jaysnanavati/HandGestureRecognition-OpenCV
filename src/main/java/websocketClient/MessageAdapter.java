package websocketClient;

import java.lang.reflect.Type;
import java.util.Set;

import org.reflections.Reflections;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MessageAdapter implements JsonSerializer<WebSocketMessage>,
		JsonDeserializer<WebSocketMessage> {

	private static final String MESSAGE_TYPE = "MESSAGE_TYPE";
	private static final String DATA = "DATA";

	public WebSocketMessage deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jdc) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(MESSAGE_TYPE);
		String className = prim.getAsString();

		Reflections reflections = new Reflections("");

		Set<Class<? extends WebSocketMessage>> subTypes = reflections
				.getSubTypesOf(WebSocketMessage.class);
		
		for(Class<? extends WebSocketMessage> c: subTypes){
			if(c.getSimpleName().equals(className)){
				return jdc.deserialize(jsonObject.get(DATA), c);
			}
		}

		throw new JsonParseException("invalid MESSAGE_TYPE: "+ className  );

	}

	public JsonElement serialize(WebSocketMessage wsm, Type type,
			JsonSerializationContext jsc) {
		JsonObject retValue = new JsonObject();
		String className = wsm.getClass().getSimpleName();
		retValue.addProperty(MESSAGE_TYPE, className);
		JsonElement elem = jsc.serialize(wsm);
		retValue.add(DATA, elem);
		return retValue;
	}

}