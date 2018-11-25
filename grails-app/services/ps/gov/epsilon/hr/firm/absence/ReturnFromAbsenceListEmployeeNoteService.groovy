package ps.gov.epsilon.hr.firm.absence

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
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ReturnFromAbsenceListEmployeeNoteService {

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
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain', wrapped:true]
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
        Long returnFromAbsenceListEmployeeId = params.long("returnFromAbsenceListEmployee.id")

        return ReturnFromAbsenceListEmployeeNote.createCriteria().list(max: max, offset: offset) {
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
                if (ids) {
                    inList("id", ids)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (noteDate) {
                    le("noteDate", noteDate)
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (returnFromAbsenceListEmployeeId) {
                    eq("returnFromAbsenceListEmployee.id", returnFromAbsenceListEmployeeId)
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
 * @return ReturnFromAbsenceListEmployeeNote.
 */
    ReturnFromAbsenceListEmployeeNote save(GrailsParameterMap params) {
        ReturnFromAbsenceListEmployeeNote returnFromAbsenceListEmployeeNoteInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            returnFromAbsenceListEmployeeNoteInstance = ReturnFromAbsenceListEmployeeNote.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (returnFromAbsenceListEmployeeNoteInstance.version > version) {
                    returnFromAbsenceListEmployeeNoteInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('returnFromAbsenceListEmployeeNote.label', null, 'returnFromAbsenceListEmployeeNote', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this returnFromAbsenceListEmployeeNote while you were editing")
                    return returnFromAbsenceListEmployeeNoteInstance
                }
            }
            if (!returnFromAbsenceListEmployeeNoteInstance) {
                returnFromAbsenceListEmployeeNoteInstance = new ReturnFromAbsenceListEmployeeNote()
                returnFromAbsenceListEmployeeNoteInstance.errors.reject('default.not.found.message', [messageSource.getMessage('returnFromAbsenceListEmployeeNote.label', null, 'returnFromAbsenceListEmployeeNote', LocaleContextHolder.getLocale())] as Object[], "This returnFromAbsenceListEmployeeNote with ${params.id} not found")
                return returnFromAbsenceListEmployeeNoteInstance
            }
        } else {
            returnFromAbsenceListEmployeeNoteInstance = new ReturnFromAbsenceListEmployeeNote()
        }
        try {
            returnFromAbsenceListEmployeeNoteInstance.properties = params;
            returnFromAbsenceListEmployeeNoteInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            returnFromAbsenceListEmployeeNoteInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return returnFromAbsenceListEmployeeNoteInstance
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
                ReturnFromAbsenceListEmployeeNote.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                ReturnFromAbsenceListEmployeeNote.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return ReturnFromAbsenceListEmployeeNote.
 */
    @Transactional(readOnly = true)
    ReturnFromAbsenceListEmployeeNote getInstance(GrailsParameterMap params) {
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