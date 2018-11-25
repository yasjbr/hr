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
 * unit test for JoinedInterviewCommitteeRole controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([JoinedInterviewCommitteeRole])
@Build([JoinedInterviewCommitteeRole])
@TestFor(JoinedInterviewCommitteeRoleController)
class JoinedInterviewCommitteeRoleControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = JoinedInterviewCommitteeRole
        service_domain = JoinedInterviewCommitteeRoleService
        entity_name = "joinedInterviewCommitteeRole"
        required_properties = PCPUtils.getRequiredFields(JoinedInterviewCommitteeRole)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["id", "encodedId"]
    }

}