package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

/**
 *<h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class JoinedRecruitmentCycleDepartmentService {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    PersonService personService


    public static getEncodedId = { cService, JoinedRecruitmentCycleDepartment rec, object, params ->
        if(rec?.encodedId) {
            return rec?.encodedId
        }else {
            return rec?.id
        }
    }

    public static getDepartment = { cService, JoinedRecruitmentCycleDepartment rec, object, params ->
        if(rec?.department) {
            return rec?.department?.descriptionInfo?.localName
        }else {
            return ""
        }
    }

    public static getDepartmentManager = { cService, JoinedRecruitmentCycleDepartment rec, object, params ->
        if(rec?.department) {
            def employee = Employee.executeQuery("select emp from Employee emp where currentEmploymentRecord.department.id=:departmentId And currentEmploymentRecord.jobTitle.jobCategory.descriptionInfo.localName=:jobCategoryName",[departmentId:rec?.department?.id,jobCategoryName:"مدير ادارة عامة"])

            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employee[0]?.personId))
            /*PersonDTO personDTO = personService.getPerson(searchBean)

            return personDTO?.localFullName*/
            return ""
        }else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: getEncodedId, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "department", type: getDepartment, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "transientData.departmentManagerFullName", type: "String", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "recruitmentCycleDepartmentStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params, Boolean isEncrypted = false){
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if(column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))

        List<String> ids = params.listString('ids[]')
        String id
        if (isEncrypted && params.id) {
            id = (HashHelper.decode(params.id))
        } else {
            id = params['id']
        }


        List<Map<String,String>> orderBy = params.list("orderBy")
        String departmentId = params["department.id"]

        String recruitmentCycleId
        if (params["recruitmentCycle.id"]) {
            recruitmentCycleId = params["recruitmentCycle.id"]
        } else if(params.encodedRecruitmentCycleId) {
            recruitmentCycleId = (HashHelper.decode(params.encodedRecruitmentCycleId))
        }

        ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus recruitmentCycleDepartmentStatus = params["recruitmentCycleDepartmentStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus.valueOf(params["recruitmentCycleDepartmentStatus"]) : null

        return JoinedRecruitmentCycleDepartment.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                        if(departmentId){
                    eq("department.id", departmentId)
                }
                        if(recruitmentCycleId){
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                        if(recruitmentCycleDepartmentStatus){
                    eq("recruitmentCycleDepartmentStatus", recruitmentCycleDepartmentStatus)
                }
            }
            if(orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            }else if(columnName){
                order(columnName, dir)
            }
        }
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params, Boolean isEncrypted = false) {
        PagedResultList JoinedRecruitmentCycleDepartmentList = search(params,isEncrypted)

        if (JoinedRecruitmentCycleDepartmentList) {
            PagedResultList generalManagerEmployeePagedResultList
            PagedResultList departmentManagerEmployeePagedResultList
            GrailsParameterMap employeeParam
            Firm firm = Firm.findById(PCPSessionUtils.getValue("firmId"))

            employeeParam = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            employeeParam["departmentIds"] = JoinedRecruitmentCycleDepartmentList?.resultList?.department?.id?.toList()
            employeeParam["max"] = Integer.MAX_VALUE

            employeeParam["jobCategory.id"] = firm.code + "-12"
            generalManagerEmployeePagedResultList = employeeService.searchWithRemotingValues(employeeParam)

            employeeParam["jobCategory.id"] = firm.code + "-8"
            departmentManagerEmployeePagedResultList = employeeService.searchWithRemotingValues(employeeParam)

            JoinedRecruitmentCycleDepartmentList?.each { JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment ->
                def employee = generalManagerEmployeePagedResultList?.find{
                    it?.currentEmploymentRecord?.department?.id == joinedRecruitmentCycleDepartment?.department?.id
                }
                if(!employee){
                    employee = departmentManagerEmployeePagedResultList?.find{
                        it?.currentEmploymentRecord?.department?.id == joinedRecruitmentCycleDepartment?.department?.id
                    }
                }
                joinedRecruitmentCycleDepartment?.transientData?.departmentManagerFullName = employee?.transientData?.personDTO?.localFullName
            }
        }

        return JoinedRecruitmentCycleDepartmentList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return JoinedRecruitmentCycleDepartment.
     */
    JoinedRecruitmentCycleDepartment save(GrailsParameterMap params) {
        JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartmentInstance
        if (params.id) {
            joinedRecruitmentCycleDepartmentInstance = JoinedRecruitmentCycleDepartment.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedRecruitmentCycleDepartmentInstance.version > version) {
                    joinedRecruitmentCycleDepartmentInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('joinedRecruitmentCycleDepartment.label', null, 'joinedRecruitmentCycleDepartment',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedRecruitmentCycleDepartment while you were editing")
                    return joinedRecruitmentCycleDepartmentInstance
                }
            }
            if (!joinedRecruitmentCycleDepartmentInstance) {
                joinedRecruitmentCycleDepartmentInstance = new JoinedRecruitmentCycleDepartment()
                joinedRecruitmentCycleDepartmentInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('joinedRecruitmentCycleDepartment.label', null, 'joinedRecruitmentCycleDepartment',LocaleContextHolder.getLocale())] as Object[], "This joinedRecruitmentCycleDepartment with ${params.id} not found")
                return joinedRecruitmentCycleDepartmentInstance
            }
        } else {
            joinedRecruitmentCycleDepartmentInstance = new JoinedRecruitmentCycleDepartment()
        }
        try {
            joinedRecruitmentCycleDepartmentInstance.properties = params;
            joinedRecruitmentCycleDepartmentInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedRecruitmentCycleDepartmentInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedRecruitmentCycleDepartmentInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)){
                JoinedRecruitmentCycleDepartment.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                JoinedRecruitmentCycleDepartment.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return JoinedRecruitmentCycleDepartment.
     */
    @Transactional(readOnly = true)
    JoinedRecruitmentCycleDepartment getInstance(GrailsParameterMap params) {
        //if id is not null then return values from search method
        if (params.encodedId) {
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
        String idProperty = params["idProperty"]?:"id"
        String nameProperty = params["nameProperty"]?:"descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo")?:[]
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList,nameProperty,idProperty,autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List<String> DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see PagedResultList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList,GrailsParameterMap params,List<String> DOMAIN_COLUMNS = null) {
        if(!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}