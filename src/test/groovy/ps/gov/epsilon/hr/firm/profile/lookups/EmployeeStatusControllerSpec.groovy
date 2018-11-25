package ps.gov.epsilon.hr.firm.profile.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import grails.util.GrailsWebUtil
import guiplugin.AlertTagLib
import ps.gov.epsilon.hr.firm.Firm
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for EmployeeStatus controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin,])
@Mock([AlertTagLib])
@Domain([EmployeeStatus])
@Build([EmployeeStatus])
@TestFor(EmployeeStatusController)
class EmployeeStatusControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = EmployeeStatus
        service_domain = EmployeeStatusService
        entity_name = "employeeStatus"
        required_properties = PCPUtils.getRequiredFields(EmployeeStatus)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId":"firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["create"]
    }

}