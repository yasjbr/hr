package ps.gov.epsilon.aoc.correspondences

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.util.Holders
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumDeliveryStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

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
class AocListRecordService {

    MessageSource messageSource
    def formatService
    AocListRecordNoteService aocListRecordNoteService
    def sessionFactory

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "joinedCorrespondenceListRecords", type: "Set", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "orderDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNo", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNotes", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordNotes", type: "Set", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        // global setting.
        Long aocCorresondenceListId = params.long('aocCorrespondenceList.id')
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = getDomainColumns()[column]?.name
        }

        List<Map<String, String>> orderBy = params.list("orderBy")
        ZonedDateTime orderDate = PCPUtils.parseZonedDateTime(params['orderDate'])
        String orderNo = params["orderNo"]
        String orderNotes = params["orderNotes"]
        Set recordNotesIds = params.listLong("recordNotes.id")
        EnumListRecordStatus recordStatus = params["recordStatus"] ? EnumListRecordStatus.valueOf(params["recordStatus"]) : null

        Long id = params.long('id')

        DetachedCriteria criteria
        if (params.correspondenceType) {
            EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
            criteria = getListRecordService(correspondenceType)?.search(params)
        } else {
            criteria = new DetachedCriteria(AocListRecord).build {}
        }

        return criteria.list(max: max, offset: offset) {
            if (id) {
                eq('id', id)
            }
            if (aocCorresondenceListId) {
                joinedCorrespondenceListRecords {
                    inList("correspondenceList.id", aocCorresondenceListId)
                }
            }
            if (orderDate) {
                le("orderDate", orderDate)
            }
            if (orderNo) {
                ilike("orderNo", "%${orderNo}%")
            }
            if (orderNotes) {
                ilike("orderNotes", "%${orderNotes}%")
            }
            if (recordNotesIds) {
                recordNotes {
                    inList("id", recordNotesIds)
                }
            }
            if (recordStatus) {
                eq("recordStatus", recordStatus)
            }
            if (columnName) {
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
 * @return AocListRecord.
 */
    AocListRecord save(GrailsParameterMap params) {
        AocListRecord aocListRecordInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.remove('encodedId'))
        }

        if (params.id) {
            aocListRecordInstance = AocListRecord.get(params.remove("id"))
            if (params.long("version")) {
                long version = params.long("version")
                params.remove('version')
                if (aocListRecordInstance.version > version) {
                    aocListRecordInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('aocListRecord.label', null, 'aocListRecord', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this aocListRecord while you were editing")
                    return aocListRecordInstance
                }
            }
            if (!aocListRecordInstance) {
                aocListRecordInstance = new AocListRecord()
                aocListRecordInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocListRecord.label', null, 'aocListRecord', LocaleContextHolder.getLocale())] as Object[], "This aocListRecord with ${params.id} not found")
                return aocListRecordInstance
            }
        }

        //  How do i make request status != created !!!

        AocListRecord.withTransaction {
            try {
                EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
                if (correspondenceType) {
                    IListRecordService listRecordService = getListRecordService(correspondenceType)
                    AocCorrespondenceList aocCorrespondenceList = AocCorrespondenceList.read(params.long('aocCorrespondenceList.id'))
                    if (!aocListRecordInstance) {
                        aocListRecordInstance = listRecordService.getNewInstance(params)
                        aocListRecordInstance.recordStatus = EnumListRecordStatus.NEW
                        aocListRecordInstance.addToJoinedCorrespondenceListRecords(new AocJoinedCorrespondenceListRecord(correspondenceList: aocCorrespondenceList, listRecord: aocListRecordInstance))
                    } else {
                        aocListRecordInstance.recordStatus = params.recordStatus ? EnumListRecordStatus.valueOf(params.remove('recordStatus')) : null
                    }
                    aocListRecordInstance.orderNo = params['orderNo'] ? params.remove('orderNo') : null
                    aocListRecordInstance.orderDate = PCPUtils.parseZonedDateTime(params.remove('orderDate'))
                    aocListRecordInstance.orderNotes = params.remove('orderNotes')

                    Long firmId = params.long('firm.id')

                    // get hr list related to aoc list and firm, or save and return if not exists
                    CorrespondenceList hrList = saveHrCorrespondenceForFirm(aocCorrespondenceList, firmId, null, true)

                    aocListRecordInstance = listRecordService.save(aocListRecordInstance, hrList, params)
                }
                if (aocListRecordInstance.hasErrors()) {
                    throw new Exception("Failed to add request to list")
                }
                aocListRecordInstance.save(failOnError: true, flush: true);
            } catch (ValidationException ve) {
                transactionStatus.setRollbackOnly()
                log.error("Failed to save reocrd", ve)
                aocListRecordInstance.errors = ve.errors
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                log.error("Failed to save reocrd", ex)
                aocListRecordInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return aocListRecordInstance
    }

    void saveExistingRecords(GrailsParameterMap params) {
        if (!params.correspondenceType || !params.long('aocCorrespondenceList.id') || !params.checked_requestIdsList) {
            throw new Exception("Not all parameters are passed")
        }
        AocListRecord.withTransaction {
            try {
                EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
                AocCorrespondenceList aocCorrespondenceList = AocCorrespondenceList.read(params.long('aocCorrespondenceList.id'))
                if (correspondenceType) {
                    IListRecordService listRecordService = getListRecordService(correspondenceType)

                    /**
                     *  get list of ids of selected allowance request
                     */
                    List checkedEncodedListRequestIdList = params?.listString("checked_requestIdsList")

                    checkedEncodedListRequestIdList?.each { requestId ->
                        log.info("Adding hr record: $requestId to list $aocCorrespondenceList.id")
                        params.listEmployeeId = requestId
                        AocListRecord aocListRecordInstance = listRecordService.getNewInstance(params)
                        if (!aocListRecordInstance.recordStatus) {
                            aocListRecordInstance.recordStatus = EnumListRecordStatus.NEW
                        }
                        aocListRecordInstance.addToJoinedCorrespondenceListRecords(new AocJoinedCorrespondenceListRecord(correspondenceList: aocCorrespondenceList, listRecord: aocListRecordInstance))
                        aocListRecordInstance.orderNo = params.remove('orderNo')
                        aocListRecordInstance.orderDate = PCPUtils.parseZonedDateTime(params.remove('orderDate'))
                        aocListRecordInstance.orderNotes = params.remove('orderNotes')

                        Long firmId = params.long('firmId')

                        // get hr list related to aoc list and firm, or save and return if not exists
                        CorrespondenceList hrList = saveHrCorrespondenceForFirm(aocCorrespondenceList, firmId, null, true)

                        if (aocListRecordInstance.id) {
                            log.info("aoc record already linked to hr record, just linking to aoc list")
                        } else {
                            log.info("aoc reocrd does not exist, create new one")
                            aocListRecordInstance = listRecordService.save(aocListRecordInstance, hrList, params)
                        }

                        aocListRecordInstance.save(failOnError: true);
                    }
                } else {
                    throw new Exception("correspondenceType is mandatory")
                }
            }
            catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                log.error("Failed to save reocrd", ex)
                throw new Exception("Failed to add existing records", ex)
            }
        }
    }

    /**
     * Save status change for record
     * @param params
     * @return
     */
    AocListRecord saveStatusChange(GrailsParameterMap params) {
        AocListRecord aocListRecordInstance

        if (params['listRecord.id']) {
            aocListRecordInstance = AocListRecord.get(params.long('listRecord.id'))
            if (params.long("version")) {
                long version = params.long("version")
                params.remove('version')
                if (aocListRecordInstance.version > version) {
                    aocListRecordInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('aocListRecord.label', null, 'aocListRecord', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this aocListRecord while you were editing")
                    return aocListRecordInstance
                }
            }
            if (!aocListRecordInstance) {
                aocListRecordInstance = new AocListRecord()
                aocListRecordInstance.errors.reject('default.not.found.message', [messageSource.getMessage('aocListRecord.label', null, 'aocListRecord', LocaleContextHolder.getLocale())] as Object[], "This aocListRecord with ${params.id} not found")
                return aocListRecordInstance
            }
        }

        AocListRecord.withTransaction {
            try {
                EnumListRecordStatus recordStatus = EnumListRecordStatus.valueOf(params.recordStatus)
                if (recordStatus) {
                    if (aocListRecordInstance.recordStatus == recordStatus) {
                        aocListRecordInstance.errors.rejectValue('recordStatus', 'default.not.changed.label')
                        throw new ValidationException("Status not changed", aocListRecordInstance.errors)
                    }

                    aocListRecordInstance.recordStatus = recordStatus

                    Boolean isEmployeeProfileLocked = checkEmployeeProfileLocked(aocListRecordInstance)

                    if (isEmployeeProfileLocked) {
                        aocListRecordInstance.errors.rejectValue('recordStatus', 'employeeProfile.locked.error.message')
                        throw new ValidationException("Employee profile locked", aocListRecordInstance.errors)
                    }

                    AocListRecordNote recordNote = aocListRecordNoteService.save(params)
                    if (recordNote.hasErrors()) {
                        recordNote?.errors?.eachWithIndex { obj, i ->
                            log.error("Error $i: $obj")
                        }
                        throw new Exception("Failed to create note")
                    }
                    aocListRecordInstance.addToRecordNotes(recordNote)
                    // add note for status change
                    if (params.orderNo) {
                        aocListRecordInstance.orderNo = params.remove('orderNo')
                        aocListRecordInstance.orderDate = PCPUtils.parseZonedDateTime(recordNote.noteDate)
                        aocListRecordInstance.orderNotes = recordNote.note
                    }

                    // save approval info
                    if (aocListRecordInstance.recordStatus == EnumListRecordStatus.APPROVED) {
                        Object hrListEmployee = saveApprovalInfo(aocListRecordInstance, params)
                        if (hrListEmployee && hrListEmployee.hasErrors()) {
                            throw new Exception("Failed to save approval info", ValidationException.formatErrors(hrListEmployee.errors))
                        }
                    }

                } else {
                    aocListRecordInstance.errors.rejectValue('recordStatus', 'default.validation.required.label')
                    throw new ValidationException("Status is mandatory", aocListRecordInstance.errors)
                }
                if (aocListRecordInstance.hasErrors()) {
                    throw new Exception("Failed to add request to list")
                }
                aocListRecordInstance.save(flush: true, failOnError: true)
            } catch (ValidationException ve) {
                transactionStatus.setRollbackOnly()
                log.error("Failed to save reocrd - " + ve.message)
                if (!aocListRecordInstance.hasErrors())
                    aocListRecordInstance.errors = ve.errors
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                log.error("Failed to save reocrd", ex)
                aocListRecordInstance.errors.reject('default.internal.server.error', [ex?.localizedMessage] as Object[], "")
            }
        }
        return aocListRecordInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, Long aocCorrespondenceId, boolean isEncrypted = false) {
        try {
            List<AocJoinedCorrespondenceListRecord> joinedRecord
            List<Long> recordIds = HashHelper.decodeList(deleteBean.ids)?.collect { it?.toString()?.toLong() }
            if (isEncrypted && recordIds?.size() > 0) {
                joinedRecord = AocJoinedCorrespondenceListRecord.createCriteria().list {
                    eq('correspondenceList.id', aocCorrespondenceId)
                    inList('listRecord.id', recordIds)
                }
            } else if (deleteBean.ids) {
                joinedRecord = AocJoinedCorrespondenceListRecord.createCriteria().list {
                    eq('correspondenceList.id', aocCorrespondenceId)
                    inList('listRecord.id', recordIds)
                }
            }
            joinedRecord*.delete()
            deleteBean.status = true
        }
        catch (Exception ex) {
            log.error("failed to remove aoc records", ex)
            deleteBean.status = false
            deleteBean.responseMessage << ex?.message
        }
        return deleteBean
    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return AocListRecord.
 */
    @Transactional(readOnly = true)
    AocListRecord getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
            params.remove('encodedId')
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
            EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
            if (correspondenceType) {
                DOMAIN_COLUMNS = getListRecordService(correspondenceType).domainColumns
            }
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    List<String> getDomainColumns(GrailsParameterMap params) {
        if (params.correspondenceType) {
            return getListRecordService(EnumCorrespondenceType.valueOf(params.correspondenceType)).domainColumns
        }
        return DOMAIN_COLUMNS
    }

    List<String> getHrDomainColumns(GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
        if (correspondenceType) {
            return getListRecordService(correspondenceType).getHrDomainColumns()
        }
        return DOMAIN_COLUMNS
    }

    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
        if (correspondenceType) {
            PagedResultList pagedResultList = search(params)
            return getListRecordService(correspondenceType).searchWithRemotingValues(pagedResultList)
        }
        return null
    }

    PagedList searchNotIncludedRecords(GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
        if (correspondenceType) {
            return getListRecordService(correspondenceType).searchNotIncludedRecords(params)
        }
        return null
    }

    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
        if (correspondenceType) {
            return getListRecordService(correspondenceType).getEmployeeRequestInfo(params)
        }
        return [success: false, message: 'correspondenceType is mandatory']
    }

    Map getOperationFormInfo(GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = EnumCorrespondenceType.valueOf(params.correspondenceType)
        if (correspondenceType) {
            return getListRecordService(correspondenceType).getOperationFormInfo(params)
        }
        return [success: false, message: 'correspondenceType is mandatory']
    }

    /**
     * Reflects workflow result on hr records
     * @param aocCorrespondenceList
     */
    public void updateListRecordStatus(Long aocCorrespondenceId, EnumCorrespondenceType correspondenceType, String orderNumber) {

        StringBuilder queryString = new StringBuilder(" select ajc.listRecord from AocJoinedCorrespondenceListRecord ajc")
        queryString << " where ajc.correspondenceList.id = :correspondenceId )"

        List<AocListRecord> aocListRecordList = AocJoinedCorrespondenceListRecord.executeQuery(queryString.toString(), [correspondenceId: aocCorrespondenceId])

        log.info("${aocListRecordList?.size()} records will be updated ")

        if (aocListRecordList.isEmpty()) {
            throw new Exception("aocCorrespondenceList.emptyRecords.error.message")
        }
        if (aocListRecordList.findAll { it.recordStatus == EnumListRecordStatus.NEW }?.size() > 0) {
            throw new Exception("aocCorrespondenceList.notHandledRecords.error.message")
        }
        getListRecordService(correspondenceType).updateHrRecordStatus(aocListRecordList, orderNumber)
    }

    /**
     * Save hr correspondence list related to aoc list
     * @param aocCorrespondenceListInstance
     * @param firmId
     * @return hr list instance
     */
    public CorrespondenceList saveHrCorrespondenceForFirm(AocCorrespondenceList aocCorrespondenceListInstance, Long firmId, String hrListId = null, Boolean doSave = false) {
        CorrespondenceList hrList
        if (hrListId) {
            hrList = CorrespondenceList.read(hrListId)
            if (aocCorrespondenceListInstance?.joinedAocHrCorrespondenceLists?.find {
                it.hrCorrespondenceList?.id == hrListId
            }) {
                return hrList
            }
        }
        if (!hrList) {
            // Make sure that hr list does not exist for aoc list and related firm
            hrList = aocCorrespondenceListInstance?.joinedAocHrCorrespondenceLists?.find {
                it.firm.id == firmId
            }?.hrCorrespondenceList
            if (hrList) {
                return hrList
            }
            // check if parent correspondence has an hr list for the same firm
            hrList = aocCorrespondenceListInstance?.parentCorrespondenceList?.joinedAocHrCorrespondenceLists.find {
                it.firm.id == firmId
            }?.hrCorrespondenceList
        }

        // create new hrList
        if (!hrList) {
            // save hr correspondence list
            ICorrespondenceListService correspondenceListService = getCorrespondenceListService(aocCorrespondenceListInstance.correspondenceType)
            GrailsParameterMap params = new GrailsParameterMap(WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            // create new hr list
            // save hrCorrespondenceList
            params['hrCorrespondenceList.firm.id'] = firmId
            params['hrCorrespondenceList.name'] = aocCorrespondenceListInstance.name
            params['hrCorrespondenceList.coverLetter'] = aocCorrespondenceListInstance.coverLetter
            params['hrCorrespondenceList.receivingParty'] = EnumReceivingParty.SARAYA
            // outgoing number for original correspondence list delivered from Firm
            if (aocCorrespondenceListInstance.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
                params['hrCorrespondenceList.manualOutgoingNo'] = params.originalSerialNumber
                params['hrCorrespondenceList.correspondenceListStatus'] = EnumCorrespondenceListStatus.SUBMITTED.toString()
                params['hrCorrespondenceList.deliveryStatus'] = EnumDeliveryStatus.REQUEST_DELIVERED
            }
            hrList = correspondenceListService.save(params['hrCorrespondenceList'])
            if (hrList?.hasErrors()) {
                throw new ValidationException("Failed to save hr list for firm $firmId", hrList.errors)
            }
        }
        aocCorrespondenceListInstance.addToJoinedAocHrCorrespondenceLists(new JoinedAocHrCorrespondenceList(
                aocCorrespondenceList: aocCorrespondenceListInstance,
                hrCorrespondenceList: hrList, firm: Firm.read(firmId)))
        if (doSave) {
            aocCorrespondenceListInstance?.save(failOnError: true)
        }
        return hrList
    }

    /**
     * Checks if employee related to aoc list
     * @param aocListRecordInstance
     * @return
     */
    private Boolean checkEmployeeProfileLocked(AocListRecord aocListRecordInstance) {
        EnumCorrespondenceType correspondenceType = getRecordCorrespondenceType(aocListRecordInstance)
        if (correspondenceType) {
            return getListRecordService(correspondenceType).isEmployeeProfileLocked(aocListRecordInstance)
        }
        return false
    }

    /**
     * If the record has additional info to be inserted on record approval, then save these info
     * @return parent folder name or null if no additional values are required
     *
     */
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        EnumCorrespondenceType correspondenceType = getRecordCorrespondenceType(aocListRecordInstance)
        if (correspondenceType && correspondenceType.needsAdditionalInfoOnApproval) {
            // save info
            return getCorrespondenceListService(correspondenceType).saveApprovalInfo(aocListRecordInstance, params)
        }
        return null
    }

    /**
     * If the record has additional info to be inserted on record approval, then the name of the folder that has the
     * form _recordAcceptForm should be returned
     * @return parent folder name or null if no additional values are required
     *
     */
    String getAcceptFormParentFolder(AocListRecord aocListRecordInstance) {
        EnumCorrespondenceType correspondenceType = getRecordCorrespondenceType(aocListRecordInstance)
        if (correspondenceType && correspondenceType.needsAdditionalInfoOnApproval) {
            return correspondenceType.hrListDomain
        }
        return null
    }

    /**
     * Extracts correspondence type that the record is related to
     * @param aocListRecordInstance
     * @return
     */
    private EnumCorrespondenceType getRecordCorrespondenceType(AocListRecord aocListRecordInstance) {
        return aocListRecordInstance?.joinedCorrespondenceListRecords?.correspondenceList?.correspondenceType?.first()
    }

    /**
     * Factory method to return the suitable service for correspondence
     * @param correspondenceType
     * @return
     */
    IListRecordService getListRecordService(EnumCorrespondenceType correspondenceType) {
        IListRecordService listRecordService = (IListRecordService) Holders.applicationContext.getBean(getListRecordServiceName(correspondenceType))
        return listRecordService
    }

    String getListRecordServiceName(EnumCorrespondenceType correspondenceType, boolean includeService = true) {
        return correspondenceType.listDomain + "Record" + (includeService ? 'Service' : '')
    }

    /**
     * Factory method to return the suitable service for correspondence
     * @param correspondenceType
     * @return
     */
    ICorrespondenceListService getCorrespondenceListService(EnumCorrespondenceType correspondenceType) {
        ICorrespondenceListService correspondenceListService = (ICorrespondenceListService) Holders.applicationContext.getBean(correspondenceType.listDomain + "Service")
        return correspondenceListService
    }

}