package ps.gov.epsilon.hr.firm.child

import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.personRelationShips.ManagePersonRelationShipsService
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestExtendExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.request.IRequestChangesReflect
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.enums.v1.GenderType
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.commands.v1.RelationshipTypeCommand
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonMaritalStatusDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * --this service is aims to add and manage child request for employee
 * <h1>Usage</h1>
 * --this service is used to create/update/delete and get child request for employee
 * <h1>Restriction</h1>
 * -need employee to be created before
 * -delete in case of new only
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ChildRequestService implements IRequestChangesReflect {

    MessageSource messageSource
    def formatService
    PersonService personService
    EmployeeService employeeService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService
    GovernorateService governorateService
    PersonRelationShipsService personRelationShipsService
    ManagePersonRelationShipsService managePersonRelationShipsService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "includedInList", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LITE_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
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
        String parentRequestId = params["parentRequestId"]
        Long relatedPersonId = params.long("relatedPersonId")

        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String status = params["status"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        String militaryRankId = params["militaryRank.id"]
        Long firmId = params.long("firm.id")
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        String threadId = params["threadId"]
        List<EnumRequestType> requestTypeList = params.list("requestType[]")?.collect { EnumRequestType.valueOf(it) }
        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return ChildRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
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
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (employeeId || militaryRankId) {
                    employee {
                        if (employeeId) {
                            eq("id", employeeId)
                        }
                        if (militaryRankId) {
                            currentEmployeeMilitaryRank {
                                eq("militaryRank.id", militaryRankId)
                            }
                        }
                    }
                }
                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }

                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (relatedPersonId) {
                    eq("relatedPersonId", relatedPersonId)
                }
                if (requestDate) {
                    eq("requestDate", requestDate)
                }
                if (fromRequestDate) {
                    ge("requestDate", fromRequestDate)
                }
                if (toRequestDate) {
                    le("requestDate", toRequestDate)
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
                if (excludeRequestStatusList) {
                    not {
                        inList("requestStatus", excludeRequestStatusList)
                    }
                }
                if (requestTypeList) {
                    inList('requestType', requestTypeList)
                }
                if (threadId) {
                    eq('threadId', threadId)
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
    PagedList searchCanHaveOperation(GrailsParameterMap params) {

        String childTypeId = params['childType.id']

        StringBuilder sbWhereStatement = new StringBuilder()
        Map queryParams = [:]

        if (childTypeId) {
            sbWhereStatement << "and r.childType.id = :childTypeId "
            queryParams['childTypeId'] = childTypeId
        }

        params.sbWhereStatement = sbWhereStatement
        params.queryParams = queryParams

        // set request types
        params[EnumRequestCategory.ORIGINAL.name()] = EnumRequestType.CHILD_REQUEST.name()
        params[EnumRequestCategory.EDIT.name()] = EnumRequestType.CHILD_EDIT_REQUEST.name()
        params[EnumRequestCategory.CANCEL.name()] = EnumRequestType.CHILD_CANCEL_REQUEST.name()

        params['domainName'] = ChildRequest.class.name

        PagedList childRequestList = requestService.searchCanHaveOperation(params)
        return injectRemotingValues(childRequestList)
    }

    /**
     * inject remoting values into list
     * List without remoting values
     * @return PagedResultList including remoting values
     */
    def injectRemotingValues(def requestList) {
        SearchBean searchBean
        List<PersonDTO> personList
        List<GovernorateDTO> governorateDTOList
        List<ChildRequest> childRequestList = (List<ChildRequest>) requestList?.resultList
        if (childRequestList) {
            /**
             * to get person DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: PCPUtils.union(childRequestList?.employee?.personId, childRequestList?.relatedPersonId)))
            personList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get governorate DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: childRequestList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(searchBean)?.resultList


            childRequestList?.each { ChildRequest childRequest ->
                childRequest?.transientData?.relatedPersonDTO = personList.find {
                    it?.id == childRequest?.relatedPersonId
                }
                /**
                 * assign personDTO to each employee
                 */
                childRequest.employee.transientData.put("personDTO", personList?.find {
                    it?.id == childRequest?.employee?.personId
                })
                /**app
                 * assign governorateDTO to each employee
                 */
                childRequest.employee.transientData.put("governorateDTO", governorateDTOList?.find {
                    it?.id == childRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })
            }
        }
        return requestList
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = search(params)
        return injectRemotingValues(pagedResultList)
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ChildRequest.
     */
    ChildRequest save(GrailsParameterMap params) {
        ChildRequest childRequestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            childRequestInstance = ChildRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (childRequestInstance.version > version) {
                    childRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('childRequest.label', null, 'childRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this childRequest while you were editing")
                    return childRequestInstance
                }
            }
            if (!childRequestInstance) {
                childRequestInstance = new ChildRequest()
                childRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('childRequest.label', null, 'childRequest', LocaleContextHolder.getLocale())] as Object[], "This childRequest with ${params.id} not found")
                return childRequestInstance
            }
        } else {
            childRequestInstance = new ChildRequest()
            childRequestInstance.requestDate = ZonedDateTime.now()
        }
        try {
            childRequestInstance.properties = params;
            GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id': params['firm.id'], id: params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

            if (employee?.transientData?.personMaritalStatusDTO?.maritalStatus?.id == ps.police.pcore.enums.v1.MaritalStatusEnum.SINGLE.value()) {
                childRequestInstance.errors.reject('childRequest.single.error.label')
                return childRequestInstance
            }


            childRequestInstance.employee = employee
            childRequestInstance.currentEmploymentRecord = employee?.currentEmploymentRecord;
            childRequestInstance.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank


            childRequestInstance = requestService.saveManagerialOrderForRequest(params, childRequestInstance)

            childRequestInstance?.save(flush: true, failOnError: true);


            if (childRequestInstance?.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        ChildRequest.getName(),
                        childRequestInstance?.id + "",
                        !hasHRRole)

                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }

        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            childRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            childRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            childRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return childRequestInstance
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ChildRequest.
     */
    ChildRequest saveOperation(GrailsParameterMap params) {
        ChildRequest childRequestInstance
        ChildRequest parentRequestInstance
        if (params.parentRequestId) {
            parentRequestInstance = ChildRequest.get(params["parentRequestId"])
            if (!parentRequestInstance) {
                parentRequestInstance = new ChildRequest()
                parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('childRequest.label', null, 'childRequest', LocaleContextHolder.getLocale())] as Object[], "This childRequest with ${params.parentRequestId} not found")
                return parentRequestInstance
            }
        } else {
            parentRequestInstance = new ChildRequest()
            parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('childRequest.label', null, 'childRequest', LocaleContextHolder.getLocale())] as Object[], "This childRequest with ${params.parentRequestId} not found")
            return parentRequestInstance
        }
        try {
            // create a clone from parent request
            childRequestInstance = parentRequestInstance.clone()

            // update data from client
            childRequestInstance.properties = params

            if (parentRequestInstance.requestType == EnumRequestType.CHILD_CANCEL_REQUEST ||
                    (childRequestInstance.requestType == EnumRequestType.CHILD_CANCEL_REQUEST && !parentRequestInstance.canCancelRequest) ||
                    (childRequestInstance.requestType != EnumRequestType.CHILD_CANCEL_REQUEST && !parentRequestInstance.canHaveOperation)) {
                throw new Exception("Cannot make any operation on request " + parentRequestInstance.id)
            }

            /**
             * assign employee for request
             */
            Employee employee = parentRequestInstance?.employee
            params["employee.id"] = employee?.id

            if (childRequestInstance.requestType in [EnumRequestType.CHILD_EDIT_REQUEST]) {
                params["doSave"] = false
                childRequestInstance = save(params)
                childRequestInstance.employee = parentRequestInstance?.employee
                childRequestInstance.currentEmploymentRecord = parentRequestInstance?.employee?.currentEmploymentRecord
                childRequestInstance.currentEmployeeMilitaryRank = parentRequestInstance?.employee?.currentEmployeeMilitaryRank
                childRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            } else if (childRequestInstance.requestType == EnumRequestType.CHILD_CANCEL_REQUEST) {
                // extra info should be instance of cancelInfo
                childRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            }

            childRequestInstance?.extraInfo?.request = childRequestInstance
            if (!childRequestInstance.extraInfo.reason) {
                childRequestInstance.extraInfo.reason = childRequestInstance?.requestReason
            }
            if (!childRequestInstance?.extraInfo?.managerialOrderDate) {
                childRequestInstance?.extraInfo?.managerialOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            childRequestInstance = requestService.saveManagerialOrderForRequest(params, childRequestInstance)

            childRequestInstance?.save(flush: true, failOnError: true);

            if (childRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        ChildRequest.getName(),
                        childRequestInstance?.id + "",
                        !hasHRRole)

                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            log.error("Failed to save request operation", ex)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return childRequestInstance
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
            ChildRequest instance = ChildRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && (instance.requestStatus == EnumRequestStatus.CREATED) && (instance?.trackingInfo?.status != GeneralStatus.DELETED)) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save(flush: true)
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('virtualDelete.error.fail.delete.label')
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
     * @return ChildRequest.
     */
    @Transactional(readOnly = true)
    ChildRequest getInstance(GrailsParameterMap params) {
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
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    ChildRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                return results[0]
            }
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
        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
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

    /**
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return childRequest.
     */
    @Transactional(readOnly = true)
    ChildRequest getPreCreateInstance(GrailsParameterMap params) {
        ChildRequest childRequest = new ChildRequest(params)
        //CHECK if employee has request in [progress or approved] requests
        /*GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, EnumRequestStatus.APPROVED]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            childRequest.errors.reject('request.employeeHasRequest.error.label')
        } else {*/
        GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id': params['firmId'], id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

        //check if the employee current status category is COMMITTED or not
        if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
            childRequest.errors.reject('request.employeeUncommitted.error.label')
        } else {
            childRequest?.employee = employee
            childRequest?.requestDate = ZonedDateTime.now()
            childRequest?.currentEmploymentRecord = employee?.currentEmploymentRecord
        }
        //}
        return childRequest
    }


    @Override
    void applyRequestChanges(Request request) {
        ChildRequest childRequest = (ChildRequest) request
        GrailsParameterMap relationShipParams
        PersonRelationShipsCommand personRelationShipsCommand
        PersonRelationShipsDTO personRelationShipsDTO
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: childRequest?.relatedPersonId))
        PersonDTO relatedPersonDTO = personService?.getPerson(searchBean)

        //reflect the changes of relation to core
        //retrieve new person relationShips status in pcore, if not exist, save new relation
        relationShipParams = new GrailsParameterMap([:], null)
        relationShipParams["person.id"] = childRequest?.employee?.personId
        relationShipParams["relatedPerson.id"] = childRequest?.relatedPersonId
        personRelationShipsDTO = personRelationShipsService.getPersonRelationShips(PCPUtils.convertParamsToSearchBean(relationShipParams));

        //1- check what is the new martial status value, if its married, add as new relation to pcore:
        if (!personRelationShipsDTO?.id) {
            relationShipParams = new GrailsParameterMap([:], null)
            relationShipParams.fromDate = childRequest?.requestDate
            relationShipParams.personId = childRequest?.employee?.personId
            relationShipParams.relatedPersonId = childRequest?.relatedPersonId
            relationShipParams.isDependent = childRequest?.isDependent
            //set the relation ship type depends on the gender type we got above
            if (relatedPersonDTO?.genderType?.id == GenderType.FEMALE.value()) {
                relationShipParams.relationshipType = RelationshipTypeEnum.DAUGHTER.value()
            } else {
                relationShipParams.relationshipType = RelationshipTypeEnum.SON.value()
            }
            try {
                personRelationShipsCommand = managePersonRelationShipsService.savePersonRelationShips(relationShipParams);
            } catch (Exception ex) {
                throw new Exception("error occurred while updating person RelationShips in core")
            }

            if (!personRelationShipsCommand?.id) {
                throw new Exception("error occurred while updating person RelationShips in core")
            } else {
                childRequest?.personRelationShipId = personRelationShipsCommand?.id
            }
        } else {
            //create new command and set all data to save the "toDate" value of old relation ship record
            personRelationShipsCommand = new PersonRelationShipsCommand(id: personRelationShipsDTO?.id)
            personRelationShipsCommand.person = new PersonCommand(id: personRelationShipsDTO?.person?.id)
            personRelationShipsCommand.relatedPerson = new PersonCommand(id: personRelationShipsDTO?.relatedPerson?.id)
            personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: personRelationShipsDTO?.relationshipType?.id)
            personRelationShipsCommand.fromDate = personRelationShipsDTO?.fromDate
            personRelationShipsCommand.toDate = personRelationShipsDTO?.toDate
            personRelationShipsCommand.isDependent = childRequest?.isDependent
            personRelationShipsCommand = personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
            if (!personRelationShipsCommand?.id) {
                throw new Exception("error occurred while updating person RelationShips in core")
            } else {
                childRequest?.personRelationShipId = personRelationShipsCommand?.id
            }
        }
        childRequest?.validate()
        childRequest?.extraInfo?.validate()
        childRequest.save(flush: true, failOnError: true)
    }

    @Override
    void revertRequestChanges(Request request) {
        ChildRequest childRequest = (ChildRequest) request
        GrailsParameterMap relationShipParams
        GrailsParameterMap personMaritalStatusParams
        DeleteBean deleteBean

        if (childRequest?.personRelationShipId) {
            relationShipParams = new GrailsParameterMap([:], null)
            relationShipParams["id"] = childRequest?.personRelationShipId
            //delete the relation ship:
            deleteBean = personRelationShipsService.deletePersonRelationShips(PCPUtils.convertParamsToDeleteBean(relationShipParams))
        }
    }

    @Transactional(readOnly = true)
    List<ChildRequest> getThreadWithRemotingValues(GrailsParameterMap params) {

        //if id is not null then return values from search method
        if (params.threadId) {
            // if there is any specific params, can be used here
            DetachedCriteria criteria = new DetachedCriteria(ChildRequest).build {

            }
            def requestList = requestService.getThreadWithRemotingValues(criteria, params)
            println "${requestList}"
            return injectRemotingValues(requestList)
        }
        return null
    }


}