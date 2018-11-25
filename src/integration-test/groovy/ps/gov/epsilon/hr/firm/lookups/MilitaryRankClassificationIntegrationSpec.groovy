package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for MilitaryRankClassification service
 */
class MilitaryRankClassificationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = MilitaryRankClassification
        service_domain = MilitaryRankClassificationService
        entity_name = "militaryRankClassification"
        required_properties = PCPUtils.getRequiredFields(MilitaryRankClassification)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["encodedId", "id"]
        is_virtual_delete = true
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}