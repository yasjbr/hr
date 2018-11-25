package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.firm.lookups.JoinedProvinceFirm
import ps.gov.epsilon.hr.firm.lookups.Province
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.gov.epsilon.hr.firm.settings.FirmSupportContactInfo
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.commands.v1.DescriptionInfoCommand
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.commands.v1.WorkingSectorCommand
import ps.police.pcore.v2.entity.organization.commands.v1.OrganizationCommand
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.CorporationClassificationCommand
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.OrganizationActivityCommand
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.OrganizationTypeCommand

/**
 * <h1>Purpose</h1>
 * this service is aims to create firm
 * <h1>Usage</h1>
 * -used for lookups
 * <h1>Restriction</h1>
 * - no restriction
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class FirmService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "code", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.coreName", type: "String", source: 'domain']
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
        List<Long> ids = params.listLong('ids[]')
        Long id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = Long.parseLong(HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params.long('id')
        }

        List<Map<String, String>> orderBy = params.list("orderBy")
        String code = params["code"]
        List codes = params["codes[]"]
        Long coreOrganizationId = params.long("coreOrganizationId")
        Set departmentsIds = params.listString("departments.id")
        Set firmActiveModulesIds = params.listString("firmActiveModules.id")
        Set firmSettingsIds = params.listString("firmSettings.id")
        String name = params["name"]
        String note = params["note"]
        Long supportContactInfoId = params.long("supportContactInfo.id")
        String status = params["status"]
        Boolean centralizedWithAOC = params.boolean('centralizedWithAOC', null)

        PagedResultList firmList = Firm.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("code", sSearch)
                    ilike("name", sSearch)
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
                if (code) {
                    ilike("code", "%${code}%")
                }
                if (coreOrganizationId) {
                    eq("coreOrganizationId", coreOrganizationId)
                }
                if (departmentsIds) {
                    departments {
                        inList("id", departmentsIds)
                    }
                }
                if (firmActiveModulesIds) {
                    firmActiveModules {
                        inList("id", firmActiveModulesIds)
                    }
                }
                if (firmSettingsIds) {
                    firmSettings {
                        inList("id", firmSettingsIds)
                    }
                }
                if (name) {
                    ilike("name", "%${name}%")
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (supportContactInfoId) {
                    eq("supportContactInfo.id", supportContactInfoId)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (centralizedWithAOC != null) {
                    //
                    firmSettings {
                        eq('propertyName', EnumFirmSetting.CENTRALIZED_WITH_AOC.value)
                        eq('propertyValue', centralizedWithAOC.toString())
                    }
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

        return firmList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return Firm.
 */
    Firm save(GrailsParameterMap params) {
        Firm firmInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            firmInstance = Firm.get(params.long("id"))
            if (params.long("version")) {
                long version = params.long("version")
                if (firmInstance.version > version) {
                    firmInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('firm.label', null, 'firm', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this firm while you were editing")
                    return firmInstance
                }
            }
            if (!firmInstance) {
                firmInstance = new Firm()
                firmInstance.errors.reject('default.not.found.message', [messageSource.getMessage('firm.label', null, 'firm', LocaleContextHolder.getLocale())] as Object[], "This firm with ${params.id} not found")
                return firmInstance
            }
        } else {
            firmInstance = new Firm()
        }
        try {
            //remove support contact info  when edit
            if (firmInstance?.id) {
                FirmSupportContactInfo.executeUpdate("delete from FirmSupportContactInfo firmSupportContactInfo where firmSupportContactInfo.id = :id", [id: firmInstance?.supportContactInfo?.id])
                JoinedProvinceFirm.executeUpdate("delete from JoinedProvinceFirm jpf where jpf.firm.id = :firmId", [firmId: firmInstance?.id])
            }

            //assign firm support contact info
            FirmSupportContactInfo firmSupportContactInfo = new FirmSupportContactInfo()
            firmSupportContactInfo.name = params.supportName
            firmSupportContactInfo.phoneNumber = params.int("phoneNumber")
            firmSupportContactInfo.faxNumber = params.int("faxNumber")
            firmSupportContactInfo.email = params.email
            if (firmSupportContactInfo.name) {
                firmInstance.supportContactInfo = firmSupportContactInfo
            }

            /**
             * assign provinces of firm.
             */
            List<String> provinceFirmsListId = params.listString("provinceFirmsListId")
            provinceFirmsListId?.each { String id ->
                firmInstance.addToProvinceFirms(new JoinedProvinceFirm(province: Province.read(id), firm: firmInstance))
            }

            firmInstance.properties = params
            firmInstance.save(failOnError: true, flush: true)
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            firmInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return firmInstance
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

            Firm instance = Firm.get(id)
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
 * @return Firm.
 */
    @Transactional(readOnly = true)
    Firm getInstance(GrailsParameterMap params) {
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
        String nameProperty = params["nameProperty"] ?: "name"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: ["code"]
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return Firm.
     */
    Firm getInstanceWithRemotingValues(GrailsParameterMap params) {
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
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList firmList = search(params)
        if (firmList) {
            Map data = [:]
            List<Long> organizationIds = firmList?.resultList?.coreOrganizationId
            if (!organizationIds) {
                organizationIds = [-1L]
            } else {
                organizationIds.remove(-1L)
            }
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: organizationIds))
            List<OrganizationDTO> organizationList = organizationService?.searchOrganization(searchBean)?.resultList
            firmList.each { Firm firm ->
                data.coreName = organizationList?.find {
                    it?.id == firm?.coreOrganizationId
                }?.descriptionInfo?.localName
                firm.transientData = data
                data = [:]
            }
        }

        return firmList
    }

    /**
     * to save organization on core
     * @param GrailsParameterMap params
     * @return organization command
     */
    public OrganizationCommand saveOrganization(GrailsParameterMap params) {
        OrganizationCommand organizationCommand = new OrganizationCommand()
        DescriptionInfoCommand descriptionInfoCommandTest = new DescriptionInfoCommand(localName: "test", latinName: "", hebrewName: '')
        if (params.long("corporationClassification.id")) {
            CorporationClassificationCommand corporationClassificationCommand = new CorporationClassificationCommand(id: params.long("corporationClassification.id"))
            organizationCommand.corporationClassification = corporationClassificationCommand

            if (params.localName) {
                DescriptionInfoCommand descriptionInfoCmd = new DescriptionInfoCommand(localName: params.localName, latinName: params.latinName, hebrewName: params.hebrewName)
                organizationCommand.descriptionInfo = descriptionInfoCmd
            }


            if (params.long("parentOrganization.id")) {
                OrganizationCommand parentOrganizationCommand = new OrganizationCommand(id: params.long("parentOrganization.id"), descriptionInfo: descriptionInfoCommandTest)
                organizationCommand.parentOrganization = parentOrganizationCommand
            }



            if (params.long("organizationMainActivity.id")) {
                OrganizationActivityCommand organizationMainActivity = new OrganizationActivityCommand(id: params.long("organizationMainActivity.id"), descriptionInfo: descriptionInfoCommandTest)
                organizationCommand.organizationMainActivity = organizationMainActivity
            }

            if (params.long("organizationType.id")) {
                OrganizationTypeCommand organizationTypeCommand = new OrganizationTypeCommand(id: params.long("organizationType.id"), descriptionInfo: descriptionInfoCommandTest)
                organizationCommand.organizationType = organizationTypeCommand

            }

            if (params.long("workingSector.id")) {
                WorkingSectorCommand workingSectorCommand = new WorkingSectorCommand(id: params.long("workingSector.id"), descriptionInfo: descriptionInfoCommandTest)
                organizationCommand.workingSector = workingSectorCommand

            }


            organizationCommand.latinDescription = params.latinDescription
            organizationCommand.localDescription = params.localDescription
            organizationCommand.missionStatement = params.missionStatement
            organizationCommand.registrationNumber = params.registrationNumber
            organizationCommand.taxId = params.taxId
            organizationCommand.organizationMainActivity
            organizationCommand.needRevision = true
            if (organizationCommand.validate()) {
                organizationCommand = organizationService.saveOrganization(organizationCommand)
            }


        }
        return organizationCommand
    }

    /*  *//**
     * This method is used to convert first char in string to lower case.
     * @param String string the string to Capitalize.
     * @return String.
     *//*
    public static String deCapitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        char[] charArray = string.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }*/

}