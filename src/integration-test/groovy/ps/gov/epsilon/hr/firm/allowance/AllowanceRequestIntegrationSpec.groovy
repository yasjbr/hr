package ps.gov.epsilon.hr.firm.allowance

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for AllowanceRequest service
 */
class AllowanceRequestIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = AllowanceRequest
        service_domain = AllowanceRequestService
        entity_name = "allowanceRequest"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(AllowanceRequest)
        filtered_parameters = ["id"]
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_keys = ["id", 'encodedId']
        is_virtual_delete=true
        is_encrypted_delete=false
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}