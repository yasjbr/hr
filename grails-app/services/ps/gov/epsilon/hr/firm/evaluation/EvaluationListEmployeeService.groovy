package ps.gov.epsilon.hr.firm.evaluation

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

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
class EvaluationListEmployeeService {

    MessageSource messageSource
    def formatService
    EmployeeEvaluationService employeeEvaluationService
    EvaluationListService evaluationListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static requestEncodedId = { cService, EvaluationListEmployee rec, object, params ->
        if (rec?.employeeEvaluation) {
            return rec?.employeeEvaluation?.encodedId
        } else {
            return ""
        }
    }

    /**
     * this closure is used to return the employee name + promotion
     */
    public static getEmployeeToString = { cService, EvaluationListEmployee rec, object, params ->
        if (rec?.employeeEvaluation?.employee) {
            return rec?.employeeEvaluation?.employee?.toString()
        } else {
            return ""

        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeEvaluation.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employeeEvaluation.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.evaluationResult", type: "EvaluationCriterium", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeEvaluation.evaluationSum", type: "Double", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.evaluationTemplate", type: "EvaluationTemplate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS  = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeEvaluation.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employeeEvaluation.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.evaluationResult", type: "EvaluationCriterium", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeEvaluation.evaluationSum", type: "Double", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.evaluationTemplate", type: "EvaluationTemplate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeEvaluation.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
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
        String employeeEvaluationId = params["employeeEvaluation.id"]
        String evaluationListId = params["evaluationList.id"]
        Set evaluationListEmployeeNotesIds = params.listString("evaluationListEmployeeNotes.id")
        Long firmId = params.long("firm.id")?params.long("firm.id"):PCPSessionUtils.getValue("firmId")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String status = params["status"]

        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]

        String evaluationResultId = params["evaluationResult.id"]

        Double evaluationSum = params.double("evaluationSum")
        String evaluationTemplateId = params["evaluationTemplate.id"]

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        return EvaluationListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (employeeEvaluationId) {
                    eq("employeeEvaluation.id", employeeEvaluationId)
                }
                if (evaluationListId) {
                    eq("evaluationList.id", evaluationListId)
                }
                if (evaluationListEmployeeNotesIds) {
                    evaluationListEmployeeNotes {
                        inList("id", evaluationListEmployeeNotesIds)
                    }
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                employeeEvaluation{
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
                    if (evaluationResultId) {
                        eq("evaluationResult.id", evaluationResultId)
                    }
                    if (evaluationSum) {
                        eq("evaluationSum", evaluationSum)
                    }
                    if (evaluationTemplateId) {
                        eq("evaluationTemplate.id", evaluationTemplateId)
                    }
                    if (fromDate) {
                        eq("fromDate", fromDate)
                    }
                    if (fromDateFrom) {
                        ge("fromDate", fromDateFrom)
                    }
                    if (fromDateTo) {
                        le("fromDate", fromDateTo)
                    }
                    if (toDate) {
                        eq("toDate", toDate)
                    }
                    if (toDateFrom) {
                        ge("toDate", toDateFrom)
                    }
                    if (toDateTo) {
                        le("toDate", toDateTo)
                    }
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
                if(columnName.contains(".")){
                    switch (columnName){
                        case "employeeEvaluation.employee":
                            employeeEvaluation {
                                employee{
                                    order("id", dir)
                                }
                            }
                            break;
                        case "employeeEvaluation.evaluationSum":
                            employeeEvaluation {
                                order("evaluationSum", dir)
                            }
                            break;
                        case "employeeEvaluation.evaluationResult":
                            employeeEvaluation {
                                evaluationResult {
                                    order("id", dir)
                                }
                            }
                            break;
                        case "employeeEvaluation.evaluationTemplate":
                            employeeEvaluation {
                                evaluationTemplate {
                                    order("id", dir)
                                }
                            }
                            break;
                        case "employeeEvaluation.id":
                            employeeEvaluation {
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        case "employeeEvaluation.fromDate":
                            employeeEvaluation {
                                order("fromDate", dir)
                            }
                            break;
                        case "employeeEvaluation.toDate":
                            employeeEvaluation {
                                order("toDate", dir)
                            }
                            break;
                        default:
                            order(columnName, dir)
                            break;
                    }
                }else {
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'id':
                            order("trackingInfo.dateCreatedUTC", dir)
                            break;
                        default:
                            order(columnName, dir)
                    }
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
        pagedResultList?.resultList?.each { EvaluationListEmployee evaluationListEmployee ->
            GrailsParameterMap employeeEvaluationParam = new GrailsParameterMap([id: evaluationListEmployee?.employeeEvaluation?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            evaluationListEmployee?.employeeEvaluation = employeeEvaluationService.getInstanceWithRemotingValues(employeeEvaluationParam)
        }
        return pagedResultList
    }



    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EvaluationListEmployee.
     */
//    EvaluationListEmployee save(GrailsParameterMap params) {
//        EvaluationListEmployee evaluationListEmployeeInstance
//
//        /**
//         * in case: id is encoded
//         */
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//
//
//        if (params.id) {
//            evaluationListEmployeeInstance = EvaluationListEmployee.get(params["id"])
//            if (params.long("version")) {
//                long version = params.long("version")
//                if (evaluationListEmployeeInstance.version > version) {
//                    evaluationListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('evaluationListEmployee.label', null, 'evaluationListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this evaluationListEmployee while you were editing")
//                    return evaluationListEmployeeInstance
//                }
//            }
//            if (!evaluationListEmployeeInstance) {
//                evaluationListEmployeeInstance = new EvaluationListEmployee()
//                evaluationListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('evaluationListEmployee.label', null, 'evaluationListEmployee', LocaleContextHolder.getLocale())] as Object[], "This evaluationListEmployee with ${params.id} not found")
//                return evaluationListEmployeeInstance
//            }
//        } else {
//            evaluationListEmployeeInstance = new EvaluationListEmployee()
//        }
//        try {
//            evaluationListEmployeeInstance.properties = params;
//            evaluationListEmployeeInstance.save(failOnError: true);
//        }
//        catch (Exception ex) {
//            transactionStatus.setRollbackOnly()
//            evaluationListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
//        }
//        return evaluationListEmployeeInstance
//    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<EvaluationListEmployee> evaluationListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  evaluation list employee by list of ids
                 */
                evaluationListEmployeeList = EvaluationListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  evaluation list employee by list of ids
                 */
                evaluationListEmployeeList = EvaluationListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of evaluation request & revert status to APPROVED_BY_WORKFLOW
             */
            List<EmployeeEvaluation> employeeEvaluationList = evaluationListEmployeeList?.employeeEvaluation
            employeeEvaluationList?.each { EmployeeEvaluation employeeEvaluation ->
                employeeEvaluation?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }
            /**
             * delete list of evaluation list employee
             */
            if (evaluationListEmployeeList) {
                evaluationListEmployeeList*.delete()
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
     * @return EvaluationListEmployee.
     */
//    @Transactional(readOnly = true)
//    EvaluationListEmployee getInstance(GrailsParameterMap params) {
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//        //if id is not null then return values from search method
//        if (params.id) {
//            PagedResultList results = search(params)
//            if (results) {
//                return results[0]
//            }
//        }
//        return null
//
//    }

    /**
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
//    @Transactional(readOnly = true)
//    JSON autoComplete(GrailsParameterMap params) {
//        List<Map> dataList = []
//        String idProperty = params["idProperty"] ?: "id"
//        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
//        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
//        try {
//            grails.gorm.PagedResultList resultList = this.search(params)
//            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
//        } catch (Exception ex) {
//            ex.printStackTrace()
//        }
//        return dataList as JSON
//    }

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
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["evaluationList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        EvaluationList evaluationList = evaluationListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = evaluationList?.code
        map.coverLetter = evaluationList?.coverLetter
        map.details = resultList
        return [map]
    }

}