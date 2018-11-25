package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([TraineeList, ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus, ps.gov.epsilon.hr.firm.Firm])
@Domain([TraineeList])
@TestMixin([HibernateTestMixin])
class TraineeListDomainSpec extends ConstraintUnitSpec {

    void "test TraineeList all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.PASS],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "correspondenceListStatuses", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: null, testResult: TestResult.PASS],
                [field: "currentStatus", value: ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: null, testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: "manualIncomeNo", testResult: TestResult.PASS],
                [field: "manualIncomeNo", value: " ", testResult: TestResult.FAIL],
                [field: "manualOutgoingNo", value: null, testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: "manualOutgoingNo", testResult: TestResult.PASS],
                [field: "manualOutgoingNo", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c_", testResult: TestResult.PASS],
                [field: "name", value: "traineeList", testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "receivingParty", value: null, testResult: TestResult.PASS],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "traineeListEmployees", value: null, testResult: TestResult.PASS],
                [field: "trainingLocationId", value: null, testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: "unstructuredLocation", testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: " ", testResult: TestResult.PASS],
        ]
        then:
        validateObject(TraineeList, constraints)
    }
}
