<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${absence?.id}" type="String" label="${message(code: 'absence.id.label', default: 'id')}"/>
    <lay:showElement value="${absence?.absenceReason}" type="enum"
                     label="${message(code: 'absence.absenceReason.label', default: 'absenceReason')}"
                     messagePrefix="EnumAbsenceReason"/>
    <lay:showElement value="${absence?.actualAbsenceReason}" type="enum"
                     label="${message(code: 'absence.actualAbsenceReason.label', default: 'actualAbsenceReason')}"
                     messagePrefix="EnumAbsenceReason"/>
    <lay:showElement value="${absence?.numOfDays}" type="Long"
                     label="${message(code: 'absence.numOfDays.label', default: 'numOfDays')}"/>
    <lay:showElement value="${absence?.fromDate}" type="ZonedDate"
                     label="${message(code: 'absence.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${absence?.toDate}" type="ZonedDate"
                     label="${message(code: 'absence.toDate.label', default: 'toDate')}"/>
    <lay:showElement value="${absence?.informer}" type="Employee"
                     label="${message(code: 'absence.informer.label', default: 'informer')}"/>
    <lay:showElement value="${absence?.noticeDate}" type="ZonedDate"
                     label="${message(code: 'absence.noticeDate.label', default: 'noticeDate')}"/>
    <lay:showElement value="${absence?.violationStatus}" type="Enum"
                     messagePrefix="EnumViolationStatus"
                     label="${message(code: 'absence.violationStatus.label', default: 'violationStatus')}"/>
    <lay:showElement value="${absence?.reasonDescription}" type="String"
                     label="${message(code: 'absence.reasonDescription.label', default: 'reasonDescription')}"/>
</lay:showWidget>
<el:row/>
<g:render template="/employeeViolation/trackingInfoWrapper" model="[violation: absence]"/>
<br/>
<el:row/>

<g:if test="${absence?.transientData?.disciplinaryRequest != null}">
    <lay:table title="${message(code: 'disciplinaryRequest.violationsAndJudgments.label')}" styleNumber="1">
        <lay:tableHead title="${message(code: 'disciplinaryRequest.disciplinaryReasons.label')}"/>
        <lay:tableHead title="${message(code: 'disciplinaryRequest.disciplinaryJudgment.label')}"/>
        <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.value.label')}"/>
        <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.unitAndCurrency.label')}"/>
        <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.orderNo.label')}"/>
        <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.note.label')}"/>

        <g:each in="${absence?.transientData?.disciplinaryRequest?.disciplinaryJudgments?.toList()?.sort {
            it.disciplinaryJudgment.descriptionInfo
        }}" var="disciplinaryRecordJudgment">
            <rowElement><tr class='center'>
                <td class='center'>${disciplinaryRecordJudgment?.disciplinaryReasons?.toList()?.sort {
                    it.descriptionInfo
                }?.descriptionInfo?.join(",")}</td>
                <td class='center'>${disciplinaryRecordJudgment?.disciplinaryJudgment}</td>
                <td class='center'>${disciplinaryRecordJudgment?.value}</td>
                <g:if test="${disciplinaryRecordJudgment?.currencyId}">
                    <td class='center'>${disciplinaryRecordJudgment?.transientData?.currencyDTO}</td>
                </g:if>
                <g:else>
                    <td class='center'>${disciplinaryRecordJudgment?.transientData?.unitDTO}</td>
                </g:else>
                <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo}</td>
                <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.note}</td>
            </tr></rowElement>

        </g:each>
    </lay:table>
</g:if>