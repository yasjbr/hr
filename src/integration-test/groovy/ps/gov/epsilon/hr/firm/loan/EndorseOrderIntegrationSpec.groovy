package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for EndorseOrder service
 */
class EndorseOrderIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = EndorseOrder
        service_domain = EndorseOrderService
        entity_name = "endorseOrder"
        List requiredProperties = PCPUtils.getRequiredFields(EndorseOrder)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["autocomplete","delete"]
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
    }
}