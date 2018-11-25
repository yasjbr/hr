package ps.gov.epsilon.hr.firm.profile.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for EmployeeStatusCategory service
 */
class EmployeeStatusCategoryIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = EmployeeStatusCategory
        service_domain = EmployeeStatusCategoryService
        entity_name = "employeeStatusCategory"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(EmployeeStatusCategory)
        filtered_parameters = ["id"]
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", 'encodedId']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}