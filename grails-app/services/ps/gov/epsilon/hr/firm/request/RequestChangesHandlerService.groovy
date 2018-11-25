package ps.gov.epsilon.hr.firm.request

import grails.transaction.Transactional
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.request.IRequestChangesReflect
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

@Transactional
class RequestChangesHandlerService implements IRequestChangesReflect {

    RequestService requestService
    /**
     * handles changes for requests
     * Request that need to be handled:
     * requests with status approved and its changes are not applied.
     * requests with status approved by workflow and its parent status is approved by workflow too
     */
    public void handleRequestChanges() {
        StringBuilder sbQuery = new StringBuilder()
        sbQuery << "from Request r where r.changesApplied= false "
        sbQuery << " and (r.requestStatus=:approvedStatus or "
        sbQuery << "(r.requestStatus=:approvedByWFStatus and r.parentRequestId <> null and r.parentRequestId in "
        sbQuery << "(select pr.id from Request pr where pr.requestStatus=:approvedByWFStatus) ) )"
        sbQuery << " order by r.requestType "

        Map params = [approvedStatus: EnumRequestStatus.APPROVED, approvedByWFStatus: EnumRequestStatus.APPROVED_BY_WORKFLOW]

        List<Request> unhandledRequests = Request.executeQuery(sbQuery?.toString(), params)

        log.info("Processing ${unhandledRequests?.size()} request changes")

        unhandledRequests?.each { req ->
            try {
                log.debug("handling changes for request ${req.id}")
                applyRequestChanges(req)
                log.debug("Finished handling changes for request ${req.id}")
            } catch (Exception ex) {
                log.error("Failed to handle changes on request $req.id", ex)
            }
        }

        log.info("Finished Processing ${unhandledRequests?.size()} request changes")
    }

    @Override
    void applyRequestChanges(Request requestInstance) {
        log.debug("processing request " + requestInstance?.id)
        log.debug("request type " + requestInstance?.requestType)
        log.debug("request status " + requestInstance?.requestStatus)
        EnumRequestStatus parentStatus = null
        Request parentRequest = null
        if (requestInstance?.parentRequestId) {
            log.debug("request is a child, reverting changes on parent request " + requestInstance?.parentRequestId)
            parentRequest = requestInstance?.parentRequest
            parentStatus = parentRequest?.requestStatus
            // revert changes from parent request
            revertRequestChanges(parentRequest, requestInstance.requestType.requestCategory == EnumRequestCategory.CANCEL ?
                    EnumRequestStatus.CANCELED : EnumRequestStatus.OVERRIDEN)
        }
        if (requestInstance.requestType.requestCategory == EnumRequestCategory.CANCEL) {
            log.debug("request category is cancel ")
            if (parentStatus == EnumRequestStatus.APPROVED_BY_WORKFLOW &&
                    requestInstance.requestStatus == EnumRequestStatus.APPROVED_BY_WORKFLOW) {
                // cancel does not need to be approved by external party since original is not approved
                requestInstance?.requestStatus = EnumRequestStatus.APPROVED
                log.debug("request status is set to APPROVED ")
            }
            if (!requestInstance?.extraInfo.allLevels) {
                log.debug("request is to cancel only the direct parent, need to re-activate previous request")
                // parent request is cancelled, then grand parent should be activated
                if (parentRequest?.parentRequestId) {
                    Request grandParent = parentRequest?.parentRequest
                    if (grandParent?.requestStatus == EnumRequestStatus.OVERRIDEN) {
                        grandParent.requestStatus = EnumRequestStatus.APPROVED
                        applyRequestChanges(grandParent)
                    }
                }
            }
        } else {
            log.debug("request is not cancel, applying changes ")
            getRequestChangesHandler(requestInstance.requestType)?.applyRequestChanges(requestInstance)
        }
        log.debug("save request $requestInstance.id after applying changes")
        if (requestInstance.extraInfo && !requestInstance.extraInfo.managerialOrderDate) {
            requestInstance.extraInfo.managerialOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
        }
        requestInstance?.changesApplied = true
        requestInstance = requestService.saveManagerialOrderForRequest(null, requestInstance)

        requestInstance?.save(flush: true, failOnError: true)
    }

    @Override
    void revertRequestChanges(Request requestInstance, EnumRequestStatus newStatus = EnumRequestStatus.OVERRIDEN) {
        if (requestInstance?.requestStatus in [EnumRequestStatus.APPROVED, EnumRequestStatus.APPROVED_BY_WORKFLOW]) {
            // reflect changes
            if (requestInstance.requestStatus == EnumRequestStatus.APPROVED) {
                getRequestChangesHandler(requestInstance.requestType)?.revertRequestChanges(requestInstance)
            }
            requestInstance?.requestStatus = newStatus
            requestInstance?.changesApplied = false

            if (!requestInstance.externalOrderDate) {
                requestInstance.externalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }
            if (!requestInstance.internalOrderDate) {
                requestInstance.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            requestInstance?.save(flush: true, failOnError: true)
        }
    }

    @Transactional(readOnly = true)
    Request getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        Request request = Request.read(params.id)
        if (!request) {
            return null
        }
        IRequestChangesReflect requestHandler = getRequestChangesHandler(request.requestType)
        return requestHandler.getInstanceWithRemotingValues(params)
    }

    @Override
    Request saveOperation(GrailsParameterMap params) {
        EnumRequestType requestType = EnumRequestType.valueOf(params.requestType)
        IRequestChangesReflect requestHandler = getRequestChangesHandler(requestType)
        return requestHandler.saveOperation(params)
    }
/**
 * returns the object that handles request changes
 * @param requestType
 * @return
 */
    private IRequestChangesReflect getRequestChangesHandler(EnumRequestType requestType) {
        try {
            return (IRequestChangesReflect) Holders.applicationContext.getBean(requestType?.serviceName)
        } catch (Exception ex) {
            log.warn("Handler for $requestType not implemented yet")
        }
        return null
    }
}
