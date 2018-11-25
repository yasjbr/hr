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
 * unit test for ApplicantInspectionCategoryResult controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ApplicantInspectionCategoryResult])
@Build([ApplicantInspectionCategoryResult])
@TestFor(ApplicantInspectionCategoryResultController)
class ApplicantInspectionCategoryResultControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = ApplicantInspectionCategoryResult
        service_domain = ApplicantInspectionCategoryResultService
        entity_name = "applicantInspectionCategoryResult"
        required_properties = PCPUtils.getRequiredFields(ApplicantInspectionCategoryResult)
        filtered_parameters = ["id"];
        autocomplete_property = "applicant.personName"
        primary_key_values = ['encodedId', 'id']
    }

}