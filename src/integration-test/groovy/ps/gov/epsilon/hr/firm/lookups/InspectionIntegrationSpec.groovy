package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for Inspection service
 */
class InspectionIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = Inspection
        service_domain = InspectionService
        entity_name = "inspection"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(Inspection)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", "encodedId"]
        is_virtual_delete=true
        is_encrypted_delete=false
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

    }
}