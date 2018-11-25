package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for SuspensionExtensionRequest service
 */
class SuspensionExtensionRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = SuspensionExtensionRequest
        service_domain = SuspensionExtensionRequestService
        entity_name = "suspensionExtensionRequest"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(SuspensionExtensionRequest)
        filtered_parameters = ["id"]
        exclude_methods = ["autocomplete", "save", "delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_remoting = true
    }

    /**
     * @goal test save method.
     * @expectedResult valid instance.
     */
    def "test success save"() {
        setup:
        println("*****************************test success save******************************************")



        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion, currentEmploymentRecord: employmentRecord)
        SuspensionRequest suspensionRequest = SuspensionRequest.build(employee: employee,
                fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1L))

        def testInstance = SuspensionExtensionRequest.build(employee: employee, suspensionRequest: suspensionRequest,
                fromDate: ZonedDateTime.now().plusYears(1L), toDate: ZonedDateTime.now().plusYears(2L))

        def instanceToSave = testInstance
        Map map = [:]
        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value != null) {
                map.put(property, value)
            } else {
                value = getPropertyValue(property, instanceToSave)
                if (value == null) {
                    value = entity_name + "_" + property + "_" + counter
                }
                map.put(property, value)
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = saveEntity(object, true)
            params[object.paramName] = null
            object.requiredProperties.each { property ->
                params[object.paramName + "." + property] = getPropertyValue(property, objectParams)
            }
            objectParams = null
        }
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.save(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property, testInstance) != null
            getPropertyValue(property, testInstance) == getPropertyValue(property, map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test save success done with instance ${testInstance}")
    }

    /**
     * @goal test save method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail save"() {
        setup:
        println("*****************************test fail save******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        include_save_properties.each { TestDataObject object ->
            params[object.paramName] = null
            object.requiredProperties.each { property ->
                params[object.paramName + "." + property] = null
            }
        }
        testInstance = serviceInstance.save(params)
        then:
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance save fail with errors ${testInstance.errors.allErrors}")
    }
}