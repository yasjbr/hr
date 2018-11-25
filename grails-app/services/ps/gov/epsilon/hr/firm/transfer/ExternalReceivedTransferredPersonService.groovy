package ps.gov.epsilon.hr.firm.transfer

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -manage all external transfer received transferred persons and get data from domain
 * <h1>Usage</h1>
 * -any service to get external transfer received info or search about internal transfer
 * <h1>Restriction</h1>
 * -must connect with pcore application to get person and organization information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ExternalReceivedTransferredPersonService {

    MessageSource messageSource
    FormatService formatService
    PersonService personService
    OrganizationService organizationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.personDTO", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNo", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.fromOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
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
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])
        Long fromOrganizationId = params.long("fromOrganizationId")
        String note = params["note"]
        String orderNo = params["orderNo"]
        Long personId = params.long("personId")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String toDepartmentId = params["toDepartment.id"]
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        return ExternalReceivedTransferredPerson.createCriteria().list(max: max, offset: offset) {
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
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (effectiveDateFrom) {
                    ge("effectiveDate", effectiveDateFrom)
                }
                if (effectiveDateTo) {
                    le("effectiveDate", effectiveDateTo)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (fromOrganizationId) {
                    eq("fromOrganizationId", fromOrganizationId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (personId) {
                    eq("personId", personId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (toDepartmentId) {
                    eq("toDepartment.id", toDepartmentId)
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
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
 * @return ExternalReceivedTransferredPerson.
 */
    ExternalReceivedTransferredPerson save(GrailsParameterMap params) {
        ExternalReceivedTransferredPerson externalReceivedTransferredPersonInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            externalReceivedTransferredPersonInstance = ExternalReceivedTransferredPerson.get(params["id"])
            if (externalReceivedTransferredPersonInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (externalReceivedTransferredPersonInstance.version > version) {
                        externalReceivedTransferredPersonInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('externalReceivedTransferredPerson.label', null, 'externalReceivedTransferredPerson', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this externalReceivedTransferredPerson while you were editing")
                        return externalReceivedTransferredPersonInstance
                    }
                }
            }else{
                externalReceivedTransferredPersonInstance = new ExternalReceivedTransferredPerson()
                externalReceivedTransferredPersonInstance.errors.reject('default.not.found.message', [messageSource.getMessage('externalReceivedTransferredPerson.label', null, 'externalReceivedTransferredPerson', LocaleContextHolder.getLocale())] as Object[], "This externalReceivedTransferredPerson with ${params.id} not found")
                return externalReceivedTransferredPersonInstance
            }
        } else {
            externalReceivedTransferredPersonInstance = new ExternalReceivedTransferredPerson()
        }
        try {
            externalReceivedTransferredPersonInstance.properties = params;
            externalReceivedTransferredPersonInstance.save(failOnError:true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            externalReceivedTransferredPersonInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return externalReceivedTransferredPersonInstance
    }

    /**
     * to count of employee depends on params.
     * @param GrailsParameterMap params the search map.
     * @return Integer.
     */
    @Transactional(readOnly = true)
    Integer count(GrailsParameterMap params) {

        Long personId = params.long("personId")
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        return ExternalReceivedTransferredPerson.createCriteria().count{
            if(personId){
                eq('personId',personId)
            }
            if (generalStatus) {
                eq("trackingInfo.status", generalStatus)
            } else {
                ne("trackingInfo.status", GeneralStatus.DELETED)
            }
        }
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ExternalReceivedTransferredPerson> externalReceivedTransferredPersonList = []
            List ids = deleteBean?.ids
            if(isEncrypted){
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            externalReceivedTransferredPersonList = ExternalReceivedTransferredPerson.findAllByIdInList(ids)
            externalReceivedTransferredPersonList.each { ExternalReceivedTransferredPerson externalReceivedTransferredPerson->
                if (externalReceivedTransferredPerson?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete externalReceivedTransferredPerson
                    externalReceivedTransferredPerson.trackingInfo.status = GeneralStatus.DELETED
                    externalReceivedTransferredPerson.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if(externalReceivedTransferredPersonList) {
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
 * @return ExternalReceivedTransferredPerson.
 */
    @Transactional(readOnly = true)
    ExternalReceivedTransferredPerson getInstance(GrailsParameterMap params) {
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
     * this method used to get the person, profession type, location and other remoting details
     * @param GrailsParameterMap params the search map.
     * @return employee instance
     */
    @Transactional(readOnly = true)
    ExternalReceivedTransferredPerson getInstanceWithRemotingValues(GrailsParameterMap params) {

        //flag to fill person info when instance is empty
        Boolean isNewInstance = params.boolean("isNewInstance")

        //we use get instance instead of search with remoting to serve empty new instance with custom filling data
        ExternalReceivedTransferredPerson externalReceivedTransferredPerson = this.getInstance(params)

        //to fill person info when instance is empty
        if(isNewInstance && !externalReceivedTransferredPerson){
            externalReceivedTransferredPerson =  new ExternalReceivedTransferredPerson()
            externalReceivedTransferredPerson.personId = params.long("personId")
        }
        if (externalReceivedTransferredPerson) {

            SearchBean searchBean = new SearchBean()

            //fill employee person information from PCORE
            searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: externalReceivedTransferredPerson?.personId))
            PersonDTO personDTO = personService.getPerson(searchBean)
            externalReceivedTransferredPerson.transientData.put("personDTO", personDTO)

            //fill organization information from PCORE
            if(externalReceivedTransferredPerson?.fromOrganizationId) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: externalReceivedTransferredPerson?.fromOrganizationId))
                OrganizationDTO organizationDTO = organizationService.getOrganization(searchBean)
                externalReceivedTransferredPerson.transientData.put("fromOrganizationDTO", organizationDTO)
            }

        }
        return externalReceivedTransferredPerson
    }


    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        if(pagedResultList.resultList) {
            SearchBean searchBean = new SearchBean()


            //get person information from PCORE
            List personIds = pagedResultList?.resultList?.personId?.toList()?.unique()
            List organizationIds = pagedResultList?.resultList?.fromOrganizationId?.toList()?.unique()


            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
            List<OrganizationDTO> personDTOList = personService.searchPerson(searchBean)?.resultList


            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            List<OrganizationDTO> organizationDTOList = organizationService.searchOrganization(searchBean)?.resultList


            //fill all remoting info
            pagedResultList.resultList.each { ExternalReceivedTransferredPerson transferredPerson ->

                //fill information from PCORE
                transferredPerson.transientData.personDTO = personDTOList.find{it.id == transferredPerson.personId}
                transferredPerson.transientData.fromOrganizationDTO = organizationDTOList.find{it.id == transferredPerson.fromOrganizationId}

            }
        }
        return pagedResultList
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
        String nameProperty = params["nameProperty"] ?: "personId"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.searchWithRemotingValues(params)
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