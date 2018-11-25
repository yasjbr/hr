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
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonUnitSpec

/**
 * unit test for LoanNominatedEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanNominatedEmployeeNote])
@Build([LoanNominatedEmployeeNote])
@TestFor(LoanNominatedEmployeeNoteController)
class LoanNominatedEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = LoanNominatedEmployeeNote
        service_domain = LoanNominatedEmployeeNoteService
        entity_name = "loanNominatedEmployeeNote"
        List requiredProperties = PCPUtils.getRequiredFields(LoanNominatedEmployeeNote)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        exclude_actions = ["autocomplete"]
        primary_key_values = ["encodedId"]
    }

}