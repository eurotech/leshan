package leshan.server.client;

import static org.mockito.Mockito.verify;
import leshan.server.lwm2m.message.ClientResponse;
import leshan.server.lwm2m.message.ContentFormat;
import leshan.server.lwm2m.message.ExecRequest;
import leshan.server.lwm2m.message.ResponseCode;

import org.junit.Test;

public class ExecuteTest extends LwM2mClientServerIntegrationTest {

	@Test
	public void canNotExecuteWriteOnlyResource() {
		register();

		sendCreate(createResourcesTlv("hello", "goodbye"), GOOD_OBJECT_ID);

		final ClientResponse response = ExecRequest.newRequest(getClient(), GOOD_OBJECT_ID, GOOD_OBJECT_INSTANCE_ID, SECOND_RESOURCE_ID,
				"world", ContentFormat.TEXT).send(server.getRequestHandler());

		assertEmptyResponse(response, ResponseCode.METHOD_NOT_ALLOWED);
	}

	@Test
	public void canExecuteResource() {
		register();

		sendCreate(createResourcesTlv("hello", "goodbye"), GOOD_OBJECT_ID);

		final ClientResponse response = ExecRequest.newRequest(getClient(), GOOD_OBJECT_ID, GOOD_OBJECT_INSTANCE_ID, EXECUTABLE_RESOURCE_ID,
				"world", ContentFormat.TEXT).send(server.getRequestHandler());

		assertResponse(response, ResponseCode.CHANGED, new byte[0]);
		verify(executableResource).execute(GOOD_OBJECT_ID, GOOD_OBJECT_INSTANCE_ID, EXECUTABLE_RESOURCE_ID);
	}

}
