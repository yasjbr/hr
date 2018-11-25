package ps.gov.epsilon.hr.firm.settings

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedFirmOperationDocument,ps.gov.epsilon.hr.firm.settings.FirmDocument])
@Domain([JoinedFirmOperationDocument])
@TestMixin([HibernateTestMixin])
class JoinedFirmOperationDocumentDomainSpec extends ConstraintUnitSpec {

    void "test JoinedFirmOperationDocument all constraints"() {
        when:
        List<Map> constraints = [
                [field: "firmDocument", value: null, testResult: TestResult.FAIL],
                [field: "firmDocument", value: ps.gov.epsilon.hr.firm.settings.FirmDocument.build(), testResult: TestResult.PASS],
                [field: "isMandatory", value: null, testResult: TestResult.FAIL],
                [field: "isMandatory", value: true, testResult: TestResult.PASS],
                [field: "isMandatory", value: false, testResult: TestResult.PASS],
                [field: "operation", value: null, testResult: TestResult.FAIL],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.APPLICANT, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.CHANGE_MARITAL_STATUS, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.CHILD_REGISTRATION, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.JOB_REQUISITION, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.PROMOTION, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.RECRUITMENT_CYCLE, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.VACANCY, testResult: TestResult.PASS],
                [field: "operation", value: ps.gov.epsilon.hr.enums.v1.EnumOperation.VACANCY_ADVERTISEMENTS, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedFirmOperationDocument,constraints)
    }
}
