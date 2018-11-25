package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.lookups.JobCategory
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

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
class EvaluationTemplateService {

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
            [sort: true, search: true, hidden: false, name: "templateType", type: "enum", source: 'domain'],
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
        Set evaluationCriteriaIds = params.listString("evaluationCriteria.id")
        Set evaluationSectionsIds = params.listString("evaluationSections.id")
        Set evaluationTemplateCategoryIds = params.listString("evaluationTemplateCategory.id")
        Long firmId = params.long("firm.id")?params.long("firm.id"):PCPSessionUtils.getValue("firmId")
        ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType templateType = params["templateType"] ? ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType.valueOf(params["templateType"]) : null
        String universalCode = params["universalCode"]
        String status = params["status"]

        return EvaluationTemplate.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
                    ilike("universalCode", sSearch)
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
                if (evaluationCriteriaIds) {
                    evaluationCriteria {
                        inList("id", evaluationCriteriaIds)
                    }
                }
                if (evaluationSectionsIds) {
                    evaluationSections {
                        inList("id", evaluationSectionsIds)
                    }
                }
                if (evaluationTemplateCategoryIds) {
                    evaluationTemplateCategory {
                        inList("id", evaluationTemplateCategoryIds)
                    }
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (templateType) {
                    eq("templateType", templateType)
                }
                if (universalCode) {
                    ilike("universalCode", "%${universalCode}%")
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
     * @return EvaluationTemplate.
     */
    EvaluationTemplate save(GrailsParameterMap params) {
        EvaluationTemplate evaluationTemplateInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            evaluationTemplateInstance = EvaluationTemplate.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (evaluationTemplateInstance.version > version) {
                    evaluationTemplateInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('evaluationTemplate.label', null, 'evaluationTemplate', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this evaluationTemplate while you were editing")
                    return evaluationTemplateInstance
                }
            }
            if (!evaluationTemplateInstance) {
                evaluationTemplateInstance = new EvaluationTemplate()
                evaluationTemplateInstance.errors.reject('default.not.found.message', [messageSource.getMessage('evaluationTemplate.label', null, 'evaluationTemplate', LocaleContextHolder.getLocale())] as Object[], "This evaluationTemplate with ${params.id} not found")
                return evaluationTemplateInstance
            }
        } else {
            evaluationTemplateInstance = new EvaluationTemplate()
        }
        try {

            evaluationTemplateInstance.properties = params;
            List<String> militaryRankIds = params.list("militaryRank.id")
            List<String> jobCategoryIds = params.list("jobCategory.id")

            if(militaryRankIds.size() == 0 && jobCategoryIds.size() == 0){
                evaluationTemplateInstance.errors.reject("evaluationTemplate.joinTable.error")
                return evaluationTemplateInstance
            }

            //remove records from hasMany tables when edit template
            if (evaluationTemplateInstance?.id) {
                JoinedEvaluationTemplateCategory.executeUpdate('delete from JoinedEvaluationTemplateCategory etc where evaluationTemplate.id =:evaluationTemplateId', ['evaluationTemplateId': evaluationTemplateInstance?.id])
            }

            //to assign list of military rank for template
            militaryRankIds?.each { String id ->
                evaluationTemplateInstance?.addToEvaluationTemplateCategory(evaluationTemplate: evaluationTemplateInstance, militaryRank: MilitaryRank.load(id))
            }

            //to assign list of military rank for template
            jobCategoryIds?.each { String id ->
                evaluationTemplateInstance?.addToEvaluationTemplateCategory(evaluationTemplate: evaluationTemplateInstance, jobCategory: JobCategory.load(id))
            }

            evaluationTemplateInstance.save(failOnError: true, flush: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            evaluationTemplateInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return evaluationTemplateInstance
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = search(params)
        if (pagedResultList) {
            pagedResultList?.each { EvaluationTemplate evaluationTemplate ->
                if (evaluationTemplate?.evaluationTemplateCategory?.militaryRank){
                    evaluationTemplate?.transientData.militaryRanks = evaluationTemplate?.evaluationTemplateCategory?.militaryRank
                }
                if(evaluationTemplate?.evaluationTemplateCategory?.jobCategory){
                    evaluationTemplate?.transientData.jobCategories = evaluationTemplate?.evaluationTemplateCategory?.jobCategory
                }

            }
        }
        return pagedResultList
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
            EvaluationTemplate instance = EvaluationTemplate.get(id)
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
     * @return EvaluationTemplate.
     */
    @Transactional(readOnly = true)
    EvaluationTemplate getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return Job.
     */
    @Transactional(readOnly = true)
    EvaluationTemplate getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
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