package com.rbkmoney.deanonimus.kafka.handler.party.management;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface PartyManagementHandler extends Handler<PartyChange, MachineEvent> {}
