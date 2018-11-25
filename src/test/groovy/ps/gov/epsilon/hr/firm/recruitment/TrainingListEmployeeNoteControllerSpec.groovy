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
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for TrainingListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([TrainingListEmployeeNote])
@Build([TrainingListEmployeeNote])
@TestFor(TrainingListEmployeeNoteController)
class TrainingListEmployeeNoteControllerSpec extends CommonUnitSpec {

    def setupSpec() {
        domain_class = TrainingListEmployeeNote
        service_domain = TrainingListEmployeeNoteService
        entity_name = "trainingListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(TrainingListEmployeeNote)
        filtered_parameters = ["id"];
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ["autocomplete"]
    }

    def setup() {
        params.note = "note"
    }

}