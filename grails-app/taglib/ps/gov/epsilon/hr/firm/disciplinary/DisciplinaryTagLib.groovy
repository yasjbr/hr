package ps.gov.epsilon.hr.firm.disciplinary

import grails.artefact.TagLibrary
import grails.gsp.TagLib
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgmentService
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReasonService
import ps.gov.epsilon.hr.firm.disciplinary.lookup.JoinedDisciplinaryJudgmentReason

@TagLib
class DisciplinaryTagLib implements TagLibrary {

    static namespace = "disciplinary"

    DisciplinaryJudgmentService disciplinaryJudgmentService
    DisciplinaryRequestService disciplinaryRequestService
    EmployeeViolationService employeeViolationService

    /**
     * using to get disciplinary judgments.
     * @attr disciplinaryReasonIds
     */
    def getViolationsWithJudgments = { attrs, body ->
        String employeeId = attrs["employeeId"];
        String disciplinaryRequestId = attrs["disciplinaryRequestId"];
        List employeeViolationIds = attrs["employeeViolationIds"];
        String disciplinaryCategoryId = params["disciplinaryCategoryId"]
        String violationCount = params['violationCount']
        List disciplinaryJudgmentList = params.listString("disciplinaryJudgmentList")

        if (disciplinaryCategoryId && employeeViolationIds) {
            params.max = Integer.MAX_VALUE
            params['employee.id'] = employeeId
            params['ids[]'] = employeeViolationIds

            List<EmployeeViolation> employeeViolationList = employeeViolationService.searchWithRemotingValues(params)
            List<DisciplinaryReason> disciplinaryReasonList = employeeViolationList?.disciplinaryReason?.findAll {
                it.disciplinaryCategories.id == disciplinaryCategoryId
            }
            List disciplinaryReasonIds = disciplinaryReasonList?.id?.toList()
            List disciplinaryJudgmentIds = []
            DisciplinaryRequest disciplinaryRequest = DisciplinaryRequest.load(disciplinaryRequestId)
            if (disciplinaryRequest) {
                disciplinaryJudgmentIds = disciplinaryRequest?.disciplinaryJudgments?.disciplinaryJudgment?.id?.unique()
            }

            List<DisciplinaryJudgment> disciplinaryJudgments = disciplinaryJudgmentService.getJoinedReasonJudgments(disciplinaryReasonIds,disciplinaryJudgmentList)
            //- disciplinaryJudgmentList

            out << el.hiddenField(name: 'disciplinaryJudgmentIdsList', id: 'disciplinaryJudgmentList', value: disciplinaryJudgments?.collect {
                it.id
            })

            out << el.formGroup(id: 'disciplinaryReasonsFormGroup') {

                out << el.hiddenField(name: 'hiddenEmployeeViolation', id: 'hiddenEmployeeViolation', value: employeeViolationList?.id)

                if (disciplinaryReasonList) {


                    out << el.checkboxGroup(disabled: 'disabled', size: '12', labelSize: '2', values: disciplinaryReasonIds, inputDivSize: '10', inputSize: '4', options: disciplinaryReasonList, class: ' isRequired',
                            label: message(code: 'disciplinaryRequest.violations.label'),
                            optionKey: "id", optionValue: "id", optionLabel: "descriptionInfo", name: "disciplinaryReason", optionInfo: 'id')
                } else {
                    out << el.labelField(label: message(code: 'disciplinaryRequest.disciplinaryReasons.label'), size: '6')
                }

            }

            out << el.formGroup(id: 'disciplinaryJudgmentsFormGroup') {
                String className = attrs["className"] ?: " isRequired";
                if (disciplinaryJudgments) {
                    out << el.checkboxGroup(size: '12', labelSize: '2', values: disciplinaryJudgmentIds, inputDivSize: '10', options: disciplinaryJudgments, class: className,
                            label: message(code: 'disciplinaryRequest.disciplinaryJudgments.label'), keyPrefix: "disciplinaryJudgment_${violationCount}",
                            optionKey: "id", optionValue: "id", optionLabel: "descriptionInfo", name: "disciplinaryJudgment", hasOneElementPerRow: 'true', optionInfo: 'id',
                            onclick: "viewDisciplinaryJudgmentInputs(this.id,this.name,'${violationCount}')")
                }
            }
        } else {

            out << el.formGroup(id: 'disciplinaryReasonsFormGroup') {
                out << el.labelField(label: message(code: 'disciplinaryRequest.disciplinaryReasons.label'), size: '6')
            }

            out << el.formGroup(id: 'disciplinaryJudgmentsFormGroup') {
                out << el.labelField(label: message(code: 'disciplinaryRequest.disciplinaryJudgments.label'), size: '6')
            }

        }
    }

