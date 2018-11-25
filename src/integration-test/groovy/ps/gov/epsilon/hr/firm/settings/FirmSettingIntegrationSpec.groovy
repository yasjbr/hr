package ps.gov.epsilon.hr.firm.settings

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for FirmSetting service
 */
class FirmSettingIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = FirmSetting
        service_domain = FirmSettingService
        entity_name = "firmSetting"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(FirmSetting)
        filtered_parameters = ["id"];
        autocomplete_property = "propertyName"
        primary_keys = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}