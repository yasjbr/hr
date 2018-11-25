package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedVacancyAdvertisement,ps.gov.epsilon.hr.firm.recruitment.Vacancy,ps.gov.epsilon.hr.firm.recruitment.VacancyAdvertisements])
@Domain([JoinedVacancyAdvertisement])
@TestMixin([HibernateTestMixin])
class JoinedVacancyAdvertisementDomainSpec extends ConstraintUnitSpec {

    void "test JoinedVacancyAdvertisement all constraints"() {
        when:
        List<Map> constraints = [
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "vacancy", value: null, testResult: TestResult.FAIL],
                [field: "vacancy", value: ps.gov.epsilon.hr.firm.recruitment.Vacancy.build(), testResult: TestResult.PASS],
                [field: "vacancyAdvertisements", value: null, testResult: TestResult.FAIL],
                [field: "vacancyAdvertisements", value: ps.gov.epsilon.hr.firm.recruitment.VacancyAdvertisements.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedVacancyAdvertisement,constraints)
    }
}
