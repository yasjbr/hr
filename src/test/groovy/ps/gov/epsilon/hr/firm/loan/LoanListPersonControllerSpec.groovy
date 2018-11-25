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
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
/**
 * unit test for LoanListPerson controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanListPerson])
@Build([LoanListPerson])
@TestFor(LoanListPersonController)
class LoanListPersonControllerSpec extends CommonUnitSpec {

    OrganizationService organizationService = mockService(OrganizationService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)


    def setupSpec() {
        domain_class = LoanListPerson
        service_domain = LoanListPersonService
        entity_name = "loanListPerson"
        required_properties = PCPUtils.getRequiredFields(LoanListPerson)
        filtered_parameters = ["loanRequest.id"];
        autocomplete_property = "loanRequest.id"
        primary_key_values = ["id","encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["autocomplete","create","save","edit","update"]
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            organizationService.proxyFactoryService = proxyFactoryService
        }

    }

}