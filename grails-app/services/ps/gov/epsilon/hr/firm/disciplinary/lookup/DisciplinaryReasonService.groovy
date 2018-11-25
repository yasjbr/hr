package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

/**
 * <h1>Purpose</h1>
 * this service is aim to create a disciplinary reasons
 * <h1>Usage</h1>
 * this service is used to create a reasons for  disciplinary
 * <h1>Restriction</h1>
 * need a firm and disciplinary category created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DisciplinaryReasonService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryCategories.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "trackingInfo.createdBy", type: "String", source: 'domain']
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
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

        String disciplinaryCategoryId = params["disciplinaryCategory.id"]
        String disciplinaryJudgmentId = params["disciplinaryJudgment.id"]

        List<Map<String, String>> orderBy = params.list("orderBy")
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String disciplinaryCategoriesId = params["disciplinaryCategories.id"]
        Set joinedDisciplinaryJudgmentReasonsIds = params.listString("joinedDisciplinaryJudgmentReasons.id")
        String universalCode = params["universalCode"]

        String status = params["status"]


        return DisciplinaryReason.createCriteria().list(max: max, offset: offset) {
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

                if (disciplinaryCategoryId) {
                    eq("disciplinaryCategories.id", disciplinaryCategoryId)
                }

                if (disciplinaryJudgmentId) {
                    joinedDisciplinaryJudgmentReasons {
                        disciplinaryJudgment {
                            eq("id", disciplinaryJudgmentId)
                        }
                    }
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
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
                switch (columnName) {
                    case "disciplinaryCategories.descriptionInfo.localName":
                        disciplinaryCategories {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break;
                    default:
                        order(columnName, 'desc')
                        break;
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
     * @return DisciplinaryReason.
     */
    DisciplinaryReason save(GrailsParameterMap params) {
        DisciplinaryReason disciplinaryReasonInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            disciplinaryReasonInstance = DisciplinaryReason.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (disciplinaryReasonInstance.version > version) {
                    disciplinaryReasonInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('disciplinaryReason.label', null, 'disciplinaryReason', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this disciplinaryReason while you were editing")
                    return disciplinaryReasonInstance
                }
            }
            if (!disciplinaryReasonInstance) {
                disciplinaryReasonInstance = new DisciplinaryReason()
                disciplinaryReasonInstance.errors.reject('default.not.found.message', [messageSource.getMessage('disciplinaryReason.label', null, 'disciplinaryReason', LocaleContextHolder.getLocale())] as Object[], "This disciplinaryReason with ${params.id} not found")
                return disciplinaryReasonInstance
            }
        } else {
            disciplinaryReasonInstance = new DisciplinaryReason()
        }
        try {


            disciplinaryReasonInstance.properties = params;

            if (disciplinaryReasonInstance?.id) {
                JoinedDisciplinaryJudgmentReason.executeUpdate("delete from JoinedDisciplinaryJudgmentReason cat where cat.disciplinaryReason.id = :disciplinaryReasonId", [disciplinaryReasonId: disciplinaryReasonInstance?.id])

            }

            //to get list of disciplinary judgment by ids
            List judgmentReasonsIds = params.list("judgmentReasons.id") ?: ["-1"]
            params.remove("judgmentReasons.id")
            List<DisciplinaryJudgment> judgmentReasonsList = DisciplinaryJudgment.findAllByIdInList(judgmentReasonsIds)
            judgmentReasonsList?.each { DisciplinaryJudgment disciplinaryJudgment ->
                disciplinaryReasonInstance.addToJoinedDisciplinaryJudgmentReasons(new JoinedDisciplinaryJudgmentReason(disciplinaryJudgment: disciplinaryJudgment, disciplinaryReason: disciplinaryReasonInstance, firm: disciplinaryReasonInstance.firm))
            }
            disciplinaryReasonInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            disciplinaryReasonInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return disciplinaryReasonInstance
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

            DisciplinaryReason instance = DisciplinaryReason.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('disciplinaryReason.deleteMessage.label')
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
     * @return DisciplinaryReason.
     */
    @Transactional(readOnly = true)
    DisciplinaryReason getInstance(GrailsParameterMap params) {
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