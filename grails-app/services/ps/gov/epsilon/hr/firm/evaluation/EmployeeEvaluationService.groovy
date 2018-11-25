package ps.gov.epsilon.hr.firm.evaluation

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationCriterium
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationItem
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationSection
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationTemplate
import ps.gov.epsilon.hr.firm.evaluation.lookups.JoinedEvaluationTemplateCategory
import ps.gov.epsilon.hr.firm.evaluation.lookups.JoinedEvaluationTemplateCategoryService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -This service is used to manage the employee evaluation -
 * <h1>Usage</h1>
 * -used to create, manage the evaluations for employees-
 * <h1>Restriction</h1>
 * -Tno delete or edit after approve the evaluation-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class EmployeeEvaluationService {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    JoinedEvaluationTemplateCategoryService joinedEvaluationTemplateCategoryService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static requestStatusValue = { cService, EmployeeEvaluation rec, object, params ->
        if (rec?.requestStatus) {
            return rec?.requestStatus?.toString()
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
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "evaluationResult", type: "EvaluationCriterium", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "evaluationSum", type: "Double", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "evaluationTemplate", type: "EvaluationTemplate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: requestStatusValue, source: 'domain'],
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
        String employeeId = params["employee.id"]
        Set employeeEvaluationItemsIds = params.listString("employeeEvaluationItems.id")
        String evaluationResultId = params["evaluationResult.id"]
        Double evaluationSum = params.double("evaluationSum")
        String evaluationTemplateId = params["evaluationTemplate.id"]
        Long firmId = params.long("firm.id") ? params.long("firm.id") : PCPSessionUtils.getValue("firmId")
        String status = params["status"]

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        String militaryRankId = params["militaryRank.id"]

        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []

        return EmployeeEvaluation.createCriteria().list(max: max, offset: offset) {
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
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
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
                if (employeeEvaluationItemsIds) {
                    employeeEvaluationItems {
                        inList("id", employeeEvaluationItemsIds)
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
                if (firmId) {
                    eq("firm.id", firmId)
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
                if (requestStatus) {
                    eq("requestStatus", requestStatus)
                }
                if (excludeRequestStatusList) {
                    not {
                        inList("requestStatus", excludeRequestStatusList)
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
                order(columnName, dir)
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
        PagedResultList pagedResultList = search(params)

        //get employee remote details
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        //loop on search result and map the employee, disciplinary remote values
        pagedResultList?.resultList?.each { EmployeeEvaluation employeeEvaluation ->
            employeeEvaluation?.employee = employeeList?.find { it?.id == employeeEvaluation?.employee?.id }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeEvaluation.
     */
    EmployeeEvaluation save(GrailsParameterMap params) {
        EmployeeEvaluation employeeEvaluationInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            employeeEvaluationInstance = EmployeeEvaluation.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeEvaluationInstance.version > version) {
                    employeeEvaluationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeEvaluation.label', null, 'employeeEvaluation', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeEvaluation while you were editing")
                    return employeeEvaluationInstance
                }
            }
            if (!employeeEvaluationInstance) {
                employeeEvaluationInstance = new EmployeeEvaluation()
                employeeEvaluationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeEvaluation.label', null, 'employeeEvaluation', LocaleContextHolder.getLocale())] as Object[], "This employeeEvaluation with ${params.id} not found")
                return employeeEvaluationInstance
            }
        } else {
            employeeEvaluationInstance = new EmployeeEvaluation()
        }
        try {
            employeeEvaluationInstance.properties = params;

            employeeEvaluationInstance?.currentEmploymentRecord = employeeEvaluationInstance?.employee?.currentEmploymentRecord;
            employeeEvaluationInstance?.currentEmployeeMilitaryRank = employeeEvaluationInstance?.employee?.currentEmployeeMilitaryRank;

            //list of the form question ids.
            List items = params.list("itemId")
            String notes = params["notes"] ?: null //general note
            Double mark //variable to store the mark.

            EvaluationItem evaluationItem
            JoinedEmployeeEvaluationItems employeeEvaluationItem

            Double evaluationSum = 0.0
            //loop on each question id:
            items.eachWithIndex { def itemId, int index ->
                mark = params.double("mark-${itemId}") ?: 1.0 //get the mark for each item.
                evaluationSum += mark;
                evaluationItem = EvaluationItem.load(itemId)
                //create the evaluation answer object and do save:

                if (employeeEvaluationInstance?.id) {
                    employeeEvaluationItem = JoinedEmployeeEvaluationItems.findByEvaluationItemAndEmployeeEvaluation(evaluationItem, employeeEvaluationInstance)
                } else {
                    employeeEvaluationItem = new JoinedEmployeeEvaluationItems()
                    employeeEvaluationItem?.evaluationItem = evaluationItem
                    employeeEvaluationItem?.employeeEvaluation = employeeEvaluationInstance
                }
                employeeEvaluationItem?.mark = mark
                employeeEvaluationItem?.notes = notes

                employeeEvaluationInstance?.addToEmployeeEvaluationItems(employeeEvaluationItem)
                mark = null
                evaluationItem = null
            }

            employeeEvaluationInstance?.evaluationSum = evaluationSum

            EvaluationCriterium evaluationResult = EvaluationCriterium.findByToMarkGreaterThanEqualsAndFromMarkLessThanEqualsAndEvaluationTemplate(evaluationSum, evaluationSum, employeeEvaluationInstance?.evaluationTemplate)
            employeeEvaluationInstance?.evaluationResult = evaluationResult

            if(employeeEvaluationInstance?.requestStatus == EnumRequestStatus.CREATED){
                employeeEvaluationInstance?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }

            employeeEvaluationInstance.save(failOnError: true);

        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            employeeEvaluationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeEvaluationInstance
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
            EmployeeEvaluation instance = EmployeeEvaluation.get(id)
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
     * @return EmployeeEvaluation.
     */
    @Transactional(readOnly = true)
    EmployeeEvaluation getInstance(GrailsParameterMap params) {
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
     * @return EmployeeEvaluation.
     */
    @Transactional(readOnly = true)
    EmployeeEvaluation getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                EmployeeEvaluation employeeEvaluation = results[0]
                employeeEvaluation?.transientData?.sections = employeeEvaluation?.evaluationTemplate?.availableSections
                JoinedEmployeeEvaluationItems joinedEmployeeEvaluationItems

                employeeEvaluation?.transientData?.sections?.each { EvaluationSection evaluationSection ->
                    Set<EvaluationItem> evaluationItemList = evaluationSection?.availableItems
                    evaluationItemList?.each { EvaluationItem evaluationItem ->
                        joinedEmployeeEvaluationItems = JoinedEmployeeEvaluationItems.findByEvaluationItemAndEmployeeEvaluation(evaluationItem, employeeEvaluation)
                        evaluationItem?.transientData?.answer = joinedEmployeeEvaluationItems?.mark
                        employeeEvaluation?.transientData?.note = joinedEmployeeEvaluationItems?.notes
                        evaluationItem?.save()
                    }
                }
                return employeeEvaluation
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
     * @return employeeEvaluation.
     */
    @Transactional(readOnly = true)
    EmployeeEvaluation getPreCreateInstance(GrailsParameterMap params) {
        EmployeeEvaluation employeeEvaluation = new EmployeeEvaluation(params)
        //CHECK if employee has request in [progress or approved] requests
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, EnumRequestStatus.APPROVED]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            employeeEvaluation.errors.reject('employeeEvaluation.employeeHasEvaluation.error.label')
        } else {
            GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id':params['firmId'],id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

            //check if the employee current status category is COMMITTED or not
            if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
                employeeEvaluation.errors.reject('request.employeeUncommitted.error.label')
            } else {
                employeeEvaluation?.employee = employee

                //TODO : ask about query criteria to select which evaluation template is applicable with any employee.
                //depends on employee military rank and job category , we choose the evaluation template
//                GrailsParameterMap templateParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
//                templateParams["militaryRank.id"] = employee?.currentEmployeeMilitaryRank?.militaryRank?.id
//                templateParams["jobCategory.id"] = employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id
//                templateParams["templateType"] = params["templateType"]
//                JoinedEvaluationTemplateCategory joinedEvaluationTemplateCategory = joinedEvaluationTemplateCategoryService.search(templateParams)[0]


                if(params["evaluationTemplateId"]){
                    EvaluationTemplate evaluationTemplate = EvaluationTemplate.load(params["evaluationTemplateId"])
                    employeeEvaluation?.evaluationTemplate = evaluationTemplate
                } else {
                    employeeEvaluation.errors.reject('employeeEvaluation.template.error.label')
                    return employeeEvaluation
                }

                employeeEvaluation?.fromDate = ZonedDateTime.now()
                employeeEvaluation?.currentEmploymentRecord = employee?.currentEmploymentRecord
                //get the sections of the evaluation
                //employeeEvaluation?.transientData?.sections = employeeEvaluation?.evaluationTemplate?.availableSections
            }
        }
        return employeeEvaluation
    }

}