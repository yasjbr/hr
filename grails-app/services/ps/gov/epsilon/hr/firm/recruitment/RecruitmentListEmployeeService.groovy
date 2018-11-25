package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

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
class RecruitmentListEmployeeService {

    MessageSource messageSource
    def formatService
    ApplicantStatusHistoryService applicantStatusHistoryService
    ApplicantService applicantService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant", type: "Applicant", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params, Boolean isEncrypted = false) {
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String domainName = params["domainName"]
        String columnName
        if (column) {
            switch (domainName){
                case 'applicantService.DOMAIN_TAB_CUSTOM_COLUMNS' :
                    columnName = applicantService.DOMAIN_TAB_CUSTOM_COLUMNS[column]?.name
                    break
                default:
                    columnName = DOMAIN_COLUMNS[column]?.name
            }
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        String id
        if (isEncrypted && params.id) {
            id = (HashHelper.decode(params.id as String)) ?: -1L
        } else {
            id = params['id']
        }

        List<Map<String, String>> orderBy = params.list("orderBy")
        String applicantId = params["applicant.id"]
        String note = params["note"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String recruitmentListId = params["recruitmentList.id"]

        String personName = params["personName"]
        String vacancyId = params["vacancy.id"]
        Long locationId = params.long("locationId")
        ZonedDateTime applyingDate = PCPUtils.parseZonedDateTime(params['applyingDate'])
        ZonedDateTime applyingDateFrom = PCPUtils.parseZonedDateTime(params['applyingDateFrom'])
        ZonedDateTime applyingDateTo = PCPUtils.parseZonedDateTime(params['applyingDateTo'])
        Double age = params.long("age")
        ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus applicantCurrentStatusValue = params["applicantCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(params["applicantCurrentStatusValue"] as String) : null

        return RecruitmentListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                    ilike("orderNo", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (applicantId) {
                    eq("applicant.id", applicantId)
                }
                applicant{
                    if (applyingDate) {
                        eq("applyingDate", applyingDate)
                    }
                    //from/to :RequestDate
                    if(applyingDateFrom){
                        ge("applyingDate", applyingDateFrom)
                    }
                    if(applyingDateTo){
                        le("applyingDate", applyingDateTo)
                    }
                    if (applicantCurrentStatusValue) {
                        applicantCurrentStatus {
                            eq("applicantStatus", applicantCurrentStatusValue)
                        }
                    }
                    if (age) {
                        eq("age", age)
                    }
                    if (personName) {
                        ilike("personName", "%${personName}%")
                    }
                    if (vacancyId) {
                        eq("vacancy.id", vacancyId)
                    }
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (recruitmentListId) {
                    eq("recruitmentList.id", recruitmentListId)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if(domainName?.equals("applicantService.DOMAIN_TAB_CUSTOM_COLUMNS")){
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'id':
                            applicant{
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        default:
                            applicant{
                                order(columnName, dir)
                            }
                    }
                } else{
                    order(columnName, dir)
                }
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentListEmployee.
     */
    RecruitmentListEmployee save(GrailsParameterMap params) {
        RecruitmentListEmployee recruitmentListEmployeeInstance
        if (params.id) {
            recruitmentListEmployeeInstance = RecruitmentListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (recruitmentListEmployeeInstance.version > version) {
                    recruitmentListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('recruitmentListEmployee.label', null, 'recruitmentListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this recruitmentListEmployee while you were editing")
                    return recruitmentListEmployeeInstance
                }
            }
            if (!recruitmentListEmployeeInstance) {
                recruitmentListEmployeeInstance = new RecruitmentListEmployee()
                recruitmentListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentListEmployee.label', null, 'recruitmentListEmployee', LocaleContextHolder.getLocale())] as Object[], "This recruitmentListEmployee with ${params.id} not found")
                return recruitmentListEmployeeInstance
            }
        } else {
            recruitmentListEmployeeInstance = new RecruitmentListEmployee()
        }
        try {
            recruitmentListEmployeeInstance.properties = params;
            recruitmentListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            recruitmentListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return recruitmentListEmployeeInstance
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
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }
            if (id) {
                RecruitmentListEmployee recruitmentListEmployee = RecruitmentListEmployee.findByApplicant(Applicant.get(id))

                // get index of toDate column
                int toDateColumnIndex = applicantStatusHistoryService.DOMAIN_COLUMNS.findIndexOf { it.name == "toDate"}

                // get applicant status before add to list
                GrailsParameterMap applicantLastStatusParams = new GrailsParameterMap(["max":1, "orderColumn":toDateColumnIndex, "orderDirection":"desc","applicant.id":id], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
                ApplicantStatusHistory applicantStatusBeforeDelete = applicantStatusHistoryService.search(applicantLastStatusParams)[0]

                // set toDate current applicant status
                recruitmentListEmployee.applicant.applicantCurrentStatus.toDate = ZonedDateTime.now();
                recruitmentListEmployee.applicant.applicantCurrentStatus.save(flush: true, failOnError: true)

                // change applicant status
                ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: recruitmentListEmployee.applicant,
                        fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), applicantStatus: applicantStatusBeforeDelete.applicantStatus)
                recruitmentListEmployee.applicant.applicantCurrentStatus = applicantStatusHistory
                recruitmentListEmployee.applicant.save(flush: true, failOnError: true)

                recruitmentListEmployee.delete(flush: true)

                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('recruitmentListEmployee.deleteErrorMessage.label')
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
     * @return RecruitmentListEmployee.
     */
    @Transactional(readOnly = true)
    RecruitmentListEmployee getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if (params.id && isEncrypted) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id) ?: -1L
        }
        def results = this.search(params)
        if (results) {
            return results[0]
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

}