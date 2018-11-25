package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ApplicantInspectionResultListEmployee service
 */
class ApplicantInspectionResultListEmployeeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ApplicantInspectionResultListEmployee
        service_domain =  ApplicantInspectionResultListEmployeeService
        entity_name = "applicantInspectionResultListEmployee"
        required_properties = PCPUtils.getRequiredFields( ApplicantInspectionResultListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}