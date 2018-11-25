package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.config.v1.Constants
import ps.police.pcore.v2.entity.location.LocationAddressUtil
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * --this service is aims to create trainee list
 * <h1>Usage</h1>
 * -this service is used to create trainee list
 * <h1>Restriction</h1>
 * -
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class TraineeListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    LocationService locationService
    ApplicantStatusHistoryService applicantStatusHistoryService
    ApplicantService applicantService
    ApplicantInspectionCategoryResultService applicantInspectionCategoryResultService

    //to get the value of requisition status
    public static getTrainingLocation = { cService, TraineeListEmployee rec, object, params ->
        return rec?.traineeList?.unstructuredLocation
    }

    //return age as long
    public static getAgeAsLong = { formatService, TraineeListEmployee rec, object, params ->
        return rec?.applicant?.age?.longValue()
    }


    //return the vacancy name
    public static vacancyLocalName = { formatService, TraineeListEmployee rec, object, params ->
        if (rec?.applicant?.vacancy?.job) {
            return rec?.applicant?.vacancy?.job?.descriptionInfo?.localName
        } else {
            return ""
        }
    }


    //return the physical inspection mark
    //TODO ask why is it needed? also the code should be fixed to not use static string
    public static getPhysicalInspectionMark = { formatService, TraineeListEmployee rec, object, params ->
        if (rec?.applicant?.inspectionCategoriesResult?.mark) {
            return rec?.applicant?.inspectionCategoriesResult?.find {
                it.inspectionCategory.descriptionInfo.localName == 'فحص لياقة بدنية'
            }?.mark
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "applicant.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "applicant.personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "applicant.transientData.genderType", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "applicant.transientData.locationName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicant.applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "physicalInspectionMark", type: getPhysicalInspectionMark, source: 'domain']
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS_TAB = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "traineeList.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "traineeList.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "location", type: getTrainingLocation, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "Enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingEvaluation", type: "Enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "mark", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "trainingRejectionReason", type: "TrainingRejectionReason", source: 'domain'],
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
        String domainName = params["domainName"]
        String columnName
        if (column) {
            switch (domainName){
                case 'applicantService.DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS' :
                    columnName = applicantService.DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS[column]?.name
                    break
                default:
                    columnName = DOMAIN_COLUMNS[column]?.name
            }
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
        String note = params["note"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null

        String traineeListId = params["traineeList.id"]

        String personName = params["personName"]
        String vacancyId = params["vacancy.id"]
        Long locationId = params.long("locationId")
        ZonedDateTime applyingDate = PCPUtils.parseZonedDateTime(params['applyingDate'])
        ZonedDateTime applyingDateFrom = PCPUtils.parseZonedDateTime(params['applyingDateFrom'])
        ZonedDateTime applyingDateTo = PCPUtils.parseZonedDateTime(params['applyingDateTo'])
        Double age = params.long("age")
        ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus applicantCurrentStatusValue = params["applicantCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(params["applicantCurrentStatusValue"] as String) : null
        String physicalInspectionMark = params["physicalInspectionMark"]
        List<Long> personIds = params.listLong('personIds[]')

        List<Long> locationIds = []
        if(params.location) {
            //get locations which trainees must be in
            SearchBean searchBean = PCPUtils.convertParamsToSearchBean(params.location)
            searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: "max", value1: Constants.getINTEGER_MAX_VALUE()))
            locationIds = locationService?.searchLocation(searchBean)?.resultList?.id
            if (!locationIds || locationIds.isEmpty()) {
                locationIds = []
            }
        }

        return TraineeListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (traineeListId) {
                    eq("traineeList.id", traineeListId)
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
                    if(physicalInspectionMark){
                        inspectionCategoriesResult{
                            eq("mark",physicalInspectionMark)
                            inspectionCategory{
                                eq("descriptionInfo.localName", "فحص لياقة بدنية")
                            }
                        }
                    }
                    if (personIds) {
                        inList("personId", personIds)
                    }
                    if(locationIds.size()>0){
                        inList("locationId", locationIds)
                    }
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if(domainName?.equals("applicantService.DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS")){
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {

        PagedResultList traineeListEmployeeList = search(params)

        if(params["genderType.id"]){
            SearchBean beforeSearchBean = new SearchBean()
            beforeSearchBean.searchCriteria.put("genderType.id", new SearchConditionCriteriaBean(operand: 'genderType.id', value1: params["genderType.id"]))
            beforeSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: Applicant.findAll()?.personId?.toList()))
            List<PersonDTO> persons = personService.searchPerson(beforeSearchBean)?.resultList
            params["personIds[]"] = persons?.id?.toList() ?: [-1L]
        }

        if (traineeListEmployeeList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: traineeListEmployeeList?.resultList?.applicant?.personId))
            List<PersonDTO> personList = personService?.searchPerson(searchBean)?.resultList

            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: traineeListEmployeeList?.resultList?.applicant?.locationId?.unique()))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList


            PersonDTO personDTO
            traineeListEmployeeList?.each { TraineeListEmployee traineeListEmployee ->
                personDTO = personList?.find { it?.id == traineeListEmployee?.applicant?.personId }
                traineeListEmployee?.applicant?.transientData.put("genderType", personDTO?.genderType?.descriptionInfo?.localName)
                traineeListEmployee?.applicant?.transientData.put("locationName", LocationAddressUtil.renderLocation(locationList?.find {
                    it?.id == traineeListEmployee?.applicant?.locationId
                }, traineeListEmployee?.applicant?.unstructuredLocation));
            }
            return traineeListEmployeeList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return TraineeListEmployee.
     */
    TraineeListEmployee save(GrailsParameterMap params) {
        TraineeListEmployee traineeListEmployeeInstance
        if (params.id) {
            traineeListEmployeeInstance = TraineeListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (traineeListEmployeeInstance.version > version) {
                    traineeListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('traineeListEmployee.label', null, 'traineeListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this traineeListEmployee while you were editing")
                    return traineeListEmployeeInstance
                }
            }
            if (!traineeListEmployeeInstance) {
                traineeListEmployeeInstance = new TraineeListEmployee()
                traineeListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('traineeListEmployee.label', null, 'traineeListEmployee', LocaleContextHolder.getLocale())] as Object[], "This traineeListEmployee with ${params.id} not found")
                return traineeListEmployeeInstance
            }
        } else {
            traineeListEmployeeInstance = new TraineeListEmployee()
        }
        try {
            traineeListEmployeeInstance.properties = params;
            traineeListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            traineeListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return traineeListEmployeeInstance
    }

    /**
     * to delete model entry ==>> delete is not supported for applicant
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
                TraineeListEmployee traineeListEmployee = TraineeListEmployee.findByApplicant(Applicant.get(id))
                traineeListEmployee?.applicant?.traineeListEmployee = null

                // get index of toDate column
                int toDateColumnIndex = applicantStatusHistoryService.DOMAIN_COLUMNS.findIndexOf { it.name == "toDate"}

                // get applicant status before add to list
                GrailsParameterMap applicantLastStatusParams = new GrailsParameterMap(["max":1, "orderColumn":toDateColumnIndex, "orderDirection":"desc","applicant.id":id], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
                ApplicantStatusHistory applicantStatusBeforeDelete = applicantStatusHistoryService.search(applicantLastStatusParams)[0]

                // set toDate current applicant status
                traineeListEmployee.applicant.applicantCurrentStatus.toDate = ZonedDateTime.now();
                traineeListEmployee.applicant.applicantCurrentStatus.save(flush: true, failOnError: true)

                // change applicant status
                ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: traineeListEmployee.applicant,
                        fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), applicantStatus: applicantStatusBeforeDelete.applicantStatus)
                traineeListEmployee.applicant.applicantCurrentStatus = applicantStatusHistory
                traineeListEmployee.applicant.save(flush: true, failOnError: true)

                traineeListEmployee.delete(flush: true, failOnError:true)
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('virtualDelete.error.fail.delete.label')
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return TraineeListEmployee.
     */
    @Transactional(readOnly = true)
    TraineeListEmployee getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                TraineeListEmployee traineeListEmployee = results[0]
                return traineeListEmployee
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
        String nameProperty = params["nameProperty"] ?: "applicant.personName"
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