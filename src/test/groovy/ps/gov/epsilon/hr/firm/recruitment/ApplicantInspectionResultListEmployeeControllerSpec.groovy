package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
/**
 * unit test for ApplicantInspectionResultListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ApplicantInspectionResultListEmployee])
@Build([ApplicantInspectionResultListEmployee])
@TestFor(ApplicantInspectionResultListEmployeeController)
class ApplicantInspectionResultListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ApplicantInspectionResultListEmployee
        service_domain = ApplicantInspectionResultListEmployeeService
        entity_name = "applicantInspectionResultListEmployee"
        required_properties = PCPUtils.getRequiredFields(ApplicantInspectionResultListEmployee)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}