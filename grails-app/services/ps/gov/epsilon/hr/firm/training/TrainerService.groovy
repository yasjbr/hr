package ps.gov.epsilon.hr.firm.training

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
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
class TrainerService {

    MessageSource messageSource
    def formatService
    PersonService personService



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personId", type: "Long", source: 'domain'],
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
        String employeeId = params["employeeId"]
        String note = params["note"]
        Long personId = params.long("personId")

        return Trainer.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                    ilike("note", sSearch)
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
                    eq("employeeId", employeeId)
                }
                if(note){
                    ilike("note", "%${note}%")
                }
                if(personId){
                    eq("personId", personId)
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
        List personIds = pagedResultList.resultList.personId.toList()
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
        List<PersonDTO> persons = personService.searchPerson(searchBean)?.resultList

        if(personIds) {
            pagedResultList.resultList.each { Trainer trainer ->
                if(trainer.personId){
                    trainer.transientData.personDTO = persons.find { it.id == trainer.personId }
                }
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return Trainer.
 */
    Trainer save(GrailsParameterMap params) {
        Trainer trainerInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            trainerInstance = Trainer.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (trainerInstance.version > version) {
                    trainerInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('trainer.label', null, 'trainer',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this trainer while you were editing")
                    return trainerInstance
                }
            }
            if (!trainerInstance) {
                trainerInstance = new Trainer()
                trainerInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('trainer.label', null, 'trainer',LocaleContextHolder.getLocale())] as Object[], "This trainer with ${params.id} not found")
                return trainerInstance
            }
        } else {
            trainerInstance = new Trainer()
        }
        try {
            trainerInstance.properties = params;
            trainerInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            trainerInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return trainerInstance
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
                Trainer.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                Trainer.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
 * @return Trainer.
 */
    @Transactional(readOnly = true)
    Trainer getInstance(GrailsParameterMap params) {
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
        String idProperty = params["idProperty"]?:"id"
        String nameProperty = params["nameProperty"]?:"transientData.personDTO.localFullName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo")?:[]
        try {
            grails.gorm.PagedResultList resultList = this.searchWithRemotingValues(params)
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}