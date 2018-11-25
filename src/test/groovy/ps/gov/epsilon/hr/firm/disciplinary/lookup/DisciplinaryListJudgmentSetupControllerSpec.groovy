package ps.gov.epsilon.hr.firm.disciplinary.lookup

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
 * unit test for DisciplinaryListJudgmentSetup controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryListJudgmentSetup])
@Build([DisciplinaryListJudgmentSetup])
@TestFor(DisciplinaryListJudgmentSetupController)
class DisciplinaryListJudgmentSetupControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = DisciplinaryListJudgmentSetup
        service_domain = DisciplinaryListJudgmentSetupService
        entity_name = "disciplinaryListJudgmentSetup"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryListJudgmentSetup)
        filtered_parameters = ["id"];
        autocomplete_property = "listNamePrefix"
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }
}