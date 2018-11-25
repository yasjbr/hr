package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([LoanListPersonNote,ps.gov.epsilon.hr.firm.loan.LoanListPerson])
@Domain([LoanListPersonNote])
@TestMixin([HibernateTestMixin])
class LoanListPersonNoteDomainSpec extends ConstraintUnitSpec {

    void "test LoanListPersonNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "loanListPerson", value: null, testResult: TestResult.FAIL],
                [field: "loanListPerson", value: ps.gov.epsilon.hr.firm.loan.LoanListPerson.build(), testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.FAIL],
//                [field: "orderNo", value: new nl.flotsam.xeger.Xeger("([a-zA-Z\p{InArabic}0-9\/\-\*]){2,25}").generate(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(LoanListPersonNote,constraints)
    }
}
