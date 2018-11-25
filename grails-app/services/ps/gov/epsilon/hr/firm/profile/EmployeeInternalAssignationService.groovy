package ps.gov.epsilon.hr.firm.profile

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 *<h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class EmployeeInternalAssignationService {

    MessageSource messageSource
    def formatService
    PersonService personService
    def sessionFactory
    NotificationService notificationService
    RequestService requestService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */

    public static canEdit = { formatService, EmployeeInternalAssignation dataRow, object, params ->
        if (dataRow) {
            ZonedDateTime currentDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            if (dataRow.employmentRecord.toDate == null && (dataRow.assignedToDepartmentToDate == null || dataRow.assignedToDepartmentToDate > currentDate)) {
                return true
            } else {
                return false
            }
        }
        return false
    }


    public static getEmployeeId ={ formatService, EmployeeInternalAssignation dataRow, object, params->
        if(dataRow){
            return dataRow?.employee?.id?.toString()
        }
        return  ""
    }

    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartmentFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartmentToDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentRecord", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentRecord", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartmentFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToDepartmentToDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
            [sort: true, search: false, hidden: true, name: "employeeId", type: getEmployeeId, source: 'domain'],
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
        String employeeId = params["employee.id"]
        String employmentRecordId = params["employmentRecord.id"]
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        String assignedToDepartmentId = params["assignedToDepartment.id"]
        ZonedDateTime assignedToDepartmentFromDate = PCPUtils.parseZonedDateTime(params['assignedToDepartmentFromDate'])
        String note = params["note"]
        String status = params["status"]

        return EmployeeInternalAssignation.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (assignedToDepartmentId) {
                    eq("assignedToDepartment.id", assignedToDepartmentId)
                }
                if (assignedToDepartmentFromDate) {
                    le("assignedToDepartmentFromDate", assignedToDepartmentFromDate)
                }

                if (employeeId || employmentRecordId) {
                    employmentRecord {
                        if (employmentRecordId) {
                            eq("id", employmentRecordId)
                        }
                        if (employeeId) {
                            eq("employee.id", employeeId)
                        }
                    }
                }
                if (note) {
                    ilike("note", "%${note}%")
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
     * @return EmployeeInternalAssignation.
     */
    EmployeeInternalAssignation save(GrailsParameterMap params) {
        EmployeeInternalAssignation employeeInternalAssignationInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeeInternalAssignationInstance = EmployeeInternalAssignation.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeInternalAssignationInstance.version > version) {
                    employeeInternalAssignationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeInternalAssignation.label', null, 'employeeInternalAssignation', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeInternalAssignation while you were editing")
                    return employeeInternalAssignationInstance
                }
            }
            if (!employeeInternalAssignationInstance) {
                employeeInternalAssignationInstance = new EmployeeInternalAssignation()
                employeeInternalAssignationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeInternalAssignation.label', null, 'employeeInternalAssignation', LocaleContextHolder.getLocale())] as Object[], "This employeeInternalAssignation with ${params.id} not found")
                return employeeInternalAssignationInstance
            }
        } else {
            employeeInternalAssignationInstance = new EmployeeInternalAssignation()
        }
        try {
            employeeInternalAssignationInstance.properties = params;

            if (!employeeInternalAssignationInstance?.id) {
                def count = EmployeeInternalAssignation.createCriteria().count {
                    eq('assignedToDepartmentToDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                    employmentRecord {
                        eq('employee.id', params["employee.id"])
                    }

                }
                if (count > 0) {
                    employeeInternalAssignationInstance.errors.reject("employeeInternalAssignation.closeAllPrevious.message")
                    return employeeInternalAssignationInstance
                }

                //set employmentRecord as last record
                employeeInternalAssignationInstance.employmentRecord = EmploymentRecord.findByToDateAndEmployee(PCPUtils.getDEFAULT_ZONED_DATE_TIME(), Employee.load(params["employee.id"]))
            }

            employeeInternalAssignationInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!employeeInternalAssignationInstance?.hasErrors()) {
                employeeInternalAssignationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return employeeInternalAssignationInstance
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

            EmployeeInternalAssignation instance = EmployeeInternalAssignation.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('firm.deleteMessage.label')
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
     * @return EmployeeInternalAssignation.
     */
    @Transactional(readOnly = true)
    EmployeeInternalAssignation getInstance(GrailsParameterMap params) {
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeInternalAssignation.
     */
    @Transactional(readOnly = true)
    EmployeeInternalAssignation getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                EmployeeInternalAssignation employeeInternalAssignation = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employeeInternalAssignation?.employmentRecord?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBean)
                employeeInternalAssignation.employmentRecord.employee.transientData.put("personDTO", personDTO)

                return employeeInternalAssignation
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * get list of employeeInternalAssignation that expire within date interval
     * @params from date
     * @params to date
     * @return list of employeeInternalAssignation
     */
    private List getListOfEmployeeInternalAssignation(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {



        final session = sessionFactory.currentSession
        def list = []
        /**
         * write query to get employeeInternalAssignation that will
         * be expire within date interval
         */
        String query = "" +
                "SELECT b.id, " +
                "       b.status, " +
                "       b.assigned_to_department_to_date_datetime " +
                "FROM   employee_internal_assignation b " +
                "WHERE  b.employment_record_id IN (SELECT id " +
                "                                  FROM   employment_record " +
                "                                  WHERE  firm_id = :firmId) " +
                "       AND b.status = '${GeneralStatus.ACTIVE}' " +
                "       AND b.assigned_to_department_to_date_datetime != '0003-03-03 03:03:03' " +
                "       AND NOT EXISTS (SELECT object_source_id AS id " +
                "                       FROM   notification " +
                "                       WHERE  notification_type_id = ${EnumNotificationType.MY_NOTIFICATION.value} " +
                "                              AND object_source_reference = '${EmployeeInternalAssignation.getName()}'" +
                "                              AND object_source_id = b.id) " +
                "GROUP  BY b.id, " +
                "          b.status " +
                "HAVING Max(b.assigned_to_department_to_date_datetime) BETWEEN :fromDate AND :toDate  "

        //fill map parameter
        Map sqlParamsMap = [:]
        sqlParamsMap = [firmId  : firm?.id,
                        fromDate: PCPUtils.convertZonedDateTimeToTimeStamp(fromDate),
                        toDate  : PCPUtils.convertZonedDateTimeToTimeStamp(toDate)]

        /**
         * create query to get dispatch & dispatch extension request that will
         * be expire within date interval
         */
        Query sqlQuery = session.createSQLQuery(query)

        //assign value to each parameter

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //execute query
        final queryResults = sqlQuery?.list()

        //fill result into list

        queryResults?.eachWithIndex { def entry, int i ->
            list.add(entry)
        }

        //return query list

        return list
    }

    /**
     * get list of dispatch request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return boolean
     */
    public Boolean createEmployeeInternalAssignationNotification(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        //get employeeInternalAssignation for selected date's interval

        List resultList = getListOfEmployeeInternalAssignation(firm, fromDate, toDate).toList()

        GrailsParameterMap notificationParams
        //create notification for each employeeInternalAssignation in list

        MessageSource messageSource = formatService.messageSource
        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
        List<String> messageParamList = []
        List<Map> notificationActionsMap = null

        resultList?.eachWithIndex { entry, index ->

            userTermKeyList = []
            userTermValueList = []

            //notification actions.
            notificationActionsMap = [
                    [action            : "show",
                     controller        : "employeeInternalAssignation",
                     label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                     icon              : "icon-eye",

                     notificationParams: [
                             new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[0]}")),
                     ]
                    ]
            ]

            //params for notification text.
            messageParamList = ["${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

            //set notification role
            userTermKeyList.add(UserTerm.ROLE)
            userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

            //save notification.
            requestService?.createRequestNotification("${entry[0]}",
                    EmployeeInternalAssignation.getName(),
                    ZonedDateTime.now()?.minusDays(1),
                    null,
                    userTermKeyList,
                    userTermValueList,
                    notificationActionsMap,
                    EnumNotificationType.MY_NOTIFICATION,
                    "employeeInternalAssignation.notification.message",
                    messageParamList,
                    [withEmployeeName: true])
        }

        return true
    }

}