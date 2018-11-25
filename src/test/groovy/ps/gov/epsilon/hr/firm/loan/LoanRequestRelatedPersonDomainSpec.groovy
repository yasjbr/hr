package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([LoanRequestRelatedPerson,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.loan.LoanRequest])
@Domain([LoanRequestRelatedPerson])
@TestMixin([HibernateTestMixin])
class LoanRequestRelatedPersonDomainSpec extends ConstraintUnitSpec {

    void "test LoanRequestRelatedPerson all constraints"() {
        when:
        List<Map> constraints = [
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "loanRequest", value: null, testResult: TestResult.FAIL],
                [field: "loanRequest", value: ps.gov.epsilon.hr.firm.loan.LoanRequest.build(), testResult: TestResult.PASS],
                [field: "recordSource", value: null, testResult: TestResult.FAIL],
                [field: "requestedPersonId", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(LoanRequestRelatedPerson,constraints)
    }
}
