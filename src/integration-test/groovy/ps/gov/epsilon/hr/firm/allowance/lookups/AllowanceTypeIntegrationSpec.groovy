package ps.gov.epsilon.hr.firm.allowance.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.CommonUnitSpec

@Integration
@Rollback
/**
 * integration test for AllowanceType service
 */
class AllowanceTypeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = AllowanceType
        service_domain = AllowanceTypeService
        entity_name = "allowanceType"
        hashing_entity = "id"
        required_properties = PCPUtils.getRequiredFields(AllowanceType)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_remoting = true
        is_virtual_delete = true
        is_encrypted_delete = false
    }
}