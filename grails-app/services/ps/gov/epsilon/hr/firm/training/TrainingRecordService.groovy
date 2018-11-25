package ps.gov.epsilon.hr.firm.training

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeExternalAssignation
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

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
class TrainingRecordService {

    MessageSource messageSource
    def formatService
    LocationService locationService
    OrganizationService organizationService
    ManageLocationService manageLocationService
    PersonService personService
    UnitOfMeasurementService unitOfMeasurementService

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */

    public static getOrganization={ formatService, TrainingRecord dataRow, object, params->
        if (dataRow) {
            if(dataRow?.organizationId){
                return dataRow?.transientData?.organizationDTO?.toString()
            }else if(dataRow?.organizationName){
                return dataRow?.organizationName
            }
        }
        return ""
    }

    public static getTraining={ formatService, TrainingRecord dataRow, object, params->
        if (dataRow) {
            if(dataRow?.trainingCourse){
                return dataRow?.trainingCourse?.toString()
            }else if(dataRow?.trainingName){
                return dataRow?.trainingName
            }
        }
        return ""
    }

    public static getLocation = { formatService, TrainingRecord dataRow, object, params ->
        if (dataRow && dataRow?.transientData?.locationDTO) {
            String locationString = dataRow?.transientData?.locationDTO?.toString()
            if (dataRow?.unstructuredLocation) {
                locationString += "-" + dataRow?.unstructuredLocation
            }
            return locationString
        }
        return ""
    }


