package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ProvinceLocation service
 */
class ProvinceLocationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ProvinceLocation
        service_domain =  ProvinceLocationService
        entity_name = "provinceLocation"
        required_properties = PCPUtils.getRequiredFields( ProvinceLocation)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}