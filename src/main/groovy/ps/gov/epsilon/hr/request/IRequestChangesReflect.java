package ps.gov.epsilon.hr.request;

import grails.web.servlet.mvc.GrailsParameterMap;
import ps.gov.epsilon.hr.firm.request.Request;

/**
 * Created by muath on 13/06/18.
 */
public interface IRequestChangesReflect {

    /**
     * Request status is approved, its changes need to be reflected
     * @param requestInstance
     */
    void applyRequestChanges(Request requestInstance);

    /**
     * Request Status is cancelled, its changes need to be reverted.
     * @param requestInstance
     */
    void revertRequestChanges(Request requestInstance);

    /**
     * retrieves request instance with remoting values
     * @param params
     * @return
     */
    Request getInstanceWithRemotingValues(GrailsParameterMap params);

    /**
     * to save/update operation model entry.
     * @param GrailsParameterMap params the search map.
     * @return Request.
     */
    Request saveOperation(GrailsParameterMap params);
}
