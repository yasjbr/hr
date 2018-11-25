package ps.gov.epsilon.hr.firm.settings

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for JoinedFirmOperationDocument service
 */
class JoinedFirmOperationDocumentIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = JoinedFirmOperationDocument
        service_domain = JoinedFirmOperationDocumentService
        entity_name = "joinedFirmOperationDocument"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(JoinedFirmOperationDocument)
        filtered_parameters = ["id"];
        autocomplete_property = "operation"
        primary_keys = ["id", 'encodedId']

    }
}