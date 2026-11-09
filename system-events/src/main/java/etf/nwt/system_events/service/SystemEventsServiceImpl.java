package etf.nwt.system_events.service;

import etf.nwt.system_events.EventRequest;
import etf.nwt.system_events.EventResponse;
import etf.nwt.system_events.SystemEventsServiceGrpc;
import etf.nwt.system_events.model.EventLog;
import etf.nwt.system_events.repository.EventLogRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@GrpcService
public class SystemEventsServiceImpl extends SystemEventsServiceGrpc.SystemEventsServiceImplBase {

    @Autowired
    private EventLogRepository eventLogRepository;

    @Override
    public void logSystemEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) {

        EventLog eventLog = new EventLog();

        eventLog.setTimestamp(LocalDateTime.parse(request.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME));
        eventLog.setMicroserviceName(request.getMicroserviceName());
        eventLog.setUserUsername(request.getUser());
        eventLog.setResourceName(request.getResourceName());

        String actionType = request.getActionType();
        String serviceResponse;

        if (actionType.endsWith("FAIL")) {
            serviceResponse = "FAIL";
            actionType = actionType.replace("FAIL", "");
        } else if (actionType.endsWith("SUCCESS")) {
            serviceResponse = "SUCCESS";
            actionType = actionType.replace("SUCCESS", "");
        } else {
            serviceResponse = "UNKNOWN";
        }

        eventLog.setActionType(actionType);
        eventLog.setResponseType(serviceResponse);

        eventLogRepository.save(eventLog);

        EventResponse response = EventResponse.newBuilder()
                .setMessage("Event successfully saved in system-events database!")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}