    public static getEmployeeId ={ formatService, TrainingRecord dataRow, object, params->
        if(dataRow){
            return dataRow?.employee?.id?.toString()
        }
        return  ""
    }




    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingClassification", type: "TrainingClassification", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingCourse", type: getTraining, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "organizationId", type: getOrganization, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "locationId", type: getLocation, source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingClassification", type: "TrainingClassification", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "trainingCourse", type: getTraining, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "organizationId", type: getOrganization, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "locationId", type: getLocation, source: 'domain'],
            [sort: true, search: false, hidden: true, name: "employeeId", type: getEmployeeId, source: 'domain'],
    ]


    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params){
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if(column) {
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


        List<Map<String,String>> orderBy = params.list("orderBy")
        String employeeId = params["employee.id"]
        String employeeMilitaryRankId = params["employeeMilitaryRank.id"]
        String employmentRecordId = params["employmentRecord.id"]
        Long firmId = params.long("firm.id")
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        Long locationId = params.long("locationId")
        String note = params["note"]
        Long numberOfTrainee = params.long("numberOfTrainee")
        Long organizationId = params.long("organizationId")
        String organizationName = params["organizationName"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String trainerId = params["trainer.id"]
        String trainerName = params["trainerName"]
        String trainingClassificationId = params["trainingClassification.id"]
        String trainingCourseId = params["trainingCourse.id"]
        String trainingName = params["trainingName"]
        String unstructuredLocation = params["unstructuredLocation"]
        String status = params["status"]

        return TrainingRecord.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                    ilike("note", sSearch)
                    ilike("organizationName", sSearch)
                    ilike("trainerName", sSearch)
                    ilike("trainingName", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                if(employeeId){
                    eq("employee.id", employeeId)
                }
                if(employeeMilitaryRankId){
                    eq("employeeMilitaryRank.id", employeeMilitaryRankId)
                }
                if(employmentRecordId){
                    eq("employmentRecord.id", employmentRecordId)
                }
                if(firmId){
                    eq("firm.id", firmId)
                }
                if(fromDate){
                    le("fromDate", fromDate)
                }
                if(locationId){
                    eq("locationId", locationId)
                }
                if(note){
                    ilike("note", "%${note}%")
                }
                if(numberOfTrainee){
                    eq("numberOfTrainee", numberOfTrainee)
                }
                if(organizationId){
                    eq("organizationId", organizationId)
                }
                if(organizationName){
                    ilike("organizationName", "%${organizationName}%")
                }
                if(toDate){
                    le("toDate", toDate)
                }
                if(trainerId){
                    eq("trainer.id", trainerId)
                }
                if(trainerName){
                    ilike("trainerName", "%${trainerName}%")
                }
                if(trainingClassificationId){
                    eq("trainingClassification.id", trainingClassificationId)
                }
                if(trainingCourseId){
                    eq("trainingCourse.id", trainingCourseId)
                }
                if(trainingName){
                    ilike("trainingName", "%${trainingName}%")
                }
                if(unstructuredLocation){
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
            }
            if(orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            }else if(columnName){
                order(columnName, dir)
            }else {
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
        List locationIds = pagedResultList.resultList.locationId.toList()
        List organizationIds = pagedResultList.resultList.organizationId.toList()?.findAll{it != null}
        SearchBean searchBeanLocation = new SearchBean()
        searchBeanLocation.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: locationIds))
        List<LocationDTO> locations = locationService.searchLocation(searchBeanLocation)?.resultList

        SearchBean searchBeanOrganization = new SearchBean()
        searchBeanOrganization.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
        List<OrganizationDTO> organizations = organizationService.searchOrganization(searchBeanOrganization)?.resultList

        if(locationIds || organizationIds) {
            pagedResultList.resultList.each { TrainingRecord trainingRecord ->

                if(trainingRecord.locationId){
                    trainingRecord.transientData.locationDTO = locations.find { it.id == trainingRecord.locationId }
                }
                if(trainingRecord.organizationId) {
                    trainingRecord.transientData.organizationDTO = organizations.find { it.id == trainingRecord.organizationId}
                }
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return TrainingRecord.
 */
    TrainingRecord save(GrailsParameterMap params) {
        TrainingRecord trainingRecordInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            trainingRecordInstance = TrainingRecord.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (trainingRecordInstance.version > version) {
                    trainingRecordInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('trainingRecord.label', null, 'trainingRecord',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this trainingRecord while you were editing")
                    return trainingRecordInstance
                }
            }
            if (!trainingRecordInstance) {
                trainingRecordInstance = new TrainingRecord()
                trainingRecordInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('trainingRecord.label', null, 'trainingRecord',LocaleContextHolder.getLocale())] as Object[], "This trainingRecord with ${params.id} not found")
                return trainingRecordInstance
            }
        } else {
            trainingRecordInstance = new TrainingRecord()
        }
        try {

            trainingRecordInstance.period = null
            trainingRecordInstance.unitId = null

            trainingRecordInstance.properties = params;

            LocationCommand locationCommand = manageLocationService.saveLocation(params)
            if (locationCommand?.id) {
                trainingRecordInstance.locationId = locationCommand.id //assign reference id of location from core
            }

            if((!locationCommand?.id) || (trainingRecordInstance?.fromDate && trainingRecordInstance?.toDate && trainingRecordInstance?.toDate < trainingRecordInstance?.fromDate)){
                trainingRecordInstance.validate()
                if(trainingRecordInstance?.fromDate && trainingRecordInstance?.toDate && trainingRecordInstance?.toDate < trainingRecordInstance?.fromDate){
                    trainingRecordInstance.errors.rejectValue("toDate","trainingRecord.toDate.error")
                }
                if(!locationCommand?.id){
                    trainingRecordInstance.errors.reject("trainingRecord.location.error")
                }
                return trainingRecordInstance
            }

            trainingRecordInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            trainingRecordInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return trainingRecordInstance
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

            TrainingRecord instance = TrainingRecord.get(id)
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
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return TrainingRecord.
 */
    @Transactional(readOnly = true)
    TrainingRecord getInstance(GrailsParameterMap params) {
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
     * @return EmploymentRecord.
     */
    @Transactional(readOnly = true)
    TrainingRecord getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                TrainingRecord trainingRecord = results[0]


                if(trainingRecord?.locationId){
                    SearchBean searchBeanLocation = new SearchBean()
                    searchBeanLocation.searchCriteria.put("id",new SearchConditionCriteriaBean(operand: 'id', value1: trainingRecord?.locationId))
                    trainingRecord.transientData.locationDTO = locationService.getLocation(searchBeanLocation)
                }


                if(trainingRecord?.organizationId){
                    SearchBean searchBeanOrganization = new SearchBean()
                    searchBeanOrganization.searchCriteria.put("id",new SearchConditionCriteriaBean(operand: 'id', value1: trainingRecord?.organizationId))
                    trainingRecord.transientData.organizationDTO = organizationService.getOrganization(searchBeanOrganization)
                }

                SearchBean searchBeanPerson = new SearchBean()
                searchBeanPerson.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: trainingRecord?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBeanPerson)
                trainingRecord.employee.transientData.put("personDTO", personDTO)

                if(trainingRecord?.trainer?.id) {
                    SearchBean searchBeanTrainer = new SearchBean()
                    searchBeanTrainer.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: trainingRecord?.trainer?.personId))
                    PersonDTO trainerDTO = personService.getPerson(searchBeanTrainer)
                    trainingRecord.trainer.transientData.put("personDTO", trainerDTO)
                }

                if(trainingRecord?.unitId){
                    SearchBean searchBeanUnit = new SearchBean()
                    searchBeanUnit.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: trainingRecord?.unitId))
                    UnitOfMeasurementDTO unitDTO = unitOfMeasurementService.getUnitOfMeasurement(searchBeanUnit)
                    trainingRecord.transientData.put("unitDTO", unitDTO)

                }

                return trainingRecord
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
        String idProperty = params["idProperty"]?:"id"
        String nameProperty = params["nameProperty"]?:"descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo")?:[]
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList,nameProperty,idProperty,autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

/**
 * Convert paged result list to map depends on DOMAINS_COLUMNS.
 * @param def resultList may be PagedResultList or PagedList.
 * @param GrailsParameterMap params the search map
 * @param List<String> DOMAIN_COLUMNS the list of model column names.
 * @return Map.
 * @see PagedResultList.
 * @see PagedList.
 */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList,GrailsParameterMap params,List<String> DOMAIN_COLUMNS = null) {
        if(!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        String domainColumns = params["domainColumns"]
        if(domainColumns){
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}