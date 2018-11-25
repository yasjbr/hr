package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

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
class JoinedInterviewCommitteeRoleService {

    MessageSource messageSource
    def formatService



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: false, hidden: false, name: "committeeRole.descriptionInfo.localName", type: "string", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "partyName", type: "String", source: 'domain']
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
        [sort: true, search: false, hidden: false, name: "committeeRole.descriptionInfo.localName", type: "string", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "partyName", type: "String", source: 'domain']
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params, Boolean isEncrypted = false){
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
        List<String> ids = params.list('ids[]')
        String id
        if(isEncrypted && params.id) {
            id = HashHelper.decode(params.id as String)?:-1L
        }else {
            id = params.id
        }


        List<Map<String,String>> orderBy = params.list("orderBy")
        String committeeRoleId = params["committeeRole.id"]
        String interviewId = params["interview.id"]
        String partyName = params["partyName"]

        return JoinedInterviewCommitteeRole.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                    ilike("partyName", sSearch)
                    committeeRole{
                        descriptionInfo{
                            ilike("localName",sSearch)
                        }
                    }
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                        if(committeeRoleId){
                    eq("committeeRole.id", committeeRoleId)
                }
                        if(interviewId){
                    eq("interview.id", interviewId)
                }
                        if(partyName){
                            ilike("partyName", "%${partyName}%")
                        }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "committeeRole.descriptionInfo.localName":
                        order("committeeRole", dir)
                        break;
                    default:
                        order(columnName, dir)
                        break;
                }
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return JoinedInterviewCommitteeRole.
     */
    JoinedInterviewCommitteeRole save(GrailsParameterMap params) {
        JoinedInterviewCommitteeRole joinedInterviewCommitteeRoleInstance
        if (params.id) {
            joinedInterviewCommitteeRoleInstance = JoinedInterviewCommitteeRole.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedInterviewCommitteeRoleInstance.version > version) {
                    joinedInterviewCommitteeRoleInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('joinedInterviewCommitteeRole.label', null, 'joinedInterviewCommitteeRole',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedInterviewCommitteeRole while you were editing")
                    return joinedInterviewCommitteeRoleInstance
                }
            }
            if (!joinedInterviewCommitteeRoleInstance) {
                joinedInterviewCommitteeRoleInstance = new JoinedInterviewCommitteeRole()
                joinedInterviewCommitteeRoleInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('joinedInterviewCommitteeRole.label', null, 'joinedInterviewCommitteeRole',LocaleContextHolder.getLocale())] as Object[], "This joinedInterviewCommitteeRole with ${params.id} not found")
                return joinedInterviewCommitteeRoleInstance
            }
        } else {
            joinedInterviewCommitteeRoleInstance = new JoinedInterviewCommitteeRole()
        }
        try {
            joinedInterviewCommitteeRoleInstance.properties = params;
            joinedInterviewCommitteeRoleInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedInterviewCommitteeRoleInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedInterviewCommitteeRoleInstance
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
                JoinedInterviewCommitteeRole.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                JoinedInterviewCommitteeRole.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
     * @return JoinedInterviewCommitteeRole.
     */
    @Transactional(readOnly = true)
    JoinedInterviewCommitteeRole getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if(params.id && isEncrypted){
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id) ?: -1L
        }
        def results = this.search(params)
        if (results) {
            return results[0]
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}