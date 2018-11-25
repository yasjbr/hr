package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import javassist.runtime.Desc
import org.grails.web.json.JSONArray
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.hibernate.transform.Transformers
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.v1.EnumDepartmentType
import ps.gov.epsilon.hr.enums.v1.EnumJobCategory
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.workflow.enums.v1.EnumNodeParentType
import ps.gov.epsilon.workflow.interfaces.v1.IDepartmentService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.LocationService
import ps.gov.epsilon.core.location.ManageLocationService
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
 * this service is aims to create department for firm
 * <h1>Usage</h1>
 * -used for firm
 * <h1>Restriction</h1>
 * - needs  firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DepartmentService implements IDepartmentService {

    MessageSource messageSource
    def formatService
    GovernorateService governorateService
    LocationService locationService
    ManageLocationService manageLocationService
    EmployeeService employeeService
    def sessionFactory

    /**
     * get functional  department name
     */
    public static functionalDepartmentLocalName = { cService, Department department, object, params ->
        if (department?.functionalParentDeptId) {
            return Department.load(department?.functionalParentDeptId).descriptionInfo?.localName
        } else {
            return ""
        }
    }

    /**
     * get managerial department name
     */
    public static managerialDepartmentLocalName = { cService, Department department, object, params ->
        if (department?.managerialParentDeptId) {
            return Department.load(department?.managerialParentDeptId)?.descriptionInfo?.localName
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "departmentType", type: "DepartmentType", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "functionalParentDeptId", type: functionalDepartmentLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "managerialParentDeptId", type: managerialDepartmentLocalName, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.locationName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "firm.name", type: "String", source: 'domain'],
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
        List<String> excludeIds = params.listString('excludeIds[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }
        List<Map<String, String>> orderBy = params.list("orderBy")
        Set contactInfosIds = params.listLong("contactInfos.id")
        Set departmentOperationalTasksIds = params.listString("departmentOperationalTasks.id")

        ps.gov.epsilon.hr.enums.v1.EnumDepartmentType enumDepartmentType = params["departmentType"] ? ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.valueOf(params["departmentType"].toString()) : null
        String departmentTypeId = params["departmentTypeId"]

        //TODO: change var name
        List<ps.gov.epsilon.hr.enums.v1.EnumDepartmentType> departmentTypeList = (List<EnumDepartmentType>) params.list("departmentTypeList")




        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String functionalParentDeptId = params["functionalParentDeptId"]
        Long governorateId = params.long("governorateId")
        Long locationId = params.long("locationId")
        String managerialParentDeptId = params["managerialParentDeptId"]
        String note = params["note"]
        String unstructuredLocation = params["unstructuredLocation"]
        String status = params["status"]
        Long firmId = params.long("firm.id")
        //to search about parent
        String recordId
        if (params['recordId']) {
            recordId = (HashHelper.decode(params["recordId"]))
        }
        Boolean isTree = params.boolean("isTree", false)
        // get department Type List
        List<String> includeDepartmentTypesStrings = params.list("departmentTypeList[]")
        List<ps.gov.epsilon.hr.enums.v1.EnumDepartmentType> includeDepartmentTypeList = []
        includeDepartmentTypesStrings.each { value ->
            includeDepartmentTypeList.push(ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.valueOf(value))
        }

        PagedResultList departmentList = Department.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
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
                if (excludeIds) {
                    not {
                        inList("id", excludeIds)
                    }
                }
                if (contactInfosIds) {
                    contactInfos {
                        inList("id", contactInfosIds)
                    }
                }
                if (departmentOperationalTasksIds) {
                    departmentOperationalTasks {
                        inList("id", departmentOperationalTasksIds)
                    }
                }
                if (enumDepartmentType) {
                    departmentType {
                        eq("staticDepartmentType", enumDepartmentType)
                    }
                }
                if (departmentTypeId) {
                    departmentType {
                        eq("id", departmentTypeId)
                    }
                }
                if (departmentTypeList) {//check type in list
                    departmentType {
                        inList("staticDepartmentType", departmentTypeList)
                    }
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                if (functionalParentDeptId) {
                    eq("functionalParentDeptId", functionalParentDeptId)
                }
                if (governorateId) {
                    eq("governorateId", governorateId)
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (managerialParentDeptId) {
                    eq("managerialParentDeptId", managerialParentDeptId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (isTree) {
                    if (recordId) {
                            eq('managerialParentDeptId', recordId)
                    } else {
                        isNull('managerialParentDeptId')
                    }
                }
                if (includeDepartmentTypeList) {
                    departmentType {
                        inList("staticDepartmentType", includeDepartmentTypeList)
                    }
                }

                if(firmId){
                    eq("firm.id", firmId)

                }else  {
                    if(!isTree){
                        eq("firm.id", PCPSessionUtils.getValue("firmId"))

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
        return departmentList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return Department.
     */
    Department save(GrailsParameterMap params) {
        Department departmentInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            departmentInstance = Department.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (departmentInstance.version > version) {
                    departmentInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('department.label', null, 'department', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this department while you were editing")
                    return departmentInstance
                }
            }
            if (!departmentInstance) {
                departmentInstance = new Department()
                departmentInstance.errors.reject('default.not.found.message', [messageSource.getMessage('department.label', null, 'department', LocaleContextHolder.getLocale())] as Object[], "This department with ${params.id} not found")
                return departmentInstance
            }
        } else {
            departmentInstance = new Department()
        }
        try {

            //if the user has AOC role, then firm should be selected in create screen. Otherwise, get the firm from session.
            if (!SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value)) {
                params["firm.id"] = PCPSessionUtils.getValue("firmId")
            }



            //set properties
            departmentInstance.properties = params;
            if (params.long("governorateId")) {
                params.remove("id");
                params.id = params.long("edit_locationId")
                LocationCommand locationCommand = manageLocationService.saveLocation(params);
                departmentInstance.locationId = locationCommand.id
            }
            departmentInstance.save();
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            departmentInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return departmentInstance
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

            Department instance = Department.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('department.deleteMessage.label')
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
     * @return Department.
     */
    @Transactional(readOnly = true)
    Department getInstance(GrailsParameterMap params) {
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList departmentList = search(params)
        if (departmentList) {
            LocationDTO locationDTO
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: departmentList?.resultList?.locationId))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
            departmentList.each { Department department ->
                department.transientData = [:]
                locationDTO = locationList?.find { it?.id == department?.locationId }
                department.transientData.put("locationDTO", locationDTO)
                department.transientData.put("locationName", LocationAddressUtil.renderLocation(locationDTO, department.unstructuredLocation))
                department.transientData.put("functionalParentDeptName", Department.load(department?.functionalParentDeptId)?.descriptionInfo?.localName)
                department.transientData.put("managerialParentDeptName", Department.load(department?.managerialParentDeptId)?.descriptionInfo?.localName)
            }
        }

        return departmentList
    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return Department.
     */
    Department getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                Department department = results[0]
                EmploymentRecord employmentRecord = EmploymentRecord.createCriteria().get {
                    eq('department.id', department?.id)
                    jobTitle {
                        eq('jobCategory.id', EnumJobCategory.HEAD_OF_DEPARTMENT.value)
                    }
                    maxResults(1)
                }
                if (employmentRecord) {
                    GrailsParameterMap paramsEmployee = new GrailsParameterMap([id: employmentRecord?.employee?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                    department.transientData.directManager = employeeService.getInstanceWithRemotingValues(paramsEmployee)
                }
                return department
            }
        }
        return null
    }

    /**
     * Used by workflow plugin
     * @param departmentId
     * @param nodeParentType
     * @return
     */
    @Override
    String getNodeParentInfo(String departmentId, EnumNodeParentType nodeParentType = EnumNodeParentType.MANGERIAL) {
        GrailsParameterMap params = new GrailsParameterMap([id: new Long(departmentId)], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Department department = getInstance(params)
        if (department) {
            return nodeParentType == EnumNodeParentType.MANGERIAL ? department.managerialParentDeptId?.toString() : department.functionalParentDeptId?.toString()
        } else {
            return null
        }
    }

    /**
     * this method was created to get a list of map of department
     * @param params
     * @return list of map
     */
    @Transactional(readOnly = true)
    public List getDepartment(GrailsParameterMap params) {
        List<Department> departmentList = []
        /**
         * get list of department by ids
         */
        List<Long> ids = params.listString("ids[]")
        if (ids) {
            departmentList = Department.findAllByIdInList(ids)
        }

        /**
         * create list of map contains only department id & name
         */
        List<Map> list = []
        if (departmentList.size() > 0) {
            list = departmentList.collect { [id: it?.id, name: it?.descriptionInfo?.localName] }
        }

        /**
         * return list of map
         */
        return list
    }

    /**
     * Used by workflow plugin
     * @param params
     * @return
     */
    @Override
    JSON autoCompleteDepartment(GrailsParameterMap params) {
        return autoComplete(params)
    }

    /**
     * custom search to find the number of requests in the disciplinary  list in one select statement for performance issue
     * this solution was suggested and approved by Mureed
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List<Department> customHierarchySearch(GrailsParameterMap params) {
        final session = sessionFactory.currentSession

        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String id = params["id"]
        List<String> ids = params["ids[]"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        Boolean justParent = params.boolean("justParent")
        Boolean justChild = params.boolean("justChild")
        ps.gov.epsilon.hr.enums.v1.EnumDepartmentType departmentType = params["departmentType"] ? ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.valueOf(params["departmentType"].toString()) : null
        List<ps.gov.epsilon.hr.enums.v1.EnumDepartmentType> departmentTypeList = params.list("departmentTypeList")
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        Long functionalParentDeptId = params.long("functionalParentDeptId")
        Long governorateId = params.long("governorateId")
        Long locationId = params.long("locationId")
        Long managerialParentDeptId = params.long("managerialParentDeptId")

        Map sqlParamsMap = [firmIdParams: PCPSessionUtils.getValue("firmId")]

        String orderByQuery = ""
        String paramsQuery = ""

        //if statements to check the params
        if (sSearch) {
            paramsQuery = paramsQuery + " AND local_name like :sSearchParam \n"
            sqlParamsMap.put("sSearchParam", "%" + sSearch + "%")
        }

        if (id) {
            paramsQuery = paramsQuery + " AND id = :idParam \n"
            sqlParamsMap.put("idParam", id)
        }

        if (ids) {
            paramsQuery = paramsQuery + " AND id in (:idsParam) \n"
            sqlParamsMap.put("idsParam", ids)
        }

        if (localName) {
            paramsQuery = paramsQuery + " AND local_name like :localNameParam \n"
            sqlParamsMap.put("localNameParam", "%" + sSearch + "%")
        }

        if (departmentType) {
            paramsQuery = paramsQuery + " AND department_type = :departmentTypeParam \n"
            sqlParamsMap.put("departmentTypeParam", departmentType?.toString())
        }

        if (justParent == true) {
            paramsQuery = paramsQuery + " AND managerial_parent_dept_id is null \n"
        }

        if (justChild == true) {
            paramsQuery = paramsQuery + " AND managerial_parent_dept_id is not null \n"
        }

        String functionQuery = " WITH RECURSIVE res AS ( \n" +
                "   SELECT id, local_name,managerial_parent_dept_id, 1 AS level\n" +
                "   FROM   department\n" +
                "   WHERE  firm_id = :firmIdParams \n" +
                "   AND status = '${GeneralStatus.ACTIVE}' \n" +
                "   ${paramsQuery} " +
                "   UNION  ALL\n" +
                "   SELECT d.id, d.local_name,d.managerial_parent_dept_id, c.level + 1 AS level\n" +
                "   FROM res c \n" +
                "   JOIN department d ON d.managerial_parent_dept_id = c.id \n" +
                "   ) \n"

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("id")) {
            orderByQuery += " ORDER BY id  ${dir};"
        } else if (columnName?.equalsIgnoreCase("descriptionInfo.localName")) {
            orderByQuery += " ORDER BY local_name ${dir};"
        } else {
            orderByQuery += " ORDER BY level;"
        }


        String query = "SELECT id,local_name,managerial_parent_dept_id,level FROM res "

        Query sqlQuery = session.createSQLQuery(functionQuery + query + orderByQuery)

        sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        sqlParamsMap?.each {
            if (it.value instanceof List) {
                sqlQuery.setParameterList(it.key.toString(), it.value)
            } else {
                sqlQuery.setParameter(it.key.toString(), it.value)
            }
        }

        //pagination parameters
//        sqlQuery.setMaxResults(max)
//        sqlQuery.setFirstResult(offset)

        final queryResults = sqlQuery.list()

        List<Department> results = []
        Department department
        queryResults.each { resultRow ->
            department = new Department(
                    id: resultRow["id"],
                    descriptionInfo: new DescriptionInfo(localName: resultRow["local_name"]),
            )
            results << department
            department = null
        }

        return results
    }

    /**
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
    @Transactional(readOnly = true)
    JSON autocompleteHierarchy(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []

        try {
            List<Department> resultList = this.customHierarchySearch(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }

        return dataList as JSON
    }
}