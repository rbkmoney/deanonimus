package com.rbkmoney.deanonimus.kafka.handler;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyManagementHandlerService {

    private final List<PartyManagementHandler> partyManagementHandlers;
    private final MachineEventParser<PartyEventData> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        PartyEventData eventPayload = parser.parse(machineEvent);
        final List<PartyChange> partyChanges = eventPayload.getChanges();
        for (int i = 0; i < partyChanges.size(); i++) {
            PartyChange partyChange = partyChanges.get(i);
            Integer changeId = i;
            partyManagementHandlers.stream()
                    .filter(handler -> handler.accept(partyChange))
                    .forEach(handler -> handler.handle(partyChange, machineEvent, changeId));
        }

    }

}
