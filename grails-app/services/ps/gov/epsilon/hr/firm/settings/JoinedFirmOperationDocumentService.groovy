package ps.gov.epsilon.hr.firm.settings

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

/**
 * <h1>Purpose</h1>
 * -this service is aims to select  document for operation
 * <h1>Usage</h1>
 * -this service is used to select document for operation
 * <h1>Restriction</h1>
 * -need a document created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JoinedFirmOperationDocumentService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "operation", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.count", type: "integer", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "transientData.operation", type: "string", source: 'domain']
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
        String firmDocumentId = params["firmDocument.id"]
        Boolean isMandatory = params.boolean("isMandatory")
        ps.gov.epsilon.hr.enums.v1.EnumOperation operation = params["operation"] ? ps.gov.epsilon.hr.enums.v1.EnumOperation.valueOf(params["operation"]) : null

        return JoinedFirmOperationDocument.createCriteria().list(max: max, offset: offset) {
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
                if (firmDocumentId) {
                    eq("firmDocument.id", firmDocumentId)
                }
                if (isMandatory) {
                    eq("isMandatory", isMandatory)
                }
                if (operation) {
                    eq("operation", operation)
                }
                firmDocument{
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
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
     * to search model entries without duplicate in operations.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithoutDuplicate(GrailsParameterMap params) {
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
        String firmDocumentId = params["firmDocument.id"]
        Boolean isMandatory = params.boolean("isMandatory")
        Long documentCount = params.long("documentCount")
        ps.gov.epsilon.hr.enums.v1.EnumOperation operation = params["operation"] ? ps.gov.epsilon.hr.enums.v1.EnumOperation.valueOf(params["operation"]) : null

        Map sqlParamsMap = [:]
        sqlParamsMap.put("max", max)
        sqlParamsMap.put("offset", offset)
        sqlParamsMap.put("firmId", PCPSessionUtils.getValue("firmId"))

        String queryStr = "select operation, count(*) from JoinedFirmOperationDocument where firmDocument.firm.id=:firmId ";


        // if statements to check the params
        if (id) {
            queryStr = queryStr + " and id = :idParam"
            sqlParamsMap.put("idParam", id)
        }
        if (firmDocumentId) {
            queryStr += " and firmDocument.id = :firmDocumentIdParam"
            sqlParamsMap.put("firmDocumentIdParam", firmDocumentId)
        }
        if (isMandatory) {
            queryStr += " and isMandatory = :isMandatoryParam"
            sqlParamsMap.put("isMandatoryParam", isMandatory)
        }
        if (operation) {
            queryStr += " and operation = :operationParam"
            sqlParamsMap.put("operationParam", operation)
        }

        // group by operation name
        queryStr += " group by operation"

        if (documentCount != null) {
            queryStr += " HAVING COUNT(*) = :documentCountParam"
            sqlParamsMap.put("documentCountParam", documentCount)
        }

        // to apply sorting & sorting direction into sql query
        if (columnName) {
            switch (columnName) {
                case 'transientData.count':
                    queryStr += " order by count(*) " + dir
                    break;
                case 'operation':
                    queryStr += " order by operation " + dir
                    break;
                default:
                    break;
            }
        }

        // execute query
        List<EnumOperation, Integer> result = JoinedFirmOperationDocument.executeQuery(queryStr, sqlParamsMap)


        //group by operation name
        List resultList = []
        def object = [:]

        //to get one instance from each operation
        result.eachWithIndex { value, key ->
            object.operation = value.getAt(0)
            object.transientData = [count: Integer.parseInt(value.getAt(1) + ""), operation: value.getAt(0)]
            resultList.push(object)
            object = [:]
        }

        PagedResultList pagedResultList = new PagedResultList()
        pagedResultList.resultList = resultList
        pagedResultList.totalCount = JoinedFirmOperationDocument.executeQuery("select count(DISTINCT operation) from JoinedFirmOperationDocument where firmDocument.firm.id=:firmId", [firmId: PCPSessionUtils.getValue("firmId")])[0]

        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return JoinedFirmOperationDocument.
 */
    JoinedFirmOperationDocument save(GrailsParameterMap params) {
        JoinedFirmOperationDocument joinedFirmOperationDocumentInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            joinedFirmOperationDocumentInstance = JoinedFirmOperationDocument.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedFirmOperationDocumentInstance.version > version) {
                    joinedFirmOperationDocumentInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('joinedFirmOperationDocument.label', null, 'joinedFirmOperationDocument', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedFirmOperationDocument while you were editing")
                    return joinedFirmOperationDocumentInstance
                }
            }
            if (!joinedFirmOperationDocumentInstance) {
                joinedFirmOperationDocumentInstance = new JoinedFirmOperationDocument()
                joinedFirmOperationDocumentInstance.errors.reject('default.not.found.message', [messageSource.getMessage('joinedFirmOperationDocument.label', null, 'joinedFirmOperationDocument', LocaleContextHolder.getLocale())] as Object[], "This joinedFirmOperationDocument with ${params.id} not found")
                return joinedFirmOperationDocumentInstance
            }
        } else {
            joinedFirmOperationDocumentInstance = new JoinedFirmOperationDocument()
        }
        try {


            List firmDocumentIds = params.list("firmDocument")
            List isMandatoryValues = params.list("isMandatory")
            EnumOperation enumOperation = EnumOperation.valueOf(params["operation"])

            //to validate operation is not null
            if (!enumOperation) {
                joinedFirmOperationDocumentInstance.errors.reject('joinedFirmOperationDocument.errorOperation.label', [] as Object[], "")
            }
            //to validate isMandatory is not null
            else if (!isMandatoryValues) {
                joinedFirmOperationDocumentInstance.errors.reject('joinedFirmOperationDocument.errorIsMandatory.label', [] as Object[], "")
            }
            //to validate document is not null
            else if (!firmDocumentIds) {
                joinedFirmOperationDocumentInstance.errors.reject('joinedFirmOperationDocument.errorFirmDocument.label', [] as Object[], "")
            } else {

                //to remove all documents by operation
                if (params["id"]) {
                    JoinedFirmOperationDocument.executeUpdate("delete from JoinedFirmOperationDocument joinedFirmOperationDocument where joinedFirmOperationDocument.operation = :operation ", [operation: joinedFirmOperationDocumentInstance?.operation])
                }

                firmDocumentIds?.eachWithIndex { value, index ->
                    joinedFirmOperationDocumentInstance = new JoinedFirmOperationDocument(firmDocument: FirmDocument.load(value),
                            isMandatory: isMandatoryValues.get(index), operation: enumOperation)
                    joinedFirmOperationDocumentInstance.save(flush: true)
                }
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedFirmOperationDocumentInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedFirmOperationDocumentInstance
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

            JoinedFirmOperationDocument instance = JoinedFirmOperationDocument.get(id)
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
 * @return JoinedFirmOperationDocument.
 */
    @Transactional(readOnly = true)
    JoinedFirmOperationDocument getInstance(GrailsParameterMap params) {
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
     * to get model entry with all documents.
     * @param GrailsParameterMap params the search map.
     * @return JoinedFirmOperationDocument.
     */
    @Transactional(readOnly = true)
    JoinedFirmOperationDocument getInstanceByOperation(GrailsParameterMap params) {

        if (params['transientData.operation']) {
            params.operation = params['transientData.operation']
        }

        PagedResultList results = search(params)
        if (results) {
            JoinedFirmOperationDocument firmOperationDocument = results[0]
            def transientData = []
            def documentIdList = []
            //to get all documents for the operation
            results?.eachWithIndex { value, index ->
                transientData.push([firmDocument: value?.firmDocument, isMandatory: value?.isMandatory, isMandatoryTranslated: value?.isMandatory ? "نعم" : "لا"])
                documentIdList.push(value?.firmDocument?.id)
            }
            //to assign firm documents for the operation
            firmOperationDocument.transientData = [firmDocumentOperation: transientData, documentIdList: documentIdList]
            return firmOperationDocument
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
        String nameProperty = params["nameProperty"] ?: "operation"
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
     * this service is used to get the type of attachment for this module
     * @return
     */
    List getAttachmentType(EnumOperation enumOperation) {
        List attachmentTypeList = []
        List<JoinedFirmOperationDocument> listOfAtt = JoinedFirmOperationDocument.createCriteria().list {
            eq('operation', enumOperation)
            firmDocument {
                eq('firm.id', PCPSessionUtils.getValue("firmId"))
            }
        }
        listOfAtt?.each {
            attachmentTypeList.add([id: it.firmDocument.id, text: it.firmDocument.descriptionInfo.localName])
        }
        return attachmentTypeList
    }
}