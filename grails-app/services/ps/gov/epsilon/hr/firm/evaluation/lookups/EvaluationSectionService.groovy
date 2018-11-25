package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

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
class EvaluationSectionService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "evaluationTemplate", type: "EvaluationTemplate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "index", type: "Integer", source: 'domain'],
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
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        Set evaluationItemsIds = params.listString("evaluationItems.id")
        String evaluationTemplateId = params["evaluationTemplate.id"]
        Long firmId = params.long("firm.id")?params.long("firm.id"):PCPSessionUtils.getValue("firmId")
        String hint = params["hint"]
        Integer index = params.long("index")
        String status = params["status"]

        return EvaluationSection.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
                    ilike("hint", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                if (evaluationItemsIds) {
                    evaluationItems {
                        inList("id", evaluationItemsIds)
                    }
                }
                if (evaluationTemplateId) {
                    eq("evaluationTemplate.id", evaluationTemplateId)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (hint) {
                    ilike("hint", "%${hint}%")
                }
                if (index) {
                    eq("index", index)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
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
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EvaluationSection.
     */
    EvaluationSection save(GrailsParameterMap params) {
        EvaluationSection evaluationSectionInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            evaluationSectionInstance = EvaluationSection.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (evaluationSectionInstance.version > version) {
                    evaluationSectionInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('evaluationSection.label', null, 'evaluationSection', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this evaluationSection while you were editing")
                    return evaluationSectionInstance
                }
            }
            if (!evaluationSectionInstance) {
                evaluationSectionInstance = new EvaluationSection()
                evaluationSectionInstance.errors.reject('default.not.found.message', [messageSource.getMessage('evaluationSection.label', null, 'evaluationSection', LocaleContextHolder.getLocale())] as Object[], "This evaluationSection with ${params.id} not found")
                return evaluationSectionInstance
            }
        } else {
            evaluationSectionInstance = new EvaluationSection()
        }
        try {

            evaluationSectionInstance.properties = params;

            List<String> localNames = params.listString("item_localName")
            List indexes = params.list("item_index")
            List maxMarks = params.list("item_maxMark")
            List<String> universalCodes = params.listString("item_universalCode");

            DescriptionInfo descriptionInfo
            List evaluationItemsList = []
            if(localNames && indexes && maxMarks){
                if(evaluationSectionInstance?.id){
                    EvaluationItem.executeUpdate("delete from EvaluationItem item where item.evaluationSection.id = :sectionId", [sectionId: evaluationSectionInstance?.id])
                }
                localNames.eachWithIndex{ String value, int i ->
                    descriptionInfo = new DescriptionInfo(localName: value)
                    evaluationItemsList << new EvaluationItem(descriptionInfo: descriptionInfo, index: indexes[i] ,
                            maxMark: indexes[i],
                            universalCode: universalCodes[i],
                            firm: evaluationSectionInstance.firm)
                }
            }

            params["evaluationItems"] = evaluationItemsList;
            evaluationSectionInstance.properties = params;
            evaluationSectionInstance.save(failOnError: true, flush:true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            evaluationSectionInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return evaluationSectionInstance
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
            EvaluationSection instance = EvaluationSection.get(id)
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
     * @return EvaluationSection.
     */
    @Transactional(readOnly = true)
    EvaluationSection getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id || params.universalCode) {
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

}