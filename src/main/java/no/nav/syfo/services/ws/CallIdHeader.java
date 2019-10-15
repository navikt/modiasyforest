package no.nav.syfo.services.ws;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.MDC;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

public class CallIdHeader extends AbstractPhaseInterceptor<Message> {

    private static final Logger log = getLogger(CallIdHeader.class);

    public CallIdHeader() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            QName qName = new QName("uri:no.nav.applikasjonsrammeverk", "callId");
            SoapHeader header = new SoapHeader(qName, callId(), new JAXBDataBinding(String.class));
            ((SoapMessage) message).getHeaders().add(header);
        } catch (JAXBException ex) {
            log.warn("Error while setting CallId header", ex);
        }
    }

    private String callId() {
        return ofNullable(MDC.get("Nav-Callid"))
                .orElseGet(randomUUID()::toString);
    }

}
