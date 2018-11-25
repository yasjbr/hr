package ps.gov.epsilon.hr.firm.settings

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for FirmActiveModule service
 */
class FirmActiveModuleIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  FirmActiveModule
        service_domain =  FirmActiveModuleService
        entity_name = "firmActiveModule"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields( FirmActiveModule)
        filtered_parameters = ["id"];
        autocomplete_property = "systemModule"
        primary_keys = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}