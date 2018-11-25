package ps.gov.epsilon.hr.firm.suspension

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for SuspensionListEmployee service
 */
class SuspensionListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = SuspensionListEmployee
        service_domain = SuspensionListEmployeeService
        entity_name = "suspensionListEmployee"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(SuspensionListEmployee)
        filtered_parameters = ["id"]
        exclude_methods = ["autocomplete"]
        primary_keys = ["id", 'encodedId']
        is_remoting = true
    }
}