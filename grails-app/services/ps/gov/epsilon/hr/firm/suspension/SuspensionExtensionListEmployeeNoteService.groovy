package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service aims to create not for suspension extension list employee.
 * <h1>Usage</h1>
 * -this service used to create not for suspension extension list employee.
 * <h1>Restriction</h1>
 * -need a suspension list employee created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionExtensionListEmployeeNoteService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "noteDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain', wrapped:true],
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
        String note = params["note"]
        ZonedDateTime noteDate = PCPUtils.parseZonedDateTime(params['noteDate'])
        String orderNo = params["orderNo"]
        String suspensionExtensionListEmployeeId = params["suspensionExtensionListEmployee.id"]

        return SuspensionExtensionListEmployeeNote.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                    ilike("orderNo", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
//                if (ids) {
//                    inList("id", ids)
//                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (noteDate) {
                    le("noteDate", noteDate)
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (suspensionExtensionListEmployeeId) {
                    eq("suspensionExtensionListEmployee.id", suspensionExtensionListEmployeeId)
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
 * @return SuspensionExtensionListEmployeeNote.
 */
    SuspensionExtensionListEmployeeNote save(GrailsParameterMap params) {
        SuspensionExtensionListEmployeeNote suspensionExtensionListEmployeeNoteInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionExtensionListEmployeeNoteInstance = SuspensionExtensionListEmployeeNote.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionExtensionListEmployeeNoteInstance.version > version) {
                    suspensionExtensionListEmployeeNoteInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionExtensionListEmployeeNote.label', null, 'suspensionExtensionListEmployeeNote', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionExtensionListEmployeeNote while you were editing")
                    return suspensionExtensionListEmployeeNoteInstance
                }
            }
            if (!suspensionExtensionListEmployeeNoteInstance) {
                suspensionExtensionListEmployeeNoteInstance = new SuspensionExtensionListEmployeeNote()
                suspensionExtensionListEmployeeNoteInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionExtensionListEmployeeNote.label', null, 'suspensionExtensionListEmployeeNote', LocaleContextHolder.getLocale())] as Object[], "This suspensionExtensionListEmployeeNote with ${params.id} not found")
                return suspensionExtensionListEmployeeNoteInstance
            }
        } else {
            suspensionExtensionListEmployeeNoteInstance = new SuspensionExtensionListEmployeeNote()
        }
        try {
            suspensionExtensionListEmployeeNoteInstance.properties = params;

            //set default note date to current
            if (!suspensionExtensionListEmployeeNoteInstance.noteDate) {
                suspensionExtensionListEmployeeNoteInstance.noteDate = ZonedDateTime.now()
            }

            suspensionExtensionListEmployeeNoteInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            suspensionExtensionListEmployeeNoteInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionExtensionListEmployeeNoteInstance
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
                SuspensionExtensionListEmployeeNote.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                SuspensionExtensionListEmployeeNote.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return SuspensionExtensionListEmployeeNote.
 */
    @Transactional(readOnly = true)
    SuspensionExtensionListEmployeeNote getInstance(GrailsParameterMap params) {
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
        String nameProperty = params["nameProperty"] ?: "orderNo"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = search(params)
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