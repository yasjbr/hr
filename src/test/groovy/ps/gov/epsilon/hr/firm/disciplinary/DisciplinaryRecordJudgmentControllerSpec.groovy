package ps.gov.epsilon.hr.firm.disciplinary

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
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.test.utils.CommonUnitSpec

/**
 * unit test for DisciplinaryRecordJudgment controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryRecordJudgment])
@Build([DisciplinaryRecordJudgment])
@TestFor(DisciplinaryRecordJudgmentController)
class DisciplinaryRecordJudgmentControllerSpec extends CommonUnitSpec {

    UnitOfMeasurementService unitOfMeasurementService = mockService(UnitOfMeasurementService)
    CurrencyService currencyService = mockService(CurrencyService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)


    def setupSpec() {
        domain_class = DisciplinaryRecordJudgment
        service_domain = DisciplinaryRecordJudgmentService
        entity_name = "disciplinaryRecordJudgment"
        List requiredFields  = PCPUtils.getRequiredFields(DisciplinaryRecordJudgment)
        requiredFields << "value"
        required_properties = requiredFields
        filtered_parameters = ["value"];
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["create","delete","save","update","list","edit","autocomplete","show","index"]
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.unitOfMeasurementService) {
            serviceInstance.unitOfMeasurementService = unitOfMeasurementService
            serviceInstance.unitOfMeasurementService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.currencyService) {
            serviceInstance.currencyService = currencyService
            serviceInstance.currencyService.proxyFactoryService = proxyFactoryService
        }
    }

}