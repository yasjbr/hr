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
 * unit test for ApplicantInspectionResultListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ApplicantInspectionResultListEmployeeNote])
@Build([ApplicantInspectionResultListEmployeeNote])
@TestFor(ApplicantInspectionResultListEmployeeNoteController)
class ApplicantInspectionResultListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ApplicantInspectionResultListEmployeeNote
        service_domain = ApplicantInspectionResultListEmployeeNoteService
        entity_name = "applicantInspectionResultListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(ApplicantInspectionResultListEmployeeNote)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }

}