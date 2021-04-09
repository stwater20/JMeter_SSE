import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import java.net.URI;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class SimpleEventHandler implements EventHandler {

	public List<String> respList = new ArrayList<String>();

	public void onOpen() throws Exception{
		log.info("The connection has been opened");
	}

	public void onClosed() throws Exception{
		log.info("The connection has been closed");
	}

	public void onMessage(String Event, MessageEvent messageEvent) throws Exception{
		respList.add(messageEvent.getData());
	}

	public void onComment(String comment) throws Exception{
		log.info(comment);
	}

	public void onError(Throwable t){
		log.info("Error "+t);
	}
}

	EventHandler eH = new SimpleEventHandler();

	String responseList="";
	
	EventSource.Builder builder = new EventSource.Builder(eH, URI.create(args[0]));
	
	EventSource eventSource = builder.build();

	eventSource.setReconnectionTimeMs(Integer.parseInt(args[1]));
	eventSource.start();
	TimeUnit.SECONDS.sleep(Integer.parseInt(args[2]));

	eventSource.close();

	for(String respRecord:eH.respList){
		JsonReader jsonReader = Json.createReader(new StringReader(respRecord));
		JsonObject jsonObject = jsonReader.readObject();
		responseList = responseList + jsonObject.toString();
	}
	
	SampleResult.setResponseData(responseList,"866");
