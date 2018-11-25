package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create a vacancy advertisements
 * <h1>Usage</h1>
 * -this service is used to create advertisements for vacancy
 * <h1>Restriction</h1>
 * -needs firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacancyAdvertisementsService {

    MessageSource messageSource
    def formatService
    JoinedVacancyAdvertisementService joinedVacancyAdvertisementService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: false, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "title", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recruitmentCycle.name", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "postingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "closingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toBePostedOn", type: "String", source: 'domain']
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
        ZonedDateTime closingDate = PCPUtils.parseZonedDateTime(params['closingDate'])
        ZonedDateTime closingDateFrom = PCPUtils.parseZonedDateTime(params['closingDateFrom'])
        ZonedDateTime closingDateTo = PCPUtils.parseZonedDateTime(params['closingDateTo'])
        String description = params["description"]
        ZonedDateTime postingDate = PCPUtils.parseZonedDateTime(params['postingDate'])
        ZonedDateTime postingDateFrom = PCPUtils.parseZonedDateTime(params['postingDateFrom'])
        ZonedDateTime postingDateTo = PCPUtils.parseZonedDateTime(params['postingDateTo'])
        String recruitmentCycleId = params["recruitmentCycle.id"]
        String title = params["title"]
        String toBePostedOn = params["toBePostedOn"]
        Set vacanciesIds = params.listString("vacancies.id")
        String status = params["status"]


        return VacancyAdvertisements.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike("title", sSearch)
                    ilike("toBePostedOn", sSearch)
                    recruitmentCycle {
                        ilike("name", sSearch)
                    }
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (closingDate) {
                    le("closingDate", closingDate)
                }
                if (description) {
                    ilike("description", "%${description}%")
                }
                if (postingDate) {
                    le("postingDate", postingDate)
                }
                if (recruitmentCycleId) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                if (title) {
                    ilike("title", "%${title}%")
                }
                if (toBePostedOn) {
                    ilike("toBePostedOn", "%${toBePostedOn}%")
                }
                if (vacanciesIds) {
                    vacancies {
                        inList("id", vacanciesIds)
                    }
                }



                if (postingDateFrom) {
                    ge("postingDate", postingDateFrom)
                }
                if (postingDateTo) {
                    le("postingDate", postingDateTo)
                }
                if (closingDateFrom) {
                    ge("closingDate", closingDateFrom)
                }
                if (closingDateTo) {
                    le("closingDate", closingDateTo)
                }





                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            //todo set this condition with role_admin only, all other users just view the active records
            if (status) {
                eq("trackingInfo.status", GeneralStatus.valueOf(status))
            } else {
                ne("trackingInfo.status", GeneralStatus.DELETED)
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "recruitmentCycle.name":
                        recruitmentCycle {
                            order("name", dir)
                        }
                        break;
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
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
     * @return VacancyAdvertisements.
     */
    VacancyAdvertisements save(GrailsParameterMap params) {
        VacancyAdvertisements vacancyAdvertisementsInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            vacancyAdvertisementsInstance = VacancyAdvertisements.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (vacancyAdvertisementsInstance.version > version) {
                    vacancyAdvertisementsInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacancyAdvertisements.label', null, 'vacancyAdvertisements', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacancyAdvertisements while you were editing")
                    return vacancyAdvertisementsInstance
                }
            }
            if (!vacancyAdvertisementsInstance) {
                vacancyAdvertisementsInstance = new VacancyAdvertisements()
                vacancyAdvertisementsInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacancyAdvertisements.label', null, 'vacancyAdvertisements', LocaleContextHolder.getLocale())] as Object[], "This vacancyAdvertisements with ${params.id} not found")
                return vacancyAdvertisementsInstance
            }
        } else {
            vacancyAdvertisementsInstance = new VacancyAdvertisements()
        }
        try {


            ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

            //to validate posting date less than or equals closing date
            if (fromDate <= toDate) {
                vacancyAdvertisementsInstance.properties = params

                if (vacancyAdvertisementsInstance?.id) {
                    //Vacancy?.executeUpdate("update Vacancy vac set vac.vacancyStatus = :vacStatus where vac.id in (select cat.vacancy.id from JoinedVacancyAdvertisement cat where cat.vacancyAdvertisements.id = :vacancyAdvertisementsId)", [vacStatus: EnumVacancyStatus.NEW,vacancyAdvertisementsId: vacancyAdvertisementsInstance?.id])
                    JoinedVacancyAdvertisement?.executeUpdate("delete from JoinedVacancyAdvertisement cat where cat.vacancyAdvertisements.id = :vacancyAdvertisementsId", [vacancyAdvertisementsId: vacancyAdvertisementsInstance?.id])
                    Vacancy?.executeUpdate("update Vacancy vac set vac.vacancyStatus = :vacStatus where vac.id not in (select cat.vacancy.id from JoinedVacancyAdvertisement cat)", [vacStatus: EnumVacancyStatus.NEW])
                }

                //to get list of committee by ids
                List vacancyIds = params.list("vacancy")
                params.remove("vacancy")

                vacancyIds?.eachWithIndex { value, index ->
                    vacancyAdvertisementsInstance.addToJoinedVacancyAdvertisement(new JoinedVacancyAdvertisement(vacancyAdvertisements: vacancyAdvertisementsInstance, vacancy: value))
                    Vacancy vacancy = Vacancy.get(value)
                    vacancy.vacancyStatus = EnumVacancyStatus.POSTED
                    if (vacancyAdvertisementsInstance.recruitmentCycle) {
                        vacancy.recruitmentCycle = vacancyAdvertisementsInstance.recruitmentCycle
                    }
                    vacancy.save(failOnError: true)
                }

                vacancyAdvertisementsInstance.save(failOnError: true)
            } else {
                vacancyAdvertisementsInstance.errors.reject('vacancyAdvertisements.dateError.label', [] as Object[], "")
            }

        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            vacancyAdvertisementsInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacancyAdvertisementsInstance
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

            VacancyAdvertisements instance = VacancyAdvertisements.get(id)
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
     * @return VacancyAdvertisements.
     */
    @Transactional(readOnly = true)
    VacancyAdvertisements getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted
            params.id = HashHelper.decode(params.encodedId as String)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
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
        String nameProperty = params["nameProperty"] ?: "title"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = search(params)
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
     * to add vacancy  from vacancy advertisement
     * @param GrailsParameterMap params
     * @return boolean
     */
    public boolean addVacancyToVacancyAdvertisements(GrailsParameterMap params) {

        //to get the list of vacancies
        List checkedVacancyList = params.listString("check_vacancyTable")

        //to get vacancy advertisement
        VacancyAdvertisements vacancyAdvertisements = VacancyAdvertisements.load(params["vacancyAdvertisementsId"])

        Vacancy vacancy
        //to add vacancies to advertisement
        checkedVacancyList?.each { String id ->
            vacancy = Vacancy.load(id)
            vacancy.vacancyStatus = EnumVacancyStatus.POSTED
            vacancyAdvertisements.addToJoinedVacancyAdvertisement(vacancyAdvertisements: vacancyAdvertisements,
                    vacancy: vacancy)
        }
        try {
            vacancyAdvertisements.save(failOnError: true)
            vacancy.save(failOnError: true)

            return true

        } catch (Exception ex) {
            return false

        }
    }
    /**
     * to delete vacancy  from vacancy advertisement
     * @param GrailsParameterMap params
     * @return boolean
     */
    public boolean deleteVacancyFromVacancyAdvertisements(GrailsParameterMap params) {
        try {
            Vacancy vacancy = JoinedVacancyAdvertisement.load(HashHelper.decode(params.encodedId))?.vacancy
            DeleteBean deleteBean = joinedVacancyAdvertisementService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
            if (deleteBean?.status) {
                vacancy.vacancyStatus = EnumVacancyStatus.NEW
                vacancy.save(failOnError: true)
                return true
            } else {
                return false
            }
        } catch (Exception ex) {
            return false
        }
    }
}