package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ExternalTransferList,ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus,ps.gov.epsilon.hr.firm.Firm])
@Domain([ExternalTransferList])
@TestMixin([HibernateTestMixin])
class ExternalTransferListDomainSpec extends ConstraintUnitSpec {

    void "test ExternalTransferList all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.PASS],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "correspondenceListStatuses", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus.build(), testResult: TestResult.PASS],
                [field: "externalTransferListEmployees", value: null, testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: null, testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: "manualIncomeNo", testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualOutgoingNo", value: null, testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: "manualOutgoingNo", testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c_", testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "receivingParty", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ExternalTransferList,constraints)
    }
}
