package ps.gov.epsilon.hr.firm.disciplinary

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for PetitionList service
 */
class PetitionListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  PetitionList
        service_domain =  PetitionListService
        entity_name = "petitionList"
        required_properties = PCPUtils.getRequiredFields( PetitionList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}