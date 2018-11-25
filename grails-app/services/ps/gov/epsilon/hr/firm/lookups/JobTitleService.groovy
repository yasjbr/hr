package ps.gov.epsilon.hr.firm.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.workflow.interfaces.v1.IJobTitleService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.lookups.EducationDegreeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationDegreeDTO

/**
 * <h1>Purpose</h1>
 * -this service is aim to create job title
 * <h1>Usage</h1>
 * -this service is used to create job title
 * <h1>Restriction</h1>
 * -need firm & job category created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JobTitleService implements IJobTitleService{

    MessageSource messageSource
    def formatService
    EducationDegreeService educationDegreeService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            //[sort: true, search: true, hidden: true, name: "code", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobCategory.descriptionInfo.localName", type: "string", source: 'domain'],
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
        String code = params["code"]
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        Long firmId = params.long("firm.id")
        String jobCategoryId = params["jobCategory.id"]
        Set joinedJobTitleEducationDegreesIds = params.listString("joinedJobTitleEducationDegrees.id")
        Set joinedJobTitleJobRequirementsIds = params.listString("joinedJobTitleJobRequirements.id")
        Set joinedJobTitleMilitaryRanksIds = params.listString("joinedJobTitleMilitaryRanks.id")
        Set joinedJobTitleOperationalTasksIds = params.listString("joinedJobTitleOperationalTasks.id")
        String note = params["note"]
        String universalCode = params["universalCode"]
        String status = params["status"]

        return JobTitle.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("code", sSearch)
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
                if (code) {
                    ilike("code", "%${code}%")
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                if (jobCategoryId) {
                    eq("jobCategory.id", jobCategoryId)
                }
                if (joinedJobTitleEducationDegreesIds) {
                    joinedJobTitleEducationDegrees {
                        inList("id", joinedJobTitleEducationDegreesIds)
                    }
                }
                if (joinedJobTitleJobRequirementsIds) {
                    joinedJobTitleJobRequirements {
                        inList("id", joinedJobTitleJobRequirementsIds)
                    }
                }
                if (joinedJobTitleMilitaryRanksIds) {
                    joinedJobTitleMilitaryRanks {
                        inList("id", joinedJobTitleMilitaryRanksIds)
                    }
                }
                if (joinedJobTitleOperationalTasksIds) {
                    joinedJobTitleOperationalTasks {
                        inList("id", joinedJobTitleOperationalTasksIds)
                    }
                }
                if (note) {
                    ilike("note", "%${note}%")
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

                switch (columnName) {
                    case "jobCategory.descriptionInfo.localName":
                        jobCategory {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
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
 * @return JobTitle.
 */
    JobTitle save(GrailsParameterMap params) {
        JobTitle jobTitleInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            jobTitleInstance = JobTitle.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (jobTitleInstance.version > version) {
                    jobTitleInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('jobTitle.label', null, 'jobTitle', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this jobTitle while you were editing")
                    return jobTitleInstance
                }
            }
            if (!jobTitleInstance) {
                jobTitleInstance = new JobTitle()
                jobTitleInstance.errors.reject('default.not.found.message', [messageSource.getMessage('jobTitle.label', null, 'jobTitle', LocaleContextHolder.getLocale())] as Object[], "This jobTitle with ${params.id} not found")
                return jobTitleInstance
            }
            //Do not allow to uncheck job title option (تكرار ضمن نفس الوحده الادارية) when their is more than employee within the same job title
            if (!params.boolean("allowToRepeetInUnit")) {
                def employeesCountsList = Employee.executeQuery("select count(*) from Employee where currentEmploymentRecord.jobTitle.id = :jobTitleId group by currentEmploymentRecord.department.id",[jobTitleId :jobTitleInstance?.id])
                for( employeesCount in employeesCountsList) {
                    if(employeesCount > 1){
                        jobTitleInstance.errors.reject('jobTitle.allowToRepeetInUnitEdit.error')
                        break
                    }
                }
                if(jobTitleInstance?.hasErrors()){
                    return jobTitleInstance
                }
            }
        } else {
            jobTitleInstance = new JobTitle()
        }
        try {
            //remove records from hasMany tables when edit job title
            if (jobTitleInstance?.id) {
                JoinedJobTitleEducationDegree.executeUpdate('delete from JoinedJobTitleEducationDegree educationDegrees where educationDegrees.jobTitle.id =:jobTitleId', ['jobTitleId': jobTitleInstance?.id])
                JoinedJobTitleOperationalTask.executeUpdate('delete from JoinedJobTitleOperationalTask operationalTask where operationalTask.jobTitle.id =:jobTitleId', ['jobTitleId': jobTitleInstance?.id])
                JoinedJobTitleMilitaryRank.executeUpdate('delete from JoinedJobTitleMilitaryRank militaryRank where militaryRank.jobTitle.id =:jobTitleId', ['jobTitleId': jobTitleInstance?.id])
                JoinedJobTitleJobRequirement.executeUpdate('delete from JoinedJobTitleJobRequirement jobRequirement where jobRequirement.jobTitle.id =:jobTitleId', ['jobTitleId': jobTitleInstance?.id])

            }

            //assign job title properties
            jobTitleInstance.properties = params

            List educationDegreesList = params.list("educationDegrees")
            List operationalTaskList = params.list("operationalTask")
            List militaryRankList = params.list("militaryRank")
            List jobRequirementList = params.list("JobRequirement")

            //to assign list of degree for job title
            educationDegreesList?.eachWithIndex { value, index ->
                jobTitleInstance.addToJoinedJobTitleEducationDegrees(jobTitle: jobTitleInstance, educationDegreeId: educationDegreesList.get(index))
            }

            //to assign list of operational task for job title
            operationalTaskList?.eachWithIndex { value, index ->
                jobTitleInstance.addToJoinedJobTitleOperationalTasks(jobTitle: jobTitleInstance, operationalTask: OperationalTask.load((operationalTaskList.get(index) + "")))
            }

            //to assign list of military rank for job title
            militaryRankList?.eachWithIndex { value, index ->
                jobTitleInstance.addToJoinedJobTitleMilitaryRanks(jobTitle: jobTitleInstance, militaryRank: MilitaryRank.load((militaryRankList.get(index) + "")))
            }

            //to assign list of job requirement for job title
            jobRequirementList?.eachWithIndex { value, index ->
                jobTitleInstance.addToJoinedJobTitleJobRequirements(jobTitle: jobTitleInstance, jobRequirement: JobRequirement.load((jobRequirementList.get(index) + "")))
            }

            jobTitleInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            jobTitleInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return jobTitleInstance
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

            JobTitle instance = JobTitle.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('firm.deleteMessage.label')
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
 * @return JobTitle.
 */
    @Transactional(readOnly = true)
    JobTitle getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return JobTitle.
     */
    @Transactional(readOnly = true)
    JobTitle getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList jobTitleList = search(params)
        if (jobTitleList) {

            List educationDegreeList = []
            List<EducationDegreeDTO> educationDegreeDTOList
            educationDegreeList.addAll(jobTitleList?.resultList?.joinedJobTitleEducationDegrees?.educationDegreeId?.collect())

            if (educationDegreeList.size() > 0 ? educationDegreeList?.get(0) : false) {
                // get the list of educationDegrees that had been selected
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: educationDegreeList.get(0)))
                educationDegreeDTOList = educationDegreeService.searchEducationDegree(searchBean)?.resultList

                jobTitleList?.each { JobTitle jobTitle ->
                    jobTitle.transientData = [:]
                    if (educationDegreeDTOList) {
                        jobTitle.transientData.put("educationDegreeMapList", educationDegreeDTOList?.collect {
                            [it.id, it.descriptionInfo.localName]
                        })
                        jobTitle.transientData.put("educationDegreeName", educationDegreeDTOList?.descriptionInfo?.localName)
                    }
                }
            }
            return jobTitleList
        }
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
     * this method was created to get a list of map of jobTitle
     * @param params
     * @return list of map
     */
    @Override
    JSON autoCompleteJobTitle(GrailsParameterMap params) {
        return autoComplete(params)
    }

    @Transactional(readOnly = true)
    public List getJobTitle(GrailsParameterMap params) {
        List<JobTitle> jobTitleList = []
        /**
         * get list of jobTitle by ids
         */
        List ids = params.listString('ids[]')
        if (ids) {
            jobTitleList = JobTitle.findAllByIdInList(ids)
        }

        /**
         * create list of map contains only jobTitle id & name
         */
        List list = []
        if (jobTitleList.size() > 0) {
            list = jobTitleList.collect { [id: it?.id, name: it?.descriptionInfo?.localName] }
        }

        /**
         * return list of map
         */
        return list
    }
}