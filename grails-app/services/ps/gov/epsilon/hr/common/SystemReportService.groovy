package ps.gov.epsilon.hr.common

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.hibernate.transform.Transformers
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.DepartmentService
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils

@Transactional
class SystemReportService {

    DepartmentService departmentService
    def sessionFactory


    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: false, search: true, hidden: true, name: "militaryRankId", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "militaryRankName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "departmentName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "employeeCount", type: "Integer", source: 'domain'],
    ]


    /**
     * to get general system report.
     * @return Map.
     */
    Map getGeneralReportSettings(){
        List<FirmSetting> firmSettings = FirmSetting.createCriteria().list{
            eq('firm.id',PCPSessionUtils.getValue("firmId"))
            isNotNull("propertyValue")
        }
        Map map = [:]
        firmSettings.each {FirmSetting firmSetting->
            if(firmSetting?.propertyValue) {
                switch (firmSetting.propertyName) {
                    case EnumFirmSetting.HEADER_ARABIC_COUNTRY_VALUE.value:
                        map["headerArabicCountry"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.HEADER_ENGLISH_COUNTRY_VALUE.value:
                        map["headerEnglishCountry"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.HEADER_ARABIC_MINISTRY_VALUE.value:
                        map["headerArabicMinistry"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.HEADER_ENGLISH_MINISTRY_VALUE.value:
                        map["headerEnglishMinistry"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.HEADER_ARABIC_ORGANIZATION_VALUE.value:
                        map["headerArabicOrganization"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.HEADER_ENGLISH_ORGANIZATION_VALUE.value:
                        map["headerEnglishOrganization"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.REPORT_LOGO_IMAGE_NAME.value:
                        map["imageLogoName"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.REPORT_GREEN_IMAGE_NAME.value:
                        map["imageGreenName"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.REPORT_MINISTRY_IMAGE_NAME.value:
                        map["imageMinistryName"] = firmSetting?.propertyValue
                        break
                    case EnumFirmSetting.USER_NAME_LABEL_VALUE.value:
                        map["userNameLabelValue"] = firmSetting?.propertyValue
                        break
                    default:
                        break
                }
            }
        }
        return map
    }

    /**
     * to get data for military rank report.
     * @param GrailsParameterMap params the search map.
     * @return List.
     */
    @Transactional(readOnly = true)
    List getMilitaryRankData(GrailsParameterMap params) {

        final session = sessionFactory.currentSession

        List<String> departmentIds = params.listString('departmentIds')
        List<String> militaryRankIds = params.listString('militaryRankIds')
        GrailsParameterMap departmentParams = new GrailsParameterMap(["ids[]":departmentIds],null)
        List<Department> departmentList = []
        if(departmentIds){
            departmentList = departmentService.customHierarchySearch(departmentParams)
        }

        Map sqlParamsMap = [firmIdParams:PCPSessionUtils.getValue("firmId")]

        String paramsQuery = ""

        //if statements to check the params
        if(militaryRankIds) {
            paramsQuery = paramsQuery + " AND mr.id in ( :militaryRankIdsParams ) \n"
            sqlParamsMap.put("militaryRankIdsParams", militaryRankIds)
        }


        if(departmentList?.size() > 0) {
            paramsQuery = paramsQuery + " AND d.id in ( :departmentIdsParams ) \n"
            sqlParamsMap.put("departmentIdsParams", departmentList?.id)
        }

        if(militaryRankIds) {
            paramsQuery = paramsQuery + " AND mr.id in (:idsParam) \n"
            sqlParamsMap.put("idsParam", militaryRankIds)
        }


        String query = "FROM employee e,employee_promotion ep,employment_record er,department d,military_rank mr\n" +
                "WHERE e.current_employee_military_rank_id = ep.id \n" +
                "AND e.current_employment_record_id = er.id\n" +
                "AND mr.id = ep.military_rank_id\n" +
                "AND er.department_id = d.id\n" +
                "AND e.firm_id = :firmIdParams \n" +
                "AND e.status = '${GeneralStatus.ACTIVE}'\n" +
                " ${paramsQuery} " +
                "GROUP BY mr.id,mr.local_name,d.id,d.local_name"


        String selectQuery = "SELECT mr.id as military_rank_id, \n" +
                "mr.local_name as military_rank_name, \n" +
                "d.id as department_id,d.local_name as department_name, \n" +
                "count(mr.id) as employee_count \n"

        Query sqlQuery = session.createSQLQuery(selectQuery + query)

        sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        sqlParamsMap?.each {
            if(it.value instanceof List){
                sqlQuery.setParameterList(it.key.toString(), it.value)
            }else{
                sqlQuery.setParameter(it.key.toString(), it.value)
            }
        }

        final queryResults = sqlQuery.list()

        List data = []
        Map map = [:]
        queryResults.eachWithIndex { resultRow,index ->
            map = [:]
            map["militaryRankId"] = resultRow["military_rank_id"]
            map["militaryRankName"] = resultRow["military_rank_name"]
            map["departmentId"] = resultRow["department_id"]
            map["departmentName"] = resultRow["department_name"]
            map["employeeCount"] = resultRow["employee_count"]
            data << map
            map = null
        }
        return data
    }

}
