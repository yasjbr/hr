package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ApplicantInspectionResultList service
 */
class ApplicantInspectionResultListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ApplicantInspectionResultList
        service_domain =  ApplicantInspectionResultListService
        entity_name = "applicantInspectionResultList"
        required_properties = PCPUtils.getRequiredFields( ApplicantInspectionResultList)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}