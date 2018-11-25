package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([SuspensionList, ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus, ps.gov.epsilon.hr.firm.Firm])
@Domain([SuspensionList])
@TestMixin([HibernateTestMixin])
class SuspensionListDomainSpec extends ConstraintUnitSpec {

    void "test SuspensionList all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "code", value: "codeA1c_", testResult: TestResult.FAIL],
                [field: "code", value: "cdco", testResult: TestResult.PASS],
                [field: "correspondenceListStatuses", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: null, testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualIncomeNo", value: "manualIncomeNoA1c_", testResult: TestResult.FAIL],
                [field: "manualIncomeNo", value: "ddd", testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: null, testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualOutgoingNo", value: "manualOutgoingNoA1c_", testResult: TestResult.FAIL],
                [field: "manualOutgoingNo", value: "ddd", testResult: TestResult.PASS],
                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c_", testResult: TestResult.PASS],
                [field: "name", value: "gb gg", testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.FAIL],
                [field: "orderNo", value: "ddd", testResult: TestResult.PASS],
                [field: "receivingParty", value: null, testResult: TestResult.PASS],
                [field: "suspensionListEmployees", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(SuspensionList, constraints)
    }
}
