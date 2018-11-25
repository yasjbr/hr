package ps.gov.epsilon.hr.firm.employmentService.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ServiceActionReason service
 */
class ServiceActionReasonIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ServiceActionReason
        service_domain =  ServiceActionReasonService
        entity_name = "serviceActionReason"
        required_properties = PCPUtils.getRequiredFields( ServiceActionReason)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = false
        is_encrypted_delete = false
    }
}