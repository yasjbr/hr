package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResult
import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.gov.epsilon.hr.firm.lookups.Inspection
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * this service is aims to create applicant category's results
 * <h1>Usage</h1>
 * this service is used to create result for applicant
 * <h1>Restriction</h1>
 * need applicant and inspection category created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ApplicantInspectionCategoryResultService {

    MessageSource messageSource
    def formatService

    //return the applicant current status
    public static getApplicantStatus = { cService, ApplicantInspectionCategoryResult rec, object, params ->
        if (rec?.applicant?.applicantCurrentStatus) {
            return rec?.applicant?.applicantCurrentStatus?.applicantStatus?.toString()
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: false, search: false, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "applicant.transientData.personName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "inspectionCategory.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "receiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "inspectionResult", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "resultSummary", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "applicantStatus", type: getApplicantStatus, source: 'domain'],
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: false, search: false, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "inspectionCategory.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "receiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "inspectionResult", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "resultSummary", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "applicantStatus", type: getApplicantStatus, source: 'domain'],
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
        String applicantId = params["applicant.id"]
        Set committeeRolesIds = params.listString("committeeRoles.id")
        String inspectionCategoryId = params["inspectionCategory.id"]
        ps.gov.epsilon.hr.enums.v1.EnumInspectionResult inspectionResult = params["inspectionResult"] ? ps.gov.epsilon.hr.enums.v1.EnumInspectionResult.valueOf(params["inspectionResult"]) : null
        ps.gov.epsilon.hr.enums.v1.EnumInspectionResultRate inspectionResultRate = params["inspectionResultRate"] ? ps.gov.epsilon.hr.enums.v1.EnumInspectionResultRate.valueOf(params["inspectionResultRate"]) : null
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime receiveDate = PCPUtils.parseZonedDateTime(params['receiveDate'])
        String resultSummary = params["resultSummary"]
        Set testsResultIds = params.listString("testsResult.id")

        return ApplicantInspectionCategoryResult.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("resultSummary", sSearch)
                    inspectionCategory {
                        descriptionInfo {
                            ilike("localName", sSearch)
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
                if (applicantId) {
                    eq("applicant.id", applicantId)
                }
                if (committeeRolesIds) {
                    committeeRoles {
                        inList("id", committeeRolesIds)
                    }
                }
                if (inspectionCategoryId) {
                    eq("inspectionCategory.id", inspectionCategoryId)
                }
                if (inspectionResult) {
                    eq("inspectionResult", inspectionResult)
                }
                if (inspectionResultRate) {
                    eq("inspectionResultRate", inspectionResultRate)
                }
                if (requestDate) {
                    eq("requestDate", requestDate)
                }
                if (receiveDate) {
                    eq("receiveDate", receiveDate)
                }
                if (resultSummary) {
                    ilike("resultSummary", "%${resultSummary}%")
                }
                if (testsResultIds) {
                    testsResult {
                        inList("id", testsResultIds)
                    }
                }


            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "inspectionCategory.descriptionInfo.localName":
                        inspectionCategory {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break
                    default: break
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
     * @return ApplicantInspectionCategoryResult.
     */
    ApplicantInspectionCategoryResult save(GrailsParameterMap params) {
        ApplicantInspectionCategoryResult applicantInspectionCategoryResultInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            applicantInspectionCategoryResultInstance = ApplicantInspectionCategoryResult.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (applicantInspectionCategoryResultInstance.version > version) {
                    applicantInspectionCategoryResultInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('applicantInspectionCategoryResult.label', null, 'applicantInspectionCategoryResult', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this applicantInspectionCategoryResult while you were editing")
                    return applicantInspectionCategoryResultInstance
                }
            }
            if (!applicantInspectionCategoryResultInstance) {
                applicantInspectionCategoryResultInstance = new ApplicantInspectionCategoryResult()
                applicantInspectionCategoryResultInstance.errors.reject('default.not.found.message', [messageSource.getMessage('applicantInspectionCategoryResult.label', null, 'applicantInspectionCategoryResult', LocaleContextHolder.getLocale())] as Object[], "This applicantInspectionCategoryResult with ${params.id} not found")
                return applicantInspectionCategoryResultInstance
            }
        } else {
            applicantInspectionCategoryResultInstance = new ApplicantInspectionCategoryResult()
        }
        try {

            //delete hasMany for applicant's inspection and inspection category
            if (applicantInspectionCategoryResultInstance.id) {
                JoinedInspectionCategoryResultCommitteeRole.executeUpdate("delete from JoinedInspectionCategoryResultCommitteeRole cat where cat.applicantInspectionCategoryResult.id = :applicantInspectionCategoryResultId", [applicantInspectionCategoryResultId: applicantInspectionCategoryResultInstance.id])
                if (applicantInspectionCategoryResultInstance?.testsResult?.toList()?.id) {
                    JoinedInspectionResultCommitteeRole.executeUpdate("delete from JoinedInspectionResultCommitteeRole cat where cat.applicantInspectionResult.id IN :applicantInspectionResultList", [applicantInspectionResultList: applicantInspectionCategoryResultInstance?.testsResult?.toList()?.id])
                }
                ApplicantInspectionResult.executeUpdate("delete from ApplicantInspectionResult cat where cat.inspectionCategoryResult.id = :inspectionCategoryResultId", [inspectionCategoryResultId: applicantInspectionCategoryResultInstance.id])
            }

            //assign applicant inspection category results values
            applicantInspectionCategoryResultInstance.properties = params

            //to get list of committee for applicant inspection's category
            List committeeRoleIds = params.list("committeeRole")
            List<String> partyNameList = params.list("partyName")

            //to assign  committee role for applicant inspection's category
            partyNameList?.eachWithIndex { value, index ->
                //check value not null
                if (value) {
                    applicantInspectionCategoryResultInstance.addToCommitteeRoles(new JoinedInspectionCategoryResultCommitteeRole
                            (partyName: partyNameList.get(index), applicantInspectionCategoryResult: applicantInspectionCategoryResultInstance,
                                    committeeRole: CommitteeRole.findById(committeeRoleIds.get(index))))
                }
            }

            //to gey list of  all inspection results from params
            List inspectionIdsList = params.list("inspectionIds")
            List sendDateList = params.list("inspectionSendDate")
            List receiveDateList = params.list("inspectionReceiveDate")
            List resultValueList = params.list("inspectionResultValue")
            List executionPeriodList = params.list("inspectionExecutionPeriod")
            List resultSummaryList = params.list("inspectionResultSummary")
            List markList = params.list("inspectionMark")

            ApplicantInspectionResult applicantInspectionResult
            CommitteeRole committeeRole
            Inspection inspection
            ZonedDateTime sendDate
            ZonedDateTime receiveDate
            //to assign result for inspection
            inspectionIdsList?.eachWithIndex { value, int index ->
                inspection = Inspection.findById(inspectionIdsList?.get(index))

                if (inspection?.hasDates) {
                    receiveDate = PCPUtils.parseZonedDateTime(receiveDateList?.get(index))
                    sendDate = PCPUtils.parseZonedDateTime(sendDateList?.get(index))
                }

                //to validate receive and send date
                if (receiveDate < sendDate) {
                    applicantInspectionCategoryResultInstance.errors.reject('applicantInspectionCategoryResult.dateError.label', [] as Object[], "")

                }
                //to assign inspection result
                applicantInspectionResult = new ApplicantInspectionResult(
                        sendDate: sendDate ? sendDate : ZonedDateTime.now(), receiveDate: receiveDate ? receiveDate : ZonedDateTime.now(),
                        resultSummary: resultSummaryList?.get(index),
                        resultValue: resultValueList?.get(index),
                        executionPeriod: executionPeriodList?.get(index),
                        mark: markList?.get(index),
                        inspectionCategoryResult: applicantInspectionCategoryResultInstance,
                        inspection: inspection)

                //to get list of committee for inspection
                String committeeRoleListName = value + "_committeeRole"
                String partyNameListName = value + "_partyName"
                List inspectionCommitteeRoleList = params.list(committeeRoleListName)
                List inspectionPartyNameList = params.list(partyNameListName)
                // to assign all committee  for inspection
                inspectionPartyNameList?.eachWithIndex { partyName, int index1 ->
                    committeeRole = CommitteeRole.findById(inspectionCommitteeRoleList.get(index1))
                    if (partyName) {
                        applicantInspectionResult.addToCommitteeRoles(partyName: partyName,
                                committeeRole: committeeRole, applicantInspectionResult: applicantInspectionResult)

                    }
                }

                applicantInspectionCategoryResultInstance.addToTestsResult(applicantInspectionResult)
            }

            if (applicantInspectionCategoryResultInstance?.receiveDate >= applicantInspectionCategoryResultInstance?.requestDate) {
                applicantInspectionCategoryResultInstance.inspectionResult = applicantInspectionCategoryResultInstance?.inspectionResult ?: EnumInspectionResult.NEW
                applicantInspectionCategoryResultInstance.save();
            }
            //in case: applicant inspection category results  status REQUESTED_BY_APPLICANT
            else if (applicantInspectionCategoryResultInstance?.receiveDate == null) {
                applicantInspectionCategoryResultInstance.save();
            } else {
                applicantInspectionCategoryResultInstance.errors.reject('applicantInspectionCategoryResult.dateError.label', [] as Object[], "")
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            applicantInspectionCategoryResultInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return applicantInspectionCategoryResultInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            String id
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = (HashHelper.decode(deleteBean?.ids[0]))
                ApplicantInspectionCategoryResult.findById(id)*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                ApplicantInspectionCategoryResult.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
     * @return ApplicantInspectionCategoryResult.
     */
    @Transactional(readOnly = true)
    ApplicantInspectionCategoryResult getInstance(GrailsParameterMap params) {
        //if id is not null then return values from search method
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
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
        String nameProperty = params["nameProperty"] ?: "applicant.personName"
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