package ps.gov.epsilon.hr.firm.profile

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for Employee service
 */
class EmployeeIntegrationSpec extends CommonIntegrationSpec {

    def setupSpec() {
        domain_class = Employee
        service_domain = EmployeeService
        entity_name = "employee"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(Employee)
        filtered_parameters = ["personId"];
        autocomplete_property = "transientData.personDTO.localFullName"
        exclude_methods = ["delete"]
        primary_keys = ["encodedId","id"]

        //currentEmploymentRecord
        TestDataObject currentEmploymentRecord = new TestDataObject()
        currentEmploymentRecord.domain = EmploymentRecord
        currentEmploymentRecord.objectName = "employmentRecord"
        currentEmploymentRecord.paramName = "employmentRecordData"
        currentEmploymentRecord.requiredProperties = PCPUtils.getRequiredFields(EmploymentRecord)

        //currentEmployeeMilitaryRank
        TestDataObject currentEmployeeMilitaryRank = new TestDataObject()
        currentEmployeeMilitaryRank.domain = EmployeePromotion
        currentEmployeeMilitaryRank.objectName = "employeePromotion"
        currentEmployeeMilitaryRank.paramName = "militaryRankData"
        currentEmployeeMilitaryRank.requiredProperties = PCPUtils.getRequiredFields(EmployeePromotion)

        include_save_properties = [currentEmploymentRecord,currentEmployeeMilitaryRank]
    }


    /**
     * @goal test count method.
     * @expectedResult known total count.
     */
    def "test count"() {
        setup:
        println("*****************************test count******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        def result = serviceInstance.count(params)
        then:
        result == (entity_total_count + 3)

        println("test instance count done with result ${result}")
    }

    /**
     * @goal test search method with filter data.
     * @expectedResult known total count.
     */
    def "test count search"() {
        setup:
        println("*****************************test count search******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]

        sendParams("personId", testInstance, map)

        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        result = serviceInstance.count(params)
        then:
        result == 1
        println("test count done with result ${result}")
    }

}