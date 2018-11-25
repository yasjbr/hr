package ps.gov.epsilon.hr.firm.lookups

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
 * this service is aims to create inspection  for firm
 * <h1>Usage</h1>
 * -used for applicant, training and recruitment cycle
 * <h1>Restriction</h1>
 * - needs a firm and inspection category created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class InspectionService {

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
            [sort: true, search: false, hidden: false, name: "inspectionCategory.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hasMark", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hasPeriod", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hasDates", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "isIncludedInLists", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "orderId", type: "short", source: 'domain'],
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
        List<Map<String, String>> orderBy = params.list("orderBy")
        Set committeeRolesIds = params.listString("committeeRoles.id")
        String description = params["description"]
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String hasMark = params["hasMark"]
        String hasPeriod = params["hasPeriod"]
        String hasDates = params["hasDates"]
        Boolean isIncludedInLists = params.boolean("isIncludedInLists")
        Boolean allInspection = params.boolean("allInspection")
        String inspectionCategoryId = params["inspectionCategory.id"]
        String note = params["note"]
        Short orderId = params.long("orderId")
        String universalCode = params["universalCode"]
        String status = params["status"]

        return Inspection.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
                    ilike("note", sSearch)
                    ilike("universalCode", sSearch)
                    inspectionCategory {
                        descriptionInfo {
                            ilike('localName', sSearch)
                        }
                    }
                }
            }

            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (committeeRolesIds) {
                    committeeRoles {
                        committeeRole {
                            inList("id", committeeRolesIds)
                        }
                    }
                }
                if (description) {
                    ilike("description", "%${description}%")
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))

                if (allInspection) {
                    if (hasMark) {
                        eq("hasMark", Boolean.parseBoolean(hasMark))
                    }
                    if (hasDates) {
                        eq("hasDates", Boolean.parseBoolean(hasDates))
                    }
                    if (hasPeriod) {
                        eq("hasPeriod", Boolean.parseBoolean(hasPeriod))
                    }
                    if (isIncludedInLists != null) {
                        eq("isIncludedInLists", isIncludedInLists)
                    }
                }

                if (inspectionCategoryId) {
                    eq("inspectionCategory.id", inspectionCategoryId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderId) {
                    eq("orderId", orderId)
                }
                if (universalCode) {
                    ilike("universalCode", "%${universalCode}%")
                }
            }

            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "inspectionCategory.descriptionInfo.localName":
                        order("inspectionCategory", dir)
                        break;
                    default:
                        order(columnName, dir)
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
 * @return Inspection.
 */
    Inspection save(GrailsParameterMap params) {
        Inspection inspectionInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params["id"]) {
            inspectionInstance = Inspection.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (inspectionInstance.version > version) {
                    inspectionInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('inspection.label', null, 'inspection', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this inspection while you were editing")
                    return inspectionInstance
                }
            }
            if (!inspectionInstance) {
                inspectionInstance = new Inspection()
                inspectionInstance.errors.reject('default.not.found.message', [messageSource.getMessage('inspection.label', null, 'inspection', LocaleContextHolder.getLocale())] as Object[], "This inspection with ${params["id"]} not found")
                return inspectionInstance
            }
        } else {
            inspectionInstance = new Inspection()
        }
        try {

            if (inspectionInstance?.id) {
                JoinedInspectionCommitteeRole?.executeUpdate("delete from JoinedInspectionCommitteeRole cat where cat.inspection.id = :inspectionId", [inspectionId: inspectionInstance?.id])
            }



            params.remove("committeeRoles.id_helper1")
            params.remove("committeeRoles")



            //to get list of committee by ids
            List committeeRoleIds = params.list("committeeRoles.id")
            params.remove("committeeRoles.id")
            List<CommitteeRole> committeeRolesList = null

            if (committeeRoleIds) {
                committeeRolesList = CommitteeRole?.findAllByIdInList(committeeRoleIds)
            }

            committeeRolesList?.each { CommitteeRole committeeRole ->
                inspectionInstance.addToCommitteeRoles(new JoinedInspectionCommitteeRole(inspection: inspectionInstance, committeeRole: committeeRole))
            }
            inspectionInstance.properties = params;
            inspectionInstance.save();
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            inspectionInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return inspectionInstance
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

            Inspection instance = Inspection.get(id)
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
 * @return Inspection.
 */
    @Transactional(readOnly = true)
    Inspection getInstance(GrailsParameterMap params) {
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