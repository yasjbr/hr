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
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for RecruitmentListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([RecruitmentListEmployeeNote])
@Build([RecruitmentListEmployeeNote])
@TestFor(RecruitmentListEmployeeNoteController)
class RecruitmentListEmployeeNoteControllerSpec extends CommonUnitSpec {


    def setupSpec() {
        domain_class = RecruitmentListEmployeeNote
        service_domain = RecruitmentListEmployeeNoteService
        entity_name = "recruitmentListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(RecruitmentListEmployeeNote)
        filtered_parameters = ["id"];
        primary_key_values = ['encodedId', 'id']
        exclude_actions = ["autocomplete"]
    }

    def setup() {
        params.note = "note"
    }

}