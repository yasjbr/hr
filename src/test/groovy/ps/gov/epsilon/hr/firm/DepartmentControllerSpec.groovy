package ps.gov.epsilon.hr.firm

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.junit.Assume
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for Department controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([Department])
@Build([Department, Firm])
@TestFor(DepartmentController)
class DepartmentControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = Department
        service_domain = DepartmentService
        entity_name = "department"
        required_properties = PCPUtils.getRequiredFields(Department)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        exclude_actions = ['delete']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }


    def "test_getInstance"() {
        setup:
        println("************************test_getInstance********************************")
        Firm firm = Firm.build()
        def instance = Department.build(firm: firm)
        when:
        params.id = instance.id
        PCPSessionUtils.setValue("firmId", firm.id)
        controller.getInstance()
        then:
        instance == serviceInstance.getInstanceWithRemotingValues(params)
        instance != [:]
        println("test_getInstance done with data:${instance}")
    }

}