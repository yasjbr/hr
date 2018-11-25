package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.config.v1.Constants
import ps.police.pcore.v2.entity.location.LocationService
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus
import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.LocationAddressUtil
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create interview
 * <h1>Usage</h1>
 * -this service used to create an interview for vacancy
 * <h1>Restriction</h1>
 * -need firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class InterviewService {

    MessageSource messageSource
    def formatService
    LocationService locationService
    ManageLocationService manageLocationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "description", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recruitmentCycle.name", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy.job.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "interviewStatus", type: "Enum", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.locationName", type: "string", source: 'domain'],
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
        Set applicantsIds = params.listString("applicants.id")
        Set committeeRoleIds = params.listString("committeeRole.id")
        String description = params["description"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus interviewStatus = params["interviewStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus.valueOf(params["interviewStatus"]) : null
        Long locationId = params.long("locationId")
        String note = params["note"]
        String recruitmentCycleId = params["recruitmentCycle.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String unstructuredLocation = params["unstructuredLocation"]
        String vacancyId = params["vacancy.id"]
        String status = params["status"]
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        /*
        * get locations which interviews must be in
        * */
        /*
        * get locations which trainees must be in
        * */
        List<Long> locationIds = []
        SearchBean searchBean = PCPUtils.convertParamsToSearchBean(params.location)
        searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: "max", value1: Constants.getINTEGER_MAX_VALUE()))
        locationIds = locationService?.searchLocation(searchBean)?.resultList?.id
        if(!locationIds || locationIds.isEmpty()) {
            locationIds = [-1l]
        }


        return Interview.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike("note", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (applicantsIds) {
                    applicants {
                        inList("id", applicantsIds)
                    }
                }
                if (committeeRoleIds) {
                    committeeRole {
                        inList("id", committeeRoleIds)
                    }
                }
                if (description) {
                    ilike("description", "%${description}%")
                }
                if (fromDate) {
                    ge("fromDate", fromDate)
                }
                if (interviewStatus) {
                    eq("interviewStatus", interviewStatus)
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if(locationIds.size()>0){
                    inList("locationId", locationIds)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (recruitmentCycleId) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                if (toDate) {
                    le("toDate", toDate)
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                if (vacancyId) {
                    eq("vacancy.id", vacancyId)
                }

                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }

                if (toFromDate) {
                    le("fromDate", toFromDate)
                }

                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }

                if (toToDate) {
                    le("toDate", toToDate)
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
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, dir)
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
     * @return Interview.
     */
    Interview save(GrailsParameterMap params) {
        Interview interviewInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            interviewInstance = Interview.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (interviewInstance.version > version) {
                    interviewInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('interview.label', null, 'interview', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this interview while you were editing")
                    return interviewInstance
                }
            }
            if (!interviewInstance) {
                interviewInstance = new Interview()
                interviewInstance.errors.reject('default.not.found.message', [messageSource.getMessage('interview.label', null, 'interview', LocaleContextHolder.getLocale())] as Object[], "This interview with ${params.id} not found")
                return interviewInstance
            }
        } else {
            interviewInstance = new Interview()
            interviewInstance?.interviewStatus = ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus.OPEN
        }
        try {

            //set properties:
            interviewInstance.properties = params

            //to validate posting date less than or equals closing date
            if (interviewInstance.fromDate <= interviewInstance.toDate) {

                if (interviewInstance?.id) {
                    JoinedInterviewCommitteeRole?.executeUpdate("delete from JoinedInterviewCommitteeRole cat where cat.interview.id = :interviewId", [interviewId: interviewInstance?.id])
                }

                if (params.long("governorateId")) {
                    params.remove("id");
                    params.id = params.long("edit_locationId")
                    LocationCommand locationCommand = manageLocationService?.saveLocation(params);
                    interviewInstance?.locationId = locationCommand?.id
                }

                //to get list of committee by ids
                List committeeRoleIds = params.list("committeeRole")
                List<String> partyNameList = params.list("partyName")
                params.remove("committeeRole")
                params.remove("partyName")

                partyNameList?.eachWithIndex { value, index ->
                    interviewInstance.addToCommitteeRoles(new JoinedInterviewCommitteeRole(partyName: value, interview: interviewInstance, committeeRole: CommitteeRole?.findById(committeeRoleIds?.get(index))))
                }
                interviewInstance.save(failOnError: true);

            } else {
                interviewInstance.errors.reject('interview.dateError.label')
                return interviewInstance
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            interviewInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return interviewInstance
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

            Interview instance = Interview.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED && instance?.applicants?.size() == 0) {
                instance?.trackingInfo?.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('interview.deleteMessage.label')
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
     * @return Interview.
     */
    @Transactional(readOnly = true)
    Interview getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return Interview.
     */
    @Transactional(readOnly = true)
    Interview getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
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
        String nameProperty = params["nameProperty"] ?: "description"
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList interviewList = search(params)
        if (interviewList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: interviewList?.resultList?.locationId?.unique()))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
            String locationName = ""

            LocationDTO locationDTO
            interviewList?.each { Interview interview ->

                locationDTO = locationList?.find { it?.id == interview?.locationId }
                if (locationDTO) {
                    interview.transientData = [:]
                    interview.transientData.put("locationDTO", locationDTO)
                    interview.transientData.put("locationName", LocationAddressUtil.renderLocation(locationDTO, interview.unstructuredLocation));
                }
                locationName = ""
            }
            return interviewList
        }
    }

    /**
     * to add change status for interview
     * @param GrailsParameterMap params
     * @return boolean
     */
    public boolean changeInterviewStatus(GrailsParameterMap params) {
        try {
            Interview interviewInstance = Interview.load(HashHelper.decode(params.encodedId))
            if (interviewInstance) {
                //to check if there is no applicant's status under interview
                def result = Applicant.executeQuery("select count(*) from Applicant applicant where applicant.interview.id = :interviewId and applicant.applicantCurrentStatus.applicantStatus= :applicantStatus", [interviewId: interviewInstance?.id, applicantStatus: EnumApplicantStatus.UNDER_INTERVIEW])
                if (result[0] > 0) {
                    return false
                }
                //close interview if there is no applicant's status under interview
                try {
                    interviewInstance.interviewStatus = EnumInterviewStatus.CLOSED
                    interviewInstance.save(failOnError: true)
                    return true
                } catch (Exception ex) {
                    return false
                }
            } else {
                return false
            }
        } catch (Exception ex) {
            return false
        }
    }

    /**
     * to add interview for applicant
     * @param GrailsParameterMap params
     * @return boolean
     */
    public boolean addApplicantToInterview(GrailsParameterMap params) {
        Interview interviewInstance = Interview.load(params["interviewId"])
        //to get the list of applicant
        List checkedApplicantList = params.listString("check_applicantTable1")
        Applicant applicant
        ApplicantStatusHistory applicantStatusHistory

        //to add applicants to interview
        checkedApplicantList?.each { String id ->
            applicant = Applicant.load(id)
            applicant.interview = interviewInstance
            /**
             * add new status for applicant, UNDER_INTERVIEW
             */
            applicantStatusHistory = new ApplicantStatusHistory(applicant: applicant,
                    applicantStatus: EnumApplicantStatus.UNDER_INTERVIEW,
                    fromDate: interviewInstance?.fromDate,
                    toDate: (interviewInstance?.toDate)
            )
            applicant.applicantCurrentStatus = applicantStatusHistory
            applicantStatusHistory = null
        }
        return true
    }

    /**
     * to delete interview from applicant
     * @param GrailsParameterMap params
     * @return boolean
     */
    public boolean deleteApplicantFromInterview(GrailsParameterMap params) {
        try {
            Applicant applicant = Applicant.load(HashHelper.decode(params.encodedId))
            Interview interview = applicant?.interview
            ApplicantStatusHistory applicantStatusHistory
            if (applicant) {
                applicant.interview = null
                //in case: interview start date less than  current date
                    applicant?.applicantCurrentStatus?.toDate = ZonedDateTime.now()
                    applicantStatusHistory = new ApplicantStatusHistory(applicant: applicant,
                            applicantStatus: EnumApplicantStatus.NEW,
                            fromDate: interview?.fromDate,
                            toDate: interview?.toDate
                    )
                    applicant.applicantCurrentStatus = applicantStatusHistory
                return true
            } else {
                return false
            }
        } catch (Exception ex) {
            return false
        }
    }

    /**
     * to get default interview location
     * @return LocationDTO
     */
    public LocationDTO getDefaultInterviewLocation() {
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: "id", value1: 1039L))
        return locationService.getLocation(searchBean)
    }
}