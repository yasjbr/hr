package ps.gov.epsilon.hr.firm.transfer

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ExternalTransferListEmployeeNote service
 */
class ExternalTransferListEmployeeNoteIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ExternalTransferListEmployeeNote
        service_domain =  ExternalTransferListEmployeeNoteService
        entity_name = "externalTransferListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields( ExternalTransferListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}