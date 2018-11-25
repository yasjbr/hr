package ps.gov.epsilon.hr.firm.loan

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
 * unit test for LoanListPersonNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanListPersonNote])
@Build([LoanListPersonNote])
@TestFor(LoanListPersonNoteController)
class LoanListPersonNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = LoanListPersonNote
        service_domain = LoanListPersonNoteService
        entity_name = "loanListPersonNote"
        List requiredProperties = PCPUtils.getRequiredFields(LoanListPersonNote)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        exclude_actions = ["autocomplete"]
        primary_key_values = ["encodedId"]
    }

}