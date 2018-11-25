package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.orm.hibernate.cfg.GrailsDomainBinder
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.workflow.OperationWorkflowSetting
import ps.gov.epsilon.workflow.OperationWorkflowSettingParam
import ps.gov.epsilon.workflow.Workflow
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.WorkflowStep
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowSettingType
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.interfaces.v1.IJobCategoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

/**
 * <h1>Purpose</h1>
 * this service is aims to create job category for firm
 * <h1>Usage</h1>
 * -used for firm
 * <h1>Restriction</h1>
 * - needs a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JobCategoryService implements IJobCategoryService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
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
        String description = params["description"]
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String universalCode = params["universalCode"]
        String status = params["status"]

        return JobCategory.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
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
                if (description) {
                    ilike("description", "%${description}%")
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (universalCode) {
                    ilike("universalCode", "%${universalCode}%")
                }
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
 * @return JobCategory.
 */
    JobCategory save(GrailsParameterMap params) {
        JobCategory jobCategoryInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params["id"]) {
            jobCategoryInstance = JobCategory.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (jobCategoryInstance.version > version) {
                    jobCategoryInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('jobCategory.label', null, 'jobCategory', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this jobCategory while you were editing")
                    return jobCategoryInstance
                }
            }
            if (!jobCategoryInstance) {
                jobCategoryInstance = new JobCategory()
                jobCategoryInstance.errors.reject('default.not.found.message', [messageSource.getMessage('jobCategory.label', null, 'jobCategory', LocaleContextHolder.getLocale())] as Object[], "This jobCategory with ${params["id"]} not found")
                return jobCategoryInstance
            }
        } else {
            jobCategoryInstance = new JobCategory()
        }
        try {
            jobCategoryInstance.properties = params;
            jobCategoryInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            jobCategoryInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return jobCategoryInstance
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

            JobCategory instance = JobCategory.get(id)
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
 * @return JobCategory.
 */
    @Transactional(readOnly = true)
    JobCategory getInstance(GrailsParameterMap params) {
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

    /***
     * this method used to get the information of the employment record of the employee who
     * suitable with the job category, department, and job title that selected if exist
     * it used by workflow plugin
     * @param jobCategoryId
     * @param jobTitleId
     * @param nodeId by defualt it contains the department id of the employee who own the request
     * @return Map < String , String >
     */
    public Map<String, String> findJobCategoryForDepartment(String jobCategoryId, String jobTitleId, String nodeId) {
        // get the firm of
        Firm firm

        if (jobCategoryId && jobCategoryId != "null") {
            firm = JobCategory.findById(jobCategoryId)?.firm
        } else if (jobTitleId && jobTitleId != "null") {
            firm = JobTitle.findById(jobTitleId)?.firm
        } else {
            firm = Department.findById(nodeId)?.firm
        }

        if (!firm) {
            throw new Exception("All parameters cannot be null")
        }

        Map<String, String> info = [:]
        Map<String, Object> sqlParams = [:]

        //get the committed employees who suitable with the params
        String sql = "from EmploymentRecord er where er.toDate=:nullDate and " +
                "er.employee.categoryStatus.id=:committedStatusId "

        //to_date is null to get the current zone date time and active record
        sqlParams.put("nullDate", PCPUtils.DEFAULT_ZONED_DATE_TIME)
        sqlParams.put("committedStatusId", EnumEmployeeStatusCategory.COMMITTED.getValue(firm.code))

        if (jobTitleId && jobTitleId != "null") {
            sqlParams.put("jobTitleId", jobTitleId)
            sql += " and er.jobTitle.id=:jobTitleId "
        } else if (jobCategoryId && jobCategoryId != "null") {
            sqlParams.put("jobCategoryId", jobCategoryId)
            sql += "  and er.jobTitle in (select jt.id from JobTitle jt where jt.jobCategory.id=:jobCategoryId) "
        }

        if (nodeId && nodeId != "null") {
            sqlParams.put("departmentId", nodeId)
            sql += " and er.department.id=:departmentId "
        }


        sql += " order by id desc "

        List<EmploymentRecord> employmentRecordList = EmploymentRecord.executeQuery(sql, sqlParams)

        if (employmentRecordList && employmentRecordList.size() > 0) {

            EmploymentRecord employmentRecord = employmentRecordList.get(0)

            info.put("nodeId", employmentRecord?.department.id?.toString())
            info.put("jobTitleId", employmentRecord?.jobTitleId?.toString())
            info.put("jobCategoryId", employmentRecord?.jobTitle?.jobCategoryId.toString())
            info.put("employeeId", employmentRecord?.employeeId?.toString())

            return info
        } else {
            //get the parent department and search
            Department department = Department.findById(nodeId)
            if (department?.managerialParentDeptId) {
                println 'Position (' + jobCategoryId + ') not found will do recursion in department :' + department.managerialParentDeptId?.toString() + ",name:" + Department.get(department.managerialParentDeptId)?.descriptionInfo?.localName
                return findJobCategoryForDepartment(jobCategoryId, jobTitleId, department.managerialParentDeptId?.toString())
            } else {
                println 'Position (' + jobCategoryId + ') not found '
                return null
            }
        }
    }

    @Override
    JSON autoCompleteJobCategory(GrailsParameterMap params) {
        return autoComplete(params)
    }
/**
 * this method was created to get a list of map of jobCategory
 * @param params
 * @return list of map
 */
    @Transactional(readOnly = true)
    public List getJobCategory(GrailsParameterMap params) {
        List<JobCategory> jobCategoryList = []
        /**
         * get list of jobCategory by ids
         */
        List ids = params.listString('ids[]')

        if (ids) {
            jobCategoryList = JobCategory.findAllByIdInList(ids)
        }

        /**
         * create list of map contains only jobCategory id & name
         */
        List list = []
        if (jobCategoryList.size() > 0) {
            list = jobCategoryList.collect { [id: it?.id, name: it?.descriptionInfo?.localName] }
        }

        /**
         * return list of map
         */
        return list
    }
}