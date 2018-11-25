package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -this service is aims to create suspension extension request
 * <h1>Usage</h1>
 * -this service is used to create suspension extension request
 * <h1>Restriction</h1>
 * -need a suspension request created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionExtensionRequestService {

    MessageSource messageSource
    def formatService
    PersonService personService
    GovernorateService governorateService
    WorkFlowProcessService workFlowProcessService
    def sessionFactory

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "suspensionRequest.employee", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionRequest.suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
    ]

    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "suspensionRequest.employee", type: "employee", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionRequest.suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
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
        String employeeId = params["employee.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        Short numOfDays = params.long("numOfDays")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String suspensionRequestId = params["suspensionRequest.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String status = params["status"]
        String militaryRankId = params["militaryRank.id"]
        ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType suspensionType = params["suspensionType"] ? ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.valueOf(params["suspensionType"]) : null
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])
        Short periodInMonth = params.long("periodInMonth")





        return SuspensionExtensionRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
                }
            }
            and {
                suspensionRequest {
                    if (employeeId) {
                        eq('employee.id', employeeId)
                    }

                    if (militaryRankId) {
                        employee {
                            currentEmployeeMilitaryRank {
                                militaryRank {
                                    eq("id", militaryRankId)
                                }
                            }
                        }
                    }

                    if (suspensionType) {
                        eq("suspensionType", suspensionType)
                    }


                }

                if (periodInMonth) {
                    eq("periodInMonth", periodInMonth)
                }

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
                if (employeeId) {
                    eq("employee.id", employeeId)
                }

                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (numOfDays) {
                    eq("numOfDays", numOfDays)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (requestDate) {
                    le("requestDate", requestDate)
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
                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }
                if (suspensionRequestId) {
                    eq("suspensionRequest.id", suspensionRequestId)
                }
                if (toDate) {
                    le("toDate", toDate)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    lte("fromDate", toFromDate)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    lte("toDate", toToDate)
                }

                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                order(columnName, dir)
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return SuspensionExtensionRequest.
 */
    SuspensionExtensionRequest save(GrailsParameterMap params) {
        SuspensionExtensionRequest suspensionExtensionRequestInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionExtensionRequestInstance = SuspensionExtensionRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionExtensionRequestInstance.version > version) {
                    suspensionExtensionRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionExtensionRequest.label', null, 'suspensionExtensionRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionExtensionRequest while you were editing")
                    return suspensionExtensionRequestInstance
                }
            }
            if (!suspensionExtensionRequestInstance) {
                suspensionExtensionRequestInstance = new SuspensionExtensionRequest()
                suspensionExtensionRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionExtensionRequest.label', null, 'suspensionExtensionRequest', LocaleContextHolder.getLocale())] as Object[], "This suspensionExtensionRequest with ${params.id} not found")
                return suspensionExtensionRequestInstance
            }
        } else {
            suspensionExtensionRequestInstance = new SuspensionExtensionRequest()
        }
        try {

            suspensionExtensionRequestInstance.properties = params;

            final session = sessionFactory.currentSession



            Map sqlParamsMap = [:]


            /**
             * validate toDate for suspension extension request  greater than toDate for request itself
             */
            if (suspensionExtensionRequestInstance.toDate && (suspensionExtensionRequestInstance?.suspensionRequest?.toDate >= suspensionExtensionRequestInstance.toDate)) {
                suspensionExtensionRequestInstance.errors.reject('suspensionExtensionRequest.error.in.date.message')
                return suspensionExtensionRequestInstance
            }

            /**
             * set fromDate for suspension extension request = toDate (for request itself)  + 1
             */
            suspensionExtensionRequestInstance.fromDate = suspensionExtensionRequestInstance?.suspensionRequest?.toDate?.plusDays(1)

            /**
             * assign currentEmploymentRecord for employee
             */
            suspensionExtensionRequestInstance.currentEmploymentRecord = suspensionExtensionRequestInstance?.employee?.currentEmploymentRecord
            /**
             * assign currentEmployeeMilitaryRank for employee
             */
            suspensionExtensionRequestInstance.currentEmployeeMilitaryRank = suspensionExtensionRequestInstance?.employee?.currentEmployeeMilitaryRank

            /**
             * calculate the periodInMonth using from date & to date
             */
            suspensionExtensionRequestInstance.periodInMonth = ChronoUnit.MONTHS.between(suspensionExtensionRequestInstance?.fromDate?.dateTime?.date, suspensionExtensionRequestInstance?.toDate?.dateTime?.date) + 1


            /**
             * validate there is no overlap with another suspension extension requests
             * use 0003-03-03 03:03:03 to represent the null in the zone date time
             */
            String query = "SELECT (count(1)=0) as request_overlap " +
                    "FROM  " +
                    "  request,  " +
                    "  suspension_extension_request " +
                    " WHERE  " +
                    "  suspension_extension_request.id = request.id AND request.request_status!= :requestStatus AND  " +
                    "  request.employee_id = :employeeId AND request.status= :trackingInfoStatus AND " +
                    "  (" +
                    "   suspension_extension_request.from_date_datetime," +
                    " (CASE WHEN suspension_extension_request.to_date_datetime " +
                    "= '${PCPUtils.convertZonedDateTimeToTimeStamp(PCPUtils.DEFAULT_ZONED_DATE_TIME)}' " +
                    " THEN current_date else suspension_extension_request.to_date_datetime end)" +
                    "   ) " +
                    " overlaps ( :fromDate, :toDate ) "

            /**
             * fill map parameter
             */
            sqlParamsMap = [fromDate          : PCPUtils.convertZonedDateTimeToTimeStamp(suspensionExtensionRequestInstance?.fromDate),
                            toDate            : PCPUtils.convertZonedDateTimeToTimeStamp(suspensionExtensionRequestInstance?.toDate),
                            employeeId        : suspensionExtensionRequestInstance?.suspensionRequest?.employee?.id,
                            requestStatus     : EnumRequestStatus.REJECTED.toString(),
                            trackingInfoStatus: GeneralStatus.ACTIVE.toString()]

            /**
             * assign values for parameters
             */
            Query sqlQuery = session?.createSQLQuery(query)

            sqlParamsMap?.each {
                sqlQuery.setParameter(it?.key?.toString(), it?.value)
            }

            /**
             * execute query
             */
            final queryResults = sqlQuery?.list()?.get(0)


            if(!queryResults){
                suspensionExtensionRequestInstance.errors.reject('suspensionExtensionRequest.error.in.date.message')
                return suspensionExtensionRequestInstance
            }


            /**
             * save instance
             */
            suspensionExtensionRequestInstance.save(failOnError: true);

            //check if user has HR Role
            boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

            /**
             * get  the workflow data
             */
            WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                    suspensionExtensionRequestInstance?.employee?.id + "",
                    suspensionExtensionRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                    suspensionExtensionRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                    suspensionExtensionRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                    SuspensionExtensionRequest.getName(),
                    suspensionExtensionRequestInstance?.id + "",
                    !hasHRRole)

            if (hasHRRole) {
                workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
            }


        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            suspensionExtensionRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            suspensionExtensionRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            suspensionExtensionRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionExtensionRequestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            def id
            //if the id is encrypted
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }

            SuspensionExtensionRequest instance = SuspensionExtensionRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save(failOnError: true)
                deleteBean.status = true
            } else {
                deleteBean.status = false
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
 * @return SuspensionExtensionRequest.
 */
    @Transactional(readOnly = true)
    SuspensionExtensionRequest getInstance(GrailsParameterMap params) {
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

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return SuspensionExtensionRequest.
     */
    @Transactional(readOnly = true)
    SuspensionExtensionRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList suspensionExtensionRequestList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<GovernorateDTO> governorateDTOList

        if (suspensionExtensionRequestList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: suspensionExtensionRequestList?.resultList?.suspensionRequest?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            //fill employee governorate information from core
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: suspensionExtensionRequestList?.resultList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList




            suspensionExtensionRequestList?.each { SuspensionExtensionRequest suspensionExtensionRequest ->

                /**
                 * assign for personDTO  for employee
                 */
                suspensionExtensionRequest?.suspensionRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == suspensionExtensionRequest?.suspensionRequest?.employee?.personId
                })

                /**
                 * assign for governorateDTO  for employee
                 */
                suspensionExtensionRequest?.suspensionRequest?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })
            }
        }
        return suspensionExtensionRequestList
    }

}