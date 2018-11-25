package ps.gov.epsilon.hr.firm.disciplinary

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for PetitionRequest service
 */
class PetitionRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  PetitionRequest
        service_domain =  PetitionRequestService
        entity_name = "petitionRequest"
        required_properties = PCPUtils.getRequiredFields( PetitionRequest)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}