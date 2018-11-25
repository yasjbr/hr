package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionRequest service
 */
class SuspensionRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = SuspensionRequest
        service_domain = SuspensionRequestService
        entity_name = "suspensionRequest"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(SuspensionRequest)
        filtered_parameters = ["id"]
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_keys = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["delete"]
        is_remoting = true
        is_virtual_delete = true
    }

    /**
     * @goal test saveAll method.
     * @expectedResult SuspensionRequest
     */
    def "test_selectEmployee_success"() {
        setup:
        println("************************test_selectEmployee_success********************************")
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap["suspensionType"] = EnumSuspensionType.MEDICAL
        parameterMap["employee.id"] = 1L
        serviceInstance.selectEmployee(parameterMap)

        then:
        def vacationRequest = serviceInstance.selectEmployee(parameterMap)
        println("test select Employee success done with : ${vacationRequest}")
    }

    /**
     * @goal test saveAll method.
     * @expectedResult SuspensionRequest
     */
    def "test_selectEmployee_failed"() {
        setup:
        println("************************test_selectEmployee_failed********************************")
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap["suspensionType"] = null
        parameterMap["employee.id"] = null
        serviceInstance.selectEmployee(parameterMap)

        then:
        def vacationRequest = serviceInstance.selectEmployee(parameterMap)
        println("test selectEmployee failed done with : ${vacationRequest}")
    }

}