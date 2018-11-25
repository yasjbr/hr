package ps.gov.epsilon.aoc.correspondences

import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

@Transactional
class AocListRecordNoteService {

    def messageSource
    FormatService formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "noteDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain', wrapped:true],
    ]

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
        Long listRecordId = params.long("listRecord.id")

        return AocListRecordNote.createCriteria().list(max: max, offset: offset) {
            if (listRecordId) {
                eq("listRecord.id", listRecordId)
            }
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

    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    AocListRecordNote save(GrailsParameterMap params) {
        AocListRecordNote aocListRecordNoteInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.remove('encodedId'))
        }


        if (params.id) {
            aocListRecordNoteInstance = AocListRecordNote.get(params.remove("id"))
            if (params.long("version")) {
                long version = params.long("version")
                params.remove('version')
                if (aocListRecordNoteInstance.version > version) {
                    aocListRecordNoteInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('aocListRecordNoteInstance.label', null, 'aocListRecordNoteInstance', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this aocListRecordNote while you were editing")
                    return aocListRecordNoteInstance
                }
            }
            if (!aocListRecordNoteInstance) {
                aocListRecordNoteInstance = new AocListRecordNote()
                aocListRecordNoteInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocListRecordNoteInstance.label', null, 'aocListRecordNoteInstance', LocaleContextHolder.getLocale())] as Object[], "This aocListRecordNote with ${params.id} not found")
                return aocListRecordNoteInstance
            }
        }else{
            aocListRecordNoteInstance= new AocListRecordNote()
        }

        try {
            aocListRecordNoteInstance.properties= params
            if(!aocListRecordNoteInstance.note){
                aocListRecordNoteInstance.errors.rejectValue("note", "profileNote.note.required.error")
                throw new ValidationException("Note is mandatory", aocListRecordNoteInstance.errors)
            }
            aocListRecordNoteInstance.save(failOnError: true);
        } catch (ValidationException ve) {
            log.error("Failed to save reocrd note", ve)
            if(!aocListRecordNoteInstance.hasErrors())
                aocListRecordNoteInstance.errors = ve.errors
        } catch (Exception ex) {
            log.error("Failed to save reocrd note", ex)
            aocListRecordNoteInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }

        return aocListRecordNoteInstance
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
                AocListRecordNote.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                AocListRecordNote.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }
}
