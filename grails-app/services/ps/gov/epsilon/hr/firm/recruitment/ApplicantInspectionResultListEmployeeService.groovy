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
 * -this service aims to create list employee for applicant inspection results.
 * <h1>Usage</h1>
 * -this service used to create list employee for applicant inspection results.
 * <h1>Restriction</h1>
 * -need a applicant result list created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ApplicantInspectionResultListEmployeeService {

    MessageSource messageSource
    def formatService
    ApplicantInspectionResultListService applicantInspectionResultListService

    //return age as long
    public static getAgeAsLong = { formatService, ApplicantInspectionResultListEmployee rec, object, params ->
        return rec?.applicant?.age?.longValue()
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, seasurch: false, hidden: true, name: "applicant.encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.id", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.personName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.vacancy", type: "vacancy", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "applicantAge", type: getAgeAsLong, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "applicant.encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.id", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.personName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.vacancy", type: "vacancy", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "applicant.age", type: "Double", source: 'domain'],
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
        String applicantId = params["applicant.id"]
        String applicantInspectionResultListId = params["applicantInspectionResultList.id"]
        Set applicantInspectionResultListEmployeeNotesIds = params.listString("applicantInspectionResultListEmployeeNotes.id")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String personName = params["personName"]
        String vacancyId = params["vacancy.id"]
        Double ageFrom = params.long("fromAge")
        Double ageTo = params.long("toAge")
        Double age = params.long("age")

        return ApplicantInspectionResultListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                applicant {
                    if (personName) {
                        ilike("personName", "%${personName}%")
                    }
                    if (vacancyId) {
                        eq("vacancy.id", vacancyId)
                    }
                    if (ageFrom) {
                        ge("age", ageFrom)
                    }
                    if (ageTo) {
                        le("age", ageTo)
                    }
                    if (age) {
                        eq("age", age)
                    }
                }

                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (applicantId) {
                    eq("applicant.id", applicantId)
                }
                if (applicantInspectionResultListId) {
                    eq("applicantInspectionResultList.id", applicantInspectionResultListId)
                }
                if (applicantInspectionResultListEmployeeNotesIds) {
                    applicantInspectionResultListEmployeeNotes {
                        inList("id", applicantInspectionResultListEmployeeNotesIds)
                    }
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case 'applicant.id':
                        applicant {
                            order("id", dir)
                        }
                        break

                    case 'applicant.age':
                        applicant {
                            order("age", dir)
                        }
                        break
                    case 'applicant.personName':
                        applicant {
                            order("personName", dir)
                        }
                        break

                    case 'applicant.vacancy':
                        applicant {
                            order("vacancy", dir)
                        }
                        break
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
 * @return ApplicantInspectionResultListEmployee.
 */
    ApplicantInspectionResultListEmployee save(GrailsParameterMap params) {
        ApplicantInspectionResultListEmployee applicantInspectionResultListEmployeeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            applicantInspectionResultListEmployeeInstance = ApplicantInspectionResultListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (applicantInspectionResultListEmployeeInstance.version > version) {
                    applicantInspectionResultListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('applicantInspectionResultListEmployee.label', null, 'applicantInspectionResultListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this applicantInspectionResultListEmployee while you were editing")
                    return applicantInspectionResultListEmployeeInstance
                }
            }
            if (!applicantInspectionResultListEmployeeInstance) {
                applicantInspectionResultListEmployeeInstance = new ApplicantInspectionResultListEmployee()
                applicantInspectionResultListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('applicantInspectionResultListEmployee.label', null, 'applicantInspectionResultListEmployee', LocaleContextHolder.getLocale())] as Object[], "This applicantInspectionResultListEmployee with ${params.id} not found")
                return applicantInspectionResultListEmployeeInstance
            }
        } else {
            applicantInspectionResultListEmployeeInstance = new ApplicantInspectionResultListEmployee()
        }
        try {
            applicantInspectionResultListEmployeeInstance.properties = params;
            applicantInspectionResultListEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            applicantInspectionResultListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return applicantInspectionResultListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            //get list of applicant inspection result list employee by ids.
            List<ApplicantInspectionResultListEmployee> applicantInspectionResultListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                applicantInspectionResultListEmployeeList = ApplicantInspectionResultListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                applicantInspectionResultListEmployeeList = ApplicantInspectionResultListEmployee.findAllByIdInList([deleteBean?.ids])
            }
            //revert applicant to previous status.
            List<Applicant> applicantList = applicantInspectionResultListEmployeeList?.applicant
            applicantList?.each { Applicant applicant ->
                applicant.applicantCurrentStatus = new ApplicantStatusHistory(applicant: applicant,
                        fromDate: ZonedDateTime.now(),
                        toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        applicantStatus: EnumApplicantStatus.NEW)
                applicant.save(failOnError: true, flush: true)
            }
            //delete list after revert applicant status to previous status.
            applicantInspectionResultListEmployeeList*.delete(failOnError: true)
            deleteBean.status = true
        } catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return ApplicantInspectionResultListEmployee.
 */
    @Transactional(readOnly = true)
    ApplicantInspectionResultListEmployee getInstance(GrailsParameterMap params) {
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
 * custom method to get report data with custom format
 * @param params
 * @return PagedList to be passed to filter
 */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["applicantInspectionResultList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstance(parameterMap)
        // to list record
        PagedResultList resultList = search(params) //get details
        Map map = [:]
        map.code = applicantInspectionResultList?.code
        map.coverLetter = applicantInspectionResultList?.coverLetter
        map.details = resultList
        return [map]
    }

}