package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
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
class LoanRequestRelatedPersonService {

    MessageSource messageSource
    def formatService
    PersonService personService


    /**
     * to represent loan request id
     */
    public static getRequestId={ formatService, LoanRequestRelatedPerson dataRow, object, params->
        if(dataRow) {
            return dataRow?.loanRequest?.encodedId?.toString()
        }
        return  ""
    }

    /**
     * to represent person id
     */
    public static getPersonId={ formatService, LoanRequestRelatedPerson dataRow, object, params->
        if(dataRow) {
            return dataRow?.requestedPersonId?.toString()
        }
        return  ""
    }



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedRequestId", type: getRequestId, source: 'domain'],
            [sort: true, search: true, hidden: true, name: "personId", type: getPersonId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanRequest.id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.requestedPersonDTO", type: "string", source: 'domain'],
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
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        String loanRequestId = params["loanRequest.id"]
        ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource recordSource = params["recordSource"] ? ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource.valueOf(params["recordSource"]) : null
        Long requestedPersonId = params.long("requestedPersonId")
        Boolean justApprovedPerson = params.boolean('justApprovedPerson')

        List closeRequestListIds = []
        if(justApprovedPerson == true){
            closeRequestListIds = LoanListPerson.executeQuery("select p.loanRequest.id from LoanListPerson p where p.loanList.currentStatus.correspondenceListStatus = :listStatus", [listStatus: EnumCorrespondenceListStatus.CLOSED])
            if(!closeRequestListIds){
                closeRequestListIds = ["-1"]
            }
        }

        //employee ids
        List<String> employeeIds = []
        if(justApprovedPerson == true){
            employeeIds = LoanListPerson.executeQuery("select e.personId from Employee e")
            if(!employeeIds){
                employeeIds = ["-1"]
            }
        }


        return LoanRequestRelatedPerson.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                if(effectiveDate){
                    le("effectiveDate", effectiveDate)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if(loanRequestId){
                    eq("loanRequest.id", loanRequestId)
                }
                if(recordSource){
                    eq("recordSource", recordSource)
                }
                if(requestedPersonId){
                    eq("requestedPersonId", requestedPersonId)
                }

                if(justApprovedPerson == true){
                    eq("recordSource", EnumPersonSource.RECEIVED)
                    loanRequest{
                        inList('id',closeRequestListIds)
                    }
                    not {
                        inList('requestedPersonId',employeeIds)
                    }
                }

            }
            if(orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            }else if(columnName){
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, dir)
                }
            }else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanRequestRelatedPerson.
 */
    LoanRequestRelatedPerson save(GrailsParameterMap params) {
        LoanRequestRelatedPerson loanRequestRelatedPersonInstance


        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            loanRequestRelatedPersonInstance = LoanRequestRelatedPerson.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanRequestRelatedPersonInstance.version > version) {
                    loanRequestRelatedPersonInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('loanRequestRelatedPerson.label', null, 'loanRequestRelatedPerson',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanRequestRelatedPerson while you were editing")
                    return loanRequestRelatedPersonInstance
                }
            }
            if (!loanRequestRelatedPersonInstance) {
                loanRequestRelatedPersonInstance = new LoanRequestRelatedPerson()
                loanRequestRelatedPersonInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('loanRequestRelatedPerson.label', null, 'loanRequestRelatedPerson',LocaleContextHolder.getLocale())] as Object[], "This loanRequestRelatedPerson with ${params.id} not found")
                return loanRequestRelatedPersonInstance
            }
        } else {
            loanRequestRelatedPersonInstance = new LoanRequestRelatedPerson()
        }
        try {
            loanRequestRelatedPersonInstance.properties = params;
            loanRequestRelatedPersonInstance.save(failOnError:true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            loanRequestRelatedPersonInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return loanRequestRelatedPersonInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)){
                LoanRequestRelatedPerson.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                LoanRequestRelatedPerson.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
                deleteBean.status = true
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
 * @return LoanRequestRelatedPerson.
 */
    @Transactional(readOnly = true)
    LoanRequestRelatedPerson getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                return results[0]
            }
        }
        return null
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
            //collect personIds
            List personIds = pagedResultList?.requestedPersonId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
            //fill all persons info
            List<PersonDTO> personList = personService.searchPerson(searchBean)?.resultList

            //loop to fill all remoting values
            pagedResultList.each { LoanRequestRelatedPerson loanRequestRelatedPerson ->

                //fill all person info
                if (loanRequestRelatedPerson?.requestedPersonId) {
                    loanRequestRelatedPerson.transientData.requestedPersonDTO = personList.find {
                        it.id == loanRequestRelatedPerson?.requestedPersonId
                    }
                }
            }
        }
        return pagedResultList
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}