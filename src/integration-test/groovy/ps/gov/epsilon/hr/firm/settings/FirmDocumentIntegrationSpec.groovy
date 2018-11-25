package ps.gov.epsilon.hr.firm.settings

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for FirmDocument service
 */
class FirmDocumentIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = FirmDocument
        service_domain = FirmDocumentService
        entity_name = "firmDocument"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(FirmDocument)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", 'encodedId']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}