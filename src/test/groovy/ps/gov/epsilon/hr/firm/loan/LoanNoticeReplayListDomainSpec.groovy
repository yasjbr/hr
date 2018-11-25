package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([LoanNoticeReplayList,ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.loan.LoanNotice])
@Domain([LoanNoticeReplayList])
@TestMixin([HibernateTestMixin])
class LoanNoticeReplayListDomainSpec extends ConstraintUnitSpec {

    void "test LoanNoticeReplayList all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "code", value: "codeA1c_", testResult: TestResult.FAIL],
//                [field: "code", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "correspondenceListStatuses", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "loanNominatedEmployees", value: null, testResult: TestResult.PASS],
                [field: "loanNotice", value: null, testResult: TestResult.FAIL],
                [field: "loanNotice", value: ps.gov.epsilon.hr.firm.loan.LoanNotice.build(), testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: null, testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualIncomeNo", value: "manualIncomeNoA1c_", testResult: TestResult.FAIL],
//                [field: "manualIncomeNo", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: null, testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualOutgoingNo", value: "manualOutgoingNoA1c_", testResult: TestResult.FAIL],
//                [field: "manualOutgoingNo", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c_", testResult: TestResult.FAIL],
//                [field: "name", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.\-\/\_\(\)\*]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.FAIL],
//                [field: "orderNo", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "receivingParty", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(LoanNoticeReplayList,constraints)
    }
}
