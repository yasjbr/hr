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
 * unit test for RecruitmentCyclePhase controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([RecruitmentCyclePhase])
@Build([RecruitmentCyclePhase])
@TestFor(RecruitmentCyclePhaseController)
class RecruitmentCyclePhaseControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = RecruitmentCyclePhase
        service_domain = RecruitmentCyclePhaseService
        entity_name = "recruitmentCyclePhase"
        required_properties = PCPUtils.getRequiredFields(RecruitmentCyclePhase)
        filtered_parameters = ["id"]
        autocomplete_property = "requisitionAnnouncementStatus"
        exclude_actions = ["save", "create", "update", "show", "edit", "delete"]
    }

}