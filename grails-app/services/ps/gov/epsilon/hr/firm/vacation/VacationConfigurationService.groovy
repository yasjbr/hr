package ps.gov.epsilon.hr.firm.vacation

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.ReligionService
import ps.police.pcore.v2.entity.lookups.dtos.v1.MaritalStatusDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.ReligionDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service is aims to create configuration for vacation
 * <h1>Usage</h1>
 * -this service is used to set configuration for vacation
 * <h1>Restriction</h1>
 * -need firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacationConfigurationService {

    MessageSource messageSource
    def formatService
    MaritalStatusService maritalStatusService
    ReligionService religionService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacationType.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "militaryRank.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maxAllowedValue", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isTransferableToNewYear", type: "Boolean", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "trackingInfo.createdBy", type: "String", source: 'domain']
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
        Short allowedValue = params.long("allowedValue")
        Boolean checkForAnnualLeave = params.boolean("checkForAnnualLeave")
        Short employmentPeriod = params.long("employmentPeriod")
        Short frequency = params.long("frequency")
        Boolean isBreakable = params.boolean("isBreakable")
        String isTransferableToNewYear = params["isTransferableToNewYear"]
        String maritalStatusId = params["maritalStatusId"]
        Short maxAllowedValue = params.long("maxAllowedValue")
        String militaryRankId = params["militaryRank.id"]
        ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted sexTypeAccepted = params["sexTypeAccepted"] ? ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.valueOf(params["sexTypeAccepted"]) : null
        Float vacationTransferValue = params.long("vacationTransferValue")
        String vacationTypeId = params["vacationType.id"]
        Short sSearchNumber = params.short("sSearch")
        Boolean takenFully = params.boolean("takenFully")
        String status = params["status"]
        String excludedId = params["excludedId"]

        return VacationConfiguration.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    vacationType {
                        ilike('localName', sSearch)
                    }
                    militaryRank {
                        ilike('localName', sSearch)
                    }

                    if (sSearchNumber) {
                        or {
                            eq("maxAllowedValue", sSearchNumber)
                            eq("employmentPeriod", sSearchNumber)
                            eq("frequency", sSearchNumber)
                        }
                    }
                }
            }
            and {

                if (excludedId) {
                    not {
                        eq("id", excludedId)
                    }
                }

                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (allowedValue) {
                    eq("allowedValue", allowedValue)
                }
                if (checkForAnnualLeave) {
                    eq("checkForAnnualLeave", checkForAnnualLeave)
                }
                if (employmentPeriod) {
                    eq("employmentPeriod", employmentPeriod)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (frequency) {
                    eq("frequency", frequency)
                }
                if (isBreakable) {
                    eq("isBreakable", isBreakable)
                }
                if (isTransferableToNewYear) {
                    eq("isTransferableToNewYear", Boolean.parseBoolean(isTransferableToNewYear))
                }
                if (maritalStatusId) {
                    eq("maritalStatusId", maritalStatusId)
                }
                if (maxAllowedValue) {
                    eq("maxAllowedValue", maxAllowedValue)
                }
                if (militaryRankId) {
                    eq("militaryRank.id", militaryRankId)
                }
                if (sexTypeAccepted) {
                    eq("sexTypeAccepted", sexTypeAccepted)
                }
                if (vacationTransferValue) {
                    eq("vacationTransferValue", vacationTransferValue)
                }
                if (vacationTypeId) {
                    eq("vacationType.id", vacationTypeId)
                }
                if (takenFully) {
                    eq("takenFully", takenFully)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {

                switch (columnName) {
                    case "vacationType.descriptionInfo.localName":
                        vacationType {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break;
                    case "militaryRank.descriptionInfo.localName":
                        militaryRank {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break;
                    default:
                        order(columnName, 'desc')
                        break;
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
 * @return VacationConfiguration.
 */
    VacationConfiguration save(GrailsParameterMap params) {
        VacationConfiguration vacationConfigurationInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            vacationConfigurationInstance = VacationConfiguration.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (vacationConfigurationInstance.version > version) {
                    vacationConfigurationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacationConfiguration.label', null, 'vacationConfiguration', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacationConfiguration while you were editing")
                    return vacationConfigurationInstance
                }
            }
            if (!vacationConfigurationInstance) {
                vacationConfigurationInstance = new VacationConfiguration()
                vacationConfigurationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationConfiguration.label', null, 'vacationConfiguration', LocaleContextHolder.getLocale())] as Object[], "This vacationConfiguration with ${params.id} not found")
                return vacationConfigurationInstance
            }
        } else {
            vacationConfigurationInstance = new VacationConfiguration()
        }
        try {
            /**
             * check if the vacation configuration is created before
             */
            int vacationConfigurationCount =  VacationConfiguration.createCriteria().get(){
                if (params.id){
                    ne("id", params["id"])
                }
                if (params["vacationType.id"]){
                    eq("vacationType.id", params["vacationType.id"])
                }
                if (params["militaryRank.id"]){
                    eq("militaryRank.id", params["militaryRank.id"])
                }
                if (params["maxAllowedValue"]){
                    eq("maxAllowedValue", params.short("maxAllowedValue"))
                }
                if (params["sexTypeAccepted"]){
                    eq("sexTypeAccepted", EnumSexAccepted.valueOf(params["sexTypeAccepted"]))
                }
                projections {
                    count('id')
                }
            }
            vacationConfigurationInstance.properties = params
            if (vacationConfigurationCount > 0) {
                transactionStatus.setRollbackOnly()
                vacationConfigurationInstance.errors.reject('vacationConfiguration.vacationType.unique', ["${vacationConfigurationInstance?.militaryRank?.descriptionInfo?.localName}"] as Object[], "")
                return vacationConfigurationInstance
            }

            /**
             * set values to zero if the user not full them
             */
            if (!vacationConfigurationInstance?.allowedValue) {
                vacationConfigurationInstance.allowedValue = 0
            }
            if (!vacationConfigurationInstance?.employmentPeriod) {
                vacationConfigurationInstance.employmentPeriod = 0
            }
            if (!vacationConfigurationInstance?.frequency) {
                vacationConfigurationInstance.frequency = 0
            }

            /**
             * to set vacationTransferValue=0.0F when isTransferableToNewYear false
             */
            if (vacationConfigurationInstance?.isTransferableToNewYear) {
                vacationConfigurationInstance.vacationTransferValue = params.float('vacationTransferValue')
            } else {
                vacationConfigurationInstance.vacationTransferValue = 0.0F
            }

            /**
             * to validate allowedValue less than or equals maxAllowedValue
             */
            vacationConfigurationInstance.save(failOnError: true,flush: true)
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            vacationConfigurationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:") > 0 ?: ex?.cause?.localizedMessage?.length())] as Object[], "")
        }
        return vacationConfigurationInstance
    }

    /**
     * to save many model entry.
     * @param GrailsParameterMap params the search map.
     * @return vacationConfiguration.
     */
    VacationConfiguration saveAll(GrailsParameterMap params) {
        List militaryRankIdsList = params.listString("militaryRankIds")
        VacationConfiguration vacationConfiguration = null
        VacationConfiguration.withTransaction {
            try {
                militaryRankIdsList?.each { String id ->
                    params.remove("militaryRank.id")
                    params["militaryRank.id"] = id
                    vacationConfiguration = save(params)

                    //check if request has error
                    if (vacationConfiguration?.errors?.hasErrors()) {
                        throw new Exception("${vacationConfiguration?.errors}");
                    }
                }
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
            }

        }
        return vacationConfiguration
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

            VacationConfiguration instance = VacationConfiguration.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
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
 * @return VacationConfiguration.
 */
    @Transactional(readOnly = true)
    VacationConfiguration getInstance(GrailsParameterMap params) {
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
     * @return vacationConfiguration.
     */
    VacationConfiguration getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList vacationConfigurationList = search(params)
        MaritalStatusDTO maritalStatusDTO
        ReligionDTO religionDTO
        SearchBean searchBean
        if (vacationConfigurationList) {

            /*
            to get martial status list by ids
            * */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationConfigurationList?.resultList?.maritalStatusId))
            List<MaritalStatusDTO> maritalStatusList = maritalStatusService?.searchMaritalStatus(searchBean)?.resultList

            /*
            * to get religion list by ids
            * */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationConfigurationList?.resultList?.religionId))
            List<ReligionDTO> religionList = religionService?.searchReligion(searchBean)?.resultList

            vacationConfigurationList?.each { VacationConfiguration vacationConfiguration ->
                vacationConfiguration.transientData = [:]
                maritalStatusDTO = maritalStatusList?.find { it?.id == vacationConfiguration?.maritalStatusId }
                religionDTO = religionList?.find { it?.id == vacationConfiguration?.religionId }
                vacationConfiguration.transientData.put("maritalStatusName", maritalStatusDTO?.descriptionInfo?.localName)
                vacationConfiguration.transientData.put("religionName", religionDTO?.descriptionInfo?.localName)
            }
        }
        return vacationConfigurationList
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
        String nameProperty = params["nameProperty"] ?: "vacationType.descriptionInfo.localName"
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
     * This method used to be able to get the suitable vacation configration for  specific employee
     * @param Employee employee represent the employee that we want to get the vacation configuration list for him.
     * @return List < VacationConfiguration > .
     */
    @Transactional(readOnly = true)
    public List<VacationConfiguration> getEmployeeSuitableConfigration(Employee employee) {

        return VacationConfiguration.executeQuery(" from VacationConfiguration v " +
                " where v.trackingInfo.status=:activeStatus " +
                " and  v.militaryRank=:militaryRank " +
                " and (v.sexTypeAccepted=:sexTypeIdeal or v.sexTypeAccepted=:employeeSex) " +
                " and (v.maritalStatusId is null or v.maritalStatusId=:employeeMaritalStatus) " +
                " and (v.religionId is null or v.religionId=:employeeReligion) " +
                " and (v.employmentPeriod=0 or v.employmentPeriod<=:employeeEmploymentPeriod) ",
                [
                        activeStatus            : GeneralStatus.ACTIVE,
                        militaryRank            : employee?.currentEmployeeMilitaryRank?.militaryRank,
                        sexTypeIdeal            : EnumSexAccepted.BOTH, employeeSex: (EnumSexAccepted.MALE.value == employee?.transientData?.personDTO?.genderType?.id) ? EnumSexAccepted.MALE : EnumSexAccepted.FEMALE,
                        employeeMaritalStatus   : employee?.transientData?.personMaritalStatusDTO?.maritalStatus?.id,
                        employeeReligion        : employee?.transientData?.personDTO?.religion?.id,
                        employeeEmploymentPeriod: employee?.employmentPeriodInMonths?.shortValue()
                ]
        )
    }

}