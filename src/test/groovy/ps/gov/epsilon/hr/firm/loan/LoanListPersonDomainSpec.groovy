package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([LoanListPerson,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.loan.LoanList,ps.gov.epsilon.hr.firm.loan.LoanRequest,ps.gov.epsilon.hr.firm.Department])
@Domain([LoanListPerson])
@TestMixin([HibernateTestMixin])
class LoanListPersonDomainSpec extends ConstraintUnitSpec {

    void "test LoanListPerson all constraints"() {
        when:
        List<Map> constraints = [
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "isEmploymentProfileProvided", value: null, testResult: TestResult.FAIL],
                [field: "loanList", value: null, testResult: TestResult.FAIL],
                [field: "loanList", value: ps.gov.epsilon.hr.firm.loan.LoanList.build(), testResult: TestResult.PASS],
                [field: "loanListPersonNotes", value: null, testResult: TestResult.PASS],
                [field: "loanRequest", value: null, testResult: TestResult.FAIL],
                [field: "loanRequest", value: ps.gov.epsilon.hr.firm.loan.LoanRequest.build(), testResult: TestResult.PASS],
                [field: "periodInMonths", value: null, testResult: TestResult.FAIL],
                [field: "periodInMonths", value: 32769, testResult: TestResult.FAIL],
                [field: "periodInMonths", value: 32765, testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "requestedFromOrganizationId", value: null, testResult: TestResult.PASS],
                [field: "requestedPersonId", value: null, testResult: TestResult.PASS],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "toDepartment", value: null, testResult: TestResult.PASS],
                [field: "toDepartment", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(LoanListPerson,constraints)
    }
}
