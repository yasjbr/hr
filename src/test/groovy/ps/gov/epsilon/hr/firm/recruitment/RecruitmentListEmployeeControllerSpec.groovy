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
 * unit test for RecruitmentListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([RecruitmentListEmployee])
@Build([RecruitmentListEmployee])
@TestFor(RecruitmentListEmployeeController)
class RecruitmentListEmployeeControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = RecruitmentListEmployee
        service_domain = RecruitmentListEmployeeService
        entity_name = "recruitmentListEmployee"
        required_properties = PCPUtils.getRequiredFields(RecruitmentListEmployee)
        filtered_parameters = ["id"];
        exclude_actions = ["autocomplete"]
        primary_key_values = ["id", "encodedId"]
    }

}