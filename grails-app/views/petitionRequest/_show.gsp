<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: petitionRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <lay:showElement value="${petitionRequest?.id}" type="String"
                     label="${message(code: 'petitionRequest.id.label', default: 'id')}"/>
    <lay:showElement value="${petitionRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'petitionRequest.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${petitionRequest?.requestStatus}" type="enum"
                     label="${message(code: 'petitionRequest.requestStatus.label', default: 'requestStatus')}"/>

    <lay:showElement value="${petitionRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>

<el:row/>
<el:row/>
<el:row/>

<g:render template="/request/wrapperShow" model="[request: petitionRequest]"/>

<br/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'petitionRequest.disciplinaryRequest.label')}">
    <lay:showElement
            value="${petitionRequest?.disciplinaryRequest?.id}"
            type="String"
            label="${message(code: 'disciplinaryRequest.id.label', default: 'id')}"/>
    <lay:showElement
            value="${petitionRequest?.disciplinaryRequest?.requestDate}"
            type="ZonedDate"
            label="${message(code: 'disciplinaryRequest.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement
            value="${petitionRequest?.disciplinaryRequest?.disciplinaryCategory}"
            type="DisciplinaryCategory"
            label="${message(code: 'disciplinaryRequest.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>
    <lay:showElement
            value="${petitionRequest?.disciplinaryRequest?.requestStatusNote}"
            type="String"
            label="${message(code: 'disciplinaryRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
</lay:showWidget>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: petitionRequest, colSize: 12]"/>
<br/>


<el:row/>
<lay:table title="${message(code: 'petitionRequest.disciplinaryRequest.violationsAndJudgments.label')}" styleNumber="1">
    <lay:tableHead title="${message(code: 'disciplinaryRequest.disciplinaryReasons.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRequest.disciplinaryJudgment.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.value.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.unitAndCurrency.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.fromDate.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.toDate.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.orderNo.label')}"/>
    <lay:tableHead title="${message(code: 'disciplinaryRecordJudgment.note.label')}"/>

    <g:each in="${petitionRequest?.disciplinaryRequest?.disciplinaryJudgments?.toList()?.sort {
        it.disciplinaryJudgment.descriptionInfo
    }}" var="disciplinaryRecordJudgment">
        <rowElement><tr class='center'>
            <td width="25%" class='center'>${disciplinaryRecordJudgment?.disciplinaryReasons?.toList()?.sort {
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
            <td class='center'>${disciplinaryRecordJudgment?.fromDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
            <td class='center'>${disciplinaryRecordJudgment?.toDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
            <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo}</td>
            <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.note}</td>
        </tr></rowElement>

    </g:each>
</lay:table>


<br/>


<el:row/>

<br/>
<el:row/>