package ps.gov.epsilon.aoc.correspondences

import grails.gorm.PagedResultList
import grails.util.Holders
import grails.util.TypeConvertingMap
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumDeliveryStatus
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.ProvinceLocationService
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationService
import ps.police.notifications.enums.UserTerm
import ps.police.security.UserService

import java.time.ZonedDateTime

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
class AocCorrespondenceListService {

    MessageSource messageSource
    def formatService
    WorkFlowProcessService workFlowProcessService
    NotificationService notificationService
    UserService userService
    AocCorrespondenceListPartyService aocCorrespondenceListPartyService
    AocListRecordService aocListRecordService
    ProvinceLocationService provinceLocationService
    CorrespondenceListService correspondenceListService
    AocCorrespondenceNotificationService aocCorrespondenceNotificationService
    FirmSettingService firmSettingService

    private static final List<String> supportedDomains = EnumCorrespondenceType.values().hrListDomain

    public static final String DEFAULT_SERIAL_NUMBER = "TBD"

    //to get the Employee
    public static getEnumCurrentStatus = { cService, AocCorrespondenceList rec, object, params ->
        return rec.currentStatus.toString()
    }

    /**
     * this used to check if the user can delete the list or can not.
     */
    public static canDeleteList = { cService, AocCorrespondenceList rec, object, params ->
        if (rec?.currentStatus == EnumCorrespondenceStatus.CREATED && rec?.joinedCorrespondenceListRecords?.size() == 0) {
            return true
        }
        return false
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "threadId", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "code", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "deliveryDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "outgoingDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "outgoingSerial", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "sendingParty.name", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "incomingDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "incomingSerial", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "receivingParty.name", type: "string", source: 'domain'],

            [sort: false, search: true, hidden: false, name: "listRecordCount", type: "int", source: 'domain'],

            [sort: true, search: true, hidden: false, name: "currentStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "correspondenceDirection", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "correspondenceType", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "enumCurrentStatus", type: getEnumCurrentStatus, source: 'domain'],
            [sort: false, search: true, hidden: true, name: "canDeleteList", type: canDeleteList, source: 'domain'],
    ]

    public static final List<String> DOMAIN_COLUMNS_WORKFLOW = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "deliveryDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "correspondenceDirection", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "correspondenceType", type: "enum", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "receivingParty.name", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "sendingParty.name", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "incomingSerial", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "outgoingSerial", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "currentStatus", type: "enum", source: 'domain']
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
        Long id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")

        ZonedDateTime archivingDate = PCPUtils.parseZonedDateTime(params['archivingDate'])
        ZonedDateTime archivingDateFrom = PCPUtils.parseZonedDateTime(params['archivingDateFrom'])
        ZonedDateTime archivingDateTo = PCPUtils.parseZonedDateTime(params['archivingDateTo'])

        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection correspondenceDirection = params["correspondenceDirection"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.valueOf(params["correspondenceDirection"]) : null
        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType correspondenceType = params["correspondenceType"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.valueOf(params["correspondenceType"]) : null
        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus currentStatus = params["currentStatus"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus.valueOf(params["currentStatus"]) : null
        String deliveredBy = params["deliveredBy"]

        ZonedDateTime deliveryDate = PCPUtils.parseZonedDateTime(params['deliveryDate'])
        ZonedDateTime deliveryDateFrom = PCPUtils.parseZonedDateTime(params['deliveryDateFrom'])
        ZonedDateTime deliveryDateTo = PCPUtils.parseZonedDateTime(params['deliveryDateTo'])


        ZonedDateTime outgoingDate = PCPUtils.parseZonedDateTime(params['outgoingDate'])
        ZonedDateTime outgoingDateFrom = PCPUtils.parseZonedDateTime(params['outgoingDateFrom'])
        ZonedDateTime outgoingDateTo = PCPUtils.parseZonedDateTime(params['outgoingDateTo'])

        Long hrCorrespondenceListId = params.long("hrCorrespondenceList.id")
        String notes = params["notes"]
        String receivedBy = params["receivedBy"]

        EnumCorrespondencePartyClass receivingPartyClass = params["${EnumCorrespondencePartyType.TO}.partyClass"] ?
                EnumCorrespondencePartyClass.valueOf(params["${EnumCorrespondencePartyType.TO}.partyClass"]) : null
        Long receivingParty = receivingPartyClass ? params.long("${EnumCorrespondencePartyType.TO}.${receivingPartyClass}Id") : null

        EnumCorrespondencePartyClass sendingPartyClass = params["${EnumCorrespondencePartyType.FROM}.partyClass"] ?
                EnumCorrespondencePartyClass.valueOf(params["${EnumCorrespondencePartyType.FROM}.partyClass"]) : null
        Long sendingParty = sendingPartyClass ? params.long("${EnumCorrespondencePartyType.FROM}.${sendingPartyClass}Id") : null

        String serialNumber = params["serialNumber"]
        String name = params["named"]
        String code = params["code"]

        return AocCorrespondenceList.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("deliveredBy", sSearch)
                    ilike("notes", sSearch)
                    ilike("receivedBy", sSearch)
                    ilike("serialNumber", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (archivingDate) {
                    le("archivingDate", archivingDate)
                }
                if (archivingDateTo) {
                    le("archivingDate", archivingDateTo)
                }
                if (archivingDateFrom) {
                    ge("archivingDate", archivingDateFrom)
                }


                if (correspondenceDirection) {
                    eq("correspondenceDirection", correspondenceDirection)
                }
                if (correspondenceType) {
                    eq("correspondenceType", correspondenceType)
                }
                if (currentStatus) {
                    eq("currentStatus", currentStatus)
                }
                if (deliveredBy) {
                    ilike("deliveredBy", "%${deliveredBy}%")
                }
                if (deliveryDate) {
                    le("archivingDate", deliveryDate)
                }
                   if (deliveryDateFrom) {
                    ge("deliveryDate", deliveryDateFrom)
                }
                   if (deliveryDateTo) {
                    le("deliveryDate", deliveryDateTo)
                }


                if (outgoingDate) {
                    le("archivingDate", outgoingDate)
                }
                   if (outgoingDateFrom) {
                    ge("archivingDate", outgoingDateFrom)
                }
                   if (outgoingDateTo) {
                    le("archivingDate", outgoingDateTo)
                }






                if (hrCorrespondenceListId) {
                    joinedAocHrCorrespondenceLists {
                        eq("hrCorrespondenceList.id", hrCorrespondenceListId)
                    }
                }

                if (notes) {
                    ilike("notes", "%${notes}%")
                }
                if (receivedBy) {
                    ilike("receivedBy", "%${receivedBy}%")
                }
                if (receivingParty || receivingPartyClass) {
                    correspondenceListParties {
                        eq("partyType", EnumCorrespondencePartyType.TO)
                        eq("partyClass", receivingPartyClass)
                        if (receivingParty) {
                            eq("partyId", receivingParty)
                        }
                    }
                }
                if (sendingParty || sendingPartyClass) {
                    correspondenceListParties {
                        eq("partyType", EnumCorrespondencePartyType.FROM)
                        eq("partyClass", sendingPartyClass)
                        if (sendingParty) {
                            eq("partyId", sendingParty)
                        }
                    }
                }
                if (serialNumber) {
                    ilike("serialNumber", "%${serialNumber}%")
                }
                if (name) {
                    ilike("name", "${name}%")
                }

                if (code) {
                    eq("code", "${code}")
                }
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
     * to search model entries for workflow
     * @param GrailsParameterMap params.
     * @return PagedList.
     */
    PagedList SearchCorrespondenceInWorkflow(GrailsParameterMap params) {
        if (!PCPSessionUtils.getValue("jobTitleId") || !PCPSessionUtils.getValue("departmentId")) {
            throw new Exception("logged employee should have a job title and should belong to a department ")
        }

        /**
         * global settings
         */
        Integer max = params.int('max') ?: 1
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"] ?: 'asc'
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS_WORKFLOW[column]?.name
        } else {
            columnName = "id"
        }

        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = HashHelper.decode(params.encodedId)
        } else {
            //in case id is not encoded
            id = params["id"]
        }

        ZonedDateTime archivingDateFrom = PCPUtils.parseZonedDateTime(params['archivingDateFrom'])
        ZonedDateTime archivingDateTo = PCPUtils.parseZonedDateTime(params['archivingDateTo'])

        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection correspondenceDirection = params["correspondenceDirection"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.valueOf(params["correspondenceDirection"]) : null
        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType correspondenceType = params["correspondenceType"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.valueOf(params["correspondenceType"]) : null
        ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus currentStatus = params["currentStatus"] ? ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus.valueOf(params["currentStatus"]) : null
        String deliveredBy = params["deliveredBy"]
        ZonedDateTime deliveryDate = PCPUtils.parseZonedDateTime(params['deliveryDate'])

        String notes = params["notes"]
        String receivedBy = params["receivedBy"]

        String serialNumber = params["serialNumber"]

        EnumWorkflowStatus workflowStatus = params.workflowStatus ? EnumWorkflowStatus.valueOf(params.workflowStatus) : null

        List workflowStatusList = (workflowStatus == null) ? [EnumWorkflowStatus.NOT_SEEN, EnumWorkflowStatus.WAIT_FOR_APPROVAL] : [workflowStatus]

        Map hqlMap = [:]
        String hqlQueryOrderBy

        /**
         * HQL query
         */

        StringBuilder hqlQuery = new StringBuilder(" From AocCorrespondenceList aocList  ")
        hqlQuery << " where aocList.currentStatus='${currentStatus}' "
        hqlQuery << " AND aocList.id in ( "
        hqlQuery << " select cast(wfd.workflowPathHeader.objectId as long) from WorkflowPathDetails wfd "
        hqlQuery << " where wfd.toJobTitle= :toJobTitle  "
        hqlQuery << " AND wfd.toNode= :toNode  "
        hqlQuery << " AND wfd.workflowStatus in (:workflowStatusList) )"

        /**
         * this parameter used every run of hql query.
         */
        hqlMap.put('toJobTitle', PCPSessionUtils.getValue("jobTitleId"))
        hqlMap.put('toNode', PCPSessionUtils.getValue("departmentId"))
        hqlMap.put('workflowStatusList', workflowStatusList)

        /**
         * add the search parameters to hql query when it is exist.
         */
        if (ids) {
            hqlQuery << " AND aocList.id in :ids "
            hqlMap.put("ids", ids)
        }
        if (id) {
            hqlQuery << " AND aocList.id = :id "
            hqlMap.put("id", id)
        }


        if (correspondenceType) {
            hqlQuery << " AND aocList.correspondenceType = :correspondenceType "
            hqlMap.put("correspondenceType", correspondenceType)
        }

        if (archivingDateFrom) {
            hqlQuery << " AND aocList.archivingDate >= :archivingDateFrom "
            hqlMap.put("archivingDateFrom", archivingDateFrom)
        }

        if (archivingDateTo) {
            hqlQuery << " AND aocList.archivingDate <= :archivingDateTo "
            hqlMap.put("archivingDateTo", archivingDateTo)
        }

        if (correspondenceDirection) {
            hqlQuery << " AND aocList.correspondenceDirection = :correspondenceDirection "
            hqlMap.put("correspondenceDirection", correspondenceDirection)
        }
        /**
         * tracking info status,
         * by default Active
         */

        hqlQuery << " AND aocList.trackingInfo.status = '${GeneralStatus.ACTIVE}' "

        /**
         * sort by column name with direction
         */
        hqlQueryOrderBy = " order by " + columnName + " " + dir

        List<AocCorrespondenceList> correspondenceList = []
        //get the count of records
        ArrayList<Long> countInfo = Request.executeQuery("select count(*) " + hqlQuery.toString(), hqlMap)

        if (countInfo?.get(0) > 0) {
            hqlMap.put('offset', offset)
            hqlMap.put('max', max)

            /**
             * get the all requests need approval
             */
            correspondenceList = AocCorrespondenceList.executeQuery(hqlQuery.toString() + hqlQueryOrderBy, hqlMap)
        }

        /** Search remoting values for parties list **/
        correspondenceList?.each { AocCorrespondenceList list ->
            list.correspondenceListParties = aocCorrespondenceListPartyService.searchWithRemotingValues(list.correspondenceListParties.toList(), params)
        }

        /**
         * return the paged result list
         */
        return new PagedList(resultList: correspondenceList, totalCount: countInfo?.get(0))
    }


    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList list = search(params)
        list?.resultList?.each { AocCorrespondenceList correspondenceList ->
            correspondenceList.correspondenceListParties = aocCorrespondenceListPartyService.searchWithRemotingValues(correspondenceList.correspondenceListParties.toList(), params)
        }
        return list
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return AocCorrespondenceList.
 */
    AocCorrespondenceList save(TypeConvertingMap params) {
        AocCorrespondenceList aocCorrespondenceListInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            aocCorrespondenceListInstance = AocCorrespondenceList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (aocCorrespondenceListInstance.version > version) {
                    aocCorrespondenceListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('aocCorrespondenceList.label', null, 'aocCorrespondenceList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this aocCorrespondenceList while you were editing")
                    return aocCorrespondenceListInstance
                }
            }
            if (!aocCorrespondenceListInstance) {
                aocCorrespondenceListInstance = new AocCorrespondenceList()
                aocCorrespondenceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocCorrespondenceList.label', null, 'aocCorrespondenceList', LocaleContextHolder.getLocale())] as Object[], "This aocCorrespondenceList with ${params.id} not found")
                return aocCorrespondenceListInstance
            }
        } else {
            aocCorrespondenceListInstance = new AocCorrespondenceList()
        }
        AocCorrespondenceList.withTransaction {
            try {

                //remove records from requisition work experience table when edit
                if (aocCorrespondenceListInstance?.id) {
                    List copyToPartyIds = aocCorrespondenceListInstance?.copyToPartyList?.id
                    if (copyToPartyIds) {
                        AocCorrespondenceListParty.executeUpdate('delete from AocCorrespondenceListParty p where p.id in (:copyToPartyIds)', ['copyToPartyIds': copyToPartyIds])
                    }
                }

                aocCorrespondenceListInstance.properties = params;
                Long otherFirmId = null

                if (!aocCorrespondenceListInstance.id) {
                    Firm aocFirm = Firm.findByCode('AOC')
                    // Add party related to AOC
                    AocCorrespondenceListParty aocParty = new AocCorrespondenceListParty()
                    aocParty.partyId = aocFirm.id
                    aocParty.partyClass = EnumCorrespondencePartyClass.FIRM

                    // set sending and receiving parties
                    if (aocCorrespondenceListInstance.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
                        aocParty.partyType = EnumCorrespondencePartyType.TO
                    } else {
                        aocParty.partyType = EnumCorrespondencePartyType.FROM
                    }
                    aocParty.correspondenceList = aocCorrespondenceListInstance
                    aocCorrespondenceListInstance.addToCorrespondenceListParties(aocParty)

                    // Add Party related to the other side
                    AocCorrespondenceListParty otherParty = new AocCorrespondenceListParty()
                    otherParty.partyType = EnumCorrespondencePartyType.valueOf(params.partyType)
                    otherParty.partyClass = EnumCorrespondencePartyClass.valueOf(params[params.partyType + '.partyClass'])
                    otherParty.partyId = params.long(params.partyType + '.' + otherParty.partyClass?.toString() + 'Id')
                    otherParty.correspondenceList = aocCorrespondenceListInstance
                    aocCorrespondenceListInstance.addToCorrespondenceListParties(otherParty)

                    if (otherParty.partyClass == EnumCorrespondencePartyClass.FIRM) {
                        otherFirmId = otherParty.partyId
                    }
                }

                // link aoc list to hr list
                if (!aocCorrespondenceListInstance?.joinedAocHrCorrespondenceLists || aocCorrespondenceListInstance.joinedAocHrCorrespondenceLists.isEmpty()) {
                    if (otherFirmId) {
                        // save hr correspondence list
                        aocListRecordService.saveHrCorrespondenceForFirm(aocCorrespondenceListInstance, otherFirmId, params['hrCorrespondenceList.id'])
                    }
                }

                /**
                 * in CREATED phase: create new status for the list
                 */
                AocCorrespondenceListStatus correspondenceListStatus
                if (!params.id) {
                    //when create the list , its is CREATED phase:
                    correspondenceListStatus = new AocCorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                            correspondenceStatus: aocCorrespondenceListInstance.currentStatus, correspondenceList: aocCorrespondenceListInstance)
                    aocCorrespondenceListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
                }

                // set threadId for children
                if (aocCorrespondenceListInstance.parentCorrespondenceList) {
                    aocCorrespondenceListInstance.threadId = aocCorrespondenceListInstance.parentCorrespondenceList.threadId
                }

                // add copy to party list
                //check if the RequisitionWorkExperience list contains data
                List copyParties = params.listString("partyTypeCopy")
                List copyPartyClasses = params.listString("partyClassCopy")
                List copyPartyIds = params.list("partyIdCopy")

                //loop on the list and create RequisitionWorkExperience instance
                if (copyParties) {

                    EnumCorrespondencePartyClass partyClass = null
                    Long partyId = null

                    copyParties.eachWithIndex { party, index ->

                        if (copyPartyClasses[index] != null && copyPartyClasses[index] != "" && copyPartyClasses[index] != "null") {
                            partyClass = EnumCorrespondencePartyClass.valueOf(copyPartyClasses[index])
                        }

                        if (copyPartyIds[index] != null && copyPartyIds[index] != "" && copyPartyIds[index] != "null") {
                            partyId = copyPartyIds[index] as long
                        }

                        if (partyClass || partyId) {
                            aocCorrespondenceListInstance.addToCorrespondenceListParties(new AocCorrespondenceListParty(
                                    partyType: EnumCorrespondencePartyType.COPY, partyClass: partyClass, partyId: partyId,
                                    correspondenceList: aocCorrespondenceListInstance))
                        }
                    }
                }
                if (aocCorrespondenceListInstance.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
                    // set delivery status to delivered for original hr lists
                    for (CorrespondenceList hrList : aocCorrespondenceListInstance.joinedAocHrCorrespondenceLists?.hrCorrespondenceList) {
                        if (hrList.deliveryStatus == EnumDeliveryStatus.NOT_DELIVERED) {
                            hrList.deliveryStatus = EnumDeliveryStatus.REQUEST_DELIVERED
                            hrList.save()
                        }
                    }
                }
                aocCorrespondenceListInstance.save(failOnError: true);
            } catch (Exception ex) {
                log.error("Failed to save correspondenceList", ex)
                transactionStatus.setRollbackOnly()
                aocCorrespondenceListInstance.errors.reject('default.internal.server.error', [ex?.message] as Object[], "")
            }
        }
        return aocCorrespondenceListInstance
    }

    /**
     * to change status of the correspondence.
     * @param GrailsParameterMap params the search map.
     * @return AocCorrespondenceList.
     */
    AocCorrespondenceList changeStatus(GrailsParameterMap params) {
        AocCorrespondenceList aocCorrespondenceListInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            aocCorrespondenceListInstance = AocCorrespondenceList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (aocCorrespondenceListInstance.version > version) {
                    aocCorrespondenceListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('aocCorrespondenceList.label', null, 'aocCorrespondenceList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this aocCorrespondenceList while you were editing")
                    return aocCorrespondenceListInstance
                }
            }
            if (!aocCorrespondenceListInstance) {
                aocCorrespondenceListInstance = new AocCorrespondenceList()
                aocCorrespondenceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocCorrespondenceList.label', null, 'aocCorrespondenceList', LocaleContextHolder.getLocale())] as Object[], "This aocCorrespondenceList with ${params.id} not found")
                return aocCorrespondenceListInstance
            }
        } else {
            if (!aocCorrespondenceListInstance) {
                aocCorrespondenceListInstance = new AocCorrespondenceList()
                aocCorrespondenceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocCorrespondenceList.label', null, 'aocCorrespondenceList', LocaleContextHolder.getLocale())] as Object[], "This aocCorrespondenceList with ${params.id} not found")
                return aocCorrespondenceListInstance
            }
        }

        try {
            EnumCorrespondenceStatus correspondenceStatus = params.correspondenceStatus ? EnumCorrespondenceStatus.valueOf(params.correspondenceStatus) : null
            if (!correspondenceStatus) {
                throw new Exception("correspondence status is mandatory")
            }
            if (correspondenceStatus == aocCorrespondenceListInstance.currentStatus) {
                throw new Exception("correspondence status not changed")
            }
            aocCorrespondenceListInstance.currentStatus = correspondenceStatus

            aocCorrespondenceListInstance.save(flush: true, failOnError: true)
        } catch (Exception ex) {
            log.error("Failed to change status - $ex.message")
            aocCorrespondenceListInstance.errors.reject('default.internal.server.error', [ex?.message] as Object[], "")
        }
        return aocCorrespondenceListInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                AocCorrespondenceList.findAllByIdInListAndCurrentStatusInList(HashHelper.decodeList(deleteBean.ids),
                        [EnumCorrespondenceStatus.NEW, EnumCorrespondenceStatus.CREATED])*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                AocCorrespondenceList.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return AocCorrespondenceList.
 */
    @Transactional(readOnly = true)
    AocCorrespondenceList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
            params.remove("encodedId")
        }
        //if id is not null then return values from search method
        if (params.id) {
            params.id = Long.parseLong(params.id.toString())
            PagedResultList results = search(params)
            if (results) {
                AocCorrespondenceList aocCorrespondenceList = results[0]
                if (params.boolean('withRemotingValues', true)) {
                    aocCorrespondenceList?.correspondenceListParties =
                            aocCorrespondenceListPartyService.searchWithRemotingValues(aocCorrespondenceList?.correspondenceListParties?.toList(), params)
                }
                if (aocCorrespondenceList?.provinceLocation) {
                    GrailsParameterMap paramMap = new GrailsParameterMap([id: aocCorrespondenceList?.provinceLocation?.id],
                            WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                    aocCorrespondenceList.provinceLocation = provinceLocationService?.searchWithRemotingValues(paramMap)?.resultList?.get(0)
                }
                //this set of encodedId was added to be used in saved patch (back button)
                params["encodedId"] = aocCorrespondenceList?.encodedId
                return aocCorrespondenceList
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

    public boolean startWorkflow(AocCorrespondenceList aocCorrespondenceList) {
        if (getManageListActionViewMatrix(aocCorrespondenceList, true).startWorkflow) {
            try {
                workFlowProcessService.generateRequestWorkflowPath("", null, null, null,
                        AocCorrespondenceList.getName(), aocCorrespondenceList?.id + "", true)
                return true
            } catch (WorkflowNotValidActionException nve) {
                log.error("workflow user action is not valid", nve)
                transactionStatus.setRollbackOnly()
            } catch (WorkflowNotSavedException nse) {
                log.error("Workflow not saved", nse)
                transactionStatus.setRollbackOnly()
            } catch (Exception ex) {
                log.error("Failed to start workflow", ex)
                transactionStatus.setRollbackOnly()
            }
        }
        return false
    }

    public Map sendList(AocCorrespondenceList aocCorrespondenceList, Boolean isManagerial = false) {
        Map sendResult = [:]
        AocCorrespondenceList.withTransaction {
            try {
                Map permittedActions = getManageListActionViewMatrix(aocCorrespondenceList, true)
                if (permittedActions.sendList) {
                    // make sure list can be sent as managerial list
                    if (isManagerial && permittedActions.sendManagerialList == true) {

                        aocCorrespondenceList.joinedAocHrCorrespondenceLists.hrCorrespondenceList?.each { CorrespondenceList hrList ->

                            log.debug("updating hr record statuses")
                            // update status of rejected hrRecords to rejected, others to approved
                            try {
                                aocListRecordService.updateListRecordStatus(aocCorrespondenceList.id,
                                        aocCorrespondenceList.correspondenceType, aocCorrespondenceList.serialNumber)
                            } catch (Exception ex) {
                                sendResult['failMessage'] = ex.message
                                throw ex
                            }

                            log.debug("update process finished successfully")

                            // if firm is centralized with AOC, notify firm hr to receive the response, otherwise close the hr ist
                            Boolean centralizedWithAOC = firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.value,
                                    hrList.firm.id)?.toBoolean()

                            if (centralizedWithAOC) {
                                correspondenceListService.createReceiveListNotification(hrList)
                            } else {
                                GrailsParameterMap parameterMap = new GrailsParameterMap([id: hrList.id],
                                        WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                                parameterMap.fromDate = (new Date()).format("dd/MM/yyyy")
                                parameterMap.encodedId = hrList.encodedId
                                log.debug("closing hr list with params = $parameterMap")
                                hrList = aocListRecordService.getCorrespondenceListService(aocCorrespondenceList.correspondenceType).closeHrList(parameterMap)
                                log.debug("hrlist close operation finished, errors = " + hrList?.errors)
                                if (hrList?.hasErrors()) {
                                    throw new ValidationException("Failed to close hr list", hrList.errors)
                                }
                            }
                        }
                    }
                    aocCorrespondenceList.currentStatus = EnumCorrespondenceStatus.SUBMITTED
                    aocCorrespondenceList.save(failOnError: true)
                    sendResult['success'] = true
                } else {
                    throw new Exception("list ${aocCorrespondenceList.id} cannot be submitted")
                }
            } catch (Exception ex) {
                log.error("Failed to submit list", ex)
                transactionStatus.setRollbackOnly()
                sendResult['success'] = false
            }
        }

        return sendResult
    }

    /**
     * Calculates actions matrix for manageList view
     * @param aocCorrespondenceList
     * @return
     */
    Map getManageListActionViewMatrix(AocCorrespondenceList aocCorrespondenceList, Boolean checkRecordsSize = false) {
        Map matrix = [:]
        matrix.addRecord = false
        matrix.createRecord = false
        matrix.startWorkflow = false
        matrix.sendList = false
        matrix.sendManagerialList = false
        matrix.editList = false
        matrix.createRelatedOugoingList = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING
        matrix.createRelatedIncomingList = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.OUTGOING
        matrix.setAsFinished = false

        Long firmRelatedId = aocCorrespondenceList.hrFirmId

        Boolean centralizedWithAOC = firmRelatedId && firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.value,
                firmRelatedId)?.toBoolean()
        Boolean isRoot = aocCorrespondenceList.parentCorrespondenceList == null

        if (aocCorrespondenceList.currentStatus in [EnumCorrespondenceStatus.CREATED]) {
            matrix.addRecord = true
            matrix.editList = true
            matrix.createRecord = !centralizedWithAOC && isRoot
            matrix.sendList = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.OUTGOING
            matrix.startWorkflow = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING && isRoot
            matrix.setAsFinished = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING && !isRoot

            if (checkRecordsSize && matrix.sendList) {
                matrix.sendList = matrix.sendList && aocCorrespondenceList.joinedCorrespondenceListRecords?.size() > 0
            }
            // send managerial is possible when all the following conditions are met:
            // list is in created status,
            // list direction is outgoing
            // parent list is approved, partially approved or rejected
            // related hr lists are submitted, received or partially closed
            matrix.sendManagerialList = matrix.sendList && aocCorrespondenceList?.parentCorrespondenceList?.currentStatus in
                    [EnumCorrespondenceStatus.APPROVED, EnumCorrespondenceStatus.PARTIALLY_APPROVED, EnumCorrespondenceStatus.REJECTED]
            if (checkRecordsSize && matrix.sendManagerialList) {
                matrix.sendManagerialList = matrix.sendManagerialList &&
                        aocCorrespondenceList.joinedAocHrCorrespondenceLists?.hrCorrespondenceList?.currentStatus?.correspondenceListStatus?.findAll {
                            it in [EnumCorrespondenceListStatus.SUBMITTED, EnumCorrespondenceListStatus.RECEIVED, EnumCorrespondenceListStatus.PARTIALLY_CLOSED]
                        }?.size() > 0
            }
        }

        if (aocCorrespondenceList.currentStatus in [EnumCorrespondenceStatus.NEW]) {
            matrix.editList = true
        }

        if (aocCorrespondenceList.currentStatus in [EnumCorrespondenceStatus.STOPPED]) {
            matrix.startWorkflow = aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING && isRoot
        }


        return matrix
    }

    public WorkflowPathHeader getWorkflowPathHeader(GrailsParameterMap params) {

        /**
         * get workflow path header by object id
         */
        WorkflowPathHeader workflowPathHeader = WorkflowPathHeader.createCriteria().get {
            eq('objectId', (params.objectId + ""))
            order('id', 'desc')
            setMaxResults(1)
        }

        if (workflowPathHeader?.workflowPathDetails?.size() > 0) {
            /**
             * get list of job title by list of id
             */
            List<JobTitle> jobTitleList = JobTitle.createCriteria().list {
                inList('id', workflowPathHeader?.workflowPathDetails?.toJobTitle?.toList())
            }

            /**
             * assign  job title name for each workflow path details
             */
            workflowPathHeader?.workflowPathDetails?.each { WorkflowPathDetails workflowPathDetails ->

                workflowPathDetails?.transientData?.put("toJobTitleName", jobTitleList.find {
                    it.id == workflowPathDetails?.toJobTitle
                })
            }
        }

        /**
         * return workflow path header
         */
        return workflowPathHeader
    }

    /***
     * Handles
     */
    public void handleSubmittedHrLists() {

        StringBuilder sbQuery = new StringBuilder()
        sbQuery << "select cl from CorrespondenceList cl inner join cl.correspondenceListStatuses cls "
        sbQuery << " where cl.deliveryStatus= :requestNotDelivered "
        sbQuery << " and cls.correspondenceListStatus=:submittedStatus and cls.toDate = :defaultDate "
        sbQuery << " order by cl.trackingInfo.createdBy "

        Map params = [submittedStatus: EnumCorrespondenceListStatus.SUBMITTED,
                      defaultDate    : PCPUtils.DEFAULT_ZONED_DATE_TIME, requestNotDelivered: EnumDeliveryStatus.NOT_DELIVERED]

        List<CorrespondenceList> unhandledHrLists = CorrespondenceList.executeQuery(sbQuery?.toString(), params)

        log.info("Delivering ${unhandledHrLists?.size()} hr lists to AOC")

        unhandledHrLists?.each { list ->
            try {
                log.debug("Delivering list ${list.id}")
                deliverHrListToAOC(list)
            } catch (Exception ex) {
                log.error("Failed to deliver list $list.id", ex)
            }
        }
    }

    private void deliverHrListToAOC(CorrespondenceList hrList) {
        String simpleClassName = hrList.class.simpleName
        boolean supported = supportedDomains.find { it.equalsIgnoreCase(simpleClassName) } != null
        if (!supported) {
            log.warn("${hrList?.class?.simpleName} not supported yet on AOC ")
            hrList.deliveryStatus = EnumDeliveryStatus.REQUEST_DELIVERED
            hrList.save()
            return
        }

        // if aoc correspondence is already exists for the hr list, return
        int count = AocCorrespondenceList.createCriteria().get {
            eq('correspondenceDirection', EnumCorrespondenceDirection.INCOMING)
            joinedAocHrCorrespondenceLists {
                eq('hrCorrespondenceList.id', hrList.id)
            }
            projections {
                count('id')
            }
        }
        if (count > 0) {
            log.info("HrList $hrList.id already delivered")
            return
        }
        EnumCorrespondenceType correspondenceType = calculateHrCorrespondenceType(hrList.class.name)
        TypeConvertingMap params = new TypeConvertingMap()
        params.hrCorrespondenceList = hrList.properties
        params['hrCorrespondenceList.id'] = hrList.id
        params.correspondenceType = correspondenceType?.value
        params.correspondenceDirection = EnumCorrespondenceDirection.INCOMING.toString()
        params.name = hrList.name
        params.coverLetter = hrList.coverLetter
        params.originalSerialNumber = hrList.manualOutgoingNo
        params.deliveryDate = (new Date()).format("dd/MM/yyyy")
        params.archivingDate = params.deliveryDate
        params.serialNumber = DEFAULT_SERIAL_NUMBER
        params.currentStatus = EnumCorrespondenceStatus.NEW.toString()

        params.partyType = EnumCorrespondencePartyType.FROM.toString()
        params[params.partyType + '.partyClass'] = EnumCorrespondencePartyClass.FIRM.toString()
        params[params.partyType + '.' + EnumCorrespondencePartyClass.FIRM.toString() + 'Id'] = hrList.firm?.id

        AocCorrespondenceList aocCorrespondenceListInstance = save(params)

        if (aocCorrespondenceListInstance.hasErrors()) {
            log.error(ValidationException.formatErrors(aocCorrespondenceListInstance.errors))
            throw new Exception("")
        }

        // create notification for persons have role
        // TODO change roles to groups
        List<String> roles = correspondenceType.roles.split(",")?.toList()
        List<UserTerm> userTerms = []
        roles.each {
            userTerms << UserTerm.ROLE
        }

        /**
         * Create notification action
         */
        Map notificationActionMap = [:]
        notificationActionMap['action'] = 'edit'
        notificationActionMap['controller'] = 'aocCorrespondenceList'
        notificationActionMap['label'] = 'aocCorrespondenceList.edit.label'
        notificationActionMap['icon'] = 'icon-pencil'
        notificationActionMap['isModal'] = false
        notificationActionMap['notificationParams'] = []
        notificationActionMap['notificationParams'] << ['name': 'id', 'value': aocCorrespondenceListInstance?.id]

        List<Map> notificationActionsList = [notificationActionMap]

        aocCorrespondenceNotificationService.createCorrespondenceNotification(aocCorrespondenceListInstance.id + '',
                aocCorrespondenceListInstance.class.name, aocCorrespondenceListInstance.deliveryDate, aocCorrespondenceListInstance.currentStatus,
                userTerms, roles, notificationActionsList, EnumNotificationType.LIST_MESSAGES)
    }

    private EnumCorrespondenceType calculateHrCorrespondenceType(String domain) {
        String controllerName = correspondenceListService.extractControllerName(domain)
        EnumCorrespondenceType type = null
        EnumCorrespondenceType.values().each { enumType ->
            if (enumType.hrListDomain.equals(controllerName)) {
                type = enumType
            }
        }
        return type
    }
}