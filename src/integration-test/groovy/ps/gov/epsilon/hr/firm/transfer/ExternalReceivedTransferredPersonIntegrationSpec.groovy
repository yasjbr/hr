package ps.gov.epsilon.hr.firm.transfer

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ExternalReceivedTransferredPerson service
 */
class ExternalReceivedTransferredPersonIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ExternalReceivedTransferredPerson
        service_domain =  ExternalReceivedTransferredPersonService
        entity_name = "externalReceivedTransferredPerson"
        required_properties = PCPUtils.getRequiredFields( ExternalReceivedTransferredPerson)
        filtered_parameters = ["personId"];
        autocomplete_property = "personId"
        primary_keys = ["encodedId"]
        exclude_methods = ["delete"]
        is_virtual_delete = true
        is_remoting = true
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}