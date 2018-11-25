package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for Province service
 */
class ProvinceIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  Province
        service_domain =  ProvinceService
        entity_name = "province"
        required_properties = PCPUtils.getRequiredFields( Province)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}