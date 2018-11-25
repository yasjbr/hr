package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class LoanRequestService {

    MessageSource messageSource
    def formatService
    PersonService personService
    OrganizationService organizationService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to represent request status
     */
    public static getStatus = { formatService, LoanRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.requestStatus?.toString()
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestedJob", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.requestedFromOrganizationDTO", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "numberOfPositions", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "status", type: getStatus, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String description = params["description"]
        String employeeId = params["employee.id"]

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])

        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])


        String loanListPersonId = params["loanListPerson.id"]
        Set loanRequestRelatedPersonsIds = params.listString("loanRequestRelatedPersons.id")
        Short numberOfPositions = params.long("numberOfPositions")
        String parentRequestId = params["parentRequestId"]
        Short periodInMonths = params.long("periodInMonths")
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus
        if (params['requestStatusHidden']) {
            requestStatus = params["requestStatusHidden"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatusHidden"]) : null
        } else if (params["requestStatus"]) {
            requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        }
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        Long requestedFromOrganizationId = params.long("requestedFromOrganizationId")
        String requestedJobId = params["requestedJob.id"]
        String requestedJobTitle = params["requestedJobTitle"]
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String toDepartmentId = params["toDepartment.id"]
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null
        Long firmId = params.long("firm.id")

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return LoanRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
                    ilike("requestedJobTitle", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (currentRequesterEmploymentRecordId) {
                    eq("currentRequesterEmploymentRecord.id", currentRequesterEmploymentRecordId)
                }
                if (description) {
                    ilike("description", "%${description}%")
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }
                //from date
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (fromDateFrom) {
                    ge("fromDate", fromDateFrom)
                }

                if (fromDateTo) {
                    le("fromDate", fromDateTo)
                }

                //to date
                if (toDate) {
                    eq("toDate", toDate)
                }
                if (toDateFrom) {
                    ge("toDate", toDateFrom)
                }
                if (toDateTo) {
                    le("toDate", toDateTo)
                }

                //request date
                if (requestDate) {
                    eq("requestDate", requestDate)
                }
                if (requestDateFrom) {
                    ge("requestDate", requestDateFrom)
                }
                if (requestDateTo) {
                    le("requestDate", requestDateTo)
                }


                if (loanListPersonId) {
                    eq("loanListPerson.id", loanListPersonId)
                }
                if (loanRequestRelatedPersonsIds) {
                    loanRequestRelatedPersons {
                        inList("id", loanRequestRelatedPersonsIds)
                    }
                }
                if (numberOfPositions) {
                    eq("numberOfPositions", numberOfPositions)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (periodInMonths) {
                    eq("periodInMonths", periodInMonths)
                }
                if (requestReason) {
                    ilike("requestReason", "%${requestReason}%")
                }
                if (requestStatus) {
                    eq("requestStatus", requestStatus)
                }
                if (requestStatusNote) {
                    ilike("requestStatusNote", "%${requestStatusNote}%")
                }
                if (requestType) {
                    eq("requestType", requestType)
                }
                if (requestedFromOrganizationId) {
                    eq("requestedFromOrganizationId", requestedFromOrganizationId)
                }
                if (requestedJobId) {
                    eq("requestedJob.id", requestedJobId)
                }
                if (requestedJobTitle) {
                    ilike("requestedJobTitle", "%${requestedJobTitle}%")
                }
                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }
                if (toDepartmentId) {
                    eq("toDepartment.id", toDepartmentId)
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (internalOrderNumber) {
                    eq('internalOrderNumber', internalOrderNumber)
                }
                if (externalOrderNumber) {
                    eq('externalOrderNumber', externalOrderNumber)
                }
                if (internalOrderDate) {
                    eq('internalOrderDate', internalOrderDate)
                }
                if (externalOrderDate) {
                    eq('externalOrderDate', externalOrderDate)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, dir)
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        if (pagedResultList.resultList) {

            SearchBean searchBean = new SearchBean()

            //collect organizationIds
            List organizationIds = pagedResultList?.requestedFromOrganizationId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            //fill all organization info
            List<OrganizationDTO> organizationList = organizationService.searchOrganization(searchBean)?.resultList

            //loop to fill all remoting values
            pagedResultList.each { LoanRequest loanRequest ->

                //fill all organization info
                if (loanRequest?.requestedFromOrganizationId) {
                    loanRequest.transientData.requestedFromOrganizationDTO = organizationList.find {
                        it.id == loanRequest?.requestedFromOrganizationId
                    }
                }
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanRequest.
 */
    LoanRequest save(GrailsParameterMap params) {
        LoanRequest loanRequestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            loanRequestInstance = LoanRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanRequestInstance.version > version) {
                    loanRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanRequest.label', null, 'loanRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanRequest while you were editing")
                    return loanRequestInstance
                }
            }
            if (!loanRequestInstance) {
                loanRequestInstance = new LoanRequest()
                loanRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanRequest.label', null, 'loanRequest', LocaleContextHolder.getLocale())] as Object[], "This loanRequest with ${params.id} not found")
                return loanRequestInstance
            }
        } else {
            loanRequestInstance = new LoanRequest()
        }
        try {

            //when update remove old loan related persons
            //We need it to remove it manually because grails not remove it automatically based on domain relations
            if (loanRequestInstance?.id) {
                LoanRequestRelatedPerson.executeUpdate("delete from LoanRequestRelatedPerson rp where rp.loanRequest.id = :loanRequestId", [loanRequestId: loanRequestInstance?.id])
            }

            // loanRequestRelatedPersons
            def requestedPersonIdList = params.list("requestedPersonId")
            def requestedPersonList = []
            requestedPersonIdList.eachWithIndex { requestedPersonId, index ->
                requestedPersonList << new LoanRequestRelatedPerson(
                        requestedPersonId: (requestedPersonId as long),
                        recordSource: EnumPersonSource.REQUESTED,
                        effectiveDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        firm: Firm.load(PCPSessionUtils.getValue("firmId"))
                )
            }
            params["loanRequestRelatedPersons"] = requestedPersonList

            loanRequestInstance.properties = params;

            //set request date as now without time
            loanRequestInstance.requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)

            loanRequestInstance = requestService.saveManagerialOrderForRequest(params, loanRequestInstance)

            /**
             * check if the request status is empty, the set default status: CREATED
             */
            if (!loanRequestInstance.requestStatus) {
                loanRequestInstance.requestStatus = EnumRequestStatus.CREATED
            }
            loanRequestInstance.save(failOnError: true);
            if (loanRequestInstance.requestStatus == EnumRequestStatus.CREATED) {

                //check if user has HR role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        null, null, null, null,
                        LoanRequest.getName(),
                        loanRequestInstance?.id + "",
                        !hasHRRole)

                // save workflow path details & update request status
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }

        } catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            loanRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            loanRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            loanRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return loanRequestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanRequest> loanRequestList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            loanRequestList = LoanRequest.findAllByIdInList(ids)

            loanRequestList.each { LoanRequest loanRequest ->
                if (loanRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete loanRequest
                    loanRequest.trackingInfo.status = GeneralStatus.DELETED
                    loanRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (loanRequestList) {
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanRequest.
 */
    @Transactional(readOnly = true)
    LoanRequest getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                return results[0]
            }
        }
        return null

    }

/**
 * to get model entry with remoting values.
 * @param GrailsParameterMap params the search map.
 * @return LoanRequest.
 */
    LoanRequest getInstanceWithRemotingValues(GrailsParameterMap params) {


        PagedResultList results = searchWithRemotingValues(params)
        if (results) {
            LoanRequest loanRequest = results[0]

            SearchBean searchBean = new SearchBean()
            //collect personIds
            List personIds = loanRequest?.loanRequestRelatedPersons?.requestedPersonId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
            //fill all persons info
            List<PersonDTO> personList = personService.searchPerson(searchBean)?.resultList

            //loop to fill all remoting values
            loanRequest.loanRequestRelatedPersons.each { LoanRequestRelatedPerson loanRequestRelatedPerson ->

                //fill all person info
                if (loanRequestRelatedPerson?.requestedPersonId) {
                    loanRequestRelatedPerson.transientData.requestedPersonDTO = personList.find {
                        it.id == loanRequestRelatedPerson?.requestedPersonId
                    }
                }
            }
            return loanRequest
        }

        return null

    }

/**
 * to auto complete model entry.
 * @param GrailsParameterMap params the search map.
 * @return JSON.
 */
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "requestedJob.id"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

/**
 * Convert paged result list to map depends on DOMAINS_COLUMNS.
 * @param def resultList may be PagedResultList or PagedList.
 * @param GrailsParameterMap params the search map
 * @param List < String >  DOMAIN_COLUMNS the list of model column names.
 * @return Map.
 * @see PagedResultList.
 * @see PagedList.
 */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}