package ps.gov.epsilon.hr.firm.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.EducationDegreeService
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationDegreeDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationMajorDTO


/**
 * <h1>Purpose</h1>
 * -this service is aim to create a job
 * <h1>Usage</h1>
 * -this service is used to create a job
 * <h1>Restriction</h1>
 * -need firm & job category created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JobService {

    MessageSource messageSource
    def formatService
    EducationDegreeService educationDegreeService
    EducationMajorService educationMajorService
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "code", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobCategory.descriptionInfo.localName", type: "JobCategory", source: 'domain'],
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
        Short fromAge = params.long("fromAge")
        Short fromWeight = params.long("fromWeight")
        Short fromHeight = params.long("fromHeight")
        String jobCategoryId = params["jobCategory.id"]
        Set joinedJobEducationDegreesIds = params.listString("joinedJobEducationDegrees.id")
        Set joinedJobEducationMajorsIds = params.listString("joinedJobEducationMajors.id")
        Set joinedJobInspectionCategoriesIds = params.listString("joinedJobInspectionCategories.id")
        Set joinedJobMilitaryRanksIds = params.listString("joinedJobMilitaryRanks.id")
        Set joinedJobOperationalTasksIds = params.listString("joinedJobOperationalTasks.id")
        String note = params["note"]
        Short toAge = params.long("toAge")
        Short toWeight = params.long("toWeight")
        Short toHeight = params.long("toHeight")
        String universalCode = params["universalCode"]
        String status = params["status"]

        return Job.createCriteria().list(max: max, offset: offset) {
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

                if (fromAge) {
                    eq("fromAge", fromAge)
                }
                if (fromWeight) {
                    eq("fromWeight", fromWeight)
                }
                if (fromHeight) {
                    eq("fromHeight", fromHeight)
                }
                if (jobCategoryId) {
                    eq("jobCategory.id", jobCategoryId)
                }
                if (joinedJobEducationDegreesIds) {
                    joinedJobEducationDegrees {
                        inList("id", joinedJobEducationDegreesIds)
                    }
                }
                if (joinedJobEducationMajorsIds) {
                    joinedJobEducationMajors {
                        inList("id", joinedJobEducationMajorsIds)
                    }
                }
                if (joinedJobInspectionCategoriesIds) {
                    joinedJobInspectionCategories {
                        inList("id", joinedJobInspectionCategoriesIds)
                    }
                }
                if (joinedJobMilitaryRanksIds) {
                    joinedJobMilitaryRanks {
                        inList("id", joinedJobMilitaryRanksIds)
                    }
                }
                if (joinedJobOperationalTasksIds) {
                    joinedJobOperationalTasks {
                        inList("id", joinedJobOperationalTasksIds)
                    }
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (toAge) {
                    eq("toAge", toAge)
                }
                if (toWeight) {
                    eq("toWeight", toWeight)
                }
                if (toHeight) {
                    eq("toHeight", toHeight)
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
 * @return Job.
 */
    Job save(GrailsParameterMap params) {
        Job jobInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            jobInstance = Job.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (jobInstance.version > version) {
                    jobInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('job.label', null, 'job', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this job while you were editing")
                    return jobInstance
                }
            }
            if (!jobInstance) {
                jobInstance = new Job()
                jobInstance.errors.reject('default.not.found.message', [messageSource.getMessage('job.label', null, 'job', LocaleContextHolder.getLocale())] as Object[], "This job with ${params.id} not found")
                return jobInstance
            }
        } else {
            jobInstance = new Job()
        }
        try {

            //remove records from hasMany tables when edit job
            if (jobInstance?.id) {
                JoinedJobEducationDegree.executeUpdate('delete from JoinedJobEducationDegree educationDegrees where educationDegrees.job.id =:jobId', ['jobId': jobInstance?.id])
                JoinedJobEducationMajor.executeUpdate('delete from JoinedJobEducationMajor educationDegrees where educationDegrees.job.id =:jobId', ['jobId': jobInstance?.id])
                JoinedJobOperationalTask.executeUpdate('delete from JoinedJobOperationalTask operationalTask where operationalTask.job.id =:jobId', ['jobId': jobInstance?.id])
                JoinedJobMilitaryRank.executeUpdate('delete from JoinedJobMilitaryRank militaryRank where militaryRank.job.id =:jobId', ['jobId': jobInstance?.id])
                JoinedJobInspectionCategory.executeUpdate('delete from JoinedJobInspectionCategory inspectionCategory where inspectionCategory.job.id =:jobId', ['jobId': jobInstance?.id])
            }

            //to assign job properties
            jobInstance.properties = params;


            List educationDegreesList = params.list("educationDegrees")
            List educationMajorList = params.list("educationMajor")
            List operationalTaskList = params.list("operationalTask")
            List militaryRankList = params.list("militaryRank")
            List inspectionCategoryList = params.list("inspectionCategory")

            //to assign list of education degree for job
            educationDegreesList?.eachWithIndex { value, index ->
                jobInstance.addToJoinedJobEducationDegrees(job: jobInstance, educationDegreeId: educationDegreesList.get(index))
            }
            //to assign list of education major for job
            educationMajorList?.eachWithIndex { value, index ->
                jobInstance.addToJoinedJobEducationMajors(job: jobInstance, educationMajorId: educationMajorList.get(index))
            }

            //to assign list of inspection category for job
            inspectionCategoryList?.eachWithIndex { value, index ->
                jobInstance.addToJoinedJobInspectionCategories(job: jobInstance, inspectionCategory: InspectionCategory.load((inspectionCategoryList.get(index) + "")))
            }

            //to assign list of operational task for job
            operationalTaskList?.eachWithIndex { value, index ->
                jobInstance.addToJoinedJobOperationalTasks(job: jobInstance, operationalTask: OperationalTask.load((operationalTaskList.get(index) + "")))
            }

            //to assign list of military rank for job
            militaryRankList?.eachWithIndex { value, index ->
                jobInstance.addToJoinedJobMilitaryRanks(job: jobInstance, militaryRank: MilitaryRank.load((militaryRankList.get(index) + "")))
            }

            jobInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            jobInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return jobInstance
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

            Job instance = Job.get(id)
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
 * @return Job.
 */
    @Transactional(readOnly = true)
    Job getInstance(GrailsParameterMap params) {
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
     * @return Job.
     */
    @Transactional(readOnly = true)
    Job getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList jobList = search(params)
        if (jobList) {
            List<EducationDegreeDTO> educationDegreeDTOList = []
            List<EducationMajorDTO> educationMajorDTOList = []
            List educationDegreeList = []
            List educationMajorList = []
            educationDegreeList.addAll(jobList?.resultList?.joinedJobEducationDegrees?.educationDegreeId)
            educationMajorList.addAll(jobList?.resultList?.joinedJobEducationMajors?.educationMajorId)
            SearchBean searchBean = new SearchBean()

            if (educationDegreeList.size() > 0 ? educationDegreeList.get(0) : false) {
                // get the list of educationDegrees that had been selected
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: jobList?.resultList?.joinedJobEducationDegrees?.educationDegreeId?.get(0)))
                educationDegreeDTOList = educationDegreeService?.searchEducationDegree(searchBean)?.resultList

            }
            if (educationMajorList.size() > 0 ? educationMajorList?.get(0) : false) {
                // get the list of educationMajor that had been selected
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: educationMajorList?.get(0)))
                educationMajorDTOList = educationMajorService?.searchEducationMajor(searchBean)?.resultList
            }

            jobList?.each { Job job ->
                job.transientData = [:]

                job.transientData.put("educationDegreeMapList", educationDegreeDTOList?.collect {
                    [it.id, it.descriptionInfo.localName]
                })
                job.transientData.put("educationDegreeName", educationDegreeDTOList?.descriptionInfo?.localName)

                job.transientData.put("educationMajorMapList", educationMajorDTOList?.collect {
                    [it.id, it.descriptionInfo.localName]
                })
                job.transientData.put("educationMajorName", educationMajorDTOList?.descriptionInfo?.localName)

                job.transientData.put("inspectionCategoryMapList", job?.joinedJobInspectionCategories?.collect {
                    [it.inspectionCategory.id, it.inspectionCategory.descriptionInfo.localName]
                })
            }
        }
        return jobList
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