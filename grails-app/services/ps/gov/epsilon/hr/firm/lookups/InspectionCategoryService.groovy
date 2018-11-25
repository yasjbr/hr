package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.recruitment.Applicant
import ps.gov.epsilon.hr.firm.recruitment.ApplicantService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

/**
 * <h1>Purpose</h1>
 * this service is aims to create inspection category
 * <h1>Usage</h1>
 * -used for inspection
 * <h1>Restriction</h1>
 * - needs a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class InspectionCategoryService {

    MessageSource messageSource
    def formatService
    ApplicantService applicantService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isRequiredByFirmPolicy", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hasResultRate", type: "Boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hasMark", type: "Boolean", source: 'domain'],
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
        List notIncluded = params.listString('notIncluded')
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
        Set inspectionsIds = params.listString("inspections.id")
        String isRequiredByFirmPolicy = params["isRequiredByFirmPolicy"]
        String hasResultRate = params["hasResultRate"]
        String hasMark = params["hasMark"]
        String note = params["note"]
        Short orderId = params.long("orderId")
        String universalCode = params["universalCode"]
        String status = params["status"]
        String applicantId = params["applicant.id"]
        List selectedInspectionCategoryId
        Applicant applicant
        if (applicantId) {
            applicant = Applicant.findById(params['applicant.id'])
            selectedInspectionCategoryId = applicant?.inspectionCategoriesResult?.inspectionCategory?.toList()?.id ?: ["-1"]
        }

        return InspectionCategory.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
                    ilike("note", sSearch)
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

                if (applicantId) {
                    not {
                        if (selectedInspectionCategoryId) {
                            inList("id", selectedInspectionCategoryId)
                        }
                    }
                }
                if (notIncluded) {
                    not {
                        inList("id", notIncluded)
                    }
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
                if (inspectionsIds) {
                    inspections {
                        inList("id", inspectionsIds)
                    }
                }
                if (isRequiredByFirmPolicy) {
                    eq("isRequiredByFirmPolicy", Boolean.parseBoolean(isRequiredByFirmPolicy))
                }
                if (hasResultRate) {
                    eq("hasResultRate", Boolean.parseBoolean(hasResultRate))
                }
                if (hasMark) {
                    eq("hasMark", Boolean.parseBoolean(hasMark))
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
 * @return InspectionCategory.
 */
    InspectionCategory save(GrailsParameterMap params) {
        InspectionCategory inspectionCategoryInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params["id"]) {
            inspectionCategoryInstance = InspectionCategory.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (inspectionCategoryInstance.version > version) {
                    inspectionCategoryInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('inspectionCategory.label', null, 'inspectionCategory', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this inspectionCategory while you were editing")
                    return inspectionCategoryInstance
                }
            }
            if (!inspectionCategoryInstance) {
                inspectionCategoryInstance = new InspectionCategory()
                inspectionCategoryInstance.errors.reject('default.not.found.message', [messageSource.getMessage('inspectionCategory.label', null, 'inspectionCategory', LocaleContextHolder.getLocale())] as Object[], "This inspectionCategory with ${params["id"]} not found")
                return inspectionCategoryInstance
            }
        } else {
            inspectionCategoryInstance = new InspectionCategory()
        }
        try {

            if (inspectionCategoryInstance?.id) {
                JoinedInspectionCategoryCommitteeRole.executeUpdate("delete from JoinedInspectionCategoryCommitteeRole cat where cat.inspectionCategory.id = :inspectionCategoryId", [inspectionCategoryId: inspectionCategoryInstance?.id])
            }
            //to get list of committee by ids
            List committeeRoleIds = params.list("committeeRoles.id") ?: ["-1"]
            params.remove("committeeRoles.id")
            List<CommitteeRole> committeeRolesList = CommitteeRole.findAllByIdInList(committeeRoleIds)
            committeeRolesList.each { CommitteeRole committeeRole ->
                inspectionCategoryInstance.addToCommitteeRoles(new JoinedInspectionCategoryCommitteeRole(inspectionCategory: inspectionCategoryInstance, committeeRole: committeeRole))
            }

            inspectionCategoryInstance.properties = params;
            inspectionCategoryInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            inspectionCategoryInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return inspectionCategoryInstance
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

            InspectionCategory instance = InspectionCategory.get(id)
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
 * @return InspectionCategory.
 */
    @Transactional(readOnly = true)
    InspectionCategory getInstance(GrailsParameterMap params) {
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