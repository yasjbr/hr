package ps.gov.epsilon.hr.firm.profileNotice

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

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
class ProfileNoticeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    OrganizationService organizationService
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "firm", type: "Firm", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "noticeText", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.sourceOrganizationName", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "presentedBy", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "profileNoticeCategory", type: "ProfileNoticeCategory", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "profileNoticeReason", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "profileNoticeStatus", type: "enum", source: 'domain'],
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
        Long employeeId = params.long("employee.id")
        Long firmId = params.long("firm.id")
        String name = params["name"]
        String noticeText = params["noticeText"]
        String presentedBy = params["presentedBy"]
        Long profileNoticeCategoryId = params.long("profileNoticeCategory.id")
        Set profileNoticeNotesIds = params.listLong("profileNoticeNotes.id")
        String profileNoticeReason = params["profileNoticeReason"]
        ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus profileNoticeStatus = params["profileNoticeStatus"] ? ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus.valueOf(params["profileNoticeStatus"]) : null
        Long sourceOrganizationId = params.long("sourceOrganizationId")
        List<Long> personIds = params.listLong('personIds[]')

        return ProfileNotice.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("name", sSearch)
                    ilike("noticeText", sSearch)
                    ilike("presentedBy", sSearch)
                    ilike("profileNoticeReason", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (personIds) {
                    employee{
                        inList("personId", personIds)
                    }
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (name) {
                    ilike("name", "%${name}%")
                }
                if (noticeText) {
                    ilike("noticeText", "%${noticeText}%")
                }
                if (presentedBy) {
                    ilike("presentedBy", "%${presentedBy}%")
                }
                if (profileNoticeCategoryId) {
                    eq("profileNoticeCategory.id", profileNoticeCategoryId)
                }
                if (profileNoticeNotesIds) {
                    profileNoticeNotes {
                        inList("id", profileNoticeNotesIds)
                    }
                }
                if (profileNoticeReason) {
                    ilike("profileNoticeReason", "%${profileNoticeReason}%")
                }
                if (profileNoticeStatus) {
                    eq("profileNoticeStatus", profileNoticeStatus)
                }
                if (sourceOrganizationId) {
                    eq("sourceOrganizationId", sourceOrganizationId)
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

        PagedResultList pagedResultList = this.search(params)
        List personIds = pagedResultList.resultList?.employee?.personId.toList()

        SearchBean searchBean = new SearchBean()

        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: pagedResultList.resultList?.sourceOrganizationId))
        List<OrganizationDTO> organizationList = organizationService?.searchOrganization(searchBean)?.resultList

        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: pagedResultList.resultList?.employee?.personId))
        List<PersonDTO> persons= personService.searchPerson(searchBean)?.resultList

        //fill all employee governorates
        pagedResultList.resultList.each { ProfileNotice notice ->
            Employee employee= notice.employee
            employee.transientData.personDTO = persons.find { it.id == employee.personId }
            notice.transientData.sourceOrganizationName = organizationList.find { it.id == notice.sourceOrganizationId }?.descriptionInfo?.localName
        }

        def orderColumn = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        String domainColumns = params["domainColumns"]
        List listOfColumns = DOMAIN_COLUMNS
        if (domainColumns) {
            listOfColumns = this."${domainColumns}"
        }
        if (orderColumn != null) {
            columnName = listOfColumns[orderColumn]?.name
        }
        if (columnName == "transientData.sourceOrganizationName") {
            //sort by person name
            pagedResultList.resultList.sort { a, b ->
                if (dir == "asc") {
                    return a?.transientData?.sourceOrganizationDTO?.localFullName <=> b?.transientData?.sourceOrganizationDTO?.localFullName
                } else {
                    return b?.transientData?.sourceOrganizationDTO?.localFullName <=> a?.transientData?.sourceOrganizationDTO?.localFullName
                }
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return ProfileNotice.
 */
    ProfileNotice save(GrailsParameterMap params) {
        ProfileNotice profileNoticeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            profileNoticeInstance = ProfileNotice.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (profileNoticeInstance.version > version) {
                    profileNoticeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('profileNotice.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this profileNotice while you were editing")
                    return profileNoticeInstance
                }
            }
            if (!profileNoticeInstance) {
                profileNoticeInstance = new ProfileNotice()
                profileNoticeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('profileNotice.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], "This profileNotice with ${params.id} not found")
                return profileNoticeInstance
            }
        } else {
            profileNoticeInstance = new ProfileNotice()
        }
        try {
            profileNoticeInstance.properties = params;
            profileNoticeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            profileNoticeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return profileNoticeInstance
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
                ProfileNotice.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                ProfileNotice.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return ProfileNotice.
 */
    @Transactional(readOnly = true)
    ProfileNotice getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = params.boolean("withRemotingValues", true)?searchWithRemotingValues(params):search(params)
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
     *
     * @param params
     * @return
     */
    ProfileNotice saveChangeStatus(GrailsParameterMap params) {
        ProfileNotice profileNoticeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            profileNoticeInstance = ProfileNotice.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (profileNoticeInstance.version > version) {
                    profileNoticeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('profileNotice.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this profileNotice while you were editing")
                    return profileNoticeInstance
                }
            }
            if (!profileNoticeInstance) {
                profileNoticeInstance = new ProfileNotice()
                profileNoticeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('profileNotice.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], "This profileNotice with ${params.id} not found")
                return profileNoticeInstance
            }
        } else {
            if (!profileNoticeInstance) {
                profileNoticeInstance = new ProfileNotice()
                profileNoticeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('profileNotice.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], "This profileNotice with ${params.id} not found")
                return profileNoticeInstance
            }
        }
        try {
            EnumProfileNoticeStatus profileNoticeStatus= EnumProfileNoticeStatus.valueOf(params.profileNoticeStatus)
            if(profileNoticeStatus == profileNoticeInstance.profileNoticeStatus){
                profileNoticeInstance.errors.reject('default.not.changed.message', [messageSource.getMessage('profileNotice.profileNoticeStatus.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], 'Status')
                return profileNoticeInstance
            }
            profileNoticeInstance.profileNoticeStatus=profileNoticeStatus

            // add note
            ProfileNoticeNote profileNoticeNote= new ProfileNoticeNote()
            profileNoticeNote.note= params.note
            profileNoticeNote.noteDate= ZonedDateTime.now()
            profileNoticeNote.profileNotice= profileNoticeInstance
            profileNoticeInstance.addToProfileNoticeNotes(profileNoticeNote)
            profileNoticeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            profileNoticeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return profileNoticeInstance
    }

}