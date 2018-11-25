package ps.gov.epsilon.hr.firm.request

import grails.databinding.SimpleMapDataBindingSource
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.ContactInfoClassificationEnum
import ps.police.pcore.enums.v1.ContactMethod
import ps.police.pcore.enums.v1.ContactType
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.BorderCrossingPointService
import ps.police.pcore.v2.entity.lookups.DocumentTypeService
import ps.police.pcore.v2.entity.lookups.dtos.v1.BorderCrossingPointDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.DocumentTypeDTO
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aim to create a border security coordination
 * <h1>Usage</h1>
 * -this service is used to create a border security coordination
 * <h1>Restriction</h1>
 * -need an employee & firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class BordersSecurityCoordinationService {

    MessageSource messageSource
    def formatService
    ContactInfoService contactInfoService
    PersonService personService
    DocumentTypeService documentTypeService
    BorderCrossingPointService borderCrossingPointService
    def grailsWebDataBinder
    GovernorateService governorateService
    WorkFlowProcessService workFlowProcessService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.documentTypeDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.borderCrossingPointDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.documentTypeDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.borderCrossingPointDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain']
    ]

    public static final List<String> VACATION_REQUEST_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.documentTypeDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.borderCrossingPointDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
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

        //set domain columns
        String columnName
        List<String> domainColumnsSearch = DOMAIN_COLUMNS
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            domainColumnsSearch = this."${domainColumns}"
        }
        if (column) {
            columnName = domainColumnsSearch[column]?.name
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
        Long borderLocationId = params.long("borderLocationId")
        Set contactInfos = params.listLong("contactInfos")
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        Long legalIdentifierId = params.long("legalIdentifierId")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String unstructuredLocation = params["unstructuredLocation"]
        String instanceId = params["instanceId"]
        String militaryRankId = params["militaryRank.id"]
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime sSearchDate = PCPUtils.parseZonedDateTime(params["sSearch"])
        String status = params["status"]


        return BordersSecurityCoordination.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
                if (sSearchDate) {
                    eq("toDate", sSearchDate)
                    eq("fromDate", sSearchDate)
                }
            }
            and {
                if (instanceId) {
                    ne("id", id)
                }

                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (borderLocationId) {
                    eq("borderLocationId", borderLocationId)
                }
                if (contactInfos) {
                    contactInfos {
                        inList("id", contactInfos)
                    }
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
                    ge("fromDate", fromDate)
                }
                if (legalIdentifierId) {
                    eq("legalIdentifierId", legalIdentifierId)
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
                if (toDate) {
                    lte("toDate", toDate)
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }


                if (militaryRankId) {
                    currentEmployeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankId)
                        }
                    }
                }


                eq("firm.id", PCPSessionUtils.getValue("firmId"))

                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    le("fromDate", toFromDate)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    le("toDate", toToDate)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
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
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return BordersSecurityCoordination.
 */
    BordersSecurityCoordination save(GrailsParameterMap params) {
        BordersSecurityCoordination bordersSecurityCoordinationInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            bordersSecurityCoordinationInstance = BordersSecurityCoordination.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (bordersSecurityCoordinationInstance.version > version) {
                    bordersSecurityCoordinationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('bordersSecurityCoordination.label', null, 'bordersSecurityCoordination', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this bordersSecurityCoordination while you were editing")
                    return bordersSecurityCoordinationInstance
                }
            }
            if (!bordersSecurityCoordinationInstance) {
                bordersSecurityCoordinationInstance = new BordersSecurityCoordination()
                bordersSecurityCoordinationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('bordersSecurityCoordination.label', null, 'bordersSecurityCoordination', LocaleContextHolder.getLocale())] as Object[], "This bordersSecurityCoordination with ${params.id} not found")
                return bordersSecurityCoordinationInstance
            }
        } else {
            bordersSecurityCoordinationInstance = new BordersSecurityCoordination()
        }
        try {
            bordersSecurityCoordinationInstance.properties = params;

            /**
             * to validate borders security coordination fromDate less than/equal toDate
             */
            if (bordersSecurityCoordinationInstance?.fromDate <= bordersSecurityCoordinationInstance?.toDate) {

                /**
                 * assign current military rank
                 */
                if (bordersSecurityCoordinationInstance?.employee?.currentEmployeeMilitaryRank) {
                    bordersSecurityCoordinationInstance.currentEmployeeMilitaryRank = bordersSecurityCoordinationInstance?.employee?.currentEmployeeMilitaryRank
                }

                /**
                 * assign current employment record
                 */
                if (bordersSecurityCoordinationInstance?.employee?.currentEmploymentRecord) {
                    bordersSecurityCoordinationInstance.currentEmploymentRecord = bordersSecurityCoordinationInstance?.employee?.currentEmploymentRecord
                }

                /**
                 * to validate, there is no border security coordination approved  in the same from & to Date for the same border location
                 */
                def count = BordersSecurityCoordination?.executeQuery("select count(*) from BordersSecurityCoordination bordersSecurityCoordination where bordersSecurityCoordination.fromDate <= :fromDate and bordersSecurityCoordination.toDate >= :toDate and bordersSecurityCoordination.id != :id and bordersSecurityCoordination.borderLocationId= :borderLocationId and bordersSecurityCoordination.requestStatus = :requestStatus",
                        [fromDate        : bordersSecurityCoordinationInstance?.fromDate, toDate: bordersSecurityCoordinationInstance?.toDate, id: bordersSecurityCoordinationInstance?.id,
                         borderLocationId: bordersSecurityCoordinationInstance?.borderLocationId, requestStatus: EnumRequestStatus.APPROVED])

                if (count?.get(0) > 0) {
                    bordersSecurityCoordinationInstance.errors.reject('bordersSecurityCoordination.createError.label')
                    return bordersSecurityCoordinationInstance
                } else {

                    if (bordersSecurityCoordinationInstance.validate()) {

                        /**
                         * save address
                         */
                        params["contactType.id"] = ContactType.PERSONAL.value()
                        params["contactMethod.id"] = ContactMethod.OTHER_ADDRESS.value()
                        ContactInfoCommand contactInfoCommand = new ContactInfoCommand()
                        grailsWebDataBinder.bind contactInfoCommand, params as SimpleMapDataBindingSource
                        contactInfoCommand.relatedObjectType = ContactInfoClassificationEnum.PERSON.toString()

                        if (params["location"]) {
                            contactInfoCommand.address = new LocationCommand()
                            grailsWebDataBinder.bind contactInfoCommand.address, params["location"] as SimpleMapDataBindingSource
                            contactInfoCommand.paramsMap.put("address", new CommandParamsMap(nameOfParameterKeyInService: "location", nameOfValueInCommand: "address"))
                            contactInfoCommand = contactInfoService?.saveContactInfo(contactInfoCommand)
                            if (contactInfoCommand.validate()&& contactInfoCommand?.id) {
                                bordersSecurityCoordinationInstance.addToContactInfos(contactInfoCommand?.id)
                            }
                            params.remove("contactMethod.id")
                        }

                        /**
                         * save phone
                         */
                        ContactInfoCommand contactInfoPhoneCommand = new ContactInfoCommand()
                        params["contactMethod.id"] = ContactMethod.MOBILE_NUMBER.value()
                        params.value = params.phoneNumber
                        grailsWebDataBinder.bind contactInfoPhoneCommand, params as SimpleMapDataBindingSource
                        contactInfoPhoneCommand.relatedObjectType = ContactInfoClassificationEnum.PERSON.toString()
                        contactInfoPhoneCommand = contactInfoService?.saveContactInfo(contactInfoPhoneCommand)
                        if (contactInfoPhoneCommand.validate()&& contactInfoPhoneCommand?.id) {
                            bordersSecurityCoordinationInstance.addToContactInfos(contactInfoPhoneCommand?.id)
                        }
                    }

                    bordersSecurityCoordinationInstance.save(failOnError: true, flush: true);


                    boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                    /**
                     * get  the workflow data
                     */
                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            bordersSecurityCoordinationInstance?.employee?.id + "",
                            bordersSecurityCoordinationInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                            bordersSecurityCoordinationInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                            bordersSecurityCoordinationInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                            BordersSecurityCoordination.getName(),
                            bordersSecurityCoordinationInstance?.id + "",
                            !hasHRRole)

                    /**
                     * save workflow path details & update request status
                     */
                    if (hasHRRole) {
                        workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                    }

                }


            } else {
                bordersSecurityCoordinationInstance.errors.reject('bordersSecurityCoordination.dateError.label')
                return bordersSecurityCoordinationInstance
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            bordersSecurityCoordinationInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            bordersSecurityCoordinationInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            bordersSecurityCoordinationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return bordersSecurityCoordinationInstance
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

            BordersSecurityCoordination instance = BordersSecurityCoordination.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('bordersSecurityCoordination.deleteMessage.label')
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
 * @return BordersSecurityCoordination.
 */
    @Transactional(readOnly = true)
    BordersSecurityCoordination getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return BordersSecurityCoordination.
     */
    @Transactional(readOnly = true)
    BordersSecurityCoordination getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList bordersSecurityCoordinationList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<DocumentTypeDTO> documentTypeDTOList
        List<BorderCrossingPointDTO> borderCrossingPointDTOList
        List<GovernorateDTO> governorateDTOList

        if (bordersSecurityCoordinationList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: bordersSecurityCoordinationList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get legal Identifier  name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: bordersSecurityCoordinationList?.resultList?.legalIdentifierId))
            documentTypeDTOList = documentTypeService?.searchDocumentType(searchBean)?.resultList

            /**
             * to get  border crossing point name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: bordersSecurityCoordinationList?.resultList?.borderLocationId))
            borderCrossingPointDTOList = borderCrossingPointService?.searchBorderCrossingPoint(searchBean)?.resultList

            //fill employee governorate information from core
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: bordersSecurityCoordinationList?.resultList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList


            bordersSecurityCoordinationList?.each { BordersSecurityCoordination bordersSecurityCoordination ->
                bordersSecurityCoordination.transientData = [:]

                /**
                 * assign personDTO for employee
                 */
                bordersSecurityCoordination.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == bordersSecurityCoordination?.employee?.personId
                })
                /**
                 * assign documentTypeDTO for employee
                 */
                bordersSecurityCoordination?.transientData?.put("documentTypeDTO", documentTypeDTOList?.find {
                    it?.id == bordersSecurityCoordination?.legalIdentifierId
                })
                /**
                 * assign borderCrossingPointDTO for employee
                 */
                bordersSecurityCoordination?.transientData?.put("borderCrossingPointDTO", borderCrossingPointDTOList?.find {
                    it?.id == bordersSecurityCoordination?.borderLocationId
                })

                /**
                 * assign for governorateDTO  for employee
                 */
                bordersSecurityCoordination?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == bordersSecurityCoordination?.employee?.currentEmploymentRecord?.department?.governorateId
                })

            }




            return bordersSecurityCoordinationList
        }

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
        String nameProperty = params["nameProperty"] ?: "transientData.borderCrossingPointDTO.descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = searchWithRemotingValues(params)
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}