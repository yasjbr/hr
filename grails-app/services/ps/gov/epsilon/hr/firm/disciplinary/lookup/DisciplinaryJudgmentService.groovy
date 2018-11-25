package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO

/**
 * <h1>Purpose</h1>
 * this service is aims to create a disciplinary judgment
 * <h1>Usage</h1>
 * this service is used to create a judgment for disciplinary
 * <h1>Restriction</h1>
 * -need a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DisciplinaryJudgmentService {

    MessageSource messageSource
    def formatService
    UnitOfMeasurementService unitOfMeasurementService
    CurrencyService currencyService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "trackingInfo.createdBy", type: "String", source: 'domain']
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "trackingInfo.createdBy", type: "String", source: 'domain']
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

        String disciplinaryReasonId = params["disciplinaryReason.id"]
        List<Map<String, String>> orderBy = params.list("orderBy")
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String disciplinaryCategoriesId = params["disciplinaryCategories.id"]
        Set joinedDisciplinaryJudgmentReasonsIds = params.listString("joinedDisciplinaryJudgmentReasons.id")
        String universalCode = params["universalCode"]
        String status = params["status"]

        return DisciplinaryJudgment.createCriteria().list(max: max, offset: offset) {
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
                if (disciplinaryCategoriesId) {
                    eq("disciplinaryCategories.id", disciplinaryCategoriesId)
                }
                if (joinedDisciplinaryJudgmentReasonsIds) {
                    joinedDisciplinaryJudgmentReasons {
                        inList("id", joinedDisciplinaryJudgmentReasonsIds)
                    }
                }
                if (universalCode) {
                    ilike("universalCode", "%${universalCode}%")
                }

                if (disciplinaryReasonId) {
                    joinedDisciplinaryJudgmentReasons {
                        disciplinaryReason {
                            eq("id", disciplinaryReasonId)
                        }
                    }
                }

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                eq("firm.id", PCPSessionUtils.getValue("firmId"))
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
     * @return DisciplinaryJudgment.
     */
    DisciplinaryJudgment save(GrailsParameterMap params) {
        DisciplinaryJudgment disciplinaryJudgmentInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            disciplinaryJudgmentInstance = DisciplinaryJudgment.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (disciplinaryJudgmentInstance.version > version) {
                    disciplinaryJudgmentInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('disciplinaryJudgment.label', null, 'disciplinaryJudgment', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this disciplinaryJudgment while you were editing")
                    return disciplinaryJudgmentInstance
                }
            }
            if (!disciplinaryJudgmentInstance) {
                disciplinaryJudgmentInstance = new DisciplinaryJudgment()
                disciplinaryJudgmentInstance.errors.reject('default.not.found.message', [messageSource.getMessage('disciplinaryJudgment.label', null, 'disciplinaryJudgment', LocaleContextHolder.getLocale())] as Object[], "This disciplinaryJudgment with ${params.id} not found")
                return disciplinaryJudgmentInstance
            }
        } else {
            disciplinaryJudgmentInstance = new DisciplinaryJudgment()
        }
        try {

            disciplinaryJudgmentInstance.properties = params;

            //to remove disciplinary judgment reasons
            if (disciplinaryJudgmentInstance?.id) {
                JoinedDisciplinaryJudgmentReason.executeUpdate("delete from JoinedDisciplinaryJudgmentReason cat where cat.disciplinaryJudgment.id = :disciplinaryJudgmentId", [disciplinaryJudgmentId: disciplinaryJudgmentInstance?.id])
                disciplinaryJudgmentInstance?.unitIds?.removeAll();
                disciplinaryJudgmentInstance?.unitIds?.clear();
            }

            //to get list of disciplinary reason by ids
            List judgmentReasonsIds = params.list("judgmentReasons.id") ?: ["-1"]
            params.remove("judgmentReasons.id")
            List<DisciplinaryReason> judgmentReasonsList = DisciplinaryReason?.findAllByIdInList(judgmentReasonsIds)
            judgmentReasonsList?.each { DisciplinaryReason disciplinaryReason ->
                disciplinaryJudgmentInstance.addToJoinedDisciplinaryJudgmentReasons(new JoinedDisciplinaryJudgmentReason(disciplinaryJudgment: disciplinaryJudgmentInstance, disciplinaryReason: disciplinaryReason, firm: disciplinaryJudgmentInstance.firm))
            }

            //to get list of unit Ids
            List unitIds = params.listString("unitIds")
            params.remove("unitIds")
            unitIds?.eachWithIndex { value, index ->
                disciplinaryJudgmentInstance.addToUnitIds(value)
            }

            disciplinaryJudgmentInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            disciplinaryJudgmentInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return disciplinaryJudgmentInstance
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

            DisciplinaryJudgment instance = DisciplinaryJudgment.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('disciplinaryJudgment.deleteMessage.label')
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
     * @return DisciplinaryJudgment.
     */
    @Transactional(readOnly = true)
    DisciplinaryJudgment getInstance(GrailsParameterMap params) {
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return DisciplinaryJudgment.
     */
    @Transactional(readOnly = true)
    DisciplinaryJudgment getInstanceWithRemotingValue(GrailsParameterMap params) {
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
        PagedResultList pagedResultList = search(params)
        List ids = pagedResultList?.resultList?.unitIds?.get(0)?.toList()
        List currencyIds = pagedResultList?.resultList?.currencyIds?.get(0)?.toList()
        if (ids) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: ids))
            List<UnitOfMeasurementDTO> unitOfMeasurementDTOList = unitOfMeasurementService.searchUnitOfMeasurement(searchBean)?.resultList
            pagedResultList.resultList.each { DisciplinaryJudgment disciplinaryJudgment ->
                disciplinaryJudgment.transientData = [:]
                disciplinaryJudgment.transientData.put("unitOfMeasurementList", unitOfMeasurementDTOList?.collect {
                    [it.id, it.descriptionInfo.localName]
                })
                disciplinaryJudgment.transientData.put("unitOfMeasurementNameList", unitOfMeasurementDTOList?.collect {
                    it.descriptionInfo.localName
                })
            }
        }

        if (currencyIds) {
            SearchBean currencySearchBean = new SearchBean()
            currencySearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: currencyIds))
            List<CurrencyDTO> currencyDTOList = currencyService.searchCurrency(currencySearchBean)?.resultList
            pagedResultList.resultList.each { DisciplinaryJudgment disciplinaryJudgment ->
                disciplinaryJudgment.transientData = [:]
                disciplinaryJudgment.transientData.put("currencyList", currencyDTOList?.collect {
                    [it.id, it.descriptionInfo.localName]
                })
                disciplinaryJudgment.transientData.put("currencyNameList", currencyDTOList?.collect {
                    it.descriptionInfo.localName
                })
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
     * get judgments depends on list of reason.
     * @param List disciplinaryReasonIds the Reasons to filter.
     * @return List.
     */
    @Transactional(readOnly = true)
    List getJoinedReasonJudgments(List disciplinaryReasonIds, List disciplinaryJudgmentList = null) {
        if (!disciplinaryReasonIds) disciplinaryReasonIds = ["-1"]
        def disciplinaryJudgments = JoinedDisciplinaryJudgmentReason.createCriteria().list {
            inList("disciplinaryReason.id", disciplinaryReasonIds)

            and {
                if (disciplinaryJudgmentList) {
                    disciplinaryJudgment {
                        not {
                            inList("id",disciplinaryJudgmentList)
                        }
                    }
                }
            }

            projections {
                groupProperty("disciplinaryJudgment")
            }
        }
        return disciplinaryJudgments
    }

}