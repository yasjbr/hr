package ps.gov.epsilon.hr.firm.recruitment

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResult
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.lookups.Inspection
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService

import java.sql.Timestamp
import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service aims to create list of applicant inspection results.
 * <h1>Usage</h1>
 * -this service used to create list of applicant inspection results.
 * <h1>Restriction</h1>
 * -need a firm created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ApplicantInspectionResultListService {


    MessageSource messageSource
    def formatService
    def sessionFactory
    PersonService personService
    ApplicantInspectionResultListEmployeeService applicantInspectionResultListEmployeeService
    RequestService requestService
    OrganizationService organizationService

    //to get the value of requisition status
    public static currentStatusValue = { cService, ApplicantInspectionResultList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }



    // to make name of list as link
    public static getListName = { formatService, ApplicantInspectionResultList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../applicantInspectionResultList/manageApplicantInspectionResultList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "code", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: getListName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trackingInfo.dateCreatedUTC", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.sendDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualOutgoingNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.receiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualIncomeNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.numberOfCompetitorsValue", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentStatus.correspondenceListStatus", type: "enum", source: 'domain'],
            [sort: false, search: true, hidden: true, name: "currentStatusValue", type: currentStatusValue, source: 'domain'],
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

        //search about list with status:
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus listCurrentStatusValue = params["listCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["listCurrentStatusValue"] as String) : null

        List<Map<String, String>> orderBy = params.list("orderBy")
        Set applicantInspectionResultListEmployeesIds = params.listString("applicantInspectionResultListEmployees.id")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        Long coreOrganizationId = params.long("coreOrganizationId")


        return ApplicantInspectionResultList.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("code", sSearch)
                    ilike("manualIncomeNo", sSearch)
                    ilike("manualOutgoingNo", sSearch)
                    ilike("name", sSearch)
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
                if (applicantInspectionResultListEmployeesIds) {
                    applicantInspectionResultListEmployees {
                        inList("id", applicantInspectionResultListEmployeesIds)
                    }
                }

                if (coreOrganizationId) {
                    eq("coreOrganizationId", coreOrganizationId)
                }

                if (code) {
                    ilike("code", "%${code}%")
                }
                if (correspondenceListStatusesIds) {
                    correspondenceListStatuses {
                        inList("id", correspondenceListStatusesIds)
                    }
                }
                if (currentStatusId) {
                    eq("currentStatus.id", currentStatusId)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (manualIncomeNo) {
                    ilike("manualIncomeNo", "%${manualIncomeNo}%")
                }
                if (manualOutgoingNo) {
                    ilike("manualOutgoingNo", "%${manualOutgoingNo}%")
                }
                if (name) {
                    ilike("name", "%${name}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (receivingParty) {
                    eq("receivingParty", receivingParty)
                }
                if (listCurrentStatusValue) {
                    currentStatus {
                        eq("correspondenceListStatus", listCurrentStatusValue)
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
     * to search about remoting values from the core
     */
    public PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedResultList = customSearch(params)

        //get remoting values.
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: pagedResultList?.resultList?.coreOrganizationId))
        List<OrganizationDTO> organizationDTOList = organizationService.searchOrganization(searchBean)?.resultList

        //assign organization name for each list.
        pagedResultList?.resultList?.each { ApplicantInspectionResultList applicantInspectionResultList ->
            applicantInspectionResultList.transientData.put("organizationName", organizationDTOList.find {
                it.id == applicantInspectionResultList?.coreOrganizationId
            }?.descriptionInfo?.localName)

        }

        pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ApplicantInspectionResultList.
     */
    ApplicantInspectionResultList save(GrailsParameterMap params) {
        ApplicantInspectionResultList applicantInspectionResultListInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            applicantInspectionResultListInstance = ApplicantInspectionResultList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (applicantInspectionResultListInstance.version > version) {
                    applicantInspectionResultListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('applicantInspectionResultList.label', null, 'applicantInspectionResultList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this applicantInspectionResultList while you were editing")
                    return applicantInspectionResultListInstance
                }
            }
            if (!applicantInspectionResultListInstance) {
                applicantInspectionResultListInstance = new ApplicantInspectionResultList()
                applicantInspectionResultListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('applicantInspectionResultList.label', null, 'applicantInspectionResultList', LocaleContextHolder.getLocale())] as Object[], "This applicantInspectionResultList with ${params.id} not found")
                return applicantInspectionResultListInstance
            }
        } else {
            applicantInspectionResultListInstance = new ApplicantInspectionResultList()
        }
        try {
            applicantInspectionResultListInstance.properties = params;
            applicantInspectionResultListInstance.save();
            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: params["receivingParty"] ? EnumReceivingParty.valueOf(params["receivingParty"]) : EnumReceivingParty.SARAYA, firm: applicantInspectionResultListInstance.firm)
                applicantInspectionResultListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            applicantInspectionResultListInstance.save(flush: true, failOnError: true);
            //save the current status:
            if (correspondenceListStatus?.id && applicantInspectionResultListInstance?.id) {
                applicantInspectionResultListInstance?.currentStatus = correspondenceListStatus
                applicantInspectionResultListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            applicantInspectionResultListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return applicantInspectionResultListInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return ApplicantInspectionResultList.
     */
    @Transactional(readOnly = true)
    ApplicantInspectionResultList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                ApplicantInspectionResultList applicantInspectionResultList = results?.resultList[0]
                return applicantInspectionResultList
            }
        }
        return null
    }

    /**
     * to get model entry with remoting values.
     * @param GrailsParameterMap params the search map.
     * @return ApplicantInspectionResultList.
     */
    @Transactional(readOnly = true)
    ApplicantInspectionResultList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = searchWithRemotingValues(params)
            if (results) {
                ApplicantInspectionResultList applicantInspectionResultList = results?.resultList[0]
                return applicantInspectionResultList
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

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see ps.police.common.beans.v1.DeleteBean.
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
            ApplicantInspectionResultList instance = ApplicantInspectionResultList.get(id)
            //to be able to delete an vacation_stop list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.applicantInspectionResultListEmployees?.size() == 0) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
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
     * add applicantInspectionResult request to the list
     * @param params
     * @return applicantInspectionResult list instance
     */
    ApplicantInspectionResultList addRequestToList(GrailsParameterMap params) {

        ApplicantInspectionResultList applicantInspectionResultList = ApplicantInspectionResultList.load(params["applicantInspectionResultListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");
        params.remove("checked_requestIdsList");

        if (checkedRequestIdList.size() > 0) {
            ApplicantInspectionResultListEmployee applicantInspectionResultListEmployee = null
            //retrieve the selected services:
            List<Applicant> applicantList = Applicant.executeQuery("from Applicant c where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (applicantList) {
                try {
                    applicantList.each { Applicant applicant ->
                        //create new service list employee and add the service Request
                        applicantInspectionResultListEmployee = new ApplicantInspectionResultListEmployee()
                        applicantInspectionResultListEmployee.applicant = applicant
                        applicantInspectionResultListEmployee.recordStatus = EnumListRecordStatus.NEW
                        //add the service list employee to list
                        applicantInspectionResultList.addToApplicantInspectionResultListEmployees(applicantInspectionResultListEmployee);
                    }
                    applicantInspectionResultList?.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (applicantInspectionResultList?.errors?.allErrors?.size() > 0) {
                        applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            applicantInspectionResultList.errors.reject("list.request.notSelected.error")
        }
        return applicantInspectionResultList
    }//save

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ApplicantInspectionResultList sendData(GrailsParameterMap params) {
        //return applicantInspectionResultListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //return the corresponding list
        ApplicantInspectionResultList applicantInspectionResultList = ApplicantInspectionResultList.get(params["id"])
        if (params.id) {
            if (applicantInspectionResultList) {
                //to change the correspondenceListStatus to submitted when we send the marital status list
                // and change the from date to the date of sending marital status list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = applicantInspectionResultList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                correspondenceListStatus.firm = applicantInspectionResultList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                if (correspondenceListStatus.save()) {
                    applicantInspectionResultList.currentStatus = correspondenceListStatus
                }
                //enter the manualIncomeNo when we send the marital status list
                applicantInspectionResultList.manualOutgoingNo = params.manualOutgoingNo

                try {
                    //save the disciplinary list changes
                    applicantInspectionResultList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (applicantInspectionResultList?.errors?.allErrors?.size() == 0) {
                        applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return applicantInspectionResultList
            }
        }
        return applicantInspectionResultList
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ApplicantInspectionResultList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //return ApplicantInspectionResultList is
        ApplicantInspectionResultList applicantInspectionResultList = ApplicantInspectionResultList.load(params["id"])
        if (params.id) {

            if (applicantInspectionResultList) {
                //to change the correspondenceListStatus to received when we receive the ApplicantInspectionResultList
                // and change the to date to the date of receive ApplicantInspectionResultList
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus(
                        fromDate: ZonedDateTime.now(),
                        toDate: PCPUtils.parseZonedDateTime(params['toDate']),
                        correspondenceList: applicantInspectionResultList,
                        receivingParty: params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null,
                        firm: applicantInspectionResultList?.firm,
                        correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED)
                applicantInspectionResultList.currentStatus = correspondenceListStatus

                //enter the manualIncomeNo when we receive the applicantInspectionResult list
                applicantInspectionResultList.manualIncomeNo = params.manualIncomeNo


                try {
                    //save the disciplinary list changes
                    applicantInspectionResultList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (applicantInspectionResultList?.errors?.allErrors?.size() == 0) {
                        applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return applicantInspectionResultList
            }
        }
        return applicantInspectionResultList
    }

    /**
     * to change the applicantInspectionResult List employee status to EMPLOYED in the receive applicantInspectionResult list
     * @param GrailsParameterMap params
     * @return ApplicantInspectionResultList
     */
    ApplicantInspectionResultList approveRequest(GrailsParameterMap params) {
        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        /**
         * to get list of  ids of applicantInspectionResult request
         */
        List applicantInspectionResultListEmployeeIds = params.listString("check_applicantTableInApplicantInspectionResultList")
        params.remove("check_applicantTableInApplicantInspectionResultList")
        ApplicantInspectionResultListEmployee applicantInspectionResultListEmployee
        List<ApplicantInspectionResultListEmployee> applicantInspectionResultListEmployeeList
        ApplicantInspectionResultList applicantInspectionResultList = null
        /**
         * get selected applicantInspectionResult list employee
         */
        GrailsParameterMap applicantInspectionResultListEmployeeParam = new GrailsParameterMap(["ids[]": applicantInspectionResultListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        applicantInspectionResultListEmployeeList = applicantInspectionResultListEmployeeService?.search(applicantInspectionResultListEmployeeParam)

        /**
         * get applicantInspectionResultList
         */
        applicantInspectionResultList = ApplicantInspectionResultList.get(params["id"])


        if (applicantInspectionResultListEmployeeIds) {


            applicantInspectionResultListEmployeeIds?.each { String id ->
                /**
                 * get applicantInspectionResult list employee by id
                 */
                applicantInspectionResultListEmployee = applicantInspectionResultListEmployeeList?.find { it?.id == id }

                /**
                 * change suspension extension list employee status
                 */
                applicantInspectionResultListEmployee?.recordStatus = EnumListRecordStatus.APPROVED

                /**
                 * note is required in reject.
                 */
                if (params.note || params.orderNo) {
                    applicantInspectionResultListEmployee?.addToApplicantInspectionResultListEmployeeNotes(new ApplicantInspectionResultListEmployeeNote(applicantInspectionResultListEmployee: applicantInspectionResultListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                }
            }
            try {

                applicantInspectionResultList.save(failOnError: true)
            } catch (Exception ex) {
                ex.printStackTrace()
                transactionStatus.setRollbackOnly()
                if (applicantInspectionResultList?.hasErrors()) {
                    applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            applicantInspectionResultList.errors.reject('applicantInspectionResultList.error.not.selected.request.message')
            return applicantInspectionResultList
        }
        return applicantInspectionResultList
    }

    /**
     * to change the applicantInspectionResult List employee status to NOT_EMPLOYED in the receive applicantInspectionResult list
     * @param GrailsParameterMap params
     * @return ApplicantInspectionResultList
     */
    ApplicantInspectionResultList rejectRequest(GrailsParameterMap params) {
        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        ApplicantInspectionResultListEmployee applicantInspectionResultListEmployee
        List<ApplicantInspectionResultListEmployee> applicantInspectionResultListEmployeeList
        ApplicantInspectionResultList applicantInspectionResultList = null

        /**
         * get applicantInspectionResultList
         */
        applicantInspectionResultList = ApplicantInspectionResultList.get(params["id"])


        if (params.note || params.orderNo) {

            /**
             * to get list of  ids of applicantInspectionResult request
             */
            List applicantInspectionResultListEmployeeIds = params.listString("check_applicantTableInApplicantInspectionResultList")
            params.remove("check_applicantTableInApplicantInspectionResultList")

            /**
             * get selected applicantInspectionResult request
             */
            GrailsParameterMap applicantInspectionResultListEmployeeParam = new GrailsParameterMap(["ids[]": applicantInspectionResultListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            applicantInspectionResultListEmployeeList = applicantInspectionResultListEmployeeService?.search(applicantInspectionResultListEmployeeParam)

            if (applicantInspectionResultListEmployeeIds) {
                applicantInspectionResultListEmployeeIds?.each { String id ->

                    /**
                     * get applicantInspectionResult list employee
                     */
                    applicantInspectionResultListEmployee = applicantInspectionResultListEmployeeList?.find {
                        it?.id == id
                    }

                    /**
                     * change applicantInspectionResult list employee status
                     */
                    applicantInspectionResultListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                    /*
                    * note is required in reject.
                    */
                    if (params.note != null || params.orderNo != null) {
                        applicantInspectionResultListEmployee?.addToApplicantInspectionResultListEmployeeNotes(new ApplicantInspectionResultListEmployeeNote(applicantInspectionResultListEmployee: applicantInspectionResultListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                    }
                }

                try {
                    applicantInspectionResultList.save(failOnSave: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (applicantInspectionResultList?.hasErrors()) {
                        applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            } else {
                applicantInspectionResultList.errors.reject('applicantInspectionResultList.error.not.selected.request.message')
                return applicantInspectionResultList
            }
        } else {
            applicantInspectionResultList.errors.reject('applicantInspectionResultList.not.requestRejected.message')
            return applicantInspectionResultList
        }
        return applicantInspectionResultList
    }

    /**
     * custom search to find the number of requests in the loan  list in one select statement for performance issue
     * this solution was suggested and approved by Mureed
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    PagedList customSearch(GrailsParameterMap params) {

        final session = sessionFactory.currentSession

        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        String orderByQuery = ""
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))

        String id = params["id"]
        String code = params["code"]
        String name = params["name"]
        Long firmId = PCPSessionUtils.getValue("firmId")
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"]) : null
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String trackingInfoDateCreatedUTC = params['trackingInfo.dateCreatedUTC']
        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        String dateCreated = params['dateCreated']
        String sendDate = params['sendDate']
        String receiveDate = params['receiveDate']
        Timestamp fromSendDate = PCPUtils.parseTimestampWithSmallestTime(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestampWithBiggestTime(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])
        Timestamp fromReceiveDate = PCPUtils.parseTimestampWithSmallestTime(params['receiveDateFrom'])
        Timestamp toReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateTo'])
        Long coreOrganizationId = params.long("coreOrganizationId")

        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of vacation_stops in the list, the send date, and the current list status
        String query = "FROM applicant_inspection_result_list al  LEFT JOIN " +
                "  (SELECT ale.applicant_inspection_result_list_id ,count(ale.id) no_of_employee" +
                "  FROM applicant_inspection_result_list_employee ale " +
                "  group by ale.applicant_inspection_result_list_id ) b" +
                "  on al.id= b.applicant_inspection_result_list_id , correspondence_list_status cls,correspondence_list cl" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sb WHERE  sb.correspondence_list_status='${EnumCorrespondenceListStatus.SUBMITTED}') sbl" +
                " ON  sbl.correspondence_List_id=cl.id" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sd WHERE  sd.correspondence_list_status='${EnumCorrespondenceListStatus.RECEIVED}') sdl" +
                " ON  sdl.correspondence_List_id=cl.id" +
                "  where cl.id= al.id and cls.id = cl.current_status_id and cl.status ='${GeneralStatus.ACTIVE}' "

        //if statements to check the params
        if (sSearch) {
            query = query + " and ( code like :codeSParam or " +
                    "manual_income_no like :manualIncomeNoSParam or " +
                    "manual_outgoing_no like :manualOutgoingNoSParam or " +
                    "name like :nameSParam ) "
            sqlParamsMap.put("codeSParam", "%" + sSearch + "%")
            sqlParamsMap.put("manualIncomeNoSParam", "%" + sSearch + "%")
            sqlParamsMap.put("manualOutgoingNoSParam", "%" + sSearch + "%")
            sqlParamsMap.put("nameSParam", "%" + sSearch + "%")
        }

        if (id) {
            query = query + " and al.id = :idParam  "
            sqlParamsMap.put("idParam", id)
        }
        if (firmId) {
            query = query + " and cl.firm_id = :firmIdParam  "
            sqlParamsMap.put("firmIdParam", firmId)
        }
        if (coreOrganizationId) {
            query = query + " and cl.core_organization_id = :coreOrganizationId  "
            sqlParamsMap.put("coreOrganizationId", coreOrganizationId)
        }
        if (code) {
            query = query + " and code like :codeParam  "
            sqlParamsMap.put("codeParam", "%" + code + "%")
        }
        if (name) {
            query = query + " and name like :nameParam  "
            sqlParamsMap.put("nameParam", "%" + name + "%")
        }
        if (correspondenceListStatus) {
            query = query + " and cls.correspondence_list_status = :correspondenceListStatusParam  "
            sqlParamsMap.put("correspondenceListStatusParam", correspondenceListStatus.toString())
        }
        if (manualIncomeNo) {
            query = query + " and manual_income_no like :manualIncomeNoParam  "
            sqlParamsMap.put("manualIncomeNoParam", "%" + manualIncomeNo + "%")
        }
        if (manualOutgoingNo) {
            query = query + " and manual_outgoing_no like :manualOutgoingNoParam  "
            sqlParamsMap.put("manualOutgoingNoParam", "%" + manualOutgoingNo + "%")
        }
        if (trackingInfoDateCreatedUTC) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", trackingInfoDateCreatedUTC)
        }

        //check 3 cases of date created > = <
        if (dateCreated) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", dateCreated)
        }
        if (fromDateCreated) {
            query = query + " and cl.date_created >= :fromDateCreated "
            sqlParamsMap.put("fromDateCreated", Date.from(fromDateCreated.toInstant()))
        }
        if (toDateCreated) {
            query = query + " and cl.date_created <= :toDateCreated "
            sqlParamsMap.put("toDateCreated", Date.from(toDateCreated.toInstant()))
        }

        //check 3 cases of send date created > = <
        if (sendDate) {
            query = query + " and to_char(sbl.from_date_datetime,'dd/MM/yyyy')  = :sendDate "
            sqlParamsMap.put("sendDate", sendDate)
        }
        if (fromSendDate) {
            query = query + " and sbl.from_date_datetime >= :fromSendDate "
            sqlParamsMap.put("fromSendDate", Date.from(fromSendDate.toInstant()))
        }
        if (toSendDate) {
            query = query + " and sbl.from_date_datetime <= :toSendDate "
            sqlParamsMap.put("toSendDate", Date.from(toSendDate.toInstant()))
        }

        //check 3 cases of receive date created > = <
        if (receiveDate) {
            query = query + " and to_char(sdl.from_date_datetime,'dd/MM/yyyy')  = :receiveDate "
            sqlParamsMap.put("receiveDate", receiveDate)
        }
        if (fromReceiveDate) {
            query = query + " and sdl.from_date_datetime >= :fromReceiveDate "
            sqlParamsMap.put("fromReceiveDate", Date.from(fromReceiveDate.toInstant()))
        }
        if (toReceiveDate) {
            query = query + " and sdl.from_date_datetime <= :toReceiveDate "
            sqlParamsMap.put("toReceiveDate", Date.from(toReceiveDate.toInstant()))
        }
        if (numberOfCompetitorsValue != null) {
            query = query + " and COALESCE(b.no_of_employee,0) = :numberOfCompetitorsValueParam  "
            sqlParamsMap.put("numberOfCompetitorsValueParam", numberOfCompetitorsValue)
        }

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("trackingInfo.dateCreatedUTC")) {
            orderByQuery += "ORDER BY cl.date_created ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.sendDate")) {
            orderByQuery += "ORDER BY sbl.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.receiveDate")) {
            orderByQuery += "ORDER BY sdl.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("fromDate")) {
            orderByQuery += "ORDER BY cls.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualincomeno")) {
            orderByQuery += "ORDER BY cl.manual_income_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("toDate")) {
            orderByQuery += "ORDER BY cls.to_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualOutgoingNo")) {
            orderByQuery += "ORDER BY cl.manual_outgoing_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.numberOfCompetitorsValue")) {
            orderByQuery += "ORDER BY b.no_of_employee  ${dir}"
        } else if (columnName?.equalsIgnoreCase("currentStatus.correspondenceListStatus")) {
            orderByQuery += "ORDER BY cls.correspondence_list_status  ${dir}"
        } else if (columnName) {
            orderByQuery += "ORDER BY ${columnName} ${dir}"
        } else {
            orderByQuery += "ORDER BY cl.date_created desc"
        }

        Query sqlQuery = session.createSQLQuery(
                """
                SELECT
                    al.id ,
                    cl.code,
                    cl.name,
                    COALESCE( NULLIF(cl.date_created,'0003-03-03 03:03:03') ) as date_created,
                    COALESCE( NULLIF(cls.to_date_datetime,'0003-03-03 03:03:03') ) as to_date_datetime,
                    COALESCE( NULLIF(cls.from_date_datetime,'0003-03-03 03:03:03') ) as from_date_datetime,
                    COALESCE( NULLIF(sbl.from_date_datetime,'0003-03-03 03:03:03') ) as send_date,
                    COALESCE( NULLIF(sdl.from_date_datetime,'0003-03-03 03:03:03') ) as receive_date,
                    cl.manual_outgoing_no,
                    cl.manual_income_no,
                    cls.correspondence_list_status,
                    cl.current_status_id,
                    COALESCE(no_of_employee,0) as no_of_employee,
                    cl.receiving_party,
                    COALESCE( NULLIF(cl.last_updated,'0003-03-03 03:03:03') ) as last_updated,
                    cl.cover_letter,
                    al.inspection_category_id,
                    al.inspection_id,
                    al.core_organization_id

              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setMaxResults(max)
        sqlQuery.setFirstResult(offset)


        final queryResults = sqlQuery.list()

        List<ApplicantInspectionResultList> results = []
        // Transform resulting rows to a map with key organisationName.
        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[3])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[14])
            ZonedDateTime toDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[4])
            ZonedDateTime fromDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[5])
            ZonedDateTime sendDateZonedDateTime
            ZonedDateTime receiveDateZonedDateTime
            if (resultRow[6]) {
                sendDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[6])
            }
            if (resultRow[7]) {
                receiveDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[7])
            }

            ApplicantInspectionResultList applicantInspectionResultList = new ApplicantInspectionResultList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC],
                    inspectionCategory: InspectionCategory?.load(resultRow[16]),
                    inspection: Inspection?.load(resultRow[17]),
                    coreOrganizationId: resultRow[18])
            applicantInspectionResultList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            applicantInspectionResultList.currentStatus = currentStatus
            results.add(applicantInspectionResultList)
        }


        Integer totalCount = 0

        //get total count for all records if we have records (results!=null)
        if (results) {
            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(al.id) """ + query)
            sqlParamsMap?.each {
                sqlCountQuery.setParameter(it.key.toString(), it.value)
            }
            final queryCountResults = sqlCountQuery.list()
            totalCount = new Integer(queryCountResults[0]?.toString())
        }

        //return the paged list result
        return new PagedList(resultList: results, totalCount: totalCount)

    }

    /**
     * close applicantInspectionResult list
     * @param GrailsParameterMap params
     * @return ApplicantInspectionResultList
     */
    ApplicantInspectionResultList closeList(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        ApplicantInspectionResultList applicantInspectionResultList = null
        List<ApplicantInspectionResultListEmployee> applicantInspectionResultListEmployeeList
        if (params.id) {
            //return the correspondence list:
            applicantInspectionResultList = ApplicantInspectionResultList?.get(params["id"])

            applicantInspectionResultListEmployeeList = ApplicantInspectionResultListEmployee.executeQuery("From ApplicantInspectionResultListEmployee vsle where  vsle.applicantInspectionResultList.id =:applicantInspectionResultListId and vsle.recordStatus= :recordStatus", [applicantInspectionResultListId: applicantInspectionResultList?.id, recordStatus: EnumListRecordStatus.NEW])

            /*to check if there is an applicantInspectionResult list employee status is still NEW to prevent close list */
            CorrespondenceListStatus correspondenceListStatus = null
            if (applicantInspectionResultListEmployeeList) {
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = applicantInspectionResultList
                correspondenceListStatus.firm = applicantInspectionResultList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                if (correspondenceListStatus.save()) {
                    applicantInspectionResultList.currentStatus = correspondenceListStatus
                }
            } else {
                if (applicantInspectionResultList) {
                    //to change the correspondenceListStatus to submitted when we close the applicantInspectionResult list
                    correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.correspondenceList = applicantInspectionResultList
                    correspondenceListStatus.firm = applicantInspectionResultList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    if (correspondenceListStatus.save()) {
                        applicantInspectionResultList.currentStatus = correspondenceListStatus
                    }
                }
            }


            ZonedDateTime sendDate = applicantInspectionResultList?.correspondenceListStatuses?.find {
                it.correspondenceListStatus == EnumCorrespondenceListStatus.SUBMITTED
            }?.fromDate
            ZonedDateTime receiveDate = applicantInspectionResultList?.correspondenceListStatuses?.find {
                it.correspondenceListStatus == EnumCorrespondenceListStatus.RECEIVED
            }?.fromDate

            println "sendDate:${sendDate}"
            println "receiveDate:${receiveDate}"

            /**
             * change request status to  list employee status
             */
            Applicant applicant = null
            List<UserTerm> userTermKeyList = []
            List<String> userTermValueList = []
            List<Map> notificationActionsMap = null
            ApplicantInspectionResult applicantInspectionResult = null
            ApplicantInspectionCategoryResult applicantInspectionCategoryResult = null
            applicantInspectionResultList?.applicantInspectionResultListEmployees?.each { ApplicantInspectionResultListEmployee applicantInspectionResultListEmployee ->
                applicant = applicantInspectionResultListEmployee.applicant

                applicantInspectionCategoryResult = ApplicantInspectionCategoryResult.findByInspectionCategoryAndApplicant(applicantInspectionResultList?.inspectionCategory, applicant)
                if (!applicantInspectionCategoryResult) {
                    applicantInspectionCategoryResult = new ApplicantInspectionCategoryResult()
                }


                applicantInspectionResult = ApplicantInspectionResult.findByInspectionCategoryResultAndInspection(applicantInspectionCategoryResult, applicantInspectionResultList?.inspection)

                //add applicant result
                if (!applicantInspectionResult) {
                    applicantInspectionResult = new ApplicantInspectionResult()
                }

                applicantInspectionCategoryResult.inspectionCategory = applicantInspectionResultList?.inspectionCategory
                applicantInspectionCategoryResult.inspectionResult = EnumInspectionResult.REQUESTED_BY_APPLICANT
                if (!applicantInspectionCategoryResult.requestDate) {
                    applicantInspectionCategoryResult.requestDate = sendDate
                }
                applicantInspectionResult.receiveDate = receiveDate
                applicantInspectionResult.sendDate = sendDate
                applicantInspectionCategoryResult.applicant = applicant
                applicantInspectionCategoryResult.save(failOnError: true, flush: true)

                if (!applicantInspectionResultListEmployee?.recordStatus && applicantInspectionResultListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                    applicantInspectionResult.resultValue = "${messageSource.getMessage('EnumInspectionResult.ACCEPTED', null, null, LocaleContextHolder.getLocale())}"

                } else if (!applicantInspectionResultListEmployee?.recordStatus && applicantInspectionResultListEmployee?.recordStatus == EnumListRecordStatus.REJECTED) {
                    applicantInspectionResult.resultValue = "${messageSource.getMessage('EnumInspectionResult.REJECTED', null, null, LocaleContextHolder.getLocale())}"

                } else if (!applicantInspectionResultListEmployee?.recordStatus) {
                    applicantInspectionResult.resultValue = "${messageSource.getMessage('EnumInspectionResult.REQUESTED_BY_APPLICANT', null, null, LocaleContextHolder.getLocale())}"

                }
                applicantInspectionResult.inspectionCategoryResult = applicantInspectionCategoryResult
                applicantInspectionResult.inspection = applicantInspectionResultList?.inspection
                applicantInspectionResult.coreOrganizationId = applicantInspectionResultList?.coreOrganizationId
                applicantInspectionResult.save(failOnError: true, flush: true)
            }

            try {
                applicantInspectionResultList.save(failOnError: true)
            } catch (Exception ex) {
                ex.printStackTrace()
                transactionStatus.setRollbackOnly()
                if (applicantInspectionResultList?.hasErrors()) {
                    applicantInspectionResultList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        }
        return applicantInspectionResultList
    }

}