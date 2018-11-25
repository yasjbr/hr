package ps.gov.epsilon.hr.firm.employmentService.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ServiceActionReasonType service
 */
class ServiceActionReasonTypeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ServiceActionReasonType
        service_domain =  ServiceActionReasonTypeService
        entity_name = "serviceActionReasonType"
        required_properties = PCPUtils.getRequiredFields( ServiceActionReasonType)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = false
        is_encrypted_delete = false
    }
}