    /**
     * using to get disciplinary judgments.
     * @attr disciplinaryReasonIds
     */
    def getDisciplinaryJudgmentsInputs = { attrs, body ->
        String disciplinaryJudgmentId = attrs["disciplinaryJudgmentId"]
        String disciplinaryRequestId = attrs["disciplinaryRequestId"]
        DisciplinaryRecordJudgment disciplinaryRecordJudgment

        if (disciplinaryRequestId) {
            DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(new GrailsParameterMap([id: disciplinaryRequestId], request))
            disciplinaryRecordJudgment = disciplinaryRequest?.disciplinaryJudgments?.toList()?.find {
                it.disciplinaryJudgment.id == disciplinaryJudgmentId
            }
        }

        DisciplinaryJudgment disciplinaryJudgment = DisciplinaryJudgment.load(disciplinaryJudgmentId)
        String divId = disciplinaryJudgment?.id?.replace("-", "_")
        String paramsFunction = ""
        if (disciplinaryJudgment?.unitIds) {
            paramsFunction = "unitParams_${divId}"
        }
        if (disciplinaryJudgment?.currencyIds) {
            paramsFunction = "currencyParams_${divId}"
        }
        out << "<div id='newDivContent_${disciplinaryJudgmentId}'>"
        out << el.textField(name: 'value_' + disciplinaryJudgmentId, value: (disciplinaryRecordJudgment ? disciplinaryRecordJudgment?.value : ""), class: ' isRequired', label: message(code: 'disciplinaryRecordJudgment.value.label'), size: '6')

        Boolean isDisabledUnit = false
        String classUnit = " isRequired"
        if (!disciplinaryJudgment?.unitIds && !disciplinaryJudgment?.currencyIds) {
            isDisabledUnit = true
            classUnit = " "
        }

        if (disciplinaryJudgment?.isCurrencyUnit) {
            def values
            if (disciplinaryRecordJudgment) {
                values = [
                        [disciplinaryRecordJudgment?.currencyId,
                         disciplinaryRecordJudgment?.transientData?.currencyDTO?.toString()
                        ]
                ]
            }
            out << el.autocomplete(name: 'currencyId_' + disciplinaryJudgmentId, isDisabled: isDisabledUnit,
                    controller: 'currency', action: 'autocomplete', class: classUnit, values: values,
                    paramsGenerateFunction: paramsFunction, label: message(code: 'disciplinaryRecordJudgment.currencyId.label'), size: '6')
        } else {
            def values
            if (disciplinaryRecordJudgment) {
                values = [
                        [disciplinaryRecordJudgment?.unitId,
                         disciplinaryRecordJudgment?.transientData?.unitDTO?.toString()
                        ]
                ]
            }
            out << el.autocomplete(name: 'unitId_' + disciplinaryJudgmentId, values: values, controller: 'unitOfMeasurement',
                    isDisabled: isDisabledUnit,
                    action: 'autocomplete', class: classUnit,
                    paramsGenerateFunction: paramsFunction,
                    label: message(code: 'disciplinaryRecordJudgment.unitId.label'), size: '6')
        }

        if (disciplinaryJudgment?.hasValidity) {
            out << el.dateField(name: 'fromDate_' + disciplinaryJudgmentId, value: (disciplinaryRecordJudgment ? disciplinaryRecordJudgment?.fromDate : null), class: '', label: message(code: 'disciplinaryRecordJudgment.fromDate.label'), size: '6')
            out << el.dateField(name: 'toDate_' + disciplinaryJudgmentId, value: (disciplinaryRecordJudgment ? disciplinaryRecordJudgment?.toDate : null), class: '', label: message(code: 'disciplinaryRecordJudgment.toDate.label'), size: '6')
        }


        out << el.textField(name: 'orderNo_' + disciplinaryJudgmentId, value: (disciplinaryRecordJudgment ? disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo : ""), class: '', label: message(code: 'disciplinaryRecordJudgment.orderNo.label'), size: '6')
        out << el.textField(name: 'note_' + disciplinaryJudgmentId, value: (disciplinaryRecordJudgment ? disciplinaryRecordJudgment?.disciplinaryListNote?.note : ""), class: '', label: message(code: 'disciplinaryRecordJudgment.note.label'), size: '6')
        out << "</div>"

        if (disciplinaryJudgment?.isCurrencyUnit && disciplinaryJudgment?.currencyIds) {
            out << """<script type="text/javascript"> 
                    function currencyParams_${divId}() {
                        return {'ids[]':'${disciplinaryJudgment?.currencyIds}'}
                    }
                    </script>
                   """
        } else if (disciplinaryJudgment?.unitIds) {
            out << """<script type="text/javascript"> 
                    function unitParams_${divId}() {
                        return {'ids[]':'${disciplinaryJudgment?.unitIds}'}
                    }
                    </script>
                   """
        }
    }
}
