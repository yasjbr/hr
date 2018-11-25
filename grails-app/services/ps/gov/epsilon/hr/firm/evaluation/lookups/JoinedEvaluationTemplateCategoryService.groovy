package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -correspondence table to join between evaluationTemplate and the category-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JoinedEvaluationTemplateCategoryService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "evaluationTemplate", type: "EvaluationTemplate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobCategory", type: "JobCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "militaryRank", type: "MilitaryRank", source: 'domain']
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
        String evaluationTemplateId = params["evaluationTemplate.id"]
        String jobCategoryId = params["jobCategory.id"]
        String militaryRankId = params["militaryRank.id"]

        String employeeId = params["employeeId"]
        if(employeeId){
            Employee employee = Employee.load(employeeId)
            if(employee){
                militaryRankId = employee?.currentEmployeeMilitaryRank?.militaryRank?.id
                jobCategoryId = employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id
            }
        }
        String status = params["status"]
        ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType templateType = params["templateType"] ? ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType.valueOf(params["templateType"]) : null

        return JoinedEvaluationTemplateCategory.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (evaluationTemplateId) {
                    eq("evaluationTemplate.id", evaluationTemplateId)
                }
                if (jobCategoryId) {
                    eq("jobCategory.id", jobCategoryId)
                }
                if (militaryRankId) {
                    eq("militaryRank.id", militaryRankId)
                }
                if (templateType) {
                    evaluationTemplate{
                        eq("templateType", templateType)
                    }
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                    evaluationTemplate{
                        ne("trackingInfo.status", GeneralStatus.valueOf(status))
                    }
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                    evaluationTemplate{
                        ne("trackingInfo.status", GeneralStatus.DELETED)
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
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return JoinedEvaluationTemplateCategory.
     */
    JoinedEvaluationTemplateCategory save(GrailsParameterMap params) {
        JoinedEvaluationTemplateCategory joinedEvaluationTemplateCategoryInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            joinedEvaluationTemplateCategoryInstance = JoinedEvaluationTemplateCategory.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedEvaluationTemplateCategoryInstance.version > version) {
                    joinedEvaluationTemplateCategoryInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('joinedEvaluationTemplateCategory.label', null, 'joinedEvaluationTemplateCategory', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedEvaluationTemplateCategory while you were editing")
                    return joinedEvaluationTemplateCategoryInstance
                }
            }
            if (!joinedEvaluationTemplateCategoryInstance) {
                joinedEvaluationTemplateCategoryInstance = new JoinedEvaluationTemplateCategory()
                joinedEvaluationTemplateCategoryInstance.errors.reject('default.not.found.message', [messageSource.getMessage('joinedEvaluationTemplateCategory.label', null, 'joinedEvaluationTemplateCategory', LocaleContextHolder.getLocale())] as Object[], "This joinedEvaluationTemplateCategory with ${params.id} not found")
                return joinedEvaluationTemplateCategoryInstance
            }
        } else {
            joinedEvaluationTemplateCategoryInstance = new JoinedEvaluationTemplateCategory()
        }
        try {
            joinedEvaluationTemplateCategoryInstance.properties = params;
            joinedEvaluationTemplateCategoryInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedEvaluationTemplateCategoryInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedEvaluationTemplateCategoryInstance
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
                JoinedEvaluationTemplateCategory.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                JoinedEvaluationTemplateCategory.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
     * @return JoinedEvaluationTemplateCategory.
     */
    @Transactional(readOnly = true)
    JoinedEvaluationTemplateCategory getInstance(GrailsParameterMap params) {
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
        String nameProperty = params["nameProperty"] ?: "evaluationTemplate.descriptionInfo.localName"
        Boolean isUniqueEvaluationTemplate = params.boolean("isUniqueEvaluationTemplate")
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            if(isUniqueEvaluationTemplate){
                nameProperty = "descriptionInfo.localName"
                dataList = PCPUtils.toMapList(resultList?.evaluationTemplate?.unique(), nameProperty, idProperty, autoCompleteReturnedInfo)
            }else{
                dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
            }
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